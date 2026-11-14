package com.playwithme.assistant.core.overlay

import android.content.Context
import android.view.WindowManager
import androidx.compose.runtime.Composable
import dagger.hilt.android.qualifiers.ApplicationContext
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Central manager for all overlay windows in the app.
 *
 * [OverlayEngine] is a singleton injected into [OverlayService].
 * It manages the lifecycle of all [OverlayWindow] instances.
 *
 * **Thread safety:** All operations should be called from the Main thread.
 *
 * Usage example:
 * ```kotlin
 * overlayEngine.addWindow(
 *     id = "assistant_panel",
 *     config = OverlayConfig(widthPx = 600, heightPx = 400, xPosition = 50, yPosition = 100),
 *     content = { AssistantPanelContent() }
 * )
 * overlayEngine.showWindow("assistant_panel")
 * overlayEngine.hideWindow("assistant_panel")
 * overlayEngine.removeAll()
 * ```
 */
@Singleton
class OverlayEngine @Inject constructor(
    @ApplicationContext private val context: Context
) {

    private val windowManager: WindowManager =
        context.getSystemService(Context.WINDOW_SERVICE) as WindowManager

    private val windows: MutableMap<String, OverlayWindow> = mutableMapOf()

    /**
     * Creates and registers an overlay window.
     *
     * If a window with the same [id] already exists, it is replaced.
     *
     * @param id Unique ID for this window.
     * @param config Window configuration (position, size, alpha).
     * @param content Composable content for this window.
     * @param autoShow If true, shows the window immediately after creation.
     */
    fun addWindow(
        id: String,
        config: OverlayConfig = OverlayConfig(),
        autoShow: Boolean = true,
        content: @Composable () -> Unit
    ) {
        // Remove existing window with same ID
        windows[id]?.hide()

        val window = OverlayWindow(
            id = id,
            context = context,
            windowManager = windowManager,
            config = config,
            content = content
        )
        windows[id] = window

        if (autoShow) window.show()
        Timber.d("OverlayEngine: window[$id] added, autoShow=$autoShow")
    }

    /** Shows an existing overlay window by ID. */
    fun showWindow(id: String) {
        windows[id]?.show() ?: Timber.w("OverlayEngine: window[$id] not found")
    }

    /** Hides (removes) an overlay window by ID. */
    fun hideWindow(id: String) {
        windows[id]?.hide() ?: Timber.w("OverlayEngine: window[$id] not found")
    }

    /** Removes and destroys an overlay window permanently. */
    fun removeWindow(id: String) {
        windows[id]?.hide()
        windows.remove(id)
        Timber.d("OverlayEngine: window[$id] removed")
    }

    /** Updates position of an overlay window. */
    fun updatePosition(id: String, x: Int, y: Int) {
        windows[id]?.updatePosition(x, y)
    }

    /** Updates size of an overlay window. */
    fun updateSize(id: String, widthPx: Int, heightPx: Int) {
        windows[id]?.updateSize(widthPx, heightPx)
    }

    /** Updates alpha (transparency) of an overlay window. */
    fun updateAlpha(id: String, alpha: Float) {
        windows[id]?.updateAlpha(alpha)
    }

    /** Updates full config of an overlay window. */
    fun updateConfig(id: String, config: OverlayConfig) {
        windows[id]?.updateConfig(config)
    }

    /** Returns true if a window with [id] exists and is visible. */
    fun isVisible(id: String): Boolean = windows[id]?.isVisible() == true

    /** Returns the config of a window. */
    fun getConfig(id: String): OverlayConfig? = windows[id]?.getConfig()

    /** Hides ALL overlay windows. */
    fun hideAll() {
        windows.values.forEach { it.hide() }
        Timber.d("OverlayEngine: all windows hidden")
    }

    /** Removes and destroys ALL overlay windows. */
    fun removeAll() {
        windows.values.forEach { it.hide() }
        windows.clear()
        Timber.d("OverlayEngine: all windows removed")
    }

    /** Returns IDs of all registered windows. */
    fun getWindowIds(): Set<String> = windows.keys.toSet()
}
