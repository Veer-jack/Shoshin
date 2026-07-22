package com.shoshin.app.ui.screens

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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.shoshin.app.R
import com.shoshin.app.ui.components.*
import com.shoshin.app.ui.theme.*

@Composable
fun PaywallScreen(navController: NavController) {
    var selectedPlan by remember { mutableStateOf("year") }
    val scrollState = rememberScrollState()
    
    val feats = listOf(
        Triple(R.drawable.ic_bolt, "Unlimited paths", "Build every routine you practice"),
        Triple(R.drawable.ic_camera, "Photo & GPS proof", "Verify each checkpoint your way"),
        Triple(R.drawable.ic_shield, "71-Day Discipline", "The advanced identity challenge"),
        Triple(R.drawable.ic_groups, "Accountability circles", "Rise together with your pod"),
        Triple(R.drawable.ic_calendar, "Full history", "Every morning, kept forever")
    )

    ShoshinTheme(darkSurface = true) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .verticalScroll(scrollState)
                .padding(horizontal = 24.dp)
        ) {
            // Close Button
            Row(
                modifier = Modifier.fillMaxWidth().padding(top = 16.dp),
                horizontalArrangement = Arrangement.End
            ) {
                IconButton(
                    onClick = { navController.popBackStack() },
                    modifier = Modifier.size(34.dp).clip(CircleShape).background(Color.White.copy(alpha = 0.08f))
                ) {
                    Icon(painterResource(R.drawable.ic_close), contentDescription = "Close", tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(20.dp))
                }
            }

            // Header
            Column(
                modifier = Modifier.fillMaxWidth().padding(top = 8.dp, bottom = 32.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                ShoshinLogoMark()
                Spacer(Modifier.height(14.dp))
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Shoshin", style = ShDisplayStyle.copy(fontSize = 32.sp), color = MaterialTheme.colorScheme.onBackground)
                    ShoshinPill(label = "PRO", variant = ShPillVariant.Accent)
                }
                Text("Go deeper into the practice.", style = ShBodyStyle, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.padding(top = 8.dp))
            }

            // Features
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                feats.forEach { (icon, title, desc) ->
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(14.dp)) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(RoundedCornerShape(11.dp))
                                .background(Color.White.copy(alpha = 0.06f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(painterResource(icon), null, modifier = Modifier.size(20.dp), tint = ShVermillion)
                        }
                        Column {
                            Text(title, style = ShButtonStyle.copy(fontSize = 15.5.sp), color = MaterialTheme.colorScheme.onBackground)
                            Text(desc, style = ShLabelStyle.copy(fontSize = 12.5.sp), color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }
            }

            Spacer(Modifier.weight(1f))
            Spacer(Modifier.height(40.dp))

            // Plans
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                PlanCard(
                    id = "month",
                    label = "Monthly",
                    price = "₹299",
                    per = "/mo",
                    isSelected = selectedPlan == "month",
                    onClick = { selectedPlan = "month" },
                    modifier = Modifier.weight(1f)
                )
                PlanCard(
                    id = "year",
                    label = "Yearly",
                    price = "₹1,999",
                    per = "/yr",
                    isSelected = selectedPlan == "year",
                    onClick = { selectedPlan = "year" },
                    badge = "SAVE 44%",
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(Modifier.height(16.dp))

            ShoshinButton(
                onClick = { /* Start Pro */ },
                variant = ShButtonVariant.Accent,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Begin Pro · 7 days free")
            }

            Row(
                modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "Restore",
                    modifier = Modifier.clickable { },
                    style = ShButtonStyle.copy(fontSize = 13.sp),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(Modifier.width(18.dp))
                Text(
                    "Maybe later",
                    modifier = Modifier.clickable { navController.popBackStack() },
                    style = ShButtonStyle.copy(fontSize = 13.sp),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            
            Spacer(Modifier.height(24.dp))
        }
    }
}

@Composable
private fun PlanCard(
    id: String,
    label: String,
    price: String,
    per: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    badge: String? = null,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(if (isSelected) ShVermillion.copy(alpha = 0.12f) else Color.White.copy(alpha = 0.04f))
            .border(
                width = if (isSelected) 2.dp else 1.dp,
                color = if (isSelected) ShVermillion else MaterialTheme.colorScheme.outline,
                shape = RoundedCornerShape(16.dp)
            )
            .clickable { onClick() }
            .padding(16.dp, 14.dp)
    ) {
        if (badge != null) {
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .offset(y = (-22).dp) // Adjust based on parent clipping
            ) {
                ShoshinPill(label = badge, variant = ShPillVariant.Accent)
            }
        }
        
        Column {
            Text(label, style = ShLabelStyle.copy(fontSize = 12.sp, fontWeight = FontWeight.SemiBold), color = MaterialTheme.colorScheme.onSurfaceVariant)
            Row(verticalAlignment = Alignment.Bottom, modifier = Modifier.padding(top = 6.dp)) {
                Text(price, style = ShNumeralStyle.copy(fontSize = 22.sp), color = MaterialTheme.colorScheme.onBackground)
                Text(per, style = ShLabelStyle.copy(fontSize = 12.sp), color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}
