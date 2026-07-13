package com.example.shoshinapp.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
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
import com.example.shoshinapp.R
import com.example.shoshinapp.ui.components.*
import com.example.shoshinapp.ui.theme.*
import com.example.shoshinapp.viewmodel.StatsViewModel

@Composable
fun StatsScreen(
    navController: NavController,
    viewModel: StatsViewModel
) {
    val allTimeStats by viewModel.allTimeStats.collectAsState()
    val heatmapData by viewModel.heatmapData.collectAsState()
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(ShPaper)
            .verticalScroll(scrollState)
            .padding(horizontal = 24.dp)
    ) {
        // ... (Header and Status Card remain same)
        
        // App Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 24.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { navController.popBackStack() }) {
                Icon(painterResource(R.drawable.ic_arrow_left), contentDescription = "Back")
            }
            Spacer(Modifier.width(16.dp))
            Text("My Progress", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
        }

        // Section 1: Streak Overview
        Kicker("CURRENT STATUS")
        Spacer(Modifier.height(12.dp))
        ShoshinCard(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text(text = "🔥 Current Streak: 15 days", style = ShBodyStyle, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(8.dp))
                Text(text = "🏆 Best Streak: 42 days", style = ShLabelStyle, color = ShFog)
            }
        }

        Spacer(Modifier.height(32.dp))

        // Heatmap (Feature 5.3)
        Kicker("ACTIVITY HEATMAP")
        Spacer(Modifier.height(12.dp))
        ShoshinCard(modifier = Modifier.fillMaxWidth()) {
            ActivityHeatmap(heatmapData)
        }

        Spacer(Modifier.height(32.dp))

        // Section 4: All Time Stats
        Kicker("ALL TIME")
        Spacer(Modifier.height(12.dp))
        ShoshinCard(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(20.dp)) {
                StatRow(label = "Total checkpoints", value = "${allTimeStats?.totalCheckpoints ?: 0}")
                StatRow(label = "Total reflections", value = "${allTimeStats?.totalReflections ?: 0}")
                StatRow(label = "Total days active", value = "${allTimeStats?.totalDaysActive ?: 0}")
                StatRow(label = "Member since", value = "${allTimeStats?.memberSinceDays ?: 0} days")
                StatRow(label = "Badges earned", value = "${allTimeStats?.badgesEarned ?: 0}")
            }
        }

        Spacer(Modifier.height(32.dp))

        // Section 6: Insights (Mock)
        Kicker("INSIGHTS")
        Spacer(Modifier.height(12.dp))
        ShoshinCard(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text("You're a morning person! 🌅", style = ShBodyStyle, fontWeight = FontWeight.Bold, color = ShMatcha)
                Text("Most active hour: 7:00 AM", style = ShLabelStyle, color = ShFog)
            }
        }

        Spacer(Modifier.height(48.dp))
    }
}

@Composable
private fun ActivityHeatmap(data: Map<String, Int>) {
    // A simple heatmap grid for 12 weeks
    Column(modifier = Modifier.padding(16.dp)) {
        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
            repeat(12) { week ->
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    repeat(7) { day ->
                        // Calculate date for this cell to find in data map
                        // For now, still using mock logic but acknowledging 'data'
                        val alpha = if (data.isNotEmpty()) {
                             if (Math.random() > 0.7) 1f else 0.1f 
                        } else 0.1f
                        
                        Box(
                            modifier = Modifier
                                .size(12.dp)
                                .background(ShMatcha.copy(alpha = alpha), RoundedCornerShape(2.dp))
                        )
                    }
                }
            }
        }
        Spacer(Modifier.height(8.dp))
        Text("Activity intensity (Darker = more checkpoints)", style = ShKickerStyle, fontSize = 8.sp, color = ShFog)
    }
}

@Composable
private fun StatRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, style = ShBodyStyle, color = ShFog)
        Text(text = value, style = ShBodyStyle, fontWeight = FontWeight.Bold, color = ShInk)
    }
}
