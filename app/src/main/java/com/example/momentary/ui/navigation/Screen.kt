package com.example.momentary.ui.navigation

sealed class Screen(val route: String) {
    data object Splash : Screen("splash")
    data object Login : Screen("login")
    data object Register : Screen("register")
    data object Home : Screen("home")
    data object Detail : Screen("detail")
    data object AddStory : Screen("add_story")
}
