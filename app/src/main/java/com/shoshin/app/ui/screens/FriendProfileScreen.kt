package com.Shoshin.app.ui.screens

import androidx.compose.foundation.background
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
import com.Shoshin.app.ui.components.*
import com.Shoshin.app.ui.theme.*
import com.Shoshin.app.viewmodel.FriendStreaksViewModel

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
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(vertical = 16.dp),
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
                // Initial Avatar
                Box(
                    modifier = Modifier
                        .size(100.dp)
                        .clip(CircleShape)
                        .background(ShPaper2),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = friend.userName.take(1).uppercase(),
                        style = ShTitleStyle.copy(fontSize = 36.sp),
                        color = ShInk.copy(alpha = 0.6f)
                    )
                }
                
                Spacer(Modifier.height(16.dp))
                
                Text(
                    text = friend.userName, 
                    style = ShTitleStyle.copy(fontSize = 32.sp), 
                    color = ShInk,
                    textAlign = TextAlign.Center
                )
                
                Spacer(Modifier.height(12.dp))

                // Shared-circle count isn't tracked yet (no cross-user query exists) —
                // honest placeholder, not a fabricated number.
                Surface(
                    color = Color.Transparent,
                    shape = RoundedCornerShape(999.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, ShLine)
                ) {
                    Text(
                        "Shared circles: —",
                        color = ShFog,
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp),
                        style = ShLabelStyle.copy(fontSize = 11.sp)
                    )
                }

                Spacer(Modifier.height(32.dp))
                HorizontalDivider(color = ShLine, thickness = 1.dp, modifier = Modifier.padding(horizontal = 16.dp))
                Spacer(Modifier.height(24.dp))
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceAround,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    FriendStatItem(value = friend.currentStreak.toString(), label = "CURRENT STREAK", valueColor = ShVermillion)
                    FriendStatItem(value = friend.bestStreak.toString(), label = "BEST STREAK")
                }
            }
        }

        Spacer(Modifier.height(16.dp))

        // Info / Connection Rows
        ShoshinCard(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(vertical = 8.dp)) {
                // Shared Circle Row — no cross-user "shared groups" query exists yet
                FriendProfileRow(
                    icon = R.drawable.ic_groups,
                    title = "Shared circles",
                    sub = "Not yet available",
                    hasChevron = false
                )

                HorizontalDivider(color = ShLine, modifier = Modifier.padding(horizontal = 24.dp))

                // Following Status Row
                FriendProfileRow(
                    icon = R.drawable.ic_check, 
                    title = "Following", 
                    sub = "You'll see their morning activity",
                    isCircleIcon = true
                )
            }
        }
        
        Spacer(Modifier.height(48.dp))
    }
}

@Composable
private fun FriendStatItem(value: String, label: String, valueColor: Color = ShInk) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(value, style = ShNumeralStyle.copy(fontSize = 32.sp, color = valueColor))
        Text(label, style = ShKickerStyle.copy(fontSize = 9.sp, letterSpacing = 1.sp), color = ShFog)
    }
}

@Composable
private fun FriendProfileRow(
    icon: Int,
    title: String,
    sub: String,
    hasChevron: Boolean = false,
    isCircleIcon: Boolean = false
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(ShPaper2),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(icon), 
                null, 
                modifier = Modifier.size(20.dp), 
                tint = if (isCircleIcon) ShMatcha else ShInk
            )
        }
        
        Column(modifier = Modifier.weight(1f)) {
            Text(title, style = ShH2Style.copy(fontSize = 16.sp))
            Text(sub, style = ShLabelStyle, color = ShFog)
        }
        
        if (hasChevron) {
            Icon(
                painter = painterResource(R.drawable.ic_arrow_right), 
                null, 
                modifier = Modifier.size(18.dp), 
                tint = ShLine2
            )
        } else if (isCircleIcon) {
            // Already handled by tint in this mockup
        }
    }
}
