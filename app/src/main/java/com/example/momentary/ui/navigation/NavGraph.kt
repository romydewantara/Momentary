package com.example.momentary.ui.navigation

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.momentary.ui.screen.addstory.AddStoryScreen
import com.example.momentary.ui.screen.auth.login.LoginScreen
import com.example.momentary.ui.screen.auth.register.RegisterScreen
import com.example.momentary.ui.screen.detail.DetailScreen
import com.example.momentary.ui.screen.home.HomeScreen
import com.example.momentary.ui.screen.splash.SplashScreen


@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun NavGraph(
    navController: NavHostController,
    startDestination: String
) {
    SharedTransitionLayout {
        val sharedScope = this

        NavHost(
            navController = navController,
            startDestination = startDestination
        ) {

            composable(Screen.Splash.route) {
                SplashScreen { isLoggedIn ->
                    if (isLoggedIn) {
                        navController.navigate(Screen.Home.route) {
                            popUpTo(Screen.Splash.route) { inclusive = true }
                            launchSingleTop = true
                        }
                    } else {
                        navController.navigate(Screen.Login.route) {
                            popUpTo(Screen.Splash.route) { inclusive = true }
                            launchSingleTop = true
                        }
                    }
                }
            }

            composable(Screen.Login.route) {
                LoginScreen(
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
                        navController.popBackStack()
                    }
                )
            }

            composable(Screen.Home.route) {
                HomeScreen(
                    sharedTransitionScope = sharedScope,
                    animatedVisibilityScope = this@composable as AnimatedVisibilityScope,
                    onLogout = {
                        navController.navigate(Screen.Login.route) {
                            popUpTo(Screen.Home.route) { inclusive = true }
                            launchSingleTop = true
                        }
                    },
                    onNavigateToDetail = { id ->
                        navController.navigate(Screen.Detail.createRoute(id))
                    },
                    onNavigateToAddStory = {
                        navController.navigate(Screen.AddStory.route)
                    }
                )
            }

            composable(
                Screen.Detail.route,
                arguments = listOf(
                    navArgument("storyId") { type = NavType.StringType }
                )
            ) { backstackEntry ->
                val storyId = backstackEntry.arguments?.getString("storyId") ?: ""
                DetailScreen(
                    sharedTransitionScope = sharedScope,
                    storyId = storyId,
                    animatedVisibilityScope = this@composable as AnimatedVisibilityScope,
                    onBack = {
                        navController.popBackStack()
                    }
                )
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
