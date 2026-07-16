package com.example.shoshinapp.ui.screens

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
import com.example.shoshinapp.navigation.ShRoutes
import com.example.shoshinapp.R
import com.example.shoshinapp.ui.components.*
import com.example.shoshinapp.ui.theme.*
import com.example.shoshinapp.viewmodel.StreakViewModel
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun StreakDetailsScreen(
    navController: NavController,
    viewModel: StreakViewModel
) {
    val user by viewModel.user.collectAsState()
    val scrollState = rememberScrollState()

    user?.let { u ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .verticalScroll(scrollState)
                .padding(horizontal = 24.dp)
        ) {
            // App Bar
            Row(
                modifier = Modifier.fillMaxWidth().padding(top = 16.dp, bottom = 22.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                IconButton(onClick = { navController.popBackStack() }, modifier = Modifier.size(24.dp)) {
                    Icon(painterResource(R.drawable.ic_arrow_left), contentDescription = "Back", tint = ShInk)
                }
                Text("Streak details", style = ShTitleStyle.copy(fontSize = 26.sp), fontWeight = FontWeight.SemiBold)
            }

            // Hero - Ink Card
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(24.dp))
                    .background(ShInk)
                    .padding(26.dp),
                contentAlignment = Alignment.Center
            ) {
                // Enso motif
                Enso(
                    size = 130,
                    color = ShVermillion.copy(alpha = 0.3f),
                    strokeWidth = 5f,
                    modifier = Modifier.align(Alignment.TopStart).offset(x = (-30).dp, y = (-30).dp)
                )

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(painterResource(R.drawable.ic_flame), null, modifier = Modifier.size(30.dp), tint = ShVermillion)
                    Spacer(Modifier.height(10.dp))
                    Text(
                        text = u.currentStreak.toString(),
                        fontSize = 56.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = DmSansFamily,
                        color = ShPaper,
                        lineHeight = 56.sp
                    )
                    Text(
                        text = "Current streak".uppercase(),
                        style = ShKickerStyle,
                        color = ShPaper.copy(alpha = 0.6f),
                        modifier = Modifier.padding(top = 6.dp)
                    )
                }
            }

            Spacer(Modifier.height(18.dp))

            // Stat Grid 2x2
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(14.dp)) {
                ShoshinCard(modifier = Modifier.weight(1f)) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Icon(painterResource(R.drawable.ic_trophy), null, modifier = Modifier.size(20.dp), tint = ShFog)
                        Spacer(Modifier.height(12.dp))
                        Text(u.bestStreak.toString(), fontSize = 24.sp, fontWeight = FontWeight.Bold, fontFamily = DmSansFamily, color = ShInk)
                        Kicker("Best streak ever", modifier = Modifier.padding(top = 3.dp))
                    }
                }
                ShoshinCard(modifier = Modifier.weight(1f)) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Icon(painterResource(R.drawable.ic_calendar), null, modifier = Modifier.size(20.dp), tint = ShFog)
                        Spacer(Modifier.height(12.dp))
                        Text(u.totalActivations.toString(), fontSize = 24.sp, fontWeight = FontWeight.Bold, fontFamily = DmSansFamily, color = ShInk)
                        Kicker("Total mornings", modifier = Modifier.padding(top = 3.dp))
                    }
                }
            }

            Spacer(Modifier.height(22.dp))

            // Monthly Trend
            ShoshinCard(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(22.dp)) {
                    Text("Streak by month", style = ShTitleStyle.copy(fontSize = 18.sp), modifier = Modifier.padding(bottom = 18.dp))
                    
                    val trendData = emptyList<Pair<String, Float>>()
                    
                    if (trendData.isEmpty()) {
                        Text("No monthly data yet. Keep your streak alive to see trends.", style = ShBodyStyle, color = ShFog)
                    } else {
                        Row(
                            modifier = Modifier.fillMaxWidth().height(90.dp),
                            verticalAlignment = Alignment.Bottom,
                            horizontalArrangement = Arrangement.spacedBy(14.dp)
                        ) {
                            trendData.forEachIndexed { i, (month, value) ->
                                Column(
                                    modifier = Modifier.weight(1f).fillMaxHeight(),
                                    verticalArrangement = Arrangement.Bottom,
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .fillMaxHeight(value)
                                            .clip(RoundedCornerShape(6.dp))
                                            .background(if (i == trendData.lastIndex) ShVermillion else ShMatcha)
                                    )
                                    Spacer(Modifier.height(8.dp))
                                    Text(month, style = ShLabelStyle.copy(fontSize = 11.sp, fontWeight = FontWeight.SemiBold))
                                }
                            }
                        }
                    }
                }
            }

            Spacer(Modifier.height(18.dp))

            // Info Note
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(ShPaper2)
                    .padding(horizontal = 18.dp, vertical = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Icon(painterResource(R.drawable.ic_info), null, modifier = Modifier.size(19.dp), tint = ShFog)
                Text(
                    text = "Streaks reset on a miss, but your total mornings kept never disappears.",
                    style = ShBodyStyle.copy(fontSize = 13.sp, lineHeight = 1.5.sp),
                    color = ShFog
                )
            }

            Spacer(Modifier.height(24.dp))

            ShoshinButton(
                onClick = { 
                    navController.navigate(ShRoutes.streakShare(u.currentStreak, "Morning Routine", u.streakStartDate))
                },
                variant = ShButtonVariant.Accent,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(painterResource(R.drawable.ic_share), null, modifier = Modifier.size(18.dp), tint = Color.White)
                Spacer(Modifier.width(8.dp))
                Text("Share Your Progress")
            }
            
            Spacer(Modifier.height(48.dp))
        }
    }
}
