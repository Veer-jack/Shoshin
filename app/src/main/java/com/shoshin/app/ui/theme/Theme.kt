package com.Shoshin.app.ui.theme

import android.app.Application
import android.content.Context
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

// ============================================================
// Shoshin (初心) — MaterialTheme wrapper
// App-wide appearance: Light / Dark / System, chosen from
// Settings → Appearance and persisted via ThemePreference.
// `darkSurface` remains available to force specific celebration
// screens (Badge Unlock, Activation, Camera, 71-Day) into the
// night palette regardless of the app-wide setting.
// ============================================================

enum class ShThemeMode { LIGHT, DARK, SYSTEM }

private const val PREFS_NAME = "shoshin_prefs"
private const val KEY_THEME_MODE = "theme_mode"

class ThemePreference private constructor(context: Context) {
    private val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    private val _mode = MutableStateFlow(
        ShThemeMode.valueOf(prefs.getString(KEY_THEME_MODE, ShThemeMode.SYSTEM.name) ?: ShThemeMode.SYSTEM.name)
    )
    val mode: StateFlow<ShThemeMode> = _mode

    fun setMode(mode: ShThemeMode) {
        _mode.value = mode
        prefs.edit().putString(KEY_THEME_MODE, mode.name).apply()
    }

    companion object {
        @Volatile
        private var INSTANCE: ThemePreference? = null

        fun getInstance(context: Context): ThemePreference {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: ThemePreference(context).also { INSTANCE = it }
            }
        }
    }
}

class ThemeViewModel(app: Application) : AndroidViewModel(app) {
    private val pref = ThemePreference.getInstance(app)
    val mode: StateFlow<ShThemeMode> = pref.mode
    fun setMode(mode: ShThemeMode) = pref.setMode(mode)
}

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

private val ShDarkColorScheme = darkColorScheme(
    primary          = ShVermillionDark,
    onPrimary        = Color.White,
    primaryContainer = ShVermillion2,
    secondary        = ShMatchaDark,
    onSecondary      = Color.White,
    background       = ShPaperDark,
    onBackground     = ShInkDark,
    surface          = ShSurfaceDark,
    onSurface        = ShInkDark,
    surfaceVariant   = ShPaper2Dark,
    onSurfaceVariant = ShFogDark,
    outline          = ShLineDark,
    outlineVariant   = ShLine2Dark,
    error            = ShError,
    scrim            = ShScrim,
)

// "Night" palette specifically for high-intensity or late-night focus screens
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
    darkSurface: Boolean = false, // force night palette for a specific celebration screen
    content: @Composable () -> Unit,
) {
    val themeViewModel: ThemeViewModel = viewModel()
    val mode by themeViewModel.mode.collectAsState()
    val systemIsDark = isSystemInDarkTheme()
    
    val appIsDark = when (mode) {
        ShThemeMode.DARK -> true
        ShThemeMode.LIGHT -> false
        ShThemeMode.SYSTEM -> systemIsDark
    }

    val colorScheme = when {
        darkSurface -> ShNightColorScheme
        appIsDark -> ShDarkColorScheme
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
