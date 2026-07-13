package com.example.shoshinapp.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.shoshinapp.data.models.UserSummary
import com.example.shoshinapp.ui.theme.*

enum class LeaderboardTab { STREAK, CHECKPOINTS, BADGES }

@Composable
fun GroupLeaderboard(
    members: List<UserSummary>,
    onMemberTap: (String) -> Unit
) {
    var selectedTab by remember { mutableStateOf(LeaderboardTab.STREAK) }
    
    val sortedMembers = remember(members, selectedTab) {
        when (selectedTab) {
            LeaderboardTab.STREAK -> members.sortedByDescending { it.currentStreak }
            LeaderboardTab.CHECKPOINTS -> members.sortedByDescending { it.totalCheckpoints }
            LeaderboardTab.BADGES -> members.sortedByDescending { it.badgeCount }
        }
    }

    Column {
        Kicker("LEADERBOARD")
        Spacer(Modifier.height(12.dp))
        
        // Tabs
        Row(
            modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            LeaderboardTabButton("By Streak", selectedTab == LeaderboardTab.STREAK) { selectedTab = LeaderboardTab.STREAK }
            LeaderboardTabButton("By Checkpoints", selectedTab == LeaderboardTab.CHECKPOINTS) { selectedTab = LeaderboardTab.CHECKPOINTS }
            LeaderboardTabButton("By Badges", selectedTab == LeaderboardTab.BADGES) { selectedTab = LeaderboardTab.BADGES }
        }

        ShoshinCard(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(vertical = 8.dp)) {
                sortedMembers.forEachIndexed { index, member ->
                    LeaderboardRow(
                        rank = index + 1,
                        member = member,
                        tab = selectedTab,
                        onClick = { onMemberTap(member.userId) }
                    )
                    if (index < sortedMembers.lastIndex) {
                        HorizontalDivider(color = ShLine, modifier = Modifier.padding(horizontal = 20.dp))
                    }
                }
            }
        }
    }
}

@Composable
private fun LeaderboardTabButton(label: String, isSelected: Boolean, onClick: () -> Unit) {
    Surface(
        onClick = onClick,
        color = if (isSelected) ShInk else ShSand,
        shape = CircleShape,
        modifier = Modifier.height(32.dp)
    ) {
        Text(
            text = label,
            color = if (isSelected) Color.White else ShFog,
            style = ShLabelStyle,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
        )
    }
}

@Composable
private fun LeaderboardRow(
    rank: Int,
    member: UserSummary,
    tab: LeaderboardTab,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 20.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        val rankText = when (rank) {
            1 -> "🥇"
            2 -> "🥈"
            3 -> "🥉"
            else -> rank.toString()
        }
        
        Text(
            text = rankText,
            style = ShBodyStyle,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.width(30.dp)
        )
        
        Spacer(Modifier.width(8.dp))
        
        Text(
            text = member.userName,
            style = ShBodyStyle,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.weight(1f)
        )
        
        val valueText = when (tab) {
            LeaderboardTab.STREAK -> "${member.currentStreak} days"
            LeaderboardTab.CHECKPOINTS -> "${member.totalCheckpoints} total"
            LeaderboardTab.BADGES -> "${member.badgeCount} badges"
        }
        
        Text(
            text = valueText,
            style = ShLabelStyle,
            color = ShFog
        )
    }
}
