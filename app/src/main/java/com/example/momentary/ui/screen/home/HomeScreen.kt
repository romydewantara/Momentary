package com.example.momentary.ui.screen.home

import android.content.Intent
import android.provider.Settings
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.example.momentary.R
import com.example.momentary.data.model.Story
import com.example.momentary.ui.common.CustomSnackBar
import com.example.momentary.ui.screen.home.component.AppDrawer
import com.example.momentary.ui.screen.home.component.EmptyState
import com.example.momentary.ui.screen.home.component.ErrorState
import com.example.momentary.ui.screen.home.component.ShimmerItem
import com.example.momentary.ui.screen.home.component.StoryItem
import com.example.momentary.utils.Constants
import com.example.momentary.utils.toEpochMillis
import com.example.momentary.utils.toRelativeTime
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
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

    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val session by viewModel.session.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.event.collect { event ->
            isErrorSnackBar = event.isError

            snackBarHostState.showSnackbar(
                message = event.message.asString(context)
            )
        }
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            AppDrawer(
                name = session.name ?: stringResource(R.string.drawer_profile_default_name),
                email = session.email ?: stringResource(R.string.drawer_profile_default_email),
                drawerState = drawerState,
                onClose = {
                    scope.launch { drawerState.close() }
                },
                onChangeLanguage = {
                    val intent = Intent(Settings.ACTION_LOCALE_SETTINGS).apply {
                        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    }
                    context.startActivity(intent)
                },
                onLogout = {
                    scope.launch {
                        drawerState.close()
                        viewModel.logout(onLogout)
                    }
                }
            )
        }
    ) {
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
                CenterAlignedTopAppBar(
                    title = {
                        Image(
                            painter = painterResource(R.drawable.ic_app_header),
                            contentDescription = stringResource(R.string.cd_header),
                            modifier = Modifier
                                .height(28.dp),
                            colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onSurfaceVariant)
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = { scope.launch { drawerState.open() } }) {
                            Icon(
                                painter = painterResource(R.drawable.ic_menu),
                                contentDescription = stringResource(R.string.cd_button_menu)
                            )
                        }
                    },
                    actions = {
                        IconButton(onClick = onNavigateToAddStory) {
                            Icon(
                                painter = painterResource(R.drawable.ic_add),
                                contentDescription = stringResource(R.string.cd_story)
                            )
                        }
                    }
                )
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
                                    itemsIndexed(
                                        items = stories,
                                        key = { _, it -> it.id }
                                    ) { index, story ->
                                        val isOwnStory = story.name == session.name
                                        val isVerified =
                                            story.name == Constants.OFFICIAL_ACCOUNT_NAME || isOwnStory
                                        val timeText = remember(story.createdAt) {
                                            story.createdAt.toEpochMillis().toRelativeTime()
                                        }

                                        Column {
                                            StoryItem(
                                                story = story,
                                                isVerified = isVerified,
                                                isOwnStory = isOwnStory,
                                                timeText = timeText,
                                                sharedTransitionScope = sharedTransitionScope,
                                                animatedVisibilityScope = animatedVisibilityScope,
                                                onClick = { onNavigateToDetail(story) },
                                                viewModel = viewModel
                                            )

                                            if (index < stories.lastIndex) {
                                                HorizontalDivider(
                                                    thickness = 0.8.dp,
                                                    color = MaterialTheme.colorScheme.outlineVariant
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
        }
    }
}
