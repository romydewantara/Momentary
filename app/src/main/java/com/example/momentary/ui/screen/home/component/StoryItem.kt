package com.example.momentary.ui.screen.home.component

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.example.momentary.data.model.Story

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun StoryItem(
    modifier: Modifier = Modifier,
    story: Story,
    sharedTransitionScope: SharedTransitionScope,
    animatedVisibilityScope: AnimatedVisibilityScope,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .padding(12.dp)
    ) {
        Column {
            with(sharedTransitionScope) {
                AsyncImage(
                    model = story.photoUrl,
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                        .sharedElement(
                            rememberSharedContentState(key = "image-${story.id}"),
                            animatedVisibilityScope = animatedVisibilityScope
                        ),
                    contentScale = ContentScale.Crop
                )
            }

            Column(
                modifier = Modifier.padding(12.dp)
            ) {
                with(sharedTransitionScope) {
                    Text(
                        story.name,
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.sharedElement(
                            rememberSharedContentState(key = "title-${story.id}"),
                            animatedVisibilityScope = animatedVisibilityScope
                        )
                    )
                }
                Text(story.description, maxLines = 2)
                Text(story.createdAt, style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}
