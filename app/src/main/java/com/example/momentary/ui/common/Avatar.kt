package com.example.momentary.ui.common

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.example.momentary.R
import com.example.momentary.utils.Constants

@Composable
fun Avatar(
    name: String,
    isOwnStory: Boolean
) {
    if (isOwnStory) {
        AsyncImage(
            model = if (name == Constants.OFFICIAL_ACCOUNT_NAME) {
                R.drawable.ic_launcher_foreground
            } else {
                Constants.PHOTO_URL
            },
            contentDescription = stringResource(R.string.cd_add_image),
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
        )
    } else {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primaryContainer)
        ) {
            Text(
                text = name.take(1).uppercase(),
                fontWeight = FontWeight.Bold
            )
        }
    }
}
