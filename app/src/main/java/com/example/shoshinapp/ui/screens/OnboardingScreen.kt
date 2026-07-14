package com.example.shoshinapp.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.shoshinapp.R
import com.example.shoshinapp.ui.components.*
import com.example.shoshinapp.ui.theme.*
import com.example.shoshinapp.viewmodel.OnboardingViewModel

@Composable
fun OnboardingScreen(
    viewModel: OnboardingViewModel,
    onComplete: () -> Unit
) {
    var currentStep by remember { mutableStateOf(1) }
    var startTime by remember { mutableStateOf("06:00") }
    var endTime by remember { mutableStateOf("22:00") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(ShPaper)
            .systemBarsPadding()
    ) {
        // Header with Skip
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Shoshin", style = ShTitleStyle.copy(fontSize = 24.sp))
            if (currentStep < 3) {
                TextButton(onClick = { 
                    viewModel.skipOnboarding()
                    onComplete()
                }) {
                    Text("Skip", color = ShFog)
                }
            }
        }

        Box(modifier = Modifier.weight(1f)) {
            when (currentStep) {
                1 -> OnboardingStep1()
                2 -> OnboardingStep2()
                3 -> OnboardingStep3(
                    startTime = startTime,
                    endTime = endTime,
                    onStartTimeChange = { startTime = it },
                    onEndTimeChange = { endTime = it }
                )
            }
        }

        // Footer
        Column(modifier = Modifier.padding(24.dp)) {
            // Progress Dots
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                repeat(3) { i ->
                    Box(
                        modifier = Modifier
                            .padding(horizontal = 4.dp)
                            .size(if (currentStep == i + 1) 10.dp else 8.dp)
                            .clip(CircleShape)
                            .background(if (currentStep == i + 1) ShInk else ShSand)
                    )
                }
            }
            
            Spacer(Modifier.height(24.dp))
            
            ShoshinButton(
                onClick = {
                    if (currentStep < 3) {
                        currentStep++
                    } else {
                        viewModel.completeOnboarding(startTime, endTime)
                        onComplete()
                    }
                },
                variant = ShButtonVariant.Primary
            ) {
                Text(if (currentStep == 3) "START →" else "NEXT →")
            }
        }
    }
}

@Composable
fun OnboardingStep1() {
    Column(
        modifier = Modifier.padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(Modifier.height(48.dp))
        Text(
            "START YOUR DAY\nWITH INTENTION",
            style = ShTitleStyle,
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(16.dp))
        Text(
            "Create a morning routine that matters. Build habits that stick. Celebrate your progress with friends.",
            style = ShBodyStyle,
            textAlign = TextAlign.Center,
            color = ShFog
        )
        
        Spacer(Modifier.height(48.dp))
        
        BenefitRow(R.drawable.ic_flame, "Build Streaks", "Consistency builds momentum")
        BenefitRow(R.drawable.ic_progress, "Track Progress", "See your improvements")
        BenefitRow(R.drawable.ic_groups, "Share Wins", "Celebrate with community")
    }
}

@Composable
fun OnboardingStep2() {
    Column(modifier = Modifier.padding(horizontal = 24.dp)) {
        Spacer(Modifier.height(48.dp))
        Text(
            "HERE'S HOW IT WORKS",
            style = ShTitleStyle,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(32.dp))
        
        HowItWorksRow(1, R.drawable.ic_sun, "Set Your Intention", "Choose morning goal")
        HowItWorksRow(2, R.drawable.ic_check, "Complete Checkpoints", "Daily small wins")
        HowItWorksRow(3, R.drawable.ic_flame, "Build Your Streak", "Consistency rewarded")
        HowItWorksRow(4, R.drawable.ic_share, "Share Progress", "Celebrate publicly")
    }
}

@Composable
fun OnboardingStep3(
    startTime: String,
    endTime: String,
    onStartTimeChange: (String) -> Unit,
    onEndTimeChange: (String) -> Unit
) {
    var showStartTimePicker by remember { mutableStateOf(false) }
    var showEndTimePicker by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier.padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(Modifier.height(48.dp))
        Text(
            "WHEN DO YOU DO YOUR BEST?",
            style = ShTitleStyle,
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(16.dp))
        Text(
            "Set your productive hours so we can remind you when it matters most.",
            style = ShBodyStyle,
            textAlign = TextAlign.Center,
            color = ShFog
        )
        
        Spacer(Modifier.height(48.dp))
        
        Icon(painterResource(R.drawable.ic_clock), contentDescription = null, modifier = Modifier.size(80.dp), tint = ShInk)
        
        Spacer(Modifier.height(32.dp))
        
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            TimeBox(label = "Start time", time = startTime, modifier = Modifier.weight(1f)) {
                showStartTimePicker = true
            }
            TimeBox(label = "End time", time = endTime, modifier = Modifier.weight(1f)) {
                showEndTimePicker = true
            }
        }
        
        Spacer(Modifier.height(24.dp))
        Text("Your reminders: $startTime - $endTime", style = ShLabelStyle, color = ShFog)
    }

    if (showStartTimePicker) {
        val parts = startTime.split(":")
        TimePickerDialog(
            onDismiss = { showStartTimePicker = false },
            onTimeSelected = { h, m -> 
                onStartTimeChange(String.format("%02d:%02d", h, m))
                showStartTimePicker = false
            },
            title = "Start Time",
            initialHour = parts[0].toInt(),
            initialMinute = parts[1].toInt()
        )
    }
    
    if (showEndTimePicker) {
        val parts = endTime.split(":")
        TimePickerDialog(
            onDismiss = { showEndTimePicker = false },
            onTimeSelected = { h, m -> 
                onEndTimeChange(String.format("%02d:%02d", h, m))
                showEndTimePicker = false
            },
            title = "End Time",
            initialHour = parts[0].toInt(),
            initialMinute = parts[1].toInt()
        )
    }
}

@Composable
private fun BenefitRow(iconRes: Int, title: String, subtitle: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .background(ShSand, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(painterResource(iconRes), contentDescription = null, tint = ShInk, modifier = Modifier.size(24.dp))
        }
        Spacer(Modifier.width(16.dp))
        Column {
            Text(title, fontWeight = FontWeight.Bold, fontSize = 16.sp)
            Text(subtitle, fontSize = 14.sp, color = ShFog)
        }
    }
}

@Composable
private fun HowItWorksRow(step: Int, iconRes: Int, title: String, subtitle: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text("STEP $step", style = ShKickerStyle, modifier = Modifier.width(60.dp))
        Spacer(Modifier.width(8.dp))
        Icon(painterResource(iconRes), contentDescription = null, tint = ShInk, modifier = Modifier.size(24.dp))
        Spacer(Modifier.width(16.dp))
        Column {
            Text(title, fontWeight = FontWeight.Bold, fontSize = 16.sp)
            Text(subtitle, fontSize = 14.sp, color = ShFog)
        }
    }
}

@Composable
private fun TimeBox(label: String, time: String, modifier: Modifier = Modifier, onClick: () -> Unit) {
    Column(modifier = modifier) {
        Text(label, style = ShLabelStyle, color = ShFog)
        Spacer(Modifier.height(8.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .background(ShSand, RoundedCornerShape(12.dp))
                .clickable { onClick() }
                .padding(horizontal = 16.dp),
            contentAlignment = Alignment.CenterStart
        ) {
            Text(time, fontWeight = FontWeight.Bold, fontSize = 18.sp)
        }
    }
}
