package com.playwithme.assistant.domain.model

/**
 * Represents a game event detected from the screen (via OCR or timer).
 *
 * Events are READ-ONLY observations — the app does not react automatically.
 */
data class GameEvent(
    val id: String,
    val type: GameEventType,
    val rawText: String,      // Original OCR text
    val description: String,  // Human-readable description
    val timestamp: Long,      // System.currentTimeMillis()
    val confidence: Float = 1.0f // OCR/AI confidence 0.0–1.0
)

/** Types of detectable game events */
enum class GameEventType {
    // ─── Objective Events ────────────────────────────────────────────────
    LORD_KILLED,
    LORD_SPAWNING,
    TURTLE_KILLED,
    TURTLE_SPAWNING,

    // ─── Buff Events ─────────────────────────────────────────────────────
    BUFF_TAKEN,
    BUFF_EXPIRED,

    // ─── Kill Events ─────────────────────────────────────────────────────
    KILL_FEED,
    FIRST_BLOOD,
    DOUBLE_KILL,
    TRIPLE_KILL,
    MANIAC,
    SAVAGE,

    // ─── Skill Events ─────────────────────────────────────────────────────
    ULTIMATE_READY,
    SKILL_READY,

    // ─── Match Events ─────────────────────────────────────────────────────
    MATCH_START,
    MATCH_END,
    SURRENDER_VOTE,

    // ─── Tower Events ────────────────────────────────────────────────────
    TOWER_DESTROYED,
    INHIBITOR_DESTROYED,

    // ─── Generic ─────────────────────────────────────────────────────────
    UNKNOWN,

    // ─── Draft Phase Events ──────────────────────────────────────────────
    DRAFT_STARTED,
    HERO_BANNED,
    HERO_PICKED
}

/**
 * Timer event for tracking objective respawn timers.
 */
data class TimerEvent(
    val id: String,
    val type: TimerType,
    val totalSeconds: Int,
    val remainingSeconds: Int,
    val startedAt: Long,
    val isManual: Boolean = true // false if auto-detected via OCR
) {
    val isExpired: Boolean get() = remainingSeconds <= 0
    val progressFraction: Float get() = if (totalSeconds == 0) 0f
        else (remainingSeconds.toFloat() / totalSeconds.toFloat()).coerceIn(0f, 1f)
}

enum class TimerType {
    LORD,
    TURTLE,
    BUFF_ALLY,
    BUFF_ENEMY,
    CUSTOM
}

/**
 * Result of an OCR processing pass.
 */
data class OcrResult(
    val id: String,
    val rawText: String,
    val blocks: List<OcrTextBlock>,
    val timestamp: Long,
    val processingTimeMs: Long
)

data class OcrTextBlock(
    val text: String,
    val boundingBox: OcrBoundingBox,
    val confidence: Float
)

data class OcrBoundingBox(
    val left: Int,
    val top: Int,
    val right: Int,
    val bottom: Int
)

/**
 * AI-analyzed result from game state.
 */
data class AnalysisResult(
    val id: String,
    val gameEvent: GameEvent,
    val suggestion: String,     // Human-readable suggestion for the player
    val priority: SuggestionPriority,
    val actionable: Boolean,    // Should this be displayed prominently?
    val timestamp: Long
)

enum class SuggestionPriority {
    LOW,
    MEDIUM,
    HIGH,
    CRITICAL
}
