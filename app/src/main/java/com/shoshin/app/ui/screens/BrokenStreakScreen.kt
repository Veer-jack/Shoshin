package com.Shoshin.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
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

@Composable
fun BrokenStreakScreen(navController: NavController) {
    ShoshinTheme(type = ShoshinThemeType.ALWAYS_DARK) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(ShNight)
                .padding(horizontal = 24.dp, vertical = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.weight(0.5f))

            // Broken chain motif
            Row(
                modifier = Modifier.height(64.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Box(modifier = Modifier.size(24.dp).clip(CircleShape).background(ShMatchaDark.copy(alpha = 0.4f)))
                Box(modifier = Modifier.width(32.dp).height(2.dp).background(ShNightLine))
                Box(modifier = Modifier.size(24.dp).clip(CircleShape).background(ShVermillion.copy(alpha = 0.4f)))
                Box(modifier = Modifier.width(32.dp).height(2.dp).background(ShNightLine))
                Box(modifier = Modifier.size(24.dp).clip(CircleShape).background(ShNight3))
            }

            Spacer(Modifier.height(32.dp))
            Kicker("THE CHAIN RESTED", color = ShNightMuted)
            Spacer(Modifier.height(12.dp))
            Text(
                "A miss is not\na failure",
                style = ShTitleStyle.copy(fontSize = 36.sp, color = Color.White),
                textAlign = TextAlign.Center
            )
            Spacer(Modifier.height(16.dp))
            Text(
                "You held 14 mornings. That practice is yours to keep. Shoshin means beginning again — without judgement.",
                style = ShBodyStyle,
                color = ShNightMuted,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 20.dp),
                lineHeight = 24.sp
            )

            Spacer(Modifier.height(48.dp))

            // Preserved Stats
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
                    SummaryStatItem(value = "14", label = "Best held", color = ShMatchaDark)
                    Box(Modifier.width(1.dp).height(32.dp).background(ShNightLine))
                    SummaryStatItem(value = "148", label = "Total kept")
                    Box(Modifier.width(1.dp).height(32.dp).background(ShNightLine))
                    SummaryStatItem(value = "86", unit = "%", label = "All-time")
                }
            }

            Spacer(Modifier.weight(1f))

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
                style = ShLabelStyle,
                color = ShNightMuted,
                textAlign = TextAlign.Center
            )
            
            Spacer(Modifier.height(16.dp))
        }
    }
}

@Composable
private fun SummaryStatItem(value: String, unit: String? = null, label: String, color: Color = Color.White) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Row(verticalAlignment = Alignment.Bottom) {
            Text(value, style = ShNumeralStyle.copy(fontSize = 28.sp, color = color))
            if (unit != null) {
                Text(unit, style = ShNumeralStyle.copy(fontSize = 14.sp, color = ShNightMuted), modifier = Modifier.padding(bottom = 4.dp, start = 2.dp))
            }
        }
        Text(label.uppercase(), style = ShKickerStyle.copy(fontSize = 9.sp, color = ShNightMuted))
    }
}
