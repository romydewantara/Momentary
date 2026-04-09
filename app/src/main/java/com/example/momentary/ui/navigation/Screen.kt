package com.example.momentary.ui.navigation

import com.example.momentary.utils.Route

sealed class Screen(val route: String) {
    data object Splash : Screen(Route.SPLASH)
    data object Login : Screen(Route.LOGIN)
    data object Register : Screen(Route.REGISTER)
    data object Home : Screen(Route.HOME)
    data object Detail : Screen(Route.DETAIL) {
        fun createRoute(storyId: String) = "detail/$storyId"
    }

    data object AddStory : Screen(Route.ADD_STORY)
}
