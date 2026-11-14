package com.playwithme.assistant.core.overlay

import android.graphics.PixelFormat
import android.view.WindowManager
import androidx.compose.runtime.Composable

/**
 * Configuration for an overlay window.
 *
 * All sizes in pixels (convert dp before passing).
 * Defaults are suitable for a compact assistant panel.
 */
data class OverlayConfig(
    val widthPx: Int = WindowManager.LayoutParams.WRAP_CONTENT,
    val heightPx: Int = WindowManager.LayoutParams.WRAP_CONTENT,
    val xPosition: Int = 0,
    val yPosition: Int = 100,
    val alpha: Float = 0.9f,
    val isDraggable: Boolean = true,
    val isResizable: Boolean = false,
    val isSnapEnabled: Boolean = true,
    val snapThresholdPx: Int = 80,
    val isLocked: Boolean = false,
    val isTouchable: Boolean = true,
    val isCollapsed: Boolean = false,
    val gravity: Int = android.view.Gravity.TOP or android.view.Gravity.START,
) {

    /** Converts this config to WindowManager.LayoutParams. */
    fun toWindowParams(): WindowManager.LayoutParams = WindowManager.LayoutParams(
        widthPx,
        heightPx,
        WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
        buildFlags(),
        PixelFormat.TRANSLUCENT
    ).also { params ->
        params.x = xPosition
        params.y = yPosition
        params.gravity = gravity
        params.alpha = alpha
    }

    private fun buildFlags(): Int {
        // ALWAYS include FLAG_NOT_FOCUSABLE so the overlay doesn't steal window focus
        // from the game. Without this, games often pause or turn black.
        var flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL or
                WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH or
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS or
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE

        if (!isTouchable || isLocked) {
            // If locked, let touches pass completely through to the game beneath
            flags = flags or WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
        }

        return flags
    }
}
