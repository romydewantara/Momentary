package com.example.momentary.ui.navigation

import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.runtime.Composable
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
                SplashScreen { isLoggedIn ->
                    val route = if (isLoggedIn) Screen.Home.route else Screen.Login.route

                    navController.navigate(route) {
                        popUpTo(Screen.Splash.route) { inclusive = true }
                        launchSingleTop = true
                    }
                }
            }

            composable(Screen.Login.route) { backStackEntry ->
                LoginScreen(
                    backStackEntry = backStackEntry,
                    onLoginSuccess = {
                        navController.navigate(Screen.Home.route) {
                            popUpTo(Screen.Login.route) { inclusive = true }
                            launchSingleTop = true
                        }
                    },
                    navigateToRegister = {
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
                    onBack = {
                        navController.popBackStack()
                    }
                )
            }

            composable(Screen.Home.route) {
                HomeScreen(
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
                        currentEntry?.savedStateHandle?.set(NavKeys.SELECTED_STORY, story)

                        navController.navigate(Screen.Detail.createRoute(story.id))
                    },
                    onNavigateToAddStory = { navController.navigate(Screen.AddStory.route) }
                )
            }

            composable(
                route = Screen.Detail.route,
                arguments = listOf(navArgument("storyId") { type = NavType.StringType })
            ) {
                val story = navController
                    .getBackStackEntry(Screen.Home.route)
                    .savedStateHandle
                    .get<Story>(NavKeys.SELECTED_STORY)

                story?.let {
                    DetailScreen(
                        sharedTransitionScope = this@SharedTransitionLayout,
                        animatedVisibilityScope = this,
                        story = it,
                        onBack = { navController.popBackStack() }
                    )
                }
            }

            composable(Screen.AddStory.route) {
                AddStoryScreen(
                    onUploadSuccess = {
                        navController.navigate(Screen.Home.route) {
                            popUpTo(Screen.Home.route) { inclusive = true }
                            launchSingleTop = true
                        }
                    },
                    onBack = {
                        navController.popBackStack()
                    }
                )
            }
        }
    }
}
