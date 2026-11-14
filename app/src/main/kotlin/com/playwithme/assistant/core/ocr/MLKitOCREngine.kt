package com.playwithme.assistant.core.ocr

import android.graphics.Bitmap
import com.playwithme.assistant.core.common.Result
import com.playwithme.assistant.domain.model.OcrResult
import com.playwithme.assistant.domain.model.OcrTextBlock
import com.playwithme.assistant.domain.model.OcrBoundingBox
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.TextRecognizer
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import kotlinx.coroutines.suspendCancellableCoroutine
import timber.log.Timber
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

/**
 * ML Kit-based OCR engine for reading game screen text.
 *
 * Converts ML Kit's [Task]-based API to suspending coroutine functions.
 * The recognizer instance is reused across calls (expensive to initialize).
 *
 * **Performance notes:**
 * - Always call from [Dispatchers.Default] (not Main)
 * - Preprocessed (downscaled) bitmaps process ~3x faster
 * - Reuse this singleton — ML Kit initialization takes ~200ms
 */
@Singleton
class MLKitOCREngine @Inject constructor() {

    private val recognizer: TextRecognizer =
        TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)

    /**
     * Processes a [Bitmap] and returns structured [OcrResult].
     *
     * @param bitmap Preprocessed bitmap (downscaled, cropped).
     * @return [Result.Success] with [OcrResult] or [Result.Error].
     */
    suspend fun recognize(bitmap: Bitmap): Result<OcrResult> {
        val startTime = System.currentTimeMillis()

        return try {
            val text = recognizeText(bitmap)
            val processingTime = System.currentTimeMillis() - startTime

            val blocks = text.textBlocks.flatMap { block ->
                block.lines.map { line ->
                    OcrTextBlock(
                        text = line.text,
                        boundingBox = line.boundingBox?.let {
                            OcrBoundingBox(it.left, it.top, it.right, it.bottom)
                        } ?: OcrBoundingBox(0, 0, 0, 0),
                        confidence = line.confidence ?: 0.8f
                    )
                }
            }

            Timber.v("OCR processed in ${processingTime}ms, ${blocks.size} blocks found")

            Result.Success(
                OcrResult(
                    id = UUID.randomUUID().toString(),
                    rawText = text.text,
                    blocks = blocks,
                    timestamp = startTime,
                    processingTimeMs = processingTime
                )
            )
        } catch (e: Exception) {
            Timber.e(e, "MLKit OCR failed")
            Result.Error(e)
        }
    }

    /**
     * Converts ML Kit Task<Text> to a suspending function.
     */
    private suspend fun recognizeText(bitmap: Bitmap) =
        suspendCancellableCoroutine { continuation ->
            val image = InputImage.fromBitmap(bitmap, 0)
            recognizer.process(image)
                .addOnSuccessListener { result -> continuation.resume(result) }
                .addOnFailureListener { e -> continuation.resumeWithException(e) }
                .addOnCanceledListener { continuation.cancel() }
        }

    /** Release ML Kit resources. Call when the service is destroyed. */
    fun close() {
        recognizer.close()
    }
}
