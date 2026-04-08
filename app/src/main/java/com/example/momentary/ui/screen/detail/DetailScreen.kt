package com.example.momentary.ui.screen.detail

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.momentary.R
import com.example.momentary.data.model.Story
import com.example.momentary.ui.common.StoryImage
import com.example.momentary.ui.common.StoryTopBar

@Composable
fun DetailScreen(
    sharedTransitionScope: SharedTransitionScope,
    animatedVisibilityScope: AnimatedVisibilityScope,
    story: Story,
    onBack: () -> Unit
) {
    Scaffold(
        topBar = {
            StoryTopBar(
                title = stringResource(R.string.detail_story_title),
                onBack = onBack
            )
        }
    ) { padding ->

        Column(
            modifier = Modifier
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .fillMaxSize()
        ) {

            StoryImage(
                id = story.id,
                photoUrl = story.photoUrl,
                sharedTransitionScope = sharedTransitionScope,
                animatedVisibilityScope = animatedVisibilityScope
            )

            Spacer(modifier = Modifier.height(16.dp))

            Column(modifier = Modifier.padding(16.dp)) {

                with(sharedTransitionScope) {
                    Text(
                        text = story.name,
                        style = MaterialTheme.typography.headlineSmall,
                        modifier = Modifier.sharedElement(
                            rememberSharedContentState(key = "title-${story.id}"),
                            animatedVisibilityScope,
                            boundsTransform = { _, _ ->
                                tween(500)
                            }
                        )
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(story.description)

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = story.createdAt,
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}
