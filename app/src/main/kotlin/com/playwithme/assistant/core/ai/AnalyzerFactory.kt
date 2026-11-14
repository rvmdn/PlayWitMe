package com.playwithme.assistant.core.ai

import com.playwithme.assistant.domain.model.AiProvider
import com.playwithme.assistant.domain.model.AppSettings
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Factory that selects the appropriate [GameAnalyzer] based on [AppSettings].
 *
 * Injected where AI analysis is needed (ViewModels, Services).
 * Uses DI-provided singletons — no new instances created at runtime.
 *
 * Usage:
 * ```kotlin
 * val analyzer = analyzerFactory.getAnalyzer(settings.aiProvider)
 * val results = analyzer.analyze(ocrResult)
 * ```
 */
@Singleton
class AnalyzerFactory @Inject constructor(
    private val ruleBasedAnalyzer: RuleBasedAnalyzer,
    private val geminiAnalyzer: GeminiAnalyzer,
    private val openAIAnalyzer: OpenAIAnalyzer,
    private val offlineAnalyzer: OfflineAnalyzer
) {

    /**
     * Returns the [GameAnalyzer] for the given [AiProvider].
     *
     * If the requested provider is not available, falls back to [RuleBasedAnalyzer].
     */
    suspend fun getAnalyzer(provider: AiProvider): GameAnalyzer {
        val analyzer = when (provider) {
            AiProvider.RULE_BASED -> ruleBasedAnalyzer
            AiProvider.GEMINI -> geminiAnalyzer
            AiProvider.OPENAI -> openAIAnalyzer
            AiProvider.OFFLINE_TF -> offlineAnalyzer
        }

        return if (analyzer.isAvailable()) {
            Timber.d("AnalyzerFactory: using ${analyzer.analyzerName}")
            analyzer
        } else {
            Timber.w("AnalyzerFactory: ${analyzer.analyzerName} unavailable, falling back to RuleBasedAnalyzer")
            ruleBasedAnalyzer
        }
    }

    /** Returns the always-available fallback analyzer. */
    fun getFallbackAnalyzer(): GameAnalyzer = ruleBasedAnalyzer
}
