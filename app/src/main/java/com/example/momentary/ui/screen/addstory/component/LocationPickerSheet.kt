package com.example.momentary.ui.screen.addstory.component

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.momentary.R
import com.example.momentary.ui.screen.addstory.StoryLocation
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.maps.android.compose.rememberUpdatedMarkerState
import kotlinx.coroutines.launch

@Composable
fun LocationPickerSheet(
    selectedLocation: StoryLocation?,
    onLocationSelected: (Double, Double) -> Unit,
) {
    val scope = rememberCoroutineScope()

    val defaultLocation = LatLng(
        selectedLocation?.lat ?: -6.8957473,
        selectedLocation?.lon ?: 107.6337669
    )

    var markerPosition by remember {
        mutableStateOf(defaultLocation)
    }

    val markerState = rememberUpdatedMarkerState(
        position = markerPosition
    )

    LaunchedEffect(markerPosition) {
        markerState.position = markerPosition
    }

    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(
            markerPosition,
            15f
        )
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(0.85f)
    ) {
        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraPositionState,
            onMapClick = { latLng ->
                markerPosition = latLng
            },
            content = {
                Marker(
                    state = markerState,
                    draggable = true
                )
            }
        )

        CurrentLocationButton(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(16.dp),
            onLocationFetched = { latLng ->

                markerPosition = latLng

                scope.launch {
                    cameraPositionState.animate(
                        CameraUpdateFactory.newLatLngZoom(
                            latLng,
                            16f
                        )
                    )
                }
            }
        )

        Button(
            onClick = {
                onLocationSelected(
                    markerPosition.latitude,
                    markerPosition.longitude
                )
            },
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(16.dp)
        ) {
            Text(
                text = stringResource(R.string.add_story_button_use_location)
            )
        }
    }
}
