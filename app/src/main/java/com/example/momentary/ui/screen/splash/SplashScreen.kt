package com.example.momentary.ui.screen.splash

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel

@Composable
fun SplashScreen(
    viewModel: SplashViewModel = hiltViewModel(),
    onNavigate: (Boolean) -> Unit
) {
    val isLoggedIn = viewModel.isLoggedIn

    LaunchedEffect(isLoggedIn) {
        isLoggedIn?.let {
            onNavigate(it)
        }
    }
}
