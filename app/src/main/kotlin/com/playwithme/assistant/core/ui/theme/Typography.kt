package com.playwithme.assistant.core.ui.theme

import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.googlefonts.Font
import androidx.compose.ui.text.googlefonts.GoogleFont
import androidx.compose.ui.unit.sp
import androidx.compose.material3.Typography
import com.playwithme.assistant.R

private val provider = GoogleFont.Provider(
    providerAuthority = "com.google.android.gms.fonts",
    providerPackage = "com.google.android.gms",
    certificates = R.array.com_google_android_gms_fonts_certs
)

private val outfitFont = GoogleFont("Outfit")
private val interFont = GoogleFont("Inter")

/** Primary display/headline font — Outfit (modern gaming feel) */
val OutfitFontFamily = FontFamily(
    Font(googleFont = outfitFont, fontProvider = provider, weight = FontWeight.Light),
    Font(googleFont = outfitFont, fontProvider = provider, weight = FontWeight.Normal),
    Font(googleFont = outfitFont, fontProvider = provider, weight = FontWeight.Medium),
    Font(googleFont = outfitFont, fontProvider = provider, weight = FontWeight.SemiBold),
    Font(googleFont = outfitFont, fontProvider = provider, weight = FontWeight.Bold),
    Font(googleFont = outfitFont, fontProvider = provider, weight = FontWeight.ExtraBold),
)

/** Secondary body font — Inter (excellent readability) */
val InterFontFamily = FontFamily(
    Font(googleFont = interFont, fontProvider = provider, weight = FontWeight.Light),
    Font(googleFont = interFont, fontProvider = provider, weight = FontWeight.Normal),
    Font(googleFont = interFont, fontProvider = provider, weight = FontWeight.Medium),
    Font(googleFont = interFont, fontProvider = provider, weight = FontWeight.SemiBold),
    Font(googleFont = interFont, fontProvider = provider, weight = FontWeight.Bold),
)

/**
 * PlayWitMe Material 3 Typography.
 *
 * Headlines use Outfit for gaming aesthetic.
 * Body text uses Inter for readability.
 */
val PlayWitMeTypography = Typography(
    // ─── Display ──────────────────────────────────────────────────────────
    displayLarge = androidx.compose.ui.text.TextStyle(
        fontFamily = OutfitFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 57.sp,
        lineHeight = 64.sp,
        letterSpacing = (-0.25).sp
    ),
    displayMedium = androidx.compose.ui.text.TextStyle(
        fontFamily = OutfitFontFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize = 45.sp,
        lineHeight = 52.sp
    ),
    displaySmall = androidx.compose.ui.text.TextStyle(
        fontFamily = OutfitFontFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize = 36.sp,
        lineHeight = 44.sp
    ),

    // ─── Headline ─────────────────────────────────────────────────────────
    headlineLarge = androidx.compose.ui.text.TextStyle(
        fontFamily = OutfitFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 32.sp,
        lineHeight = 40.sp
    ),
    headlineMedium = androidx.compose.ui.text.TextStyle(
        fontFamily = OutfitFontFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize = 28.sp,
        lineHeight = 36.sp
    ),
    headlineSmall = androidx.compose.ui.text.TextStyle(
        fontFamily = OutfitFontFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize = 24.sp,
        lineHeight = 32.sp
    ),

    // ─── Title ────────────────────────────────────────────────────────────
    titleLarge = androidx.compose.ui.text.TextStyle(
        fontFamily = OutfitFontFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize = 22.sp,
        lineHeight = 28.sp
    ),
    titleMedium = androidx.compose.ui.text.TextStyle(
        fontFamily = InterFontFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.15.sp
    ),
    titleSmall = androidx.compose.ui.text.TextStyle(
        fontFamily = InterFontFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.1.sp
    ),

    // ─── Body ─────────────────────────────────────────────────────────────
    bodyLarge = androidx.compose.ui.text.TextStyle(
        fontFamily = InterFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp
    ),
    bodyMedium = androidx.compose.ui.text.TextStyle(
        fontFamily = InterFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.25.sp
    ),
    bodySmall = androidx.compose.ui.text.TextStyle(
        fontFamily = InterFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 12.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.4.sp
    ),

    // ─── Label ────────────────────────────────────────────────────────────
    labelLarge = androidx.compose.ui.text.TextStyle(
        fontFamily = InterFontFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.1.sp
    ),
    labelMedium = androidx.compose.ui.text.TextStyle(
        fontFamily = InterFontFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 12.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.5.sp
    ),
    labelSmall = androidx.compose.ui.text.TextStyle(
        fontFamily = InterFontFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 11.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.5.sp
    ),
)
