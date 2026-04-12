package com.example.momentary.ui.screen.home.model

import com.example.momentary.data.model.Story

data class StoryUiModel(
    val id: String,
    val story: Story,
    val isOwnStory: Boolean,
    val isVerified: Boolean,
    val timeText: String,
    val locationText: String? = null
)
