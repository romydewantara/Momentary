package com.example.momentary.ui.screen.home.component

import androidx.compose.animation.AnimatedVisibilityScope
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
import androidx.compose.ui.unit.dp
import com.example.momentary.data.model.Story
import com.example.momentary.ui.common.StoryImage

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
            StoryImage(
                id = story.id,
                photoUrl = story.photoUrl,
                sharedTransitionScope = sharedTransitionScope,
                animatedVisibilityScope = animatedVisibilityScope,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
            )

            Column(
                modifier = Modifier.padding(12.dp)
            ) {
                with(sharedTransitionScope) {
                    Text(
                        story.name,
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.sharedElement(
                            rememberSharedContentState(key = "title-${story.id}"),
                            animatedVisibilityScope
                        )
                    )
                }
                Text(story.description, maxLines = 2)
                Text(story.createdAt, style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}
