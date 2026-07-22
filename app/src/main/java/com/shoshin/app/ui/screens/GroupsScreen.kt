package com.shoshin.app.ui.screens

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
import com.shoshin.app.R
import com.shoshin.app.navigation.ShRoutes
import com.shoshin.app.ui.components.*
import com.shoshin.app.ui.theme.*

@Composable
fun GroupsScreen(
    navController: NavController,
    referralViewModel: com.shoshin.app.viewmodel.ReferralViewModel? = null,
    groupViewModel: com.shoshin.app.viewmodel.GroupViewModel? = null
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
        // Header
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(ShInk)
                .padding(horizontal = 24.dp, vertical = 32.dp)
        ) {
            Column {
                Kicker("Community", color = ShVermillion)
                Spacer(Modifier.height(8.dp))
                Text(
                    "Your Circles",
                    fontSize = 32.sp,
                    fontWeight = FontWeight.SemiBold,
                    fontFamily = CormorantFamily,
                    color = Color.White
                )
                Text(
                    "Rise together, stay accountable.",
                    fontSize = 15.sp,
                    color = ShFog,
                    fontFamily = DmSansFamily,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
        }
        
        // LIMIT DISPLAY (Feature 4.4)
        Surface(
            color = MaterialTheme.colorScheme.onBackground,
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
                    Text("MY GROUPS", style = ShKickerStyle, color = MaterialTheme.colorScheme.background.copy(alpha = 0.6f))
                    Text(
                        "Joined: $joinedCount of $maxJoin",
                        style = ShBodyStyle,
                        color = MaterialTheme.colorScheme.background,
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
                EdgeLayout(
                    icon = R.drawable.ic_groups,
                    kicker = "Solitude is peace, but a circle is power",
                    title = "Find your circle",
                    body = "Join or create a circle to rise with others and keep each other accountable.",
                    actionLabel = "Create a Circle",
                    onAction = { navController.navigate(ShRoutes.CREATE_GROUP) }
                )
            } else {
                Text("YOUR CIRCLES", style = ShKickerStyle, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.padding(bottom = 16.dp))
                
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
                    ShoshinButton(
                        onClick = { navController.navigate(ShRoutes.CREATE_GROUP) },
                        variant = ShButtonVariant.Ghost,
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(painterResource(id = R.drawable.ic_plus), null, modifier = Modifier.size(19.dp))
                        Spacer(Modifier.width(8.dp))
                        Text("Create")
                    }

                    ShoshinButton(
                        onClick = { /* Join code logic */ },
                        variant = ShButtonVariant.Primary,
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(painterResource(id = R.drawable.ic_groups), null, modifier = Modifier.size(19.dp))
                        Spacer(Modifier.width(8.dp))
                        Text("Join")
                    }
                }
            }

            Spacer(modifier = Modifier.height(48.dp))
        }
    }
}
