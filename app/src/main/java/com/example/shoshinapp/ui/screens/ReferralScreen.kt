package com.example.shoshinapp.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
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
    ) {
        // App Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { navController.popBackStack() }) {
                Icon(painterResource(R.drawable.ic_arrow_left), contentDescription = "Back")
            }
            Spacer(Modifier.width(16.dp))
            Text("Referrals", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
        }

        Column(modifier = Modifier.padding(horizontal = 24.dp)) {
            
            // Section 1: Your Code
            Kicker("YOUR REFERRAL CODE")
            Spacer(Modifier.height(12.dp))
            ShoshinCard(modifier = Modifier.fillMaxWidth()) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, ShLine, RoundedCornerShape(12.dp))
                            .background(ShSand, RoundedCornerShape(12.dp))
                            .padding(vertical = 16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = limits?.referralCode ?: "LOADING...",
                            fontSize = 32.sp,
                            fontWeight = FontWeight.Bold,
                            color = ShInk,
                            letterSpacing = 2.sp
                        )
                    }
                    
                    Spacer(Modifier.height(16.dp))
                    
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        ShoshinButton(
                            onClick = {
                                val clipboard = context.getSystemService(android.content.Context.CLIPBOARD_SERVICE) as android.content.ClipboardManager
                                val clip = android.content.ClipData.newPlainText("Referral Code", limits?.referralCode)
                                clipboard.setPrimaryClip(clip)
                            },
                            variant = ShButtonVariant.Ghost,
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Copy Code")
                        }
                        
                        ShoshinButton(
                            onClick = { /* Share intent */ },
                            variant = ShButtonVariant.Accent,
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Share Code")
                        }
                    }
                }
            }

            Spacer(Modifier.height(32.dp))

            // Section 2: How it works
            Kicker("HOW IT WORKS")
            Spacer(Modifier.height(12.dp))
            ShoshinCard(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(20.dp)) {
                    HowItWorksItem(number = "1", text = "Share your code with friends")
                    HowItWorksItem(number = "2", text = "Friend signs up with your code")
                    HowItWorksItem(number = "3", text = "You BOTH unlock instantly:\n+5 groups to join\n+5 members per group")
                }
            }

            Spacer(Modifier.height(32.dp))

            // Section 3: Your Current Access
            Kicker("YOUR CURRENT ACCESS")
            Spacer(Modifier.height(12.dp))
            ShoshinCard(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(20.dp)) {
                    AccessItem(label = "Groups you can join", value = "${limits?.groupsJoinLimit ?: 5}")
                    AccessItem(label = "Members per group", value = "${limits?.groupMemberLimit ?: 5}")
                    AccessItem(label = "Total referrals", value = "${limits?.totalReferrals ?: 0}")
                }
            }

            Spacer(Modifier.height(48.dp))
        }
    }
}

@Composable
private fun HowItWorksItem(number: String, text: String) {
    Row(modifier = Modifier.padding(vertical = 8.dp)) {
        Text(
            text = "$number.",
            style = ShBodyStyle,
            fontWeight = FontWeight.Bold,
            color = ShVermillion,
            modifier = Modifier.width(24.dp)
        )
        Text(text = text, style = ShBodyStyle, color = ShInk)
    }
}

@Composable
private fun AccessItem(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, style = ShBodyStyle, color = ShFog)
        Text(text = value, style = ShBodyStyle, fontWeight = FontWeight.Bold, color = ShInk)
    }
}
