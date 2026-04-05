package com.example.momentary.ui

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import com.example.momentary.ui.navigation.NavGraph

@Composable
fun MomentaryApp(
    navController: NavHostController,
    startDestination: String
) {
    Surface(color = MaterialTheme.colorScheme.background) {
        NavGraph(
            navController = navController,
            startDestination = startDestination
        )
    }
}
