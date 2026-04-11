package com.example.momentary.ui.screen.addstory

import android.app.Activity
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.example.momentary.R
import com.example.momentary.ui.common.CustomSnackBar
import com.example.momentary.ui.common.StoryTopBar
import com.example.momentary.ui.screen.addstory.component.AddStoryContent
import com.example.momentary.ui.screen.addstory.component.ImageSourceBottomSheet
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
    val state by viewModel.state.collectAsState()
    val context = LocalContext.current
    val snackBarHostState = remember { SnackbarHostState() }
    var isErrorSnackBar by remember { mutableStateOf(false) }
    var showBottomSheet by remember { mutableStateOf(false) }
    var tempCameraUri by remember { mutableStateOf<Uri?>(null) }
    val sheetState = rememberModalBottomSheetState()
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
            UploadButton(
                state = state,
                onClick = {
                    viewModel.uploadStory()
                    focusManager.clearFocus()
                }
            )
        }
    ) { padding ->

        AddStoryContent(
            modifier = modifier.padding(padding),
            state = state,
            onImageClick = { showBottomSheet = true },
            onDescriptionChange = viewModel::setDescription,
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
    }
}
