package com.example.momentary.ui.screen.home

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.example.momentary.R
import com.example.momentary.data.model.Story
import com.example.momentary.ui.common.CustomSnackBar
import com.example.momentary.ui.screen.home.component.EmptyState
import com.example.momentary.ui.screen.home.component.ErrorState
import com.example.momentary.ui.screen.home.component.ShimmerItem
import com.example.momentary.ui.screen.home.component.StoryItem

@OptIn(ExperimentalMaterial3Api::class, ExperimentalSharedTransitionApi::class)
@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    sharedTransitionScope: SharedTransitionScope,
    animatedVisibilityScope: AnimatedVisibilityScope,
    onLogout: () -> Unit,
    onNavigateToDetail: (Story) -> Unit,
    onNavigateToAddStory: () -> Unit,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()

    val snackBarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current

    var isErrorSnackBar by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.event.collect { event ->
            isErrorSnackBar = event.isError

            snackBarHostState.showSnackbar(
                message = event.message.asString(context)
            )
        }
    }

    Scaffold(
        modifier = modifier,
        snackbarHost = {
            SnackbarHost(
                hostState = snackBarHostState,
                snackbar = { data ->
                    CustomSnackBar(
                        message = data.visuals.message,
                        isError = isErrorSnackBar
                    )
                }
            )
        },
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.app_name)) },
                actions = {
                    IconButton(onClick = { viewModel.logout(onLogout) }) {
                        Icon(Icons.AutoMirrored.Default.Logout, contentDescription = null)
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onNavigateToAddStory) {
                Icon(Icons.Default.Add, contentDescription = null)
            }
        }
    ) { padding ->

        when {
            state.isLoading -> {
                LazyColumn(
                    modifier = Modifier
                        .padding(padding)
                        .fillMaxSize()
                ) {
                    items(5) {
                        ShimmerItem()
                    }
                }
            }

            else -> {
                PullToRefreshBox(
                    isRefreshing = state.isRefreshing,
                    onRefresh = { viewModel.refresh() },
                    modifier = Modifier
                        .padding(padding)
                        .fillMaxSize()
                ) {
                    val error = state.error
                    val stories = state.stories

                    when {
                        error != null && stories.isEmpty() -> {
                            ErrorState(
                                message = error.asString(context)
                            )
                        }

                        stories.isEmpty() -> {
                            EmptyState()
                        }

                        else -> {
                            LazyColumn {
                                items(
                                    items = stories,
                                    key = { it.id }
                                ) { story ->
                                    StoryItem(
                                        story = story,
                                        sharedTransitionScope = sharedTransitionScope,
                                        animatedVisibilityScope = animatedVisibilityScope,
                                        onClick = { onNavigateToDetail(story) }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
