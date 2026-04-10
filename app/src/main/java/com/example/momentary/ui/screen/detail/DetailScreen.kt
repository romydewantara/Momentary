package com.example.momentary.ui.screen.detail

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavBackStackEntry
import com.example.momentary.R
import com.example.momentary.data.model.Story
import com.example.momentary.ui.common.StoryImage
import com.example.momentary.ui.common.StoryTopBar
import com.example.momentary.ui.screen.detail.component.StoryHeader
import com.example.momentary.ui.screen.home.HomeViewModel
import com.example.momentary.utils.Constants
import com.example.momentary.utils.toEpochMillis
import com.example.momentary.utils.toRelativeTime

@Composable
fun DetailScreen(
    parentEntry: NavBackStackEntry,
    sharedTransitionScope: SharedTransitionScope,
    animatedVisibilityScope: AnimatedVisibilityScope,
    story: Story,
    onBack: () -> Unit
) {
    val viewModel: HomeViewModel = hiltViewModel(parentEntry)
    val session by viewModel.session.collectAsState()
    val context = LocalContext.current

    val ratio = viewModel.getAspectRatio(story.id)
    val location = viewModel.locationMap[story.id].orEmpty()
    val timeText = remember(story.createdAt) {
        story.createdAt.toEpochMillis().toRelativeTime(context = context)
    }

    LaunchedEffect(Unit) {
        viewModel.resolveLocation(context, story)
    }

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
        ) {

            val isOwnStory = story.name == session.name
            val isVerified =
                story.name == Constants.OFFICIAL_ACCOUNT_NAME || isOwnStory
            StoryHeader(
                name = story.name,
                isOwnStory = isOwnStory,
                isVerified = isVerified,
                location = location
            )

            StoryImage(
                id = story.id,
                photoUrl = story.photoUrl,
                sharedTransitionScope = sharedTransitionScope,
                animatedVisibilityScope = animatedVisibilityScope,
                modifier = Modifier
                    .fillMaxWidth()
                    .storyImageSize(ratio)
            )

            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = story.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = story.description,
                    style = MaterialTheme.typography.bodyMedium
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = timeText,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )
            }
        }
    }
}

fun Modifier.storyImageSize(ratio: Float?): Modifier {
    return this.then(
        if (ratio != null) Modifier.aspectRatio(ratio)
        else Modifier.fillMaxHeight()
    )
}
