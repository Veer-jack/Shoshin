package com.example.shoshinapp.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.shoshinapp.R
import com.example.shoshinapp.navigation.ShRoutes
import com.example.shoshinapp.ui.components.*
import com.example.shoshinapp.ui.theme.*
import com.example.shoshinapp.viewmodel.SettingsViewModel
import java.util.Locale

@Composable
fun SettingsScreen(
    navController: NavController,
    viewModel: SettingsViewModel
) {
    val user by viewModel.user.collectAsState()
    var showLogoutDialog by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(ShPaper)
            .verticalScroll(rememberScrollState())
    ) {
        // App Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { navController.popBackStack() }) {
                Icon(painterResource(R.drawable.ic_arrow_left), contentDescription = "Back", tint = ShInk)
            }
            Spacer(Modifier.width(16.dp))
            Text("Settings", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
        }

        user?.let { u ->
            // Notifications
            SettingsSection(title = "Notifications") {
                SettingsSwitchRow(
                    title = "Push Notifications",
                    subtitle = "Receive alerts for routine starts",
                    checked = u.notificationsEnabled,
                    onCheckedChange = { viewModel.updateNotifications(it) },
                    icon = Icons.Default.Notifications
                )
                
                var showTimePicker by remember { mutableStateOf(false) }
                SettingsRow(
                    title = "Reminder Time",
                    subtitle = u.notificationTime,
                    onClick = { showTimePicker = true },
                    icon = Icons.Default.Alarm
                )

                if (showTimePicker) {
                    val timeParts = u.notificationTime.split(":")
                    val currentHour = timeParts.getOrNull(0)?.toIntOrNull() ?: 6
                    val currentMinute = timeParts.getOrNull(1)?.toIntOrNull() ?: 0
                    
                    TimePickerDialog(
                        onDismiss = { showTimePicker = false },
                        onTimeSelected = { hour, minute ->
                            viewModel.updateNotificationTime(hour, minute)
                            showTimePicker = false
                        },
                        title = "Reminder Time",
                        initialHour = currentHour,
                        initialMinute = currentMinute
                    )
                }
            }

            // Productivity
            SettingsSection(title = "Productivity") {
                var showStartTimePicker by remember { mutableStateOf(false) }
                var showEndTimePicker by remember { mutableStateOf(false) }

                SettingsRow(
                    title = "Productive Window",
                    subtitle = "${u.productiveStartTime} - ${u.productiveEndTime}",
                    onClick = { showStartTimePicker = true },
                    icon = Icons.Default.Timer
                )

                if (showStartTimePicker) {
                    TimePickerDialog(
                        onDismiss = { showStartTimePicker = false },
                        onTimeSelected = { hour, minute ->
                            val time = String.format(Locale.getDefault(), "%02d:%02d", hour, minute)
                            viewModel.updateProductiveHours(time, u.productiveEndTime)
                            showStartTimePicker = false
                            showEndTimePicker = true
                        },
                        title = "Start Time"
                    )
                }

                if (showEndTimePicker) {
                    TimePickerDialog(
                        onDismiss = { showEndTimePicker = false },
                        onTimeSelected = { hour, minute ->
                            val time = String.format(Locale.getDefault(), "%02d:%02d", hour, minute)
                            viewModel.updateProductiveHours(u.productiveStartTime, time)
                            showEndTimePicker = false
                        },
                        title = "End Time"
                    )
                }
            }

            // About
            SettingsSection(title = "About") {
                SettingsRow(
                    title = "Refer a Friend",
                    subtitle = "Unlock more groups",
                    onClick = { navController.navigate(ShRoutes.REFERRALS) },
                    icon = Icons.Default.CardGiftcard
                )
                SettingsRow(
                    title = "App Version",
                    subtitle = "1.0.1 (Beta)",
                    icon = Icons.Default.Info
                )
                SettingsRow(
                    title = "Privacy Policy",
                    onClick = { navController.navigate(ShRoutes.PRIVACY) },
                    icon = Icons.Default.PrivacyTip
                )
                SettingsRow(
                    title = "Terms of Service",
                    onClick = { navController.navigate(ShRoutes.TERMS) },
                    icon = Icons.Default.Description
                )
            }

            // Account
            SettingsSection(title = "Account") {
                SettingsRow(
                    title = "Log Out",
                    titleColor = ShVermillion,
                    onClick = { showLogoutDialog = true },
                    icon = Icons.AutoMirrored.Filled.ExitToApp,
                    iconColor = ShVermillion
                )
                SettingsRow(
                    title = "Delete Account",
                    titleColor = ShError,
                    onClick = { showDeleteDialog = true },
                    icon = Icons.Default.DeleteForever,
                    iconColor = ShError
                )
            }
        }

        Spacer(Modifier.height(48.dp))
    }

    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            title = { Text("Log Out?") },
            text = { Text("Are you sure you want to log out of Shoshin?") },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.logout {
                        navController.navigate(ShRoutes.AUTH) {
                            popUpTo(0) { inclusive = true }
                        }
                    }
                }) {
                    Text("Log Out", color = ShVermillion)
                }
            },
            dismissButton = {
                TextButton(onClick = { showLogoutDialog = false }) {
                    Text("Cancel", color = ShFog)
                }
            },
            containerColor = ShSurface,
            shape = RoundedCornerShape(20.dp)
        )
    }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Delete Account?", color = ShError) },
            text = { Text("This action is permanent and will delete all your streaks, photos, and group memberships.") },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.deleteAccount {
                        navController.navigate(ShRoutes.AUTH) {
                            popUpTo(0) { inclusive = true }
                        }
                    }
                }) {
                    Text("Delete Forever", color = ShError)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Cancel", color = ShFog)
                }
            },
            containerColor = ShSurface,
            shape = RoundedCornerShape(20.dp)
        )
    }
}

@Composable
fun SettingsSection(title: String, content: @Composable ColumnScope.() -> Unit) {
    Column(modifier = Modifier.padding(horizontal = 24.dp, vertical = 16.dp)) {
        Kicker(title, color = ShFog)
        Spacer(Modifier.height(12.dp))
        ShoshinCard(modifier = Modifier.fillMaxWidth()) {
            Column(content = content)
        }
    }
}

@Composable
fun SettingsRow(
    title: String,
    subtitle: String? = null,
    icon: ImageVector,
    iconColor: Color = ShInk,
    titleColor: Color = ShInk,
    onClick: (() -> Unit)? = null
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(enabled = onClick != null) { onClick?.invoke() }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, contentDescription = null, tint = iconColor, modifier = Modifier.size(22.dp))
        Spacer(Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(title, style = MaterialTheme.typography.bodyLarge, color = titleColor, fontWeight = FontWeight.Medium)
            if (subtitle != null) {
                Text(subtitle, style = MaterialTheme.typography.bodySmall, color = ShFog)
            }
        }
        if (onClick != null) {
            Icon(Icons.Default.ChevronRight, contentDescription = null, tint = ShLine2)
        }
    }
}

@Composable
fun SettingsSwitchRow(
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    icon: ImageVector
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, contentDescription = null, tint = ShInk, modifier = Modifier.size(22.dp))
        Spacer(Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(title, style = MaterialTheme.typography.bodyLarge, color = ShInk, fontWeight = FontWeight.Medium)
            Text(subtitle, style = MaterialTheme.typography.bodySmall, color = ShFog)
        }
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = ShVermillion,
                uncheckedThumbColor = ShPaper2,
                uncheckedTrackColor = ShLine
            )
        )
    }
}
