package com.playwithme.assistant.core.overlay

import android.view.MotionEvent
import android.view.WindowManager
import kotlin.math.abs

/**
 * Handles drag gesture for an overlay window.
 *
 * Attach this to a [ComposeView] via Modifier.pointerInteropFilter or
 * by passing touch events from the service's [OverlayWindow] touch handler.
 *
 * Features:
 * - Move on drag
 * - Snap to screen edges when released (if enabled)
 * - Minimum movement threshold to avoid accidental drags
 *
 * @param windowManager Used to get screen dimensions for edge snapping.
 * @param onPositionChanged Callback with new (x, y) position.
 */
class DragController(
    private val windowManager: WindowManager,
    private val snapThresholdPx: Int = 80,
    private val onPositionChanged: (x: Int, y: Int) -> Unit
) {

    private var lastX = 0
    private var lastY = 0
    private var startX = 0
    private var startY = 0
    private var initialWindowX = 0
    private var initialWindowY = 0
    private var isDragging = false

    companion object {
        private const val DRAG_THRESHOLD_PX = 10
    }

    /**
     * Process a [MotionEvent] to drive drag behavior.
     *
     * @param event The touch event.
     * @param currentX Current window X position.
     * @param currentY Current window Y position.
     * @param snapEnabled Whether to snap to screen edges on release.
     * @return True if the event was consumed (drag in progress).
     */
    fun onTouchEvent(
        event: MotionEvent,
        currentX: Int,
        currentY: Int,
        snapEnabled: Boolean = true
    ): Boolean {
        return when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                lastX = event.rawX.toInt()
                lastY = event.rawY.toInt()
                startX = lastX
                startY = lastY
                initialWindowX = currentX
                initialWindowY = currentY
                isDragging = false
                true
            }

            MotionEvent.ACTION_MOVE -> {
                val dx = event.rawX.toInt() - lastX
                val dy = event.rawY.toInt() - lastY

                val totalMoved = abs(event.rawX.toInt() - startX) + abs(event.rawY.toInt() - startY)
                if (totalMoved > DRAG_THRESHOLD_PX) isDragging = true

                if (isDragging) {
                    val newX = currentX + dx
                    val newY = currentY + dy
                    lastX = event.rawX.toInt()
                    lastY = event.rawY.toInt()
                    onPositionChanged(newX, newY)
                }
                isDragging
            }

            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                if (isDragging && snapEnabled) {
                    snapToEdge(currentX, currentY)
                }
                val wasDragging = isDragging
                isDragging = false
                wasDragging
            }

            else -> false
        }
    }

    /**
     * Snaps the window to the nearest screen edge.
     */
    private fun snapToEdge(x: Int, y: Int) {
        val screenWidth = windowManager.currentWindowMetrics.bounds.width()
        val midpoint = screenWidth / 2

        // Snap left or right based on which side of screen
        val snappedX = if (x < midpoint) 0 else screenWidth - snapThresholdPx * 2
        onPositionChanged(snappedX, y)
    }

    /** Returns true if a drag gesture is currently active. */
    fun isDragging(): Boolean = isDragging
}
