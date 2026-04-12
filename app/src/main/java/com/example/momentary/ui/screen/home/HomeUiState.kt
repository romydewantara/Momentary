package com.example.momentary.ui.screen.home

import com.example.momentary.ui.common.UiText
import com.example.momentary.ui.screen.home.component.DrawerItem
import com.example.momentary.ui.screen.home.model.StoryUiModel

data class HomeUiState(
    val stories: List<StoryUiModel> = emptyList(),
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    val error: UiText? = null,
    val scrollToTopTrigger: Boolean = false,
    val showLogoutDialog: Boolean = false,
    val selectedDrawerItem: DrawerItem = DrawerItem.MAIN
)
