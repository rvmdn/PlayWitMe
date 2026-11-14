package com.playwithme.assistant.domain.repository

import com.playwithme.assistant.core.common.Result
import com.playwithme.assistant.domain.model.OcrResult
import kotlinx.coroutines.flow.Flow

/** Repository interface for OCR processing. */
interface OcrRepository {
    /** Stream of OCR results from the capture pipeline. */
    fun getOcrResultStream(): Flow<OcrResult>

    /** Process a single bitmap and return OCR result. */
    suspend fun processFrame(bitmapData: ByteArray, width: Int, height: Int): Result<OcrResult>

    /** Enable/disable OCR processing. */
    suspend fun setEnabled(enabled: Boolean)
}

/** Repository interface for screen capture (MediaProjection). */
interface CaptureRepository {
    /** Current capture state. */
    val captureState: Flow<CaptureState>

    /** Start capture with MediaProjection token. */
    suspend fun startCapture(resultCode: Int, data: android.content.Intent): Result<Unit>

    /** Stop capture and release resources. */
    suspend fun stopCapture(): Result<Unit>

    /** Adjust capture frame rate. */
    suspend fun setFrameRate(fps: Int)
}

sealed class CaptureState {
    data object Idle : CaptureState()
    data object Starting : CaptureState()
    data object Running : CaptureState()
    data object Stopping : CaptureState()
    data class Error(val message: String) : CaptureState()
}
