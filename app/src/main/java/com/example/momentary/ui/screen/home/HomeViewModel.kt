package com.example.momentary.ui.screen.home

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.example.momentary.data.model.Story
import com.example.momentary.data.model.UserSession
import com.example.momentary.data.repository.MomentaryRepository
import com.example.momentary.ui.common.SnackBarEvent
import com.example.momentary.ui.screen.home.component.DrawerItem
import com.example.momentary.utils.getCityCountrySafe
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: MomentaryRepository,
    @param:ApplicationContext private val context: Context,
) : ViewModel() {

    private val _state = MutableStateFlow(HomeUiState())
    val state: StateFlow<HomeUiState> = _state

    val session = repository.getSession().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = UserSession(null, null, null)
    )

    private val resolvingSet = mutableSetOf<String>()

    private val _event = MutableSharedFlow<SnackBarEvent>()
    val event = _event.asSharedFlow()

    private val _logoutEvent = MutableSharedFlow<Unit>()
    val logoutEvent = _logoutEvent.asSharedFlow()

    val stories = session
        .flatMapLatest { userSession ->

            repository.getPagedStories(
                sessionName = userSession.name
            )
        }
        .cachedIn(viewModelScope)

    fun setAspectRatio(id: String, ratio: Float) {
        val current = _state.value.aspectRatioMap
        if (current[id] != null) return

        _state.update {
            it.copy(
                aspectRatioMap = current + (id to ratio)
            )
        }
    }

    fun resolveLocation(story: Story) {
        val lat = story.lat
        val lon = story.lon
        val id = story.id

        if (lat == null || lon == null) return

        val current = _state.value.locationMap
        if (current.containsKey(id)) return
        if (resolvingSet.contains(id)) return

        resolvingSet.add(id)

        context.getCityCountrySafe(lat, lon) { result ->
            _state.update {
                it.copy(
                    locationMap = it.locationMap + (id to result)
                )
            }
            resolvingSet.remove(id)
        }
    }

    fun onDrawerItemSelected(item: DrawerItem) {
        _state.update {
            it.copy(
                selectedDrawerItem = item,
                showLogoutDialog = item == DrawerItem.LOGOUT
            )
        }
    }

    fun dismissLogoutDialog() {
        _state.update {
            it.copy(showLogoutDialog = false)
        }
    }

    fun confirmLogout() {
        viewModelScope.launch {
            repository.logout()
            _state.update { it.copy(showLogoutDialog = false) }
            _logoutEvent.emit(Unit)
        }
    }
}
