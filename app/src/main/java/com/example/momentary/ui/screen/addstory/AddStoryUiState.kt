package com.example.momentary.ui.screen.addstory

data class StoryLocation(
    val lat: Double,
    val lon: Double,
    val address: String
)

data class AddStoryUiState(
    val imageUri: String? = null,
    val description: String = "",
    val pendingAddress: String? = null,
    val isResolvingAddress: Boolean = false,
    val selectedLocation: StoryLocation? = null,
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false
) {
    val isValid: Boolean
        get() = imageUri != null && description.isNotBlank()
}
