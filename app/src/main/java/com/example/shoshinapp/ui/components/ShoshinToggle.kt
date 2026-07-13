package com.example.shoshinapp.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.shoshinapp.ui.theme.*

// ============================================================
// ShoshinToggle
// Size: 48×28dp · Thumb: 22dp circle
// On:  ShMatcha background, thumb at end (+20dp)
// Off: ShLine2 background, thumb at start
// Duration: 200ms cubic-bezier(.3,.7,.4,1)
// ============================================================

private val ToggleEasing = CubicBezierEasing(0.3f, 0.7f, 0.4f, 1f)

@Composable
fun ShoshinToggle(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    val trackColor by animateColorAsState(
        targetValue = if (checked) ShMatcha else ShLine2,
        animationSpec = tween(200),
        label = "toggle_track",
    )
    val thumbOffset by animateDpAsState(
        targetValue = if (checked) 20.dp else 0.dp,
        animationSpec = tween(200, easing = ToggleEasing),
        label = "toggle_thumb",
    )

    Box(
        modifier = modifier
            .width(48.dp)
            .height(28.dp)
            .background(trackColor, RoundedCornerShape(999.dp))
            .clickable(
                indication = null,
                interactionSource = remember { MutableInteractionSource() },
                onClick = { onCheckedChange(!checked) },
            ),
        contentAlignment = Alignment.CenterStart,
    ) {
        Box(
            modifier = Modifier
                .padding(start = 3.dp + thumbOffset)
                .size(22.dp)
                .shadow(elevation = 2.dp, shape = CircleShape)
                .background(Color.White, CircleShape),
        )
    }
}
