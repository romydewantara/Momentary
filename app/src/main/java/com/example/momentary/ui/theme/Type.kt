package com.example.momentary.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import com.example.momentary.R

val MulishFontFamily = FontFamily(
    Font(R.font.mulish_regular, FontWeight.Normal),
    Font(R.font.mulish_medium, FontWeight.Medium),
    Font(R.font.mulish_bold, FontWeight.Bold)
)

val DefaultTypography = Typography()

val AppTypography = Typography(
    displayLarge = DefaultTypography.displayLarge.copy(fontFamily = MulishFontFamily),
    displayMedium = DefaultTypography.displayMedium.copy(fontFamily = MulishFontFamily),
    displaySmall = DefaultTypography.displaySmall.copy(fontFamily = MulishFontFamily),

    headlineLarge = DefaultTypography.headlineLarge.copy(fontFamily = MulishFontFamily),
    headlineMedium = DefaultTypography.headlineMedium.copy(fontFamily = MulishFontFamily),
    headlineSmall = DefaultTypography.headlineSmall.copy(fontFamily = MulishFontFamily),

    titleLarge = DefaultTypography.titleLarge.copy(fontFamily = MulishFontFamily),
    titleMedium = DefaultTypography.titleMedium.copy(fontFamily = MulishFontFamily),
    titleSmall = DefaultTypography.titleSmall.copy(fontFamily = MulishFontFamily),

    bodyLarge = DefaultTypography.bodyLarge.copy(fontFamily = MulishFontFamily),
    bodyMedium = DefaultTypography.bodyMedium.copy(fontFamily = MulishFontFamily),
    bodySmall = DefaultTypography.bodySmall.copy(fontFamily = MulishFontFamily),

    labelLarge = DefaultTypography.labelLarge.copy(fontFamily = MulishFontFamily),
    labelMedium = DefaultTypography.labelMedium.copy(fontFamily = MulishFontFamily),
    labelSmall = DefaultTypography.labelSmall.copy(fontFamily = MulishFontFamily),
)