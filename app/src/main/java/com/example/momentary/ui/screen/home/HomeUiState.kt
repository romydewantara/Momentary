package com.example.momentary.ui.screen.home

import com.example.momentary.data.model.Story
import com.example.momentary.ui.common.UiText
import com.example.momentary.ui.screen.home.component.DrawerItem

data class HomeUiState(
    val stories: List<Story> = emptyList(),
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    val error: UiText? = null,
    val scrollToTopTrigger: Boolean = false,
    val showLogoutDialog: Boolean = false,
    val selectedDrawerItem: DrawerItem = DrawerItem.MAIN
)
