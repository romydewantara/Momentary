package com.example.momentary.ui.screen.maps

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import com.example.momentary.R
import com.example.momentary.data.model.Story
import com.example.momentary.ui.common.ErrorState
import com.example.momentary.utils.rememberMapProperties
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.maps.android.compose.rememberUpdatedMarkerState
import kotlin.math.abs

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapsScreen(
    modifier: Modifier = Modifier,
    onBack: () -> Unit,
    viewModel: MapsViewModel = hiltViewModel()
) {
    val cameraPositionState = rememberCameraPositionState()
    var selectedStory by remember { mutableStateOf<Story?>(null) }
    val state by viewModel.state.collectAsStateWithLifecycle()
    var mapLoaded by remember { mutableStateOf(false) }
    val mapProperties = rememberMapProperties()
    val indonesiaLatLng = remember { LatLng(-2.5489, 118.0149) }

    LaunchedEffect(state.stories, mapLoaded) {

        if (!mapLoaded) return@LaunchedEffect

        val locations = state.stories.mapNotNull { story ->
            story.lat?.let { lat ->
                story.lon?.let { lon ->
                    LatLng(lat, lon)
                }
            }
        }

        if (locations.isEmpty()) {
            cameraPositionState.animate(
                update = CameraUpdateFactory.newLatLngZoom(
                    indonesiaLatLng,
                    4.2f
                ),
                durationMs = 1000
            )

            return@LaunchedEffect
        }

        if (locations.size == 1) {
            cameraPositionState.animate(
                update = CameraUpdateFactory.newLatLngZoom(
                    locations.first(),
                    12f
                ),
                durationMs = 1000
            )

            return@LaunchedEffect
        }

        val clusters = mutableListOf<MutableList<LatLng>>()

        val maxDistance = 20.0

        locations.forEach { location ->

            val cluster = clusters.find { cluster ->
                cluster.any { existing ->
                    abs(existing.latitude - location.latitude) < maxDistance &&
                            abs(existing.longitude - location.longitude) < maxDistance
                }
            }

            if (cluster != null) {
                cluster.add(location)
            } else {
                clusters.add(mutableListOf(location))
            }
        }

        val biggestCluster =
            clusters.maxByOrNull { it.size }
                ?: return@LaunchedEffect

        if (biggestCluster.size < 3) {
            cameraPositionState.animate(
                update = CameraUpdateFactory.newLatLngZoom(
                    indonesiaLatLng,
                    4.5f
                ),
                durationMs = 1000
            )

            return@LaunchedEffect
        }

        val boundsBuilder = LatLngBounds.Builder()

        biggestCluster.forEach(boundsBuilder::include)

        val bounds = boundsBuilder.build()

        cameraPositionState.animate(
            update = CameraUpdateFactory.newLatLngBounds(
                bounds,
                300
            ),
            durationMs = 1000
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text("Story Maps")
                },
                navigationIcon = {
                    IconButton(
                        onClick = onBack
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                            contentDescription = null
                        )
                    }
                }
            )
        }
    ) { padding ->

        Box(
            modifier = modifier
                .padding(padding)
                .fillMaxSize()
        ) {
            when {
                state.isLoading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }

                state.error != null -> {
                    ErrorState(
                        message = stringResource(R.string.error_unknown),
                        showRefreshHint = false,
                        onRetry = {
                            viewModel.fetchStories()
                        }
                    )
                }

                else -> {
                    GoogleMap(
                        modifier = Modifier.fillMaxSize(),
                        cameraPositionState = cameraPositionState,
                        properties = mapProperties,
                        onMapLoaded = {
                            mapLoaded = true
                        }
                    ) {

                        state.stories.forEach { story ->

                            val lat = story.lat ?: return@forEach
                            val lon = story.lon ?: return@forEach

                            Marker(
                                state = rememberUpdatedMarkerState(
                                    position = LatLng(lat, lon)
                                ),
                                title = story.name,
                                snippet = story.description,
                                onClick = {
                                    selectedStory = story
                                    false
                                }
                            )
                        }
                    }

                    val story = selectedStory

                    AnimatedVisibility(
                        visible = story != null,
                        modifier = Modifier.align(Alignment.BottomCenter)
                    ) {

                        story?.let {

                            ElevatedCard(
                                modifier = Modifier
                                    .padding(16.dp)
                                    .fillMaxWidth()
                            ) {

                                Row(
                                    modifier = Modifier.padding(12.dp)
                                ) {

                                    AsyncImage(
                                        model = story.photoUrl,
                                        contentDescription = null,
                                        modifier = Modifier
                                            .size(80.dp)
                                            .clip(RoundedCornerShape(16.dp)),
                                        contentScale = ContentScale.Crop
                                    )

                                    Spacer(modifier = Modifier.width(12.dp))

                                    Column(
                                        modifier = Modifier.weight(1f)
                                    ) {

                                        Text(
                                            text = story.name,
                                            style = MaterialTheme.typography.titleMedium
                                        )

                                        Spacer(modifier = Modifier.height(4.dp))

                                        Text(
                                            text = story.description,
                                            maxLines = 2,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
