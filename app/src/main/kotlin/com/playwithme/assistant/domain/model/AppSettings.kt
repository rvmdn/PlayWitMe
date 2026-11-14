package com.playwithme.assistant.domain.model

/**
 * A floating note created by the player for in-game reminders.
 *
 * Examples:
 * - "Use Purify against Franco"
 * - "Buy Immortality when losing"
 */
data class Note(
    val id: String,
    val content: String,
    val title: String = "",
    val color: NoteColor = NoteColor.DEFAULT,
    val isPinned: Boolean = false,
    val createdAt: Long,
    val updatedAt: Long
)

enum class NoteColor {
    DEFAULT,
    RED,
    YELLOW,
    GREEN,
    BLUE,
    PURPLE
}

/**
 * Application-wide settings persisted via DataStore.
 */
data class AppSettings(
    // ─── Appearance ───────────────────────────────────────────────────────
    val isDarkMode: Boolean = true,
    val themeColor: ThemeColor = ThemeColor.VIOLET,
    val fontScale: Float = 1.0f,

    // ─── Overlay ──────────────────────────────────────────────────────────
    val overlayAlpha: Float = 0.9f,
    val overlayWidthDp: Int = 300,
    val overlayHeightDp: Int = 200,
    val overlayPositionX: Int = 0,
    val overlayPositionY: Int = 100,
    val isOverlayLocked: Boolean = false,
    val overlaySnapEnabled: Boolean = true,

    // ─── OCR ──────────────────────────────────────────────────────────────
    val isOcrEnabled: Boolean = true,
    val ocrFrameRateFps: Int = 5, // Adjustable 1–10
    val ocrRegionEnabled: Boolean = false, // ROI mode

    // ─── AI & Vision ──────────────────────────────────────────────────────
    val isAiEnabled: Boolean = true,
    val aiProvider: AiProvider = AiProvider.RULE_BASED,
    val isGankDetectorEnabled: Boolean = false, // OpenCV Minimap Feature

    // ─── Performance ──────────────────────────────────────────────────────
    val isBatteryOptimizationEnabled: Boolean = true,
    val captureResolutionScale: Float = 0.5f, // 0.25–1.0

    // ─── Notifications ────────────────────────────────────────────────────
    val showEventNotifications: Boolean = true,
    val timerAlertEnabled: Boolean = true
)

enum class ThemeColor {
    VIOLET, // Default
    BLUE,
    GREEN,
    ORANGE,
    RED
}

enum class AiProvider {
    RULE_BASED,  // Default, offline
    GEMINI,      // Google Gemini API
    OPENAI,      // OpenAI API
    OFFLINE_TF   // TensorFlow Lite local model
}
