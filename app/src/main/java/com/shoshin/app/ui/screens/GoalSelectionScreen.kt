package com.Shoshin.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.Shoshin.app.R
import com.Shoshin.app.ui.components.*
import com.Shoshin.app.ui.theme.*

@Composable
fun GoalSelectionScreen(onContinue: (String) -> Unit) {
    var selectedGoal by remember { mutableStateOf<String?>(null) }

    ShoshinTheme(type = ShoshinThemeType.DYNAMIC) {
        Scaffold(
            containerColor = MaterialTheme.colorScheme.background,
            bottomBar = {
                Box(modifier = Modifier.padding(24.dp).navigationBarsPadding()) {
                    ShoshinButton(
                        onClick = {
                            selectedGoal?.let { onContinue(it) }
                        },
                        variant = ShButtonVariant.Accent,
                        modifier = Modifier.fillMaxWidth(),
                        enabled = selectedGoal != null,
                        trailingIcon = {
                            Icon(painterResource(R.drawable.ic_arrow_right), null, modifier = Modifier.size(18.dp))
                        }
                    ) {
                        Text("Continue", fontWeight = FontWeight.Bold)
                    }
                }
            }
        ) { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 24.dp)
            ) {
                Spacer(Modifier.height(40.dp))
                
                Kicker("A BEGINNING · 1 OF 2", color = MaterialTheme.colorScheme.onSurfaceVariant)
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Text(
                    text = "What are you\npracticing for?",
                    style = ShTitleStyle.copy(fontSize = 32.sp, lineHeight = 38.sp),
                    color = MaterialTheme.colorScheme.onBackground
                )
                
                Spacer(modifier = Modifier.height(32.dp))

                GoalOption(
                    title = "Crack the exam",
                    subtitle = "Deep study before the world wakes",
                    iconRes = R.drawable.ic_book,
                    isSelected = selectedGoal == "study",
                    onClick = { selectedGoal = "study" }
                )
                Spacer(modifier = Modifier.height(14.dp))
                
                GoalOption(
                    title = "Get fit",
                    subtitle = "Move first, decide later",
                    iconRes = R.drawable.ic_dumbbell,
                    isSelected = selectedGoal == "gym",
                    onClick = { selectedGoal = "gym" }
                )
                Spacer(modifier = Modifier.height(14.dp))
                
                GoalOption(
                    title = "Morning walk",
                    subtitle = "Step gently into the day",
                    iconRes = R.drawable.ic_walk,
                    isSelected = selectedGoal == "walk",
                    onClick = { selectedGoal = "walk" }
                )
                Spacer(modifier = Modifier.height(14.dp))
                
                GoalOption(
                    title = "Build a routine",
                    subtitle = "Show up, every single day",
                    iconRes = R.drawable.ic_sun,
                    isSelected = selectedGoal == "routine",
                    onClick = { selectedGoal = "routine" }
                )
                Spacer(modifier = Modifier.height(14.dp))
                
                GoalOption(
                    title = "Create your own goal",
                    subtitle = "Name the checkpoints yourself",
                    iconRes = R.drawable.ic_edit,
                    isSelected = selectedGoal == "custom",
                    onClick = { selectedGoal = "custom" }
                )
                
                Spacer(modifier = Modifier.height(40.dp))
            }
        }
    }
}

@Composable
private fun GoalOption(
    title: String,
    subtitle: String,
    iconRes: Int,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val isDark = MaterialTheme.colorScheme.background == ShNight
    val borderColor = if (isSelected) {
        if (isDark) Color.White else MaterialTheme.colorScheme.onBackground
    } else {
        MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
    }
    val borderWith = if (isSelected) 1.5.dp else 1.dp
    
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .border(borderWith, borderColor, RoundedCornerShape(20.dp))
            .clickable { onClick() }
            .padding(18.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            // Icon Chip
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(if (isSelected && !isDark) MaterialTheme.colorScheme.onBackground else MaterialTheme.colorScheme.background.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(iconRes),
                    contentDescription = null,
                    modifier = Modifier.size(20.dp),
                    tint = if (isSelected && !isDark) MaterialTheme.colorScheme.background else MaterialTheme.colorScheme.onBackground
                )
            }
            
            Spacer(Modifier.width(16.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = ShH2Style.copy(fontSize = 16.sp),
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = subtitle,
                    style = ShLabelStyle.copy(fontSize = 13.sp),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            
            // Selection Circle
            Box(
                modifier = Modifier
                    .size(22.dp)
                    .clip(CircleShape)
                    .background(if (isSelected) (if (isDark) Color.White else MaterialTheme.colorScheme.onBackground) else Color.Transparent)
                    .border(1.5.dp, if (isSelected) (if (isDark) Color.White else MaterialTheme.colorScheme.onBackground) else MaterialTheme.colorScheme.outline, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                if (isSelected) {
                    Icon(
                        painter = painterResource(R.drawable.ic_check),
                        contentDescription = null,
                        modifier = Modifier.size(12.dp),
                        tint = MaterialTheme.colorScheme.background
                    )
                }
            }
        }
    }
}
