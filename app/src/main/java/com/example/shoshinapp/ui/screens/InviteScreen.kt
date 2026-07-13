package com.example.shoshinapp.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.shoshinapp.R
import com.example.shoshinapp.data.models.UserSummary
import com.example.shoshinapp.ui.components.*
import com.example.shoshinapp.ui.theme.*
import com.example.shoshinapp.viewmodel.InviteViewModel

@Composable
fun InviteScreen(
    navController: NavController,
    viewModel: InviteViewModel
) {
    var selectedTab by remember { mutableStateOf(0) }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(ShPaper)
    ) {
        // App Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { navController.popBackStack() }) {
                Icon(painterResource(R.drawable.ic_arrow_left), contentDescription = "Back")
            }
            Spacer(Modifier.width(16.dp))
            Text("Invite Friends", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
        }

        ShoshinSegmented(
            options = listOf(
                SegmentOption(0, "Search"),
                SegmentOption(1, "Invite Link")
            ),
            selected = selectedTab,
            onSelect = { selectedTab = it },
            modifier = Modifier.padding(horizontal = 24.dp)
        )

        Spacer(Modifier.height(24.dp))

        Box(modifier = Modifier.weight(1f).padding(horizontal = 24.dp)) {
            if (selectedTab == 0) {
                FriendInviteContent(viewModel)
            } else {
                LinkInviteContent(viewModel)
            }
        }
    }
}

@Composable
fun FriendInviteContent(viewModel: InviteViewModel) {
    var query by remember { mutableStateOf("") }
    val searchResults by viewModel.searchResults.collectAsState()
    val suggestions by viewModel.suggestedFriends.collectAsState()

    Column {
        OutlinedTextField(
            value = query,
            onValueChange = { 
                query = it
                viewModel.searchFriends(it)
            },
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("Search by email or username...") },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
            shape = RoundedCornerShape(12.dp)
        )

        Spacer(Modifier.height(24.dp))

        LazyColumn(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            if (query.length >= 2) {
                item { Kicker("SEARCH RESULTS") }
                items(searchResults) { user ->
                    SuggestedFriendRow(user)
                }
            } else {
                item { Kicker("SUGGESTED FRIENDS") }
                items(suggestions) { user ->
                    SuggestedFriendRow(user)
                }
            }
        }
    }
}

@Composable
fun SuggestedFriendRow(user: UserSummary) {
    ShoshinCard(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier.size(40.dp).background(ShSand, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(user.userName.take(1).uppercase())
            }
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(user.userName, fontWeight = FontWeight.Bold, style = ShBodyStyle)
                Text("${user.currentStreak} day streak", style = ShLabelStyle, color = ShFog)
            }
            TextButton(onClick = { /* Add friend */ }) {
                Text("+ Add", color = ShVermillion)
            }
        }
    }
}

@Composable
fun LinkInviteContent(viewModel: InviteViewModel) {
    val context = LocalContext.current
    val inviteCode = viewModel.getUserInviteCode()
    val inviteLink = viewModel.getInviteLink()

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Spacer(Modifier.height(32.dp))
        
        Text("Your Invite Code", style = ShLabelStyle, color = ShFog)
        Text(inviteCode, fontSize = 32.sp, fontWeight = FontWeight.Bold, color = ShInk)
        
        Spacer(Modifier.height(16.dp))
        
        ShoshinButton(
            onClick = {
                val clipboard = context.getSystemService(android.content.Context.CLIPBOARD_SERVICE) as android.content.ClipboardManager
                val clip = android.content.ClipData.newPlainText("Invite Code", inviteCode)
                clipboard.setPrimaryClip(clip)
            },
            variant = ShButtonVariant.Ghost
        ) {
            Text("Copy Code")
        }

        Spacer(Modifier.height(48.dp))

        Kicker("SHARE INVITE LINK")
        Spacer(Modifier.height(16.dp))
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            ShareIconButton(Icons.Default.Chat, "WhatsApp")
            ShareIconButton(Icons.Default.CameraAlt, "Instagram")
            ShareIconButton(Icons.Default.Email, "Email")
            ShareIconButton(Icons.Default.Link, "Copy")
        }
    }
}

@Composable
fun ShareIconButton(icon: androidx.compose.ui.graphics.vector.ImageVector, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        IconButton(
            onClick = { /* Share */ },
            modifier = Modifier.size(56.dp).background(ShSand, CircleShape)
        ) {
            Icon(icon, contentDescription = label, tint = ShInk)
        }
        Spacer(Modifier.height(8.dp))
        Text(label, style = ShLabelStyle, color = ShFog)
    }
}
