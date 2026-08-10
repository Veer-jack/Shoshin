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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.Shoshin.app.R
import com.Shoshin.app.data.models.Friend
import com.Shoshin.app.navigation.ShRoutes
import com.Shoshin.app.ui.components.*
import com.Shoshin.app.ui.theme.*
import com.Shoshin.app.viewmodel.FriendStreaksViewModel

@Composable
fun AllFriendsScreen(
    navController: NavController,
    viewModel: FriendStreaksViewModel
) {
    val friends by viewModel.allFriends.collectAsState()
    var searchQuery by remember { mutableStateOf("") }
    
    ShoshinTheme(type = ShoshinThemeType.DYNAMIC) {
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
                    .padding(horizontal = 24.dp, vertical = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = { navController.popBackStack() }, modifier = Modifier.size(24.dp)) {
                        Icon(painterResource(R.drawable.ic_arrow_left), contentDescription = "Back", tint = MaterialTheme.colorScheme.onBackground)
                    }
                    Spacer(Modifier.width(16.dp))
                    Text("Friends", style = ShTitleStyle.copy(fontSize = 32.sp), color = MaterialTheme.colorScheme.onBackground)
                }
                IconButton(onClick = { navController.navigate(ShRoutes.INVITE) }, modifier = Modifier.size(24.dp)) {
                    Icon(painterResource(R.drawable.ic_plus), contentDescription = "Add", tint = MaterialTheme.colorScheme.onBackground)
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 24.dp)
            ) {
                // Search Bar
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("Search friends", style = ShLabelStyle, color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)) },
                    leadingIcon = { Icon(painterResource(R.drawable.ic_search), null, tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(20.dp)) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedBorderColor = Color.Transparent,
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        cursorColor = MaterialTheme.colorScheme.primary,
                        unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                        focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant
                    ),
                    singleLine = true
                )

                Spacer(Modifier.height(24.dp))

                if (friends.isEmpty()) {
                    EdgeLayout(
                        icon = R.drawable.ic_user,
                        kicker = "Your circle is quiet",
                        title = "No friends yet",
                        body = "Invite someone who needs a reason to rise.",
                        actionLabel = "Invite Friends",
                        onAction = { navController.navigate(ShRoutes.INVITE) }
                    )
                } else {
                    ShoshinCard(modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.padding(vertical = 8.dp)) {
                            friends.forEachIndexed { i, friend ->
                                FriendListRow(
                                    friend = friend,
                                    onClick = { navController.navigate(ShRoutes.friendProfile(friend.userId)) }
                                )
                                if (i < friends.lastIndex) {
                                    HorizontalDivider(
                                        color = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f), 
                                        thickness = 1.dp, 
                                        modifier = Modifier.padding(start = 72.dp, end = 24.dp)
                                    )
                                }
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
private fun FriendListRow(
    friend: Friend,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 24.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Initial Avatar
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.surfaceVariant),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = friend.userName.take(1).uppercase(),
                style = ShH2Style.copy(fontSize = 17.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
            )
        }
        
        Spacer(Modifier.width(16.dp))
        
        Column(modifier = Modifier.weight(1f)) {
            Text(friend.userName, style = ShH2Style.copy(fontSize = 16.sp), color = MaterialTheme.colorScheme.onSurface)
            val friendStreakText = if (friend.currentStreak > 0) "${friend.currentStreak} day streak" else friend.activityStatus
            Text(friendStreakText, style = ShLabelStyle, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            Icon(
                painter = painterResource(R.drawable.ic_flame), 
                null, 
                modifier = Modifier.size(14.dp), 
                tint = ShVermillion
            )
            Text(
                text = friend.currentStreak.toString(), 
                style = ShNumeralStyle.copy(fontSize = 16.sp), 
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.Bold
            )
        }
    }
}
