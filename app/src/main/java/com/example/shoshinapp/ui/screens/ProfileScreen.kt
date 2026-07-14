package com.example.shoshinapp.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.shoshinapp.R
import com.example.shoshinapp.ui.components.*
import com.example.shoshinapp.ui.theme.*
import com.example.shoshinapp.viewmodel.ProfileViewModel
import com.example.shoshinapp.viewmodel.BadgeViewModel
import com.example.shoshinapp.navigation.ShRoutes

@Composable
fun ProfileScreen(
    navController: NavController,
    viewModel: ProfileViewModel,
    badgeViewModel: BadgeViewModel? = null
) {
    val user by viewModel.user.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val badges by badgeViewModel?.badges?.collectAsState(initial = emptyList()) ?: remember { mutableStateOf(emptyList()) }
    val earnedCount = badges.count { !it.isLocked }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(ShPaper)
            .verticalScroll(rememberScrollState())
    ) {
        // Custom App Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { navController.popBackStack() }) {
                Icon(painterResource(R.drawable.ic_arrow_left), contentDescription = "Back", tint = ShInk)
            }
            Text("Profile", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            IconButton(onClick = { navController.navigate("settings") }) {
                Icon(Icons.Default.Settings, contentDescription = "Settings", tint = ShInk)
            }
        }

        if (isLoading && user == null) {
            Box(Modifier.fillMaxWidth().height(300.dp), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = ShVermillion)
            }
        } else if (user == null) {
            Box(Modifier.fillMaxWidth().height(300.dp), contentAlignment = Alignment.Center) {
                Text("Profile not found. Please sign in again.", color = ShFog)
            }
        } else {
            user?.let { u ->
                // Profile Info
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(120.dp)
                            .clip(CircleShape)
                            .background(ShSand)
                            .border(2.dp, ShLine, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        if (u.profilePictureUrl != null) {
                            AsyncImage(
                                model = u.profilePictureUrl,
                                contentDescription = "Profile Picture",
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                        } else {
                            Icon(
                                Icons.Default.Person,
                                contentDescription = null,
                                modifier = Modifier.size(60.dp),
                                tint = ShFog2
                            )
                        }
                    }

                    Spacer(Modifier.height(16.dp))

                    Text(
                        text = u.displayName,
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = ShInk
                    )

                    u.bio?.let { bio ->
                        Text(
                            text = bio,
                            style = MaterialTheme.typography.bodyMedium,
                            color = ShFog,
                            modifier = Modifier.padding(top = 8.dp)
                        )
                    }

                    Spacer(Modifier.height(24.dp))

                    ShoshinButton(
                        onClick = { navController.navigate("edit_profile") },
                        variant = ShButtonVariant.Ghost,
                        modifier = Modifier.widthIn(max = 200.dp)
                    ) {
                        Text("Edit Profile")
                    }
                }

                Spacer(Modifier.height(40.dp))

                // Referral Section
                Column(modifier = Modifier.padding(horizontal = 24.dp)) {
                    Kicker("YOUR REFERRAL CODE", color = ShInk)
                    Spacer(Modifier.height(12.dp))
                    ShoshinCard(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { navController.navigate(ShRoutes.REFERRALS) }
                    ) {
                        Row(
                            modifier = Modifier.padding(20.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                val referralCode = u.inviteCode.ifEmpty { "TAP TO GEN" }
                                Text(text = referralCode, style = ShNumeralStyle.copy(fontSize = 24.sp), letterSpacing = 2.sp)
                                Text("Tap to see your rewards", style = ShLabelStyle, color = ShFog)
                            }
                            Icon(Icons.Default.ChevronRight, contentDescription = null, tint = ShLine2)
                        }
                    }
                }

                Spacer(Modifier.height(40.dp))

                // Badges Section
                Column(modifier = Modifier.padding(horizontal = 24.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Kicker("My Badges ($earnedCount/${badges.size})", color = ShInk)
                        TextButton(onClick = { navController.navigate(ShRoutes.BADGES) }) {
                            Text("Show all", style = ShLabelStyle, color = ShVermillion)
                        }
                    }
                    
                    Spacer(Modifier.height(12.dp))
                    
                    if (badges.isNotEmpty()) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            badges.take(4).forEach { badge ->
                                Box(
                                    modifier = Modifier
                                        .size(64.dp)
                                        .background(
                                            if (badge.isLocked) ShSand else Color.parseColor(badge.color).copy(alpha = 0.15f),
                                            RoundedCornerShape(12.dp)
                                        )
                                        .clickable { navController.navigate(ShRoutes.badgeDetail(badge.id)) },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = badge.icon,
                                        fontSize = 28.sp,
                                        modifier = Modifier.alpha(if (badge.isLocked) 0.3f else 1f)
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(Modifier.height(40.dp))

                // Stats Section
                Column(modifier = Modifier.padding(horizontal = 24.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Kicker("Statistics", color = ShInk)
                        TextButton(onClick = { navController.navigate(ShRoutes.STATS) }) {
                            Text("View detail", style = ShLabelStyle, color = ShVermillion)
                        }
                    }
                    Spacer(Modifier.height(16.dp))

                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        StatCard(
                            label = "Best Streak",
                            value = "${u.bestStreak}",
                            icon = R.drawable.ic_flame,
                            color = ShVermillion,
                            modifier = Modifier.weight(1f)
                        )
                        StatCard(
                            label = "Friends",
                            value = "${u.friendCount}",
                            icon = R.drawable.ic_groups,
                            color = ShMatcha,
                            modifier = Modifier.weight(1f),
                            onClick = { navController.navigate(ShRoutes.ALL_FRIENDS) }
                        )
                    }
                }

                Spacer(Modifier.height(40.dp))
            }
        }
    }
}

private fun Color.Companion.parseColor(colorString: String): Color {
    return try {
        Color(android.graphics.Color.parseColor(colorString))
    } catch (e: Exception) {
        ShVermillion // Default
    }
}

@Composable
fun StatCard(
    label: String,
    value: String,
    icon: Int,
    color: Color,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null
) {
    ShoshinCard(
        modifier = if (onClick != null) modifier.clickable { onClick() } else modifier
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                painter = painterResource(icon),
                contentDescription = null,
                tint = color,
                modifier = Modifier.size(24.dp)
            )
            Spacer(Modifier.height(12.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = ShInk
            )
            Text(
                text = label.uppercase(),
                style = ShKickerStyle.copy(fontSize = 10.sp),
                color = ShFog
            )
        }
    }
}
