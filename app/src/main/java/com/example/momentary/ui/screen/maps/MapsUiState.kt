package com.example.momentary.ui.screen.maps

import com.example.momentary.data.model.Story
import com.example.momentary.ui.common.UiText

data class MapsUiState(
    val stories: List<Story> = emptyList(),
    val isLoading: Boolean = false,
    val error: UiText? = null
)
