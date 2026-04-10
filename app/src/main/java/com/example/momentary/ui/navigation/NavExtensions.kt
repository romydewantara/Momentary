package com.example.momentary.ui.navigation

import android.app.Activity
import androidx.navigation.NavController

fun NavController.safePopBackStack() {
    val popped = popBackStack()
    if (!popped) {
        (context as? Activity)?.finish()
    }
}
