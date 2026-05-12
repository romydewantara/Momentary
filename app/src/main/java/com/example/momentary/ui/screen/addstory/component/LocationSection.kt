package com.example.momentary.ui.screen.addstory.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.momentary.R
import com.example.momentary.ui.screen.addstory.StoryLocation

@Composable
fun LocationSection(
    modifier: Modifier = Modifier,
    location: StoryLocation?,
    onClick: () -> Unit,
    onDeleteLocation: () -> Unit,
) {
    val hasLocation = location != null
    val interactionSource = remember { MutableInteractionSource() }

    Row(
        modifier = modifier
            .offset(x = (-16).dp)
            .clip(RoundedCornerShape(12.dp))
            .clickable(
                interactionSource = interactionSource,
                onClick = onClick,
                indication = null
            )
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            modifier = Modifier.weight(1f),
            text = location?.address
                ?: stringResource(R.string.add_story_description_location),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            style = MaterialTheme.typography.bodyMedium,
            color = if (hasLocation) {
                MaterialTheme.colorScheme.onBackground
            } else {
                MaterialTheme.colorScheme.onSurfaceVariant
            }
        )

        if (hasLocation) {
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = stringResource(R.string.cd_delete_location),
                modifier = Modifier
                    .size(18.dp)
                    .clickable(onClick = onDeleteLocation)
            )
        }
    }
}
