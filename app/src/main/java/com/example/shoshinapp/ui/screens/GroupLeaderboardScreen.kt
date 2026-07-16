package com.example.shoshinapp.ui.screens

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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.shoshinapp.R
import com.example.shoshinapp.ui.components.*
import com.example.shoshinapp.ui.theme.*

@Composable
fun GroupLeaderboardScreen(
    navController: NavController,
    groupId: String
) {
    val scrollState = rememberScrollState()
    var selectedTimeframe by remember { mutableStateOf(0) }

    val leaderboard = emptyList<LeaderboardEntry>() // Removed SH_LEADERBOARD dummy data

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
                Icon(painterResource(R.drawable.ic_arrow_left), contentDescription = "Back")
            }
            Text("Dawn Circle leaderboard", style = ShTitleStyle.copy(fontSize = 24.sp), fontWeight = FontWeight.SemiBold)
        }

        // Timeframe Tabs
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            ShoshinPill(
                label = "This week",
                variant = if (selectedTimeframe == 0) ShPillVariant.Ink else ShPillVariant.Outline,
                modifier = Modifier.clickable { selectedTimeframe = 0 }
            )
            ShoshinPill(
                label = "All time",
                variant = if (selectedTimeframe == 1) ShPillVariant.Ink else ShPillVariant.Outline,
                modifier = Modifier.clickable { selectedTimeframe = 1 }
            )
        }

        Spacer(Modifier.height(32.dp))

        // Podium Top 3
        if (leaderboard.isNotEmpty()) {
            Podium(leaderboard.take(3))
        }

        Spacer(Modifier.height(24.dp))

        // Full List
        ShoshinCard(modifier = Modifier.fillMaxWidth().weight(1f)) {
            if (leaderboard.isEmpty()) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Leaderboard is quiet. Invite someone to begin!", style = ShLabelStyle, color = ShFog, textAlign = androidx.compose.ui.text.style.TextAlign.Center, modifier = Modifier.padding(24.dp))
                }
            } else {
                Column(modifier = Modifier.padding(horizontal = 18.dp, vertical = 4.dp)) {
                    leaderboard.forEachIndexed { i, entry ->
                        LeaderboardRow(entry)
                        if (i < leaderboard.lastIndex) HorizontalDivider(color = ShLine)
                    }
                }
            }
        }
        
        Spacer(Modifier.height(40.dp))
    }
}

@Composable
private fun Podium(top3: List<LeaderboardEntry>) {
    // Top 3 sorted for podium: [2nd, 1st, 3rd]
    if (top3.size < 3) return
    val podiumOrder = listOf(top3[1], top3[0], top3[2])

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.Bottom
    ) {
        podiumOrder.forEach { entry ->
            val isFirst = entry.rank == 1
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(horizontal = 7.dp)
            ) {
                if (isFirst) {
                    Icon(
                        painter = painterResource(R.drawable.ic_crown),
                        null,
                        modifier = Modifier.size(22.dp),
                        tint = ShVermillion
                    )
                    Spacer(Modifier.height(8.dp))
                }
                Box(
                    modifier = Modifier
                        .size(if (isFirst) 64.dp else 52.dp)
                        .clip(CircleShape)
                        .background(if (entry.isYou) ShInk else ShSand)
                        .then(
                            if (isFirst) Modifier.border(3.dp, ShVermillion, CircleShape) else Modifier
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = entry.initial,
                        fontSize = if (isFirst) 24.sp else 19.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (entry.isYou) ShPaper else ShInk,
                        fontFamily = DmSansFamily
                    )
                }
                Spacer(Modifier.height(8.dp))
                Text(
                    entry.name,
                    fontSize = 12.5.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = ShInk
                )
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    Icon(painterResource(R.drawable.ic_flame), null, modifier = Modifier.size(13.dp), tint = ShVermillion)
                    Text(entry.streak.toString(), style = ShNumeralStyle.copy(fontSize = 13.sp), color = ShInk)
                }
            }
        }
    }
}

@Composable
private fun LeaderboardRow(entry: LeaderboardEntry) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(if (entry.isYou) ShPaper2 else Color.Transparent)
            .padding(horizontal = if (entry.isYou) { 10.dp } else 0.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = entry.rank.toString(),
            modifier = Modifier.width(20.dp),
            fontSize = 14.sp,
            fontFamily = DmSansFamily,
            color = ShFog2
        )
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(ShSand),
            contentAlignment = Alignment.Center
        ) {
            Text(entry.initial, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = ShInk, fontFamily = DmSansFamily)
        }
        Spacer(Modifier.width(14.dp))
        Text(
            text = entry.name,
            modifier = Modifier.weight(1f),
            fontSize = 15.5.sp,
            fontWeight = FontWeight.Medium,
            color = ShInk
        )
        
        // Trend Icon
        val (trendIcon, trendColor) = when (entry.trend) {
            "up" -> R.drawable.ic_arrow_up to ShMatcha
            "down" -> R.drawable.ic_arrow_down to ShVermillion
            else -> R.drawable.ic_check to ShFog2
        }
        Icon(painterResource(trendIcon), null, modifier = Modifier.size(14.dp), tint = trendColor)
        
        Spacer(Modifier.width(14.dp))
        
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(5.dp)) {
            Icon(painterResource(R.drawable.ic_flame), null, modifier = Modifier.size(15.dp), tint = ShVermillion)
            Text(entry.streak.toString(), style = ShNumeralStyle.copy(fontSize = 15.sp), color = ShInk)
        }
    }
}

private data class LeaderboardEntry(
    val rank: Int,
    val initial: String,
    val name: String,
    val streak: Int,
    val trend: String,
    val isYou: Boolean = false
)
