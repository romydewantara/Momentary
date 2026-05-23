package com.example.momentary.ui.screen.maps

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.momentary.data.repository.MomentaryRepository
import com.example.momentary.ui.common.ResultState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MapsViewModel @Inject constructor(
    private val repository: MomentaryRepository
) : ViewModel() {

    private val _state = MutableStateFlow(MapsUiState())
    val state = _state.asStateFlow()

    init {
        fetchStories()
    }

    fun fetchStories() {
        viewModelScope.launch {

            _state.update {
                it.copy(isLoading = true, error = null)
            }

            when (
                val result = repository.getStoriesWithLocation()
            ) {

                is ResultState.Success -> {
                    _state.update {
                        it.copy(stories = result.data, isLoading = false)
                    }
                }

                is ResultState.Error -> {
                    _state.update {
                        it.copy(isLoading = false, error = result.message)
                    }
                }
            }
        }
    }
}
