package com.example.shoshinapp.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import com.example.shoshinapp.R
import com.example.shoshinapp.ui.components.*
import com.example.shoshinapp.ui.theme.*

@Composable
fun Challenge21Screen(navController: NavController) {
    Box(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
        EdgeLayout(
            icon = R.drawable.ic_check,
            kicker = "Habit Built",
            title = "21-Day Challenge",
            body = "You've successfully crossed the foundation phase. The habit is now part of who you are.",
            actionLabel = "Continue the journey",
            onAction = { navController.popBackStack() }
        )
    }
}
