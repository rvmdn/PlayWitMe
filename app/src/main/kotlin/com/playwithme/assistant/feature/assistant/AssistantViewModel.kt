package com.playwithme.assistant.feature.assistant

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.playwithme.assistant.core.ai.AnalyzerFactory
import com.playwithme.assistant.domain.model.*
import com.playwithme.assistant.domain.usecase.GetSettingsUseCase
import com.playwithme.assistant.service.OCRService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import timber.log.Timber
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class AssistantViewModel @Inject constructor(
    private val getSettingsUseCase: GetSettingsUseCase,
    private val analyzerFactory: AnalyzerFactory
) : ViewModel() {

    private val _uiState = MutableStateFlow(AssistantUiState())
    val uiState: StateFlow<AssistantUiState> = _uiState.asStateFlow()

    // Timer management
    private val _timers = MutableStateFlow<List<TimerEvent>>(emptyList())

    init {
        observeSettings()
        observeOcrResults()
    }

    private fun observeSettings() {
        getSettingsUseCase()
            .onEach { settings ->
                _uiState.update {
                    it.copy(
                        isOcrEnabled = settings.isOcrEnabled,
                        isAiEnabled = settings.isAiEnabled
                    )
                }
            }
            .launchIn(viewModelScope)
    }

    private fun observeOcrResults() {
        OCRService.ocrResultFlow
            .onEach { ocrResult ->
                _uiState.update { it.copy(lastOcrResult = ocrResult, ocrProcessingTimeMs = ocrResult.processingTimeMs) }
                analyzeOcr(ocrResult)
            }
            .catch { e -> Timber.e(e, "AssistantViewModel: OCR stream error") }
            .launchIn(viewModelScope)
    }

    private suspend fun analyzeOcr(ocrResult: OcrResult) {
        if (!_uiState.value.isAiEnabled) return
        if (ocrResult.rawText.isBlank()) return

        try {
            val settings = getSettingsUseCase().first()
            val analyzer = analyzerFactory.getAnalyzer(settings.aiProvider)
            val results = analyzer.analyze(ocrResult)

            if (results.isNotEmpty()) {
                _uiState.update { state ->
                    state.copy(
                        recentEvents = (results + state.recentEvents).take(20) // Keep last 20
                    )
                }
            }
        } catch (e: Exception) {
            Timber.e(e, "AssistantViewModel: analysis failed")
        }
    }

    /** Start a manual timer for an objective. */
    fun startTimer(type: TimerType) {
        val totalSeconds = when (type) {
            TimerType.LORD -> 360
            TimerType.TURTLE -> 90
            TimerType.BUFF_ALLY, TimerType.BUFF_ENEMY -> 90
            TimerType.CUSTOM -> 60
        }

        val timer = TimerEvent(
            id = UUID.randomUUID().toString(),
            type = type,
            totalSeconds = totalSeconds,
            remainingSeconds = totalSeconds,
            startedAt = System.currentTimeMillis(),
            isManual = true
        )

        _uiState.update { state ->
            state.copy(activeTimers = state.activeTimers + timer)
        }

        // Countdown coroutine
        viewModelScope.launch {
            repeat(totalSeconds) {
                kotlinx.coroutines.delay(1000)
                _uiState.update { state ->
                    val updated = state.activeTimers.map { t ->
                        if (t.id == timer.id) t.copy(remainingSeconds = t.remainingSeconds - 1)
                        else t
                    }.filter { !it.isExpired }
                    state.copy(activeTimers = updated)
                }
            }
        }
    }

    fun dismissTimer(timerId: String) {
        _uiState.update { state ->
            state.copy(activeTimers = state.activeTimers.filter { it.id != timerId })
        }
    }

    fun dismissEvent(eventId: String) {
        _uiState.update { state ->
            state.copy(recentEvents = state.recentEvents.filter { it.id != eventId })
        }
    }

    fun clearAllEvents() {
        _uiState.update { it.copy(recentEvents = emptyList()) }
    }
}
