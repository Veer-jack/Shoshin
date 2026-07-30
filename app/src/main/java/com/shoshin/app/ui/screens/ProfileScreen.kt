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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.Shoshin.app.R
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

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(ShPaper)
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
            Text("You", style = ShTitleStyle.copy(fontSize = 32.sp))
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                Icon(
                    painterResource(R.drawable.ic_bell), 
                    null, 
                    tint = ShInk, 
                    modifier = Modifier.size(24.dp).clickable { navController.navigate(ShRoutes.NOTIFICATIONS) }
                )
                Icon(
                    painterResource(R.drawable.ic_settings),
                    null, 
                    tint = ShInk, 
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
                        .padding(horizontal = 20.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // Avatar
                        Box(
                            modifier = Modifier
                                .size(100.dp)
                                .clip(CircleShape)
                                .background(ShPaper2),
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
                                Text(
                                    text = displayName.firstOrNull()?.toString()?.uppercase() ?: "A",
                                    style = ShTitleStyle.copy(fontSize = 36.sp, color = ShInk.copy(alpha = 0.6f))
                                )
                            }
                        }

                        Spacer(Modifier.height(16.dp))

                        Text(
                            text = displayName,
                            style = ShTitleStyle.copy(fontSize = 28.sp),
                            textAlign = TextAlign.Center
                        )

                        Spacer(Modifier.height(12.dp))

                        // Tiers
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Surface(
                                color = ShInk,
                                shape = RoundedCornerShape(999.dp)
                            ) {
                                val tierText = if (currentStreak >= 30) "Tier III · Master" 
                                              else if (currentStreak >= 14) "Tier II · Disciplined" 
                                              else "Tier I · Beginner"
                                Text(
                                    tierText,
                                    color = Color.White,
                                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp),
                                    style = ShKickerStyle.copy(fontSize = 11.sp, letterSpacing = 0.5.sp)
                                )
                            }
                            Surface(
                                color = Color.Transparent,
                                shape = RoundedCornerShape(999.dp),
                                border = androidx.compose.foundation.BorderStroke(1.dp, ShLine)
                            ) {
                                Text(
                                    "Early Riser",
                                    color = ShFog,
                                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp),
                                    style = ShKickerStyle.copy(fontSize = 11.sp, letterSpacing = 0.5.sp)
                                )
                            }
                        }

                        Spacer(Modifier.height(24.dp))
                        HorizontalDivider(color = ShLine, thickness = 1.dp, modifier = Modifier.padding(horizontal = 24.dp))
                        Spacer(Modifier.height(24.dp))

                        // Row Stats
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceAround,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            ProfileStatItem(value = morningsCount.toString(), label = "MORNINGS")
                            ProfileStatItem(value = currentStreak.toString(), label = "CURRENT", valueColor = ShVermillion)
                            ProfileStatItem(value = consistencyValue.toString(), label = "CONSISTENCY", unit = "%")
                        }
                    }
                }

                Spacer(Modifier.height(16.dp))

                // 71-Day Discipline Card (Real or Dynamic)
                ShoshinCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp)
                        .clickable { navController.navigate(ShRoutes.DISCIPLINE_71) }
                ) {
                    Row(
                        modifier = Modifier.padding(20.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Calculate discipline progress (max 71)
                        val disciplineProgress = (currentStreak.toFloat() / 71f * 100f).coerceIn(0f, 100f).toInt()
                        
                        Box(contentAlignment = Alignment.Center) {
                            RingProgress(
                                percentage = disciplineProgress,
                                size = 64,
                                strokeWidth = 5f,
                                valueText = "",
                                color = ShVermillion,
                                trackColor = ShLine.copy(alpha = 0.5f)
                            )
                        }
                        
                        Spacer(Modifier.width(20.dp))
                        
                        Column(modifier = Modifier.weight(1f)) {
                            Kicker("IN PROGRESS", color = ShVermillion)
                            Text("71-Day Discipline", style = ShH2Style.copy(fontSize = 18.sp))
                            val dayText = if (currentStreak <= 71) "Day $currentStreak · Practice phase" else "Goal Achieved"
                            Text(dayText, style = ShLabelStyle, color = ShFog)
                        }
                        
                        Icon(painterResource(R.drawable.ic_arrow_right), null, tint = ShLine2, modifier = Modifier.size(24.dp))
                    }
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
                            Kicker("EARNED", color = ShFog)
                            Text("Marks of practice", style = ShH2Style.copy(fontSize = 20.sp))
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

                    // Badge Grid (Dynamic based on earned badges)
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
                                // Fill empty slots in the row
                                repeat(3 - row.size) {
                                    Spacer(modifier = Modifier.weight(1f))
                                }
                            }
                        }
                    }
                }

                Spacer(Modifier.height(16.dp))

                // View Full Stats Button
                ShoshinButton(
                    onClick = { navController.navigate(ShRoutes.STATS) },
                    variant = ShButtonVariant.Ghost,
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp),
                    leadingIcon = { Icon(painterResource(R.drawable.ic_pulse), null, modifier = Modifier.size(18.dp), tint = ShFog) }
                ) {
                    Text("View full stats", color = ShFog)
                }

                Spacer(Modifier.height(48.dp))
            }
        }
    }
}

@Composable
private fun ProfileStatItem(value: String, label: String, unit: String? = null, valueColor: Color = ShInk) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Row(verticalAlignment = Alignment.Bottom) {
            Text(value, style = ShNumeralStyle.copy(fontSize = 32.sp, color = valueColor))
            if (unit != null) {
                Text(unit, style = ShNumeralStyle.copy(fontSize = 16.sp, color = ShFog), modifier = Modifier.padding(bottom = 6.dp, start = 2.dp))
            }
        }
        Text(label, style = ShKickerStyle.copy(fontSize = 9.sp, letterSpacing = 1.sp), color = ShFog)
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
                modifier = Modifier.size(48.dp).clip(CircleShape).border(1.dp, ShLine, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(painterResource(R.drawable.ic_lock), null, tint = ShFog, modifier = Modifier.size(20.dp))
            }
            Spacer(Modifier.height(12.dp))
            Text(label, style = ShLabelStyle.copy(fontSize = 12.sp), textAlign = TextAlign.Center, lineHeight = 14.sp)
        }
    }
}

