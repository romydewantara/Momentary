package com.example.momentary.ui.screen.home.component

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Public
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.momentary.R
import com.example.momentary.data.model.Story
import com.example.momentary.ui.common.Avatar
import com.example.momentary.ui.common.StoryImage
import com.example.momentary.ui.screen.home.HomeViewModel

@Composable
fun StoryItem(
    modifier: Modifier = Modifier,
    story: Story,
    isVerified: Boolean,
    isOwnStory: Boolean,
    timeText: String,
    sharedTransitionScope: SharedTransitionScope,
    animatedVisibilityScope: AnimatedVisibilityScope,
    onClick: () -> Unit,
    viewModel: HomeViewModel,
) {
    val context = LocalContext.current
    val locationText = viewModel.locationMap[story.id]
    val hasLocation = !locationText.isNullOrEmpty()

    LaunchedEffect(story.id) {
        viewModel.resolveLocation(context, story)
    }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 12.dp, vertical = 8.dp)
    ) {

        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Avatar(
                name = story.name,
                isOwnStory = isOwnStory
            )

            Spacer(modifier = Modifier.height(4.dp))

            Box(
                modifier = Modifier
                    .width(2.dp)
                    .weight(1f)
                    .background(MaterialTheme.colorScheme.outlineVariant)
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp)
        ) {

            Column {

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = story.name,
                        style = MaterialTheme.typography.bodyLarge.copy(
                            fontWeight = FontWeight.Black
                        )
                    )

                    if (isVerified) {
                        Spacer(modifier = Modifier.width(4.dp))

                        Image(
                            painter = painterResource(R.drawable.ic_certified),
                            contentDescription = stringResource(R.string.cd_certified),
                            modifier = Modifier
                                .size(16.dp)
                                .offset(y = (-1).dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(2.dp))

                if (hasLocation) {
                    Row(verticalAlignment = Alignment.CenterVertically) {

                        Icon(
                            imageVector = Icons.Default.LocationOn,
                            contentDescription = null,
                            tint = Color.Red,
                            modifier = Modifier.size(14.dp)
                        )

                        Spacer(modifier = Modifier.width(4.dp))

                        Text(
                            text = locationText,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                } else {
                    Text(
                        text = story.description,
                        style = MaterialTheme.typography.bodyMedium,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            if (hasLocation) {
                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = story.description,
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {

                Text(
                    text = timeText,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = .7f)
                )

                Spacer(modifier = Modifier.width(6.dp))

                Text(
                    text = "•",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = .7f)
                )

                Spacer(modifier = Modifier.width(6.dp))

                Icon(
                    imageVector = Icons.Default.Public,
                    contentDescription = stringResource(R.string.cd_story_item_public),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = .7f),
                    modifier = Modifier.size(14.dp)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Card(
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                StoryImage(
                    id = story.id,
                    photoUrl = story.photoUrl,
                    sharedTransitionScope = sharedTransitionScope,
                    animatedVisibilityScope = animatedVisibilityScope,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(220.dp),
                    onImageLoaded = { ratio ->
                        viewModel.setAspectRatio(story.id, ratio)
                    }
                )
            }
        }
    }
}
