package com.Shoshin.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
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
import com.Shoshin.app.navigation.ShRoutes
import com.Shoshin.app.R
import com.Shoshin.app.ui.components.*
import com.Shoshin.app.ui.theme.*
import com.Shoshin.app.viewmodel.StreakViewModel
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun StreakDetailsScreen(
    navController: NavController,
    viewModel: StreakViewModel
) {
    val user by viewModel.user.collectAsState()
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
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(painterResource(R.drawable.ic_arrow_left), contentDescription = "Back", tint = Color.White)
                }
                Spacer(Modifier.width(8.dp))
                Text("Streak details", style = ShTitleStyle.copy(fontSize = 28.sp, color = Color.White))
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(scrollState)
                    .padding(horizontal = 24.dp)
            ) {
                // Hero
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(24.dp))
                        .background(ShNight2)
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Enso(size = 130, color = ShVermillionLight.copy(alpha = 0.12f), strokeWidth = 5f, modifier = Modifier.align(Alignment.TopEnd).offset(x = 40.dp, y = (-20).dp))
                    
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(painterResource(R.drawable.ic_flame), null, modifier = Modifier.size(32.dp), tint = ShVermillionLight)
                        Spacer(Modifier.height(16.dp))
                        Text(
                            text = (user?.currentStreak ?: 14).toString(),
                            style = ShNumeralStyle.copy(fontSize = 72.sp, color = Color.White)
                        )
                        Kicker("CURRENT STREAK", color = ShNightMuted)
                    }
                }

                Spacer(Modifier.height(16.dp))

                // Grid 2x1
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    Box(modifier = Modifier.weight(1f).clip(RoundedCornerShape(24.dp)).background(ShNight2).padding(20.dp)) {
                        Column {
                            Icon(painterResource(R.drawable.ic_trophy), null, modifier = Modifier.size(20.dp), tint = ShNightMuted)
                            Spacer(Modifier.height(16.dp))
                            Text((user?.bestStreak ?: 31).toString(), style = ShNumeralStyle.copy(fontSize = 28.sp, color = Color.White))
                            Kicker("BEST STREAK EVER", color = ShNightMuted)
                        }
                    }
                    Box(modifier = Modifier.weight(1f).clip(RoundedCornerShape(24.dp)).background(ShNight2).padding(20.dp)) {
                        Column {
                            Icon(painterResource(R.drawable.ic_calendar), null, modifier = Modifier.size(20.dp), tint = ShNightMuted)
                            Spacer(Modifier.height(16.dp))
                            Text((user?.totalActivations ?: 148).toString(), style = ShNumeralStyle.copy(fontSize = 28.sp, color = Color.White))
                            Kicker("TOTAL MORNINGS", color = ShNightMuted)
                        }
                    }
                }

                Spacer(Modifier.height(24.dp))

                // Chart Card
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(24.dp))
                        .background(ShNight2)
                        .padding(24.dp)
                ) {
                    Column {
                        Text("Streak by month", style = ShH2Style.copy(fontSize = 17.sp, color = Color.White))
                        Spacer(Modifier.height(32.dp))
                        
                        Row(modifier = Modifier.fillMaxWidth().height(80.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.Bottom) {
                            val months = listOf("Apr", "May", "Jun", "Jul")
                            val heights = listOf(0.6f, 1.0f, 0.5f, 0.8f)
                            val colors = listOf(ShMatchaDark, ShMatchaDark, ShMatchaDark, ShVermillionLight)
                            
                            months.forEachIndexed { i, month ->
                                Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.weight(1f)) {
                                    Box(modifier = Modifier.fillMaxWidth(0.7f).fillMaxHeight(heights[i]).clip(RoundedCornerShape(6.dp)).background(colors[i]))
                                    Spacer(Modifier.height(8.dp))
                                    Text(month, style = ShLabelStyle.copy(fontSize = 11.sp, color = ShNightMuted))
                                }
                            }
                        }
                    }
                }

                Spacer(Modifier.height(16.dp))

                // Info Box
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(ShNight2)
                        .padding(16.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(painterResource(R.drawable.ic_info), null, tint = ShNightMuted, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(12.dp))
                        Text(
                            "Streaks reset on a miss, but your total mornings kept never disappears.",
                            style = ShLabelStyle.copy(fontSize = 13.sp, color = ShNightMuted, lineHeight = 18.sp)
                        )
                    }
                }

                Spacer(Modifier.height(32.dp))
            }
        }
    }
}
