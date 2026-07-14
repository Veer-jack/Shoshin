package com.example.shoshinapp.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.Alignment
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.shoshinapp.R
import com.example.shoshinapp.navigation.ShRoutes
import com.example.shoshinapp.ui.components.*
import com.example.shoshinapp.ui.theme.*
import com.example.shoshinapp.viewmodel.GroupViewModel
import com.example.shoshinapp.viewmodel.GroupStatsViewModel
import com.example.shoshinapp.utils.ErrorHandler
import com.google.firebase.auth.FirebaseAuth
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun GroupDetailScreen(
    navController: NavController,
    groupId: String,
    viewModel: GroupViewModel = viewModel(),
    statsViewModel: GroupStatsViewModel? = null
) {
    var selectedTab by remember { mutableStateOf(0) }
    val group by viewModel.currentGroup.collectAsState()
    val posts by viewModel.groupPosts.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    
    val membersSummary by statsViewModel?.members?.collectAsState(initial = emptyList()) ?: remember { mutableStateOf(emptyList()) }
    val groupStats by statsViewModel?.stats?.collectAsState(initial = null) ?: remember { mutableStateOf(null) }

    val userId = FirebaseAuth.getInstance().currentUser?.uid ?: ""
    val isCreator = group?.createdBy == userId
    val isFull = (group?.members?.size ?: 0) >= (groupStats?.totalMemberCount ?: 5) // Simplified logic for UI

    val context = LocalContext.current

    LaunchedEffect(groupId) {
        viewModel.loadGroupMembers(groupId)
        viewModel.loadGroupPosts(groupId)
        statsViewModel?.loadGroupData(groupId)
    }

    if (isLoading && group == null) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = ShVermillion)
        }
    } else {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(ShPaper)
                .verticalScroll(rememberScrollState())
                .padding(24.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(painterResource(R.drawable.ic_arrow_left), "Back", tint = ShInk)
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = group?.name ?: "Group Details",
                        style = MaterialTheme.typography.displayMedium.copy(fontSize = 24.sp),
                        color = ShInk
                    )
                    Text("Collective Practice", style = ShLabelStyle, color = ShFog)
                }
                Spacer(Modifier.width(48.dp))
            }

            Spacer(modifier = Modifier.height(24.dp))
            
            // FULL GROUP BANNER (Feature 4.4)
            if (isFull && isCreator) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 24.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(ShVermillion.copy(alpha = 0.1f))
                        .clickable { navController.navigate(ShRoutes.REFERRALS) }
                        .padding(16.dp)
                ) {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Icon(painterResource(R.drawable.ic_groups), contentDescription = null, tint = ShVermillion, modifier = Modifier.size(18.dp))
                            Text(
                                "Group Full (${group?.members?.size ?: 0}/${groupStats?.totalMemberCount ?: 5} members)",
                                style = ShBodyStyle,
                                fontWeight = FontWeight.Bold,
                                color = ShVermillion
                            )
                        }
                        Text("Refer a friend to expand →", style = ShLabelStyle, color = ShVermillion)
                    }
                }
            }

            // Group Stats Section (Feature 3.2)
            groupStats?.let { stats ->
                GroupStatsSection(
                    activeCount = stats.activeMembersThisWeek,
                    totalCount = stats.totalMemberCount,
                    avgStreak = stats.averageStreak,
                    checkpointsThisMonth = stats.totalCheckpointsThisMonth
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Leaderboard Section (Feature 3.2)
            GroupLeaderboard(
                members = membersSummary,
                onMemberTap = { /* Navigate to member profile */ }
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Existing Feed/Discussion Tab
            Kicker("DISCUSSION")
            Spacer(Modifier.height(12.dp))
            ShoshinCard(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    if (posts.isEmpty()) {
                        Text("No posts yet. Start the conversation!", style = ShBodyStyle, color = ShFog)
                    } else {
                        posts.take(3).forEach { post ->
                            Text(post.content, style = ShBodyStyle)
                            HorizontalDivider(color = ShLine, modifier = Modifier.padding(vertical = 8.dp))
                        }
                    }
                    TextButton(
                        onClick = { /* Open full feed */ },
                        modifier = Modifier.align(Alignment.End)
                    ) {
                        Text("View all posts →", color = ShInk, style = ShLabelStyle)
                    }
                }
            }

            Spacer(Modifier.height(32.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                ShoshinButton(
                    onClick = { /* Open Invite */ },
                    variant = ShButtonVariant.Accent,
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Invite Members")
                }
                
                ShoshinButton(
                    onClick = {
                        viewModel.leaveGroup(groupId)
                        navController.popBackStack()
                    },
                    variant = ShButtonVariant.Ghost,
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Leave Group")
                }
            }

            Spacer(Modifier.height(48.dp))
        }
    }
}
