package com.playwithme.assistant.core.common

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.util.TypedValue
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import timber.log.Timber

// ─── Result Extensions ───────────────────────────────────────────────────────

/**
 * Emits [Result.Loading] first, then calls [block] and wraps in [Result.Success] or [Result.Error].
 */
fun <T> Flow<T>.asResult(): Flow<Result<T>> = map<T, Result<T>> { Result.Success(it) }
    .catch { emit(Result.Error(it as Exception)) }

// ─── Context Extensions ──────────────────────────────────────────────────────

/** Checks if overlay permission is granted on this device. */
fun Context.hasOverlayPermission(): Boolean =
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) Settings.canDrawOverlays(this)
    else true

/** Opens the overlay permission settings screen for this app. */
fun Context.openOverlayPermissionSettings() {
    val intent = Intent(
        Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
        Uri.parse("package:$packageName")
    )
    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    startActivity(intent)
}

/** Converts dp to pixels using display metrics. */
fun Context.dpToPx(dp: Float): Int =
    TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, resources.displayMetrics).toInt()

// ─── Compose Extensions ──────────────────────────────────────────────────────

/** Converts [Dp] to pixels in Compose context. */
@Composable
fun Dp.toPx(): Float {
    val density = LocalDensity.current
    return with(density) { this@toPx.toPx() }
}

// ─── String Extensions ───────────────────────────────────────────────────────

/** Returns true if the string is not null and not blank. */
fun String?.isNotNullOrBlank(): Boolean = !this.isNullOrBlank()

/** Truncates string to [maxLength] and appends "..." if needed. */
fun String.truncate(maxLength: Int): String =
    if (length <= maxLength) this else "${take(maxLength)}…"

/** Capitalizes first letter of each word. */
fun String.titleCase(): String =
    split(" ").joinToString(" ") { word ->
        word.replaceFirstChar { it.uppercaseChar() }
    }

// ─── Number Extensions ───────────────────────────────────────────────────────

/** Formats seconds into MM:SS display string. */
fun Int.toTimerDisplay(): String {
    val minutes = this / 60
    val seconds = this % 60
    return "%02d:%02d".format(minutes, seconds)
}

/** Clamps a value between [min] and [max]. */
fun Float.clamp(min: Float, max: Float): Float = this.coerceIn(min, max)

// ─── Flow Extensions ─────────────────────────────────────────────────────────

/**
 * Catches exceptions from a Flow and logs them via Timber,
 * re-emitting the error as a [Result.Error].
 */
fun <T> Flow<Result<T>>.catchAndLogError(tag: String = "Flow"): Flow<Result<T>> =
    catch { e ->
        Timber.tag(tag).e(e, "Flow error")
        emit(Result.Error(e as Exception))
    }

// ─── Collection Extensions ───────────────────────────────────────────────────

/** Returns the first element matching [predicate] or null. */
inline fun <T> List<T>.findOrNull(predicate: (T) -> Boolean): T? = find(predicate)

/** Groups a list into chunks of [size]. */
fun <T> List<T>.chunked(size: Int): List<List<T>> =
    if (size <= 0) listOf(this)
    else (indices step size).map { subList(it, minOf(it + size, this.size)) }
