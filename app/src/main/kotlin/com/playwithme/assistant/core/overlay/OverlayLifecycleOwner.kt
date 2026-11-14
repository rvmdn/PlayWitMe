package com.playwithme.assistant.core.overlay

import android.os.Bundle
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import androidx.savedstate.SavedStateRegistry
import androidx.savedstate.SavedStateRegistryController
import androidx.savedstate.SavedStateRegistryOwner

/**
 * Custom [LifecycleOwner] and [SavedStateRegistryOwner] for Compose views
 * hosted inside a [WindowManager] overlay (outside of an Activity).
 *
 * **Why this is needed:**
 * Jetpack Compose requires a [LifecycleOwner] via [ViewTreeLifecycleOwner].
 * When a [ComposeView] is added to [WindowManager], there is no Activity to
 * provide this. This class acts as a synthetic host, allowing Compose
 * recomposition, [LaunchedEffect], [remember], and [viewModel] to function
 * correctly inside overlays.
 *
 * **Usage:**
 * ```kotlin
 * val lifecycleOwner = OverlayLifecycleOwner()
 * lifecycleOwner.onCreate()
 *
 * composeView.apply {
 *     setViewTreeLifecycleOwner(lifecycleOwner)
 *     setViewTreeSavedStateRegistryOwner(lifecycleOwner)
 *     setContent { ... }
 * }
 * windowManager.addView(composeView, params)
 * lifecycleOwner.onResume()
 *
 * // When removing overlay:
 * lifecycleOwner.onDestroy()
 * windowManager.removeView(composeView)
 * ```
 */
class OverlayLifecycleOwner : LifecycleOwner, SavedStateRegistryOwner {

    private val lifecycleRegistry: LifecycleRegistry = LifecycleRegistry(this)
    private val savedStateRegistryController: SavedStateRegistryController =
        SavedStateRegistryController.create(this)

    override val lifecycle: Lifecycle get() = lifecycleRegistry
    override val savedStateRegistry: SavedStateRegistry
        get() = savedStateRegistryController.savedStateRegistry

    /**
     * Call this when the overlay is first created (before [WindowManager.addView]).
     */
    fun onCreate() {
        savedStateRegistryController.performAttach()
        savedStateRegistryController.performRestore(null)
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_CREATE)
    }

    /**
     * Call this after [WindowManager.addView] to activate Compose effects.
     */
    fun onResume() {
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_START)
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_RESUME)
    }

    /**
     * Call this when the overlay is hidden (but not removed).
     */
    fun onPause() {
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_PAUSE)
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_STOP)
    }

    /**
     * Call this BEFORE [WindowManager.removeView] to clean up Compose resources.
     */
    fun onDestroy() {
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_PAUSE)
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_STOP)
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    }

    /** Save state (call from [Service.onSaveInstanceState] if available). */
    fun performSave(outState: Bundle) {
        savedStateRegistryController.performSave(outState)
    }
}
