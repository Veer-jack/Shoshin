package com.example.shoshinapp.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.shoshinapp.R
import com.example.shoshinapp.ui.components.*
import com.example.shoshinapp.ui.theme.*
import com.example.shoshinapp.viewmodel.ReferralViewModel

@Composable
fun ReferralScreen(
    navController: NavController,
    viewModel: ReferralViewModel
) {
    val limits by viewModel.limits.collectAsState()
    val context = LocalContext.current
    val scrollState = rememberScrollState()

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
            Text("Invite the circle", style = ShTitleStyle.copy(fontSize = 26.sp), fontWeight = FontWeight.SemiBold)
        }

        // Hero Card - Ink
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(24.dp))
                .background(ShInk)
                .padding(26.dp),
            contentAlignment = Alignment.Center
        ) {
            Enso(
                size = 130,
                color = ShVermillion.copy(alpha = 0.3f),
                strokeWidth = 5f,
                modifier = Modifier.align(Alignment.TopEnd).offset(x = 30.dp, y = (-30).dp)
            )

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.08f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(painterResource(R.drawable.ic_gift), null, modifier = Modifier.size(26.dp), tint = ShVermillion)
                }
                Spacer(Modifier.height(16.dp))
                Text("A month of Pro, on us", fontSize = 24.sp, color = ShPaper, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
                Spacer(Modifier.height(8.dp))
                Text(
                    "Invite someone who needs a reason to rise. When they keep their first 7 mornings, you both earn 30 days of Shoshin Pro.",
                    fontSize = 14.sp,
                    color = ShPaper.copy(alpha = 0.65f),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.widthIn(max = 280.dp)
                )
            }
        }

        Spacer(Modifier.height(18.dp))

        // Your Code
        Kicker("Your code", modifier = Modifier.padding(start = 4.dp, bottom = 10.dp))
        ShoshinCard(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 18.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = limits?.referralCode ?: "ARJUN-M14K",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = DmSansFamily,
                    letterSpacing = 1.3.sp,
                    color = ShInk
                )
                Button(
                    onClick = {
                        val clipboard = context.getSystemService(android.content.Context.CLIPBOARD_SERVICE) as android.content.ClipboardManager
                        val clip = android.content.ClipData.newPlainText("Invite Code", limits?.referralCode)
                        clipboard.setPrimaryClip(clip)
                    },
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = ShPaper2, contentColor = ShInk),
                    contentPadding = PaddingValues(horizontal = 14.dp, vertical = 9.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        Icon(painterResource(R.drawable.ic_check), null, modifier = Modifier.size(15.dp), tint = ShMatcha)
                        Text("Copy", fontSize = 12.5.sp, fontWeight = FontWeight.SemiBold)
                    }
                }
            }
        }

        Spacer(Modifier.height(14.dp))

        // Progress
        ShoshinCard(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(20.dp)) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Invited so far", style = ShTitleStyle.copy(fontSize = 18.sp))
                    Text(
                        text = buildAnnotatedString {
                            append((limits?.totalReferrals ?: 2).toString())
                            withStyle(style = SpanStyle(color = ShFog2, fontWeight = FontWeight.Medium)) {
                                append("/5")
                            }
                        },
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = DmSansFamily
                    )
                }
                Spacer(Modifier.height(12.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    val referrals = limits?.totalReferrals ?: 2
                    repeat(5) { i ->
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(8.dp)
                                .clip(CircleShape)
                                .background(if (i < referrals) ShMatcha else ShSand)
                        )
                    }
                }
                Spacer(Modifier.height(12.dp))
                Text(
                    "3 more invites keeps their first week for another 30 days of Pro.",
                    style = ShLabelStyle.copy(fontSize = 12.5.sp),
                    color = ShFog
                )
            }
        }

        Spacer(Modifier.height(22.dp))

        ShoshinButton(
            onClick = { /* Share link */ },
            variant = ShButtonVariant.Accent,
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(painterResource(R.drawable.ic_share), null, modifier = Modifier.size(18.dp), tint = Color.White)
            Spacer(Modifier.width(8.dp))
            Text("Share invite link")
        }
        
        Spacer(Modifier.height(12.dp))
        
        ShoshinButton(
            onClick = { /* Email invite */ },
            variant = ShButtonVariant.Ghost,
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(painterResource(R.drawable.ic_mail), null, modifier = Modifier.size(18.dp), tint = ShInk)
            Spacer(Modifier.width(8.dp))
            Text("Invite by email")
        }

        Spacer(Modifier.height(48.dp))
    }
}
