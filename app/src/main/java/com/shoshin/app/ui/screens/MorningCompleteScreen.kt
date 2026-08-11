package com.Shoshin.app.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.Shoshin.app.R
import com.Shoshin.app.data.models.BadgeDefinitions
import com.Shoshin.app.ui.components.*
import com.Shoshin.app.ui.theme.*
import com.Shoshin.app.viewmodel.StreakViewModel
import kotlinx.coroutines.delay

@Composable
fun MorningCompleteScreen(
    onClose: () -> Unit,
    onShare: () -> Unit,
    streakViewModel: com.Shoshin.app.viewmodel.StreakViewModel? = null
) {
    val user by streakViewModel?.user?.collectAsState(initial = null) ?: remember { mutableStateOf(null) }
    val streakCount = user?.currentStreak ?: 0
    val newBadgeId by streakViewModel?.newBadgeUnlocked?.collectAsState() ?: remember { mutableStateOf(null) }

    val reducedMotion = rememberReducedMotion()

    var ringDrawn by remember { mutableStateOf(false) }
    var checkVisible by remember { mutableStateOf(false) }
    var streakVisible by remember { mutableStateOf(false) }

    val ringSweep by animateFloatAsState(
        targetValue = if (ringDrawn) 360f else 0f,
        animationSpec = tween(if (reducedMotion) 0 else 900, easing = FastOutSlowInEasing),
        label = "enso_draw"
    )
    val checkScale by animateFloatAsState(
        targetValue = if (checkVisible) 1f else 0f,
        animationSpec = tween(if (reducedMotion) 0 else 300, easing = if (reducedMotion) FastOutSlowInEasing else ShCelebrationEasing),
        label = "check_scale_in"
    )

    LaunchedEffect(Unit) {
        ringDrawn = true
        if (!reducedMotion) delay(600)
        checkVisible = true
        if (!reducedMotion) delay(300)
        streakVisible = true
    }

    ShoshinTheme(type = ShoshinThemeType.ALWAYS_DARK) {
        Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(ShNight)
                .padding(horizontal = 24.dp, vertical = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.weight(0.2f))

            // Large Enso with Check
            Box(
                modifier = Modifier.size(240.dp),
                contentAlignment = Alignment.Center
            ) {
                ConfettiBurst(trigger = checkVisible)
                androidx.compose.foundation.Canvas(modifier = Modifier.fillMaxSize()) {
                    drawArc(
                        color = ShMatchaDark.copy(alpha = 0.5f),
                        startAngle = -90f,
                        sweepAngle = ringSweep,
                        useCenter = false,
                        style = androidx.compose.ui.graphics.drawscope.Stroke(
                            width = 6.dp.toPx(),
                            cap = androidx.compose.ui.graphics.StrokeCap.Round
                        )
                    )
                }
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .scale(checkScale)
                        .clip(CircleShape)
                        .background(ShMatchaDark),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_check),
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(36.dp)
                    )
                }
            }

            Spacer(Modifier.height(48.dp))

            Kicker("THE BRIDGE IS CROSSED", color = ShMatchaDark)
            
            Spacer(Modifier.height(12.dp))

            Text(
                "You've begun.",
                style = ShTitleStyle.copy(fontSize = 40.sp, color = Color.White),
                textAlign = TextAlign.Center
            )

            Spacer(Modifier.height(16.dp))

            Text(
                "Five checkpoints, twenty-two minutes. The hardest part of the day is already behind you.",
                style = ShBodyStyle,
                color = ShNightMuted,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 24.dp),
                lineHeight = 24.sp
            )

            Spacer(Modifier.height(48.dp))

            // Summary Card
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
                    SummaryStatDark(value = "05:30", label = "Started")
                    Box(Modifier.width(1.dp).height(32.dp).background(ShNightLine))
                    SummaryStatDark(value = "22", unit = "min", label = "Bridge")
                    Box(Modifier.width(1.dp).height(32.dp).background(ShNightLine))
                    SummaryStatDark(value = "5/5", label = "Kept", color = ShMatchaDark)
                }
            }

            Spacer(Modifier.height(24.dp))

            // Streak indicator — under reduced motion, keep only the opacity crossfade (≤150ms), no slide
            AnimatedVisibility(
                visible = streakVisible,
                enter = if (reducedMotion) {
                    fadeIn(tween(150))
                } else {
                    fadeIn(tween(300)) + slideInVertically(tween(300)) { it / 3 }
                }
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    Icon(painterResource(R.drawable.ic_flame), null, tint = ShVermillion, modifier = Modifier.size(16.dp))
                    Text("$streakCount mornings kept", style = ShLabelStyle, fontWeight = FontWeight.Bold, color = Color.White)
                    ShoshinPill(label = "+1", variant = ShPillVariant.Matcha)
                }
            }

            Spacer(Modifier.weight(1f))

            ShoshinButton(
                onClick = onClose,
                variant = ShButtonVariant.Ghost, // Off-white button in dark mode
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Carry it into the day", color = Color.Black, fontWeight = FontWeight.Bold)
            }

            Spacer(Modifier.height(12.dp))

            ShoshinButton(
                onClick = onShare,
                variant = ShButtonVariant.Dark,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(painterResource(R.drawable.ic_share), null, modifier = Modifier.size(18.dp), tint = Color.White)
                Spacer(Modifier.width(8.dp))
                Text("Share streak", color = Color.White, fontWeight = FontWeight.Bold)
            }

            Spacer(Modifier.height(24.dp))

            Text(
                "Return again tomorrow.",
                style = ShLabelStyle.copy(fontSize = 13.sp),
                color = ShNightMuted,
                textAlign = TextAlign.Center
            )

            Spacer(Modifier.height(16.dp))
        }

        if (newBadgeId != null) {
            BadgeCelebrationOverlay(
                badgeId = newBadgeId!!,
                onDismiss = { streakViewModel?.clearBadgeUnlock() }
            )
        }
        }
    }
}

private data class BadgeDisplay(val name: String, val icon: String, val day: Int)

private fun resolveBadgeDisplay(badgeId: String): BadgeDisplay {
    val def = BadgeDefinitions.ALL_BADGES.find { it.id == badgeId }
    if (def != null) return BadgeDisplay(def.name, def.icon, def.threshold)
    val day = badgeId.removePrefix("streak_").toIntOrNull() ?: 0
    val name = StreakViewModel.STREAK_BADGE_NAMES[day] ?: "Day $day"
    return BadgeDisplay(name, "streak_$day", day)
}

@Composable
private fun BadgeCelebrationOverlay(badgeId: String, onDismiss: () -> Unit) {
    val display = remember(badgeId) { resolveBadgeDisplay(badgeId) }
    val reducedMotion = rememberReducedMotion()
    var visible by remember(badgeId) { mutableStateOf(false) }

    val scale by animateFloatAsState(
        targetValue = if (visible) 1f else 0f,
        animationSpec = tween(if (reducedMotion) 0 else 400, easing = if (reducedMotion) FastOutSlowInEasing else ShCelebrationEasing),
        label = "badge_scale_in"
    )

    LaunchedEffect(badgeId) {
        visible = true
        delay(8000)
        onDismiss()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.75f))
            .clickable { onDismiss() },
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Box(modifier = Modifier.size(160.dp), contentAlignment = Alignment.Center) {
                ConfettiBurst(trigger = visible)
                Box(
                    modifier = Modifier
                        .size(120.dp)
                        .scale(scale)
                        .clip(CircleShape)
                        .background(ShVermillionLight),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(getBadgeIconRes(display.icon)),
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(52.dp)
                    )
                }
            }

            Spacer(Modifier.height(24.dp))

            Text("🎉 Badge Earned!", style = ShTitleStyle.copy(fontSize = 22.sp, color = Color.White), textAlign = TextAlign.Center)
            Spacer(Modifier.height(8.dp))
            Text(display.name, style = ShTitleStyle.copy(fontSize = 30.sp, color = Color.White), textAlign = TextAlign.Center)
            Spacer(Modifier.height(4.dp))
            Text("Day ${display.day}", style = ShLabelStyle, color = Color.White.copy(alpha = 0.7f))

            Spacer(Modifier.height(24.dp))

            Text("Tap to close", style = ShLabelStyle.copy(fontSize = 12.sp), color = Color.White.copy(alpha = 0.5f))
        }
    }
}

@Composable
private fun SummaryStatDark(value: String, unit: String? = null, label: String, color: Color = Color.White) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Row(verticalAlignment = Alignment.Bottom) {
            Text(value, style = ShNumeralStyle.copy(fontSize = 32.sp, color = color))
            if (unit != null) {
                Text(unit, style = ShNumeralStyle.copy(fontSize = 14.sp, color = ShNightMuted), modifier = Modifier.padding(bottom = 6.dp, start = 2.dp))
            }
        }
        Text(label.uppercase(), style = ShKickerStyle.copy(fontSize = 9.sp, color = ShNightMuted))
    }
}
