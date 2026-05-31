package com.example.momentary.ui.screen.addstory

import android.app.Activity
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.momentary.R
import com.example.momentary.ui.common.CustomSnackBar
import com.example.momentary.ui.common.StoryTopBar
import com.example.momentary.ui.screen.addstory.component.AddStoryContent
import com.example.momentary.ui.screen.addstory.component.ImageSourceBottomSheet
import com.example.momentary.ui.screen.addstory.component.LocationPickerSheet
import com.example.momentary.ui.screen.addstory.component.UploadButton
import com.example.momentary.utils.ImageUtils
import com.yalantis.ucrop.UCrop

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddStoryScreen(
    modifier: Modifier = Modifier,
    onUploadSuccess: (String) -> Unit,
    onBack: () -> Unit,
    viewModel: AddStoryViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val session by viewModel.session.collectAsStateWithLifecycle()
    val context = LocalContext.current

    val snackBarHostState = remember { SnackbarHostState() }
    var isErrorSnackBar by remember { mutableStateOf(false) }

    var showBottomSheet by remember { mutableStateOf(false) }
    var tempCameraUri by remember { mutableStateOf<Uri?>(null) }
    val sheetState = rememberModalBottomSheetState()

    var showLocationPicker by remember { mutableStateOf(false) }
    var showLocationSection by remember { mutableStateOf(false) }

    val focusManager = LocalFocusManager.current

    val addStorySuccessMessage = stringResource(R.string.add_story_success)

    val cropLauncher =
        rememberLauncherForActivityResult(
            contract = ActivityResultContracts.StartActivityForResult()
        ) { result ->

            if (result.resultCode == Activity.RESULT_OK) {
                val resultUri = result.data?.let(UCrop::getOutput)
                viewModel.setImage(resultUri.toString())
            }
        }

    val galleryLauncher =
        rememberLauncherForActivityResult(
            ActivityResultContracts.GetContent()
        ) { uri ->
            uri?.let { ImageUtils.startCrop(context, it, cropLauncher) }
        }

    val cameraLauncher =
        rememberLauncherForActivityResult(
            ActivityResultContracts.TakePicture()
        ) { success ->
            if (success) {
                tempCameraUri?.let { ImageUtils.startCrop(context, it, cropLauncher) }
            }
        }

    val openCamera: () -> Unit = {
        val uri = ImageUtils.createImageUri(context)
        tempCameraUri = uri
        cameraLauncher.launch(uri)
    }

    val cameraPermissionLauncher =
        rememberLauncherForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { granted ->
            if (granted) {
                openCamera()
            }
        }

    LaunchedEffect(Unit) {
        viewModel.event.collect { event ->
            isErrorSnackBar = event.isError
            snackBarHostState.showSnackbar(event.message.asString(context))
        }
    }

    LaunchedEffect(state.isSuccess) {
        if (state.isSuccess) onUploadSuccess(addStorySuccessMessage)
    }

    val hideBottomSheet = remember {
        { showBottomSheet = false }
    }

    val hideLocationPickerBottomSheet = remember {
        { showLocationPicker = false }
    }

    Scaffold(
        topBar = {
            StoryTopBar(
                title = stringResource(R.string.add_story_title),
                onBack = onBack
            )
        },
        snackbarHost = {
            SnackbarHost(snackBarHostState) { data ->
                CustomSnackBar(data.visuals.message, isErrorSnackBar)
            }
        },
        bottomBar = {
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 16.dp)
                ) {
                    Checkbox(
                        checked = showLocationSection,
                        onCheckedChange = { showLocationSection = it },
                    )

                    Text(
                        text = stringResource(R.string.add_story_description_location),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }

                UploadButton(
                    modifier = Modifier.testTag("upload_button"),
                    state = state,
                    onClick = {
                        if (!showLocationSection) {
                            viewModel.clearLocation()
                        }
                        viewModel.uploadStory()
                        focusManager.clearFocus()
                    }
                )
            }
        }
    ) { padding ->

        AddStoryContent(
            modifier = modifier.padding(padding),
            state = state,
            name = session.name.toString(),
            showLocationSection = showLocationSection,
            onImageClick = { showBottomSheet = true },
            onDescriptionChange = viewModel::setDescription,
            onLocationClick = { showLocationPicker = true },
            onDeleteLocation = viewModel::clearLocation,
            onClearFocus = { focusManager.clearFocus() }
        )

        ImageSourceBottomSheet(
            show = showBottomSheet,
            sheetState = sheetState,
            onDismiss = hideBottomSheet,
            onGalleryClick = {
                galleryLauncher.launch("image/*")
            },
            onCameraClick = {
                cameraPermissionLauncher.launch(android.Manifest.permission.CAMERA)
            }
        )

        if (showLocationPicker) {
            ModalBottomSheet(
                onDismissRequest = {
                    hideLocationPickerBottomSheet()
                },
                dragHandle = null,
                sheetState = rememberModalBottomSheetState(
                    skipPartiallyExpanded = true
                ),
                sheetGesturesEnabled = false
            ) {
                LocationPickerSheet(
                    selectedLocation = state.selectedLocation,
                    pendingAddress = state.pendingAddress,
                    isResolvingAddress = state.isResolvingAddress,
                    onMarkerTapped = viewModel::resolveAddress,
                    onLocationSelected = { lat, lon ->
                        viewModel.selectLocation(lat, lon)
                        showLocationPicker = false
                    }
                )
            }
        }
    }
}
