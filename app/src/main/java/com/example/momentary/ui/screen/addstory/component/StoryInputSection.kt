package com.example.momentary.ui.screen.addstory.component

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import com.example.momentary.ui.screen.addstory.StoryLocation
import kotlinx.coroutines.delay

@Composable
fun StoryInputSection(
    value: String,
    onValueChange: (String) -> Unit,
    name: String,
    location: StoryLocation?,
    showLocationSection: Boolean,
    onLocationClick: () -> Unit,
    onDeleteLocation: () -> Unit
) {
    val bounceScale = remember { Animatable(0f) }

    LaunchedEffect(showLocationSection) {
        if (showLocationSection) {
            bounceScale.snapTo(0f)
            delay(300)
            bounceScale.animateTo(
                targetValue = 1f,
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessMedium
                )
            )
        } else {
            bounceScale.snapTo(0f)
        }
    }

    Column(
        modifier = Modifier.fillMaxWidth()
    ) {

        AddStoryHeader(name)

        Spacer(Modifier.height(8.dp))

        StoryTextField(
            value = value,
            onValueChange = onValueChange
        )

        Spacer(Modifier.height(10.dp))

        if (showLocationSection) {
            LocationSection(
                location = location,
                onClick = onLocationClick,
                onDeleteLocation = onDeleteLocation,
                modifier = Modifier.graphicsLayer {
                    scaleX = bounceScale.value
                    scaleY = bounceScale.value
                    transformOrigin = androidx.compose.ui.graphics.TransformOrigin(0f, 0.5f)
                }
            )
        }
    }
}
