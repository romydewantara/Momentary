package com.example.momentary.utils

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.activity.result.ActivityResultLauncher
import androidx.core.content.FileProvider
import com.yalantis.ucrop.UCrop
import java.io.File
import java.net.HttpURLConnection
import java.net.URL

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

    fun loadBitmap(url: String): Bitmap? {
        return try {
            val connection = URL(url).openConnection() as HttpURLConnection
            connection.doInput = true
            connection.connect()

            val input = connection.inputStream

            val options = BitmapFactory.Options().apply {
                inSampleSize = 2
            }

            BitmapFactory.decodeStream(input, null, options)
        } catch (_: Exception) {
            null
        }
    }
}
