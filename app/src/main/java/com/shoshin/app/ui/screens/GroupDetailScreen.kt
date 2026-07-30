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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.Shoshin.app.R
import com.Shoshin.app.navigation.ShRoutes
import com.Shoshin.app.ui.components.*
import com.Shoshin.app.ui.theme.*
import com.Shoshin.app.viewmodel.GroupViewModel
import com.Shoshin.app.viewmodel.GroupStatsViewModel
import com.google.firebase.auth.FirebaseAuth

@Composable
fun GroupDetailScreen(
    navController: NavController,
    groupId: String,
    viewModel: GroupViewModel = viewModel(),
    statsViewModel: GroupStatsViewModel? = null
) {
    val group by viewModel.currentGroup.collectAsState()
    val members by viewModel.groupMembers.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val userId = FirebaseAuth.getInstance().currentUser?.uid ?: ""

    var showDeleteDialog by remember { mutableStateOf(false) }
    var showLeaveDialog by remember { mutableStateOf(false) }
    val isOwner = group?.createdBy == userId

    LaunchedEffect(groupId) {
        viewModel.loadGroupMembers(groupId)
        statsViewModel?.loadGroupData(groupId)
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
                Surface(
                    color = MaterialTheme.colorScheme.onBackground,
                    shape = RoundedCornerShape(999.dp)
                ) {
                    Text(
                        text = group?.name ?: "Dawn Circle",
                        color = MaterialTheme.colorScheme.background,
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp),
                        style = ShLabelStyle.copy(fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    )
                }
                IconButton(onClick = { navController.navigate(ShRoutes.groupLeaderboard(groupId)) }) {
                    Icon(painterResource(R.drawable.ic_trophy), null, tint = MaterialTheme.colorScheme.onBackground, modifier = Modifier.size(24.dp))
                }
            }

            if (isLoading && members.isEmpty()) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                }
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = 24.dp)
                ) {
                    Kicker("ACCOUNTABILITY", color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Spacer(Modifier.height(4.dp))
                    Text(
                        "Your circle", 
                        style = ShTitleStyle.copy(fontSize = 32.sp, color = MaterialTheme.colorScheme.onBackground)
                    )

                    Spacer(Modifier.height(24.dp))

                    // Summary Hero Card - Stays Dark for "Nike focus"
                    val activeCount = members.count { it.consistencyStreak > 0 }
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(32.dp))
                            .background(ShNight2)
                            .padding(24.dp)
                    ) {
                        Enso(size = 140, color = ShVermillionLight.copy(alpha = 0.12f), strokeWidth = 6f, modifier = Modifier.align(Alignment.TopEnd).offset(x = 40.dp, y = (-20).dp))
                        Column {
                            Kicker("THIS MORNING · 5:30 AM", color = ShNightMuted)
                            Spacer(Modifier.height(16.dp))
                            Row(verticalAlignment = Alignment.Bottom) {
                                Text(activeCount.toString(), style = ShNumeralStyle.copy(fontSize = 48.sp, color = Color.White))
                                Spacer(Modifier.width(8.dp))
                                Text("of ${members.size} have begun", style = ShTitleStyle.copy(fontSize = 24.sp, color = ShNightMuted))
                            }
                            Spacer(Modifier.height(16.dp))
                            Text(
                                "You rose with Mei and Rahul. Sit together at dawn.",
                                style = ShBodyStyle,
                                color = ShNightMuted,
                                lineHeight = 22.sp
                            )
                        }
                    }

                    Spacer(Modifier.height(24.dp))

                    // Member List Card
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(24.dp))
                            .background(MaterialTheme.colorScheme.surfaceVariant)
                    ) {
                        Column(modifier = Modifier.padding(vertical = 8.dp)) {
                            members.forEachIndexed { index, m ->
                                val isMe = m.userId == userId
                                MemberRow(
                                    name = if (isMe) "${m.name} (you)" else m.name,
                                    status = if (m.consistencyStreak > 0) "Practicing" else "Asleep",
                                    statusColor = if (m.consistencyStreak > 0) ShMatcha else MaterialTheme.colorScheme.outline,
                                    streak = m.consistencyStreak,
                                    isMe = isMe
                                )
                                if (index < members.lastIndex) {
                                    HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f), modifier = Modifier.padding(start = 72.dp, end = 24.dp))
                                }
                            }
                        }
                    }

                    Spacer(Modifier.height(24.dp))

                    // Invite Action
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(64.dp)
                            .clip(RoundedCornerShape(20.dp))
                            .border(1.2.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(20.dp))
                            .clickable { navController.navigate(ShRoutes.groupInvite(groupId)) }
                            .padding(horizontal = 18.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(painterResource(R.drawable.ic_plus), null, modifier = Modifier.size(18.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
                            Spacer(Modifier.width(8.dp))
                            Text("Invite someone to the circle", style = ShLabelStyle.copy(color = MaterialTheme.colorScheme.onSurfaceVariant, fontWeight = FontWeight.Bold))
                        }
                    }

                    Spacer(Modifier.height(16.dp))

                    // Danger Zone - Exit/Delete
                    if (isOwner) {
                        ShoshinButton(
                            onClick = { showDeleteDialog = true },
                            variant = ShButtonVariant.Dark,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(painterResource(R.drawable.ic_trash), null, tint = ShError, modifier = Modifier.size(18.dp))
                            Spacer(Modifier.width(8.dp))
                            Text("Delete Circle", color = ShError, fontWeight = FontWeight.Bold)
                        }
                    } else {
                        ShoshinButton(
                            onClick = { showLeaveDialog = true },
                            variant = ShButtonVariant.Dark,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(painterResource(R.drawable.ic_logout), null, tint = ShError, modifier = Modifier.size(18.dp))
                            Spacer(Modifier.width(8.dp))
                            Text("Leave Circle", color = ShError, fontWeight = FontWeight.Bold)
                        }
                    }

                    Spacer(Modifier.height(48.dp))
                }
            }
        }
    }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Delete Circle?", style = ShTitleStyle.copy(fontSize = 20.sp, color = MaterialTheme.colorScheme.onSurface)) },
            text = { Text("This action cannot be undone. All members will be removed.", style = ShBodyStyle, color = MaterialTheme.colorScheme.onSurfaceVariant) },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.deleteGroup(groupId)
                    showDeleteDialog = false
                    navController.popBackStack()
                }) {
                    Text("Delete", color = ShError, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Cancel", color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            },
            containerColor = MaterialTheme.colorScheme.surface,
            shape = RoundedCornerShape(24.dp)
        )
    }

    if (showLeaveDialog) {
        AlertDialog(
            onDismissRequest = { showLeaveDialog = false },
            title = { Text("Leave Circle?", style = ShTitleStyle.copy(fontSize = 20.sp, color = MaterialTheme.colorScheme.onSurface)) },
            text = { Text("You will no longer see morning updates from this circle.", style = ShBodyStyle, color = MaterialTheme.colorScheme.onSurfaceVariant) },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.leaveGroup(groupId)
                    showLeaveDialog = false
                    navController.popBackStack()
                }) {
                    Text("Leave", color = ShError, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showLeaveDialog = false }) {
                    Text("Cancel", color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            },
            containerColor = MaterialTheme.colorScheme.surface,
            shape = RoundedCornerShape(24.dp)
        )
    }
}

@Composable
private fun MemberRow(
    name: String,
    status: String,
    statusColor: Color,
    streak: Int,
    isMe: Boolean = false
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(if (isMe) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.surface),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = name.firstOrNull()?.toString()?.uppercase() ?: "U",
                style = ShH2Style.copy(fontSize = 17.sp, color = if (isMe) MaterialTheme.colorScheme.surface else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
            )
        }
        
        Spacer(Modifier.width(16.dp))
        
        Column(modifier = Modifier.weight(1f)) {
            Text(name, style = ShH2Style.copy(fontSize = 16.sp, color = MaterialTheme.colorScheme.onSurface))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(modifier = Modifier.size(6.dp).clip(CircleShape).background(statusColor))
                Spacer(Modifier.width(6.dp))
                Text(status, style = ShLabelStyle, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
        
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            Icon(painterResource(R.drawable.ic_flame), null, modifier = Modifier.size(14.dp), tint = ShVermillion)
            Text(
                text = streak.toString(), 
                style = ShNumeralStyle.copy(fontSize = 16.sp, fontWeight = FontWeight.Bold), 
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}
