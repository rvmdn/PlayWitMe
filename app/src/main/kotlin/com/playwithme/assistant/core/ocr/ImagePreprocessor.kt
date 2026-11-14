package com.playwithme.assistant.core.ocr

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.Matrix
import com.playwithme.assistant.core.common.Constants
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Preprocesses captured frames before OCR.
 *
 * Optimization pipeline:
 * 1. Decode raw bytes to [Bitmap]
 * 2. Downscale to [Constants.OCR_MAX_IMAGE_DIMENSION] (major speed improvement)
 * 3. Optional: Crop to Region of Interest
 *
 * This runs on [Dispatchers.Default] — never on UI thread.
 */
@Singleton
class ImagePreprocessor @Inject constructor() {

    /**
     * Prepares a raw frame for OCR.
     *
     * @param bytes Raw RGBA byte array from [CaptureService].
     * @param width Source frame width.
     * @param height Source frame height.
     * @param pixelStride Pixel stride from ImageReader.
     * @param rowStride Row stride from ImageReader.
     * @param cropRegion Optional region of interest to crop.
     * @return Processed [Bitmap] ready for ML Kit, or null on failure.
     */
    fun process(
        bytes: ByteArray,
        width: Int,
        height: Int,
        pixelStride: Int,
        rowStride: Int,
        cropRegion: RegionOfInterest? = null
    ): Bitmap? {
        return try {
            // Step 1: Create bitmap from raw bytes
            val bitmap = createBitmapFromRawBytes(bytes, width, height, pixelStride, rowStride)
                ?: return null

            // Step 2: Crop to region of interest (if specified)
            val cropped = if (cropRegion != null) {
                cropToRegion(bitmap, cropRegion)
            } else {
                bitmap
            }

            // Step 3: Downscale for performance
            downscale(cropped, Constants.OCR_MAX_IMAGE_DIMENSION)
        } catch (e: Exception) {
            null
        }
    }

    private fun createBitmapFromRawBytes(
        bytes: ByteArray,
        width: Int,
        height: Int,
        pixelStride: Int,
        rowStride: Int
    ): Bitmap? {
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val pixels = IntArray(width * height)

        for (row in 0 until height) {
            for (col in 0 until width) {
                val offset = row * rowStride + col * pixelStride
                if (offset + 3 >= bytes.size) break
                val r = bytes[offset].toInt() and 0xFF
                val g = bytes[offset + 1].toInt() and 0xFF
                val b = bytes[offset + 2].toInt() and 0xFF
                val a = bytes[offset + 3].toInt() and 0xFF
                pixels[row * width + col] = Color.argb(a, r, g, b)
            }
        }
        bitmap.setPixels(pixels, 0, width, 0, 0, width, height)
        return bitmap
    }

    private fun cropToRegion(bitmap: Bitmap, region: RegionOfInterest): Bitmap {
        val x = (bitmap.width * region.xFraction).toInt().coerceIn(0, bitmap.width - 1)
        val y = (bitmap.height * region.yFraction).toInt().coerceIn(0, bitmap.height - 1)
        val w = (bitmap.width * region.widthFraction).toInt().coerceAtMost(bitmap.width - x)
        val h = (bitmap.height * region.heightFraction).toInt().coerceAtMost(bitmap.height - y)
        return Bitmap.createBitmap(bitmap, x, y, w, h)
    }

    private fun downscale(bitmap: Bitmap, maxDimension: Int): Bitmap {
        val scale = minOf(
            maxDimension.toFloat() / bitmap.width,
            maxDimension.toFloat() / bitmap.height,
            1.0f // Never upscale
        )
        if (scale >= 1.0f) return bitmap
        val matrix = Matrix().apply { postScale(scale, scale) }
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
    }
}

/**
 * Defines a rectangular region of interest as fractions of the screen.
 *
 * Example: Top-right quadrant (kill feed area in MLBB):
 * ```kotlin
 * RegionOfInterest(xFraction = 0.5f, yFraction = 0f, widthFraction = 0.5f, heightFraction = 0.15f)
 * ```
 */
data class RegionOfInterest(
    val xFraction: Float,   // 0.0 = left edge, 1.0 = right edge
    val yFraction: Float,   // 0.0 = top edge, 1.0 = bottom edge
    val widthFraction: Float,
    val heightFraction: Float
) {
    companion object {
        /** Full screen (no crop). */
        val FULL_SCREEN = RegionOfInterest(0f, 0f, 1f, 1f)

        /** Top-right kill feed area (typical MLBB layout). */
        val KILL_FEED = RegionOfInterest(0.6f, 0f, 0.4f, 0.25f)

        /** Top-center area (objectives, events). */
        val OBJECTIVE_AREA = RegionOfInterest(0.3f, 0f, 0.4f, 0.1f)

        /** Bottom HUD area (skill cooldowns). */
        val HUD_BOTTOM = RegionOfInterest(0f, 0.8f, 1f, 0.2f)
    }
}
