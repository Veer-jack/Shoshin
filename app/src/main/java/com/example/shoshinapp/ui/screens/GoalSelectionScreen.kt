package com.example.shoshinapp.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.shoshinapp.R
import com.example.shoshinapp.ui.components.*
import com.example.shoshinapp.ui.theme.*

@Composable
fun GoalSelectionScreen(onContinue: (String) -> Unit) {
    var selectedGoal by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(ShPaper)
            .verticalScroll(rememberScrollState())
            .padding(24.dp)
    ) {
        Kicker(stringResource(R.string.goal_kicker), color = ShVermillion)
        Spacer(modifier = Modifier.height(10.dp))
        Text(
            stringResource(R.string.goal_title),
            fontSize = 34.sp,
            fontWeight = FontWeight.SemiBold,
            fontFamily = CormorantFamily,
            color = ShInk,
            lineHeight = 38.sp
        )
        Spacer(modifier = Modifier.height(32.dp))

        GoalOption(
            title = stringResource(R.string.goal_exam_title),
            subtitle = stringResource(R.string.goal_exam_sub),
            isSelected = selectedGoal == "study",
            onClick = { selectedGoal = "study" }
        )
        Spacer(modifier = Modifier.height(16.dp))
        GoalOption(
            title = stringResource(R.string.goal_fit_title),
            subtitle = stringResource(R.string.goal_fit_sub),
            isSelected = selectedGoal == "gym",
            onClick = { selectedGoal = "gym" }
        )
        Spacer(modifier = Modifier.height(16.dp))
        GoalOption(
            title = stringResource(R.string.goal_walk_title),
            subtitle = stringResource(R.string.goal_walk_sub),
            isSelected = selectedGoal == "walk",
            onClick = { selectedGoal = "walk" }
        )
        Spacer(modifier = Modifier.height(16.dp))
        GoalOption(
            title = stringResource(R.string.goal_routine_title),
            subtitle = stringResource(R.string.goal_routine_sub),
            isSelected = selectedGoal == "routine",
            onClick = { selectedGoal = "routine" }
        )

        Spacer(modifier = Modifier.weight(1f))
        Spacer(modifier = Modifier.height(32.dp))

        ShoshinButton(
            onClick = {
                selectedGoal?.let { onContinue(it) }
            },
            enabled = selectedGoal != null
        ) {
            Text(stringResource(R.string.onboard_continue))
        }
    }
}

@Composable
fun GoalOption(
    title: String,
    subtitle: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    ShoshinCard(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
    ) {
        Row(
            modifier = Modifier
                .padding(20.dp),
            verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(title, style = ShH2Style, color = if (isSelected) ShVermillion else ShInk)
                Text(subtitle, style = ShLabelStyle, color = ShFog)
            }
            RadioButton(
                selected = isSelected,
                onClick = onClick,
                colors = RadioButtonDefaults.colors(selectedColor = ShVermillion, unselectedColor = ShLine2)
            )
        }
    }
}
