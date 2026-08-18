package com.Shoshin.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import com.Shoshin.app.data.db.entities.avatarUrl
import com.Shoshin.app.ui.components.*
import com.Shoshin.app.ui.theme.*
import com.Shoshin.app.viewmodel.ProfileViewModel
import com.Shoshin.app.viewmodel.BadgeViewModel
import com.Shoshin.app.navigation.ShRoutes

@Composable
fun ProfileScreen(
    navController: NavController,
    viewModel: ProfileViewModel,
    badgeViewModel: BadgeViewModel? = null
) {
    val user by viewModel.user.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val badges by badgeViewModel?.badges?.collectAsState(initial = emptyList()) ?: remember { mutableStateOf(emptyList()) }
    
    // Extract real data from user entity
    val displayName = user?.displayName ?: "User"
    val morningsCount = user?.totalActivations ?: 0
    val currentStreak = user?.currentStreak ?: 0
    val bestStreak = user?.bestStreak ?: 0
    
    // Calculate real consistency
    val consistencyValue = if (user != null && user!!.totalActivations > 0) {
        val daysSinceCreation = ((System.currentTimeMillis() - user!!.createdAt) / (1000 * 60 * 60 * 24)).coerceAtLeast(1)
        ((user!!.totalActivations.toFloat() / daysSinceCreation.toFloat()) * 100).toInt().coerceIn(0, 100)
    } else {
        0
    }

    ShoshinTheme(type = ShoshinThemeType.DYNAMIC) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .verticalScroll(rememberScrollState())
        ) {
            // Top App Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(horizontal = 24.dp, vertical = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("You", style = ShTitleStyle.copy(fontSize = 32.sp), color = MaterialTheme.colorScheme.onBackground)
                Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    Icon(
                        painterResource(R.drawable.ic_bell), 
                        null, 
                        tint = MaterialTheme.colorScheme.onBackground, 
                        modifier = Modifier.size(24.dp).clickable { navController.navigate(ShRoutes.NOTIFICATIONS) }
                    )
                    Icon(
                        painterResource(R.drawable.ic_settings),
                        null, 
                        tint = MaterialTheme.colorScheme.onBackground, 
                        modifier = Modifier.size(24.dp).clickable { navController.navigate(ShRoutes.SETTINGS) }
                    )
                }
            }

            if (isLoading && user == null) {
                Box(Modifier.fillMaxWidth().height(300.dp), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = ShVermillion)
                }
            } else {
                user?.let { u ->
                    // Main Profile Card
                    ShoshinCard(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 24.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            // Avatar
                            ShoshinAvatar(
                                imageUrl = u.avatarUrl,
                                name = displayName,
                                size = 100.dp,
                                onClick = { navController.navigate(ShRoutes.EDIT_PROFILE) }
                            )

                            Spacer(Modifier.height(16.dp))

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = displayName,
                                    style = ShTitleStyle.copy(fontSize = 28.sp),
                                    color = MaterialTheme.colorScheme.onSurface,
                                    textAlign = TextAlign.Center
                                )
                                Spacer(Modifier.width(8.dp))
                                Icon(
                                    painterResource(R.drawable.ic_edit),
                                    contentDescription = "Edit profile",
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier
                                        .size(18.dp)
                                        .clickable { navController.navigate(ShRoutes.EDIT_PROFILE) }
                                )
                            }

                            if (!u.bio.isNullOrBlank()) {
                                Spacer(Modifier.height(8.dp))
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(50))
                                        .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(50))
                                        .padding(horizontal = 14.dp, vertical = 6.dp)
                                ) {
                                    Text(u.bio!!, style = ShLabelStyle.copy(fontSize = 12.sp), color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                            }

                            Spacer(Modifier.height(24.dp))
                            HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f), thickness = 1.dp, modifier = Modifier.padding(horizontal = 24.dp))
                            Spacer(Modifier.height(24.dp))

                            // Row Stats
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceAround,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                ProfileStatItem(value = morningsCount.toString(), label = "MORNINGS", textColor = MaterialTheme.colorScheme.onSurface)
                                ProfileStatItem(value = currentStreak.toString(), label = "CURRENT", valueColor = ShVermillion)
                                ProfileStatItem(value = consistencyValue.toString(), label = "CONSISTENCY", unit = "%", textColor = MaterialTheme.colorScheme.onSurface)
                            }
                        }
                    }

                    Spacer(Modifier.height(24.dp))

                    // Invite CTA — teaser for the full Referral screen
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 24.dp)
                            .clip(RoundedCornerShape(20.dp))
                            .background(ShNight)
                            .clickable { navController.navigate(ShRoutes.REFERRALS) }
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier.size(48.dp).clip(CircleShape).background(ShNight3),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(painterResource(R.drawable.ic_gift), null, tint = ShVermillionLight, modifier = Modifier.size(22.dp))
                        }
                        Spacer(Modifier.width(16.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Invite the circle", style = ShLabelStyle.copy(fontSize = 15.sp), fontWeight = FontWeight.Bold, color = Color.White)
                            Text("Unlock 5 extra spots in your circle", style = ShLabelStyle.copy(fontSize = 12.sp), color = ShNightMuted)
                        }
                        Icon(painterResource(R.drawable.ic_arrow_right), null, tint = ShNightMuted, modifier = Modifier.size(18.dp))
                    }

                    Spacer(Modifier.height(32.dp))

                    // Marks of Practice Section
                    Column(modifier = Modifier.padding(horizontal = 24.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.Bottom
                        ) {
                            Column {
                                Kicker("EARNED", color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Text("Marks of practice", style = ShH2Style.copy(fontSize = 20.sp), color = MaterialTheme.colorScheme.onSurface)
                            }
                            Text(
                                "See all", 
                                style = ShLabelStyle, 
                                color = ShVermillion, 
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.clickable { navController.navigate(ShRoutes.BADGES) }
                            )
                        }

                        Spacer(Modifier.height(20.dp))

                        val earnedBadges = badges.filter { !it.isLocked }
                        val lockedBadges = badges.filter { it.isLocked }
                        val displayBadges = (earnedBadges + lockedBadges).take(6)
                        
                        Column {
                            displayBadges.chunked(3).forEach { row ->
                                Row(
                                    modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
                                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                                ) {
                                    row.forEach { badge ->
                                        if (badge.isLocked) {
                                            LockedBadgeItem(label = badge.name, modifier = Modifier.weight(1f))
                                        } else {
                                            EarnedBadgeItem(
                                                icon = getBadgeIconRes(badge.icon), 
                                                label = badge.name, 
                                                modifier = Modifier.weight(1f),
                                                onClick = { navController.navigate(ShRoutes.badgeDetail(badge.id)) }
                                            )
                                        }
                                    }
                                    repeat(3 - row.size) {
                                        Spacer(modifier = Modifier.weight(1f))
                                    }
                                }
                            }
                        }
                    }

                    Spacer(Modifier.height(16.dp))

                    ShoshinButton(
                        onClick = { navController.navigate(ShRoutes.STATS) },
                        variant = ShButtonVariant.Ghost,
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp),
                        leadingIcon = { Icon(painterResource(R.drawable.ic_pulse), null, modifier = Modifier.size(18.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant) }
                    ) {
                        Text("View full stats", color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }

                    Spacer(Modifier.height(48.dp))
                }
            }
        }
    }
}

@Composable
private fun ProfileStatItem(value: String, label: String, unit: String? = null, valueColor: Color? = null, textColor: Color = MaterialTheme.colorScheme.onSurface) {
    val finalValueColor = valueColor ?: textColor
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Row(verticalAlignment = Alignment.Bottom) {
            Text(value, style = ShNumeralStyle.copy(fontSize = 32.sp, color = finalValueColor))
            if (unit != null) {
                Text(unit, style = ShNumeralStyle.copy(fontSize = 16.sp, color = MaterialTheme.colorScheme.onSurfaceVariant), modifier = Modifier.padding(bottom = 6.dp, start = 2.dp))
            }
        }
        Text(label, style = ShKickerStyle.copy(fontSize = 9.sp, letterSpacing = 1.sp), color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

@Composable
private fun EarnedBadgeItem(
    icon: Int, 
    label: String, 
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null
) {
    ShoshinCard(modifier = modifier.then(if (onClick != null) Modifier.clickable { onClick() } else Modifier)) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier.size(48.dp).clip(CircleShape).background(ShVermillion.copy(alpha = 0.05f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(painterResource(icon), null, tint = ShVermillion.copy(alpha = 0.6f), modifier = Modifier.size(24.dp))
            }
            Spacer(Modifier.height(12.dp))
            Text(label, style = ShLabelStyle.copy(fontSize = 12.sp), fontWeight = FontWeight.Bold, textAlign = TextAlign.Center, lineHeight = 14.sp)
        }
    }
}

@Composable
private fun LockedBadgeItem(label: String, modifier: Modifier = Modifier) {
    ShoshinCard(modifier = modifier) {
        Column(
            modifier = Modifier.padding(16.dp).alpha(0.4f),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier.size(48.dp).clip(CircleShape).border(1.dp, MaterialTheme.colorScheme.outline, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(painterResource(R.drawable.ic_lock), null, tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(20.dp))
            }
            Spacer(Modifier.height(12.dp))
            Text(label, style = ShLabelStyle.copy(fontSize = 12.sp), textAlign = TextAlign.Center, lineHeight = 14.sp)
        }
    }
}

