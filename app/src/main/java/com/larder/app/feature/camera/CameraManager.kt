package com.larder.app.feature.camera

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import java.io.File
import java.io.FileOutputStream
import java.util.UUID

enum class ScanType { RECEIPT, SHELF }

data class CapturedPhoto(
    val file: File,
    val scanType: ScanType,
    val width: Int,
    val height: Int
)

class CameraManager(private val context: Context) {

    /**
     * Saves a captured bitmap image to local cache directory with compression.
     */
    fun saveCapturedImage(
        bitmap: Bitmap,
        scanType: ScanType,
        quality: Int = 85
    ): CapturedPhoto {
        val outputDir = File(context.cacheAreaDir(), "scans").apply { if (!exists()) mkdirs() }
        val filename = "scan_${scanType.name.lowercase()}_${UUID.randomUUID()}.jpg"
        val photoFile = File(outputDir, filename)

        FileOutputStream(photoFile).use { out ->
            bitmap.compress(Bitmap.CompressFormat.JPEG, quality, out)
        }

        return CapturedPhoto(
            file = photoFile,
            scanType = scanType,
            width = bitmap.width,
            height = bitmap.height
        )
    }

    /**
     * Rotates a bitmap by [degrees] if needed.
     */
    fun rotateBitmap(bitmap: Bitmap, degrees: Float): Bitmap {
        if (degrees == 0f) return bitmap
        val matrix = Matrix().apply { postRotate(degrees) }
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
    }

    private fun Context.cacheAreaDir(): File = this.externalCacheDir ?: this.cacheDir
}
