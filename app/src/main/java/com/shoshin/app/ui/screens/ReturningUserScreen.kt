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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.Shoshin.app.navigation.ShRoutes
import com.Shoshin.app.ui.components.*
import com.Shoshin.app.ui.theme.*
import com.Shoshin.app.utils.AnalyticsManager
import com.Shoshin.app.viewmodel.StreakViewModel
import kotlinx.coroutines.delay
import java.util.concurrent.TimeUnit

@Composable
fun ReturningUserScreen(
    navController: NavController,
    streakViewModel: StreakViewModel? = null,
    lastOpenDate: Long = 0L
) {
    val user by streakViewModel?.user?.collectAsState(initial = null) ?: remember { mutableStateOf(null) }
    val displayName = user?.displayName?.takeIf { it.isNotBlank() && it != "New User" && it != "User" } ?: "there"
    val currentStreak = user?.currentStreak ?: 0
    val bestStreak = user?.bestStreak ?: 0
    val daysSinceLastOpen = if (lastOpenDate > 0) {
        TimeUnit.MILLISECONDS.toDays(System.currentTimeMillis() - lastOpenDate).toInt().coerceAtLeast(0)
    } else 0

    fun resume() {
        navController.navigate(ShRoutes.MAIN) {
            popUpTo(0) { inclusive = true }
        }
    }

    LaunchedEffect(Unit) {
        AnalyticsManager.logReturningUserShown(daysSinceLastOpen)
        delay(5000)
        resume()
    }

    ShoshinTheme(type = ShoshinThemeType.ALWAYS_DARK) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(ShNight)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.weight(0.6f))

            // Initial Avatar
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .clip(CircleShape)
                    .background(ShNight2),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    displayName.take(1).uppercase(),
                    style = ShTitleStyle.copy(fontSize = 36.sp, color = Color.White.copy(alpha = 0.6f))
                )
            }

            Spacer(Modifier.height(32.dp))

            Kicker("WELCOME BACK", color = ShVermillionLight)

            Spacer(Modifier.height(12.dp))

            Text(
                "Good to see you,\n$displayName",
                style = ShTitleStyle.copy(fontSize = 36.sp, color = Color.White),
                textAlign = TextAlign.Center,
                lineHeight = 42.sp
            )

            Spacer(Modifier.height(16.dp))

            Text(
                if (currentStreak == 0) "Ready to start fresh? 💪" else "Keep it going! $currentStreak days strong 🔥",
                style = ShBodyStyle,
                color = ShNightMuted,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 24.dp),
                lineHeight = 24.sp
            )

            Spacer(Modifier.height(48.dp))

            // Streak Summary Card
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(24.dp))
                    .background(ShNight2)
                    .padding(20.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceAround,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("$currentStreak", style = ShNumeralStyle.copy(fontSize = 28.sp, color = Color.White))
                        Text("CURRENT STREAK", style = ShKickerStyle.copy(fontSize = 9.sp, color = ShNightMuted))
                    }
                    Box(Modifier.width(1.dp).height(32.dp).background(ShNightLine))
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("$bestStreak", style = ShNumeralStyle.copy(fontSize = 28.sp, color = Color.White))
                        Text("BEST STREAK", style = ShKickerStyle.copy(fontSize = 9.sp, color = ShNightMuted))
                    }
                }
            }

            Spacer(Modifier.weight(1f))

            ShoshinButton(
                onClick = { resume() },
                variant = ShButtonVariant.Accent,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Resume practice", fontWeight = FontWeight.Bold)
            }

            Spacer(Modifier.height(24.dp))

            Text(
                "Beginner's mind. Pick up gently.",
                style = ShLabelStyle,
                color = ShNightMuted,
                textAlign = TextAlign.Center
            )

            Spacer(Modifier.height(24.dp))
        }
    }
}
