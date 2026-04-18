package com.example.momentary.ui.screen.onboarding

import android.content.res.Configuration
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Circle
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.lerp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.momentary.R
import com.example.momentary.ui.screen.onboarding.component.OnboardingPage
import com.example.momentary.utils.calculatePageOffset

@Composable
fun OnboardingScreen(
    modifier: Modifier = Modifier,
    viewModel: OnboardingViewModel = hiltViewModel(),
    onFinish: () -> Unit
) {
    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

    val state by viewModel.state.collectAsStateWithLifecycle()
    val pagerState = rememberPagerState(pageCount = { 4 })
    val currentPage by remember {
        derivedStateOf { pagerState.currentPage }
    }

    LaunchedEffect(Unit) {
        snapshotFlow { currentPage }
            .collect { page ->
                viewModel.onPageChanged(page)
            }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {

        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxSize()
        ) { page ->

            val pageOffset by remember {
                derivedStateOf {
                    calculatePageOffset(
                        currentPage,
                        page,
                        pagerState.currentPageOffsetFraction
                    )
                }
            }

            OnboardingPage(
                page = page,
                pageOffset = pageOffset,
                isLandscape = isLandscape
            )
        }

        val progress by remember {
            derivedStateOf {
                val pageOffsetFromFirst =
                    currentPage + pagerState.currentPageOffsetFraction

                (1f - pageOffsetFromFirst).coerceIn(0f, 1f)
            }
        }

        val logoSize = if (isLandscape) {
            160.dp
        } else {
            lerp(220.dp, 160.dp, 1f - progress)
        }

        val topInset = WindowInsets.statusBars.asPaddingValues().calculateTopPadding()
        val logoTop = if (isLandscape) {
            topInset + 16.dp
        } else {
            lerp(topInset + 50.dp, topInset + 90.dp, 1f - progress)
        }

        val indicatorBottom = if (isLandscape) 24.dp else 80.dp
        val buttonBottom = if (isLandscape) 60.dp else 140.dp
        val contentHorizontalOffset = if (isLandscape) 0.5f else 0f

        Image(
            painter = painterResource(id = R.drawable.ic_app_header),
            contentDescription = null,
            modifier = Modifier
                .align(Alignment.TopCenter)
                .graphicsLayer {
                    translationX = size.width * contentHorizontalOffset
                }
                .padding(top = logoTop)
                .size(logoSize),
            colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.primary)
        )

        Row(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = indicatorBottom),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            repeat(4) { index ->
                val alpha by animateFloatAsState(
                    targetValue = if (currentPage == index) 1f else 0.3f,
                    label = ""
                )

                Icon(
                    imageVector = Icons.Default.Circle,
                    contentDescription = null,
                    modifier = Modifier
                        .size(10.dp)
                        .alpha(alpha),
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }

        val isLastPage = state.isLastPage

        val buttonAlpha by animateFloatAsState(
            targetValue = if (isLastPage) 1f else 0f,
            label = ""
        )

        Button(
            onClick = {
                viewModel.finishOnboarding()
                onFinish()
            },
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .graphicsLayer {
                    translationX = size.width * contentHorizontalOffset
                }
                .padding(bottom = buttonBottom)
                .alpha(buttonAlpha)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(R.string.onboarding_button_start),
                    color = MaterialTheme.colorScheme.onPrimary
                )
                Spacer(modifier = Modifier.width(8.dp))
                Icon(
                    imageVector = Icons.AutoMirrored.Default.ArrowForward,
                    contentDescription = null,
                    modifier = Modifier.height(18.dp),
                    tint = MaterialTheme.colorScheme.onPrimary
                )
            }
        }
    }
}
