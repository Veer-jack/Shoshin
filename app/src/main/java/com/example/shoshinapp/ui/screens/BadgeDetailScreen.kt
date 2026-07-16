package com.example.shoshinapp.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
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
import com.example.shoshinapp.R
import com.example.shoshinapp.navigation.ShRoutes
import com.example.shoshinapp.ui.components.*
import com.example.shoshinapp.ui.theme.*
import com.example.shoshinapp.viewmodel.BadgeViewModel
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun BadgeDetailScreen(
    navController: NavController,
    viewModel: BadgeViewModel,
    badgeId: String
) {
    val badges by viewModel.badges.collectAsState()
    val badge = badges.find { it.id == badgeId } ?: return

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp)
    ) {
        // App Bar
        Row(
            modifier = Modifier.fillMaxWidth().padding(top = 16.dp, bottom = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { navController.popBackStack() }, modifier = Modifier.size(24.dp)) {
                Icon(painterResource(R.drawable.ic_arrow_left), contentDescription = "Back")
            }
        }

        Column(
            modifier = Modifier.weight(1f).fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Badge Medallion
            Box(
                modifier = Modifier
                    .size(120.dp)
                    .clip(CircleShape)
                    .background(if (!badge.isLocked) Color.parseColor(badge.color).copy(alpha = 0.1f) else ShPaper2),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(76.dp)
                        .clip(CircleShape)
                        .background(if (!badge.isLocked) Color.parseColor(badge.color) else ShSand),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(if (badge.isLocked) R.drawable.ic_lock else getBadgeIconRes(badge.icon)),
                        contentDescription = null,
                        tint = if (badge.isLocked) ShFog else Color.White,
                        modifier = Modifier.size(34.dp)
                    )
                }
            }

            Spacer(Modifier.height(24.dp))

            Kicker(
                text = if (badge.isLocked) "Locked" else "Earned",
                color = if (badge.isLocked) ShFog2 else Color.parseColor(badge.color),
                modifier = Modifier.padding(bottom = 8.dp)
            )

            Text(
                text = badge.name,
                style = ShTitleStyle.copy(fontSize = 28.sp),
                color = ShInk,
                textAlign = TextAlign.Center
            )

            Spacer(Modifier.height(10.dp))

            Text(
                text = if (!badge.isLocked) {
                    "Earned ${SimpleDateFormat("d MMMM yyyy", Locale.getDefault()).format(Date(badge.unlockedDate ?: System.currentTimeMillis()))} — ${badge.description}"
                } else {
                    badge.requirementDescription
                },
                style = ShBodyStyle,
                color = ShFog,
                textAlign = TextAlign.Center,
                modifier = Modifier.widthIn(max = 280.dp)
            )
        }

        Box(modifier = Modifier.padding(bottom = 24.dp)) {
            if (!badge.isLocked) {
                ShoshinButton(
                    onClick = { 
                        navController.navigate(ShRoutes.streakShare(1, badge.name, System.currentTimeMillis()))
                    },
                    variant = ShButtonVariant.Accent,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(painterResource(R.drawable.ic_share), null, modifier = Modifier.size(18.dp), tint = Color.White)
                    Spacer(Modifier.width(8.dp))
                    Text("Share this badge")
                }
            } else {
                ShoshinButton(
                    onClick = { },
                    variant = ShButtonVariant.Ghost,
                    modifier = Modifier.fillMaxWidth(),
                    enabled = false
                ) {
                    Text("Keep practicing to unlock", color = ShFog)
                }
            }
        }
    }
}

// Helper for icon mapping
private fun getBadgeIconRes(iconName: String): Int {
    return when (iconName) {
        "streak_7", "streak_30", "streak_100", "streak_365" -> R.drawable.ic_flame
        "milestone" -> R.drawable.ic_check
        "groups" -> R.drawable.ic_groups
        "influence" -> R.drawable.ic_plus
        "sun" -> R.drawable.ic_sun
        "thought" -> R.drawable.ic_book
        "share" -> R.drawable.ic_share
        "community" -> R.drawable.ic_user
        else -> R.drawable.ic_trophy
    }
}

private fun Color.Companion.parseColor(colorString: String): Color {
    return Color(android.graphics.Color.parseColor(colorString))
}
