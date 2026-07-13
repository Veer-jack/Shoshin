package com.example.shoshinapp.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.shoshinapp.ui.theme.*

@Composable
fun GroupStatsSection(
    activeCount: Int,
    totalCount: Int,
    avgStreak: Double,
    checkpointsThisMonth: Int
) {
    ShoshinCard(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(20.dp)) {
            Kicker("GROUP STATS")
            Spacer(Modifier.height(16.dp))
            
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                StatItem(label = "Active This Week", value = "$activeCount of $totalCount")
                StatItem(label = "Average Streak", value = "${avgStreak.toInt()} days")
            }
            
            HorizontalDivider(color = ShLine, modifier = Modifier.padding(vertical = 12.dp))
            
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                StatItem(label = "Total Checkpoints", value = "$checkpointsThisMonth this month")
                StatItem(label = "Group Founded", value = "2 months ago")
            }
        }
    }
}

@Composable
private fun StatItem(label: String, value: String) {
    Column {
        Text(text = label, style = ShLabelStyle, color = ShFog)
        Text(text = value, style = ShBodyStyle, fontWeight = FontWeight.Bold, color = ShInk)
    }
}
