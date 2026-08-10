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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.Shoshin.app.R
import com.Shoshin.app.ui.components.*
import com.Shoshin.app.ui.theme.*
import com.Shoshin.app.viewmodel.StreakViewModel
import java.util.*
import java.text.SimpleDateFormat

private val dateKeyFmt = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

@Composable
fun HistoryScreen(
    navController: NavController,
    streakViewModel: StreakViewModel? = null
) {
    val historyByDate by streakViewModel?.historyByDate?.collectAsState(initial = emptyMap())
        ?: remember { mutableStateOf(emptyMap<String, Boolean>()) }

    var selectedMonth by remember { mutableStateOf(Calendar.getInstance()) }
    val todayKey = remember { dateKeyFmt.format(Date()) }
    var selectedDateKey by remember { mutableStateOf(todayKey) }

    // Kept/missed/rate for the visible month — "missed" = an elapsed day with no logged
    // completion; days that haven't happened yet aren't counted either way.
    val monthStats = remember(selectedMonth.get(Calendar.YEAR), selectedMonth.get(Calendar.MONTH), historyByDate) {
        val cal = selectedMonth.clone() as Calendar
        cal.set(Calendar.DAY_OF_MONTH, 1)
        val daysInMonth = cal.getActualMaximum(Calendar.DAY_OF_MONTH)
        var kept = 0
        var missed = 0
        for (day in 1..daysInMonth) {
            cal.set(Calendar.DAY_OF_MONTH, day)
            val key = dateKeyFmt.format(cal.time)
            when {
                historyByDate[key] == true -> kept++
                key < todayKey -> missed++
                // today or future: not counted
            }
        }
        val elapsed = kept + missed
        val rate = if (elapsed > 0) (kept * 100 / elapsed) else 0
        Triple(kept, missed, rate)
    }

    ShoshinTheme(type = ShoshinThemeType.DYNAMIC) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .statusBarsPadding()
        ) {
            // App Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(painterResource(R.drawable.ic_arrow_left), null, tint = MaterialTheme.colorScheme.onBackground)
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = {
                        val cal = selectedMonth.clone() as Calendar
                        cal.add(Calendar.MONTH, -1)
                        selectedMonth = cal
                    }, modifier = Modifier.size(28.dp)) {
                        Icon(painterResource(R.drawable.ic_arrow_left), null, tint = MaterialTheme.colorScheme.onBackground, modifier = Modifier.size(16.dp))
                    }
                    val monthName = SimpleDateFormat("MMMM yyyy", Locale.getDefault()).format(selectedMonth.time)
                    Text(monthName, style = ShH2Style, color = MaterialTheme.colorScheme.onBackground)
                    IconButton(onClick = {
                        val cal = selectedMonth.clone() as Calendar
                        cal.add(Calendar.MONTH, 1)
                        selectedMonth = cal
                    }, modifier = Modifier.size(28.dp)) {
                        Icon(painterResource(R.drawable.ic_arrow_right), null, tint = MaterialTheme.colorScheme.onBackground, modifier = Modifier.size(16.dp))
                    }
                }

                Spacer(Modifier.width(48.dp)) // Placeholder for balance
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 24.dp)
            ) {
                // Summary Stats
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    HistoryStatCard(value = monthStats.first.toString(), label = "KEPT", color = ShMatchaDark, modifier = Modifier.weight(1f))
                    HistoryStatCard(value = monthStats.second.toString(), label = "MISSED", color = ShVermillion, modifier = Modifier.weight(1f))
                    HistoryStatCard(value = "${monthStats.third}%", label = "RATE", color = MaterialTheme.colorScheme.onSurface, modifier = Modifier.weight(1f))
                }

                Spacer(Modifier.height(24.dp))

                // Calendar Card
                ShoshinCard(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        CalendarGrid(
                            month = selectedMonth,
                            historyByDate = historyByDate,
                            todayKey = todayKey,
                            selectedDateKey = selectedDateKey,
                            onSelectDate = { selectedDateKey = it }
                        )
                    }
                }

                Spacer(Modifier.height(32.dp))

                // Detail Section
                val selectedDateLabel = remember(selectedDateKey) {
                    try {
                        val parsed = dateKeyFmt.parse(selectedDateKey)
                        if (parsed != null) SimpleDateFormat("EEEE, d MMMM", Locale.getDefault()).format(parsed).uppercase() else selectedDateKey
                    } catch (e: Exception) {
                        selectedDateKey
                    }
                }
                KICKER(selectedDateLabel)
                Spacer(Modifier.height(16.dp))

                ShoshinCard(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(24.dp)) {
                        val kept = historyByDate[selectedDateKey]
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(painterResource(R.drawable.ic_walk), null, tint = MaterialTheme.colorScheme.onSurface, modifier = Modifier.size(20.dp))
                            Spacer(Modifier.width(12.dp))
                            Text("Morning practice", style = ShH2Style, color = MaterialTheme.colorScheme.onSurface)
                            Spacer(Modifier.weight(1f))
                            when {
                                kept == true -> ShoshinPill(label = "Kept", variant = ShPillVariant.Matcha)
                                selectedDateKey < todayKey -> ShoshinPill(label = "Missed", variant = ShPillVariant.Outline)
                                else -> ShoshinPill(label = "Not yet", variant = ShPillVariant.Outline)
                            }
                        }

                        Spacer(Modifier.height(24.dp))

                        // Per-checkpoint timing/duration isn't tracked yet — honest
                        // placeholders rather than fabricated numbers.
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            DayDetailItem(value = "—", label = "STARTED")
                            DayDetailItem(value = "—", label = "BRIDGE")
                            DayDetailItem(value = if (kept == true) "Kept" else "—", label = "CHECKPOINTS", color = if (kept == true) ShMatchaDark else null)
                        }
                    }
                }

                Spacer(Modifier.height(48.dp))
            }
        }
    }
}

@Composable
private fun KICKER(text: String) {
    Text(
        text = text,
        style = ShLabelStyle.copy(fontSize = 11.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp),
        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
    )
}

@Composable
private fun HistoryStatCard(value: String, label: String, color: Color, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(20.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .padding(16.dp)
    ) {
        Column {
            Text(value, style = ShNumeralStyle.copy(fontSize = 24.sp, color = color))
            Text(label, style = ShLabelStyle.copy(fontSize = 10.sp), color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
private fun CalendarGrid(
    month: Calendar,
    historyByDate: Map<String, Boolean>,
    todayKey: String,
    selectedDateKey: String,
    onSelectDate: (String) -> Unit
) {
    val days = listOf("S", "M", "T", "W", "T", "F", "S")

    Column {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceAround) {
            days.forEach { d ->
                Text(d, style = ShLabelStyle.copy(fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)))
            }
        }

        Spacer(Modifier.height(16.dp))

        val cal = month.clone() as Calendar
        cal.set(Calendar.DAY_OF_MONTH, 1)
        val daysInMonth = cal.getActualMaximum(Calendar.DAY_OF_MONTH)
        val firstDayOffset = cal.get(Calendar.DAY_OF_WEEK) - 1 // Calendar.SUNDAY=1 -> 0

        val cells = buildList {
            repeat(firstDayOffset) { add(0) }
            addAll(1..daysInMonth)
        }
        val rows = cells.chunked(7)

        rows.forEach { row ->
            Row(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp), horizontalArrangement = Arrangement.SpaceAround) {
                row.forEach { day ->
                    if (day == 0) {
                        Spacer(Modifier.size(36.dp))
                    } else {
                        cal.set(Calendar.DAY_OF_MONTH, day)
                        val key = dateKeyFmt.format(cal.time)
                        val kept = historyByDate[key]
                        val isFuture = key > todayKey
                        val isSelected = key == selectedDateKey
                        val bg = when {
                            kept == true -> ShMatchaDark
                            !isFuture -> ShVermillion.copy(alpha = 0.8f)
                            else -> Color.Transparent
                        }
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isFuture) Color.Transparent else bg)
                                .border(
                                    width = if (isSelected) 1.5.dp else 1.dp,
                                    color = if (isSelected) ShVermillion else if (bg == Color.Transparent || isFuture) MaterialTheme.colorScheme.outline.copy(alpha = 0.1f) else Color.Transparent,
                                    shape = RoundedCornerShape(8.dp)
                                )
                                .clickable { onSelectDate(key) },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                day.toString(),
                                style = ShLabelStyle.copy(fontSize = 13.sp, fontWeight = FontWeight.Bold),
                                color = if (!isFuture && bg != Color.Transparent) Color.White else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun DayDetailItem(value: String, unit: String? = null, label: String, color: Color? = null) {
    Column {
        Row(verticalAlignment = Alignment.Bottom) {
            Text(value, style = ShNumeralStyle.copy(fontSize = 28.sp, color = color ?: MaterialTheme.colorScheme.onSurface))
            if (unit != null) {
                Text(unit, style = ShLabelStyle.copy(fontSize = 12.sp), modifier = Modifier.padding(bottom = 4.dp, start = 2.dp))
            }
        }
        Text(label, style = ShKickerStyle.copy(fontSize = 9.sp), color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}
