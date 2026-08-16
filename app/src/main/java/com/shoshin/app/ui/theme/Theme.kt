package com.Shoshin.app.ui.theme

import android.app.Application
import android.content.Context
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.unit.dp
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
    primary          = ShVermillionLight, // Vermillion is primary in dark
    onPrimary        = Color.White,
    secondary        = ShMatchaDark,
    onSecondary      = Color.White,
    background       = ShNight,      // #0F0F0F
    onBackground     = ShNightText,  // #F2F1EC
    surface          = ShNight2,     // #1A1A1A
    onSurface        = ShNightText,
    surfaceVariant   = ShNight3,     // #242424
    onSurfaceVariant = ShNightMuted, // #8C8A85
    outline          = ShNightLine,
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
    val isPreview = LocalInspectionMode.current
    
    val mode = if (isPreview) {
        ShThemeMode.SYSTEM
    } else {
        val themeViewModel: ThemeViewModel = viewModel(
            key = "ShoshinThemeViewModel",
            factory = object : androidx.lifecycle.ViewModelProvider.Factory {
                override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
                    return ThemeViewModel(context.applicationContext as Application) as T
                }
            }
        )
        themeViewModel.mode.collectAsState().value
    }

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

// ============================================================
// Shoshin — Shadow tokens (--sh-shadow-sm / --sh-shadow / --sh-shadow-lg)
// CSS defines each as two stacked box-shadows; Modifier.shadow only
// draws one layer, so each level is approximated with a single
// elevation + tint that reads the same as the combined CSS shadow.
// ============================================================
enum class ShShadowLevel { Small, Medium, Large }

// The one place a small overshoot is allowed, per motion spec §3.3 (celebration moments)
val ShCelebrationEasing = androidx.compose.animation.core.CubicBezierEasing(0.34f, 1.56f, 0.64f, 1f)

/**
 * Android's equivalent of `prefers-reduced-motion`: users toggle this via
 * Settings → Accessibility → Remove animations (or Developer options → Animator duration scale = 0).
 * Per motion spec §3.4: non-essential animation (ring draw, bar grow, confetti, rotating
 * decorative rings) should skip to its end-state immediately when this is true.
 */
@Composable
fun rememberReducedMotion(): Boolean {
    val context = LocalContext.current
    return remember {
        try {
            android.provider.Settings.Global.getFloat(
                context.contentResolver,
                android.provider.Settings.Global.ANIMATOR_DURATION_SCALE,
                1f,
            ) == 0f
        } catch (e: Exception) {
            false
        }
    }
}

private val ShShadowTintLight = Color(0xFF1C1C1E)

@Composable
fun Modifier.shShadow(
    level: ShShadowLevel = ShShadowLevel.Medium,
    shape: Shape = RoundedCornerShape(20.dp),
): Modifier {
    val isDark = MaterialTheme.colorScheme.background == ShNight
    val (elevation, tint) = when (level) {
        ShShadowLevel.Small  -> 2.dp to (if (isDark) Color.Black.copy(alpha = 0.30f) else ShShadowTintLight.copy(alpha = 0.04f))
        ShShadowLevel.Medium -> 6.dp to (if (isDark) Color.Black.copy(alpha = 0.30f) else ShShadowTintLight.copy(alpha = 0.05f))
        ShShadowLevel.Large  -> 16.dp to (if (isDark) Color.Black.copy(alpha = 0.40f) else ShShadowTintLight.copy(alpha = 0.08f))
    }
    return this.shadow(elevation = elevation, shape = shape, ambientColor = tint, spotColor = tint)
}
