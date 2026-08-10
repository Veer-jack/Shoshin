package com.Shoshin.app.ui.screens

import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.Shoshin.app.R
import com.Shoshin.app.ui.components.*
import com.Shoshin.app.ui.theme.*

@Composable
fun PermissionsScreen(onContinue: () -> Unit) {
    val context = LocalContext.current
    
    fun hasPerm(p: String) = androidx.core.content.ContextCompat.checkSelfPermission(context, p) == android.content.pm.PackageManager.PERMISSION_GRANTED
    
    var cameraGranted  by remember { mutableStateOf(hasPerm(Manifest.permission.CAMERA)) }
    var notifsGranted  by remember { mutableStateOf(hasPerm(Manifest.permission.POST_NOTIFICATIONS)) }

    val cameraLauncher = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { cameraGranted = it }
    val notifLauncher  = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { notifsGranted = it }

    ShoshinTheme(type = ShoshinThemeType.DYNAMIC) {
        Column(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
            Column(modifier = Modifier.weight(1f).padding(horizontal = 24.dp, vertical = 32.dp)) {
                Kicker(stringResource(R.string.perms_kicker), color = ShVermillion)
                Spacer(Modifier.height(10.dp))
                Text(stringResource(R.string.perms_title), style = ShTitleStyle, color = MaterialTheme.colorScheme.onBackground)
                Spacer(Modifier.height(10.dp))
                Text(stringResource(R.string.perms_subtitle), style = ShBodyStyle, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Spacer(Modifier.height(28.dp))

                PermissionRow(
                    icon = R.drawable.ic_bell, 
                    title = "Notifications", 
                    sub = "One gentle wind-down at night, and the alarm that begins your morning. Nothing more.",
                    granted = notifsGranted,
                    onToggle = { notifLauncher.launch(Manifest.permission.POST_NOTIFICATIONS) }
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                PermissionRow(
                    icon = R.drawable.ic_camera, 
                    title = "Camera", 
                    sub = "Used only when you choose photo proof for a checkpoint. Images never leave your phone.",
                    granted = cameraGranted,
                    onToggle = { cameraLauncher.launch(Manifest.permission.CAMERA) }
                )

                Spacer(Modifier.height(32.dp))
                Box(modifier = Modifier.fillMaxWidth().padding(horizontal = 4.dp)) {
                    Text("No background tracking. No noise. We ask plainly, and you stay in control.", style = ShBodyStyle, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }

            Column(modifier = Modifier.padding(24.dp).navigationBarsPadding(), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(16.dp)) {
                ShoshinButton(
                    variant = ShButtonVariant.Accent,
                    onClick = onContinue,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Continue", fontWeight = FontWeight.Bold)
                }
                
                Text(
                    "Maybe later",
                    style = ShLabelStyle.copy(color = MaterialTheme.colorScheme.onSurfaceVariant, fontWeight = FontWeight.Bold),
                    modifier = Modifier.clickable { onContinue() }
                )
            }
        }
    }
}

@Composable
fun PermissionRow(
    icon: Int,
    title: String,
    sub: String,
    granted: Boolean,
    onToggle: () -> Unit
) {
    Box(
        modifier = Modifier.fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .padding(20.dp)
    ) {
        Row(verticalAlignment = Alignment.Top) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.05f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(id = icon),
                    contentDescription = null,
                    tint = ShMatchaDark.copy(alpha = 0.8f),
                    modifier = Modifier.size(20.dp)
                )
            }
            Spacer(Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(title, style = ShH2Style, color = MaterialTheme.colorScheme.onSurface)
                Spacer(Modifier.height(8.dp))
                Text(sub, style = ShLabelStyle.copy(fontSize = 13.sp), color = MaterialTheme.colorScheme.onSurfaceVariant, lineHeight = 19.sp)
            }
            Spacer(Modifier.width(12.dp))
            Switch(
                checked = granted,
                onCheckedChange = { onToggle() },
                colors = SwitchDefaults.colors(
                    checkedThumbColor = Color.White,
                    checkedTrackColor = ShMatcha,
                    uncheckedThumbColor = Color.White.copy(alpha = 0.4f),
                    uncheckedTrackColor = MaterialTheme.colorScheme.outline
                )
            )
        }
    }
}
