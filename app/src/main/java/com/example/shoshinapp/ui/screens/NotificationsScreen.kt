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
import androidx.compose.ui.platform.LocalContext
import com.example.shoshinapp.R
import com.example.shoshinapp.data.db.AppDatabase
import com.example.shoshinapp.data.db.entities.NotificationEntity
import com.example.shoshinapp.ui.components.*
import com.example.shoshinapp.ui.theme.*
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch

@Composable
fun NotificationsScreen(
    navController: NavController
) {
    val context = LocalContext.current
    val database = remember { AppDatabase.getInstance(context) }
    val userId = FirebaseAuth.getInstance().currentUser?.uid ?: ""
    val scope = rememberCoroutineScope()
    
    val notifications by database.notificationDao().getNotificationsFlow(userId).collectAsState(initial = emptyList())
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
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
            TextButton(onClick = { scope.launch { database.notificationDao().clearAll(userId) } }) {
                Text("Clear all", color = ShVermillion, fontWeight = FontWeight.SemiBold, fontSize = 12.5.sp)
            }
        }

        if (notifications.isEmpty()) {
            Box(modifier = Modifier.fillMaxWidth().weight(1f), contentAlignment = Alignment.Center) {
                Text("All caught up", style = ShLabelStyle, color = ShFog)
            }
        } else {
            ShoshinCard(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(horizontal = 18.dp, vertical = 4.dp)) {
                    notifications.forEachIndexed { i, item ->
                        NotificationRow(item)
                        if (i < notifications.lastIndex) HorizontalDivider(color = ShLine)
                    }
                }
            }
        }
        
        Spacer(Modifier.height(40.dp))
    }
}

@Composable
private fun NotificationRow(item: NotificationEntity) {
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
                .background(if (!item.isRead) ShVermillion.copy(alpha = 0.08f) else ShPaper2),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(item.iconRes),
                contentDescription = null,
                tint = if (!item.isRead) ShVermillion else ShInk,
                modifier = Modifier.size(18.dp)
            )
        }

        Column(modifier = Modifier.weight(1f)) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(
                    text = item.title,
                    fontSize = 15.sp,
                    fontWeight = if (!item.isRead) FontWeight.SemiBold else FontWeight.Medium,
                    color = ShInk
                )
                if (!item.isRead) {
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
            val dateStr = java.text.SimpleDateFormat("MMM d · h:mm a", java.util.Locale.getDefault()).format(java.util.Date(item.timestamp))
            Text(
                text = dateStr,
                fontSize = 11.5.sp,
                color = ShFog2
            )
        }
    }
}
