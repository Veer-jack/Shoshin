package com.example.shoshinapp.ui.theme

import androidx.compose.ui.graphics.Color

// ============================================================
// Shoshin (初心) — Color Tokens
// Design direction: Japanese Modernism · Muji calm × Nike focus
// ============================================================

// ── Core ──
val ShInk         = Color(0xFF1C1C1E)
val ShInk2        = Color(0xFF2C2C2E)
val ShPaper       = Color(0xFFFAF9F6)
val ShPaper2      = Color(0xFFF3F1EB)
val ShSurface     = Color(0xFFFFFFFF)

// ── Accent (one per screen — never two) ──
val ShVermillion  = Color(0xFFC84B31)
val ShVermillion2 = Color(0xFFB23E27)

// ── Success / completion ──
val ShMatcha      = Color(0xFF4A7C59)
val ShMatchaLight = Color(0x1F4A7C59) // 12% opacity

// ── Neutrals ──
val ShSand        = Color(0xFFE8E4DC)
val ShFog         = Color(0xFF8A8580)
val ShFog2        = Color(0xFFB0ABA2)
val ShLine        = Color(0xFFE7E3DA)
val ShLine2       = Color(0xFFD8D3C8)

// ── Night / dark screens ──
// Used on: Splash, Activation, Camera, 71-Day, Paywall, Wrong Answer
val ShNight       = Color(0xFF0F0F0F)
val ShNight2      = Color(0xFF1A1A1A)
val ShNight3      = Color(0xFF242424)
val ShNightText   = Color(0xFFF2F1EC)
val ShNightMuted  = Color(0xFF8C8A85)

// ── Semantic ──
val ShError       = Color(0xFFE53935)
val ShScrim       = Color(0x80000000)

// ── Surface overlays ──
val ShNightBorder = Color(0x1AFFFFFF) // rgba(255,255,255,0.10)
val ShFabShadow   = Color(0x52C84B31) // rgba(200,75,49,0.32)

// Legacy compatibility aliases
val Sumi = ShInk
val Ink2 = ShInk2
val Washi = ShPaper
val Paper2 = ShPaper2
val Surface = ShSurface
val Vermillion = ShVermillion
val Vermillion2 = ShVermillion2
val Matcha = ShMatcha
val Ink = ShInk
val Paper = ShPaper
val Night = ShNight
val Fog = ShFog
val Sand = ShSand
val Line = ShLine
val Line2 = ShLine2
val Fog2 = ShFog2
val NightText = ShNightText
val NightMuted = ShNightMuted
val NightLine = ShNightBorder
val NightBorder = ShNightBorder
