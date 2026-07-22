package com.shoshin.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
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
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.shoshin.app.R
import com.shoshin.app.data.ShoshinRepository
import com.shoshin.app.navigation.ShRoutes
import com.shoshin.app.sync.*
import com.shoshin.app.ui.components.*
import com.shoshin.app.ui.theme.*
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
    )),
    "gym"   to TemplateData("Strength",     R.drawable.ic_dumbbell, listOf(
        StepData("Mind awake", R.drawable.ic_brain),
        StepData("Freshen up", R.drawable.ic_droplet),
        StepData("Kit on", R.drawable.ic_shirt),
        StepData("Out the door", R.drawable.ic_sun),
        StepData("Training begun", R.drawable.ic_dumbbell)
    ))
)

@Composable
fun DashboardTab(
    navController: NavController,
    syncManager: SyncManager,
    networkMonitor: NetworkStateMonitor,
    conflictResolver: ConflictResolver,
    streakViewModel: com.shoshin.app.viewmodel.StreakViewModel,
    friendViewModel: com.shoshin.app.viewmodel.FriendStreaksViewModel? = null
) {
    val context = LocalContext.current
    val repo = remember { ShoshinRepository(context) }
    val user by streakViewModel.user.collectAsState()
    
    val userName = user?.displayName ?: "Friend"
    val streak = user?.currentStreak ?: 0
    
    val topFriends by friendViewModel?.topFriends?.collectAsState(initial = emptyList()) ?: remember { mutableStateOf(emptyList()) }
    val totalFriends = user?.friendCount ?: 0

    val template by repo.template.collectAsState(initial = "walk")
    val t = TEMPLATE_MAP[template] ?: TEMPLATE_MAP["walk"]!!

    val isOffline by networkMonitor.isOnline.collectAsState(initial = true)
    val syncState by syncManager.syncState.collectAsState(initial = SyncState.Idle)
    val conflictDialog by conflictResolver.conflictDialog.collectAsState(initial = null)
    val scope = rememberCoroutineScope()

    val calendar = Calendar.getInstance()
    val dateStr = SimpleDateFormat("EEEE, d MMM", Locale.getDefault()).format(calendar.time)
    val hour = calendar.get(Calendar.HOUR_OF_DAY)
    val greeting = when (hour) {
        in 0..11  -> "Good morning"
        in 12..16 -> "Good afternoon"
        else       -> "Good evening"
    }

    if (syncState is SyncState.Syncing) {
        LoadingDialog(message = "Syncing your progress...")
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Offline indicator
        OfflineIndicator(isOffline = !isOffline)

        // Sync status bar
        if (syncState !is SyncState.Idle && syncState !is SyncState.Syncing) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        when (syncState) {
                            is SyncState.Success -> ShMatcha.copy(alpha = 0.1f)
                            is SyncState.Error -> ShVermillion.copy(alpha = 0.1f)
                            else -> MaterialTheme.colorScheme.surfaceVariant
                        }
                    )
                    .padding(vertical = 8.dp, horizontal = 16.dp),
                contentAlignment = Alignment.Center
            ) {
                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_pulse),
                        contentDescription = "Sync",
                        tint = when (syncState) {
                            is SyncState.Success -> ShMatcha
                            is SyncState.Error -> ShVermillion
                            else -> MaterialTheme.colorScheme.onBackground
                        },
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = when (syncState) {
                            is SyncState.Success -> (syncState as SyncState.Success).message
                            is SyncState.Error -> (syncState as SyncState.Error).message
                            else -> ""
                        },
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 16.dp)
        ) {
            // Header
            Row(modifier = Modifier.fillMaxWidth().padding(bottom = 24.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Column {
                    val displayDate = SimpleDateFormat("EEEE · d MMM", Locale.getDefault()).format(calendar.time).uppercase()
                    Text(displayDate, fontSize = 11.sp, fontWeight = FontWeight.Medium, fontFamily = DmSansFamily, color = MaterialTheme.colorScheme.onSurfaceVariant, letterSpacing = 2.sp)
                    Spacer(Modifier.height(6.dp))
                    Text("$greeting,\n$userName", fontSize = 32.sp, fontWeight = FontWeight.SemiBold, fontFamily = CormorantFamily, color = MaterialTheme.colorScheme.onBackground, lineHeight = 36.sp)
                }
                
                // Circular Initial Avatar
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                        .clickable { navController.navigate("profile") },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = userName.firstOrNull()?.toString()?.uppercase() ?: "U",
                        style = ShH2Style.copy(color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f))
                    )
                }
            }

            // Hero card — ink background (Always dark as per design spec)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(24.dp))
                    .background(ShInk)
                    .clickable { navController.navigate(ShRoutes.CLOCK) }
                    .padding(24.dp)
            ) {
                // Enso motif
                Enso(
                    size = 200,
                    color = ShVermillion.copy(alpha = 0.15f),
                    strokeWidth = 8f,
                    modifier = Modifier.align(Alignment.TopEnd).offset(x = 60.dp, y = (-60).dp)
                )
                Column {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                        Row(modifier = Modifier.clip(RoundedCornerShape(999.dp)).background(ShMatcha.copy(alpha = 0.15f)).padding(horizontal = 12.dp, vertical = 6.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            Box(modifier = Modifier.size(6.dp).clip(CircleShape).background(ShMatcha))
                            Text("SET FOR DAWN", fontSize = 11.sp, fontWeight = FontWeight.Bold, fontFamily = DmSansFamily, color = ShMatcha, letterSpacing = 1.sp)
                        }
                        
                        Text(
                            text = "Life counting down →",
                            style = ShLabelStyle.copy(fontSize = 11.sp, color = ShPaper.copy(alpha = 0.5f))
                        )
                    }

                    Spacer(Modifier.height(20.dp))
                    Row(verticalAlignment = Alignment.Bottom, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text("05:30", fontSize = 64.sp, fontWeight = FontWeight.Bold, fontFamily = DmSansFamily, color = ShPaper, letterSpacing = (-2).sp)
                        Text("AM", fontSize = 20.sp, fontWeight = FontWeight.SemiBold, fontFamily = DmSansFamily, color = ShPaper.copy(alpha = 0.5f), modifier = Modifier.padding(bottom = 12.dp))
                    }
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(bottom = 20.dp)) {
                        Icon(
                            painter = painterResource(id = t.icon),
                            contentDescription = null,
                            tint = ShPaper.copy(alpha = 0.7f),
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(Modifier.width(8.dp))
                        Text(
                            "${t.name} · ${t.steps.size} checkpoints", 
                            fontSize = 15.sp, 
                            color = ShPaper.copy(alpha = 0.7f), 
                            fontFamily = DmSansFamily
                        )
                    }
                    
                    val adjustInteractionSource = remember { MutableInteractionSource() }
                    
                    ShoshinButton(
                        onClick = { navController.navigate("alarm_setup") },
                        variant = ShButtonVariant.Dark,
                        modifier = Modifier.fillMaxWidth().height(48.dp),
                        leadingIcon = {
                            Icon(painterResource(R.drawable.ic_bell), null, modifier = Modifier.size(16.dp), tint = Color.White.copy(alpha = 0.7f))
                        },
                        interactionSource = adjustInteractionSource,
                        pressedColor = Color.White.copy(alpha = 0.1f)
                    ) {
                        Text(
                            "Adjust tomorrow's wake", 
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color.White.copy(alpha = 0.9f)
                        )
                    }
                }
            }

            Spacer(Modifier.height(16.dp))

            // Streak Loss Warning
            val currentHour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
            val lastCheckpoint = user?.lastCheckpointDate ?: 0L
            val isTodayDone = isSameDay(lastCheckpoint, System.currentTimeMillis())
            
            if (!isTodayDone && currentHour >= 20) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(ShVermillion.copy(alpha = 0.1f))
                        .border(1.dp, ShVermillion, RoundedCornerShape(12.dp))
                        .clickable { navController.navigate(ShRoutes.CLOCK) }
                        .padding(16.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(painterResource(R.drawable.ic_info), contentDescription = null, tint = ShVermillion, modifier = Modifier.size(20.dp))
                        Spacer(Modifier.width(12.dp))
                        Text(
                            "Complete your checkpoint to maintain your streak!",
                            style = ShBodyStyle,
                            color = ShVermillion,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(Modifier.height(16.dp))

            // Split Stats Row (Replacing the old large streak card)
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(14.dp)) {
                // Consistency Card
                ShoshinCard(modifier = Modifier.weight(1f)) {
                    Row(
                        modifier = Modifier.padding(16.dp), 
                        verticalAlignment = Alignment.CenterVertically, 
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        val consistency = 86 // This would ideally be calculated from history
                        RingProgress(percentage = consistency, size = 48, strokeWidth = 5f, valueText = "", color = ShMatcha, trackColor = MaterialTheme.colorScheme.surfaceVariant)
                        Column {
                            Text("$consistency%", fontSize = 20.sp, fontWeight = FontWeight.Bold, fontFamily = DmSansFamily, color = MaterialTheme.colorScheme.onBackground)
                            Text("CONSISTENCY", fontSize = 8.sp, fontWeight = FontWeight.Bold, fontFamily = DmSansFamily, color = MaterialTheme.colorScheme.onSurfaceVariant, letterSpacing = 1.sp)
                        }
                    }
                }
                
                // Mornings Kept Card (The current streak)
                ShoshinCard(
                    modifier = Modifier
                        .weight(1f)
                        .clickable { navController.navigate(ShRoutes.STREAK_DETAILS) }
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp), 
                        verticalAlignment = Alignment.CenterVertically, 
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Box(
                            modifier = Modifier.size(40.dp).clip(CircleShape).background(ShVermillion.copy(alpha = 0.08f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(painterResource(R.drawable.ic_flame), null, modifier = Modifier.size(16.dp), tint = ShVermillion)
                        }
                        Column {
                            Text(streak.toString(), fontSize = 20.sp, fontWeight = FontWeight.Bold, fontFamily = DmSansFamily, color = MaterialTheme.colorScheme.onBackground)
                            Text("MORNINGS KEPT", fontSize = 8.sp, fontWeight = FontWeight.Bold, fontFamily = DmSansFamily, color = MaterialTheme.colorScheme.onSurfaceVariant, letterSpacing = 1.sp)
                        }
                    }
                }
            }

            Spacer(Modifier.height(24.dp))

            Row(modifier = Modifier.fillMaxWidth().padding(bottom = 14.dp), verticalAlignment = Alignment.CenterVertically) {
                Kicker("The Bridge", color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.weight(1f))
                Text(
                    text = "Edit", 
                    fontSize = 14.sp, 
                    color = ShVermillion, 
                    fontWeight = FontWeight.Medium, 
                    fontFamily = DmSansFamily, 
                    modifier = Modifier.clickable { navController.navigate("routine_editor") }
                )
            }
            Text(
                text = "Tomorrow's path", 
                fontSize = 18.sp, 
                fontWeight = FontWeight.SemiBold, 
                fontFamily = DmSansFamily, 
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.padding(bottom = 14.dp)
            )

            ShoshinCard(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 12.dp)) {
                    t.steps.forEachIndexed { i, step ->
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(14.dp)
                        ) {
                            // Circular Number
                            Box(
                                modifier = Modifier.size(32.dp).border(1.dp, ShLine, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(text = (i + 1).toString(), fontSize = 13.sp, color = ShFog)
                            }
                            
                            // Icon
                            Icon(painterResource(step.icon), null, modifier = Modifier.size(18.dp), tint = ShFog)
                            
                            // Label
                            Text(
                                text = step.label,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Medium,
                                color = ShInk,
                                modifier = Modifier.weight(1f)
                            )
                        }
                        if (i < t.steps.lastIndex) HorizontalDivider(color = ShLine.copy(alpha = 0.5f), thickness = 1.dp)
                    }
                }
            }

            Spacer(Modifier.height(32.dp))

            ShoshinButton(
                onClick = { navController.navigate("morning/activation") },
                variant = ShButtonVariant.Accent,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(painterResource(R.drawable.ic_bolt_heavy), null, modifier = Modifier.size(20.dp), tint = Color.White)
                Spacer(Modifier.width(10.dp))
                Text("Begin Morning Practice", fontWeight = FontWeight.Bold)
            }

            Spacer(Modifier.height(40.dp))
        }
    }

    conflictDialog?.let { conflict ->
        ConflictResolutionDialog(
            isVisible = true,
            localContent = conflict.local.content.take(50),
            remoteContent = conflict.remote.content.take(50),
            onUseLocal = { conflictResolver.resolveWithLocal(conflict.local) },
            onUseRemote = { conflictResolver.resolveWithRemote(conflict.remote) },
            onMerge = { conflictResolver.resolveWithMerge(conflict.local, conflict.remote) }
        )
    }
}

private fun isSameDay(t1: Long, t2: Long): Boolean {
    val cal1 = Calendar.getInstance().apply { timeInMillis = t1 }
    val cal2 = Calendar.getInstance().apply { timeInMillis = t2 }
    return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
           cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR)
}
