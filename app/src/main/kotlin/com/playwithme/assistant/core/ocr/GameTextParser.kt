package com.playwithme.assistant.core.ocr

import com.playwithme.assistant.domain.model.GameEvent
import com.playwithme.assistant.domain.model.GameEventType
import com.playwithme.assistant.domain.model.OcrResult
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Parses raw [OcrResult] text into structured [GameEvent] objects.
 *
 * Uses keyword matching against known Mobile Legends game text patterns.
 * All parsing is done locally — no network calls required.
 *
 * Add more patterns as the game text database grows.
 */
@Singleton
class GameTextParser @Inject constructor() {

    /**
     * Parses an [OcrResult] and extracts all detected [GameEvent]s.
     *
     * @param ocrResult The raw OCR output.
     * @return List of detected [GameEvent]s (may be empty if nothing recognized).
     */
    fun parse(ocrResult: OcrResult): List<GameEvent> {
        val events = mutableListOf<GameEvent>()
        val fullText = ocrResult.rawText.lowercase()
        val timestamp = ocrResult.timestamp

        // ─── Objective Events ─────────────────────────────────────────────
        if (fullText.containsAny("lord has been slain", "lord telah terbunuh", "killed lord")) {
            events += gameEvent(fullText, GameEventType.LORD_KILLED, "Lord telah dikalahkan oleh musuh!", timestamp)
        }
        if (fullText.containsAny("turtle has been slain", "turtle telah terbunuh", "killed turtle")) {
            events += gameEvent(fullText, GameEventType.TURTLE_KILLED, "Turtle telah diambil!", timestamp)
        }
        if (fullText.containsAny("lord will respawn", "lord akan muncul", "lord spawns in")) {
            events += gameEvent(fullText, GameEventType.LORD_SPAWNING, "Lord akan muncul segera!", timestamp)
        }

        // ─── Kill Events ──────────────────────────────────────────────────
        if (fullText.containsAny("first blood", "darah pertama")) {
            events += gameEvent(fullText, GameEventType.FIRST_BLOOD, "First Blood!", timestamp)
        }
        if (fullText.containsAny("double kill")) {
            events += gameEvent(fullText, GameEventType.DOUBLE_KILL, "Double Kill!", timestamp)
        }
        if (fullText.containsAny("triple kill")) {
            events += gameEvent(fullText, GameEventType.TRIPLE_KILL, "Triple Kill!", timestamp)
        }
        if (fullText.containsAny("maniac")) {
            events += gameEvent(fullText, GameEventType.MANIAC, "Maniac! 5 kill berturut-turut!", timestamp)
        }
        if (fullText.containsAny("savage")) {
            events += gameEvent(fullText, GameEventType.SAVAGE, "SAVAGE! Wipeout sempurna!", timestamp)
        }

        // ─── Surrender ────────────────────────────────────────────────────
        if (fullText.containsAny("surrender", "menyerah")) {
            events += gameEvent(fullText, GameEventType.SURRENDER_VOTE, "Vote surrender sedang berlangsung", timestamp)
        }

        // ─── Tower ────────────────────────────────────────────────────────
        if (fullText.containsAny("tower destroyed", "menara dihancurkan")) {
            events += gameEvent(fullText, GameEventType.TOWER_DESTROYED, "Menara dihancurkan!", timestamp)
        }

        // ─── Draft Phase ──────────────────────────────────────────────────
        if (fullText.containsAny("banning phase", "picking phase", "enemy picking", "your turn", "ban", "pick", "select your hero")) {
            // Ini trigger awal yang menandakan kita berada di draft screen
            events += gameEvent(fullText, GameEventType.DRAFT_STARTED, "Fase Draft Dimulai!", timestamp)
        }

        return events
    }

    private fun gameEvent(raw: String, type: GameEventType, description: String, timestamp: Long) =
        GameEvent(
            id = UUID.randomUUID().toString(),
            type = type,
            rawText = raw,
            description = description,
            timestamp = timestamp,
            confidence = 0.85f
        )

    private fun String.containsAny(vararg keywords: String): Boolean =
        keywords.any { this.contains(it, ignoreCase = true) }
}
