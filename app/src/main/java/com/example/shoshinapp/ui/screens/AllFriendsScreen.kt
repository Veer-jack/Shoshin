package com.example.shoshinapp.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.shoshinapp.R
import com.example.shoshinapp.data.models.Friend
import com.example.shoshinapp.ui.components.*
import com.example.shoshinapp.ui.theme.*
import com.example.shoshinapp.viewmodel.FriendStreaksViewModel

@Composable
fun AllFriendsScreen(
    navController: NavController,
    viewModel: FriendStreaksViewModel
) {
    val friends by viewModel.allFriends.collectAsState()
    var searchQuery by remember { mutableStateOf("") }
    
    val filteredFriends = remember(friends, searchQuery) {
        if (searchQuery.isEmpty()) friends
        else friends.filter { it.userName.contains(searchQuery, ignoreCase = true) }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(ShPaper)
            .padding(horizontal = 24.dp)
    ) {
        // App Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 24.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { navController.popBackStack() }) {
                Icon(painterResource(R.drawable.ic_arrow_left), contentDescription = "Back")
            }
            Spacer(Modifier.width(16.dp))
            Text("All Friends (${friends.size})", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
        }

        // Search
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("Search friends...") },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = ShInk,
                unfocusedBorderColor = ShLine
            )
        )

        Spacer(Modifier.height(24.dp))

        // Friends List
        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(filteredFriends) { friend ->
                FriendListItem(friend = friend) {
                    navController.navigate("friend_profile/${friend.userId}")
                }
            }
        }
        
        Spacer(Modifier.height(16.dp))
        
        ShoshinButton(
            onClick = { /* Navigate to invite */ },
            variant = ShButtonVariant.Primary
        ) {
            Text("+ Add More Friends")
        }
        
        Spacer(Modifier.height(32.dp))
    }
}

@Composable
fun FriendListItem(friend: Friend, onClick: () -> Unit) {
    ShoshinCard(modifier = Modifier.fillMaxWidth().height(80.dp)) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .clickable { onClick() }
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("🔥", fontSize = 20.sp)
            Spacer(Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(friend.userName, fontWeight = FontWeight.Bold, style = ShBodyStyle)
                Text("${friend.currentStreak} day streak", style = ShLabelStyle, color = ShFog)
            }
            StatusBadge(status = friend.activityStatus)
        }
    }
}

@Composable
private fun StatusBadge(status: String) {
    val color = when (status) {
        "Active" -> ShMatcha
        "Building" -> Color(0xFF2196F3)
        else -> ShFog
    }
    
    Surface(
        color = color.copy(alpha = 0.1f),
        shape = RoundedCornerShape(4.dp)
    ) {
        Text(
            text = status,
            color = color,
            style = ShKickerStyle.copy(fontSize = 10.sp),
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        )
    }
}
