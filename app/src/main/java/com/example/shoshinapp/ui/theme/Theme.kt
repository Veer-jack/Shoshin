package com.example.shoshinapp.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
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

// Shoshin Dark Color Scheme
private val ShDarkColorScheme = darkColorScheme(
    primary          = ShVermillion,
    onPrimary        = Color.White,
    primaryContainer = ShVermillion2,
    secondary        = ShMatcha,
    onSecondary      = Color.White,
    background       = Color(0xFF121212), // Deep dark for system dark mode
    onBackground     = Color(0xFFF2F1EC),
    surface          = Color(0xFF1E1E1E),
    onSurface        = Color(0xFFF2F1EC),
    surfaceVariant   = Color(0xFF2C2C2E),
    onSurfaceVariant = Color(0xFFA2A2A7),
    outline          = Color(0xFF3A3A3C),
    outlineVariant   = Color(0xFF48484A),
    error            = ShError,
)

// Dark used only for specific screens (e.g., Morning Activation)
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
    darkTheme: Boolean = isSystemInDarkTheme(),
    darkSurface: Boolean = false, // pass true only for specific full-dark screens
    content: @Composable () -> Unit,
) {
    val colorScheme = when {
        darkSurface -> ShNightColorScheme
        darkTheme -> ShDarkColorScheme
        else -> ShLightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
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
