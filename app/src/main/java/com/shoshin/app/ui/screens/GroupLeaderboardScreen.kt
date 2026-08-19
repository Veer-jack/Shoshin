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
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.Shoshin.app.R
import com.Shoshin.app.data.db.entities.toBadgeIdList
import com.Shoshin.app.data.groups.Group
import com.Shoshin.app.data.groups.GroupMember
import com.Shoshin.app.data.models.BadgeDefinitions
import com.Shoshin.app.ui.components.*
import com.Shoshin.app.ui.theme.*
import com.Shoshin.app.viewmodel.GroupViewModel
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.delay

@Composable
fun GroupLeaderboardScreen(
    navController: NavController,
    groupId: String,
    viewModel: GroupViewModel,
    networkMonitor: com.Shoshin.app.sync.NetworkStateMonitor? = null
) {
    val scrollState = rememberScrollState()
    var selectedTimeframe by remember { mutableIntStateOf(0) }
    var selectedMemberId by remember { mutableStateOf<String?>(null) }

    val members by viewModel.groupMembers.collectAsState()
    val currentGroup by viewModel.currentGroup.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val userId = FirebaseAuth.getInstance().currentUser?.uid ?: ""
    val isOnline by networkMonitor?.isOnline?.collectAsState(initial = true) ?: remember { mutableStateOf(true) }

    LaunchedEffect(groupId) {
        viewModel.loadGroupMembers(groupId) // one-time fetch, just for the header/currentGroup
        viewModel.observeGroupMembers(groupId) // live leaderboard
    }

    // Convert GroupMember to LeaderboardEntry
    val leaderboard = remember(members) {
        members.sortedByDescending { it.consistencyStreak }.mapIndexed { index, m ->
            LeaderboardEntry(
                rank = index + 1,
                userId = m.userId,
                initial = m.name.firstOrNull()?.toString()?.uppercase() ?: "U",
                name = if (m.userId == userId) "${m.name} (you)" else m.name,
                streak = m.consistencyStreak,
                lastActive = m.lastCompletionDate,
                trend = "neutral", // Real trend would need history
                isYou = m.userId == userId
            )
        }
    }

    ShoshinTheme(type = ShoshinThemeType.DYNAMIC) {
        Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
        ) {
            // App Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(horizontal = 16.dp, vertical = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { navController.popBackStack() }, modifier = Modifier.size(24.dp)) {
                    Icon(painterResource(R.drawable.ic_arrow_left), contentDescription = "Back", tint = MaterialTheme.colorScheme.onBackground)
                }
                Spacer(Modifier.width(12.dp))
                Box(
                    modifier = Modifier.size(64.dp).clip(CircleShape).background(MaterialTheme.colorScheme.surfaceVariant),
                    contentAlignment = Alignment.Center
                ) {
                    val photoUrl = currentGroup?.photo
                    if (photoUrl != null) {
                        AsyncImage(
                            model = photoUrl,
                            contentDescription = "Circle image",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = androidx.compose.ui.layout.ContentScale.Crop
                        )
                    } else {
                        Text(
                            text = currentGroup?.name?.take(1)?.uppercase() ?: "C",
                            style = ShH2Style.copy(fontSize = 22.sp),
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        )
                    }
                }
                Spacer(Modifier.width(12.dp))
                Text(
                    "${currentGroup?.name?.takeIf { it.isNotBlank() } ?: "Circle"} leaderboard",
                    style = ShTitleStyle.copy(fontSize = 26.sp),
                    color = MaterialTheme.colorScheme.onBackground
                )
            }

            OfflineIndicator(isOffline = !isOnline)

            if (isLoading && leaderboard.isEmpty()) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = ShVermillion)
                }
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(scrollState)
                        .padding(horizontal = 24.dp)
                ) {
                    // Timeframe Selector
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        TimeframePill("This week", selectedTimeframe == 0) { selectedTimeframe = 0 }
                        TimeframePill("All time", selectedTimeframe == 1) { selectedTimeframe = 1 }
                    }

                    Spacer(Modifier.height(48.dp))

                    // Podium Visualization
                    if (leaderboard.isNotEmpty()) {
                        Podium(leaderboard.take(3))
                    }

                    Spacer(Modifier.height(48.dp))

                    // Full Ranking List
                    ShoshinCard(modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.padding(vertical = 8.dp)) {
                            leaderboard.forEachIndexed { i, entry ->
                                LeaderboardRow(entry, onClick = { selectedMemberId = entry.userId })
                                if (i < leaderboard.lastIndex) {
                                    HorizontalDivider(
                                        color = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f),
                                        thickness = 1.dp,
                                        modifier = Modifier.padding(horizontal = 24.dp)
                                    )
                                }
                            }
                        }
                    }

                    Spacer(Modifier.height(48.dp))
                }
            }
        }

        if (selectedMemberId != null) {
            MemberProfileModal(
                userId = selectedMemberId!!,
                currentGroupId = groupId,
                currentGroupName = currentGroup?.name?.takeIf { it.isNotBlank() } ?: "Circle",
                currentMembers = members,
                viewModel = viewModel,
                onDismiss = { selectedMemberId = null }
            )
        }
        }
    }
}

@Composable
private fun TimeframePill(label: String, isSelected: Boolean, onClick: () -> Unit) {
    Surface(
        color = if (isSelected) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.surfaceVariant,
        shape = RoundedCornerShape(999.dp),
        modifier = Modifier.clickable { onClick() }
    ) {
        Text(
            text = label,
            color = if (isSelected) MaterialTheme.colorScheme.surface else MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            style = ShLabelStyle.copy(fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium)
        )
    }
}

@Composable
private fun Podium(top3: List<LeaderboardEntry>) {
    val podiumOrder = when (top3.size) {
        1 -> listOf(top3[0])
        2 -> listOf(top3[1], top3[0])
        else -> listOf(top3[1], top3[0], top3[2])
    }

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.Bottom
    ) {
        podiumOrder.forEach { entry ->
            val isFirst = entry.rank == 1
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(horizontal = 12.dp)
            ) {
                if (isFirst) {
                    Icon(
                        painter = painterResource(R.drawable.ic_trophy), // Use crown if available, trophy is placeholder
                        contentDescription = null,
                        modifier = Modifier.size(24.dp),
                        tint = ShVermillion
                    )
                    Spacer(Modifier.height(8.dp))
                }
                
                Box(
                    modifier = Modifier
                        .size(if (isFirst) 84.dp else 72.dp)
                        .clip(CircleShape)
                        .then(
                            if (isFirst) Modifier.border(3.dp, ShVermillion, CircleShape) else Modifier
                        )
                        .background(if (entry.isYou) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.surfaceVariant),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = entry.initial,
                        style = ShTitleStyle.copy(
                            fontSize = if (isFirst) 32.sp else 28.sp,
                            color = if (entry.isYou) MaterialTheme.colorScheme.surface else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        )
                    )
                }
                
                Spacer(Modifier.height(12.dp))
                
                Text(
                    entry.name,
                    style = ShLabelStyle.copy(fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                )
                
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    Icon(painterResource(R.drawable.ic_flame), null, modifier = Modifier.size(12.dp), tint = ShVermillion)
                    Text(
                        entry.streak.toString(), 
                        style = ShNumeralStyle.copy(fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurface)
                    )
                }
            }
        }
    }
}

@Composable
private fun LeaderboardRow(entry: LeaderboardEntry, onClick: () -> Unit) {
    val rowBg = if (entry.isYou) MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f) else Color.Transparent

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(rowBg)
            .clickable { onClick() }
            .padding(horizontal = 24.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = entry.rank.toString(),
            style = ShLabelStyle,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.width(24.dp)
        )
        
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.surfaceVariant),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = entry.initial,
                style = ShH2Style.copy(fontSize = 17.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
            )
        }
        
        Spacer(Modifier.width(16.dp))
        
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = entry.name,
                style = ShH2Style.copy(fontSize = 16.sp),
                color = MaterialTheme.colorScheme.onSurface
            )
            if (entry.lastActive.isNotBlank()) {
                Text(
                    text = "Last active ${entry.lastActive}",
                    style = ShLabelStyle.copy(fontSize = 11.sp),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            Icon(painterResource(R.drawable.ic_flame), null, modifier = Modifier.size(14.dp), tint = ShVermillion)
            Text(
                text = entry.streak.toString(), 
                style = ShNumeralStyle.copy(fontSize = 16.sp, fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

private data class LeaderboardEntry(
    val rank: Int,
    val userId: String,
    val initial: String,
    val name: String,
    val streak: Int,
    val lastActive: String,
    val trend: String,
    val isYou: Boolean = false
)

@Composable
private fun MemberProfileModal(
    userId: String,
    currentGroupId: String,
    currentGroupName: String,
    currentMembers: List<GroupMember>,
    viewModel: GroupViewModel,
    onDismiss: () -> Unit
) {
    val profile by viewModel.selectedMemberProfile.collectAsState()
    val groupsInCommon by viewModel.selectedMemberGroupsInCommon.collectAsState()
    val isLoading by viewModel.memberProfileLoading.collectAsState()
    val actionResult by viewModel.memberActionResult.collectAsState()
    var showAddConfirm by remember { mutableStateOf(false) }

    LaunchedEffect(userId) {
        viewModel.loadMemberProfile(userId)
    }

    DisposableEffect(Unit) {
        onDispose { viewModel.clearSelectedMember() }
    }

    LaunchedEffect(actionResult) {
        if (actionResult != null) {
            delay(3000)
            viewModel.clearMemberActionResult()
        }
    }

    val isAlreadyMember = currentMembers.any { it.userId == userId }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.5f))
            .clickable { onDismiss() },
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .clickable(enabled = false) {}
        ) {
            ShoshinCard(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(24.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Profile", style = ShH2Style.copy(fontSize = 18.sp), color = MaterialTheme.colorScheme.onSurface)
                        IconButton(onClick = onDismiss) {
                            Icon(painterResource(R.drawable.ic_close), contentDescription = "Close", tint = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }

                    if (isLoading && profile == null) {
                        Box(Modifier.fillMaxWidth().padding(vertical = 32.dp), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator(color = ShVermillion)
                        }
                    } else if (profile != null) {
                        val member = profile!!
                        Spacer(Modifier.height(16.dp))

                        Box(
                            modifier = Modifier
                                .size(72.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.surfaceVariant),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                member.displayName.take(1).uppercase(),
                                style = ShTitleStyle.copy(fontSize = 28.sp),
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                            )
                        }

                        Spacer(Modifier.height(12.dp))

                        Text(member.displayName, style = ShH2Style.copy(fontSize = 20.sp), color = MaterialTheme.colorScheme.onSurface)

                        Spacer(Modifier.height(8.dp))

                        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                            Column {
                                Text("${member.currentStreak}", style = ShNumeralStyle.copy(fontSize = 20.sp), color = ShVermillion)
                                Text("CURRENT", style = ShKickerStyle.copy(fontSize = 9.sp), color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                            Column {
                                Text("${member.bestStreak}", style = ShNumeralStyle.copy(fontSize = 20.sp), color = MaterialTheme.colorScheme.onSurface)
                                Text("BEST", style = ShKickerStyle.copy(fontSize = 9.sp), color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }

                        val badgeIds = member.unlockedBadgeIds.toBadgeIdList()
                        if (badgeIds.isNotEmpty()) {
                            Spacer(Modifier.height(12.dp))
                            val badgeNames = badgeIds.mapNotNull { id -> BadgeDefinitions.ALL_BADGES.find { it.id == id }?.name }
                            Text("Badges: ${badgeNames.joinToString(", ")}", style = ShLabelStyle.copy(fontSize = 12.sp), color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }

                        if (!member.bio.isNullOrBlank()) {
                            Spacer(Modifier.height(12.dp))
                            Text(
                                "\"${member.bio}\"",
                                style = ShBodyStyle.copy(fontSize = 13.sp, fontStyle = FontStyle.Italic),
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        if (member.height != null || member.weight != null) {
                            Spacer(Modifier.height(8.dp))
                            val parts = buildList {
                                member.height?.let { add("Height: $it ${member.heightUnit}") }
                                member.weight?.let { add("Weight: $it ${member.weightUnit}") }
                            }
                            Text(parts.joinToString(" · "), style = ShLabelStyle.copy(fontSize = 12.sp), color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }

                        if (groupsInCommon.isNotEmpty()) {
                            Spacer(Modifier.height(16.dp))
                            Text(
                                "Groups in common (${groupsInCommon.size})",
                                style = ShLabelStyle.copy(fontSize = 12.sp, fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(Modifier.height(8.dp))
                            groupsInCommon.forEach { group ->
                                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(vertical = 2.dp)) {
                                    Text("• ${group.name}", style = ShLabelStyle.copy(fontSize = 13.sp), color = MaterialTheme.colorScheme.onSurface)
                                    if (group.id == currentGroupId) {
                                        Spacer(Modifier.width(6.dp))
                                        Text("(Current)", style = ShLabelStyle.copy(fontSize = 10.sp), color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    }
                                }
                            }
                        }

                        if (actionResult != null) {
                            Spacer(Modifier.height(16.dp))
                            Text(actionResult!!, style = ShLabelStyle.copy(fontSize = 12.sp), color = ShMatcha)
                        }

                        if (!isAlreadyMember) {
                            Spacer(Modifier.height(20.dp))
                            ShoshinButton(
                                onClick = { showAddConfirm = true },
                                variant = ShButtonVariant.Accent,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text("Add to Group")
                            }
                        }

                        Spacer(Modifier.height(12.dp))
                        ShoshinButton(
                            onClick = onDismiss,
                            variant = ShButtonVariant.Ghost,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Close")
                        }
                    }
                }
            }
        }
    }

    if (showAddConfirm && profile != null) {
        AlertDialog(
            onDismissRequest = { showAddConfirm = false },
            title = { Text("Add ${profile!!.displayName} to $currentGroupName?") },
            text = { Text("They'll immediately appear in this group's leaderboard.") },
            confirmButton = {
                TextButton(onClick = {
                    showAddConfirm = false
                    viewModel.addMemberToCurrentGroup(currentGroupId, currentGroupName, userId, profile!!.displayName)
                }) { Text("Add") }
            },
            dismissButton = {
                TextButton(onClick = { showAddConfirm = false }) { Text("Cancel") }
            }
        )
    }
}
