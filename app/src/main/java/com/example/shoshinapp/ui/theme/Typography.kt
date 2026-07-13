package com.example.shoshinapp.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.shoshinapp.R

// Shoshin (初心) — Typography
// Fonts: Cormorant Garamond (display) · DM Sans (UI) · Noto Serif JP (kanji)

// Use Default as fallback since font files are missing in handoff
val CormorantFamily = FontFamily.Default
val DmSansFamily = FontFamily.Default
val NotoSerifJpFamily = FontFamily.Default

val CormorantGaramond = CormorantFamily
val DMSans = DmSansFamily
val NotoSerifJP = NotoSerifJpFamily

// ── Shoshin custom text styles ──────────────────────────────

/** 40–64 sp · Cormorant Garamond 600 · Clock / hero numerals */
val ShDisplayStyle = TextStyle(
    fontFamily   = CormorantFamily,
    fontWeight   = FontWeight.SemiBold,
    fontSize     = 52.sp,
    lineHeight   = 52.sp,
    letterSpacing = (-0.52).sp,
)

/** 26–34 sp · Cormorant Garamond 600 · Screen titles */
val ShTitleStyle = TextStyle(
    fontFamily   = CormorantFamily,
    fontWeight   = FontWeight.SemiBold,
    fontSize     = 30.sp,
    lineHeight   = 33.6.sp,
    letterSpacing = (-0.3).sp,
)

/** 17 sp · DM Sans 600 · Card titles, subsection headers */
val ShH2Style = TextStyle(
    fontFamily   = DmSansFamily,
    fontWeight   = FontWeight.SemiBold,
    fontSize     = 17.sp,
    lineHeight   = 22.sp,
    letterSpacing = (-0.17).sp,
)

/** 11 sp · DM Sans 500 · UPPERCASE · letter-spacing 0.22em */
val ShKickerStyle = TextStyle(
    fontFamily    = DmSansFamily,
    fontWeight    = FontWeight.Medium,
    fontSize      = 11.sp,
    lineHeight    = 14.sp,
    letterSpacing = 2.42.sp,
)

/** 15 sp · DM Sans 400 · Body paragraphs */
val ShBodyStyle = TextStyle(
    fontFamily   = DmSansFamily,
    fontWeight   = FontWeight.Normal,
    fontSize     = 15.sp,
    lineHeight   = 24.sp,
    color        = ShFog,
)

/** 13 sp · DM Sans 500 · Secondary labels, metadata */
val ShLabelStyle = TextStyle(
    fontFamily   = DmSansFamily,
    fontWeight   = FontWeight.Medium,
    fontSize     = 13.sp,
    lineHeight   = 18.sp,
    color        = ShFog,
)

/** DM Sans 700 tabular · Streaks, clocks, stats */
val ShNumeralStyle = TextStyle(
    fontFamily        = DmSansFamily,
    fontWeight        = FontWeight.Bold,
    fontSize          = 36.sp,
    letterSpacing     = (-0.72).sp,
)

/** 16 sp · DM Sans 600 · Button labels */
val ShButtonStyle = TextStyle(
    fontFamily   = DmSansFamily,
    fontWeight   = FontWeight.SemiBold,
    fontSize     = 16.sp,
    lineHeight   = 20.sp,
    letterSpacing = 0.sp,
)

/** Noto Serif JP 500 · Kanji accent on brand surfaces */
val ShKanjiStyle = TextStyle(
    fontFamily   = NotoSerifJpFamily,
    fontWeight   = FontWeight.Medium,
    fontSize     = 22.sp,
    letterSpacing = 6.6.sp, // ~0.3em at 22sp
    color        = ShVermillion,
)

// ── Material 3 Typography mapping ───────────────────────────
val ShTypography = Typography(
    displayLarge  = ShDisplayStyle,
    displayMedium = ShDisplayStyle.copy(fontSize = 40.sp),
    headlineLarge = ShTitleStyle,
    headlineMedium = ShTitleStyle.copy(fontSize = 26.sp),
    titleLarge    = ShH2Style,
    titleMedium   = ShH2Style.copy(fontSize = 15.sp),
    bodyLarge     = ShBodyStyle,
    bodyMedium    = ShBodyStyle.copy(fontSize = 13.sp),
    labelLarge    = ShButtonStyle,
    labelMedium   = ShLabelStyle,
    labelSmall    = ShKickerStyle,
)
