package com.example.shoshinapp.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
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
import com.example.shoshinapp.R
import com.example.shoshinapp.ui.components.*
import com.example.shoshinapp.ui.theme.*

@Composable
fun MorningCompleteScreen(onClose: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(ShPaper2) // Design spec uses paper-2 for completion
            .padding(horizontal = 24.dp, vertical = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(Modifier.weight(0.2f))

        // Large Enso with Check
        Box(
            modifier = Modifier.size(200.dp),
            contentAlignment = Alignment.Center
        ) {
            androidx.compose.foundation.Canvas(modifier = Modifier.fillMaxSize()) {
                drawArc(
                    color = ShMatcha,
                    startAngle = -90f,
                    sweepAngle = 360f,
                    useCenter = false,
                    style = androidx.compose.ui.graphics.drawscope.Stroke(
                        width = 7.dp.toPx(),
                        cap = androidx.compose.ui.graphics.StrokeCap.Round
                    )
                )
            }
            Box(
                modifier = Modifier
                    .size(72.dp)
                    .clip(CircleShape)
                    .background(ShMatcha),
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

        Spacer(Modifier.height(28.dp))

        Kicker("The bridge is crossed", color = ShMatcha)
        
        Spacer(Modifier.height(8.dp))

        Text(
            "You've begun.",
            fontSize = 32.sp,
            fontWeight = FontWeight.SemiBold,
            fontFamily = CormorantFamily,
            color = ShInk,
            textAlign = TextAlign.Center
        )

        Spacer(Modifier.height(10.dp))

        Text(
            "Five checkpoints, twenty-two minutes. The hardest part of the day is already behind you.",
            fontSize = 15.sp,
            color = ShFog,
            fontFamily = DmSansFamily,
            textAlign = TextAlign.Center,
            modifier = Modifier.width(290.dp)
        )

        Spacer(Modifier.height(28.dp))

        // Summary Card
        ShoshinCard(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier.padding(18.dp),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                SummaryStat(value = "05:30", label = "Started")
                Box(Modifier.width(1.dp).height(30.dp).background(ShLine))
                SummaryStat(value = "22", unit = "min", label = "Bridge")
                Box(Modifier.width(1.dp).height(30.dp).background(ShLine))
                SummaryStat(value = "5/5", label = "Kept", color = ShMatcha)
            }
        }

        Spacer(Modifier.height(16.dp))

        // Streak Bump
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Icon(
                painter = painterResource(R.drawable.ic_flame),
                contentDescription = null,
                tint = ShVermillion,
                modifier = Modifier.size(18.dp)
            )
            Text(
                "15 mornings kept",
                style = ShLabelStyle,
                fontWeight = FontWeight.Bold,
                color = ShInk
            )
            ShoshinPill(label = "+1", variant = ShPillVariant.Matcha)
        }

        Spacer(Modifier.weight(1f))

        ShoshinButton(
            onClick = onClose,
            variant = ShButtonVariant.Primary
        ) {
            Text("Carry it into the day")
        }

        Spacer(Modifier.height(16.dp))

        Text(
            "Return again tomorrow.",
            style = ShLabelStyle,
            color = ShFog2,
            textAlign = TextAlign.Center
        )

        Spacer(Modifier.height(12.dp))
    }
}

@Composable
private fun SummaryStat(value: String, unit: String? = null, label: String, color: Color = ShInk) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Row(verticalAlignment = Alignment.Bottom) {
            Text(value, style = ShNumeralStyle.copy(fontSize = 24.sp, color = color))
            if (unit != null) {
                Text(unit, style = ShNumeralStyle.copy(fontSize = 12.sp, color = ShFog), modifier = Modifier.padding(bottom = 3.dp, start = 2.dp))
            }
        }
        Text(label.uppercase(), style = ShKickerStyle.copy(fontSize = 9.sp, letterSpacing = 1.sp))
    }
}
