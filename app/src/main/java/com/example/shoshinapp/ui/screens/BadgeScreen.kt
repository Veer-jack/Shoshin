package com.example.shoshinapp.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
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
import com.example.shoshinapp.R
import com.example.shoshinapp.data.models.Badge
import com.example.shoshinapp.navigation.ShRoutes
import com.example.shoshinapp.ui.components.*
import com.example.shoshinapp.ui.theme.*
import com.example.shoshinapp.viewmodel.BadgeViewModel

@Composable
fun BadgeScreen(
    navController: NavController,
    viewModel: BadgeViewModel
) {
    val badges by viewModel.badges.collectAsState()
    val earnedCount = badges.count { !it.isLocked }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(ShPaper)
            .padding(horizontal = 24.dp)
    ) {
        // App Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 24.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { navController.popBackStack() }) {
                Icon(painterResource(R.drawable.ic_arrow_left), contentDescription = "Back")
            }
            Spacer(Modifier.width(16.dp))
            Text("My Badges", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
        }

        // Summary
        ShoshinCard(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier.padding(20.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier.size(60.dp).background(ShSand, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text("🏅", fontSize = 32.sp)
                }
                Spacer(Modifier.width(20.dp))
                Column {
                    Text("$earnedCount/${badges.size} Unlocked", style = ShNumeralStyle.copy(fontSize = 20.sp))
                    Text("Keep crossing the bridge to earn more", style = ShLabelStyle, color = ShFog)
                }
            }
        }

        Spacer(Modifier.height(32.dp))

        // Grid
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.weight(1f)
        ) {
            items(badges) { badge ->
                BadgeItem(badge = badge) {
                    navController.navigate(ShRoutes.badgeDetail(badge.id))
                }
            }
        }
        
        Spacer(Modifier.height(24.dp))
    }
}

@Composable
fun BadgeItem(badge: Badge, onClick: () -> Unit) {
    ShoshinCard(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .alpha(if (badge.isLocked) 0.6f else 1f)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .background(if (badge.isLocked) ShSand else Color.parseColor(badge.color).copy(alpha = 0.15f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = badge.icon,
                    fontSize = 32.sp,
                    modifier = Modifier.alpha(if (badge.isLocked) 0.4f else 1f)
                )
            }
            Spacer(Modifier.height(12.dp))
            Text(
                text = badge.name,
                style = ShLabelStyle,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
            if (badge.isLocked && badge.threshold > 0) {
                val progress = badge.currentProgress.toFloat() / badge.threshold
                Spacer(Modifier.height(8.dp))
                LinearProgressIndicator(
                    progress = progress.coerceIn(0f, 1f),
                    modifier = Modifier.fillMaxWidth().height(4.dp).clip(CircleShape),
                    color = ShVermillion,
                    trackColor = ShSand
                )
            }
        }
    }
}

private fun Color.Companion.parseColor(colorString: String): Color {
    return Color(android.graphics.Color.parseColor(colorString))
}
