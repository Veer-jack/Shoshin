package com.Shoshin.app.ui.screens

import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.Shoshin.app.R
import com.Shoshin.app.ui.components.*
import com.Shoshin.app.ui.theme.*

@Composable
fun AlarmDeniedScreen(onDismiss: () -> Unit) {
    val context = LocalContext.current

    ShoshinTheme(type = ShoshinThemeType.ALWAYS_DARK) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(ShNight)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(48.dp))

            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(ShNight2),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_bell),
                    contentDescription = null,
                    tint = ShVermillionLight,
                    modifier = Modifier.size(32.dp)
                )
            }

            Spacer(Modifier.height(32.dp))

            Kicker("ALARMS ARE OFF", color = ShVermillionLight)
            
            Spacer(Modifier.height(12.dp))

            Text(
                "We can't wake\nyou yet",
                style = ShTitleStyle.copy(fontSize = 36.sp, color = Color.White),
                lineHeight = 42.sp
            )

            Spacer(Modifier.height(16.dp))

            Text(
                "Shoshin needs alarm & notification permission to reach you at dawn. Without it, your morning won't begin.",
                style = ShBodyStyle,
                color = ShNightMuted,
                lineHeight = 22.sp
            )

            Spacer(Modifier.height(48.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(24.dp))
                    .background(ShNight2)
            ) {
                Column(modifier = Modifier.padding(vertical = 12.dp)) {
                    PermissionStepRowDark(
                        number = "1",
                        title = "Open Settings",
                        subtitle = "App Info › Shoshin"
                    )
                    HorizontalDivider(color = ShNightLine, modifier = Modifier.padding(start = 64.dp, end = 24.dp))
                    PermissionStepRowDark(
                        number = "2",
                        title = "Allow Notifications",
                        subtitle = "Turn on, then enable Critical Alerts"
                    )
                    HorizontalDivider(color = ShNightLine, modifier = Modifier.padding(start = 64.dp, end = 24.dp))
                    PermissionStepRowDark(
                        number = "3",
                        title = "Allow Alarms & Timers",
                        subtitle = "So we can sound at 5:30 AM"
                    )
                }
            }

            Spacer(Modifier.weight(1f))

            ShoshinButton(
                onClick = {
                    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                        data = Uri.fromParts("package", context.packageName, null)
                    }
                    context.startActivity(intent)
                },
                variant = ShButtonVariant.Accent,
                modifier = Modifier.fillMaxWidth(),
                trailingIcon = { Icon(painterResource(R.drawable.ic_arrow_right), null, modifier = Modifier.size(18.dp)) }
            ) {
                Text("Open Settings", fontWeight = FontWeight.Bold)
            }

            Spacer(Modifier.height(24.dp))
        }
    }
}

@Composable
private fun PermissionStepRowDark(number: String, title: String, subtitle: String) {
    Row(
        modifier = Modifier.padding(horizontal = 24.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier.size(36.dp).clip(CircleShape).background(Color.White),
            contentAlignment = Alignment.Center
        ) {
            Text(number, color = Color.Black, style = ShLabelStyle, fontWeight = FontWeight.Bold)
        }
        Spacer(Modifier.width(16.dp))
        Column {
            Text(title, style = ShH2Style.copy(fontSize = 16.sp, color = Color.White))
            Text(subtitle, style = ShLabelStyle, color = ShNightMuted)
        }
    }
}
