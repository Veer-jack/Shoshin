package com.example.shoshinapp.ui.screens

import androidx.compose.foundation.background
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
            modifier = Modifier.fillMaxWidth().padding(top = 16.dp, bottom = 22.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            IconButton(onClick = { navController.popBackStack() }, modifier = Modifier.size(24.dp)) {
                Icon(painterResource(R.drawable.ic_arrow_left), contentDescription = "Back")
            }
            Text("Marks of practice", style = ShTitleStyle.copy(fontSize = 26.sp), fontWeight = FontWeight.SemiBold)
        }

        Text(
            text = "$earnedCount of ${badges.size} earned",
            style = ShBodyStyle,
            modifier = Modifier.padding(bottom = 6.dp)
        )
        
        // Progress Bar
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .clip(CircleShape)
                .background(ShSand)
                .padding(bottom = 24.dp) // Wait, padding on Box might not be what I want for spacing
        )
        // Re-writing progress bar logic
        LinearProgressIndicator(
            progress = { earnedCount.toFloat() / badges.size.coerceAtLeast(1) },
            modifier = Modifier.fillMaxWidth().height(8.dp).clip(CircleShape),
            color = ShVermillion,
            trackColor = ShSand
        )

        Spacer(Modifier.height(24.dp))

        // Grid 3 columns
        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
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
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(130.dp),
        shape = RoundedCornerShape(20.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = ShSurface,
            contentColor = ShInk
        ),
        contentPadding = PaddingValues(8.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, ShLine)
    ) {
        Column(
            modifier = Modifier.fillMaxSize().alpha(if (badge.isLocked) 0.45f else 1f),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(if (badge.isLocked) ShPaper2 else Color.parseColor(badge.color).copy(alpha = 0.08f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                // Use lock icon if locked
                Icon(
                    painter = painterResource(if (badge.isLocked) R.drawable.ic_lock else getBadgeIconRes(badge.icon)),
                    contentDescription = null,
                    tint = if (badge.isLocked) ShFog else ShVermillion,
                    modifier = Modifier.size(22.dp)
                )
            }
            Spacer(Modifier.height(10.dp))
            Text(
                text = badge.name,
                style = ShLabelStyle.copy(fontSize = 11.5.sp, fontWeight = FontWeight.SemiBold),
                textAlign = TextAlign.Center,
                color = if (badge.isLocked) ShFog else ShInk
            )
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
