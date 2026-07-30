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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.Shoshin.app.R
import com.Shoshin.app.navigation.ShRoutes
import com.Shoshin.app.ui.components.*
import com.Shoshin.app.ui.theme.*

@Composable
fun DataPrivacyScreen(
    navController: NavController
) {
    var showDeleteConfirm by remember { mutableStateOf(false) }
    val scrollState = rememberScrollState()
    val context = LocalContext.current

    ShoshinTheme(type = ShoshinThemeType.DYNAMIC) {
        Box(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(ShNight)
                    .statusBarsPadding()
            ) {
                // App Bar
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(painterResource(R.drawable.ic_arrow_left), contentDescription = "Back", tint = Color.White)
                    }
                    Spacer(Modifier.width(8.dp))
                    Text("Privacy & data", style = ShTitleStyle.copy(fontSize = 28.sp, color = Color.White))
                }

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(scrollState)
                        .padding(horizontal = 24.dp)
                ) {
                    Text(
                        text = "Shoshin keeps your photos on-device and never sells your data. You control what stays and what leaves.",
                        style = ShBodyStyle.copy(fontSize = 15.sp, color = ShNightMuted, lineHeight = 22.sp),
                        modifier = Modifier.padding(bottom = 32.dp)
                    )

                    Kicker("YOUR DATA", color = ShNightMuted)
                    Spacer(Modifier.height(16.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(24.dp))
                            .background(ShNight2)
                    ) {
                        Column(modifier = Modifier.padding(vertical = 8.dp)) {
                            PrivacyRowDark(
                                icon = R.drawable.ic_download, 
                                title = "Export my data", 
                                sub = "Mornings, checkpoints, streaks as a file",
                                onClick = {
                                    val exportText = "Shoshin Data Export\nGenerated: ${java.util.Date()}\n\n" +
                                                     "Practice consistency and beginner's mind every morning."
                                    val intent = android.content.Intent(android.content.Intent.ACTION_SEND).apply {
                                        type = "text/plain"
                                        putExtra(android.content.Intent.EXTRA_SUBJECT, "Shoshin Data Export")
                                        putExtra(android.content.Intent.EXTRA_TEXT, exportText)
                                    }
                                    context.startActivity(android.content.Intent.createChooser(intent, "Export Data"))
                                }
                            )
                            HorizontalDivider(color = ShNightLine, modifier = Modifier.padding(horizontal = 16.dp))
                            PrivacyRowDark(
                                icon = R.drawable.ic_shield, 
                                title = "Privacy policy", 
                                sub = "How Shoshin handles your information",
                                onClick = { navController.navigate(ShRoutes.PRIVACY) }
                            )
                        }
                    }

                    Spacer(Modifier.height(32.dp))

                    Kicker("DANGER ZONE", color = ShNightMuted)
                    Spacer(Modifier.height(16.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(24.dp))
                            .background(ShNight2)
                    ) {
                        Column(modifier = Modifier.padding(vertical = 8.dp)) {
                            PrivacyRowDark(
                                icon = R.drawable.ic_trash,
                                title = "Delete account", 
                                sub = "Permanently erase all your data",
                                danger = true,
                                onClick = { showDeleteConfirm = true }
                            )
                        }
                    }

                    Spacer(Modifier.height(48.dp))
                }
            }

            if (showDeleteConfirm) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.7f))
                        .clickable { showDeleteConfirm = false },
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .width(320.dp)
                            .clip(RoundedCornerShape(28.dp))
                            .background(ShNight2)
                            .clickable(enabled = false) { }
                    ) {
                        Column(
                            modifier = Modifier.padding(32.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(56.dp)
                                    .clip(CircleShape)
                                    .background(ShNight3),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(painterResource(R.drawable.ic_trash), null, tint = ShVermillionLight, modifier = Modifier.size(24.dp))
                            }
                            
                            Spacer(Modifier.height(24.dp))
                            
                            Text("Delete your account?", style = ShTitleStyle.copy(fontSize = 20.sp, color = Color.White), textAlign = TextAlign.Center)
                            
                            Spacer(Modifier.height(12.dp))
                            
                            Text(
                                "148 mornings kept, your streaks, and your circle memberships will be gone. This can't be undone.",
                                style = ShBodyStyle.copy(fontSize = 14.sp, color = ShNightMuted, lineHeight = 20.sp),
                                textAlign = TextAlign.Center
                            )
                            
                            Spacer(Modifier.height(32.dp))
                            
                            ShoshinButton(
                                onClick = { 
                                    showDeleteConfirm = false
                                    navController.navigate(ShRoutes.SPLASH) {
                                        popUpTo(ShRoutes.MAIN) { inclusive = true }
                                    }
                                },
                                variant = ShButtonVariant.Accent,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text("Delete permanently", fontWeight = FontWeight.Bold)
                            }
                            
                            Spacer(Modifier.height(12.dp))
                            
                            ShoshinButton(
                                onClick = { showDeleteConfirm = false },
                                variant = ShButtonVariant.Dark,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text("Keep my account", color = Color.White, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun PrivacyRowDark(
    icon: Int,
    title: String,
    sub: String,
    danger: Boolean = false,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(20.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Box(
            modifier = Modifier.size(40.dp).clip(RoundedCornerShape(10.dp)).background(ShNight3),
            contentAlignment = Alignment.Center
        ) {
            Icon(painterResource(icon), null, modifier = Modifier.size(20.dp), tint = if (danger) ShVermillionLight else Color.White)
        }
        Column(modifier = Modifier.weight(1f)) {
            Text(title, style = ShH2Style.copy(fontSize = 16.sp, color = if (danger) ShVermillionLight else Color.White))
            Text(sub, style = ShLabelStyle.copy(fontSize = 13.sp, color = ShNightMuted))
        }
        Icon(painterResource(R.drawable.ic_arrow_right), null, modifier = Modifier.size(18.dp), tint = ShNightLine)
    }
}
