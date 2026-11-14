package com.playwithme.assistant.core.ai

import com.playwithme.assistant.BuildConfig
import com.playwithme.assistant.domain.model.AnalysisResult
import com.playwithme.assistant.domain.model.OcrResult
import com.playwithme.assistant.domain.model.SuggestionPriority
import timber.log.Timber
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Gemini API-based analyzer.
 *
 * Sends OCR text to Google Gemini for contextual game analysis.
 * Falls back to empty list if API key is not configured.
 *
 * **API Key:** Set via `GEMINI_API_KEY` in `local.properties`.
 * The key is embedded in [BuildConfig.GEMINI_API_KEY] at build time.
 *
 * NOTE: This is a placeholder implementation. Real HTTP calls would use
 * Retrofit/OkHttp with the Gemini REST API endpoint.
 */
@Singleton
class GeminiAnalyzer @Inject constructor() : GameAnalyzer {

    override val analyzerName: String = "Google Gemini Analyzer"

    private val apiKey = BuildConfig.GEMINI_API_KEY

    override suspend fun isAvailable(): Boolean = apiKey.isNotBlank()

    override suspend fun analyze(ocrResult: OcrResult): List<AnalysisResult> {
        if (!isAvailable()) {
            Timber.w("GeminiAnalyzer: API key not configured")
            return emptyList()
        }

        // TODO: Implement actual Gemini API call
        // POST https://generativelanguage.googleapis.com/v1beta/models/gemini-pro:generateContent
        // Body: { "contents": [{ "parts": [{ "text": buildPrompt(ocrResult) }] }] }
        // Parse response and map to AnalysisResult

        Timber.d("GeminiAnalyzer: analyzing ${ocrResult.rawText.take(100)}")
        return emptyList()
    }

    private fun buildPrompt(ocrResult: OcrResult): String = """
        You are a Mobile Legends game assistant. Analyze the following game screen text
        and provide a SHORT (1-2 sentence) tactical suggestion in Indonesian.
        
        GAME TEXT:
        ${ocrResult.rawText}
        
        RULES:
        - Only provide suggestions based on what you see in the text
        - Be specific and actionable
        - Maximum 2 sentences
        - Use Indonesian language
        
        SUGGESTION:
    """.trimIndent()
}

/**
 * OpenAI API-based analyzer placeholder.
 *
 * **API Key:** Set via `OPENAI_API_KEY` in `local.properties`.
 */
@Singleton
class OpenAIAnalyzer @Inject constructor() : GameAnalyzer {

    override val analyzerName: String = "OpenAI Analyzer"

    private val apiKey = BuildConfig.OPENAI_API_KEY

    override suspend fun isAvailable(): Boolean = apiKey.isNotBlank()

    override suspend fun analyze(ocrResult: OcrResult): List<AnalysisResult> {
        if (!isAvailable()) {
            Timber.w("OpenAIAnalyzer: API key not configured")
            return emptyList()
        }
        // TODO: Implement OpenAI chat completion API call
        Timber.d("OpenAIAnalyzer: placeholder — not yet implemented")
        return emptyList()
    }
}

/**
 * Offline TFLite-based analyzer (future implementation).
 * Currently falls back to returning an empty list.
 */
@Singleton
class OfflineAnalyzer @Inject constructor() : GameAnalyzer {

    override val analyzerName: String = "Offline AI (TFLite)"

    override suspend fun isAvailable(): Boolean = false // TFLite model not yet bundled

    override suspend fun analyze(ocrResult: OcrResult): List<AnalysisResult> {
        Timber.d("OfflineAnalyzer: TFLite model not yet available")
        return emptyList()
    }
}
