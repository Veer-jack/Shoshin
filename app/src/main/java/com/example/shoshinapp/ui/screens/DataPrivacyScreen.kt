package com.example.shoshinapp.ui.screens

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
import com.example.shoshinapp.R
import com.example.shoshinapp.navigation.ShRoutes
import com.example.shoshinapp.ui.components.*
import com.example.shoshinapp.ui.theme.*

@Composable
fun DataPrivacyScreen(
    navController: NavController
) {
    var showDeleteConfirm by remember { mutableStateOf(false) }
    var showExportSuccess by remember { mutableStateOf(false) }
    val scrollState = rememberScrollState()
    val context = LocalContext.current

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(ShPaper)
                .verticalScroll(scrollState)
                .padding(horizontal = 24.dp)
        ) {
            // App Bar
            Row(
                modifier = Modifier.fillMaxWidth().padding(top = 16.dp, bottom = 24.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                IconButton(onClick = { navController.popBackStack() }, modifier = Modifier.size(24.dp)) {
                    Icon(painterResource(R.drawable.ic_arrow_left), contentDescription = "Back")
                }
                Text("Privacy & data", style = ShTitleStyle.copy(fontSize = 26.sp), fontWeight = FontWeight.SemiBold)
            }

            Text(
                text = "Shoshin keeps your photos on-device and never sells your data. You control what stays and what leaves.",
                style = ShBodyStyle,
                modifier = Modifier.padding(bottom = 24.dp)
            )

            Kicker("Your data", modifier = Modifier.padding(start = 4.dp, bottom = 10.dp))
            ShoshinCard(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(horizontal = 18.dp, vertical = 4.dp)) {
                    PrivacyRow(
                        icon = R.drawable.ic_download, 
                        title = "Export my data", 
                        sub = "Mornings, checkpoints, streaks as a file",
                        onClick = {
                            // Simple export logic for now
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
                    HorizontalDivider(color = ShLine)
                    PrivacyRow(
                        icon = R.drawable.ic_shield, 
                        title = "Privacy policy", 
                        sub = "How Shoshin handles your information",
                        onClick = { navController.navigate(ShRoutes.PRIVACY) }
                    )
                }
            }

            Spacer(Modifier.height(22.dp))

            Kicker("Danger zone", modifier = Modifier.padding(start = 4.dp, bottom = 10.dp))
            ShoshinCard(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(horizontal = 18.dp, vertical = 4.dp)) {
                    PrivacyRow(
                        icon = R.drawable.ic_trash,
                        title = "Delete account", 
                        sub = "Permanently erase all your data",
                        danger = true,
                        onClick = { showDeleteConfirm = true }
                    )
                }
            }

            Spacer(Modifier.height(40.dp))
        }

        // Delete Confirmation Modal
        if (showDeleteConfirm) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.5f))
                    .clickable { showDeleteConfirm = false },
                contentAlignment = Alignment.Center
            ) {
                ShoshinCard(
                    modifier = Modifier
                        .width(320.dp)
                        .clickable(enabled = false) { }
                ) {
                    Column(
                        modifier = Modifier.padding(26.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(
                            modifier = Modifier
                                .size(52.dp)
                                .background(ShVermillion.copy(alpha = 0.1f), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(painterResource(R.drawable.ic_trash), null, modifier = Modifier.size(24.dp), tint = ShVermillion)
                        }
                        
                        Spacer(Modifier.height(16.dp))
                        
                        Text("Delete your account?", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = ShInk)
                        
                        Spacer(Modifier.height(8.dp))
                        
                        Text(
                            "148 mornings kept, your streaks, and your circle memberships will be gone. This can't be undone.",
                            style = ShBodyStyle.copy(fontSize = 13.5.sp),
                            textAlign = TextAlign.Center,
                            color = ShFog
                        )
                        
                        Spacer(Modifier.height(22.dp))
                        
                        ShoshinButton(
                            onClick = { 
                                showDeleteConfirm = false
                                // Handle delete logic
                                navController.navigate(ShRoutes.SPLASH) {
                                    popUpTo(ShRoutes.MAIN) { inclusive = true }
                                }
                            },
                            variant = ShButtonVariant.Accent,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Delete permanently")
                        }
                        
                        Spacer(Modifier.height(10.dp))
                        
                        ShoshinButton(
                            onClick = { showDeleteConfirm = false },
                            variant = ShButtonVariant.Ghost,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Keep my account", color = ShFog)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun PrivacyRow(
    icon: Int,
    title: String,
    sub: String,
    danger: Boolean = false,
    onClick: () -> Unit = {}
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Icon(painterResource(icon), null, modifier = Modifier.size(20.dp), tint = if (danger) ShVermillion else ShInk)
        Column(modifier = Modifier.weight(1f)) {
            Text(title, fontSize = 15.5.sp, fontWeight = FontWeight.Medium, color = if (danger) ShVermillion else ShInk)
            Text(sub, fontSize = 12.5.sp, color = ShFog)
        }
        Icon(painterResource(R.drawable.ic_arrow_right), null, modifier = Modifier.size(17.dp), tint = ShFog2)
    }
}
