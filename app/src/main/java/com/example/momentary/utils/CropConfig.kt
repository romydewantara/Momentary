package com.example.momentary.utils

import com.yalantis.ucrop.model.AspectRatio

object CropConfig {

    val DEFAULT_ASPECT_RATIOS = arrayOf(
        AspectRatio("FREE", 0f, 0f),
        AspectRatio("1:1", 1f, 1f),
        AspectRatio("4:3", 4f, 3f),
        AspectRatio("3:4", 3f, 4f),
        AspectRatio("16:9", 16f, 9f)
    )

    const val DEFAULT_RATIO_INDEX = 2
}
