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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.shoshinapp.R
import com.example.shoshinapp.ui.components.*
import com.example.shoshinapp.ui.theme.*

@Composable
fun GroupInviteScreen(
    navController: NavController,
    groupId: String
) {
    val context = LocalContext.current
    var selectedIds by remember { mutableStateOf(setOf<String>()) }
    
    // Using empty list to remove dummy data
    val friends = emptyList<FriendItem>()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 24.dp)
    ) {
        // App Bar
        Row(
            modifier = Modifier.fillMaxWidth().padding(top = 16.dp, bottom = 22.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            IconButton(onClick = { navController.popBackStack() }, modifier = Modifier.size(24.dp)) {
                Icon(painterResource(R.drawable.ic_arrow_left), contentDescription = "Back")
            }
            Text("Invite to circle", style = ShTitleStyle.copy(fontSize = 24.sp), fontWeight = FontWeight.SemiBold)
        }

        Text(
            text = "Choose who joins your circle. They'll see your morning activity, and you'll see theirs.",
            style = ShBodyStyle,
            modifier = Modifier.padding(bottom = 20.dp)
        )

        ShoshinButton(
            onClick = { 
                val message = "Join my circle on Shoshin! We keep our mornings together. Download the app to begin: https://shoshin.app/join/$groupId"
                val intent = android.content.Intent(android.content.Intent.ACTION_SEND).apply {
                    type = "text/plain"
                    putExtra(android.content.Intent.EXTRA_TEXT, message)
                }
                context.startActivity(android.content.Intent.createChooser(intent, "Invite to circle"))
            },
            variant = ShButtonVariant.Ghost,
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(painterResource(R.drawable.ic_share), null, modifier = Modifier.size(18.dp), tint = ShInk)
            Spacer(Modifier.width(8.dp))
            Text("Share circle invite link")
        }

        Spacer(Modifier.height(24.dp))

        Kicker("Your circle", modifier = Modifier.padding(start = 4.dp, bottom = 12.dp))

        ShoshinCard(modifier = Modifier.fillMaxWidth().weight(1f)) {
            if (friends.isEmpty()) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No friends yet. Share the link above!", style = ShLabelStyle, color = ShFog, textAlign = androidx.compose.ui.text.style.TextAlign.Center, modifier = Modifier.padding(24.dp))
                }
            } else {
                Column(modifier = Modifier.padding(horizontal = 18.dp, vertical = 4.dp)) {
                    friends.forEachIndexed { i, friend ->
                        val isSelected = selectedIds.contains(friend.id)
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    selectedIds = if (isSelected) selectedIds - friend.id else selectedIds + friend.id
                                }
                                .padding(vertical = 14.dp),
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
                                    text = friend.initial,
                                    style = ShTitleStyle.copy(fontSize = 17.sp),
                                    color = ShInk
                                )
                            }
                            Spacer(Modifier.width(14.dp))
                            Text(
                                text = friend.name,
                                modifier = Modifier.weight(1f),
                                fontSize = 15.5.sp,
                                fontWeight = FontWeight.Medium,
                                color = ShInk
                            )
                            Box(
                                modifier = Modifier
                                    .size(24.dp)
                                    .clip(CircleShape)
                                    .background(if (isSelected) ShMatcha else Color.Transparent)
                                    .border(
                                        width = 1.5.dp,
                                        color = if (isSelected) ShMatcha else ShLine2,
                                        shape = CircleShape
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                if (isSelected) {
                                    Icon(
                                        painter = painterResource(R.drawable.ic_check),
                                        contentDescription = null,
                                        tint = Color.White,
                                        modifier = Modifier.size(14.dp)
                                    )
                                }
                            }
                        }
                        if (i < friends.lastIndex) HorizontalDivider(color = ShLine)
                    }
                }
            }
        }

        Spacer(Modifier.height(24.dp))

        ShoshinButton(
            onClick = { navController.popBackStack() },
            variant = ShButtonVariant.Accent,
            modifier = Modifier.fillMaxWidth().padding(bottom = 24.dp),
            enabled = selectedIds.isNotEmpty()
        ) {
            val count = selectedIds.size
            Text("Send ${if (count > 0) "$count " else ""}invite${if (count == 1) "" else "s"}")
        }
    }
}

private data class FriendItem(val id: String, val initial: String, val name: String)
