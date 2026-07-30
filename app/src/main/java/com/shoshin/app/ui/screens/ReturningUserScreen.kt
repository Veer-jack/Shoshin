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
import com.Shoshin.app.ui.components.*
import com.Shoshin.app.ui.theme.*

@Composable
fun ReturningUserScreen(navController: NavController) {
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
                    "A",
                    style = ShTitleStyle.copy(fontSize = 36.sp, color = Color.White.copy(alpha = 0.6f))
                )
            }

            Spacer(Modifier.height(32.dp))

            Kicker("WELCOME BACK", color = ShVermillionLight)
            
            Spacer(Modifier.height(12.dp))

            Text(
                "Good to see you,\nArjun",
                style = ShTitleStyle.copy(fontSize = 36.sp, color = Color.White),
                textAlign = TextAlign.Center,
                lineHeight = 42.sp
            )

            Spacer(Modifier.height(16.dp))

            Text(
                "It's been 3 days. Your 71-day practice paused at day 38 — and it's waiting exactly where you left it.",
                style = ShBodyStyle,
                color = ShNightMuted,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 24.dp),
                lineHeight = 24.sp
            )

            Spacer(Modifier.height(48.dp))

            // Hold Card
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(24.dp))
                    .background(ShNight2)
                    .padding(20.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(contentAlignment = Alignment.Center) {
                        RingProgress(
                            percentage = 53,
                            size = 64,
                            strokeWidth = 5f,
                            valueText = "",
                            color = ShVermillion,
                            trackColor = ShNight3
                        )
                    }
                    
                    Spacer(Modifier.width(20.dp))
                    
                    Column(modifier = Modifier.weight(1f)) {
                        Text("71-Day Discipline", style = ShH2Style.copy(fontSize = 17.sp, color = Color.White))
                        Text("Resume at day 38", style = ShLabelStyle, color = ShNightMuted)
                    }
                    
                    Surface(
                        color = ShMatchaDark.copy(alpha = 0.1f),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            "Held",
                            color = ShMatchaDark,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                            style = ShLabelStyle.copy(fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        )
                    }
                }
            }

            Spacer(Modifier.weight(1f))

            ShoshinButton(
                onClick = { navController.navigate("main") },
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
