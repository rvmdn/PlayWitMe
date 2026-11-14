package com.playwithme.assistant.core.ai

import com.playwithme.assistant.core.ocr.GameTextParser
import com.playwithme.assistant.domain.model.*
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Rule-based game analyzer — fully offline, instant response.
 *
 * Uses keyword matching and predefined rules to generate suggestions.
 * This is the default analyzer — always available, no API key needed.
 *
 * Rules are organized by game event type for easy expansion.
 */
@Singleton
class RuleBasedAnalyzer @Inject constructor(
    private val gameTextParser: GameTextParser
) : GameAnalyzer {

    override val analyzerName: String = "Rule-Based Analyzer (Offline)"

    override suspend fun isAvailable(): Boolean = true

    override suspend fun analyze(ocrResult: OcrResult): List<AnalysisResult> {
        val events = gameTextParser.parse(ocrResult)
        return events.mapNotNull { event -> analyzeEvent(event) }
    }

    private fun analyzeEvent(event: GameEvent): AnalysisResult? {
        val suggestion = when (event.type) {
            GameEventType.LORD_KILLED ->
                "⚠️ Lord diambil musuh! Fokus farming untuk catch up, atau push lane agresif."

            GameEventType.TURTLE_KILLED ->
                "🐢 Turtle diambil musuh! Perhatikan level advantage mereka. Kontra dengan vision."

            GameEventType.LORD_SPAWNING ->
                "👑 Lord akan muncul! Siapkan tim di Lord area. Pastikan semua skill siap."

            GameEventType.TURTLE_SPAWNING ->
                "🐢 Turtle akan spawn! Ambil posisi lebih awal dari musuh."

            GameEventType.FIRST_BLOOD ->
                "💀 First Blood! Manfaatkan momentum — push lane atau ambil objective segera."

            GameEventType.DOUBLE_KILL ->
                "🔥 Double Kill! Musuh 2 hero mati — manfaatkan untuk push atau ambil objective."

            GameEventType.TRIPLE_KILL ->
                "🔥🔥 Triple Kill! 3 musuh mati — window besar untuk push base atau ambil Lord/Turtle."

            GameEventType.MANIAC ->
                "💥 Maniac! 5 musuh mati — ini window sempurna. Push straight ke base!"

            GameEventType.SAVAGE ->
                "🏆 SAVAGE! Semua musuh mati — push dan hancurkan base sekarang!"

            GameEventType.SURRENDER_VOTE ->
                "🤝 Vote surrender sedang berjalan. Keputusan ada di tangan tim."

            GameEventType.TOWER_DESTROYED ->
                "🏰 Menara dihancurkan! Jaga rotasi. Pertahankan sisa menara atau counter push."

            GameEventType.BUFF_TAKEN ->
                "💪 Buff diambil! Manfaatkan untuk rotasi lebih agresif."

            GameEventType.ULTIMATE_READY ->
                "⚡ Ultimate siap! Cari momentum yang tepat untuk engage."

            else -> return null // No suggestion for unknown events
        }

        return AnalysisResult(
            id = UUID.randomUUID().toString(),
            gameEvent = event,
            suggestion = suggestion,
            priority = getPriority(event.type),
            actionable = true,
            timestamp = System.currentTimeMillis()
        )
    }

    private fun getPriority(type: GameEventType): SuggestionPriority = when (type) {
        GameEventType.SAVAGE,
        GameEventType.MANIAC -> SuggestionPriority.CRITICAL

        GameEventType.LORD_KILLED,
        GameEventType.TRIPLE_KILL,
        GameEventType.FIRST_BLOOD -> SuggestionPriority.HIGH

        GameEventType.TURTLE_KILLED,
        GameEventType.LORD_SPAWNING,
        GameEventType.DOUBLE_KILL -> SuggestionPriority.MEDIUM

        else -> SuggestionPriority.LOW
    }
}
