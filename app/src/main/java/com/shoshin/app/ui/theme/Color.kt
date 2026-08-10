package com.Shoshin.app.ui.theme

import androidx.compose.ui.graphics.Color

// ============================================================
// Shoshin (初心) — Color Tokens
// Design direction: Japanese Modernism · Muji calm × Nike focus
// Tokens synced with Design Tokens (Light-Dark).json
// ============================================================

// ── Core Tokens (Theme Responsive) ──
val ShInkLight        = Color(0xFF1C1C1E)
val ShInkDark         = Color(0xFFF2F1EC)

val ShInk2Light      = Color(0xFF2C2C2E)
val ShInk2Dark       = Color(0xFFDAD8D2)

val ShPaperLight      = Color(0xFFFAF9F6)
val ShPaperDark       = Color(0xFF0F0F0F) // Ink background

val ShPaper2Light     = Color(0xFFF3F1EB)
val ShPaper2Dark      = Color(0xFF1A1A1A) // Darker card background

val ShSurfaceLight    = Color(0xFFFFFFFF)
val ShSurfaceDark     = Color(0xFF1C1C1C)

val ShVermillionLight = Color(0xFFC84B31)
val ShVermillionDark  = Color(0xFFE0654A)

val ShVermillion2Light = Color(0xFFB23E27)
val ShVermillion2Dark  = Color(0xFFC84B31)

val ShMatchaLightToken = Color(0xFF4A7C59)
val ShMatchaDark       = Color(0xFF6FAF80)

val ShMatcha2Light     = Color(0xFF3D6849)
val ShMatcha2Dark      = Color(0xFF4A7C59)

val ShSandLight        = Color(0xFFE8E4DC)
val ShSandDark         = Color(0xFF262523)

val ShFogLight         = Color(0xFF8A8580)
val ShFogDark          = Color(0xFF9A968E)

val ShFog2Light        = Color(0xFFB0ABA2)
val ShFog2Dark         = Color(0xFF6B6862)

val ShLineLight        = Color(0xFFE7E3DA)
val ShLineDark         = Color(0x17FFFFFF) // rgba(255,255,255,0.09)

val ShLine2Light       = Color(0xFFD8D3C8)
val ShLine2Dark        = Color(0x29FFFFFF) // rgba(255,255,255,0.16)

// ── Night / Fixed-Dark Tokens (Same both themes) ──
// Used on: Splash, Activation, Camera, 71-Day, Paywall, Wrong Answer
val ShNight            = Color(0xFF0F0F0F)
val ShNight2           = Color(0xFF1A1A1A)
val ShNight3           = Color(0xFF242424)
val ShNightText        = Color(0xFFF2F1EC)
val ShNightMuted       = Color(0xFF8C8A85)
val ShNightLine        = Color(0x1AFFFFFF) // rgba(255,255,255,0.10)
val ShAmberNight       = Color(0xFFE0A93F) // Checkpoint timer warning state (fixed-dark)

// ── Semantic & Overlays ──
val ShError            = Color(0xFFE53935)
val ShScrim            = Color(0x80000000)
val ShFabShadow        = Color(0x52C84B31) // rgba(200,75,49,0.32)

// Common Opacity
val ShMatchaLightAlpha = Color(0x1F4A7C59) // 12% opacity (Light only)

// ── Theme Mapping Helpers ──
val ShMatchaLight = ShMatchaLightAlpha

// ── Legacy Aliases (DO NOT REMOVE: Required for broad code compatibility) ──
val ShInk = ShInkLight
val ShPaper = ShPaperLight
val ShFog = ShFogLight
val ShLine = ShLineLight
val ShLine2 = ShLine2Light
val ShFog2 = ShFog2Light
val ShPaper2 = ShPaper2Light
val ShSurface = ShSurfaceLight
val ShSand = ShSandLight
val ShNightBorder = ShNightLine

// Accent colors: theme-responsive (unlike the rest of this block, which is frozen to
// the light value on purpose). Reads MaterialTheme.colorScheme, so callers must be
// in a @Composable context — matches every existing call site.
val ShMatcha: Color
    @androidx.compose.runtime.Composable
    get() = if (androidx.compose.material3.MaterialTheme.colorScheme.background == ShNight) ShMatchaDark else ShMatchaLightToken

val ShVermillion: Color
    @androidx.compose.runtime.Composable
    get() = if (androidx.compose.material3.MaterialTheme.colorScheme.background == ShNight) ShVermillionDark else ShVermillionLight

// Additional direct aliases used in older screen versions
val Sumi = ShInkLight
val Ink = ShInkLight
val Ink2 = ShInk2Light
val Washi = ShPaperLight
val Paper = ShPaperLight
val Paper2 = ShPaper2Light
val Surface = ShSurfaceLight
val Vermillion2 = ShVermillion2Light
val Fog = ShFogLight
val Fog2 = ShFog2Light
val Line = ShLineLight
val Line2 = ShLine2Light
val Sand = ShSandLight
val Night = ShNight
val NightText = ShNightText
val NightMuted = ShNightMuted
val NightLine = ShNightLine
val NightBorder = ShNightLine

val Vermillion: Color
    @androidx.compose.runtime.Composable
    get() = ShVermillion

val Matcha: Color
    @androidx.compose.runtime.Composable
    get() = ShMatcha
