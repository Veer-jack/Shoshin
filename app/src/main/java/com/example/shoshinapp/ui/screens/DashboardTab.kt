package com.example.shoshinapp.ui.screens

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
    conflictResolver: ConflictResolver,
    streakViewModel: com.example.shoshinapp.viewmodel.StreakViewModel,
    friendViewModel: com.example.shoshinapp.viewmodel.FriendStreaksViewModel? = null
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
            Row(modifier = Modifier.fillMaxWidth().padding(bottom = 24.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.Top) {
                Column {
                    Text(dateStr.uppercase(), fontSize = 11.sp, fontWeight = FontWeight.Medium, fontFamily = DmSansFamily, color = MaterialTheme.colorScheme.onSurfaceVariant, letterSpacing = 2.sp)
                    Spacer(Modifier.height(6.dp))
                    Text("$greeting,\n$userName", fontSize = 32.sp, fontWeight = FontWeight.SemiBold, fontFamily = CormorantFamily, color = MaterialTheme.colorScheme.onBackground, lineHeight = 36.sp)
                }
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    // Notifications Button
                    IconButton(
                        onClick = { navController.navigate(ShRoutes.NOTIFICATIONS) }
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_bell),
                            contentDescription = "Notifications",
                            tint = MaterialTheme.colorScheme.onBackground
                        )
                    }

                    // Combined Share & Profile Pill
                    Row(
                        modifier = Modifier
                            .height(44.dp)
                            .clip(RoundedCornerShape(22.dp))
                            .background(ShPaper2)
                            .border(1.dp, ShLine, RoundedCornerShape(22.dp)),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Share Action
                        IconButton(
                            onClick = { 
                                navController.navigate(ShRoutes.streakShare(streak, t.name, user?.streakStartDate ?: 0L))
                            },
                            modifier = Modifier.size(44.dp)
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_share),
                                contentDescription = "Share",
                                tint = MaterialTheme.colorScheme.onBackground,
                                modifier = Modifier.size(18.dp)
                            )
                        }

                        // Vertical Divider
                        Box(
                            modifier = Modifier
                                .width(1.dp)
                                .height(20.dp)
                                .background(ShLine)
                        )

                        // Profile Action
                        IconButton(
                            onClick = { navController.navigate("profile") },
                            modifier = Modifier.size(44.dp)
                        ) {
                            Icon(
                                painter = painterResource(R.drawable.ic_user),
                                contentDescription = "Profile",
                                tint = MaterialTheme.colorScheme.onBackground,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
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
                        variant = ShButtonVariant.Ghost,
                        modifier = Modifier.fillMaxWidth().height(52.dp),
                        interactionSource = adjustInteractionSource,
                        pressedColor = Color.Black.copy(alpha = 0.05f)
                    ) {
                        Text(
                            "Adjust tomorrow's wake", 
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Medium,
                            color = ShInk
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

            // Streak Section
            ShoshinCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { navController.navigate(ShRoutes.STREAK_DETAILS) }
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    val streakColor = streakViewModel.getStreakColor(streak)
                    val badges = streakViewModel.getMilestoneBadges(streak)

                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_flame),
                            contentDescription = null,
                            tint = streakColor,
                            modifier = Modifier.size(32.dp)
                        )
                        Text(
                            text = streak.toString(),
                            fontSize = 48.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = DmSansFamily,
                            color = streakColor
                        )
                    }
                    
                    Text(
                        text = "DAYS IN A ROW",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Normal,
                        fontFamily = DmSansFamily,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        letterSpacing = 1.sp
                    )

                    if (badges.isNotEmpty()) {
                        Spacer(Modifier.height(8.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            badges.forEach { Text(it, fontSize = 20.sp) }
                        }
                    }
                    
                    Spacer(Modifier.height(4.dp))
                    
                    val startDateText = if ((user?.streakStartDate ?: 0) > 0) {
                        SimpleDateFormat("MMMM d, yyyy", Locale.getDefault()).format(Date(user!!.streakStartDate))
                    } else "Not started"
                    
                    Text(
                        text = "Started: $startDateText",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                        fontFamily = DmSansFamily
                    )
                    
                    if ((user?.streakFreezes ?: 0) > 0) {
                        Spacer(Modifier.height(16.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("Freeze Tokens: ", style = ShLabelStyle, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            repeat(user?.streakFreezes ?: 0) { i ->
                                val isUsed = i < (user?.freezesUsedThisMonth ?: 0)
                                Icon(
                                    painter = painterResource(id = R.drawable.ic_bolt),
                                    contentDescription = null,
                                    modifier = Modifier
                                        .padding(horizontal = 2.dp)
                                        .size(18.dp)
                                        .alpha(if (isUsed) 0.3f else 1f),
                                    tint = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }
                    
                    Spacer(Modifier.height(16.dp))
                    
                    ShoshinButton(
                        onClick = { 
                            navController.navigate(ShRoutes.streakShare(streak, t.name, user?.streakStartDate ?: 0L))
                        },
                        variant = ShButtonVariant.Primary,
                        modifier = Modifier.fillMaxWidth().height(48.dp)
                    ) {
                        Text("Share Your Streak")
                    }
                }
            }

            Spacer(Modifier.height(16.dp))

            FriendStreaksSection(
                friends = topFriends,
                totalCount = totalFriends,
                onSeeAll = { navController.navigate(ShRoutes.ALL_FRIENDS) },
                onInvite = { navController.navigate(ShRoutes.INVITE) },
                onFriendTap = { friendId -> navController.navigate(ShRoutes.friendProfile(friendId)) }
            )

            Spacer(Modifier.height(16.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(14.dp)) {
                ShoshinCard(modifier = Modifier.fillMaxWidth()) {
                    Row(modifier = Modifier.padding(20.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        val consistency = 0
                        RingProgress(percentage = consistency, size = 52, strokeWidth = 5f, valueText = consistency.toString(), color = ShMatcha, trackColor = MaterialTheme.colorScheme.surfaceVariant)
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                Text("$consistency%", fontSize = 22.sp, fontWeight = FontWeight.Bold, fontFamily = DmSansFamily, color = MaterialTheme.colorScheme.onBackground)
                                Icon(painterResource(R.drawable.ic_pulse), null, modifier = Modifier.size(14.dp), tint = ShMatcha)
                            }
                            Text("MORNINGS KEPT", fontSize = 9.sp, fontWeight = FontWeight.Bold, fontFamily = DmSansFamily, color = MaterialTheme.colorScheme.onSurfaceVariant, letterSpacing = 1.sp)
                        }
                    }
                }
            }

            Spacer(Modifier.height(24.dp))

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
                        Text("TIME REMAINING", fontSize = 9.sp, fontWeight = FontWeight.Bold, fontFamily = DmSansFamily, color = MaterialTheme.colorScheme.onSurfaceVariant, letterSpacing = 1.sp)
                        Spacer(Modifier.height(4.dp))
                        Text("Day is counting down", fontSize = 17.sp, fontWeight = FontWeight.SemiBold, fontFamily = DmSansFamily, color = MaterialTheme.colorScheme.onBackground)
                    }
                    Box(
                        modifier = Modifier
                            .size(56.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.surfaceVariant),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_clock),
                            contentDescription = "Clock",
                            tint = ShVermillion,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                }
            }

            Spacer(Modifier.height(24.dp))

            Row(modifier = Modifier.fillMaxWidth().padding(bottom = 14.dp), verticalAlignment = Alignment.CenterVertically) {
                Kicker("The Bridge", color = ShVermillion, modifier = Modifier.weight(1f))
                Row(modifier = Modifier.weight(2f), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Icon(painterResource(R.drawable.ic_walk), null, modifier = Modifier.size(18.dp), tint = ShInk)
                    Text("Morning Walk", fontSize = 17.sp, fontWeight = FontWeight.SemiBold, fontFamily = DmSansFamily, color = MaterialTheme.colorScheme.onBackground)
                }
                Text("Edit", fontSize = 14.sp, color = ShVermillion, fontWeight = FontWeight.Medium, fontFamily = DmSansFamily, modifier = Modifier.clickable { navController.navigate("routine_editor") })
            }

            ShoshinCard(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp)) {
                    t.steps.forEachIndexed { i, step ->
                        CheckpointRow(number = i + 1, label = step, state = CheckpointState.PENDING)
                        if (i < t.steps.lastIndex) HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f), thickness = 1.dp)
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
