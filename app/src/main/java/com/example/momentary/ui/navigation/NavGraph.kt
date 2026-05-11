package com.example.momentary.ui.navigation

import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.momentary.data.model.Story
import com.example.momentary.ui.screen.addstory.AddStoryScreen
import com.example.momentary.ui.screen.auth.login.LoginScreen
import com.example.momentary.ui.screen.auth.register.RegisterScreen
import com.example.momentary.ui.screen.detail.DetailScreen
import com.example.momentary.ui.screen.home.HomeScreen
import com.example.momentary.ui.screen.maps.MapsScreen
import com.example.momentary.ui.screen.onboarding.OnboardingScreen
import com.example.momentary.ui.screen.splash.SplashScreen


@Composable
fun NavGraph(
    navController: NavHostController,
    startDestination: String
) {
    SharedTransitionLayout {

        NavHost(
            navController = navController,
            startDestination = startDestination
        ) {

            composable(Screen.Splash.route) {
                SplashScreen { state ->
                    val route = when {
                        !state.isOnboardingCompleted -> Screen.Onboarding.route
                        state.isLoggedIn -> Screen.Home.route
                        else -> Screen.Login.route
                    }

                    navController.navigate(route) {
                        popUpTo(Screen.Splash.route) { inclusive = true }
                        launchSingleTop = true
                    }
                }
            }

            composable(Screen.Onboarding.route) {
                OnboardingScreen(
                    onFinish = {
                        navController.navigate(Screen.Login.route) {
                            popUpTo(Screen.Onboarding.route) { inclusive = true }
                            launchSingleTop = true
                        }
                    }
                )
            }

            composable(Screen.Login.route) { backStackEntry ->
                LoginScreen(
                    backStackEntry = backStackEntry,
                    onLoginSuccess = { message ->
                        navController.navigate(Screen.Home.route) {
                            popUpTo(Screen.Login.route) { inclusive = true }
                            launchSingleTop = true
                        }

                        navController.getBackStackEntry(Screen.Home.route)
                            .savedStateHandle[NavKeys.HOME_SUCCESS_MESSAGE] = message
                    },
                    navigateToRegister = {
                        navController.currentBackStackEntry
                            ?.savedStateHandle
                            ?.remove<Boolean>(NavKeys.REGISTER_SUCCESS)

                        navController.navigate(Screen.Register.route)
                    }
                )
            }

            composable(Screen.Register.route) {
                RegisterScreen(
                    onRegisterSuccess = {
                        navController.previousBackStackEntry
                            ?.savedStateHandle
                            ?.set(NavKeys.REGISTER_SUCCESS, true)

                        navController.popBackStack()
                    },
                    onBack = { navController.safePopBackStack() }
                )
            }

            composable(Screen.Home.route) { backStackEntry ->
                HomeScreen(
                    backStackEntry = backStackEntry,
                    sharedTransitionScope = this@SharedTransitionLayout,
                    animatedVisibilityScope = this,
                    onLogout = {
                        navController.navigate(Screen.Login.route) {
                            popUpTo(Screen.Home.route) { inclusive = true }
                            launchSingleTop = true
                        }
                    },
                    onNavigateToDetail = { story ->
                        val currentEntry = navController.currentBackStackEntry
                        currentEntry?.savedStateHandle
                            ?.remove<String>(NavKeys.HOME_SUCCESS_MESSAGE)

                        currentEntry?.savedStateHandle
                            ?.set(NavKeys.SELECTED_STORY, story)

                        navController.navigate(Screen.Detail.createRoute(story.id))
                    },
                    onNavigateToAddStory = {
                        navController.currentBackStackEntry
                            ?.savedStateHandle
                            ?.remove<String>(NavKeys.HOME_SUCCESS_MESSAGE)

                        navController.navigate(Screen.AddStory.route)
                    },
                    onNavigateToMaps = {
                        navController.navigate(Screen.Maps.route)
                    }
                )
            }

            composable(
                route = Screen.Detail.route,
                arguments = listOf(navArgument("storyId") { type = NavType.StringType })
            ) { backStackEntry ->
                val parentEntry = remember(backStackEntry) {
                    try {
                        navController.getBackStackEntry(Screen.Home.route)
                    } catch (_: IllegalArgumentException) {
                        null
                    }
                }

                val story = parentEntry
                    ?.savedStateHandle
                    ?.get<Story>(NavKeys.SELECTED_STORY)

                story?.let {
                    DetailScreen(
                        parentEntry = parentEntry,
                        sharedTransitionScope = this@SharedTransitionLayout,
                        animatedVisibilityScope = this,
                        story = it,
                        onBack = { navController.safePopBackStack() }
                    )
                }
            }

            composable(Screen.AddStory.route) {
                AddStoryScreen(
                    onUploadSuccess = { message ->
                        navController.navigate(Screen.Home.route) {
                            popUpTo(Screen.Home.route) { inclusive = true }
                            launchSingleTop = true
                        }

                        navController.getBackStackEntry(Screen.Home.route)
                            .savedStateHandle[NavKeys.HOME_SUCCESS_MESSAGE] = message
                    },
                    onBack = { navController.safePopBackStack() }
                )
            }

            composable(Screen.Maps.route) {
                MapsScreen(
                    onBack = {
                        navController.safePopBackStack()
                    },
                    onNavigateToDetail = { story ->

                        navController.currentBackStackEntry
                            ?.savedStateHandle
                            ?.set(NavKeys.SELECTED_STORY, story)

                        navController.navigate(
                            Screen.Detail.createRoute(story.id)
                        )
                    }
                )
            }
        }
    }
}
