package com.shoshin.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import com.shoshin.app.R
import com.shoshin.app.ui.components.*
import com.shoshin.app.ui.theme.*

@Composable
fun HistoryScreen(navController: NavController) {
    val miss = listOf(4, 11, 19)
    val today = 24
    val firstDow = 6 // Saturday
    val dow = listOf("S", "M", "T", "W", "T", "F", "S")
    
    val cells = mutableListOf<Int?>()
    repeat(firstDow) { cells.add(null) }
    for (d in 1..30) { cells.add(d) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp)
    ) {
        // App Bar
        Row(
            modifier = Modifier.fillMaxWidth().padding(top = 16.dp, bottom = 22.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            IconButton(onClick = { navController.popBackStack() }, modifier = Modifier.size(24.dp)) {
                Icon(painterResource(R.drawable.ic_arrow_left), contentDescription = "Back", tint = MaterialTheme.colorScheme.onBackground)
            }
            Text("June 2026", style = ShH2Style, color = MaterialTheme.colorScheme.onBackground)
            Icon(painterResource(R.drawable.ic_arrow_right), null, modifier = Modifier.size(22.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f))
        }

        // Month Summary
        Row(modifier = Modifier.fillMaxWidth().padding(bottom = 20.dp), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            SummaryCard(value = "21", label = "Kept", color = ShMatcha, modifier = Modifier.weight(1f))
            SummaryCard(value = "3", label = "Missed", color = ShVermillion, modifier = Modifier.weight(1f))
            SummaryCard(value = "88", unit = "%", label = "Rate", modifier = Modifier.weight(1f))
        }

        // Calendar Grid
        ShoshinCard(modifier = Modifier.fillMaxWidth().padding(bottom = 18.dp)) {
            Column(modifier = Modifier.padding(18.dp)) {
                // Day Labels
                Row(modifier = Modifier.fillMaxWidth().padding(bottom = 10.dp)) {
                    dow.forEach { day ->
                        Text(
                            text = day,
                            modifier = Modifier.weight(1f),
                            textAlign = TextAlign.Center,
                            style = ShLabelStyle.copy(fontSize = 11.sp, fontWeight = FontWeight.SemiBold),
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                        )
                    }
                }
                
                // Day Cells
                cells.chunked(7).forEach { row ->
                    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 3.dp)) {
                        row.forEach { day ->
                            Box(modifier = Modifier.weight(1f).aspectRatio(1f), contentAlignment = Alignment.Center) {
                                if (day != null) {
                                    val isMiss = miss.contains(day)
                                    val isToday = day == today
                                    val isFuture = day > today
                                    val kept = !isMiss && !isFuture

                                    Box(
                                        modifier = Modifier
                                            .fillMaxSize(0.85f)
                                            .clip(RoundedCornerShape(9.dp))
                                            .background(if (kept) ShMatcha else Color.Transparent)
                                            .border(
                                                width = if (isToday) 2.dp else if (isMiss) 1.5.dp else 0.dp,
                                                color = if (isToday) ShVermillion else if (isMiss) MaterialTheme.colorScheme.outline else Color.Transparent,
                                                shape = RoundedCornerShape(9.dp)
                                            ),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = day.toString(),
                                            style = ShNumeralStyle.copy(fontSize = 12.sp),
                                            color = when {
                                                kept -> Color.White
                                                isToday -> ShVermillion
                                                else -> MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                                            }
                                        )
                                    }
                                }
                            }
                        }
                        // Fill empty spots in last row
                        if (row.size < 7) {
                            repeat(7 - row.size) { Spacer(Modifier.weight(1f)) }
                        }
                    }
                }
            }
        }

        // Day Detail
        Kicker("Friday, 23 June", modifier = Modifier.padding(start = 4.dp, bottom = 12.dp))
        ShoshinCard(modifier = Modifier.fillMaxWidth().padding(bottom = 24.dp)) {
            Column(modifier = Modifier.padding(20.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        Icon(painterResource(R.drawable.ic_walk), null, modifier = Modifier.size(20.dp), tint = MaterialTheme.colorScheme.onBackground)
                        Text("Morning Walk", style = ShH2Style, color = MaterialTheme.colorScheme.onBackground)
                    }
                    ShoshinPill(label = "Kept", variant = ShPillVariant.Matcha)
                }
                
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    ShoshinStat(value = "05:31", label = "Started")
                    ShoshinStat(value = "20", unit = "min", label = "Bridge")
                    ShoshinStat(value = "5/5", label = "Checkpoints", color = ShMatcha)
                }
            }
        }
        
        Spacer(Modifier.height(40.dp))
    }
}

@Composable
private fun SummaryCard(value: String, unit: String? = null, label: String, color: Color = Color.Unspecified, modifier: Modifier = Modifier) {
    val finalColor = if (color == Color.Unspecified) MaterialTheme.colorScheme.onBackground else color
    ShoshinCard(modifier = modifier) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.Bottom) {
                Text(value, style = ShNumeralStyle.copy(fontSize = 24.sp), color = finalColor)
                if (unit != null) {
                    Text(unit, style = ShNumeralStyle.copy(fontSize = 12.sp), color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.padding(bottom = 3.dp, start = 2.dp))
                }
            }
            Kicker(label, modifier = Modifier.padding(top = 3.dp))
        }
    }
}
