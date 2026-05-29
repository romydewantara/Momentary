package com.example.momentary.ui.screen.addstory

import androidx.core.net.toUri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.momentary.R
import com.example.momentary.data.location.GeocoderHelper
import com.example.momentary.data.model.UserSession
import com.example.momentary.data.repository.MomentaryRepository
import com.example.momentary.ui.common.ResultState
import com.example.momentary.ui.common.SnackBarEvent
import com.example.momentary.ui.common.UiText
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import javax.inject.Inject

@HiltViewModel
class AddStoryViewModel @Inject constructor(
    private val repository: MomentaryRepository,
    private val geocoderHelper: GeocoderHelper
) : ViewModel() {

    private val _state = MutableStateFlow(AddStoryUiState())
    val state: StateFlow<AddStoryUiState> = _state

    val session = repository.getSession().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = UserSession(null, null, null)
    )

    private val _event = MutableSharedFlow<SnackBarEvent>()
    val event = _event.asSharedFlow()

    fun setImage(uri: String) {
        _state.update { it.copy(imageUri = uri) }
    }

    fun setDescription(desc: String) {
        _state.update { it.copy(description = desc) }
    }

    fun clearLocation() {
        _state.update {
            it.copy(selectedLocation = null)
        }
    }

    fun resolveAddress(latitude: Double, longitude: Double) {
        viewModelScope.launch {
            _state.update { it.copy(isResolvingAddress = true, pendingAddress = null) }
            val address = geocoderHelper.getAddress(latitude, longitude)
            _state.update {
                it.copy(
                    isResolvingAddress = false,
                    pendingAddress = address
                )
            }
        }
    }

    fun selectLocation(
        latitude: Double,
        longitude: Double
    ) {
        viewModelScope.launch {
            val address = geocoderHelper.getAddress(
                latitude,
                longitude
            )

            _state.update {
                it.copy(
                    selectedLocation = StoryLocation(
                        lat = latitude,
                        lon = longitude,
                        address = address
                    )
                )
            }
        }
    }

    fun uploadStory() {
        val current = _state.value
        if (!current.isValid) return

        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }

            val file = File(current.imageUri?.toUri()?.path ?: "")
            val requestFile = file
                .asRequestBody("image/jpeg".toMediaTypeOrNull())
            val body = MultipartBody.Part
                .createFormData("photo", file.name, requestFile)
            val descBody = current.description
                .toRequestBody("text/plain".toMediaTypeOrNull())

            val location = current.selectedLocation
            val latBody = location?.lat?.toString()
                ?.toRequestBody("text/plain".toMediaTypeOrNull())
            val lonBody = location?.lon?.toString()
                ?.toRequestBody("text/plain".toMediaTypeOrNull())

            when (val response =
                repository.addStory(body, descBody, latBody, lonBody)
            ) {
                is ResultState.Success -> {
                    _state.update { it.copy(isLoading = false, isSuccess = true) }
                    _event.emit(
                        SnackBarEvent(
                            UiText.StringResource(R.string.add_story_success),
                            isError = false
                        )
                    )
                }

                is ResultState.Error -> {
                    _state.update { it.copy(isLoading = false) }
                    _event.emit(SnackBarEvent(response.message, isError = true))
                }
            }
        }
    }
}
