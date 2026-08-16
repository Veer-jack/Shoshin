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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.Shoshin.app.R
import com.Shoshin.app.data.ShoshinRepository
import com.Shoshin.app.navigation.ShRoutes
import com.Shoshin.app.sync.*
import com.Shoshin.app.ui.components.*
import com.Shoshin.app.ui.theme.*
import kotlinx.coroutines.launch
import java.util.*
import java.text.SimpleDateFormat

private data class StepData(val label: String, val icon: Int)
private data class TemplateData(val name: String, val icon: Int, val steps: List<StepData>)

private val TEMPLATE_MAP = mapOf(
    "walk"  to TemplateData("Morning Walk", R.drawable.ic_walk, listOf(
        StepData("Mind awake", R.drawable.ic_brain),
        StepData("Freshen up", R.drawable.ic_droplet),
        StepData("Dressed", R.drawable.ic_shirt),
        StepData("Out the door", R.drawable.ic_sun),
        StepData("Walk begun", R.drawable.ic_walk)
    )),
    "study" to TemplateData("Deep Study",   R.drawable.ic_book, listOf(
        StepData("Mind awake", R.drawable.ic_brain),
        StepData("Freshen up", R.drawable.ic_droplet),
        StepData("Tea brewed", R.drawable.ic_check),
        StepData("Desk ready", R.drawable.ic_check),
        StepData("Study begun", R.drawable.ic_book)
    ))
)

@Composable
fun DashboardTab(
    navController: NavController,
    syncManager: SyncManager,
    networkMonitor: NetworkStateMonitor,
    conflictResolver: ConflictResolver,
    streakViewModel: com.Shoshin.app.viewmodel.StreakViewModel,
    friendViewModel: com.Shoshin.app.viewmodel.FriendStreaksViewModel? = null,
    onNavigateToTab: ((String) -> Unit)? = null
) {
    val context = LocalContext.current
    val repo = remember { ShoshinRepository(context) }
    val user by streakViewModel.user.collectAsState()
    val scope = rememberCoroutineScope()
    
    val userName = user?.displayName?.takeIf { it.isNotBlank() } ?: "Friend"
    val streak = user?.currentStreak ?: 0

    val consistencyValue = if (user != null && user!!.totalActivations > 0) {
        val daysSinceCreation = ((System.currentTimeMillis() - user!!.createdAt) / (1000 * 60 * 60 * 24)).coerceAtLeast(1)
        ((user!!.totalActivations.toFloat() / daysSinceCreation.toFloat()) * 100).toInt().coerceIn(0, 100)
    } else {
        0
    }
    
    val savedHour by repo.alarmHour.collectAsState(initial = 5)
    val savedMinute by repo.alarmMinute.collectAsState(initial = 30)
    
    val templateKey by repo.template.collectAsState(initial = "walk")
    val t = TEMPLATE_MAP[templateKey] ?: TEMPLATE_MAP["walk"]!!

    val isOnline by networkMonitor.isOnline.collectAsState(initial = true)
    
    ShoshinTheme(type = ShoshinThemeType.DYNAMIC) {
        val isDark = MaterialTheme.colorScheme.background == ShNight
        
        if (!isOnline) {
            OfflineDashboard(onReconnect = { scope.launch { syncManager.syncAll() } })
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background)
                    // Inset the status bar on the container, not on the header row —
                    // inside the scroller the inset scrolled away with the content.
                    .statusBarsPadding()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 24.dp)
            ) {
                Spacer(Modifier.height(24.dp))

                // ── Header Section ──────────────────────────────────
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        val dateStr = SimpleDateFormat("EEEE · d MMM", Locale.getDefault()).format(Date()).uppercase()
                        Kicker(dateStr, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Spacer(Modifier.height(8.dp))
                        
                        val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
                        val greeting = if (hour < 12) "Good morning," else if (hour < 17) "Good afternoon," else "Good evening,"
                        
                        Text(
                            text = "$greeting\n$userName", 
                            style = ShTitleStyle.copy(fontSize = 32.sp, color = MaterialTheme.colorScheme.onBackground),
                            lineHeight = 36.sp
                        )
                    }
                    
                    Spacer(Modifier.width(16.dp))

                    // Avatar
                    Box(
                        modifier = Modifier
                            .size(56.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.surfaceVariant)
                            .clickable { navController.navigate(ShRoutes.PROFILE) },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = userName.firstOrNull()?.toString()?.uppercase() ?: "F",
                            style = ShTitleStyle.copy(fontSize = 20.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f))
                        )
                    }
                }

                Spacer(Modifier.height(32.dp))

                // ── Wake Time Hero Card (The Focus) ────────────────
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(32.dp))
                        .background(if (isDark) ShNight2 else ShInk) // Always dark/Ink
                        .padding(24.dp)
                ) {
                    // Subtle Enso Detail
                    Enso(
                        size = 160, 
                        color = ShVermillion.copy(alpha = 0.08f),
                        strokeWidth = 6f, 
                        modifier = Modifier.align(Alignment.TopEnd).offset(x = 30.dp, y = (-20).dp)
                    )
                    
                    Column {
                        // Status Pill
                        Surface(
                            color = ShMatcha.copy(alpha = 0.15f),
                            shape = RoundedCornerShape(999.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Box(modifier = Modifier.size(6.dp).clip(CircleShape).background(ShMatchaDark))
                                Text("SET FOR DAWN", style = ShLabelStyle.copy(fontSize = 11.sp, fontWeight = FontWeight.Bold, color = ShMatchaDark))
                            }
                        }

                        Spacer(Modifier.height(16.dp))
                        
                        // Large Time
                        Row(verticalAlignment = Alignment.Bottom) {
                            val timeStr = String.format("%02d:%02d", if (savedHour == 0) 12 else if (savedHour > 12) savedHour - 12 else savedHour, savedMinute)
                            val amPm = if (savedHour >= 12) "PM" else "AM"
                            
                            Text(
                                text = timeStr, 
                                style = ShTitleStyle.copy(fontSize = 72.sp, color = Color.White)
                            )
                            Spacer(Modifier.width(8.dp))
                            Text(
                                text = amPm, 
                                style = ShTitleStyle.copy(fontSize = 22.sp, color = Color.White.copy(alpha = 0.4f)),
                                modifier = Modifier.padding(bottom = 14.dp)
                            )
                        }
                        
                        // Habit Detail Row
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(painterResource(t.icon), null, tint = Color.White.copy(alpha = 0.6f), modifier = Modifier.size(16.dp))
                            Spacer(Modifier.width(10.dp))
                            Text("${t.name} · ${t.steps.size} checkpoints", style = ShLabelStyle, color = Color.White.copy(alpha = 0.6f))
                        }

                        Spacer(Modifier.height(24.dp))
                        
                        // Adjust Button
                        ShoshinButton(
                            onClick = { navController.navigate(ShRoutes.ALARM_SETUP) },
                            variant = ShButtonVariant.Dark,
                            modifier = Modifier.fillMaxWidth().height(52.dp),
                            leadingIcon = { Icon(painterResource(R.drawable.ic_bell), null, tint = Color.White, modifier = Modifier.size(18.dp)) }
                        ) {
                            Text("Adjust tomorrow's wake", color = Color.White, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                Spacer(Modifier.height(16.dp))

                // ── Double Stats Row ────────────────────────────────
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    // Consistency Card
                    ShoshinCard(modifier = Modifier.weight(1f)) {
                        Row(
                            modifier = Modifier.padding(20.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RingProgress(percentage = consistencyValue, size = 64, strokeWidth = 8f, valueText = "", color = ShMatcha, trackColor = MaterialTheme.colorScheme.outline)
                            Spacer(Modifier.width(12.dp))
                            Column {
                                Row(verticalAlignment = Alignment.Bottom) {
                                    Text(consistencyValue.toString(), style = ShNumeralStyle.copy(fontSize = 24.sp, color = MaterialTheme.colorScheme.onSurface))
                                    Text("%", style = ShLabelStyle.copy(fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant), modifier = Modifier.padding(bottom = 4.dp, start = 1.dp))
                                }
                                Text("CONSISTENCY", style = ShKickerStyle.copy(fontSize = 8.sp, color = MaterialTheme.colorScheme.onSurfaceVariant))
                            }
                        }
                    }

                    // Mornings Kept Card
                    ShoshinCard(modifier = Modifier.weight(1f)) {
                        Row(
                            modifier = Modifier.padding(20.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier.size(44.dp).clip(CircleShape).background(ShVermillion.copy(alpha = 0.05f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(painterResource(R.drawable.ic_flame), null, tint = ShVermillion, modifier = Modifier.size(20.dp))
                            }
                            Spacer(Modifier.width(12.dp))
                            Column {
                                Text(streak.toString(), style = ShNumeralStyle.copy(fontSize = 24.sp, color = MaterialTheme.colorScheme.onSurface))
                                Text("MORNINGS\nKEPT", style = ShKickerStyle.copy(fontSize = 8.sp, color = MaterialTheme.colorScheme.onSurfaceVariant), lineHeight = 10.sp)
                            }
                        }
                    }
                }

                Spacer(Modifier.height(32.dp))

                // ── The Bridge Section ──────────────────────────────
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Bottom
                ) {
                    Column {
                        Kicker("THE BRIDGE", color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text("Tomorrow's path", style = ShTitleStyle.copy(fontSize = 20.sp, color = MaterialTheme.colorScheme.onBackground))
                    }
                    Text(
                        "Edit", 
                        style = ShLabelStyle.copy(fontWeight = FontWeight.Bold, color = ShVermillion), 
                        modifier = Modifier.clickable { navController.navigate(ShRoutes.ROUTINE_EDITOR) }
                    )
                }

                Spacer(Modifier.height(16.dp))

                // Path List Card
                ShoshinCard(modifier = Modifier.fillMaxWidth().padding(bottom = 32.dp)) {
                    Column(modifier = Modifier.padding(vertical = 8.dp)) {
                        t.steps.forEachIndexed { i, step ->
                            Row(
                                modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp, vertical = 14.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                // Tomorrow's path is a preview, not live progress — no step is
                                // active yet, so every row reads at the same weight.
                                Box(
                                    modifier = Modifier.size(32.dp).border(1.dp, MaterialTheme.colorScheme.outline, CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text((i + 1).toString(), style = ShLabelStyle, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                                Spacer(Modifier.width(16.dp))
                                Icon(
                                    painter = painterResource(step.icon),
                                    null,
                                    modifier = Modifier.size(18.dp),
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                                )
                                Spacer(Modifier.width(16.dp))
                                Text(
                                    step.label,
                                    style = ShBodyStyle,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    fontWeight = FontWeight.Normal,
                                    modifier = Modifier.weight(1f)
                                )
                            }
                            if (i < t.steps.lastIndex) {
                                HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f), modifier = Modifier.padding(start = 72.dp, end = 24.dp))
                            }
                        }
                    }
                }

                Spacer(Modifier.height(16.dp))
            }
        }
    }
    
    // Conflict dialog remains integrated
    conflictResolver.conflictDialog.collectAsState(initial = null).value?.let { conflict ->
        ConflictResolutionDialog(
            isVisible = true,
            localContent = conflict.local.content.take(50),
            remoteContent = conflict.remote.content.take(50),
            onUseLocal = { scope.launch { conflictResolver.resolveWithLocal(conflict.local) } },
            onUseRemote = { scope.launch { conflictResolver.resolveWithRemote(conflict.remote) } },
            onMerge = { scope.launch { conflictResolver.resolveWithMerge(conflict.local, conflict.remote) } }
        )
    }
}

@Composable
private fun OfflineDashboard(onReconnect: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize().background(ShNight).statusBarsPadding()
    ) {
        Surface(
            modifier = Modifier.padding(horizontal = 24.dp, vertical = 16.dp),
            color = ShNight2,
            shape = RoundedCornerShape(12.dp)
        ) {
            Row(modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp), verticalAlignment = Alignment.CenterVertically) {
                Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(ShNightMuted))
                Spacer(Modifier.width(12.dp))
                Text("You're offline — showing your last sync", style = ShLabelStyle, color = ShNightMuted, modifier = Modifier.weight(1f))
                Text("Retry", style = ShLabelStyle, color = ShVermillionLight, fontWeight = FontWeight.Bold, modifier = Modifier.clickable { onReconnect() })
            }
        }
        // ... rest of offline UI remains minimal
    }
}
