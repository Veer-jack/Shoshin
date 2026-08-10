package com.Shoshin.app.ui.screens

import androidx.compose.foundation.background
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
import com.Shoshin.app.navigation.ShRoutes
import com.Shoshin.app.ui.components.*
import com.Shoshin.app.ui.theme.*

@Composable
fun ConsistencyScreen(
    navController: NavController,
    streakViewModel: com.Shoshin.app.viewmodel.StreakViewModel? = null,
    networkMonitor: com.Shoshin.app.sync.NetworkStateMonitor? = null
) {
    val user by streakViewModel?.user?.collectAsState(initial = null) ?: remember { mutableStateOf(null) }
    val streak = user?.currentStreak ?: 0

    if (streak == 0) {
        CleanPageScreen(networkMonitor = networkMonitor)
    } else {
        PopulatedConsistencyScreen(navController, streakViewModel, networkMonitor)
    }
}

@Composable
private fun CleanPageScreen(networkMonitor: com.Shoshin.app.sync.NetworkStateMonitor? = null) {
    val isOnline by networkMonitor?.isOnline?.collectAsState(initial = true) ?: remember { mutableStateOf(true) }

    ShoshinTheme(type = ShoshinThemeType.DYNAMIC) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background),
        ) {
            OfflineIndicator(isOffline = !isOnline)
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Spacer(Modifier.weight(1f))

            Box(
                modifier = Modifier.size(240.dp),
                contentAlignment = Alignment.Center
            ) {
                Enso(size = 200, color = MaterialTheme.colorScheme.outline.copy(alpha = 0.15f), strokeWidth = 8f)
            }

            Spacer(Modifier.height(48.dp))

            Kicker("DAY ONE", color = ShVermillion)
            
            Spacer(Modifier.height(12.dp))
            
            Text(
                "A clean page",
                style = ShTitleStyle.copy(fontSize = 36.sp, color = MaterialTheme.colorScheme.onBackground),
                textAlign = TextAlign.Center
            )

            Spacer(Modifier.height(16.dp))

            Text(
                "No streak yet, no history — just tomorrow morning, and the first small step across the bridge.",
                style = ShBodyStyle,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 24.dp),
                lineHeight = 24.sp
            )

            Spacer(Modifier.weight(1.2f))

            Text(
                "Every practice begins once.",
                style = ShLabelStyle,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )

            Spacer(Modifier.height(24.dp))
        }
        }
    }
}

@Composable
private fun PopulatedConsistencyScreen(
    navController: NavController,
    streakViewModel: com.Shoshin.app.viewmodel.StreakViewModel? = null,
    networkMonitor: com.Shoshin.app.sync.NetworkStateMonitor? = null
) {
    val user by streakViewModel?.user?.collectAsState(initial = null) ?: remember { mutableStateOf(null) }
    val weekPattern by streakViewModel?.weekPattern?.collectAsState(initial = List(7) { null }) ?: remember { mutableStateOf(List<Boolean?>(7) { null }) }

    val streak = user?.currentStreak ?: 0
    val bestStreak = user?.bestStreak ?: 0
    val totalMornings = user?.totalActivations ?: 0

    val consistencyValue = if (user != null && user!!.totalActivations > 0) {
        val daysSinceCreation = ((System.currentTimeMillis() - user!!.createdAt) / (1000 * 60 * 60 * 24)).coerceAtLeast(1)
        ((user!!.totalActivations.toFloat() / daysSinceCreation.toFloat()) * 100).toInt().coerceIn(0, 100)
    } else {
        0
    }

    val isOnline by networkMonitor?.isOnline?.collectAsState(initial = true) ?: remember { mutableStateOf(true) }

    ShoshinTheme(type = ShoshinThemeType.DYNAMIC) {
        Column(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
            OfflineIndicator(isOffline = !isOnline)
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp)
        ) {
            Spacer(Modifier.height(32.dp))

            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Kicker("CONSISTENCY", color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Spacer(Modifier.height(4.dp))
                    Text(
                        "Your practice",
                        style = ShTitleStyle.copy(fontSize = 32.sp, color = MaterialTheme.colorScheme.onBackground)
                    )
                }
                
                Surface(
                    color = ShVermillion.copy(alpha = 0.1f),
                    shape = RoundedCornerShape(999.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(painterResource(R.drawable.ic_flame), null, tint = ShVermillion, modifier = Modifier.size(14.dp))
                        Text("$streak kept", color = ShVermillion, style = ShLabelStyle, fontWeight = FontWeight.Bold)
                    }
                }
            }

            Spacer(Modifier.height(24.dp))

            ShoshinCard(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.padding(24.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        RingProgress(
                            percentage = consistencyValue,
                            size = 100,
                            strokeWidth = 10f,
                            valueText = consistencyValue.toString(),
                            color = ShMatcha,
                            trackColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                        )
                        Text(
                            "CONSISTENCY",
                            style = ShKickerStyle.copy(fontSize = 8.sp, color = MaterialTheme.colorScheme.onSurfaceVariant),
                            modifier = Modifier.padding(top = 40.dp)
                        )
                    }
                    
                    Spacer(Modifier.width(24.dp))
                    
                    Column {
                        Text("$totalMornings mornings\nkept", style = ShH2Style.copy(fontSize = 18.sp, color = MaterialTheme.colorScheme.onSurface), lineHeight = 22.sp)
                        Text("Your personal evolution", style = ShLabelStyle, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        
                        Spacer(Modifier.height(16.dp))
                        HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f))
                        Spacer(Modifier.height(16.dp))
                        
                        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                            StatSmall(value = user?.productiveStartTime ?: "06:00", label = "AVG START")
                        }
                    }
                }
            }

            Spacer(Modifier.height(16.dp))

            ShoshinCard(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(24.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("This week", style = ShH2Style, color = MaterialTheme.colorScheme.onSurface)
                        Row(
                            modifier = Modifier.clickable { navController.navigate(ShRoutes.HISTORY) },
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("History", style = ShLabelStyle, color = ShVermillion)
                            Icon(painterResource(R.drawable.ic_arrow_right), null, tint = ShVermillion, modifier = Modifier.size(14.dp))
                        }
                    }
                    
                    Spacer(Modifier.height(24.dp))
                    
                    Row(
                        modifier = Modifier.fillMaxWidth().height(100.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Bottom
                    ) {
                        val days = listOf("M", "T", "W", "T", "F", "S", "S")
                        // Monday-first offset of today, so the current day can be highlighted for real.
                        val todayIndex = remember {
                            val dow = java.util.Calendar.getInstance().get(java.util.Calendar.DAY_OF_WEEK)
                            (dow + 5) % 7 // Calendar.SUNDAY=1..SATURDAY=7 -> Monday-first 0..6
                        }

                        days.forEachIndexed { i, day ->
                            val kept = weekPattern.getOrNull(i)
                            val height = when (kept) {
                                true -> 1.0f
                                false -> 0.12f
                                null -> 0.06f // day hasn't happened yet
                            }
                            val barColor = when (kept) {
                                true -> ShMatcha
                                false -> MaterialTheme.colorScheme.outline
                                null -> MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                            }
                            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.weight(1f)) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth(0.6f)
                                        .fillMaxHeight(height)
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(barColor)
                                )
                                Spacer(Modifier.height(8.dp))
                                Text(day, style = ShLabelStyle.copy(fontSize = 11.sp), color = if (i == todayIndex) ShVermillion else MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                    }
                }
            }

            Spacer(Modifier.height(16.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                StatGridItem(icon = R.drawable.ic_droplet, value = bestStreak.toString(), label = "BEST STREAK", modifier = Modifier.weight(1f))
                StatGridItem(icon = R.drawable.ic_sun, value = totalMornings.toString(), label = "TOTAL MORNINGS", modifier = Modifier.weight(1f))
            }
            
            Spacer(Modifier.height(16.dp))
            
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                StatGridItem(icon = R.drawable.ic_bell, value = "$consistencyValue%", label = "ON-TIME RATE", modifier = Modifier.weight(1f))
                StatGridItem(icon = R.drawable.ic_check, value = (totalMornings * 5).toString(), label = "CHECKPOINTS KEPT", modifier = Modifier.weight(1f))
            }

            Spacer(Modifier.height(32.dp))
        }
        }
    }
}

@Composable
private fun StatSmall(value: String, label: String) {
    Column {
        Text(value, style = ShH2Style.copy(fontSize = 20.sp, color = MaterialTheme.colorScheme.onSurface))
        Text(label, style = ShKickerStyle.copy(fontSize = 8.sp, letterSpacing = 1.sp, color = MaterialTheme.colorScheme.onSurfaceVariant))
    }
}

@Composable
private fun StatGridItem(icon: Int, value: String, label: String, modifier: Modifier = Modifier) {
    ShoshinCard(modifier = modifier) {
        Column(modifier = Modifier.padding(20.dp)) {
            Icon(painterResource(icon), null, tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(20.dp))
            Spacer(Modifier.height(16.dp))
            Text(value, style = ShTitleStyle.copy(fontSize = 28.sp, color = MaterialTheme.colorScheme.onSurface))
            Text(label, style = ShKickerStyle.copy(fontSize = 9.sp, letterSpacing = 1.2.sp, color = MaterialTheme.colorScheme.onSurfaceVariant))
        }
    }
}
