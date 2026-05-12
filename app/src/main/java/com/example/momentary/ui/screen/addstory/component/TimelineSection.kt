package com.example.momentary.ui.screen.addstory.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.LocationOff
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.momentary.ui.common.Avatar

@Composable
fun TimelineSection(
    showLocationSection: Boolean,
    onLocationIconClick: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Avatar(
            name = "",
            isOwnStory = true
        )

        Spacer(Modifier.height(8.dp))

        Box(
            modifier = Modifier
                .width(1.dp)
                .height(110.dp)
                .background(
                    MaterialTheme.colorScheme.outlineVariant
                )
        )

        Spacer(Modifier.height(8.dp))

        Icon(
            imageVector = if (showLocationSection) {
                Icons.Outlined.LocationOn
            } else {
                Icons.Outlined.LocationOff
            },
            contentDescription = null,
            tint = if (showLocationSection) {
                MaterialTheme.colorScheme.primary
            } else {
                MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = .7f)
            },
            modifier = Modifier
                .size(20.dp)
                .clickable(
                    interactionSource = interactionSource,
                    indication = null,
                    onClick = onLocationIconClick
                )
        )
    }
}
