package com.example.shoshinapp.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
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
import com.example.shoshinapp.R
import com.example.shoshinapp.ui.components.*
import com.example.shoshinapp.ui.theme.*
import com.example.shoshinapp.viewmodel.FriendStreaksViewModel
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun FriendProfileScreen(
    navController: NavController,
    viewModel: FriendStreaksViewModel,
    friendUserId: String
) {
    val friends by viewModel.allFriends.collectAsState()
    val friend = friends.find { it.userId == friendUserId }
    var showUnfollowDialog by remember { mutableStateOf(false) }

    if (friend == null) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = ShVermillion)
        }
        return
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(ShPaper)
            .verticalScroll(rememberScrollState())
    ) {
        // App Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { navController.popBackStack() }) {
                Icon(painterResource(R.drawable.ic_arrow_left), contentDescription = "Back")
            }
            TextButton(onClick = { showUnfollowDialog = true }) {
                Text("Unfollow", color = ShError, style = ShLabelStyle)
            }
        }

        // Profile Header
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .clip(CircleShape)
                    .background(ShSand)
                    .border(2.dp, ShLine, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(friend.userName.take(1).uppercase(), fontSize = 40.sp, fontWeight = FontWeight.Bold, color = ShInk)
            }
            
            Spacer(Modifier.height(16.dp))
            
            Text(friend.userName, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
            Text("@${friend.userName.lowercase().replace(" ", "")}", style = ShLabelStyle, color = ShFog)
        }

        Spacer(Modifier.height(32.dp))

        // Streak Card
        ShoshinCard(modifier = Modifier.padding(horizontal = 24.dp)) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("🔥 ${friend.currentStreak} DAYS", fontSize = 48.sp, fontWeight = FontWeight.Bold, color = ShInk)
                Text("CURRENT STREAK", style = ShKickerStyle, color = ShFog)
                
                Spacer(Modifier.height(24.dp))
                
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceAround) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("${friend.bestStreak}", fontWeight = FontWeight.Bold, style = ShBodyStyle)
                        Text("BEST", style = ShLabelStyle, color = ShFog)
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        val dateStr = SimpleDateFormat("MMM d").format(Date(friend.followedDate))
                        Text(dateStr, fontWeight = FontWeight.Bold, style = ShBodyStyle)
                        Text("FOLLOWED", style = ShLabelStyle, color = ShFog)
                    }
                }
            }
        }

        Spacer(Modifier.height(32.dp))

        // Recent Activity (Mock)
        Kicker("RECENT ACTIVITY", modifier = Modifier.padding(horizontal = 24.dp))
        Spacer(Modifier.height(12.dp))
        ShoshinCard(modifier = Modifier.padding(horizontal = 24.dp)) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                repeat(7) { i ->
                    val isDone = i < 5 // Mock: last 5 days done
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(if (isDone) ShMatcha.copy(alpha = 0.1f) else ShSand),
                            contentAlignment = Alignment.Center
                        ) {
                            if (isDone) {
                                Icon(Icons.Default.Check, contentDescription = null, tint = ShMatcha, modifier = Modifier.size(16.dp))
                            } else {
                                Text("-", color = ShFog)
                            }
                        }
                    }
                }
            }
        }

        Spacer(Modifier.height(48.dp))
    }

    if (showUnfollowDialog) {
        AlertDialog(
            onDismissRequest = { showUnfollowDialog = false },
            title = { Text("Unfollow ${friend.userName}?") },
            text = { Text("They won't be able to see your streaks anymore.") },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.removeFriend(friendUserId)
                    showUnfollowDialog = false
                    navController.popBackStack()
                }) {
                    Text("Unfollow", color = ShError)
                }
            },
            dismissButton = {
                TextButton(onClick = { showUnfollowDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}
