package com.Shoshin.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.Shoshin.app.R
import com.Shoshin.app.ui.components.*
import com.Shoshin.app.ui.theme.*

@Composable
fun MorningCompleteScreen(
    onClose: () -> Unit,
    onShare: () -> Unit,
    streakViewModel: com.Shoshin.app.viewmodel.StreakViewModel? = null
) {
    val user by streakViewModel?.user?.collectAsState(initial = null) ?: remember { mutableStateOf(null) }
    val streakCount = user?.currentStreak ?: 15

    ShoshinTheme(type = ShoshinThemeType.ALWAYS_DARK) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(ShNight)
                .padding(horizontal = 24.dp, vertical = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.weight(0.2f))

            // Large Enso with Check
            Box(
                modifier = Modifier.size(240.dp),
                contentAlignment = Alignment.Center
            ) {
                androidx.compose.foundation.Canvas(modifier = Modifier.fillMaxSize()) {
                    drawArc(
                        color = ShMatchaDark.copy(alpha = 0.5f),
                        startAngle = -90f,
                        sweepAngle = 360f,
                        useCenter = false,
                        style = androidx.compose.ui.graphics.drawscope.Stroke(
                            width = 6.dp.toPx(),
                            cap = androidx.compose.ui.graphics.StrokeCap.Round
                        )
                    )
                }
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(CircleShape)
                        .background(ShMatchaDark),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_check),
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(36.dp)
                    )
                }
            }

            Spacer(Modifier.height(48.dp))

            Kicker("THE BRIDGE IS CROSSED", color = ShNightMuted)
            
            Spacer(Modifier.height(12.dp))

            Text(
                "You've begun.",
                style = ShTitleStyle.copy(fontSize = 40.sp, color = Color.White),
                textAlign = TextAlign.Center
            )

            Spacer(Modifier.height(16.dp))

            Text(
                "Five checkpoints, twenty-two minutes. The hardest part of the day is already behind you.",
                style = ShBodyStyle,
                color = ShNightMuted,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 24.dp),
                lineHeight = 24.sp
            )

            Spacer(Modifier.height(48.dp))

            // Summary Card
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(24.dp))
                    .background(ShNight2)
                    .padding(24.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceAround,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    SummaryStatDark(value = "05:30", label = "Started")
                    Box(Modifier.width(1.dp).height(32.dp).background(ShNightLine))
                    SummaryStatDark(value = "22", unit = "min", label = "Bridge")
                    Box(Modifier.width(1.dp).height(32.dp).background(ShNightLine))
                    SummaryStatDark(value = "5/5", label = "Kept", color = ShMatchaDark)
                }
            }

            Spacer(Modifier.height(24.dp))

            // Streak indicator
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                Icon(painterResource(R.drawable.ic_flame), null, tint = ShVermillion, modifier = Modifier.size(16.dp))
                Text("$streakCount mornings kept", style = ShLabelStyle, fontWeight = FontWeight.Bold, color = Color.White)
                ShoshinPill(label = "+1", variant = ShPillVariant.Matcha)
            }

            Spacer(Modifier.weight(1f))

            ShoshinButton(
                onClick = onClose,
                variant = ShButtonVariant.Ghost, // Off-white button in dark mode
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Carry it into the day", color = Color.Black, fontWeight = FontWeight.Bold)
            }

            Spacer(Modifier.height(24.dp))

            Text(
                "Return again tomorrow.",
                style = ShLabelStyle.copy(fontSize = 13.sp),
                color = ShNightMuted,
                textAlign = TextAlign.Center
            )

            Spacer(Modifier.height(16.dp))
        }
    }
}

@Composable
private fun SummaryStatDark(value: String, unit: String? = null, label: String, color: Color = Color.White) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Row(verticalAlignment = Alignment.Bottom) {
            Text(value, style = ShNumeralStyle.copy(fontSize = 32.sp, color = color))
            if (unit != null) {
                Text(unit, style = ShNumeralStyle.copy(fontSize = 14.sp, color = ShNightMuted), modifier = Modifier.padding(bottom = 6.dp, start = 2.dp))
            }
        }
        Text(label.uppercase(), style = ShKickerStyle.copy(fontSize = 9.sp, color = ShNightMuted))
    }
}
