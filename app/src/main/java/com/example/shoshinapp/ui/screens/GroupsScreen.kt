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
import com.example.shoshinapp.navigation.ShRoutes
import com.example.shoshinapp.ui.components.*
import com.example.shoshinapp.ui.theme.*

private data class Member(
    val initial: String,
    val name: String,
    val status: String,
    val streak: Int,
    val isYou: Boolean = false
)

private val SH_POD = listOf(
    Member("A", "Arjun (you)", "practicing", 14, true),
    Member("M", "Mei", "practicing", 31),
    Member("R", "Rahul", "practicing", 9),
    Member("S", "Sofia", "resting", 22),
    Member("K", "Kenji", "sleeping", 5)
)

@Composable
fun GroupsScreen(
    navController: NavController,
    referralViewModel: com.example.shoshinapp.viewmodel.ReferralViewModel? = null
) {
    val limits by referralViewModel?.limits?.collectAsState(initial = null) ?: remember { mutableStateOf(null) }
    val joinedCount = 4 // Mocked for now
    val maxJoin = limits?.groupsJoinLimit ?: 5
    
    val practicingCount = SH_POD.count { it.status == "practicing" }
    val members = SH_POD // In real app, this would be dynamic

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(ShPaper)
    ) {
        // ... (Header Box remains same)
        
        // LIMIT DISPLAY (Feature 4.4)
        Surface(
            color = ShInk,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("MY GROUPS", style = ShKickerStyle, color = ShPaper.copy(alpha = 0.6f))
                    Text(
                        "Joined: $joinedCount of $maxJoin",
                        style = ShBodyStyle,
                        color = ShPaper,
                        fontWeight = FontWeight.Bold
                    )
                }
                TextButton(onClick = { navController.navigate(ShRoutes.REFERRALS) }) {
                    Text("Refer to unlock", color = ShVermillion, style = ShLabelStyle)
                }
            }
        }

        // WARNING BANNER (Feature 4.4)
        if (maxJoin - joinedCount <= 1) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(ShVermillion.copy(alpha = 0.1f))
                    .clickable { navController.navigate(ShRoutes.REFERRALS) }
                    .padding(vertical = 8.dp, horizontal = 24.dp)
            ) {
                Text(
                    "⚠️ ${maxJoin - joinedCount} group slot remaining. Refer a friend to unlock more →",
                    style = ShLabelStyle,
                    color = ShVermillion
                )
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp)
        ) {
            // ... (rest of the file)

            if (members.isEmpty()) {
                EmptyState(
                    title = "Solitude is peace, but a circle is power",
                    description = "Join or create a circle to rise with others and keep each other accountable.",
                    iconRes = R.drawable.ic_groups,
                    actionLabel = "Create a Circle",
                    onAction = { navController.navigate("create_group") }
                )
            } else {
                // Wake Board Card
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(20.dp))
                        .background(ShInk)
                        .padding(22.dp)
                ) {
                    // Enso motif background
                    Box(
                        modifier = Modifier
                            .size(150.dp)
                            .align(Alignment.TopEnd)
                            .offset(x = 34.dp, y = (-34).dp)
                    ) {
                        androidx.compose.foundation.Canvas(modifier = Modifier.fillMaxSize()) {
                            drawArc(
                                color = ShVermillion.copy(alpha = 0.35f),
                                startAngle = -90f,
                                sweepAngle = 320f,
                                useCenter = false,
                                style = androidx.compose.ui.graphics.drawscope.Stroke(
                                    width = 6.dp.toPx(),
                                    cap = androidx.compose.ui.graphics.StrokeCap.Round
                                )
                            )
                        }
                    }

                    Column {
                        Kicker(
                            "This morning · 5:30 AM",
                            color = ShNightText.copy(alpha = 0.6f),
                            modifier = Modifier.padding(bottom = 10.dp)
                        )
                        Row(verticalAlignment = Alignment.Bottom, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text(
                                "$practicingCount",
                                fontSize = 40.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = DmSansFamily,
                                color = ShPaper
                            )
                            Text(
                                "of ${members.size} have begun",
                                fontSize = 22.sp,
                                fontFamily = CormorantFamily,
                                color = ShNightText.copy(alpha = 0.7f),
                                modifier = Modifier.padding(bottom = 4.dp)
                            )
                        }
                        Text(
                            "You rose with others in your circle. Sit together at dawn.",
                            fontSize = 13.5.sp,
                            color = ShNightText.copy(alpha = 0.7f),
                            fontFamily = DmSansFamily,
                            modifier = Modifier.padding(top = 10.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Members List Card
                ShoshinCard(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(horizontal = 18.dp, vertical = 6.dp)) {
                        members.forEachIndexed { index, member ->
                            MemberRow(member)
                            if (index < members.lastIndex) {
                                HorizontalDivider(color = ShLine, thickness = 1.dp)
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Invite Button
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .clickable { /* Invite logic */ },
                    color = Color.Transparent,
                    shape = RoundedCornerShape(14.dp),
                    border = androidx.compose.foundation.BorderStroke(1.5.dp, ShLine2)
                ) {
                    Row(
                        modifier = Modifier.fillMaxSize(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_plus),
                            contentDescription = null,
                            tint = ShFog,
                            modifier = Modifier.size(19.dp).offset(y = (-1).dp)
                        )
                        Spacer(Modifier.width(8.dp))
                        Text(
                            "Invite someone to the circle",
                            fontSize = 14.5.sp,
                            fontWeight = FontWeight.SemiBold,
                            fontFamily = DmSansFamily,
                            color = ShFog
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(48.dp))
        }
    }
}

@Composable
private fun MemberRow(member: Member) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Avatar
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(if (member.isYou) ShInk else ShSand),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = member.initial,
                style = ShH2Style,
                color = if (member.isYou) ShPaper else ShInk,
                fontSize = 17.sp
            )
        }

        Spacer(Modifier.width(14.dp))

        // Info
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = member.name,
                style = ShH2Style.copy(fontSize = 15.sp),
                color = ShInk
            )
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(5.dp), modifier = Modifier.padding(top = 3.dp)) {
                val statusColor = when (member.status) {
                    "practicing" -> ShMatcha
                    "resting" -> ShFog
                    else -> ShFog2
                }
                Box(modifier = Modifier.size(6.dp).clip(CircleShape).background(statusColor))
                Text(
                    text = member.status.replaceFirstChar { it.uppercase() },
                    style = ShKickerStyle.copy(fontSize = 12.sp, letterSpacing = 0.sp),
                    color = statusColor,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }

        // Streak
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(5.dp)) {
            Icon(
                painter = painterResource(id = R.drawable.ic_flame),
                contentDescription = null,
                tint = ShVermillion,
                modifier = Modifier.size(15.dp)
            )
            Text(
                text = member.streak.toString(),
                style = ShNumeralStyle.copy(fontSize = 15.sp),
                color = ShInk
            )
        }
    }
}
