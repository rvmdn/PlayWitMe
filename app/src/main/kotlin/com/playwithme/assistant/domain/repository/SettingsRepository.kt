package com.playwithme.assistant.domain.repository

import com.playwithme.assistant.domain.model.AppSettings
import com.playwithme.assistant.domain.model.AiProvider
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for application settings.
 * Backed by DataStore Preferences.
 */
interface SettingsRepository {
    /** Observe settings as a live Flow. */
    fun getSettings(): Flow<AppSettings>

    /** Update all settings at once. */
    suspend fun updateSettings(settings: AppSettings)

    /** Update dark mode preference. */
    suspend fun setDarkMode(enabled: Boolean)

    /** Update overlay alpha (transparency). */
    suspend fun setOverlayAlpha(alpha: Float)

    /** Update overlay width in dp. */
    suspend fun setOverlaySize(widthDp: Int, heightDp: Int)

    /** Update overlay position. */
    suspend fun setOverlayPosition(x: Int, y: Int)

    /** Toggle overlay lock. */
    suspend fun setOverlayLocked(locked: Boolean)

    /** Toggle OCR enabled. */
    suspend fun setOcrEnabled(enabled: Boolean)

    /** Set OCR frame rate. */
    suspend fun setOcrFrameRate(fps: Int)

    /** Toggle AI analysis. */
    suspend fun setAiEnabled(enabled: Boolean)

    /** Set AI provider. */
    suspend fun setAiProvider(provider: AiProvider)

    /** Toggle Gank Detector. */
    suspend fun setGankDetectorEnabled(enabled: Boolean)

    /** Reset all settings to defaults. */
    suspend fun resetToDefaults()
}
