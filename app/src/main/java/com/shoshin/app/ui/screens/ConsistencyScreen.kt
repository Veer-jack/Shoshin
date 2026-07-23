package com.Shoshin.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.Shoshin.app.R
import com.Shoshin.app.ui.components.*
import com.Shoshin.app.ui.theme.*

@Composable
fun ConsistencyScreen(navController: NavController) {
    // These should ideally come from a ViewModel/Repository
    var streak by remember { mutableIntStateOf(0) }
    var bestStreak by remember { mutableIntStateOf(0) }
    val dailyData = remember { IntArray(30) { 0 } } // Empty data

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
    ) {
        // Header (Uses sh_ink which stays dark as per design intent for this specific header)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(ShInk)
                .padding(horizontal = 24.dp, vertical = 32.dp)
        ) {
            Column {
                Kicker("Practice Stats", color = ShVermillion)
                Spacer(Modifier.height(8.dp))
                Text(
                    "Consistency",
                    fontSize = 32.sp,
                    fontWeight = FontWeight.SemiBold,
                    fontFamily = CormorantFamily,
                    color = Color.White
                )
                Text(
                    "Keep the momentum going",
                    fontSize = 15.sp,
                    color = ShFog,
                    fontFamily = DmSansFamily,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
        }

        Column(modifier = Modifier.padding(24.dp)) {
            if (streak == 0 && dailyData.all { it == 0 }) {
                EdgeLayout(
                    icon = R.drawable.ic_pulse,
                    kicker = "Day One is the hardest",
                    title = "A beginning",
                    body = "Your consistency heatmap will appear here once you complete your first morning practice.",
                    actionLabel = "Begin Morning Practice",
                    onAction = { navController.navigate("morning/activation") }
                )
            } else {
                // Current streak card
                ShoshinCard(modifier = Modifier.fillMaxWidth()) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("CURRENT STREAK", style = ShKickerStyle, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Spacer(Modifier.height(8.dp))
                        Row(verticalAlignment = Alignment.Bottom) {
                            Text(
                                "$streak",
                                fontSize = 64.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = CormorantFamily,
                                color = ShVermillion,
                                lineHeight = 64.sp
                            )
                            Spacer(Modifier.width(8.dp))
                            Text(
                                "days", 
                                fontSize = 18.sp, 
                                color = MaterialTheme.colorScheme.onSurfaceVariant, 
                                fontFamily = DmSansFamily,
                                modifier = Modifier.padding(bottom = 12.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(24.dp))
                        HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f), thickness = 1.dp)
                        Spacer(modifier = Modifier.height(24.dp))
                        
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            StatBox("BEST", "$bestStreak days")
                            StatBox("TOTAL", "${streak * 2} days")
                            StatBox("PRACTICES", "26")
                        }
                    }
                }

                // Milestones
                Spacer(modifier = Modifier.height(32.dp))
                Kicker("Milestones", color = MaterialTheme.colorScheme.onBackground)

                Spacer(modifier = Modifier.height(16.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    MilestoneBox("7 DAY", "Achieved", ShMatcha, Modifier.weight(1f))
                    MilestoneBox("21 DAY", "In Progress", MaterialTheme.colorScheme.surfaceVariant, Modifier.weight(1f))
                    MilestoneBox("71 DAY", "Locked", MaterialTheme.colorScheme.surfaceVariant, Modifier.weight(1f))
                }

                // Calendar heatmap
                Spacer(modifier = Modifier.height(32.dp))
                Kicker("Last 30 Days", color = MaterialTheme.colorScheme.onBackground)

                Spacer(modifier = Modifier.height(16.dp))
                ShoshinCard(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        repeat(6) { week ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(bottom = 6.dp),
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                repeat(5) { day ->
                                    val index = week * 5 + day
                                    if (index < dailyData.size) {
                                        val color = if (dailyData[index] == 1) ShMatcha else MaterialTheme.colorScheme.surfaceVariant
                                        Box(
                                            modifier = Modifier
                                                .weight(1f)
                                                .aspectRatio(1f)
                                                .background(color, RoundedCornerShape(6.dp))
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(48.dp))
        }
    }
}

@Composable
fun StatBox(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(label, style = ShKickerStyle.copy(fontSize = 9.sp), color = MaterialTheme.colorScheme.onSurfaceVariant)
        Spacer(Modifier.height(4.dp))
        Text(value, fontSize = 16.sp, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onSurface, fontFamily = DmSansFamily)
    }
}

@Composable
fun MilestoneBox(label: String, status: String, color: Color, modifier: Modifier = Modifier) {
    val isMatcha = color == ShMatcha
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(if (isMatcha) ShMatchaLight else MaterialTheme.colorScheme.surfaceVariant)
            .border(1.dp, if (isMatcha) ShMatcha.copy(alpha = 0.2f) else MaterialTheme.colorScheme.outline, RoundedCornerShape(16.dp))
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(label, style = ShKickerStyle.copy(fontSize = 10.sp), color = if (isMatcha) ShMatcha else MaterialTheme.colorScheme.onSurface)
            Spacer(Modifier.height(4.dp))
            Text(status, fontSize = 11.sp, color = if (isMatcha) ShMatcha else MaterialTheme.colorScheme.onSurfaceVariant, fontFamily = DmSansFamily)
        }
    }
}
