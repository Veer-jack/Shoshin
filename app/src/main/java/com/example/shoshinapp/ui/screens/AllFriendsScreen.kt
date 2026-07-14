package com.example.shoshinapp.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import com.example.shoshinapp.data.models.Friend
import com.example.shoshinapp.navigation.ShRoutes
import com.example.shoshinapp.ui.components.*
import com.example.shoshinapp.ui.theme.*
import com.example.shoshinapp.viewmodel.FriendStreaksViewModel

@Composable
fun AllFriendsScreen(
    navController: NavController,
    viewModel: FriendStreaksViewModel
) {
    val friends by viewModel.allFriends.collectAsState()
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(ShPaper)
            .padding(horizontal = 24.dp)
    ) {
        // App Bar
        Row(
            modifier = Modifier.fillMaxWidth().padding(top = 16.dp, bottom = 22.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(14.dp)) {
                IconButton(onClick = { navController.popBackStack() }, modifier = Modifier.size(24.dp)) {
                    Icon(painterResource(R.drawable.ic_arrow_left), contentDescription = "Back")
                }
                Text("Friends", style = ShTitleStyle.copy(fontSize = 26.sp), fontWeight = FontWeight.SemiBold)
            }
            IconButton(onClick = { navController.navigate(ShRoutes.INVITE) }, modifier = Modifier.size(22.dp)) {
                Icon(painterResource(R.drawable.ic_plus), contentDescription = "Add", tint = ShInk)
            }
        }

        // Search
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
                .clip(RoundedCornerShape(14.dp))
                .background(ShSurface)
                .border(1.5.dp, ShLine2, RoundedCornerShape(14.dp))
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(painterResource(R.drawable.ic_search), null, modifier = Modifier.size(18.dp), tint = ShFog)
            Spacer(Modifier.width(10.dp))
            Text("Search friends", fontSize = 15.sp, color = ShFog2)
        }

        Spacer(Modifier.height(20.dp))

        ShoshinCard(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(horizontal = 18.dp, vertical = 4.dp)) {
                friends.forEachIndexed { i, friend ->
                    FriendListRow(
                        friend = friend,
                        onClick = { navController.navigate(ShRoutes.friendProfile(friend.userId)) }
                    )
                    if (i < friends.lastIndex) HorizontalDivider(color = ShLine)
                }
            }
        }
        
        Spacer(Modifier.height(40.dp))
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
            .padding(vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(ShSand),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = friend.userName.take(1).uppercase(),
                style = ShTitleStyle.copy(fontSize = 17.sp),
                color = ShInk
            )
        }
        Spacer(Modifier.width(14.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(friend.userName, fontSize = 15.5.sp, fontWeight = FontWeight.Medium, color = ShInk)
            Text("2 shared circles", fontSize = 12.5.sp, color = ShFog) // Mock shared count
        }
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(5.dp)) {
            Icon(painterResource(R.drawable.ic_flame), null, modifier = Modifier.size(15.dp), tint = ShVermillion)
            Text(friend.currentStreak.toString(), style = ShNumeralStyle.copy(fontSize = 15.sp), color = ShInk)
        }
    }
}
