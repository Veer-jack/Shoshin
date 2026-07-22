package com.example.shoshinapp.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.shoshinapp.R
import com.example.shoshinapp.ui.components.*
import com.example.shoshinapp.ui.theme.*
import com.example.shoshinapp.viewmodel.FriendStreaksViewModel

@Composable
fun FriendProfileScreen(
    navController: NavController,
    viewModel: FriendStreaksViewModel,
    friendUserId: String
) {
    val friends by viewModel.allFriends.collectAsState()
    val friend = friends.find { it.userId == friendUserId }
    val scrollState = rememberScrollState()

    if (friend == null) return

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(ShPaper)
            .verticalScroll(scrollState)
            .padding(horizontal = 24.dp)
    ) {
        // App Bar
        Row(
            modifier = Modifier.fillMaxWidth().padding(top = 16.dp, bottom = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { navController.popBackStack() }, modifier = Modifier.size(24.dp)) {
                Icon(painterResource(R.drawable.ic_arrow_left), contentDescription = "Back", tint = ShInk)
            }
        }

        // Header Card
        ShoshinCard(modifier = Modifier.fillMaxWidth()) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(78.dp)
                        .clip(CircleShape)
                        .background(ShSand),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = friend.userName.take(1).uppercase(),
                        style = ShTitleStyle.copy(fontSize = 34.sp),
                        color = ShInk
                    )
                }

                Spacer(Modifier.height(14.dp))

                Text(friend.userName, style = ShTitleStyle.copy(fontSize = 24.sp), color = ShInk)

                Spacer(Modifier.height(8.dp))

                ShoshinPill(label = "2 shared circles", variant = ShPillVariant.Outline)

                Spacer(Modifier.height(20.dp))
                HorizontalDivider(color = ShLine)
                Spacer(Modifier.height(18.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    ShoshinStat(value = friend.currentStreak.toString(), label = "Current streak", color = ShVermillion)
                    ShoshinStat(value = friend.bestStreak.toString(), label = "Best streak")
                }
            }
        }

        Spacer(Modifier.height(14.dp))

        // Info Rows
        ShoshinCard(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(horizontal = 18.dp, vertical = 4.dp)) {
                ProfileRow(icon = R.drawable.ic_groups, title = "Dawn Circle", sub = "Shared accountability group", hasChevron = true)
                HorizontalDivider(color = ShLine)
                if (friend.isFollowing) {
                    ProfileRow(icon = R.drawable.ic_check, title = "Following", sub = "You'll see their morning activity")
                } else {
                    ProfileRow(
                        icon = R.drawable.ic_plus,
                        title = "Follow",
                        sub = "See their morning activity",
                        onClick = { viewModel.followFriend(friend.userId) }
                    )
                }
            }
        }

        Spacer(Modifier.height(48.dp))
    }
}

@Composable
private fun ProfileRow(
    icon: Int,
    title: String,
    sub: String,
    hasChevron: Boolean = false,
    onClick: (() -> Unit)? = null
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .let { if (onClick != null) it.clickable(onClick = onClick) else it }
            .padding(vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Icon(painterResource(icon), null, modifier = Modifier.size(20.dp), tint = ShInk)
        Column(modifier = Modifier.weight(1f)) {
            Text(title, fontSize = 15.5.sp, fontWeight = FontWeight.Medium, color = ShInk)
            Text(sub, fontSize = 12.5.sp, color = ShFog)
        }
        if (hasChevron) {
            Icon(painterResource(R.drawable.ic_arrow_right), null, modifier = Modifier.size(17.dp), tint = ShFog2)
        }
    }
}
