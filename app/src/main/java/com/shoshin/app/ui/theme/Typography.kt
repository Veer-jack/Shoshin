package com.Shoshin.app.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.googlefonts.Font
import androidx.compose.ui.text.googlefonts.GoogleFont
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.sp
import com.Shoshin.app.R

// ============================================================
// Shoshin (初心) — Typography Design System
// Based on "Japanese Modernism" spec.
// Fonts: Cormorant Garamond, DM Sans, Noto Serif JP
// ============================================================

val provider = GoogleFont.Provider(
    providerAuthority = "com.google.android.gms.fonts",
    providerPackage = "com.google.android.gms",
    certificates = R.array.com_google_android_gms_fonts_certs
)

val CormorantFamily = FontFamily(
    Font(googleFont = GoogleFont("Cormorant Garamond"), fontProvider = provider, weight = FontWeight.Medium), // 500
    Font(googleFont = GoogleFont("Cormorant Garamond"), fontProvider = provider, weight = FontWeight.SemiBold), // 600
    Font(googleFont = GoogleFont("Cormorant Garamond"), fontProvider = provider, weight = FontWeight.Bold)      // 700
)

val DmSansFamily = FontFamily(
    Font(googleFont = GoogleFont("DM Sans"), fontProvider = provider, weight = FontWeight.Normal),  // 400
    Font(googleFont = GoogleFont("DM Sans"), fontProvider = provider, weight = FontWeight.Medium),  // 500
    Font(googleFont = GoogleFont("DM Sans"), fontProvider = provider, weight = FontWeight.SemiBold), // 600
    Font(googleFont = GoogleFont("DM Sans"), fontProvider = provider, weight = FontWeight.Bold)      // 700
)

val NotoSerifJpFamily = FontFamily(
    Font(googleFont = GoogleFont("Noto Serif JP"), fontProvider = provider, weight = FontWeight.Medium), // 500
    Font(googleFont = GoogleFont("Noto Serif JP"), fontProvider = provider, weight = FontWeight.SemiBold) // 600
)

// Primary aliases for direct usage in screens
val CormorantGaramond = CormorantFamily
val DMSans = DmSansFamily
val NotoSerifJP = NotoSerifJpFamily

// ── Shoshin custom text styles ──────────────────────────────

/** .sh-display: 40–64 sp · Cormorant Garamond 600 · Clock / large numerals */
val ShDisplayStyle = TextStyle(
    fontFamily   = CormorantFamily,
    fontWeight   = FontWeight.SemiBold,
    fontSize     = 52.sp,
    lineHeight   = 52.sp,
    letterSpacing = (-0.52).sp,
)

/** .sh-title: 32 sp · Cormorant Garamond 600 · Screen titles */
val ShTitleStyle = TextStyle(
    fontFamily   = CormorantFamily,
    fontWeight   = FontWeight.SemiBold,
    fontSize     = 32.sp,
    lineHeight   = 35.8.sp, // 1.12 * 32
    letterSpacing = (-0.32).sp, // -0.01em
)

/** .sh-h2: 17 sp · DM Sans 600 · Card titles, subsection headers */
val ShH2Style = TextStyle(
    fontFamily   = DmSansFamily,
    fontWeight   = FontWeight.SemiBold,
    fontSize     = 17.sp,
    letterSpacing = (-0.17).sp, // -0.01em
)

/** .sh-kicker: 11 sp · DM Sans 500 · UPPERCASE · letter-spacing 0.22em */
val ShKickerStyle = TextStyle(
    fontFamily    = DmSansFamily,
    fontWeight    = FontWeight.Medium,
    fontSize      = 11.sp,
    letterSpacing = 2.42.sp, // 0.22em * 11
    textDecoration = TextDecoration.None // Apply .uppercase() in UI
)

/** .sh-body: 15 sp · DM Sans 400 · Body paragraphs */
val ShBodyStyle = TextStyle(
    fontFamily   = DmSansFamily,
    fontWeight   = FontWeight.Normal,
    fontSize     = 15.sp,
    lineHeight   = 24.sp, // 1.6 * 15
    color        = ShFog,
)

/** .sh-label: 13 sp · DM Sans 500 · Secondary labels, metadata */
val ShLabelStyle = TextStyle(
    fontFamily   = DmSansFamily,
    fontWeight   = FontWeight.Medium,
    fontSize     = 13.sp,
    color        = ShFog,
)

/** .sh-num: DM Sans 700 tabular · Streaks, clocks, stats */
val ShNumeralStyle = TextStyle(
    fontFamily        = DmSansFamily,
    fontWeight        = FontWeight.Bold,
    fontSize          = 36.sp,
    letterSpacing     = (-0.72).sp, // -0.02em * 36
    // Note: Use FontVariantNumeric.TabularNums in the Composable where used
)

/** Button: 16 sp · DM Sans 600 · Button labels */
val ShButtonStyle = TextStyle(
    fontFamily   = DmSansFamily,
    fontWeight   = FontWeight.SemiBold,
    fontSize     = 16.sp,
    letterSpacing = 0.16.sp, // 0.01em
)

/** Kanji (JP): Noto Serif JP 500 · Japanese accents */
val ShKanjiStyle = TextStyle(
    fontFamily   = NotoSerifJpFamily,
    fontWeight   = FontWeight.Medium,
    fontSize     = 22.sp,
    letterSpacing = 6.6.sp, // 0.3em
    color        = ShVermillion,
)

// ── Material 3 Typography mapping ───────────────────────────
// This ensures that default Material components also use Shoshin fonts
val ShTypography = Typography(
    displayLarge   = ShDisplayStyle,
    displayMedium  = ShDisplayStyle.copy(fontSize = 40.sp, lineHeight = 40.sp),
    displaySmall   = ShDisplayStyle.copy(fontSize = 32.sp, lineHeight = 32.sp),
    
    headlineLarge  = ShTitleStyle,
    headlineMedium = ShTitleStyle.copy(fontSize = 26.sp, lineHeight = 29.sp),
    headlineSmall  = ShTitleStyle.copy(fontSize = 20.sp, lineHeight = 22.sp),
    
    titleLarge     = ShH2Style,
    titleMedium    = ShH2Style.copy(fontSize = 15.sp),
    titleSmall     = ShH2Style.copy(fontSize = 13.sp),
    
    bodyLarge      = ShBodyStyle,
    bodyMedium     = ShBodyStyle.copy(fontSize = 13.sp, lineHeight = 20.sp),
    bodySmall      = ShBodyStyle.copy(fontSize = 11.sp, lineHeight = 16.sp),

    labelLarge     = ShButtonStyle,
    labelMedium    = ShLabelStyle,
    labelSmall     = ShKickerStyle,
)
