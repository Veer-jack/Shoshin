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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.Shoshin.app.R
import com.Shoshin.app.ui.components.*
import com.Shoshin.app.ui.theme.*
import com.Shoshin.app.viewmodel.GroupStatsViewModel

@Composable
fun GroupStatsScreen(
    navController: NavController,
    groupId: String,
    viewModel: GroupStatsViewModel
) {
    val stats by viewModel.stats.collectAsState()

    LaunchedEffect(groupId) {
        viewModel.loadGroupData(groupId)
    }

    ShoshinTheme(type = ShoshinThemeType.DYNAMIC) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .statusBarsPadding()
        ) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(painterResource(R.drawable.ic_arrow_left), contentDescription = "Back", tint = MaterialTheme.colorScheme.onBackground)
                }
                Spacer(Modifier.width(8.dp))
                Text("Circle stats", style = ShTitleStyle.copy(fontSize = 28.sp), color = MaterialTheme.colorScheme.onBackground)
            }

            if (stats == null) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = ShVermillion)
                }
            } else {
                val s = stats!!
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = 24.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(32.dp))
                            .background(ShNight2)
                            .padding(24.dp)
                    ) {
                        Column {
                            Kicker("THIS CIRCLE", color = ShNightMuted)
                            Spacer(Modifier.height(16.dp))
                            Row(verticalAlignment = Alignment.Bottom) {
                                Text("${s.totalMemberCount}", style = ShNumeralStyle.copy(fontSize = 48.sp, color = androidx.compose.ui.graphics.Color.White))
                                Spacer(Modifier.width(8.dp))
                                Text("members · ${s.groupAgeInDays}d old", style = ShTitleStyle.copy(fontSize = 20.sp, color = ShNightMuted))
                            }
                        }
                    }

                    Spacer(Modifier.height(24.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        StatTile(
                            modifier = Modifier.weight(1f),
                            value = String.format("%.1f", s.averageStreak),
                            label = "AVG STREAK"
                        )
                        StatTile(
                            modifier = Modifier.weight(1f),
                            value = "${s.activeMembersThisWeek}",
                            label = "ACTIVE THIS WEEK"
                        )
                        StatTile(
                            modifier = Modifier.weight(1f),
                            value = "${s.totalCheckpointsThisMonth}",
                            label = "DAYS THIS MONTH"
                        )
                    }

                    Spacer(Modifier.height(32.dp))
                    Kicker("TOP PERFORMERS", color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Spacer(Modifier.height(12.dp))

                    ShoshinCard(modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.padding(vertical = 8.dp)) {
                            if (s.topPerformers.isEmpty()) {
                                Text(
                                    "No completions yet this circle.",
                                    style = ShBodyStyle,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.padding(20.dp)
                                )
                            } else {
                                s.topPerformers.forEachIndexed { index, performer ->
                                    Row(
                                        modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 14.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text("#${index + 1}", style = ShLabelStyle, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.width(28.dp))
                                        Box(
                                            modifier = Modifier.size(36.dp).clip(CircleShape).background(MaterialTheme.colorScheme.surfaceVariant),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(
                                                performer.userName.firstOrNull()?.toString()?.uppercase() ?: "U",
                                                style = ShH2Style.copy(fontSize = 15.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
                                            )
                                        }
                                        Spacer(Modifier.width(12.dp))
                                        Text(
                                            performer.userName,
                                            style = ShH2Style.copy(fontSize = 15.sp),
                                            color = MaterialTheme.colorScheme.onSurface,
                                            modifier = Modifier.weight(1f)
                                        )
                                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                            Icon(painterResource(R.drawable.ic_flame), null, modifier = Modifier.size(12.dp), tint = ShVermillion)
                                            Text("${performer.currentStreak}", style = ShNumeralStyle.copy(fontSize = 14.sp), color = MaterialTheme.colorScheme.onSurface)
                                        }
                                    }
                                    if (index < s.topPerformers.lastIndex) {
                                        HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f), modifier = Modifier.padding(horizontal = 20.dp))
                                    }
                                }
                            }
                        }
                    }

                    Spacer(Modifier.height(48.dp))
                }
            }
        }
    }
}

@Composable
private fun StatTile(modifier: Modifier = Modifier, value: String, label: String) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(20.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .padding(vertical = 18.dp, horizontal = 12.dp)
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
            Text(value, style = ShNumeralStyle.copy(fontSize = 22.sp), color = MaterialTheme.colorScheme.onSurface)
            Spacer(Modifier.height(4.dp))
            Text(label, style = ShKickerStyle.copy(fontSize = 9.sp, letterSpacing = 1.sp), color = MaterialTheme.colorScheme.onSurfaceVariant, textAlign = androidx.compose.ui.text.style.TextAlign.Center)
        }
    }
}
