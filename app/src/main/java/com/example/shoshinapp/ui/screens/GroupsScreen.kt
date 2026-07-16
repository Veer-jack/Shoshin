package com.example.shoshinapp.ui.screens

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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.shoshinapp.R
import com.example.shoshinapp.navigation.ShRoutes
import com.example.shoshinapp.ui.components.*
import com.example.shoshinapp.ui.theme.*

@Composable
fun GroupsScreen(
    navController: NavController,
    referralViewModel: com.example.shoshinapp.viewmodel.ReferralViewModel? = null,
    groupViewModel: com.example.shoshinapp.viewmodel.GroupViewModel? = null
) {
    val limits by referralViewModel?.limits?.collectAsState(initial = null) ?: remember { mutableStateOf(null) }
    
    val groups by groupViewModel?.groups?.collectAsState() ?: remember { mutableStateOf(emptyList()) }
    val isLoading by groupViewModel?.isLoading?.collectAsState() ?: remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        groupViewModel?.loadGroups()
    }

    val joinedCount = groups.size
    val maxJoin = limits?.groupsJoinLimit ?: 5

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // ... (Header Box remains same)
        
        // LIMIT DISPLAY (Feature 4.4)
        Surface(
            color = ShInk,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("MY GROUPS", style = ShKickerStyle, color = ShPaper.copy(alpha = 0.6f))
                    Text(
                        "Joined: $joinedCount of $maxJoin",
                        style = ShBodyStyle,
                        color = ShPaper,
                        fontWeight = FontWeight.Bold
                    )
                }
                TextButton(onClick = { navController.navigate(ShRoutes.REFERRALS) }) {
                    Text("Refer to unlock", color = ShVermillion, style = ShLabelStyle)
                }
            }
        }

        // WARNING BANNER (Feature 4.4)
        if (maxJoin - joinedCount <= 1) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(ShVermillion.copy(alpha = 0.1f))
                    .clickable { navController.navigate(ShRoutes.REFERRALS) }
                    .padding(vertical = 8.dp, horizontal = 24.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(painterResource(R.drawable.ic_info), contentDescription = null, tint = ShVermillion, modifier = Modifier.size(16.dp))
                Text(
                    "${maxJoin - joinedCount} group slot remaining. Refer a friend to unlock more →",
                    style = ShLabelStyle,
                    color = ShVermillion
                )
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp)
        ) {
            Spacer(Modifier.height(24.dp))

            if (groups.isEmpty()) {
                EmptyState(
                    title = "Solitude is peace, but a circle is power",
                    description = "Join or create a circle to rise with others and keep each other accountable.",
                    iconRes = R.drawable.ic_groups,
                    actionLabel = "Create a Circle",
                    onAction = { navController.navigate(ShRoutes.CREATE_GROUP) }
                )
            } else {
                Text("YOUR CIRCLES", style = ShKickerStyle, color = ShFog, modifier = Modifier.padding(bottom = 16.dp))
                
                groups.forEach { group ->
                    GroupCard(
                        name = group.name,
                        description = group.description,
                        memberCount = group.members.size,
                        onClick = { navController.navigate(ShRoutes.groupDetail(group.id)) }
                    )
                    Spacer(Modifier.height(16.dp))
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Action Buttons
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    // Create Button
                    Surface(
                        modifier = Modifier
                            .weight(1f)
                            .height(56.dp)
                            .clickable { navController.navigate(ShRoutes.CREATE_GROUP) },
                        color = Color.Transparent,
                        shape = RoundedCornerShape(14.dp),
                        border = androidx.compose.foundation.BorderStroke(1.5.dp, ShLine2)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxSize(),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_plus),
                                contentDescription = null,
                                tint = ShInk,
                                modifier = Modifier.size(19.dp)
                            )
                            Spacer(Modifier.width(8.dp))
                            Text("Create", fontSize = 14.5.sp, fontWeight = FontWeight.SemiBold, color = ShInk)
                        }
                    }

                    // Join Button
                    Surface(
                        modifier = Modifier
                            .weight(1f)
                            .height(56.dp)
                            .clickable { 
                                // Show join dialog or navigate to join
                                // For now, maybe just a placeholder
                            },
                        color = ShInk,
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxSize(),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_groups),
                                contentDescription = null,
                                tint = ShPaper,
                                modifier = Modifier.size(19.dp)
                            )
                            Spacer(Modifier.width(8.dp))
                            Text("Join with code", fontSize = 14.5.sp, fontWeight = FontWeight.SemiBold, color = ShPaper)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(48.dp))
        }
    }
}
