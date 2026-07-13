package com.example.shoshinapp.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.shoshinapp.navigation.ShRoutes
import com.example.shoshinapp.R
import com.example.shoshinapp.ui.components.*
import com.example.shoshinapp.ui.theme.*
import com.example.shoshinapp.viewmodel.StreakViewModel
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun StreakDetailsScreen(
    navController: NavController,
    viewModel: StreakViewModel
) {
    val user by viewModel.user.collectAsState()
    val scrollState = rememberScrollState()

    user?.let { u ->
        val streakColor = viewModel.getStreakColor(u.currentStreak)
        val badges = viewModel.getMilestoneBadges(u.currentStreak)

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(ShPaper)
                .verticalScroll(scrollState)
        ) {
            // App Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(painterResource(R.drawable.ic_arrow_left), contentDescription = "Back", tint = ShInk)
                }
                Spacer(Modifier.width(16.dp))
                Text("Streak Details", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            }

            // Hero Streak Display
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 32.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "🔥 ${u.currentStreak}",
                    fontSize = 72.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = DmSansFamily,
                    color = streakColor
                )
                Text(
                    text = "DAYS IN A ROW",
                    style = ShKickerStyle,
                    color = ShFog
                )
            }

            // Stats Card
            ShoshinCard(modifier = Modifier.padding(horizontal = 24.dp)) {
                Column(modifier = Modifier.padding(20.dp)) {
                    DetailRow(label = "Best Streak", value = "${u.bestStreak} days")
                    HorizontalDivider(color = ShLine, modifier = Modifier.padding(vertical = 12.dp))
                    DetailRow(
                        label = "Started", 
                        value = if (u.streakStartDate > 0) SimpleDateFormat("MMM d, yyyy").format(Date(u.streakStartDate)) else "Not started"
                    )
                    HorizontalDivider(color = ShLine, modifier = Modifier.padding(vertical = 12.dp))
                    DetailRow(
                        label = "Last Checkpoint", 
                        value = if (u.lastCheckpointDate > 0) SimpleDateFormat("MMM d, HH:mm").format(Date(u.lastCheckpointDate)) else "None"
                    )
                }
            }

            Spacer(Modifier.height(24.dp))

            // Badges Section
            if (badges.isNotEmpty()) {
                Kicker("Earned Badges", modifier = Modifier.padding(horizontal = 24.dp))
                Spacer(Modifier.height(12.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    badges.forEach { badge ->
                        Box(
                            modifier = Modifier
                                .size(64.dp)
                                .background(ShSand, MaterialTheme.shapes.medium),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(badge, fontSize = 32.sp)
                        }
                    }
                }
                Spacer(Modifier.height(32.dp))
            }

            // Action
            ShoshinButton(
                onClick = { 
                    navController.navigate(ShRoutes.streakShare(u.currentStreak, "Morning Routine", u.streakStartDate))
                },
                variant = ShButtonVariant.Accent,
                modifier = Modifier.padding(horizontal = 24.dp)
            ) {
                Text("Share Your Progress")
            }
            
            Spacer(Modifier.height(48.dp))
        }
    }
}

@Composable
private fun DetailRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, style = ShBodyStyle, color = ShFog)
        Text(value, style = ShBodyStyle, fontWeight = FontWeight.SemiBold, color = ShInk)
    }
}
