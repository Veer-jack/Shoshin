package com.example.shoshinapp.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.shoshinapp.ui.theme.*

// ============================================================
// ShoshinButton — Primary, Accent, Ghost, Matcha, Dark variants
// Height: 56dp · Radius: 14dp · Font: DM Sans 600 16sp
// Press: scale(0.985) · 120ms ease
// Rule: ONE vermillion/accent CTA per screen maximum
// ============================================================

enum class ShButtonVariant { Primary, Accent, Ghost, Matcha, Dark }

@Composable
fun ShoshinButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    variant: ShButtonVariant = ShButtonVariant.Primary,
    enabled: Boolean = true,
    leadingIcon: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    content: @Composable RowScope.() -> Unit,
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.985f else 1f,
        animationSpec = androidx.compose.animation.core.tween(120),
        label = "button_scale",
    )

    val (bg, fg, border) = when (variant) {
        ShButtonVariant.Primary -> Triple(ShInk, Color.White, Color.Transparent)
        ShButtonVariant.Accent  -> Triple(ShVermillion, Color.White, Color.Transparent)
        ShButtonVariant.Ghost   -> Triple(Color.Transparent, ShInk, ShLine2)
        ShButtonVariant.Matcha  -> Triple(ShMatcha, Color.White, Color.Transparent)
        ShButtonVariant.Dark    -> Triple(ShNight2, ShNightText, ShNightBorder)
    }

    Button(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp)
            .scale(scale),
        enabled = enabled,
        shape = ShRadiusButton,
        colors = ButtonDefaults.buttonColors(
            containerColor = bg,
            contentColor   = fg,
        ),
        border = if (border != Color.Transparent)
            androidx.compose.foundation.BorderStroke(1.5.dp, border) else null,
        contentPadding = PaddingValues(horizontal = 24.dp),
        interactionSource = interactionSource,
    ) {
        if (leadingIcon != null) {
            leadingIcon()
            Spacer(Modifier.width(8.dp))
        }
        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            content()
        }
        if (trailingIcon != null) {
            Spacer(Modifier.width(8.dp))
            trailingIcon()
        }
    }
}
