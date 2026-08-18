package com.Shoshin.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.Shoshin.app.R
import com.Shoshin.app.ui.components.*
import com.Shoshin.app.ui.theme.*
import com.Shoshin.app.viewmodel.ReferralViewModel

@Composable
fun ReferralScreen(
    navController: NavController,
    viewModel: ReferralViewModel
) {
    val limits by viewModel.limits.collectAsState()
    val context = LocalContext.current
    val scrollState = rememberScrollState()

    ShoshinTheme(type = ShoshinThemeType.DYNAMIC) {
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
                    .padding(horizontal = 24.dp, vertical = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(painterResource(R.drawable.ic_arrow_left), contentDescription = "Back", tint = Color.White)
                }
                Spacer(Modifier.width(8.dp))
                Text("Invite the circle", style = ShTitleStyle.copy(fontSize = 28.sp, color = Color.White))
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(scrollState)
                    .padding(horizontal = 24.dp)
            ) {
                // Hero Card
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(24.dp))
                        .background(ShNight2)
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Enso(size = 140, color = ShVermillionLight.copy(alpha = 0.12f), strokeWidth = 6f, modifier = Modifier.align(Alignment.TopEnd).offset(x = 40.dp, y = (-20).dp))
                    
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Box(
                            modifier = Modifier.size(56.dp).clip(CircleShape).background(ShNight3),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(painterResource(R.drawable.ic_gift), null, tint = ShVermillionLight, modifier = Modifier.size(24.dp))
                        }
                        Spacer(Modifier.height(24.dp))
                        Text("A month of Pro, on us", style = ShTitleStyle.copy(fontSize = 24.sp, color = Color.White), textAlign = TextAlign.Center)
                        Spacer(Modifier.height(12.dp))
                        Text(
                            "Invite someone who needs a reason to rise. When they keep their first 7 mornings, you both earn 30 days of Shoshin Pro.",
                            style = ShBodyStyle.copy(fontSize = 14.sp, color = ShNightMuted, lineHeight = 20.sp),
                            textAlign = TextAlign.Center
                        )
                    }
                }

                Spacer(Modifier.height(24.dp))

                Kicker("YOUR CODE", color = ShNightMuted)
                Spacer(Modifier.height(12.dp))
                
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(ShNight2)
                        .padding(horizontal = 20.dp, vertical = 20.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = limits?.referralCode?.takeIf { it.isNotBlank() } ?: "···",
                            style = ShNumeralStyle.copy(fontSize = 24.sp, color = Color.White, letterSpacing = 1.sp)
                        )
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            ReferralActionPill(
                                icon = R.drawable.ic_check,
                                iconTint = ShMatchaDark,
                                label = "Copy",
                                onClick = {
                                    val clipboard = context.getSystemService(android.content.Context.CLIPBOARD_SERVICE) as android.content.ClipboardManager
                                    val clip = android.content.ClipData.newPlainText("Invite Code", limits?.referralCode ?: "")
                                    clipboard.setPrimaryClip(clip)
                                }
                            )
                            val referralCode = limits?.referralCode
                            ReferralActionPill(
                                icon = R.drawable.ic_share,
                                iconTint = Color.White,
                                label = "Share",
                                dimmed = referralCode.isNullOrBlank(),
                                onClick = {
                                    if (referralCode.isNullOrBlank()) {
                                        android.widget.Toast.makeText(context, "No invite code", android.widget.Toast.LENGTH_SHORT).show()
                                    } else {
                                        val message = "🤝 Join my circle on Shoshin!\nCode: $referralCode\n\nshoshin://invite?code=$referralCode"
                                        val intent = android.content.Intent(android.content.Intent.ACTION_SEND).apply {
                                            type = "text/plain"
                                            putExtra(android.content.Intent.EXTRA_TEXT, message)
                                        }
                                        context.startActivity(android.content.Intent.createChooser(intent, "Share invite code"))
                                    }
                                }
                            )
                        }
                    }
                }

                Spacer(Modifier.height(16.dp))

                // Progress Card
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(24.dp))
                        .background(ShNight2)
                        .padding(24.dp)
                ) {
                    Column {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Invited so far", style = ShH2Style.copy(fontSize = 17.sp, color = Color.White))
                            Text(
                                text = buildAnnotatedString {
                                    append((limits?.totalReferrals ?: 0).toString())
                                    withStyle(style = SpanStyle(color = ShNightMuted)) { append("/5") }
                                },
                                style = ShNumeralStyle.copy(fontSize = 17.sp, color = Color.White)
                            )
                        }
                        Spacer(Modifier.height(16.dp))
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            val referrals = limits?.totalReferrals ?: 0
                            repeat(5) { i ->
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(8.dp)
                                        .clip(CircleShape)
                                        .background(if (i < referrals) ShMatchaDark else ShNight3)
                                )
                            }
                        }
                        Spacer(Modifier.height(16.dp))
                        Text(
                            "3 more invites keeps their first week for another 30 days of Pro.",
                            style = ShLabelStyle.copy(fontSize = 13.sp, color = ShNightMuted, lineHeight = 18.sp)
                        )
                    }
                }

                Spacer(Modifier.height(32.dp))

                ShoshinButton(
                    onClick = { 
                        val intent = android.content.Intent(android.content.Intent.ACTION_SEND).apply {
                            type = "text/plain"
                            putExtra(android.content.Intent.EXTRA_TEXT, viewModel.getShareMessage())
                        }
                        context.startActivity(android.content.Intent.createChooser(intent, "Share invite link"))
                    },
                    variant = ShButtonVariant.Accent,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(painterResource(R.drawable.ic_share), null, tint = Color.White, modifier = Modifier.size(20.dp))
                    Spacer(Modifier.width(10.dp))
                    Text("Share invite link", fontWeight = FontWeight.Bold)
                }
                
                Spacer(Modifier.height(12.dp))
                
                ShoshinButton(
                    onClick = { 
                        val intent = android.content.Intent(android.content.Intent.ACTION_SENDTO).apply {
                            data = android.net.Uri.parse("mailto:")
                            putExtra(android.content.Intent.EXTRA_SUBJECT, "Join me on Shoshin")
                            putExtra(android.content.Intent.EXTRA_TEXT, viewModel.getShareMessage())
                        }
                        context.startActivity(android.content.Intent.createChooser(intent, "Invite by email"))
                    },
                    variant = ShButtonVariant.Dark,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(painterResource(R.drawable.ic_mail), null, tint = Color.White, modifier = Modifier.size(20.dp))
                    Spacer(Modifier.width(10.dp))
                    Text("Invite by email", color = Color.White, fontWeight = FontWeight.Bold)
                }

                Spacer(Modifier.height(48.dp))
            }
        }
    }
}

@Composable
private fun ReferralActionPill(
    icon: Int,
    iconTint: Color,
    label: String,
    dimmed: Boolean = false,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .alpha(if (dimmed) 0.4f else 1f)
            .background(ShNight3)
            .padding(horizontal = 12.dp, vertical = 8.dp)
            .clickable { onClick() },
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Icon(painterResource(icon), null, tint = iconTint, modifier = Modifier.size(14.dp))
        Text(label, style = ShLabelStyle.copy(fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color.White))
    }
}
