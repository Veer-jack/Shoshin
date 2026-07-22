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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.shoshinapp.R
import com.example.shoshinapp.ui.components.*
import com.example.shoshinapp.ui.theme.*
import com.example.shoshinapp.viewmodel.ShareViewModel

private data class ShareCardData(val kicker: String, val big: String, val sub: String, val bigIsNumber: Boolean)

@Composable
fun ShareScreen(
    navController: NavController,
    viewModel: ShareViewModel,
    streak: Int,
    habitName: String,
    startDate: Long
) {
    val description by viewModel.customDescription.collectAsState()
    val selectedStyle by viewModel.selectedStyle.collectAsState()
    val scrollState = rememberScrollState()

    LaunchedEffect(streak, habitName, startDate, description, selectedStyle) {
        viewModel.generatePreview(streak, habitName, startDate)
    }

    val card = when (selectedStyle) {
        "ring" -> ShareCardData("CONSISTENCY", "86%", "Top 8% of early risers this month.", false)
        "badge" -> ShareCardData("EARNED", "Early riser", "Five mornings before 6 AM, in a row.", false)
        else -> ShareCardData("MORNINGS KEPT", streak.toString(), "Fourteen days of beginning again.", true)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(ShPaper)
            .verticalScroll(scrollState)
            .padding(horizontal = 24.dp, vertical = 16.dp)
    ) {
        // App Bar
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { navController.popBackStack() }) {
                Icon(painterResource(R.drawable.ic_arrow_left), contentDescription = "Back", tint = ShInk)
            }
            Spacer(Modifier.width(14.dp))
            Text("Share your practice", style = ShTitleStyle.copy(fontSize = 26.sp), fontWeight = FontWeight.SemiBold, color = ShInk)
        }

        Spacer(Modifier.height(22.dp))

        // Preview card
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 260.dp)
                .clip(RoundedCornerShape(24.dp))
                .background(ShInk)
                .padding(30.dp),
            contentAlignment = Alignment.Center
        ) {
            // decorative enso ring
            Box(modifier = Modifier.align(Alignment.BottomEnd).offset(x = 40.dp, y = 40.dp).size(180.dp)) {
                EnsoRing(color = ShVermillion.copy(alpha = 0.35f), strokeWidth = 7.dp)
            }

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Box(modifier = Modifier.size(36.dp).clip(CircleShape).background(ShPaper), contentAlignment = Alignment.Center) {
                    Text("初", style = ShLogoStyle.copy(fontSize = 16.sp), color = ShInk)
                }
                Spacer(Modifier.height(16.dp))
                Kicker(card.kicker, color = ShVermillion)
                Spacer(Modifier.height(10.dp))
                Text(
                    card.big,
                    style = ShDisplayStyle.copy(fontSize = if (card.bigIsNumber) 72.sp else 42.sp, fontWeight = FontWeight.SemiBold),
                    color = ShPaper
                )
                Spacer(Modifier.height(14.dp))
                Text(card.sub, fontFamily = DmSansFamily, fontSize = 14.sp, color = ShPaper.copy(alpha = 0.65f))
                Spacer(Modifier.height(18.dp))
                Text("初心", fontFamily = NotoSerifJpFamily, fontSize = 14.sp, color = ShVermillion.copy(alpha = 0.7f), letterSpacing = 4.sp)
            }
        }

        Spacer(Modifier.height(24.dp))

        // Style Picker
        Kicker("Choose a card", modifier = Modifier.padding(start = 4.dp))
        Spacer(Modifier.height(10.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            val styles = listOf(
                Triple("streak", R.drawable.ic_flame, "Streak"),
                Triple("ring", R.drawable.ic_pulse, "Consistency"),
                Triple("badge", R.drawable.ic_trophy, "Badge")
            )

            styles.forEach { (id, icon, label) ->
                val isSelected = selectedStyle == id
                Button(
                    onClick = { viewModel.setStyle(id) },
                    modifier = Modifier.weight(1f).height(72.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isSelected) ShInk else ShSurface,
                        contentColor = if (isSelected) ShPaper else ShFog
                    ),
                    contentPadding = PaddingValues(8.dp),
                    elevation = null,
                    border = if (isSelected) null else androidx.compose.foundation.BorderStroke(1.5.dp, ShLine2)
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(painterResource(icon), null, modifier = Modifier.size(20.dp))
                        Spacer(Modifier.height(6.dp))
                        Text(label, fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = if (isSelected) ShPaper else ShInk)
                    }
                }
            }
        }

        Spacer(Modifier.height(24.dp))

        // Share targets
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            val targets = listOf(
                Triple("Instagram", R.drawable.ic_share, Unit),
                Triple("WhatsApp", R.drawable.ic_mail, Unit),
                Triple("More", R.drawable.ic_grid, Unit)
            )

            targets.forEach { (label, icon, _) ->
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(horizontal = 12.dp).clickable { viewModel.shareToPlatform(label, streak) }
                ) {
                    Box(
                        modifier = Modifier
                            .size(54.dp)
                            .clip(CircleShape)
                            .background(ShPaper2),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(painterResource(icon), null, modifier = Modifier.size(22.dp), tint = ShInk)
                    }
                    Spacer(Modifier.height(8.dp))
                    Text(label, style = ShLabelStyle.copy(fontSize = 11.5.sp), color = ShInk)
                }
            }
        }

        Spacer(Modifier.height(24.dp))

        ShoshinButton(
            onClick = { viewModel.shareToPlatform("Generic", streak) },
            variant = ShButtonVariant.Accent,
            modifier = Modifier.fillMaxWidth().height(56.dp)
        ) {
            Icon(painterResource(R.drawable.ic_share), null, modifier = Modifier.size(20.dp), tint = Color.White)
            Spacer(Modifier.width(10.dp))
            Text("Share this card", fontWeight = FontWeight.Bold)
        }

        Spacer(Modifier.height(48.dp))
    }
}
