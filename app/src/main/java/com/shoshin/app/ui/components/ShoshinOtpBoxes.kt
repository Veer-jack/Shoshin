package com.Shoshin.app.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.Shoshin.app.ui.theme.*
import kotlin.math.roundToInt

@Composable
fun ShoshinOtpBoxes(
    value: String,
    length: Int = 6,
    dark: Boolean = false,
    isError: Boolean = false,
    modifier: Modifier = Modifier,
) {
    // ±4dp, 3 cycles, 300ms total, per motion spec §4.1
    val shakeOffset = remember { Animatable(0f) }
    LaunchedEffect(isError) {
        if (isError) {
            val amplitude = 4f
            val step = 50
            repeat(3) {
                shakeOffset.animateTo(amplitude, tween(step))
                shakeOffset.animateTo(-amplitude, tween(step))
            }
            shakeOffset.animateTo(0f, tween(step))
        } else {
            shakeOffset.snapTo(0f)
        }
    }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .offset { IntOffset(shakeOffset.value.roundToInt(), 0) },
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        repeat(length) { index ->
            val char   = value.getOrNull(index)?.toString() ?: ""
            val active = index == value.length

            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(64.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(if (dark) ShNight2 else ShPaper2)
                    .border(
                        width = if (isError || active) 1.5.dp else 1.2.dp,
                        color = when {
                            isError -> MaterialTheme.colorScheme.error
                            active  -> ShVermillion
                            else    -> if (dark) ShNightLine else ShLine
                        },
                        shape = RoundedCornerShape(14.dp),
                    ),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text       = char,
                    fontFamily = CormorantFamily,
                    fontWeight = FontWeight.SemiBold,
                    fontSize   = 28.sp,
                    color      = if (dark) Color.White else ShInk,
                )
            }
        }
    }
}
