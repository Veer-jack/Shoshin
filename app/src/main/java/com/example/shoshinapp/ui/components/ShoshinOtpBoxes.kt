package com.example.shoshinapp.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.shoshinapp.ui.theme.*

// ============================================================
// ShoshinOtpBoxes
// 6 boxes · aspect ~0.8 (w:h) · max-width 50dp per box
// Radius: 12dp · Active: sh_ink border 2dp · Gap: 10dp
// Variants: light (Auth) · dark (Activation)
// ============================================================

@Composable
fun ShoshinOtpBoxes(
    value: String,
    length: Int = 6,
    dark: Boolean = false,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        repeat(length) { index ->
            val char   = value.getOrNull(index)?.toString() ?: ""
            val active = index == value.length
            val filled = index < value.length

            Box(
                modifier = Modifier
                    .weight(1f)
                    .aspectRatio(0.8f)
                    .widthIn(max = 50.dp)
                    .background(
                        color = if (dark) ShNight3 else ShPaper2,
                        shape = RoundedCornerShape(12.dp),
                    )
                    .border(
                        width = if (active) 2.dp else 1.5.dp,
                        color = when {
                            active -> if (dark) ShNightText else ShInk
                            filled -> if (dark) ShNightBorder else ShLine
                            else   -> if (dark) ShNightBorder else ShLine
                        },
                        shape = RoundedCornerShape(12.dp),
                    ),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text       = char,
                    fontFamily = DmSansFamily,
                    fontWeight = FontWeight.SemiBold,
                    fontSize   = 22.sp,
                    color      = if (dark) ShNightText else ShInk,
                )
            }
        }
    }
}
