package com.example.momentary.ui.screen.addstory.component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.LocationOn
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.momentary.R
import com.example.momentary.ui.screen.addstory.StoryLocation
import com.example.momentary.utils.rememberMapProperties
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
    pendingAddress: String?,
    isResolvingAddress: Boolean,
    onLocationSelected: (Double, Double) -> Unit,
    onMarkerTapped: (Double, Double) -> Unit,
) {
    val scope = rememberCoroutineScope()
    val mapProperties = rememberMapProperties()

    val defaultLocation = LatLng(
        selectedLocation?.lat ?: -6.8957473,
        selectedLocation?.lon ?: 107.6337669
    )

    var markerPosition by remember { mutableStateOf(defaultLocation) }
    var showAddressCard by remember { mutableStateOf(false) }

    val markerState = rememberUpdatedMarkerState(position = markerPosition)

    LaunchedEffect(markerPosition) {
        markerState.position = markerPosition
    }

    LaunchedEffect(pendingAddress, isResolvingAddress) {
        if (isResolvingAddress || pendingAddress != null) {
            showAddressCard = true
        }
    }

    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(markerPosition, 15f)
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(0.85f)
    ) {
        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraPositionState,
            properties = mapProperties,
            onMapClick = { latLng ->
                markerPosition = latLng
                showAddressCard = false
            },
            content = {
                Marker(
                    state = markerState,
                    draggable = true,
                    onClick = {
                        onMarkerTapped(
                            markerPosition.latitude,
                            markerPosition.longitude
                        )
                        false
                    },
                    onInfoWindowClose = {
                        showAddressCard = false
                    }
                )
            }
        )

        CurrentLocationButton(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(16.dp),
            onLocationFetched = { latLng ->
                markerPosition = latLng
                showAddressCard = false
                scope.launch {
                    cameraPositionState.animate(
                        CameraUpdateFactory.newLatLngZoom(latLng, 16f)
                    )
                }
            }
        )

        AnimatedVisibility(
            visible = showAddressCard,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 72.dp),
            enter = fadeIn() + slideInVertically { it / 2 },
            exit = fadeOut() + slideOutVertically { it / 2 }
        ) {
            ElevatedCard(
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Rounded.LocationOn,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    if (isResolvingAddress) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(18.dp),
                            strokeWidth = 2.dp
                        )
                    } else {
                        Text(
                            text = pendingAddress ?: "",
                            style = MaterialTheme.typography.bodyMedium,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }
        }

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
            Text(text = stringResource(R.string.add_story_button_use_location))
        }
    }
}
