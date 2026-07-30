package com.Shoshin.app.ui.theme

import android.app.Application
import android.content.Context
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

// ============================================================
// Shoshin (初心) — MaterialTheme wrapper
// App-wide appearance: Light / Dark / System, chosen from
// Settings → Appearance and persisted via ThemePreference.
// ============================================================

enum class ShThemeMode { LIGHT, DARK, SYSTEM }

/**
 * Categorization for Shoshin screens.
 * DYNAMIC: Follows user preference (Dashboard, Settings, Circle, etc.)
 * ALWAYS_LIGHT: Fixed light theme (Reserved for specific onboarding steps if needed)
 * ALWAYS_DARK: "Night" screens for morning focus (Activation, Camera, 71-Day, etc.)
 */
enum class ShoshinThemeType { DYNAMIC, ALWAYS_LIGHT, ALWAYS_DARK }

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
    primary          = ShVermillionLight,
    onPrimary        = Color.White,
    secondary        = ShMatchaLightToken,
    onSecondary      = Color.White,
    background       = ShPaperLight,
    onBackground     = ShInkLight,
    surface          = ShSurfaceLight,
    onSurface        = ShInkLight,
    surfaceVariant   = ShPaper2Light,
    onSurfaceVariant = ShFogLight,
    outline          = ShLineLight,
    error            = ShError,
    scrim            = ShScrim,
)

private val ShDarkColorScheme = darkColorScheme(
    primary          = ShVermillionDark,
    onPrimary        = Color.White,
    secondary        = ShMatchaDark,
    onSecondary      = Color.White,
    background       = ShPaperDark,
    onBackground     = ShInkDark,
    surface          = ShSurfaceDark,
    onSurface        = ShInkDark,
    surfaceVariant   = ShPaper2Dark,
    onSurfaceVariant = ShFogDark,
    outline          = ShLineDark,
    error            = ShError,
    scrim            = ShScrim,
)

// "Night" palette for fixed-dark screens (Camera, Activation, etc.)
private val ShNightColorScheme = darkColorScheme(
    primary          = ShVermillionLight,
    onPrimary        = Color.White,
    background       = ShNight,
    onBackground     = ShNightText,
    surface          = ShNight2,
    onSurface        = ShNightText,
    surfaceVariant   = ShNight3,
    onSurfaceVariant = ShNightMuted,
    outline          = ShNightLine,
    error            = ShError,
)

@Composable
fun ShoshinTheme(
    type: ShoshinThemeType = ShoshinThemeType.DYNAMIC,
    content: @Composable () -> Unit,
) {
    val context = LocalContext.current
    val themeViewModel: ThemeViewModel = viewModel(
        key = "ShoshinThemeViewModel",
        factory = object : androidx.lifecycle.ViewModelProvider.Factory {
            override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
                return ThemeViewModel(context.applicationContext as Application) as T
            }
        }
    )
    val mode by themeViewModel.mode.collectAsState()
    val systemIsDark = isSystemInDarkTheme()
    
    val colorScheme = when (type) {
        ShoshinThemeType.ALWAYS_DARK -> ShNightColorScheme
        ShoshinThemeType.ALWAYS_LIGHT -> ShLightColorScheme
        ShoshinThemeType.DYNAMIC -> {
            val appIsDark = when (mode) {
                ShThemeMode.DARK -> true
                ShThemeMode.LIGHT -> false
                ShThemeMode.SYSTEM -> systemIsDark
            }
            if (appIsDark) ShDarkColorScheme else ShLightColorScheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography  = ShTypography,
        shapes      = ShShapes,
        content     = content,
    )
}
