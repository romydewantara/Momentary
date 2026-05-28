package com.example.momentary.ui.screen.addstory.component

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.EaseIn
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import com.example.momentary.ui.common.Avatar

@Composable
fun TimelineSection(
    showLocationSection: Boolean,
) {
    val fadeAlpha = remember { Animatable(0f) }

    LaunchedEffect(showLocationSection) {
        if (showLocationSection) {
            fadeAlpha.snapTo(0f)
            fadeAlpha.animateTo(
                targetValue = 1f,
                animationSpec = tween(durationMillis = 300, easing = EaseIn)
            )
        } else {
            fadeAlpha.snapTo(0f)
        }
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Avatar(
            name = "",
            isOwnStory = true
        )

        if (showLocationSection) {
            Spacer(Modifier.height(8.dp))

            Box(
                modifier = Modifier
                    .width(1.dp)
                    .height(110.dp)
                    .graphicsLayer {
                        alpha = fadeAlpha.value
                    }
                    .background(
                        MaterialTheme.colorScheme.outlineVariant
                    )
            )

            Spacer(Modifier.height(8.dp))

            Icon(
                imageVector = Icons.Outlined.LocationOn,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier
                    .size(20.dp)
                    .graphicsLayer {
                        alpha = fadeAlpha.value
                    }
            )
        }
    }
}
