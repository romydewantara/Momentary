package com.example.momentary.ui.screen.splash

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.snapshotFlow
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel

@Composable
fun SplashScreen(
    viewModel: SplashViewModel = hiltViewModel(),
    onNavigate: (Boolean) -> Unit
) {
    val state = viewModel.isLoggedIn

    LaunchedEffect(Unit) {
        snapshotFlow { state }
            .collect { value ->
                value?.let { onNavigate(it) }
            }
    }
}
