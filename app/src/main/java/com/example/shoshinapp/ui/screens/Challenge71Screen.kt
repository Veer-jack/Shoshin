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
fun Challenge71Screen(navController: NavController) {
    ShoshinTheme(darkSurface = true) {
        Box(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
            EdgeLayout(
                icon = R.drawable.ic_trophy,
                kicker = "Habit Mastered",
                title = "71-Day Discipline",
                body = "Unstoppable. You have mastered the art of beginning again. This mark is yours forever.",
                actionLabel = "Celebrate Mastery",
                onAction = { navController.popBackStack() }
            )
        }
    }
}
