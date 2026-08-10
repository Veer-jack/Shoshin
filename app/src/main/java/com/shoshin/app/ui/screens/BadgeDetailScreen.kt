package com.Shoshin.app.ui.screens

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
import com.Shoshin.app.R
import com.Shoshin.app.navigation.ShRoutes
import com.Shoshin.app.ui.components.*
import com.Shoshin.app.ui.theme.*
import com.Shoshin.app.viewmodel.BadgeViewModel
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

    ShoshinTheme(type = ShoshinThemeType.DYNAMIC) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
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
                    Icon(painterResource(R.drawable.ic_arrow_left), contentDescription = "Back", tint = MaterialTheme.colorScheme.onBackground)
                }
            }

            Column(
                modifier = Modifier.weight(1f).fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                // Large Badge Icon
                Box(
                    modifier = Modifier
                        .size(160.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.surfaceVariant),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(100.dp)
                            .clip(CircleShape)
                            .background(if (!badge.isLocked) ShVermillion else MaterialTheme.colorScheme.background),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            painter = painterResource(if (badge.isLocked) R.drawable.ic_lock else getBadgeIconRes(badge.icon)),
                            contentDescription = null,
                            tint = if (badge.isLocked) MaterialTheme.colorScheme.onSurfaceVariant else Color.White,
                            modifier = Modifier.size(48.dp)
                        )
                    }
                }

                Spacer(Modifier.height(48.dp))

                Kicker(
                    text = if (badge.isLocked) "LOCKED" else "EARNED",
                    color = if (badge.isLocked) MaterialTheme.colorScheme.onSurfaceVariant else ShVermillion,
                    modifier = Modifier.padding(bottom = 12.dp)
                )

                Text(
                    text = if (!badge.isLocked) badge.name else "???",
                    style = ShTitleStyle.copy(fontSize = 40.sp, color = MaterialTheme.colorScheme.onSurface),
                    textAlign = TextAlign.Center
                )

                Spacer(Modifier.height(16.dp))

                Text(
                    text = if (!badge.isLocked) {
                        "Earned ${SimpleDateFormat("d MMMM yyyy", Locale.getDefault()).format(Date(badge.unlockedDate ?: System.currentTimeMillis()))} — ${badge.description}"
                    } else {
                        badge.requirementDescription
                    },
                    style = ShBodyStyle,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.widthIn(max = 300.dp),
                    lineHeight = 24.sp
                )
            }

            Column(modifier = Modifier.padding(bottom = 32.dp)) {
                if (!badge.isLocked) {
                    ShoshinButton(
                        onClick = { 
                            navController.navigate(ShRoutes.streakShare(1, badge.name, System.currentTimeMillis()))
                        },
                        variant = ShButtonVariant.Accent,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(painterResource(R.drawable.ic_share), null, modifier = Modifier.size(20.dp), tint = Color.White)
                        Spacer(Modifier.width(10.dp))
                        Text("Share this badge", fontWeight = FontWeight.Bold)
                    }
                } else {
                    ShoshinButton(
                        onClick = { },
                        variant = ShButtonVariant.Dark,
                        modifier = Modifier.fillMaxWidth(),
                        enabled = false
                    ) {
                        Text("Keep practicing to unlock", color = MaterialTheme.colorScheme.onSurfaceVariant, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
