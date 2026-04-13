package com.example.momentary.ui.screen.addstory.component

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import com.example.momentary.ui.screen.addstory.AddStoryUiState

@Composable
fun AddStoryContent(
    modifier: Modifier,
    state: AddStoryUiState,
    name: String,
    onImageClick: () -> Unit,
    onDescriptionChange: (String) -> Unit,
    onClearFocus: () -> Unit
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .pointerInput(Unit) {
                detectTapGestures { onClearFocus() }
            }
            .padding(16.dp)
    ) {
        Column {
            ImagePicker(
                imageUri = state.imageUri,
                isLoading = state.isLoading,
                onClick = {
                    onImageClick()
                    onClearFocus()
                }
            )

            Spacer(Modifier.height(20.dp))

            StoryDescriptionInput(
                value = state.description,
                onValueChange = onDescriptionChange,
                name = name,
            )
        }
    }
}
