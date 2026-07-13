package com.example.shoshinapp.ui.screens

import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.shoshinapp.R
import com.example.shoshinapp.ui.components.*
import com.example.shoshinapp.ui.theme.*

@Composable
fun PermissionsScreen(onContinue: () -> Unit) {
    val context = LocalContext.current
    var cameraGranted  by remember { mutableStateOf(false) }
    var notifsGranted  by remember { mutableStateOf(false) }
    var locationGranted by remember { mutableStateOf(false) }

    val cameraLauncher = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { cameraGranted = it }
    val notifLauncher  = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { notifsGranted = it }
    val locationLauncher = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { locationGranted = it }

    Column(modifier = Modifier.fillMaxSize().background(ShPaper)) {
        Column(modifier = Modifier.weight(1f).padding(horizontal = 24.dp, vertical = 32.dp)) {
            // ... (Title/Subtitle)
            Kicker(stringResource(R.string.perms_kicker), color = ShVermillion)
            Spacer(Modifier.height(10.dp))
            Text(stringResource(R.string.perms_title), fontSize = 34.sp, fontWeight = FontWeight.SemiBold, fontFamily = CormorantFamily, color = ShInk)
            Spacer(Modifier.height(10.dp))
            Text(stringResource(R.string.perms_subtitle), fontSize = 15.sp, color = ShFog, fontFamily = DmSansFamily, lineHeight = 22.sp)
            Spacer(Modifier.height(28.dp))

            PermissionRow(
                icon = R.drawable.ic_camera, 
                title = stringResource(R.string.perms_camera_title), 
                sub = stringResource(R.string.perms_camera_body),
                granted = cameraGranted,
                onAllow = { cameraLauncher.launch(Manifest.permission.CAMERA) }
            )
            
            Spacer(modifier = Modifier.height(14.dp))
            
            PermissionRow(
                icon = R.drawable.ic_map_pin, 
                title = "Location", 
                sub = "Helps us give you geographic insights into your habits.",
                granted = locationGranted,
                onAllow = { locationLauncher.launch(Manifest.permission.ACCESS_COARSE_LOCATION) }
            )

            Spacer(modifier = Modifier.height(14.dp))
            
            PermissionRow(
                icon = R.drawable.ic_bell, 
                title = stringResource(R.string.perms_notif_title), 
                sub = stringResource(R.string.perms_notif_body),
                granted = notifsGranted,
                onAllow = { notifLauncher.launch(Manifest.permission.POST_NOTIFICATIONS) }
            )

            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
                // ... (Exact Alarms)
                var alarmGranted by remember { mutableStateOf(false) }
                val alarmManager = context.getSystemService(android.content.Context.ALARM_SERVICE) as android.app.AlarmManager
                
                // Update granted state periodically or via Lifecycle
                LaunchedEffect(Unit) {
                    alarmGranted = alarmManager.canScheduleExactAlarms()
                }

                Spacer(modifier = Modifier.height(14.dp))
                PermissionRow(
                    icon = R.drawable.ic_clock, 
                    title = "Exact Alarms", 
                    sub = "Required for precise morning wake-up times.",
                    granted = alarmGranted,
                    onAllow = {
                        val intent = android.content.Intent(
                            android.provider.Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM,
                            android.net.Uri.parse("package:${context.packageName}")
                        )
                        context.startActivity(intent)
                    }
                )
            }

            Spacer(Modifier.height(16.dp))
            Box(modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(14.dp)).background(ShInk.copy(alpha = 0.04f)).padding(14.dp)) {
                Text("Your data stays on your device. No ads, no tracking, no sharing.", fontSize = 13.sp, color = ShFog, fontFamily = DmSansFamily, lineHeight = 20.sp)
            }
        }

        Column(modifier = Modifier.padding(24.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            ShoshinButton(
                variant = if (cameraGranted && notifsGranted && locationGranted) ShButtonVariant.Accent else ShButtonVariant.Ghost,
                onClick = onContinue
            ) {
                Text(if (cameraGranted && notifsGranted && locationGranted) stringResource(R.string.perms_continue) else stringResource(R.string.perms_later))
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
    onAllow: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(ShSurface)
            .border(1.5.dp, if (granted) ShMatcha else ShLine, RoundedCornerShape(20.dp))
            .padding(18.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .background(ShPaper2, RoundedCornerShape(12.dp)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(id = icon),
                contentDescription = null,
                tint = ShInk,
                modifier = Modifier.size(24.dp)
            )
        }
        Column(modifier = Modifier.weight(1f)) {
            Text(title, fontSize = 16.sp, fontWeight = FontWeight.Bold, fontFamily = DmSansFamily, color = ShInk)
            Text(sub, fontSize = 13.sp, color = ShFog, fontFamily = DmSansFamily, lineHeight = 18.sp)
        }
        if (granted) {
            Box(modifier = Modifier.clip(RoundedCornerShape(999.dp)).background(ShMatcha.copy(alpha = 0.12f)).padding(horizontal = 10.dp, vertical = 5.dp)) {
                Text("Granted", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = ShMatcha, fontFamily = DmSansFamily)
            }
        } else {
            Button(
                onClick = onAllow, 
                colors = ButtonDefaults.buttonColors(containerColor = ShInk), 
                shape = RoundedCornerShape(10.dp),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 0.dp),
                modifier = Modifier.height(36.dp)
            ) {
                Text("Allow", fontSize = 13.sp, fontFamily = DmSansFamily, fontWeight = FontWeight.Bold)
            }
        }
    }
}
