package com.Shoshin.app.ui.screens

import androidx.compose.foundation.background
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
import com.Shoshin.app.R
import com.Shoshin.app.navigation.ShRoutes
import com.Shoshin.app.ui.components.*
import com.Shoshin.app.ui.theme.*

@Composable
fun GroupsScreen(
    navController: NavController,
    referralViewModel: com.Shoshin.app.viewmodel.ReferralViewModel? = null,
    groupViewModel: com.Shoshin.app.viewmodel.GroupViewModel? = null
) {
    val groups by groupViewModel?.groups?.collectAsState() ?: remember { mutableStateOf(emptyList()) }
    val isLoading by groupViewModel?.isLoading?.collectAsState() ?: remember { mutableStateOf(false) }

    var showJoinDialog by remember { mutableStateOf(false) }
    var joinCode by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        groupViewModel?.loadGroups()
    }

    ShoshinTheme(type = ShoshinThemeType.DYNAMIC) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
        ) {
            // App Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(horizontal = 24.dp, vertical = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    "Circles", 
                    style = ShTitleStyle.copy(fontSize = 32.sp), 
                    color = MaterialTheme.colorScheme.onBackground
                )
                IconButton(onClick = { navController.navigate(ShRoutes.REFERRALS) }) {
                    Icon(painterResource(R.drawable.ic_bolt), null, tint = MaterialTheme.colorScheme.primary)
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 24.dp)
            ) {
                Spacer(Modifier.height(8.dp))

                if (isLoading && groups.isEmpty()) {
                    Box(Modifier.fillMaxWidth().height(200.dp), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                    }
                } else if (groups.isEmpty()) {
                    EdgeLayout(
                        icon = R.drawable.ic_groups,
                        kicker = "YOUR CIRCLE IS QUIET",
                        title = "Find your circle",
                        body = "Solitude is peace, but a circle is power. Join others to rise together.",
                        actionLabel = "Create a Circle",
                        onAction = { navController.navigate(ShRoutes.CREATE_GROUP) }
                    )
                } else {
                    Kicker("YOUR CIRCLES", color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Spacer(Modifier.height(16.dp))

                    groups.forEach { group ->
                        ShoshinCard(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 16.dp)
                                .clickable { navController.navigate(ShRoutes.groupDetail(group.id)) }
                        ) {
                            Row(
                                modifier = Modifier.padding(20.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                // Group Avatar
                                Box(
                                    modifier = Modifier.size(48.dp).clip(CircleShape).background(MaterialTheme.colorScheme.surfaceVariant),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = group.name.take(1).uppercase(),
                                        style = ShH2Style.copy(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
                                    )
                                }
                                
                                Spacer(Modifier.width(16.dp))
                                
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(group.name, style = ShH2Style.copy(fontSize = 17.sp), color = MaterialTheme.colorScheme.onSurface)
                                    Text("${group.members.size} members", style = ShLabelStyle, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                                
                                Icon(
                                    painter = painterResource(R.drawable.ic_arrow_right), 
                                    null, 
                                    tint = MaterialTheme.colorScheme.outline, 
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                    }
                }

                Spacer(Modifier.height(24.dp))

                // Bottom Actions
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    ShoshinButton(
                        onClick = { navController.navigate(ShRoutes.CREATE_GROUP) },
                        variant = ShButtonVariant.Ghost,
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(painterResource(id = R.drawable.ic_plus), null, modifier = Modifier.size(18.dp), tint = MaterialTheme.colorScheme.onBackground)
                        Spacer(Modifier.width(8.dp))
                        Text("Create", color = MaterialTheme.colorScheme.onBackground, fontWeight = FontWeight.Bold)
                    }

                    ShoshinButton(
                        onClick = { showJoinDialog = true },
                        variant = ShButtonVariant.Primary,
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(painterResource(id = R.drawable.ic_groups), null, modifier = Modifier.size(18.dp), tint = MaterialTheme.colorScheme.background)
                        Spacer(Modifier.width(8.dp))
                        Text("Join", fontWeight = FontWeight.Bold)
                    }
                }
                
                Spacer(Modifier.height(48.dp))
            }
        }
    }

    if (showJoinDialog) {
        AlertDialog(
            onDismissRequest = { showJoinDialog = false },
            title = { Text("Join a Circle", style = ShTitleStyle.copy(fontSize = 24.sp, color = MaterialTheme.colorScheme.onSurface)) },
            text = {
                Column {
                    Text("Enter the circle code shared with you.", style = ShBodyStyle, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Spacer(Modifier.height(16.dp))
                    OutlinedTextField(
                        value = joinCode,
                        onValueChange = { joinCode = it.uppercase() },
                        placeholder = { Text("CODE", style = ShLabelStyle, color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                            unfocusedBorderColor = MaterialTheme.colorScheme.outline
                        )
                    )
                }
            },
            confirmButton = {
                TextButton(
                    onClick = { 
                        if (joinCode.isNotBlank()) {
                            groupViewModel?.joinGroup(joinCode)
                            showJoinDialog = false
                        }
                    }
                ) {
                    Text("Join", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showJoinDialog = false }) {
                    Text("Cancel", color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            },
            containerColor = MaterialTheme.colorScheme.surface,
            shape = RoundedCornerShape(24.dp)
        )
    }
}
