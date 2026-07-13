package com.example.shoshinapp.ui.screens

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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.shoshinapp.R
import com.example.shoshinapp.data.ShoshinRepository
import com.example.shoshinapp.navigation.ShRoutes
import com.example.shoshinapp.sync.*
import com.example.shoshinapp.ui.components.*
import com.example.shoshinapp.ui.theme.*
import kotlinx.coroutines.launch
import java.util.*
import java.text.SimpleDateFormat

private data class TemplateData(val name: String, val icon: Int, val steps: List<String>)
private val TEMPLATE_MAP = mapOf(
    "walk"  to TemplateData("Morning Walk", R.drawable.ic_walk, listOf("Mind awake","Freshen up","Dressed","Out the door","Walk begun")),
    "study" to TemplateData("Deep Study",   R.drawable.ic_book, listOf("Mind awake","Freshen up","Tea brewed","Desk ready","Study begun")),
    "gym"   to TemplateData("Strength",     R.drawable.ic_dumbbell, listOf("Mind awake","Freshen up","Kit on","Out the door","Training begun"))
)

@Composable
fun DashboardTab(
    navController: NavController,
    syncManager: SyncManager,
    networkMonitor: NetworkStateMonitor,
    conflictResolver: ConflictResolver
) {
    val context = LocalContext.current
    val repo = remember { ShoshinRepository(context) }
    val userName by repo.userName.collectAsState(initial = "Friend")
    val streak by repo.streakCount.collectAsState(initial = 0)
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
            .background(ShPaper)
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
                            else -> ShPaper
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
                            else -> ShInk
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
                        color = ShInk
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
            Row(modifier = Modifier.fillMaxWidth().padding(bottom = 24.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.Top) {
                Column {
                    Text(dateStr.uppercase(), fontSize = 11.sp, fontWeight = FontWeight.Medium, fontFamily = DmSansFamily, color = ShFog, letterSpacing = 2.sp)
                    Spacer(Modifier.height(6.dp))
                    Text("$greeting,\n$userName", fontSize = 32.sp, fontWeight = FontWeight.SemiBold, fontFamily = CormorantFamily, color = ShInk, lineHeight = 36.sp)
                }
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    // Manual Sync Button
                    IconButton(
                        onClick = { scope.launch { syncManager.syncAll() } },
                        enabled = !isOffline && syncState !is SyncState.Syncing
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_pulse),
                            contentDescription = "Sync Now",
                            tint = if (syncState is SyncState.Syncing) ShVermillion else ShInk
                        )
                    }

                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(ShSand)
                            .border(1.5.dp, ShLine, CircleShape)
                            .clickable { navController.navigate("profile") }, 
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.ic_user),
                            contentDescription = "Profile",
                            tint = ShInk,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            }

            // Hero card — ink background
            Box(modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(24.dp)).background(ShInk).padding(24.dp)) {
                // Enso motif
                Box(modifier = Modifier.size(200.dp).align(Alignment.TopEnd).offset(x = 60.dp, y = (-60).dp)) {
                    androidx.compose.foundation.Canvas(modifier = Modifier.fillMaxSize()) {
                        drawArc(color = ShVermillion.copy(alpha = 0.15f), startAngle = -90f, sweepAngle = 310f, useCenter = false, style = androidx.compose.ui.graphics.drawscope.Stroke(width = 8.dp.toPx(), cap = androidx.compose.ui.graphics.StrokeCap.Round))
                    }
                }
                Column {
                    Row(modifier = Modifier.clip(RoundedCornerShape(999.dp)).background(ShMatcha.copy(alpha = 0.15f)).padding(horizontal = 12.dp, vertical = 6.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        Box(modifier = Modifier.size(6.dp).clip(CircleShape).background(ShMatcha))
                        Text("SET FOR DAWN", fontSize = 11.sp, fontWeight = FontWeight.Bold, fontFamily = DmSansFamily, color = ShMatcha, letterSpacing = 1.sp)
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
                    
                    ShoshinButton(
                        onClick = { navController.navigate("alarm_setup") }, // Use new route
                        variant = ShButtonVariant.Dark,
                        modifier = Modifier.height(48.dp)
                    ) {
                        Text("Adjust tomorrow's wake", fontSize = 14.sp)
                    }
                }
            }

            Spacer(Modifier.height(16.dp))

            // Metrics
            Row(horizontalArrangement = Arrangement.spacedBy(14.dp)) {
                ShoshinCard(modifier = Modifier.weight(1f)) {
                    Row(modifier = Modifier.padding(20.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        RingProgress(percentage = 86, size = 52, strokeWidth = 5f, valueText = "86", color = ShInk, trackColor = ShSand)
                        Column {
                            Text("86%", fontSize = 22.sp, fontWeight = FontWeight.Bold, fontFamily = DmSansFamily, color = ShInk)
                            Text("CONSISTENCY", fontSize = 9.sp, fontWeight = FontWeight.Bold, fontFamily = DmSansFamily, color = ShFog, letterSpacing = 1.sp)
                        }
                    }
                }
                ShoshinCard(modifier = Modifier.width(115.dp)) {
                    Column(modifier = Modifier.padding(20.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("🔥", fontSize = 24.sp); Spacer(Modifier.height(6.dp))
                        Text("$streak", fontSize = 26.sp, fontWeight = FontWeight.Bold, fontFamily = DmSansFamily, color = ShInk)
                        Text("MORNINGS\nKEPT", fontSize = 9.sp, fontWeight = FontWeight.Bold, fontFamily = DmSansFamily, color = ShFog, letterSpacing = 1.sp, textAlign = TextAlign.Center)
                    }
                }
            }

            Spacer(Modifier.height(24.dp))

            // Backwards Clock Preview Card
            ShoshinCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { navController.navigate(ShRoutes.CLOCK) }
            ) {
                Row(
                    modifier = Modifier.padding(20.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("TIME REMAINING", fontSize = 9.sp, fontWeight = FontWeight.Bold, fontFamily = DmSansFamily, color = ShFog, letterSpacing = 1.sp)
                        Spacer(Modifier.height(4.dp))
                        Text("Day is counting down", fontSize = 17.sp, fontWeight = FontWeight.SemiBold, fontFamily = DmSansFamily, color = ShInk)
                    }
                    Box(
                        modifier = Modifier
                            .size(56.dp)
                            .clip(CircleShape)
                            .background(ShSand),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_pulse), // Using pulse icon as placeholder for clock
                            contentDescription = "Clock",
                            tint = ShVermillion,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                }
            }

            Spacer(Modifier.height(24.dp))

            // The Bridge
            Row(modifier = Modifier.fillMaxWidth().padding(bottom = 14.dp), verticalAlignment = Alignment.CenterVertically) {
                Kicker("The Bridge", color = ShVermillion, modifier = Modifier.weight(1f))
                Text("Tomorrow's path", fontSize = 17.sp, fontWeight = FontWeight.SemiBold, fontFamily = DmSansFamily, color = ShInk, modifier = Modifier.weight(2f))
                Text("Edit", fontSize = 14.sp, color = ShFog, fontFamily = DmSansFamily, modifier = Modifier.clickable { navController.navigate("routine_editor") })
            }

            ShoshinCard(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp)) {
                    t.steps.forEachIndexed { i, step ->
                        CheckpointRow(number = i + 1, label = step, state = CheckpointState.PENDING)
                        if (i < t.steps.lastIndex) HorizontalDivider(color = ShLine, thickness = 1.dp)
                    }
                }
            }

            Spacer(Modifier.height(32.dp))

            // CTA
            ShoshinButton(
                onClick = { navController.navigate("morning/activation") },
                variant = ShButtonVariant.Accent,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(painterResource(R.drawable.ic_bolt), null, modifier = Modifier.size(20.dp), tint = Color.White)
                Spacer(Modifier.width(10.dp))
                Text("Begin Morning Practice", fontWeight = FontWeight.Bold)
            }

            Spacer(Modifier.height(40.dp))
        }
    }

    // Conflict Dialog
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
