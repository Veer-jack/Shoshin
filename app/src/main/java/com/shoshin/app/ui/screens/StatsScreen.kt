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
                .background(MaterialTheme.colorScheme.background)
                .statusBarsPadding()
        ) {
            // App Bar
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(painterResource(R.drawable.ic_arrow_left), contentDescription = "Back", tint = MaterialTheme.colorScheme.onBackground)
                }
                Spacer(Modifier.width(8.dp))
                Text("Your stats", style = ShTitleStyle.copy(fontSize = 32.sp), color = MaterialTheme.colorScheme.onBackground)
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(scrollState)
                    .padding(horizontal = 24.dp)
            ) {
                // 2x2 Grid
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(14.dp)) {
                    StatCard(
                        modifier = Modifier.weight(1f),
                        value = (allTimeStats?.totalActivations ?: 0).toString(),
                        label = "TOTAL MORNINGS",
                        icon = R.drawable.ic_sun
                    )
                    StatCard(
                        modifier = Modifier.weight(1f),
                        value = (allTimeStats?.bestStreak ?: 0).toString(),
                        label = "BEST STREAK",
                        icon = R.drawable.ic_droplet
                    )
                }
                Spacer(Modifier.height(14.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(14.dp)) {
                    StatCard(
                        modifier = Modifier.weight(1f),
                        value = allTimeStats?.onTimeRate ?: "0%",
                        label = "ON-TIME RATE",
                        icon = R.drawable.ic_clock
                    )
                    StatCard(
                        modifier = Modifier.weight(1f),
                        value = (allTimeStats?.totalCheckpoints ?: 0).toString(),
                        label = "CHECKPOINTS KEPT",
                        icon = R.drawable.ic_check
                    )
                }

                Spacer(Modifier.height(24.dp))

                // Horizontal Summary Row — these aren't tracked yet (no wake-time,
                // bridge-duration, or photo-proof data source exists), so show an
                // honest "not yet available" placeholder rather than fake numbers.
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(24.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                        .padding(24.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceAround,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        SummaryStat(value = "—", label = "AVG WAKE")
                        Box(Modifier.width(1.dp).height(32.dp).background(MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)))
                        SummaryStat(value = "—", label = "AVG BRIDGE")
                        Box(Modifier.width(1.dp).height(32.dp).background(MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)))
                        SummaryStat(value = "—", label = "PHOTO PROOF")
                    }
                }

                Spacer(Modifier.height(32.dp))

                Kicker("TIME SPENT BY PATH", color = MaterialTheme.colorScheme.onSurfaceVariant)
                Spacer(Modifier.height(16.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(24.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        "Not yet available",
                        style = ShLabelStyle,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Spacer(Modifier.height(48.dp))
            }
        }
    }
}

@Composable
private fun SummaryStat(value: String, unit: String? = null, label: String, color: Color? = null) {
    val finalColor = color ?: MaterialTheme.colorScheme.onSurface
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Row(verticalAlignment = Alignment.Bottom) {
            Text(value, style = ShNumeralStyle.copy(fontSize = 32.sp, color = finalColor))
            if (unit != null) {
                Text(unit, style = ShNumeralStyle.copy(fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurfaceVariant), modifier = Modifier.padding(bottom = 4.dp, start = 2.dp))
            }
        }
        Text(label, style = ShKickerStyle.copy(fontSize = 9.sp, color = MaterialTheme.colorScheme.onSurfaceVariant))
    }
}

@Composable
private fun StatCard(
    modifier: Modifier = Modifier,
    value: String,
    label: String,
    icon: Int
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(24.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .padding(24.dp)
    ) {
        Column {
            Icon(painterResource(icon), null, modifier = Modifier.size(20.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(Modifier.height(16.dp))
            Text(value, style = ShNumeralStyle.copy(fontSize = 32.sp, color = MaterialTheme.colorScheme.onSurface))
            Text(label, style = ShKickerStyle.copy(fontSize = 9.sp, color = MaterialTheme.colorScheme.onSurfaceVariant))
        }
    }
}

