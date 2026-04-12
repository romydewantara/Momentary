package com.example.momentary.ui.screen.home

import android.content.Context
import androidx.compose.runtime.mutableStateMapOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.momentary.data.model.Story
import com.example.momentary.data.model.UserSession
import com.example.momentary.data.repository.MomentaryRepository
import com.example.momentary.ui.common.ResultState
import com.example.momentary.ui.common.SnackBarEvent
import com.example.momentary.ui.screen.home.component.DrawerItem
import com.example.momentary.ui.screen.home.model.StoryUiModel
import com.example.momentary.utils.AppConstants
import com.example.momentary.utils.getCityCountrySafe
import com.example.momentary.utils.toEpochMillis
import com.example.momentary.utils.toRelativeTime
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
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

    private val _aspectRatioMap = MutableStateFlow<Map<String, Float>>(emptyMap())
    val aspectRatioMap = _aspectRatioMap.asStateFlow()

    private val _locationMap = mutableStateMapOf<String, String>()
    val locationMap: Map<String, String> = _locationMap

    private val loadingSet = mutableSetOf<String>()

    private val _event = MutableSharedFlow<SnackBarEvent>()
    val event = _event.asSharedFlow()

    private val _logoutEvent = MutableSharedFlow<Unit>()
    val logoutEvent = _logoutEvent.asSharedFlow()

    init {
        fetchStories(isRefresh = false)
    }

    private fun fetchStories(isRefresh: Boolean = false) {
        viewModelScope.launch {

            _state.update {
                it.copy(
                    isLoading = if (isRefresh) it.stories.isEmpty() else true,
                    isRefreshing = isRefresh,
                    error = if (!isRefresh) null else it.error
                )
            }

            when (val result = repository.getStories(
                page = 1,
                size = 10,
                location = 0
            )) {
                is ResultState.Success -> {
                    val sessionValue = session.value
                    val uiModel = mapToUiModel(
                        stories = result.data,
                        session = sessionValue,
                        context = context
                    )

                    _state.update {
                        it.copy(
                            stories = uiModel,
                            isLoading = false,
                            isRefreshing = false,
                            error = null,
                            scrollToTopTrigger = isRefresh
                        )
                    }
                }

                is ResultState.Error -> {
                    val isEmpty = _state.value.stories.isEmpty()

                    if (isEmpty) {
                        _state.update {
                            it.copy(
                                stories = emptyList(),
                                isLoading = false,
                                isRefreshing = false,
                                error = result.message,
                                scrollToTopTrigger = false
                            )
                        }
                    } else {
                        _state.update {
                            it.copy(
                                isLoading = false,
                                isRefreshing = false,
                                scrollToTopTrigger = false
                            )
                        }

                        _event.emit(
                            SnackBarEvent(
                                message = result.message,
                                isError = true
                            )
                        )
                    }
                }
            }
        }
    }

    private fun mapToUiModel(
        stories: List<Story>,
        session: UserSession,
        context: Context
    ): List<StoryUiModel> {
        return stories.map { story ->
            val isOwnStory = story.name == session.name
            val isVerified =
                story.name == AppConstants.OFFICIAL_ACCOUNT_NAME || isOwnStory

            val timeText = story.createdAt
                .toEpochMillis()
                .toRelativeTime(context = context)

            StoryUiModel(
                id = story.id,
                story = story,
                isOwnStory = isOwnStory,
                isVerified = isVerified,
                timeText = timeText,
                locationText = _locationMap[story.id]
            )
        }
    }

    fun resetScrollTrigger() {
        _state.update {
            it.copy(scrollToTopTrigger = false)
        }
    }

    fun refresh() {
        fetchStories(isRefresh = true)
    }

    fun setAspectRatio(id: String, ratio: Float) {
        if (_aspectRatioMap.value[id] == null) {
            _aspectRatioMap.value += (id to ratio)
        }
    }

    fun getAspectRatio(id: String): Float? {
        return aspectRatioMap.value[id]
    }

    fun resolveLocation(context: Context, story: Story) {
        val lat = story.lat
        val lon = story.lon

        if (lat == null || lon == null) return
        if (_locationMap.containsKey(story.id)) return
        if (loadingSet.contains(story.id)) return

        loadingSet.add(story.id)

        context.getCityCountrySafe(lat, lon) { result ->
            _locationMap[story.id] = result
            loadingSet.remove(story.id)
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
