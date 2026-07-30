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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.compose.ui.platform.LocalContext
import com.Shoshin.app.R
import com.Shoshin.app.data.db.AppDatabase
import com.Shoshin.app.data.db.entities.NotificationEntity
import com.Shoshin.app.ui.components.*
import com.Shoshin.app.ui.theme.*
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

    ShoshinTheme(type = ShoshinThemeType.DYNAMIC) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(ShNight)
                .statusBarsPadding()
        ) {
            // App Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(painterResource(R.drawable.ic_arrow_left), contentDescription = "Back", tint = Color.White)
                    }
                    Spacer(Modifier.width(8.dp))
                    Text("Notifications", style = ShTitleStyle.copy(fontSize = 28.sp, color = Color.White))
                }
                TextButton(onClick = { scope.launch { database.notificationDao().clearAll(userId) } }) {
                    Text("Clear all", color = ShVermillionLight, style = ShLabelStyle.copy(fontWeight = FontWeight.Bold))
                }
            }

            if (notifications.isEmpty()) {
                EdgeLayout(
                    icon = R.drawable.ic_bell,
                    kicker = "ALL CAUGHT UP",
                    title = "Nothing new",
                    body = "We'll let you know when your circle rises or a badge is earned."
                )
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(scrollState)
                        .padding(horizontal = 24.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(24.dp))
                            .background(ShNight2)
                    ) {
                        Column(modifier = Modifier.padding(vertical = 8.dp)) {
                            notifications.forEachIndexed { i, item ->
                                NotificationRowDark(item)
                                if (i < notifications.lastIndex) {
                                    HorizontalDivider(color = ShNightLine, modifier = Modifier.padding(horizontal = 16.dp))
                                }
                            }
                        }
                    }
                    Spacer(Modifier.height(48.dp))
                }
            }
        }
    }
}

@Composable
private fun NotificationRowDark(item: NotificationEntity) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 18.dp),
        verticalAlignment = Alignment.Top,
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(CircleShape)
                .background(if (!item.isRead) ShVermillionLight.copy(alpha = 0.1f) else ShNight3),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(item.iconRes),
                contentDescription = null,
                tint = if (!item.isRead) ShVermillionLight else Color.White,
                modifier = Modifier.size(18.dp)
            )
        }

        Column(modifier = Modifier.weight(1f)) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = item.title,
                    style = ShH2Style.copy(fontSize = 15.5.sp, color = Color.White),
                    fontWeight = if (!item.isRead) FontWeight.Bold else FontWeight.Medium
                )
                if (!item.isRead) {
                    Box(modifier = Modifier.size(6.dp).clip(CircleShape).background(ShVermillionLight))
                }
            }
            Spacer(Modifier.height(4.dp))
            Text(
                text = item.body,
                style = ShBodyStyle.copy(fontSize = 14.sp, color = ShNightMuted, lineHeight = 19.sp)
            )
            Spacer(Modifier.height(8.dp))
            val dateStr = java.text.SimpleDateFormat("MMM d · h:mm a", java.util.Locale.getDefault()).format(java.util.Date(item.timestamp))
            Text(
                text = dateStr.uppercase(),
                style = ShKickerStyle.copy(fontSize = 9.sp, color = ShNightMuted.copy(alpha = 0.6f))
            )
        }
    }
}
