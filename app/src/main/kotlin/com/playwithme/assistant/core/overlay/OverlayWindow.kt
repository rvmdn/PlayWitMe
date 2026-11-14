package com.playwithme.assistant.core.overlay

import android.content.Context
import android.view.WindowManager
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.ComposeView
import androidx.lifecycle.setViewTreeLifecycleOwner
import androidx.savedstate.setViewTreeSavedStateRegistryOwner
import timber.log.Timber

/**
 * Represents a single overlay window managed by [OverlayEngine].
 *
 * Each [OverlayWindow] owns:
 * - A [ComposeView] with Compose content
 * - An [OverlayLifecycleOwner] for proper lifecycle management
 * - Its own [OverlayConfig] (position, size, alpha, etc.)
 *
 * @param id Unique identifier for this overlay window.
 * @param context Android context (from Service).
 * @param windowManager The [WindowManager] to add/remove views.
 * @param config Initial overlay configuration.
 * @param content The Composable content to display.
 */
class OverlayWindow(
    val id: String,
    private val context: Context,
    private val windowManager: WindowManager,
    private var config: OverlayConfig,
    private val content: @Composable () -> Unit
) {

    private val lifecycleOwner = OverlayLifecycleOwner()
    private var composeView: ComposeView? = null
    private var isShowing = false

    /** Shows the overlay window. Safe to call multiple times (idempotent). */
    fun show() {
        if (isShowing) return

        lifecycleOwner.onCreate()

        composeView = ComposeView(context).apply {
            setViewTreeLifecycleOwner(lifecycleOwner)
            setViewTreeSavedStateRegistryOwner(lifecycleOwner)
            setContent { content() }
        }

        try {
            windowManager.addView(composeView, config.toWindowParams())
            lifecycleOwner.onResume()
            isShowing = true
            Timber.d("OverlayWindow[$id] shown")
        } catch (e: Exception) {
            Timber.e(e, "OverlayWindow[$id] failed to show")
            lifecycleOwner.onDestroy()
            composeView = null
        }
    }

    /** Hides (removes) the overlay window. Safe to call multiple times. */
    fun hide() {
        if (!isShowing) return
        lifecycleOwner.onDestroy()
        try {
            composeView?.let { windowManager.removeViewImmediate(it) }
        } catch (e: Exception) {
            Timber.e(e, "OverlayWindow[$id] failed to hide")
        } finally {
            composeView = null
            isShowing = false
            Timber.d("OverlayWindow[$id] hidden")
        }
    }

    /**
     * Updates the overlay position.
     *
     * @param x X position in pixels from left
     * @param y Y position in pixels from top
     */
    fun updatePosition(x: Int, y: Int) {
        config = config.copy(xPosition = x, yPosition = y)
        updateLayoutParams()
    }

    /**
     * Updates the overlay size.
     *
     * @param widthPx Width in pixels
     * @param heightPx Height in pixels
     */
    fun updateSize(widthPx: Int, heightPx: Int) {
        config = config.copy(widthPx = widthPx, heightPx = heightPx)
        updateLayoutParams()
    }

    /**
     * Updates overlay transparency.
     *
     * @param alpha Value between 0.0 (invisible) and 1.0 (opaque)
     */
    fun updateAlpha(alpha: Float) {
        config = config.copy(alpha = alpha.coerceIn(0f, 1f))
        updateLayoutParams()
    }

    /** Updates overlay configuration. */
    fun updateConfig(newConfig: OverlayConfig) {
        config = newConfig
        updateLayoutParams()
    }

    /** Returns current config. */
    fun getConfig(): OverlayConfig = config

    /** Returns true if the overlay is currently visible. */
    fun isVisible(): Boolean = isShowing

    private fun updateLayoutParams() {
        if (!isShowing) return
        try {
            composeView?.let { view ->
                val params = config.toWindowParams()
                windowManager.updateViewLayout(view, params)
            }
        } catch (e: Exception) {
            Timber.e(e, "OverlayWindow[$id] failed to update layout")
        }
    }
}
