package com.example.momentary.ui.screen.addstory.component

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.momentary.ui.screen.addstory.StoryLocation

@Composable
fun StoryDescriptionInput(
    value: String,
    onValueChange: (String) -> Unit,
    name: String,
    location: StoryLocation?,
    onLocationClick: () -> Unit,
    onDeleteLocation: () -> Unit
) {
    var showLocationSection by remember { mutableStateOf(false) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.Top
    ) {

        TimelineSection(
            showLocationSection = showLocationSection,
            onLocationIconClick = {
                showLocationSection = !showLocationSection
            }
        )

        Spacer(Modifier.width(12.dp))

        StoryInputSection(
            value = value,
            onValueChange = onValueChange,
            name = name,
            location = location,
            showLocationSection = showLocationSection,
            onLocationClick = onLocationClick,
            onDeleteLocation = onDeleteLocation
        )
    }
}
