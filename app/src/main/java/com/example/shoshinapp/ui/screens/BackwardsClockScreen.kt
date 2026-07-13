package com.example.shoshinapp.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import com.example.shoshinapp.R
import com.example.shoshinapp.features.clock.TimeRemaining
import com.example.shoshinapp.ui.components.*
import com.example.shoshinapp.ui.theme.*
import com.example.shoshinapp.viewmodel.BackwardsClockViewModel
import java.util.Locale

@Composable
fun BackwardsClockScreen(
    navController: NavController,
    viewModel: BackwardsClockViewModel
) {
    val timeRemaining by viewModel.timeRemaining.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(ShPaper)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // App Bar
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { navController.popBackStack() }) {
                Icon(painterResource(R.drawable.ic_arrow_left), contentDescription = "Back", tint = ShInk)
            }
            Spacer(Modifier.width(16.dp))
            Text("Day Countdown", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
        }

        Spacer(Modifier.weight(0.5f))

        // Large Clock Display
        BackwardsClockDisplay(timeRemaining)

        Spacer(Modifier.height(48.dp))

        // Motivation
        MotivationalMessage(timeRemaining)

        Spacer(Modifier.weight(1f))

        // Progress Details
        ShoshinCard(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(24.dp)) {
                ProgressSection(
                    title = "Hours Left in Day",
                    percentage = timeRemaining.percentDayRemaining,
                    hours = timeRemaining.hoursRemaining.toFloat() + (timeRemaining.minutesRemaining / 60f),
                    color = getColorForPercentage(timeRemaining.percentDayRemaining)
                )

                if (timeRemaining.isProductiveHours) {
                    Spacer(Modifier.height(32.dp))
                    ProgressSection(
                        title = "Productive Time Left",
                        percentage = timeRemaining.percentProductiveRemaining,
                        hours = timeRemaining.productiveHoursRemaining,
                        color = ShMatcha
                    )
                }
            }
        }
        
        Spacer(Modifier.height(24.dp))
    }
}

@Composable
fun BackwardsClockDisplay(timeRemaining: TimeRemaining) {
    Box(
        modifier = Modifier
            .size(260.dp)
            .clip(CircleShape)
            .background(ShSurface)
            .border(
                width = 2.dp,
                color = getColorForPercentage(timeRemaining.percentDayRemaining),
                shape = CircleShape
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = String.format(Locale.getDefault(), "%02d:%02d:%02d", 
                    timeRemaining.hoursRemaining, 
                    timeRemaining.minutesRemaining, 
                    timeRemaining.secondsRemaining),
                fontSize = 48.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = DmSansFamily,
                color = ShInk
            )
            Text(
                text = "REMAINING",
                style = ShKickerStyle,
                color = ShFog
            )
        }
    }
}

@Composable
fun ProgressSection(
    title: String,
    percentage: Float,
    hours: Float,
    color: Color
) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Bottom
        ) {
            Text(title, style = MaterialTheme.typography.labelLarge, color = ShFog)
            Text(
                text = String.format(Locale.getDefault(), "%.1fh", hours),
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Bold,
                color = color
            )
        }
        Spacer(Modifier.height(12.dp))
        LinearProgressIndicator(
            progress = { percentage / 100f },
            modifier = Modifier
                .fillMaxWidth()
                .height(12.dp)
                .clip(RoundedCornerShape(6.dp)),
            color = color,
            trackColor = ShLine
        )
    }
}

@Composable
fun MotivationalMessage(timeRemaining: TimeRemaining) {
    val (text, icon) = when {
        timeRemaining.percentDayRemaining > 75 -> "🌅 Fresh start! Make it count!" to "🎯"
        timeRemaining.percentDayRemaining > 50 -> "💪 Keep pushing! Half day left!" to "🔥"
        timeRemaining.percentDayRemaining > 25 -> "⏳ Final stretch! You've got this!" to "⚡"
        else -> "🌙 Night's coming soon! Last push!" to "🛌"
    }

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(icon, fontSize = 40.sp)
        Spacer(Modifier.height(12.dp))
        Text(
            text = text,
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Medium,
            textAlign = TextAlign.Center,
            color = ShInk
        )
    }
}

fun getColorForPercentage(percentage: Float): Color {
    return when {
        percentage > 75 -> ShMatcha      // Green
        percentage > 50 -> Color(0xFFFFC107)      // Yellow
        percentage > 25 -> Color(0xFFFF9800)      // Orange
        else -> ShVermillion                 // Red
    }
}
