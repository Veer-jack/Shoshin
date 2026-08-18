package com.Shoshin.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
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
import com.Shoshin.app.BuildConfig
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
                    .padding(horizontal = 24.dp, vertical = 16.dp),
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
                    text = "Settings",
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
                    Spacer(Modifier.height(8.dp))

                    // ACCOUNT
                    SettingsSection(title = "ACCOUNT") {
                        SettingsRow(
                            title = userName,
                            subtitle = u.phone ?: u.email,
                            iconRes = R.drawable.ic_user,
                            onClick = { navController.navigate(ShRoutes.EDIT_PROFILE) }
                        )
                        HorizontalDivider(color = MaterialTheme.colorScheme.outline, modifier = Modifier.padding(horizontal = 16.dp))
                        SettingsRow(
                            title = "Invite the circle",
                            subtitle = "Unlock 5 extra spots",
                            iconRes = R.drawable.ic_gift,
                            onClick = { navController.navigate(ShRoutes.REFERRALS) }
                        )
                    }

                    // APP
                    val themeViewModel: ThemeViewModel = viewModel(key = "ShoshinThemeViewModel")
                    val currentMode by themeViewModel.mode.collectAsState()

                    SettingsSection(title = "APP") {
                        SettingsRow(title = "Appearance", iconRes = R.drawable.ic_sun, showChevron = false)
                        ThemeSelectorRow(
                            selectedMode = currentMode,
                            onModeSelected = { themeViewModel.setMode(it) }
                        )
                        HorizontalDivider(color = MaterialTheme.colorScheme.outline, modifier = Modifier.padding(horizontal = 16.dp))
                        SettingsRow(
                            title = "Haptics",
                            iconRes = R.drawable.ic_pulse,
                            trailing = {
                                ShoshinToggle(checked = u.hapticsEnabled, onCheckedChange = { viewModel.updateHaptics(it) })
                            }
                        )
                        HorizontalDivider(color = MaterialTheme.colorScheme.outline, modifier = Modifier.padding(horizontal = 16.dp))
                        SettingsRow(
                            title = "About",
                            value = "v${BuildConfig.VERSION_NAME}",
                            iconRes = R.drawable.ic_info
                        )
                    }

                    // HELP
                    SettingsSection(title = "HELP") {
                        SettingsRow(
                            title = "Help & support",
                            iconRes = R.drawable.ic_help,
                            onClick = { navController.navigate(ShRoutes.SUPPORT) }
                        )
                        HorizontalDivider(color = MaterialTheme.colorScheme.outline, modifier = Modifier.padding(horizontal = 16.dp))
                        SettingsRow(
                            title = "Privacy & data",
                            iconRes = R.drawable.ic_lock,
                            onClick = { navController.navigate(ShRoutes.DATA_PRIVACY) }
                        )
                    }

                    // SIGN OUT
                    Column(modifier = Modifier.padding(horizontal = 24.dp, vertical = 16.dp)) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(24.dp))
                                .background(MaterialTheme.colorScheme.surfaceVariant)
                        ) {
                            SettingsRow(
                                title = "Sign out",
                                iconRes = R.drawable.ic_logout,
                                danger = true,
                                showChevron = false,
                                onClick = { viewModel.logout { navController.navigate(ShRoutes.AUTH) { popUpTo(0) } } }
                            )
                        }
                    }
                }
            }
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
    danger: Boolean = false,
    showChevron: Boolean = true,
    trailing: (@Composable () -> Unit)? = null,
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
            Icon(painterResource(iconRes), null, tint = if (danger) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurface, modifier = Modifier.size(20.dp))
        }
        Spacer(Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(title, style = ShH2Style.copy(fontSize = 16.sp, color = if (danger) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurface))
            if (subtitle != null) {
                Text(subtitle, style = ShLabelStyle.copy(fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant))
            }
        }
        if (value != null) {
            Text(value, style = ShLabelStyle.copy(color = MaterialTheme.colorScheme.onSurfaceVariant))
            Spacer(Modifier.width(8.dp))
        }
        when {
            trailing != null -> trailing()
            showChevron -> Icon(painterResource(R.drawable.ic_arrow_right), null, tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(18.dp))
        }
    }
}
