package com.example.momentary.ui.screen.home

import com.example.momentary.ui.screen.home.component.DrawerItem

data class HomeUiState(
    val locationMap: Map<String, String> = emptyMap(),
    val aspectRatioMap: Map<String, Float> = emptyMap(),
    val isRefreshing: Boolean = false,
    val showLogoutDialog: Boolean = false,
    val selectedDrawerItem: DrawerItem = DrawerItem.MAIN
)
