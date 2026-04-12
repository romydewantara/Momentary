package com.example.momentary.ui.screen.home

import com.example.momentary.ui.common.UiText
import com.example.momentary.ui.screen.home.component.DrawerItem
import com.example.momentary.ui.screen.home.model.StoryUiModel

data class HomeUiState(
    val stories: List<StoryUiModel> = emptyList(),
    val locationMap: Map<String, String> = emptyMap(),
    val aspectRatioMap: Map<String, Float> = emptyMap(),
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    val error: UiText? = null,
    val scrollToTopTrigger: Boolean = false,
    val showLogoutDialog: Boolean = false,
    val selectedDrawerItem: DrawerItem = DrawerItem.MAIN
)
