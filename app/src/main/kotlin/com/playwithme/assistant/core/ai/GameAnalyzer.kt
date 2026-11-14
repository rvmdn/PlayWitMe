package com.playwithme.assistant.core.ai

import com.playwithme.assistant.domain.model.AnalysisResult
import com.playwithme.assistant.domain.model.OcrResult

/**
 * Core interface for the AI game analysis layer.
 *
 * Multiple implementations exist for different providers/modes.
 * The active implementation is selected via [AnalyzerFactory] based on [AppSettings.aiProvider].
 *
 * **Design principles:**
 * - All analysis is READ-ONLY — no game interaction whatsoever
 * - Interface must remain stable regardless of which backend is used
 * - Always async (suspend) to support both local and remote analyzers
 *
 * Implementations:
 * - [RuleBasedAnalyzer] — offline, instant, keyword-driven
 * - [GeminiAnalyzer] — Google Gemini API, requires internet + API key
 * - [OpenAIAnalyzer] — OpenAI API, requires internet + API key
 * - [OfflineAnalyzer] — TensorFlow Lite local model (future)
 */
interface GameAnalyzer {

    /**
     * Analyzes the given [OcrResult] and returns a list of [AnalysisResult]s.
     *
     * @param ocrResult The OCR output from the current game frame.
     * @return List of analysis results. Empty list if nothing significant detected.
     */
    suspend fun analyze(ocrResult: OcrResult): List<AnalysisResult>

    /**
     * Returns the display name of this analyzer for settings/debugging.
     */
    val analyzerName: String

    /**
     * Returns true if this analyzer is available (e.g., API key configured).
     */
    suspend fun isAvailable(): Boolean
}
