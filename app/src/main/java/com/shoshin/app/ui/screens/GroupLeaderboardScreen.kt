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
import androidx.navigation.NavController
import com.Shoshin.app.R
import com.Shoshin.app.ui.components.*
import com.Shoshin.app.ui.theme.*
import com.Shoshin.app.viewmodel.GroupViewModel
import com.google.firebase.auth.FirebaseAuth

@Composable
fun GroupLeaderboardScreen(
    navController: NavController,
    groupId: String,
    viewModel: GroupViewModel
) {
    val scrollState = rememberScrollState()
    var selectedTimeframe by remember { mutableIntStateOf(0) }
    
    val members by viewModel.groupMembers.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val userId = FirebaseAuth.getInstance().currentUser?.uid ?: ""

    LaunchedEffect(groupId) {
        viewModel.loadGroupMembers(groupId)
    }

    // Convert GroupMember to LeaderboardEntry
    val leaderboard = remember(members) {
        members.sortedByDescending { it.consistencyStreak }.mapIndexed { index, m ->
            LeaderboardEntry(
                rank = index + 1,
                initial = m.name.firstOrNull()?.toString()?.uppercase() ?: "U",
                name = if (m.userId == userId) "${m.name} (you)" else m.name,
                streak = m.consistencyStreak,
                trend = "neutral", // Real trend would need history
                isYou = m.userId == userId
            )
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(ShPaper)
    ) {
        // App Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(horizontal = 24.dp, vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { navController.popBackStack() }, modifier = Modifier.size(24.dp)) {
                Icon(painterResource(R.drawable.ic_arrow_left), contentDescription = "Back", tint = ShInk)
            }
            Spacer(Modifier.width(16.dp))
            Text("Circle leaderboard", style = ShTitleStyle.copy(fontSize = 28.sp), color = ShInk)
        }

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
                            LeaderboardRow(entry)
                            if (i < leaderboard.lastIndex) {
                                HorizontalDivider(
                                    color = ShLine, 
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
}

@Composable
private fun TimeframePill(label: String, isSelected: Boolean, onClick: () -> Unit) {
    Surface(
        color = if (isSelected) ShInk else ShPaper2,
        shape = RoundedCornerShape(999.dp),
        modifier = Modifier.clickable { onClick() }
    ) {
        Text(
            text = label,
            color = if (isSelected) Color.White else ShFog,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            style = ShLabelStyle.copy(fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium)
        )
    }
}

@Composable
private fun Podium(top3: List<LeaderboardEntry>) {
    // If only one or two members, still show them centered
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
                        painter = painterResource(R.drawable.ic_trophy),
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
                        .background(if (entry.isYou) ShInk else ShPaper2),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = entry.initial,
                        style = ShTitleStyle.copy(
                            fontSize = if (isFirst) 32.sp else 28.sp,
                            color = if (entry.isYou) Color.White else ShInk.copy(alpha = 0.6f)
                        )
                    )
                }
                
                Spacer(Modifier.height(12.dp))
                
                Text(
                    entry.name,
                    style = ShLabelStyle.copy(fontWeight = FontWeight.Bold, color = ShInk)
                )
                
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    Icon(painterResource(R.drawable.ic_flame), null, modifier = Modifier.size(12.dp), tint = ShVermillion)
                    Text(
                        entry.streak.toString(), 
                        style = ShNumeralStyle.copy(fontSize = 14.sp, color = ShInk)
                    )
                }
            }
        }
    }
}

@Composable
private fun LeaderboardRow(entry: LeaderboardEntry) {
    val rowBg = if (entry.isYou) ShPaper2.copy(alpha = 0.5f) else Color.Transparent
    
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(rowBg)
            .padding(horizontal = 24.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = entry.rank.toString(),
            style = ShLabelStyle,
            color = ShFog,
            modifier = Modifier.width(24.dp)
        )
        
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(ShPaper2),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = entry.initial,
                style = ShH2Style.copy(fontSize = 17.sp, color = ShInk.copy(alpha = 0.6f))
            )
        }
        
        Spacer(Modifier.width(16.dp))
        
        Text(
            text = entry.name,
            style = ShH2Style.copy(fontSize = 16.sp),
            modifier = Modifier.weight(1f)
        )
        
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            Icon(painterResource(R.drawable.ic_flame), null, modifier = Modifier.size(14.dp), tint = ShVermillion)
            Text(
                text = entry.streak.toString(), 
                style = ShNumeralStyle.copy(fontSize = 16.sp, fontWeight = FontWeight.Bold),
                color = ShInk
            )
        }
    }
}

private data class LeaderboardEntry(
    val rank: Int,
    val initial: String,
    val name: String,
    val streak: Int,
    val trend: String,
    val isYou: Boolean = false
)
