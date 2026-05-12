package com.example.momentary.ui.screen.addstory.component

import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.LatLng

@Composable
fun CurrentLocationButton(
    modifier: Modifier = Modifier,
    onLocationFetched: (LatLng) -> Unit
) {

    val context = LocalContext.current

    val client = remember {
        LocationServices.getFusedLocationProviderClient(context)
    }

    fun fetchCurrentLocation() {
        val hasPermission =
            ContextCompat.checkSelfPermission(
                context,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED

        if (!hasPermission) return

        client.lastLocation
            .addOnSuccessListener { location ->
                location ?: return@addOnSuccessListener

                onLocationFetched(
                    LatLng(
                        location.latitude,
                        location.longitude
                    )
                )
            }
    }

    val permissionLauncher =
        rememberLauncherForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { granted ->
            if (granted) {
                fetchCurrentLocation()
            }
        }

    FilledIconButton(
        modifier = modifier,
        onClick = {
            val hasPermission =
                ContextCompat.checkSelfPermission(
                    context,
                    android.Manifest.permission.ACCESS_FINE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED

            if (hasPermission) {
                fetchCurrentLocation()
            } else {
                permissionLauncher.launch(
                    android.Manifest.permission.ACCESS_FINE_LOCATION
                )
            }
        }
    ) {
        Icon(
            imageVector = Icons.Default.MyLocation,
            contentDescription = null
        )
    }
}
