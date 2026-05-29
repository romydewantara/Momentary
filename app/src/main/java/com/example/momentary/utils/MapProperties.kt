package com.example.momentary.utils

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalResources
import com.example.momentary.R
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.maps.android.compose.MapProperties

@Composable
fun rememberMapProperties(): MapProperties {
    val resources = LocalResources.current
    val mapStyle = remember(resources) {
        resources
            .openRawResource(R.raw.map_style)
            .bufferedReader()
            .use { it.readText() }
    }
    return remember(mapStyle) {
        MapProperties(mapStyleOptions = MapStyleOptions(mapStyle))
    }
}
