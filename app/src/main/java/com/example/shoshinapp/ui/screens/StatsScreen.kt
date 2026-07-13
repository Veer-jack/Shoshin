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
import com.example.shoshinapp.R
import com.example.shoshinapp.ui.components.*
import com.example.shoshinapp.ui.theme.*
import com.example.shoshinapp.viewmodel.StatsViewModel

@Composable
fun StatsScreen(
    navController: NavController,
    viewModel: StatsViewModel
) {
    val allTimeStats by viewModel.allTimeStats.collectAsState()
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
            modifier = Modifier.fillMaxWidth().padding(top = 16.dp, bottom = 22.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            IconButton(onClick = { navController.popBackStack() }, modifier = Modifier.size(24.dp)) {
                Icon(painterResource(R.drawable.ic_arrow_left), contentDescription = "Back")
            }
            Text("Your stats", style = ShTitleStyle.copy(fontSize = 26.sp), fontWeight = FontWeight.SemiBold)
        }

        // 2x2 Grid
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(14.dp)) {
            StatCard(
                modifier = Modifier.weight(1f),
                value = (allTimeStats?.totalActivations ?: 148).toString(),
                label = "Total mornings",
                icon = R.drawable.ic_sun
            )
            StatCard(
                modifier = Modifier.weight(1f),
                value = (allTimeStats?.bestStreak ?: 31).toString(),
                label = "Best streak",
                icon = R.drawable.ic_flame
            )
        }
        Spacer(Modifier.height(14.dp))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(14.dp)) {
            StatCard(
                modifier = Modifier.weight(1f),
                value = "91%",
                label = "On-time rate",
                icon = R.drawable.ic_clock
            )
            StatCard(
                modifier = Modifier.weight(1f),
                value = (allTimeStats?.totalCheckpoints ?: 740).toString(),
                label = "Checkpoints kept",
                icon = R.drawable.ic_check
            )
        }

        Spacer(Modifier.height(22.dp))

        // Avg Times Row
        ShoshinCard(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier.padding(22.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                ShoshinStat(value = "05:34", label = "Avg wake")
                Box(modifier = Modifier.width(1.dp).height(30.dp).background(ShLine))
                ShoshinStat(value = "21", unit = "min", label = "Avg bridge")
                Box(modifier = Modifier.width(1.dp).height(30.dp).background(ShLine))
                ShoshinStat(value = "98", unit = "%", label = "Photo proof", color = ShMatcha)
            }
        }

        Spacer(Modifier.height(18.dp))

        // Time spent by path
        Kicker("Time spent by path", modifier = Modifier.padding(start = 4.dp, bottom = 12.dp))
        ShoshinCard(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(22.dp)) {
                val paths = listOf(
                    Triple("Morning Walk", 62, ShVermillion),
                    Triple("Deep Study", 28, ShMatcha),
                    Triple("Strength", 10, ShMatcha)
                )
                
                paths.forEachIndexed { i, (name, pct, color) ->
                    Column(modifier = Modifier.padding(bottom = if (i < paths.lastIndex) 16.dp else 0.dp)) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text(name, fontSize = 14.sp, fontWeight = FontWeight.Medium, color = ShInk)
                            Text("$pct%", fontSize = 13.sp, fontWeight = FontWeight.Bold, fontFamily = DmSansFamily, color = ShInk)
                        }
                        Spacer(Modifier.height(8.dp))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(8.dp)
                                .clip(RoundedCornerShape(4.dp))
                                .background(ShSand)
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth(pct / 100f)
                                    .fillMaxHeight()
                                    .background(color)
                            )
                        }
                    }
                }
            }
        }

        Spacer(Modifier.height(40.dp))
    }
}

@Composable
private fun StatCard(
    modifier: Modifier = Modifier,
    value: String,
    label: String,
    icon: Int
) {
    ShoshinCard(modifier = modifier) {
        Column(modifier = Modifier.padding(18.dp)) {
            Icon(painterResource(icon), null, modifier = Modifier.size(20.dp), tint = ShFog)
            Spacer(Modifier.height(12.dp))
            Text(value, fontSize = 26.sp, fontWeight = FontWeight.Bold, fontFamily = DmSansFamily, color = ShInk)
            Kicker(label, modifier = Modifier.padding(top = 3.dp), color = ShFog)
        }
    }
}
