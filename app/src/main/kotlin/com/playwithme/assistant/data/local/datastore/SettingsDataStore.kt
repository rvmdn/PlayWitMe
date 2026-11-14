package com.playwithme.assistant.data.local.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import com.playwithme.assistant.core.common.Constants
import com.playwithme.assistant.domain.model.*
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(
    name = Constants.PREFS_NAME
)

/**
 * DataStore-backed settings storage.
 *
 * All settings keys are defined here as private objects.
 * Inject this class (not the interface) for DataStore-specific operations.
 */
@Singleton
class SettingsDataStore @Inject constructor(
    @ApplicationContext private val context: Context
) {

    // ─── Preference Keys ──────────────────────────────────────────────────
    private object Keys {
        val DARK_MODE = booleanPreferencesKey("dark_mode")
        val THEME_COLOR = stringPreferencesKey("theme_color")
        val FONT_SCALE = floatPreferencesKey("font_scale")
        val OVERLAY_ALPHA = floatPreferencesKey("overlay_alpha")
        val OVERLAY_WIDTH = intPreferencesKey("overlay_width_dp")
        val OVERLAY_HEIGHT = intPreferencesKey("overlay_height_dp")
        val OVERLAY_X = intPreferencesKey("overlay_x")
        val OVERLAY_Y = intPreferencesKey("overlay_y")
        val OVERLAY_LOCKED = booleanPreferencesKey("overlay_locked")
        val OVERLAY_SNAP = booleanPreferencesKey("overlay_snap")
        val OCR_ENABLED = booleanPreferencesKey("ocr_enabled")
        val OCR_FRAME_RATE = intPreferencesKey("ocr_frame_rate")
        val OCR_REGION_ENABLED = booleanPreferencesKey("ocr_region_enabled")
        val AI_ENABLED = booleanPreferencesKey("ai_enabled")
        val AI_PROVIDER = stringPreferencesKey("ai_provider")
        val BATTERY_OPTIMIZATION = booleanPreferencesKey("battery_optimization")
        val CAPTURE_SCALE = floatPreferencesKey("capture_scale")
        val SHOW_NOTIFICATIONS = booleanPreferencesKey("show_notifications")
        val TIMER_ALERTS = booleanPreferencesKey("timer_alerts")
        val GANK_DETECTOR = booleanPreferencesKey("gank_detector_enabled")
    }

    private val defaults = AppSettings()

    /** Observe settings as a live Flow. Emits defaults on any read error. */
    val settings: Flow<AppSettings> = context.dataStore.data
        .catch { e ->
            Timber.e(e, "DataStore read error — emitting defaults")
            emit(emptyPreferences())
        }
        .map { prefs ->
            AppSettings(
                isDarkMode = prefs[Keys.DARK_MODE] ?: defaults.isDarkMode,
                themeColor = prefs[Keys.THEME_COLOR]?.let { runCatching { ThemeColor.valueOf(it) }.getOrNull() } ?: defaults.themeColor,
                fontScale = prefs[Keys.FONT_SCALE] ?: defaults.fontScale,
                overlayAlpha = prefs[Keys.OVERLAY_ALPHA] ?: defaults.overlayAlpha,
                overlayWidthDp = prefs[Keys.OVERLAY_WIDTH] ?: defaults.overlayWidthDp,
                overlayHeightDp = prefs[Keys.OVERLAY_HEIGHT] ?: defaults.overlayHeightDp,
                overlayPositionX = prefs[Keys.OVERLAY_X] ?: defaults.overlayPositionX,
                overlayPositionY = prefs[Keys.OVERLAY_Y] ?: defaults.overlayPositionY,
                isOverlayLocked = prefs[Keys.OVERLAY_LOCKED] ?: defaults.isOverlayLocked,
                overlaySnapEnabled = prefs[Keys.OVERLAY_SNAP] ?: defaults.overlaySnapEnabled,
                isOcrEnabled = prefs[Keys.OCR_ENABLED] ?: defaults.isOcrEnabled,
                ocrFrameRateFps = prefs[Keys.OCR_FRAME_RATE] ?: defaults.ocrFrameRateFps,
                ocrRegionEnabled = prefs[Keys.OCR_REGION_ENABLED] ?: defaults.ocrRegionEnabled,
                isAiEnabled = prefs[Keys.AI_ENABLED] ?: defaults.isAiEnabled,
                aiProvider = prefs[Keys.AI_PROVIDER]?.let { runCatching { AiProvider.valueOf(it) }.getOrNull() } ?: defaults.aiProvider,
                isGankDetectorEnabled = prefs[Keys.GANK_DETECTOR] ?: defaults.isGankDetectorEnabled,
                isBatteryOptimizationEnabled = prefs[Keys.BATTERY_OPTIMIZATION] ?: defaults.isBatteryOptimizationEnabled,
                captureResolutionScale = prefs[Keys.CAPTURE_SCALE] ?: defaults.captureResolutionScale,
                showEventNotifications = prefs[Keys.SHOW_NOTIFICATIONS] ?: defaults.showEventNotifications,
                timerAlertEnabled = prefs[Keys.TIMER_ALERTS] ?: defaults.timerAlertEnabled,
            )
        }

    suspend fun update(block: suspend MutablePreferences.() -> Unit) {
        context.dataStore.edit { prefs -> prefs.block() }
    }

    suspend fun setDarkMode(enabled: Boolean) = update { set(Keys.DARK_MODE, enabled) }
    suspend fun setOverlayAlpha(alpha: Float) = update { set(Keys.OVERLAY_ALPHA, alpha) }
    suspend fun setOverlaySize(w: Int, h: Int) = update { set(Keys.OVERLAY_WIDTH, w); set(Keys.OVERLAY_HEIGHT, h) }
    suspend fun setOverlayPosition(x: Int, y: Int) = update { set(Keys.OVERLAY_X, x); set(Keys.OVERLAY_Y, y) }
    suspend fun setOverlayLocked(locked: Boolean) = update { set(Keys.OVERLAY_LOCKED, locked) }
    suspend fun setOcrEnabled(enabled: Boolean) = update { set(Keys.OCR_ENABLED, enabled) }
    suspend fun setOcrFrameRate(fps: Int) = update { set(Keys.OCR_FRAME_RATE, fps) }
    suspend fun setAiEnabled(enabled: Boolean) = update { set(Keys.AI_ENABLED, enabled) }
    suspend fun setAiProvider(provider: AiProvider) = update { set(Keys.AI_PROVIDER, provider.name) }
    suspend fun setGankDetectorEnabled(enabled: Boolean) = update { set(Keys.GANK_DETECTOR, enabled) }

    suspend fun updateAll(settings: AppSettings) = update {
        set(Keys.DARK_MODE, settings.isDarkMode)
        set(Keys.THEME_COLOR, settings.themeColor.name)
        set(Keys.FONT_SCALE, settings.fontScale)
        set(Keys.OVERLAY_ALPHA, settings.overlayAlpha)
        set(Keys.OVERLAY_WIDTH, settings.overlayWidthDp)
        set(Keys.OVERLAY_HEIGHT, settings.overlayHeightDp)
        set(Keys.OVERLAY_X, settings.overlayPositionX)
        set(Keys.OVERLAY_Y, settings.overlayPositionY)
        set(Keys.OVERLAY_LOCKED, settings.isOverlayLocked)
        set(Keys.OVERLAY_SNAP, settings.overlaySnapEnabled)
        set(Keys.OCR_ENABLED, settings.isOcrEnabled)
        set(Keys.OCR_FRAME_RATE, settings.ocrFrameRateFps)
        set(Keys.OCR_REGION_ENABLED, settings.ocrRegionEnabled)
        set(Keys.AI_ENABLED, settings.isAiEnabled)
        set(Keys.AI_PROVIDER, settings.aiProvider.name)
        set(Keys.GANK_DETECTOR, settings.isGankDetectorEnabled)
        set(Keys.BATTERY_OPTIMIZATION, settings.isBatteryOptimizationEnabled)
        set(Keys.CAPTURE_SCALE, settings.captureResolutionScale)
        set(Keys.SHOW_NOTIFICATIONS, settings.showEventNotifications)
        set(Keys.TIMER_ALERTS, settings.timerAlertEnabled)
    }

    suspend fun reset() { context.dataStore.edit { it.clear() } }
}
