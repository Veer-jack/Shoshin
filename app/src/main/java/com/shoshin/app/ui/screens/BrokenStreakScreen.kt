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
import androidx.navigation.NavController
import com.Shoshin.app.R
import com.Shoshin.app.ui.components.*
import com.Shoshin.app.ui.theme.*
import com.Shoshin.app.viewmodel.StreakViewModel

@Composable
fun BrokenStreakScreen(
    navController: NavController,
    streakViewModel: StreakViewModel? = null
) {
    val user by streakViewModel?.user?.collectAsState(initial = null) ?: remember { mutableStateOf(null) }
    val bestStreak = user?.bestStreak ?: 0
    val totalMornings = user?.totalActivations ?: 0
    val allTimeRate = if (user != null && user!!.totalActivations > 0) {
        val daysSinceCreation = ((System.currentTimeMillis() - user!!.createdAt) / (1000 * 60 * 60 * 24)).coerceAtLeast(1)
        ((user!!.totalActivations.toFloat() / daysSinceCreation.toFloat()) * 100).toInt().coerceIn(0, 100)
    } else {
        0
    }

    ShoshinTheme(type = ShoshinThemeType.ALWAYS_DARK) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(ShNight)
                .padding(horizontal = 24.dp, vertical = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.weight(0.3f))

            // Broken Streak Icon
            Box(contentAlignment = Alignment.Center, modifier = Modifier.size(120.dp)) {
                Enso(size = 120, color = ShNightLine, strokeWidth = 3f)
                Icon(
                    painter = painterResource(R.drawable.ic_flame), 
                    contentDescription = null, 
                    tint = ShVermillion.copy(alpha = 0.4f),
                    modifier = Modifier.size(48.dp)
                )
                // Diagonal slash mockup
                val slashColor = ShVermillion
                androidx.compose.foundation.Canvas(modifier = Modifier.size(60.dp)) {
                    drawLine(
                        color = slashColor,
                        start = androidx.compose.ui.geometry.Offset(0f, size.height),
                        end = androidx.compose.ui.geometry.Offset(size.width, 0f),
                        strokeWidth = 4.dp.toPx(),
                        cap = androidx.compose.ui.graphics.StrokeCap.Round
                    )
                }
            }

            Spacer(Modifier.height(48.dp))

            Kicker("THE CHAIN RESTED", color = ShNightMuted)
            
            Spacer(Modifier.height(12.dp))

            Text(
                "A miss is not\na failure",
                style = ShTitleStyle.copy(fontSize = 36.sp, color = Color.White),
                textAlign = TextAlign.Center
            )

            Spacer(Modifier.height(16.dp))

            Text(
                "You held $bestStreak mornings. That practice is yours to keep. Shoshin means beginning again — without judgement.",
                style = ShBodyStyle,
                color = ShNightMuted,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 24.dp),
                lineHeight = 24.sp
            )

            Spacer(Modifier.height(48.dp))

            // Stats Card
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
                    BrokenStatItem(value = bestStreak.toString(), label = "BEST HELD", color = ShMatchaDark)
                    Box(Modifier.width(1.dp).height(32.dp).background(ShNightLine))
                    BrokenStatItem(value = totalMornings.toString(), label = "TOTAL KEPT")
                    Box(Modifier.width(1.dp).height(32.dp).background(ShNightLine))
                    BrokenStatItem(value = allTimeRate.toString(), unit = "%", label = "ALL-TIME")
                }
            }

            Spacer(Modifier.weight(0.7f))

            ShoshinButton(
                onClick = { navController.popBackStack() },
                variant = ShButtonVariant.Accent,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Begin again", fontWeight = FontWeight.Bold)
            }

            Spacer(Modifier.height(24.dp))

            Text(
                "Tomorrow is day one of the next chain.",
                style = ShLabelStyle.copy(fontSize = 13.sp),
                color = ShNightMuted,
                textAlign = TextAlign.Center
            )

            Spacer(Modifier.height(16.dp))
        }
    }
}

@Composable
private fun BrokenStatItem(value: String, unit: String? = null, label: String, color: Color = Color.White) {
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
