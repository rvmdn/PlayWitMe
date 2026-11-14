package com.playwithme.assistant.domain.usecase

import com.playwithme.assistant.core.common.Result
import com.playwithme.assistant.domain.model.AnalysisResult
import com.playwithme.assistant.domain.model.OcrResult
import com.playwithme.assistant.domain.repository.CaptureRepository
import com.playwithme.assistant.domain.repository.OcrRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/** Use case: Start screen capture. */
class StartCaptureUseCase @Inject constructor(
    private val repository: CaptureRepository
) {
    suspend operator fun invoke(resultCode: Int, data: android.content.Intent): Result<Unit> =
        repository.startCapture(resultCode, data)
}

/** Use case: Stop screen capture. */
class StopCaptureUseCase @Inject constructor(
    private val repository: CaptureRepository
) {
    suspend operator fun invoke(): Result<Unit> = repository.stopCapture()
}

/** Use case: Get live OCR result stream. */
class GetOcrResultStreamUseCase @Inject constructor(
    private val repository: OcrRepository
) {
    operator fun invoke(): Flow<OcrResult> = repository.getOcrResultStream()
}

/** Use case: Analyze OCR result using the AI layer. */
class AnalyzeOcrResultUseCase @Inject constructor(
    private val ocrRepository: OcrRepository
) {
    operator fun invoke(): Flow<OcrResult> = ocrRepository.getOcrResultStream()
}
