package com.Shoshin.app.ui.screens

import androidx.compose.foundation.background
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
import com.Shoshin.app.R
import com.Shoshin.app.ui.components.*
import com.Shoshin.app.ui.theme.*
import com.Shoshin.app.viewmodel.StatsViewModel

@Composable
fun StatsScreen(
    navController: NavController,
    viewModel: StatsViewModel
) {
    val allTimeStats by viewModel.allTimeStats.collectAsState()
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
                Text("Your stats", style = ShTitleStyle.copy(fontSize = 28.sp, color = Color.White))
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(scrollState)
                    .padding(horizontal = 24.dp)
            ) {
                // 2x2 Grid
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(14.dp)) {
                    StatCardDark(
                        modifier = Modifier.weight(1f),
                        value = (allTimeStats?.totalActivations ?: 148).toString(),
                        label = "TOTAL MORNINGS",
                        icon = R.drawable.ic_sun
                    )
                    StatCardDark(
                        modifier = Modifier.weight(1f),
                        value = (allTimeStats?.bestStreak ?: 31).toString(),
                        label = "BEST STREAK",
                        icon = R.drawable.ic_droplet
                    )
                }
                Spacer(Modifier.height(14.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(14.dp)) {
                    StatCardDark(
                        modifier = Modifier.weight(1f),
                        value = allTimeStats?.onTimeRate ?: "91%",
                        label = "ON-TIME RATE",
                        icon = R.drawable.ic_clock
                    )
                    StatCardDark(
                        modifier = Modifier.weight(1f),
                        value = (allTimeStats?.totalCheckpoints ?: 740).toString(),
                        label = "CHECKPOINTS KEPT",
                        icon = R.drawable.ic_check
                    )
                }

                Spacer(Modifier.height(24.dp))

                // Horizontal Summary Row
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(24.dp))
                        .background(ShNight2)
                        .padding(24.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceAround,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        SummaryStatStat(value = "05:34", label = "AVG WAKE")
                        Box(Modifier.width(1.dp).height(32.dp).background(ShNightLine))
                        SummaryStatStat(value = "21", unit = "min", label = "AVG BRIDGE")
                        Box(Modifier.width(1.dp).height(32.dp).background(ShNightLine))
                        SummaryStatStat(value = "98", unit = "%", label = "PHOTO PROOF", color = ShMatchaDark)
                    }
                }

                Spacer(Modifier.height(32.dp))

                Kicker("TIME SPENT BY PATH", color = ShNightMuted)
                Spacer(Modifier.height(16.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(24.dp))
                        .background(ShNight2)
                        .padding(24.dp)
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(24.dp)) {
                        PathProgressRow(label = "Morning Walk", percentage = 0.62f, color = ShVermillionLight)
                        PathProgressRow(label = "Deep Study", percentage = 0.28f, color = ShMatchaDark)
                        PathProgressRow(label = "Strength", percentage = 0.10f, color = ShMatchaDark)
                    }
                }

                Spacer(Modifier.height(48.dp))
            }
        }
    }
}

@Composable
private fun StatCardDark(
    modifier: Modifier = Modifier,
    value: String,
    label: String,
    icon: Int
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(24.dp))
            .background(ShNight2)
            .padding(24.dp)
    ) {
        Column {
            Icon(painterResource(icon), null, modifier = Modifier.size(20.dp), tint = ShNightMuted)
            Spacer(Modifier.height(16.dp))
            Text(value, style = ShNumeralStyle.copy(fontSize = 32.sp, color = Color.White))
            Text(label, style = ShKickerStyle.copy(fontSize = 9.sp, color = ShNightMuted))
        }
    }
}

@Composable
private fun SummaryStatStat(value: String, unit: String? = null, label: String, color: Color = Color.White) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Row(verticalAlignment = Alignment.Bottom) {
            Text(value, style = ShNumeralStyle.copy(fontSize = 32.sp, color = color))
            if (unit != null) {
                Text(unit, style = ShNumeralStyle.copy(fontSize = 14.sp, color = ShNightMuted), modifier = Modifier.padding(bottom = 4.dp, start = 2.dp))
            }
        }
        Text(label, style = ShKickerStyle.copy(fontSize = 9.sp, color = ShNightMuted))
    }
}

@Composable
private fun PathProgressRow(label: String, percentage: Float, color: Color) {
    Column {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(label, style = ShH2Style.copy(fontSize = 15.sp, color = Color.White))
            Text("${(percentage * 100).toInt()}%", style = ShNumeralStyle.copy(fontSize = 14.sp, color = Color.White))
        }
        Spacer(Modifier.height(10.dp))
        Box(modifier = Modifier.fillMaxWidth().height(6.dp).clip(CircleShape).background(ShNight3)) {
            Box(modifier = Modifier.fillMaxWidth(percentage).fillMaxHeight().clip(CircleShape).background(color))
        }
    }
}
