package com.example.momentary.utils

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.activity.result.ActivityResultLauncher
import androidx.core.content.FileProvider
import com.yalantis.ucrop.UCrop
import java.io.File

object ImageUtils {

    fun startCrop(
        context: Context,
        sourceUri: Uri,
        launcher: ActivityResultLauncher<Intent>
    ) {

        val destinationUri = Uri.fromFile(
            File(context.cacheDir, "cropped_${System.currentTimeMillis()}.jpg")
        )

        val options = UCrop.Options().apply {

            setAspectRatioOptions(
                CropConfig.DEFAULT_RATIO_INDEX,
                *CropConfig.DEFAULT_ASPECT_RATIOS
            )

            setFreeStyleCropEnabled(true)
        }

        val intent = UCrop.of(sourceUri, destinationUri)
            .withOptions(options)
            .withMaxResultSize(1080, 1080)
            .getIntent(context)

        launcher.launch(intent)
    }

    fun createImageUri(context: Context): Uri {
        val file = File.createTempFile(
            "camera_",
            ".jpg",
            context.cacheDir
        )

        return FileProvider.getUriForFile(
            context,
            "${context.packageName}.provider",
            file
        )
    }
}