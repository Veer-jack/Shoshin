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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.Shoshin.app.R
import com.Shoshin.app.ui.components.*
import com.Shoshin.app.ui.theme.*

@Composable
fun RoutineEditorScreen(navController: NavController) {
    val scrollState = rememberScrollState()
    var pathName by remember { mutableStateOf("Morning Walk") }
    
    val steps = listOf(
        Pair(R.drawable.ic_brain, "Mind awake"),
        Pair(R.drawable.ic_droplet, "Freshen up"),
        Pair(R.drawable.ic_shirt, "Dressed"),
        Pair(R.drawable.ic_sun, "Out the door"),
        Pair(R.drawable.ic_walk, "Walk begun")
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(ShNight)
            .statusBarsPadding()
    ) {
        // App Bar
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(painterResource(R.drawable.ic_arrow_left), null, tint = Color.White)
                }
                Text("Back", style = ShLabelStyle.copy(color = ShNightMuted, fontWeight = FontWeight.Bold))
            }
            Surface(
                color = ShVermillion.copy(alpha = 0.15f),
                shape = RoundedCornerShape(999.dp)
            ) {
                Text(
                    "Movement",
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                    style = ShLabelStyle.copy(fontSize = 11.sp, fontWeight = FontWeight.Bold, color = ShVermillion)
                )
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(horizontal = 24.dp)
        ) {
            Text("Edit path", style = ShTitleStyle.copy(fontSize = 32.sp, color = Color.White))
            
            Spacer(Modifier.height(24.dp))

            Kicker("PATH NAME", color = ShNightMuted)
            Spacer(Modifier.height(10.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .border(1.2.dp, ShNightLine, RoundedCornerShape(16.dp))
                    .background(ShNight2)
                    .padding(horizontal = 16.dp),
                contentAlignment = Alignment.CenterStart
            ) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Text(pathName, style = ShH2Style.copy(fontSize = 16.sp, color = Color.White))
                    Icon(painterResource(R.drawable.ic_edit), null, modifier = Modifier.size(18.dp), tint = ShNightMuted)
                }
            }

            Spacer(Modifier.height(32.dp))
            Kicker("CHECKPOINTS · 5 MIN APART", color = ShNightMuted)
            Spacer(Modifier.height(14.dp))

            steps.forEach { step ->
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp)
                        .clip(RoundedCornerShape(24.dp))
                        .background(ShNight2)
                        .padding(18.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        // Drag Handle Mock
                        Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
                            Icon(painterResource(R.drawable.ic_arrow_left), null, modifier = Modifier.size(12.dp).rotate(90f), tint = ShNightLine)
                            Icon(painterResource(R.drawable.ic_arrow_left), null, modifier = Modifier.size(12.dp).rotate(270f), tint = ShNightLine)
                        }
                        
                        Spacer(Modifier.width(16.dp))

                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(ShNightText.copy(alpha = 0.05f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(painterResource(step.first), null, modifier = Modifier.size(20.dp), tint = Color.White)
                        }

                        Spacer(Modifier.width(16.dp))
                        Text(step.second, style = ShH2Style.copy(fontSize = 16.sp, color = Color.White), modifier = Modifier.weight(1f))
                        
                        IconButton(onClick = { }) {
                            Icon(painterResource(R.drawable.ic_trash), null, modifier = Modifier.size(18.dp), tint = ShNightMuted)
                        }
                    }
                }
            }

            Spacer(Modifier.height(12.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(64.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .border(1.2.dp, ShNightLine, RoundedCornerShape(20.dp))
                    .clickable { }
                    .padding(horizontal = 18.dp),
                contentAlignment = Alignment.Center
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(painterResource(R.drawable.ic_plus), null, modifier = Modifier.size(16.dp), tint = ShNightMuted)
                    Spacer(Modifier.width(8.dp))
                    Text("Add checkpoint", style = ShLabelStyle.copy(color = ShNightMuted, fontWeight = FontWeight.Bold))
                }
            }

            Spacer(Modifier.height(32.dp))

            ShoshinButton(
                onClick = { /* navController.popBackStack() */ },
                variant = ShButtonVariant.Accent,
                modifier = Modifier.fillMaxWidth(),
                enabled = false
            ) {
                Text("Save path (Coming soon)", fontWeight = FontWeight.Bold)
            }

            Spacer(Modifier.height(48.dp))
        }
    }
}
