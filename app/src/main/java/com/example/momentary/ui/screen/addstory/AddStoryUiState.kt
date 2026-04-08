package com.example.momentary.ui.screen.addstory

data class AddStoryUiState(
    val imageUri: String? = null,
    val description: String = "",
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false
) {
    val isValid: Boolean
        get() = imageUri != null && description.isNotBlank()
}