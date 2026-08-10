package com.Shoshin.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.foundation.border
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.Shoshin.app.ui.theme.*

@Composable
fun ShoshinLogoMark(modifier: Modifier = Modifier, on: String = "light") {
    val isNight = on == "night"
    // Fixed-dark screens (Splash, Paywall, ...) force "night" regardless of app theme.
    // Everywhere else, the frame stroke + first bar follow var(--sh-ink): they must flip
    // with the app's dark mode instead of staying pinned to the light value.
    val isDark = !isNight && MaterialTheme.colorScheme.background == ShNight

    val borderColor = when {
        isNight -> ShNightMuted
        isDark -> ShLine2Dark
        else -> ShLine2
    }
    val frameBarColor = when {
        isNight -> Color.White
        isDark -> ShInkDark
        else -> ShInk
    }

    Box(
        modifier = modifier
            .size(56.dp)
            .background(Color.Transparent, RoundedCornerShape(16.dp))
            .border(1.5.dp, borderColor, RoundedCornerShape(16.dp)),
        contentAlignment = Alignment.Center
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalAlignment = Alignment.Bottom,
            modifier = Modifier.padding(bottom = 10.dp)
        ) {
            Box(modifier = Modifier.width(7.dp).height(12.dp).background(frameBarColor, RoundedCornerShape(2.dp)))
            Box(modifier = Modifier.width(7.dp).height(20.dp).background(ShMatcha, RoundedCornerShape(2.dp)))
            Box(modifier = Modifier.width(7.dp).height(28.dp).background(ShVermillion, RoundedCornerShape(2.dp)))
        }
    }
}
