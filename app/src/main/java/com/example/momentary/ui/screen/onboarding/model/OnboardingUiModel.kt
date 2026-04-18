package com.example.momentary.ui.screen.onboarding.model

import com.example.momentary.R

data class OnboardingUiModel(
    val image: Int,
    val title: Int,
    val desc: Int
)

fun getOnboardingContent(page: Int): OnboardingUiModel = when (page) {
    0 -> OnboardingUiModel(
        R.drawable.onboarding_image_first,
        R.string.onboarding_title_first,
        R.string.onboarding_desc_first
    )

    1 -> OnboardingUiModel(
        R.drawable.onboarding_image_second,
        R.string.onboarding_title_second,
        R.string.onboarding_desc_second
    )

    2 -> OnboardingUiModel(
        R.drawable.onboarding_image_third,
        R.string.onboarding_title_third,
        R.string.onboarding_desc_third
    )

    else -> OnboardingUiModel(
        R.drawable.onboarding_image_fourth,
        R.string.onboarding_title_fourth,
        R.string.onboarding_desc_fourth
    )
}
