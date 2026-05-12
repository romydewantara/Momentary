package com.example.momentary.ui.screen.addstory.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.momentary.ui.screen.addstory.StoryLocation

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
                onDeleteLocation = onDeleteLocation
            )
        }
    }
}
