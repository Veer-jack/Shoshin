package com.shoshin.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.shoshin.app.ui.components.*
import com.shoshin.app.ui.theme.*

@Composable
fun ReturningUserScreen(navController: NavController) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 24.dp, vertical = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(Modifier.weight(1f))

        // Profile Initial
        Box(
            modifier = Modifier
                .size(78.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.surfaceVariant),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "A",
                style = ShTitleStyle.copy(fontSize = 34.sp),
                color = MaterialTheme.colorScheme.onSurface
            )
        }

        Spacer(Modifier.height(20.dp))
        Kicker("Welcome back", color = ShVermillion)
        Spacer(Modifier.height(6.dp))
        Text(
            "Good to see you,\nArjun",
            style = ShTitleStyle.copy(fontSize = 32.sp),
            color = MaterialTheme.colorScheme.onBackground,
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(10.dp))
        Text(
            "It's been 3 days. Your 71-day practice paused at day 38 — and it's waiting exactly where you left it.",
            style = ShBodyStyle,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            modifier = Modifier.widthIn(max = 300.dp)
        )

        Spacer(Modifier.height(24.dp))

        // Paused challenge card
        ShoshinCard(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier.padding(18.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                RingProgress(
                    percentage = (38 * 100 / 71),
                    size = 56,
                    strokeWidth = 6f,
                    valueText = "",
                    color = ShVermillion
                )
                Column(modifier = Modifier.weight(1f)) {
                    Text("71-Day Discipline", style = ShH2Style.copy(fontSize = 15.sp), color = MaterialTheme.colorScheme.onSurface)
                    Text("Resume at day 38", style = ShLabelStyle, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                ShoshinPill(label = "Held", variant = ShPillVariant.Matcha)
            }
        }

        Spacer(Modifier.weight(1f))

        ShoshinButton(
            onClick = { navController.popBackStack() },
            variant = ShButtonVariant.Accent,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Resume practice")
        }
        
        Spacer(Modifier.height(16.dp))
        Text(
            "Beginner's mind. Pick up gently.",
            style = ShLabelStyle,
            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
            textAlign = TextAlign.Center
        )
        
        Spacer(Modifier.height(28.dp))
    }
}
