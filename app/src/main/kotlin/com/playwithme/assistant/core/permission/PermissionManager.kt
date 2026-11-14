package com.playwithme.assistant.core.permission

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.core.content.ContextCompat
import dagger.hilt.android.qualifiers.ApplicationContext
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Central manager for all runtime permissions in PlayWitMe.
 *
 * **Overlay permission** (`SYSTEM_ALERT_WINDOW`) is special — it must be
 * granted via Settings, not a system dialog. Use [hasOverlayPermission] and
 * [openOverlayPermissionSettings].
 *
 * **Standard permissions** (NOTIFICATIONS) are handled via ActivityResultLauncher
 * in [MainActivity]. This class provides query utilities only.
 */
@Singleton
class PermissionManager @Inject constructor(
    @ApplicationContext private val context: Context
) {

    // ─── Overlay Permission ───────────────────────────────────────────────

    /** Returns true if the overlay (draw over other apps) permission is granted. */
    fun hasOverlayPermission(): Boolean =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Settings.canDrawOverlays(context)
        } else {
            true // Always granted below M
        }

    /** Opens the system overlay permission settings for this app. */
    fun openOverlayPermissionSettings(context: Context) {
        val intent = Intent(
            Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
            Uri.parse("package:${context.packageName}")
        ).apply { addFlags(Intent.FLAG_ACTIVITY_NEW_TASK) }
        context.startActivity(intent)
        Timber.d("Opened overlay permission settings")
    }

    // ─── Notification Permission (Android 13+) ────────────────────────────

    /** Returns true if notification permission is granted (or not needed). */
    fun hasNotificationPermission(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ContextCompat.checkSelfPermission(
                context,
                android.Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        } else {
            true
        }
    }

    // ─── Permission State ─────────────────────────────────────────────────

    /**
     * Returns the overall permission state for starting the overlay service.
     *
     * All required permissions must be granted for the service to start.
     */
    fun getPermissionState(): PermissionState = PermissionState(
        hasOverlayPermission = hasOverlayPermission(),
        hasNotificationPermission = hasNotificationPermission()
    )

    /** Returns true if ALL required permissions are granted. */
    fun areAllPermissionsGranted(): Boolean {
        val state = getPermissionState()
        return state.hasOverlayPermission && state.hasNotificationPermission
    }
}

/**
 * Snapshot of the current permission state.
 */
data class PermissionState(
    val hasOverlayPermission: Boolean,
    val hasNotificationPermission: Boolean
) {
    val isComplete: Boolean get() = hasOverlayPermission && hasNotificationPermission

    val missingPermissions: List<String> = buildList {
        if (!hasOverlayPermission) add("Draw over other apps")
        if (!hasNotificationPermission) add("Notifications")
    }
}
