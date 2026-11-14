package com.playwithme.assistant.core.state

import com.playwithme.assistant.core.ocr.GameTextParser
import com.playwithme.assistant.domain.model.OcrResult
import com.playwithme.assistant.domain.model.ScreenState
import com.playwithme.assistant.service.OCRService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

import android.graphics.Rect

enum class ScanStep {
    MAIN_INFO,
    SKILL_1,
    SKILL_2,
    SKILL_3,
    ULTIMATE
}

data class ScanCommand(
    val step: ScanStep,
    val captureRect: Rect // A generic rect that targets whatever the current step needs
)

/**
 * Manages the high-level ScreenState (LOBBY, DRAFT, IN_GAME, SCANNING)
 * by analyzing OCR results over time and applying debouncing/thresholds
 * to prevent false positives.
 */
@Singleton
class GameStateManager @Inject constructor(
    private val gameTextParser: GameTextParser
) {
    private val _screenState = MutableStateFlow(ScreenState.UNKNOWN)
    val screenState: StateFlow<ScreenState> = _screenState.asStateFlow()

    private val _scanCommandFlow = kotlinx.coroutines.flow.MutableSharedFlow<ScanCommand>(extraBufferCapacity = 1)
    val scanCommandFlow = _scanCommandFlow.asSharedFlow()

    private var isManualMode = false

    private val scope = CoroutineScope(Dispatchers.Default)

    // Thresholds
    private val requiredHits = 3
    private var currentHitCount = 0
    private var pendingState: ScreenState = ScreenState.UNKNOWN

    init {
        scope.launch {
            OCRService.ocrResultFlow.collect { ocrResult ->
                analyzeScreen(ocrResult)
            }
        }
    }

    fun setManualState(state: ScreenState) {
        isManualMode = state != ScreenState.UNKNOWN
        _screenState.value = state
        pendingState = state
    }

    fun triggerScan(command: ScanCommand) {
        _scanCommandFlow.tryEmit(command)
    }

    private fun analyzeScreen(ocrResult: OcrResult) {
        if (isManualMode) return

        val rawText = ocrResult.rawText.lowercase()
        
        val detectedState = when {
            isDraft(rawText) -> ScreenState.DRAFT
            isLobby(rawText) -> ScreenState.LOBBY
            isInGame(rawText) -> ScreenState.IN_GAME
            else -> ScreenState.UNKNOWN
        }

        if (detectedState != ScreenState.UNKNOWN) {
            if (detectedState == pendingState) {
                currentHitCount++
                if (currentHitCount >= requiredHits && _screenState.value != detectedState) {
                    Timber.d("GameStateManager: State changed to \$detectedState")
                    _screenState.value = detectedState
                }
            } else {
                pendingState = detectedState
                currentHitCount = 1
            }
        }
    }

    private fun isLobby(text: String): Boolean {
        return text.containsAny("ranked", "classic", "brawl", "matchup", "enter", "invite", "start game")
    }

    private fun isDraft(text: String): Boolean {
        return text.containsAny("banning phase", "picking phase", "enemy picking", "choose hero", "ban", "pick", "select your hero")
    }

    private fun isInGame(text: String): Boolean {
        return text.containsAny(
            "00:", "01:", "02:", "$", "first blood", "turtle", "lord",
            "recall", "regen", "revitalize", "burst", "aoe", "retribution", "flicker",
            "gold lane", "exp lane", "mid lane", "roaming", "movement speed"
        )
    }

    private fun String.containsAny(vararg keywords: String): Boolean =
        keywords.any { this.contains(it, ignoreCase = true) }
}
