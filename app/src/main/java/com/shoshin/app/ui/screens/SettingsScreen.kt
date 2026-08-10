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
import androidx.lifecycle.viewmodel.compose.viewModel
import com.Shoshin.app.R
import com.Shoshin.app.navigation.ShRoutes
import com.Shoshin.app.ui.components.*
import com.Shoshin.app.ui.theme.*
import com.Shoshin.app.viewmodel.SettingsViewModel

@Composable
fun SettingsScreen(
    navController: NavController,
    viewModel: SettingsViewModel
) {
    val user by viewModel.user.collectAsState()
    val userName = user?.displayName ?: user?.email ?: "User"

    ShoshinTheme(type = ShoshinThemeType.DYNAMIC) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .statusBarsPadding()
        ) {
            // App Bar - Fix #2: Back Button + Username
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(
                        painter = painterResource(R.drawable.ic_arrow_left),
                        contentDescription = "Back",
                        tint = MaterialTheme.colorScheme.onBackground
                    )
                }
                Spacer(Modifier.width(8.dp))
                Text(
                    text = userName,
                    style = ShTitleStyle.copy(fontSize = 28.sp, color = MaterialTheme.colorScheme.onBackground)
                )
            }

            user?.let { u ->
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(bottom = 32.dp)
                ) {
                    // Profile Section - Fix #4: Design Layout
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(
                            modifier = Modifier
                                .size(96.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.surfaceVariant),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = userName.firstOrNull()?.toString()?.uppercase() ?: "U",
                                style = ShTitleStyle.copy(fontSize = 36.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
                            )
                        }
                        Spacer(Modifier.height(16.dp))
                        Text(u.email ?: "", style = ShLabelStyle, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text("Joined July 2026", style = ShLabelStyle.copy(fontSize = 11.sp), color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f))
                    }

                    // Fix #5: Prominent Mark Practice Button
                    val isTodayMarked = u.lastRoutineStepIndex > 0 // In production, check date too
                    MarkPracticeSection(isTodayMarked) {
                        navController.navigate(ShRoutes.ACTIVATION)
                    }

                    // APPEARANCE
                    val themeViewModel: ThemeViewModel = viewModel(key = "ShoshinThemeViewModel")
                    val currentMode by themeViewModel.mode.collectAsState()
                    
                    SettingsSection(title = "APPEARANCE") {
                        ThemeSelectorRow(
                            selectedMode = currentMode,
                            onModeSelected = { themeViewModel.setMode(it) }
                        )
                    }

                    // ACCOUNT
                    SettingsSection(title = "ACCOUNT") {
                        SettingsRow(
                            title = "Profile details",
                            subtitle = u.phone ?: u.email,
                            iconRes = R.drawable.ic_user,
                            onClick = { navController.navigate(ShRoutes.EDIT_PROFILE) }
                        )
                        HorizontalDivider(color = MaterialTheme.colorScheme.outline, modifier = Modifier.padding(horizontal = 16.dp))
                        SettingsRow(
                            title = "Invite the circle",
                            subtitle = "Earn a month of Pro",
                            iconRes = R.drawable.ic_plus,
                            onClick = { navController.navigate(ShRoutes.REFERRALS) }
                        )
                    }

                    // PRACTICE
                    SettingsSection(title = "PRACTICE") {
                        SettingsRow(
                            title = "Default challenge",
                            value = "Standard",
                            iconRes = R.drawable.ic_bolt,
                            onClick = { /* Change challenge */ }
                        )
                        HorizontalDivider(color = MaterialTheme.colorScheme.outline, modifier = Modifier.padding(horizontal = 16.dp))
                        SettingsSwitchRow(
                            title = "Require photo proof",
                            checked = true,
                            onCheckedChange = { },
                            iconRes = R.drawable.ic_camera
                        )
                        HorizontalDivider(color = MaterialTheme.colorScheme.outline, modifier = Modifier.padding(horizontal = 16.dp))
                        SettingsSwitchRow(
                            title = "Strict mode",
                            subtitle = "No skips, no excuses",
                            checked = false,
                            onCheckedChange = { },
                            iconRes = R.drawable.ic_lock
                        )
                    }

                    // NOTIFICATIONS - Fix #6: Functional Button
                    SettingsSection(title = "NOTIFICATIONS") {
                        SettingsRow(
                            title = "View all notifications",
                            iconRes = R.drawable.ic_bell,
                            onClick = { navController.navigate(ShRoutes.NOTIFICATIONS) }
                        )
                        HorizontalDivider(color = MaterialTheme.colorScheme.outline, modifier = Modifier.padding(horizontal = 16.dp))
                        SettingsRow(
                            title = "Wind-down reminder",
                            value = "9:30 PM",
                            iconRes = R.drawable.ic_moon,
                            onClick = { /* Change time */ }
                        )
                        HorizontalDivider(color = MaterialTheme.colorScheme.outline, modifier = Modifier.padding(horizontal = 16.dp))
                        SettingsSwitchRow(
                            title = "Streak alerts",
                            checked = true,
                            onCheckedChange = { },
                            iconRes = R.drawable.ic_droplet
                        )
                    }

                    // SUPPORT & PRIVACY
                    SettingsSection(title = "SUPPORT & PRIVACY") {
                        SettingsRow(
                            title = "Help & Support",
                            iconRes = R.drawable.ic_help,
                            onClick = { navController.navigate(ShRoutes.SUPPORT) }
                        )
                        HorizontalDivider(color = MaterialTheme.colorScheme.outline, modifier = Modifier.padding(horizontal = 16.dp))
                        SettingsRow(
                            title = "Privacy & Data",
                            iconRes = R.drawable.ic_shield,
                            onClick = { navController.navigate(ShRoutes.DATA_PRIVACY) }
                        )
                    }

                    // LOGOUT
                    Spacer(Modifier.height(24.dp))
                    Text(
                        text = "Log Out",
                        style = ShLabelStyle.copy(color = MaterialTheme.colorScheme.error, fontWeight = FontWeight.Bold),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { viewModel.logout { navController.navigate(ShRoutes.AUTH) { popUpTo(0) } } }
                            .padding(vertical = 16.dp),
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                }
            }
        }
    }
}

@Composable
private fun MarkPracticeSection(isCompleted: Boolean, onStart: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(if (isCompleted) MaterialTheme.colorScheme.secondary.copy(alpha = 0.1f) else Color.Transparent)
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = if (isCompleted) "Today's practice: Completed ✓" else "Mark today's practice",
                style = ShLabelStyle,
                color = if (isCompleted) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.onSurfaceVariant
            )
            if (isCompleted) {
                Icon(painterResource(R.drawable.ic_check), null, tint = MaterialTheme.colorScheme.secondary, modifier = Modifier.size(16.dp))
            }
        }
        
        Spacer(Modifier.height(8.dp))
        
        ShoshinButton(
            onClick = onStart,
            variant = ShButtonVariant.Accent,
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
        ) {
            Icon(painterResource(R.drawable.ic_check), null, modifier = Modifier.size(20.dp))
            Spacer(Modifier.width(8.dp))
            Text("Mark Practice Complete", fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun ThemeSelectorRow(
    selectedMode: ShThemeMode,
    onModeSelected: (ShThemeMode) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        ShThemeMode.entries.forEach { mode ->
            val isSelected = mode == selectedMode
            Surface(
                onClick = { onModeSelected(mode) },
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(12.dp),
                color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface,
                border = if (!isSelected) androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline) else null,
                contentColor = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface
            ) {
                Box(
                    modifier = Modifier.padding(vertical = 12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = mode.name.lowercase().replaceFirstChar { it.uppercase() },
                        style = ShLabelStyle.copy(fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    )
                }
            }
        }
    }
}

@Composable
private fun SettingsSection(title: String, content: @Composable ColumnScope.() -> Unit) {
    Column(modifier = Modifier.padding(horizontal = 24.dp, vertical = 16.dp)) {
        Kicker(title, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Spacer(Modifier.height(12.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(24.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant)
        ) {
            Column(content = content)
        }
    }
}

@Composable
private fun SettingsRow(
    title: String,
    subtitle: String? = null,
    value: String? = null,
    iconRes: Int,
    onClick: (() -> Unit)? = null
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(enabled = onClick != null) { onClick?.invoke() }
            .padding(20.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier.size(40.dp).clip(RoundedCornerShape(10.dp)).background(MaterialTheme.colorScheme.surface),
            contentAlignment = Alignment.Center
        ) {
            Icon(painterResource(iconRes), null, tint = MaterialTheme.colorScheme.onSurface, modifier = Modifier.size(20.dp))
        }
        Spacer(Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(title, style = ShH2Style.copy(fontSize = 16.sp, color = MaterialTheme.colorScheme.onSurface))
            if (subtitle != null) {
                Text(subtitle, style = ShLabelStyle.copy(fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant))
            }
        }
        if (value != null) {
            Text(value, style = ShLabelStyle.copy(color = MaterialTheme.colorScheme.onSurfaceVariant))
            Spacer(Modifier.width(8.dp))
        }
        Icon(painterResource(R.drawable.ic_arrow_right), null, tint = MaterialTheme.colorScheme.outline, modifier = Modifier.size(18.dp))
    }
}

@Composable
private fun SettingsSwitchRow(
    title: String,
    subtitle: String? = null,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    iconRes: Int
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(20.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier.size(40.dp).clip(RoundedCornerShape(10.dp)).background(MaterialTheme.colorScheme.surface),
            contentAlignment = Alignment.Center
        ) {
            Icon(painterResource(iconRes), null, tint = MaterialTheme.colorScheme.onSurface, modifier = Modifier.size(20.dp))
        }
        Spacer(Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(title, style = ShH2Style.copy(fontSize = 16.sp, color = MaterialTheme.colorScheme.onSurface))
            if (subtitle != null) {
                Text(subtitle, style = ShLabelStyle.copy(fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant))
            }
        }
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = ShMatcha,
                uncheckedThumbColor = Color.White.copy(alpha = 0.4f),
                uncheckedTrackColor = MaterialTheme.colorScheme.outline
            )
        )
    }
}
