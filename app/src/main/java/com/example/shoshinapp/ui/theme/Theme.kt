package com.example.shoshinapp.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// Shoshin (初心) — MaterialTheme wrapper

private val ShLightColorScheme = lightColorScheme(
    primary          = ShVermillion,
    onPrimary        = Color.White,
    primaryContainer = ShVermillion2,
    secondary        = ShMatcha,
    onSecondary      = Color.White,
    background       = ShPaper,
    onBackground     = ShInk,
    surface          = ShSurface,
    onSurface        = ShInk,
    surfaceVariant   = ShPaper2,
    onSurfaceVariant = ShFog,
    outline          = ShLine,
    outlineVariant   = ShLine2,
    error            = ShError,
    scrim            = ShScrim,
)

// Dark used only for specific screens — not a true dark mode theme
private val ShNightColorScheme = darkColorScheme(
    primary          = ShVermillion,
    onPrimary        = Color.White,
    background       = ShNight,
    onBackground     = ShNightText,
    surface          = ShNight2,
    onSurface        = ShNightText,
    surfaceVariant   = ShNight3,
    onSurfaceVariant = ShNightMuted,
    outline          = ShNightBorder,
    error            = ShError,
)

@Composable
fun ShoshinTheme(
    darkSurface: Boolean = false, // pass true only for night screens
    content: @Composable () -> Unit,
) {
    MaterialTheme(
        colorScheme = if (darkSurface) ShNightColorScheme else ShLightColorScheme,
        typography  = ShTypography,
        shapes      = ShShapes,
        content     = content,
    )
}

// Compatibility alias for the old theme name if used in MainActivity or elsewhere
@Composable
fun ShoshinAPPTheme(
    content: @Composable () -> Unit
) {
    ShoshinTheme(content = content)
}
