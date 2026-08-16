package com.Shoshin.app.ui.screens

import androidx.compose.foundation.Image
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
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.Shoshin.app.R
import com.Shoshin.app.ui.components.*
import com.Shoshin.app.ui.theme.*
import com.Shoshin.app.viewmodel.ShareViewModel

@Composable
fun ShareScreen(
    navController: NavController,
    viewModel: ShareViewModel,
    streak: Int,
    habitName: String,
    startDate: Long,
    consistencyPercent: Int = 0,
    referralCode: String = ""
) {
    val bitmap by viewModel.shareBitmap.collectAsState()
    val selectedStyle by viewModel.selectedStyle.collectAsState()
    val scrollState = rememberScrollState()

    LaunchedEffect(streak, habitName, startDate, consistencyPercent, selectedStyle) {
        viewModel.generatePreview(streak, habitName, startDate, consistencyPercent)
    }

    ShoshinTheme(type = ShoshinThemeType.DYNAMIC) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(ShNight)
                .statusBarsPadding()
        ) {
            // App Bar
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(painterResource(R.drawable.ic_arrow_left), null, tint = Color.White)
                }
                Spacer(Modifier.width(8.dp))
                Text("Share your practice", style = ShTitleStyle.copy(fontSize = 28.sp, color = Color.White))
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(scrollState)
                    .padding(horizontal = 24.dp)
            ) {
                Spacer(Modifier.height(24.dp))

                // Card Preview
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(1f)
                        .clip(RoundedCornerShape(32.dp))
                        .background(ShNight2),
                    contentAlignment = Alignment.Center
                ) {
                    Enso(size = 240, color = ShVermillionLight.copy(alpha = 0.12f), strokeWidth = 10f, modifier = Modifier.align(Alignment.BottomEnd).offset(x = 60.dp, y = 60.dp))
                    
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Box(
                            modifier = Modifier.size(56.dp).clip(RoundedCornerShape(12.dp)).background(ShNightText.copy(alpha = 0.05f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(painterResource(R.drawable.ic_pulse), null, tint = ShVermillionLight, modifier = Modifier.size(24.dp))
                        }
                        Spacer(Modifier.height(16.dp))
                        Kicker("MORNINGS KEPT", color = ShVermillionLight)
                        Spacer(Modifier.height(16.dp))
                        Text(streak.toString(), style = ShTitleStyle.copy(fontSize = 72.sp, color = Color.White))
                        Spacer(Modifier.height(8.dp))
                        Text("Fourteen days of beginning again.", style = ShBodyStyle.copy(color = ShNightMuted), textAlign = TextAlign.Center)
                        Spacer(Modifier.height(16.dp))
                        Text("初心", style = ShKanjiStyle.copy(fontSize = 22.sp, color = ShVermillionLight.copy(alpha = 0.5f)))
                    }
                }

                Spacer(Modifier.height(32.dp))

                Kicker("CHOOSE A CARD", color = ShNightMuted)
                Spacer(Modifier.height(12.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    val styles = listOf("streak" to "Streak", "ring" to "Consistency", "badge" to "Badge")
                    val icons = listOf(R.drawable.ic_droplet, R.drawable.ic_pulse, R.drawable.ic_trophy)
                    
                    styles.forEachIndexed { i, (id, label) ->
                        val isSelected = selectedStyle == id
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(80.dp)
                                .clip(RoundedCornerShape(16.dp))
                                .background(if (isSelected) Color.White else ShNight2)
                                .clickable { viewModel.setStyle(id) }
                                .padding(12.dp)
                        ) {
                            Column {
                                Icon(painterResource(icons[i]), null, modifier = Modifier.size(18.dp), tint = if (isSelected) Color.Black else ShNightMuted)
                                Spacer(Modifier.weight(1f))
                                Text(label, style = ShLabelStyle.copy(fontWeight = FontWeight.Bold, color = if (isSelected) Color.Black else Color.White))
                            }
                        }
                    }
                }

                Spacer(Modifier.height(40.dp))

                // Social Row
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceAround) {
                    SocialIcon(icon = R.drawable.ic_share, label = "Instagram")
                    SocialIcon(icon = R.drawable.ic_mail, label = "WhatsApp")
                    SocialIcon(icon = R.drawable.ic_grid, label = "More")
                }

                Spacer(Modifier.height(40.dp))

                ShoshinButton(
                    onClick = { viewModel.shareToPlatform("Generic", streak, referralCode) },
                    variant = ShButtonVariant.Accent,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(painterResource(R.drawable.ic_share), null, modifier = Modifier.size(20.dp), tint = Color.White)
                    Spacer(Modifier.width(12.dp))
                    Text("Share this card", fontWeight = FontWeight.Bold)
                }

                Spacer(Modifier.height(48.dp))
            }
        }
    }
}

@Composable
private fun SocialIcon(icon: Int, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(modifier = Modifier.size(60.dp).clip(CircleShape).background(ShNight2), contentAlignment = Alignment.Center) {
            Icon(painterResource(icon), null, tint = Color.White, modifier = Modifier.size(24.dp))
        }
        Spacer(Modifier.height(8.dp))
        Text(label, style = ShLabelStyle.copy(fontSize = 11.sp, color = ShNightMuted))
    }
}
