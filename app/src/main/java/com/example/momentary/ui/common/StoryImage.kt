package com.example.momentary.ui.common

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import coil3.compose.AsyncImage
import coil3.request.CachePolicy
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.example.momentary.R

@Composable
fun StoryImage(
    modifier: Modifier = Modifier,
    id: String,
    photoUrl: String,
    sharedTransitionScope: SharedTransitionScope,
    animatedVisibilityScope: AnimatedVisibilityScope
) {

    with(sharedTransitionScope) {
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(photoUrl)
                .crossfade(false)
                .memoryCachePolicy(CachePolicy.ENABLED)
                .diskCachePolicy(CachePolicy.ENABLED)
                .build(),
            contentDescription = stringResource(R.string.cd_story_image),
            modifier = modifier
                .fillMaxWidth()
                .sharedElement(
                    rememberSharedContentState("image-${id}"),
                    animatedVisibilityScope,
                    boundsTransform = { _, _ ->
                        tween(500)
                    }
                ),
            contentScale = ContentScale.Crop
        )
    }
}
