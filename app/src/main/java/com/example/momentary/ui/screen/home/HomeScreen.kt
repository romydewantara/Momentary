package com.example.momentary.ui.screen.home

import android.content.Intent
import android.provider.Settings
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Map
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavBackStackEntry
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemContentType
import androidx.paging.compose.itemKey
import com.example.momentary.R
import com.example.momentary.data.model.Story
import com.example.momentary.ui.common.CustomSnackBar
import com.example.momentary.ui.common.ErrorState
import com.example.momentary.ui.common.UiText
import com.example.momentary.ui.navigation.NavKeys
import com.example.momentary.ui.screen.home.component.AppDrawer
import com.example.momentary.ui.screen.home.component.AppendErrorItem
import com.example.momentary.ui.screen.home.component.EmptyState
import com.example.momentary.ui.screen.home.component.ShimmerItem
import com.example.momentary.ui.screen.home.component.StoryItem
import com.example.momentary.utils.toEpochMillis
import com.example.momentary.utils.toRelativeTime
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    backStackEntry: NavBackStackEntry,
    sharedTransitionScope: SharedTransitionScope,
    animatedVisibilityScope: AnimatedVisibilityScope,
    onLogout: () -> Unit,
    onNavigateToDetail: (Story) -> Unit,
    onNavigateToAddStory: () -> Unit,
    onNavigateToMaps: () -> Unit,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val stories = viewModel.stories.collectAsLazyPagingItems()
    val listState = rememberLazyListState()
    val context = LocalContext.current
    val snackBarHostState = remember { SnackbarHostState() }
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val session by viewModel.session.collectAsStateWithLifecycle()

    val isManualRefreshing = remember { mutableStateOf(false) }
    var isErrorSnackBar by remember { mutableStateOf(false) }
    val hasShownLoadStateError = rememberSaveable { mutableStateOf(false) }

    val savedStateHandle = backStackEntry.savedStateHandle
    val homeSuccessMessage = savedStateHandle.get<String>(NavKeys.HOME_SUCCESS_MESSAGE)

    LaunchedEffect(homeSuccessMessage) {
        homeSuccessMessage?.let {
            snackBarHostState.showSnackbar(message = it)
            savedStateHandle.remove<String>(NavKeys.HOME_SUCCESS_MESSAGE)
        }
    }

    LaunchedEffect(stories.loadState.refresh) {
        val refreshSate = stories.loadState.refresh
        if (isManualRefreshing.value && refreshSate is LoadState.NotLoading && stories.itemCount > 0) {
            listState.animateScrollToItem(0)
            isManualRefreshing.value = false
        }
    }

    LaunchedEffect(stories.loadState) {
        val refreshState = stories.loadState.refresh
        val isShown = hasShownLoadStateError.value
        if (refreshState is LoadState.Error) {
            if (!isShown) {
                val message = if (refreshState.error.message?.contains("timeout", true) == true) {
                    UiText.StringResource(R.string.error_timeout)
                } else {
                    UiText.StringResource(R.string.error_network)
                }

                isErrorSnackBar = true

                snackBarHostState.showSnackbar(message.asString(context))
                hasShownLoadStateError.value = true
            }
        } else if (refreshState is LoadState.NotLoading || refreshState is LoadState.Loading) {
            hasShownLoadStateError.value = false
        }
    }

    LaunchedEffect(stories.itemSnapshotList.items) {
        stories.itemSnapshotList.items.forEach { item ->
            viewModel.resolveLocation(item.story)
        }
    }

    LaunchedEffect(Unit) {
        launch {
            viewModel.event.collect { event ->
                isErrorSnackBar = event.isError

                snackBarHostState.showSnackbar(
                    message = event.message.asString(context)
                )
            }
        }
        launch {
            viewModel.logoutEvent.collect {
                onLogout()
            }
        }
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        gesturesEnabled = !drawerState.isClosed,
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
                viewModel = viewModel
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
                        IconButton(
                            modifier = Modifier.testTag("drawer_menu_button"),
                            onClick = { scope.launch { drawerState.open() } }
                        ) {
                            Icon(
                                painter = painterResource(R.drawable.ic_menu),
                                contentDescription = stringResource(R.string.cd_button_menu)
                            )
                        }
                    },
                    actions = {
                        IconButton(
                            modifier = Modifier.testTag("add_story"),
                            onClick = onNavigateToAddStory
                        ) {
                            Icon(
                                painter = painterResource(R.drawable.ic_add),
                                contentDescription = stringResource(R.string.cd_story)
                            )
                        }
                        IconButton(
                            onClick = onNavigateToMaps
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.Map,
                                contentDescription = stringResource(R.string.cd_maps)
                            )
                        }
                    }
                )
            }
        ) { padding ->
            val refreshState = stories.loadState.refresh

            when {
                refreshState is LoadState.Loading && stories.itemCount == 0 -> {
                    LazyColumn(
                        modifier = Modifier
                            .padding(padding)
                            .fillMaxSize()
                    ) {
                        items(5) {
                            ShimmerItem()
                            HorizontalDivider(
                                thickness = 0.8.dp,
                                color = MaterialTheme.colorScheme.outlineVariant
                            )
                        }
                    }
                }

                else -> {
                    PullToRefreshBox(
                        isRefreshing = refreshState is LoadState.Loading,
                        onRefresh = {
                            isManualRefreshing.value = true
                            stories.refresh()
                        },
                        modifier = Modifier
                            .padding(padding)
                            .fillMaxSize()
                    ) {
                        Box(
                            modifier = Modifier.fillMaxSize()
                        ) {
                            when (refreshState) {
                                is LoadState.Error if stories.itemCount == 0 -> {
                                    ErrorState(
                                        message = refreshState.error.message
                                            ?: stringResource(R.string.error_unknown)
                                    )
                                }

                                is LoadState.NotLoading if stories.itemCount == 0 -> {
                                    EmptyState()
                                }

                                else -> {
                                    LazyColumn(
                                        state = listState,
                                        modifier = Modifier
                                            .testTag("stories_list")
                                            .fillMaxSize()
                                    ) {
                                        items(
                                            count = stories.itemCount,
                                            key = stories.itemKey { it.id },
                                            contentType = stories.itemContentType { "story" }
                                        ) { index ->
                                            stories[index]?.let { item ->
                                                val locationText = state.locationMap[item.id]
                                                val timeText = item.createdAt
                                                    .toEpochMillis()
                                                    .toRelativeTime(context = context)

                                                Column {
                                                    StoryItem(
                                                        id = item.story.id,
                                                        name = item.story.name,
                                                        description = item.story.description,
                                                        photoUrl = item.story.photoUrl,
                                                        isVerified = item.isVerified,
                                                        isOwnStory = item.isOwnStory,
                                                        locationText = locationText,
                                                        timeText = timeText,
                                                        sharedTransitionScope = sharedTransitionScope,
                                                        animatedVisibilityScope = animatedVisibilityScope,
                                                        onImageLoaded = { ratio ->
                                                            viewModel.setAspectRatio(
                                                                item.id,
                                                                ratio
                                                            )
                                                        },
                                                        onClick = {
                                                            onNavigateToDetail(item.story)
                                                        }
                                                    )

                                                    if (index < stories.itemCount - 1) {
                                                        HorizontalDivider(
                                                            thickness = 0.8.dp,
                                                            color = MaterialTheme.colorScheme.outlineVariant
                                                        )
                                                    }
                                                }
                                            }
                                        }

                                        when (stories.loadState.append) {
                                            is LoadState.Loading -> {
                                                item {
                                                    Column(
                                                        modifier = Modifier
                                                            .fillMaxWidth()
                                                            .padding(vertical = 16.dp),
                                                        horizontalAlignment = Alignment.CenterHorizontally
                                                    ) {
                                                        CircularProgressIndicator()
                                                    }
                                                }
                                            }

                                            is LoadState.Error -> {
                                                item {
                                                    AppendErrorItem(
                                                        onRetry = { stories.retry() }
                                                    )
                                                }
                                            }

                                            else -> Unit
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
