package com.example.shoshinapp.ui.screens

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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.shoshinapp.R
import com.example.shoshinapp.ui.components.*
import com.example.shoshinapp.ui.theme.*

data class NotificationItem(
    val icon: Int,
    val title: String,
    val body: String,
    val time: String,
    val unread: Boolean
)

@Composable
fun NotificationsScreen(
    navController: NavController
) {
    val scrollState = rememberScrollState()
    val notifications = listOf(
        NotificationItem(R.drawable.ic_flame, "14 mornings kept", "You're one away from tying your best streak.", "Today · 6:02 AM", true),
        NotificationItem(R.drawable.ic_bell, "Wind-down reminder", "Set tomorrow's alarm before you sleep.", "Yesterday · 9:30 PM", true),
        NotificationItem(R.drawable.ic_groups, "Mei rose at 5:28 AM", "Two of five in your circle have begun.", "Yesterday · 5:35 AM", false),
        NotificationItem(R.drawable.ic_trophy, "Badge earned: Early riser", "Five mornings before 6 AM in a row.", "2 days ago", false),
        NotificationItem(R.drawable.ic_shield, "71-Day Discipline: Day 21", "You've entered the Reinforcement phase.", "3 days ago", false),
        NotificationItem(R.drawable.ic_info, "Shoshin Pro renews soon", "Your annual plan renews on 12 Jul.", "5 days ago", false)
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(ShPaper)
            .verticalScroll(scrollState)
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
                Text("Notifications", style = ShTitleStyle.copy(fontSize = 26.sp), fontWeight = FontWeight.SemiBold)
            }
            TextButton(onClick = { }) {
                Text("Clear all", color = ShVermillion, fontWeight = FontWeight.SemiBold, fontSize = 12.5.sp)
            }
        }

        ShoshinCard(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(horizontal = 18.dp, vertical = 4.dp)) {
                notifications.forEachIndexed { i, item ->
                    NotificationRow(item)
                    if (i < notifications.lastIndex) HorizontalDivider(color = ShLine)
                }
            }
        }
        
        Spacer(Modifier.height(40.dp))
    }
}

@Composable
private fun NotificationRow(item: NotificationItem) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp),
        verticalAlignment = Alignment.Top,
        horizontalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(if (item.unread) ShVermillion.copy(alpha = 0.08f) else ShPaper2),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(item.icon),
                contentDescription = null,
                tint = if (item.unread) ShVermillion else ShInk,
                modifier = Modifier.size(18.dp)
            )
        }

        Column(modifier = Modifier.weight(1f)) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(
                    text = item.title,
                    fontSize = 15.sp,
                    fontWeight = if (item.unread) FontWeight.SemiBold else FontWeight.Medium,
                    color = ShInk
                )
                if (item.unread) {
                    Box(modifier = Modifier.size(6.dp).background(ShVermillion, CircleShape))
                }
            }
            Spacer(Modifier.height(2.dp))
            Text(
                text = item.body,
                fontSize = 14.sp,
                color = ShFog,
                lineHeight = 18.sp
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = item.time,
                fontSize = 11.5.sp,
                color = ShFog2
            )
        }
    }
}
