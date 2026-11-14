package com.playwithme.assistant.core.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

// ─── Dark Color Scheme (Primary theme for gaming app) ─────────────────────
private val DarkColorScheme = darkColorScheme(
    primary = Primary60,
    onPrimary = Primary10,
    primaryContainer = Primary30,
    onPrimaryContainer = Primary90,

    secondary = Secondary60,
    onSecondary = Secondary10,
    secondaryContainer = Secondary30,
    onSecondaryContainer = Secondary90,

    tertiary = Tertiary70,
    onTertiary = Tertiary10,
    tertiaryContainer = Tertiary30,
    onTertiaryContainer = Tertiary90,

    error = Error80,
    onError = Error10,
    errorContainer = Error40,
    onErrorContainer = Error90,

    background = Neutral10,
    onBackground = Neutral90,

    surface = Neutral10,
    onSurface = Neutral90,
    surfaceVariant = NeutralVariant20,
    onSurfaceVariant = NeutralVariant70,

    outline = NeutralVariant50,
    outlineVariant = NeutralVariant30,

    inverseSurface = Neutral90,
    inverseOnSurface = Neutral20,
    inversePrimary = Primary40,

    surfaceTint = Primary60,
    scrim = Neutral10,
)

// ─── Light Color Scheme ───────────────────────────────────────────────────
private val LightColorScheme = lightColorScheme(
    primary = Primary40,
    onPrimary = Primary99,
    primaryContainer = Primary90,
    onPrimaryContainer = Primary10,

    secondary = Secondary40,
    onSecondary = Secondary99,
    secondaryContainer = Secondary90,
    onSecondaryContainer = Secondary10,

    tertiary = Tertiary40,
    onTertiary = Tertiary95,
    tertiaryContainer = Tertiary90,
    onTertiaryContainer = Tertiary10,

    error = Error40,
    onError = Error90,
    errorContainer = Error90,
    onErrorContainer = Error10,

    background = Neutral99,
    onBackground = Neutral10,

    surface = Neutral99,
    onSurface = Neutral10,
    surfaceVariant = NeutralVariant90,
    onSurfaceVariant = NeutralVariant40,

    outline = NeutralVariant50,
    outlineVariant = NeutralVariant80,

    inverseSurface = Neutral20,
    inverseOnSurface = Neutral95,
    inversePrimary = Primary80,
)

/**
 * PlayWitMe Material 3 Theme.
 *
 * Supports dark mode (default), light mode, and Android 12+ dynamic colors.
 * The app defaults to dark mode to suit gaming use cases.
 *
 * @param darkTheme Whether to use dark theme. Defaults to system setting.
 * @param dynamicColor Whether to use Material You dynamic colors (Android 12+).
 * @param content The composable content.
 */
@Composable
fun PlayWitMeTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false, // Disabled by default — use our curated palette
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            var context = view.context
            while (context is android.content.ContextWrapper) {
                if (context is Activity) break
                context = context.baseContext
            }
            val window = (context as? Activity)?.window
            if (window != null) {
                WindowCompat.getInsetsController(window, view).apply {
                    isAppearanceLightStatusBars = !darkTheme
                    isAppearanceLightNavigationBars = !darkTheme
                }
            }
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = PlayWitMeTypography,
        content = content
    )
}
