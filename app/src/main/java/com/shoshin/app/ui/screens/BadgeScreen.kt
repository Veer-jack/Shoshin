package com.Shoshin.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.Shoshin.app.R
import com.Shoshin.app.data.models.Badge
import com.Shoshin.app.navigation.ShRoutes
import com.Shoshin.app.ui.components.*
import com.Shoshin.app.ui.theme.*
import com.Shoshin.app.viewmodel.BadgeViewModel

@Composable
fun BadgeScreen(
    navController: NavController,
    viewModel: BadgeViewModel
) {
    val badges by viewModel.badges.collectAsState()
    val earnedCount = badges.count { !it.isLocked }

    ShoshinTheme(type = ShoshinThemeType.DYNAMIC) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .statusBarsPadding()
        ) {
            // App Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(painterResource(R.drawable.ic_arrow_left), contentDescription = "Back", tint = MaterialTheme.colorScheme.onBackground)
                }
                Spacer(Modifier.width(8.dp))
                Text("Marks of practice", style = ShTitleStyle.copy(fontSize = 32.sp), color = MaterialTheme.colorScheme.onBackground)
            }

            Column(modifier = Modifier.padding(horizontal = 24.dp)) {
                Text(
                    text = "$earnedCount of ${badges.size} earned",
                    style = ShLabelStyle.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                )
                
                Spacer(Modifier.height(16.dp))
                
                LinearProgressIndicator(
                    progress = { earnedCount.toFloat() / badges.size.coerceAtLeast(1) },
                    modifier = Modifier.fillMaxWidth().height(6.dp).clip(CircleShape),
                    color = ShVermillion,
                    trackColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
                    strokeCap = androidx.compose.ui.graphics.StrokeCap.Round
                )
            }

            Spacer(Modifier.height(32.dp))

            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                contentPadding = PaddingValues(horizontal = 24.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.weight(1f)
            ) {
                items(badges) { badge ->
                    BadgeGridItem(badge = badge) {
                        navController.navigate(ShRoutes.badgeDetail(badge.id))
                    }
                }
            }
        }
    }
}

@Composable
private fun BadgeGridItem(badge: Badge, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(0.9f)
            .clip(RoundedCornerShape(20.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .clickable { onClick() }
            .padding(12.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(CircleShape)
                    .background(if (badge.isLocked) Color.Transparent else ShVermillion.copy(alpha = 0.05f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(if (badge.isLocked) R.drawable.ic_lock else getBadgeIconRes(badge.icon)),
                    contentDescription = null,
                    tint = if (badge.isLocked) MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f) else ShVermillion,
                    modifier = Modifier.size(24.dp)
                )
            }
            Spacer(Modifier.height(12.dp))
            Text(
                text = badge.name,
                style = ShLabelStyle.copy(fontSize = 11.sp, color = if (badge.isLocked) MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f) else MaterialTheme.colorScheme.onSurface),
                fontWeight = if (badge.isLocked) FontWeight.Normal else FontWeight.Bold,
                textAlign = TextAlign.Center,
                lineHeight = 14.sp
            )
        }
    }
}
