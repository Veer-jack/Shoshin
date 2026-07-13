package com.example.shoshinapp.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.shoshinapp.data.models.Friend
import com.example.shoshinapp.ui.theme.*

@Composable
fun FriendStreaksSection(
    friends: List<Friend>,
    totalCount: Int,
    onSeeAll: () -> Unit,
    onInvite: () -> Unit,
    onFriendTap: (String) -> Unit
) {
    ShoshinCard(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "👥 YOUR COMMUNITY ($totalCount friends)",
                    style = ShKickerStyle,
                    color = ShFog
                )
            }

            Spacer(Modifier.height(16.dp))

            if (friends.isEmpty()) {
                Text(
                    "Building habits is better with friends.",
                    style = ShBodyStyle,
                    color = ShFog2,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            } else {
                friends.forEach { friend ->
                    FriendStreakRow(friend = friend) { onFriendTap(friend.userId) }
                    HorizontalDivider(color = ShLine, modifier = Modifier.padding(vertical = 12.dp))
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                TextButton(onClick = onSeeAll) {
                    Text("See all $totalCount friends →", style = ShLabelStyle, color = ShInk)
                }
                TextButton(onClick = onInvite) {
                    Text("+ Invite Friends", style = ShLabelStyle, color = ShVermillion)
                }
            }
        }
    }
}

@Composable
fun FriendStreakRow(friend: Friend, onClick: () -> Unit) {
    val streakColor = when {
        friend.currentStreak >= 100 -> Color(0xFFE91E63)
        friend.currentStreak >= 31 -> Color(0xFFFF9800)
        friend.currentStreak >= 8 -> ShMatcha
        else -> Color(0xFFFFC107)
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Indicator bar
        Box(
            modifier = Modifier
                .width(3.dp)
                .height(24.dp)
                .background(streakColor, CircleShape)
        )
        
        Spacer(Modifier.width(12.dp))
        
        Text("🔥", fontSize = 16.sp)
        
        Spacer(Modifier.width(8.dp))
        
        Text(
            text = friend.userName,
            style = ShBodyStyle,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.weight(1f)
        )
        
        Text(
            text = "${friend.currentStreak} days",
            style = ShBodyStyle,
            color = ShFog
        )
    }
}
