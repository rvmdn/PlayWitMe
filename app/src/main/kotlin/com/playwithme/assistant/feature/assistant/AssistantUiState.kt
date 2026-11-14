package com.playwithme.assistant.feature.assistant

import com.playwithme.assistant.domain.model.AnalysisResult
import com.playwithme.assistant.domain.model.OcrResult
import com.playwithme.assistant.domain.model.TimerEvent
import com.playwithme.assistant.domain.model.TimerType

data class AssistantUiState(
    val isOcrEnabled: Boolean = false,
    val isAiEnabled: Boolean = false,
    val lastOcrResult: OcrResult? = null,
    val recentEvents: List<AnalysisResult> = emptyList(),
    val activeTimers: List<TimerEvent> = emptyList(),
    val isCapturing: Boolean = false,
    val ocrProcessingTimeMs: Long = 0L,
    val errorMessage: String? = null
)
