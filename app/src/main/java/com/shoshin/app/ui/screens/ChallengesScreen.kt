package com.Shoshin.app.ui.screens

import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.Shoshin.app.R
import com.Shoshin.app.data.models.Badge
import com.Shoshin.app.data.models.BadgeCategory
import com.Shoshin.app.data.models.MonthlyStats
import com.Shoshin.app.navigation.ShRoutes
import com.Shoshin.app.ui.components.*
import com.Shoshin.app.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun ChallengesScreen(
    navController: NavController,
    streakViewModel: com.Shoshin.app.viewmodel.StreakViewModel? = null,
    badgeViewModel: com.Shoshin.app.viewmodel.BadgeViewModel? = null,
    referralViewModel: com.Shoshin.app.viewmodel.ReferralViewModel? = null,
    onNavigateToTab: ((String) -> Unit)? = null
) {
    val user by streakViewModel?.user?.collectAsState(initial = null) ?: remember { mutableStateOf(null) }
    val currentStreak = user?.currentStreak ?: 0

    if (currentStreak == 0) {
        ChallengesEmptyState(onGoHome = { onNavigateToTab?.invoke(ShRoutes.HOME) })
    } else {
        PopulatedChallengesScreen(navController, streakViewModel, badgeViewModel, referralViewModel)
    }
}

@Composable
private fun ChallengesEmptyState(onGoHome: () -> Unit) {
    ShoshinTheme(type = ShoshinThemeType.DYNAMIC) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(painterResource(R.drawable.ic_trophy), contentDescription = null, tint = ShVermillion, modifier = Modifier.size(40.dp))
            Spacer(Modifier.height(24.dp))
            Kicker("NO STREAK YET", color = ShVermillion)
            Spacer(Modifier.height(12.dp))
            Text(
                "Start your morning routine to unlock badges",
                style = ShTitleStyle.copy(fontSize = 24.sp, color = MaterialTheme.colorScheme.onBackground),
                textAlign = TextAlign.Center
            )
            Spacer(Modifier.height(12.dp))
            Text(
                "Every milestone starts with day one.",
                style = ShBodyStyle,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
            Spacer(Modifier.height(32.dp))
            ShoshinButton(
                onClick = onGoHome,
                variant = ShButtonVariant.Accent,
                modifier = Modifier.fillMaxWidth(0.7f)
            ) {
                Text("Go to Home", fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
private fun PopulatedChallengesScreen(
    navController: NavController,
    streakViewModel: com.Shoshin.app.viewmodel.StreakViewModel?,
    badgeViewModel: com.Shoshin.app.viewmodel.BadgeViewModel?,
    referralViewModel: com.Shoshin.app.viewmodel.ReferralViewModel?
) {
    val context = LocalContext.current
    val user by streakViewModel?.user?.collectAsState(initial = null) ?: remember { mutableStateOf(null) }
    val monthlyStats by streakViewModel?.monthlyStats?.collectAsState(initial = null) ?: remember { mutableStateOf(null) }
    val allBadges by badgeViewModel?.badges?.collectAsState(initial = emptyList()) ?: remember { mutableStateOf(emptyList()) }
    val referralLimits by referralViewModel?.limits?.collectAsState(initial = null) ?: remember { mutableStateOf(null) }

    val currentStreak = user?.currentStreak ?: 0
    val bestStreak = user?.bestStreak ?: 0
    val stats = monthlyStats ?: MonthlyStats(0, 0f, currentStreak, bestStreak)
    val daysInMonth = remember { Calendar.getInstance().getActualMaximum(Calendar.DAY_OF_MONTH) }

    val streakBadges = remember(allBadges) {
        allBadges.filter { it.category == BadgeCategory.STREAK && it.threshold <= 120 }.sortedBy { it.threshold }
    }
    val nextMilestone = streakBadges.firstOrNull { it.isLocked }

    fun shareProgress() {
        val code = referralLimits?.referralCode
        val link = if (!code.isNullOrEmpty()) "https://shoshin.app/join/$code" else "https://shoshin.app"
        val message = "I'm on Day $currentStreak of my morning routine on Shoshin! 🔥\nJoin me: $link"
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, message)
        }
        context.startActivity(Intent.createChooser(intent, "Share your streak"))
    }

    ShoshinTheme(type = ShoshinThemeType.DYNAMIC) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(Modifier.height(32.dp))

            Column(Modifier.padding(horizontal = 24.dp)) {
                Kicker("STREAK & BADGES", color = MaterialTheme.colorScheme.onSurfaceVariant)
                Spacer(Modifier.height(4.dp))
                Text("Challenges", style = ShTitleStyle.copy(fontSize = 32.sp, color = MaterialTheme.colorScheme.onBackground))
            }

            Spacer(Modifier.height(24.dp))

            Column(Modifier.padding(horizontal = 24.dp)) {
                HeroStreakCard(
                    currentStreak = currentStreak,
                    bestStreak = bestStreak,
                    nextMilestone = nextMilestone,
                    onShare = { shareProgress() }
                )
                Spacer(Modifier.height(16.dp))
                MonthlyStatsCard(stats, daysInMonth)
            }

            Spacer(Modifier.height(28.dp))

            BadgeProgressRow(
                badges = streakBadges,
                currentStreak = currentStreak,
                nextMilestoneId = nextMilestone?.id,
                onBadgeClick = { badge -> navController.navigate(ShRoutes.badgeDetail(badge.id)) }
            )

            Spacer(Modifier.height(28.dp))

            BadgeGallerySection(
                badges = streakBadges,
                currentStreak = currentStreak,
                nextMilestoneId = nextMilestone?.id,
                onBadgeClick = { badge -> navController.navigate(ShRoutes.badgeDetail(badge.id)) }
            )

            Spacer(Modifier.height(32.dp))
        }
    }
}

private fun daysUntil(threshold: Int, currentStreak: Int) = (threshold - currentStreak).coerceAtLeast(0)

@Composable
private fun HeroStreakCard(
    currentStreak: Int,
    bestStreak: Int,
    nextMilestone: Badge?,
    onShare: () -> Unit
) {
    ShoshinCard(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(24.dp)) {
            Text(
                "Day $currentStreak",
                style = ShTitleStyle.copy(fontSize = 48.sp, color = ShVermillion),
                fontWeight = FontWeight.Bold
            )
            Spacer(Modifier.height(4.dp))
            Text("Current Morning Streak", style = ShLabelStyle, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(Modifier.height(2.dp))
            Text("Best: $bestStreak days", style = ShLabelStyle, color = MaterialTheme.colorScheme.onSurfaceVariant)

            Spacer(Modifier.height(20.dp))

            if (nextMilestone != null) {
                val progress = (currentStreak.toFloat() / nextMilestone.threshold).coerceIn(0f, 1f)
                LinearProgressIndicator(
                    progress = { progress },
                    modifier = Modifier.fillMaxWidth().height(8.dp).clip(RoundedCornerShape(999.dp)),
                    color = ShVermillion,
                    trackColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
                    strokeCap = androidx.compose.ui.graphics.StrokeCap.Round
                )
                Spacer(Modifier.height(8.dp))
                val daysLeft = daysUntil(nextMilestone.threshold, currentStreak)
                Text(
                    "$daysLeft ${if (daysLeft == 1) "day" else "days"} until ${nextMilestone.threshold}-day badge",
                    style = ShLabelStyle,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            } else {
                Text("All streak milestones unlocked!", style = ShLabelStyle, color = ShMatcha)
            }

            Spacer(Modifier.height(20.dp))
            HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f))
            Spacer(Modifier.height(16.dp))

            ShoshinButton(
                onClick = onShare,
                variant = ShButtonVariant.Ghost,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(painterResource(R.drawable.ic_share), contentDescription = null, tint = MaterialTheme.colorScheme.onBackground, modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(8.dp))
                Text("Share progress", fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
private fun MonthlyStatsCard(stats: MonthlyStats, daysInMonth: Int) {
    ShoshinCard(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(24.dp)) {
            Text("This month", style = ShH2Style, color = MaterialTheme.colorScheme.onSurface)
            Spacer(Modifier.height(20.dp))
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                MonthlyStatItem("${stats.daysCompleted}/$daysInMonth", "DAYS COMPLETED", Modifier.weight(1f))
                MonthlyStatItem("${"%.1f".format(stats.completionRate)}%", "COMPLETION RATE", Modifier.weight(1f))
            }
            Spacer(Modifier.height(20.dp))
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                MonthlyStatItem("${stats.currentStreak} days", "CURRENT STREAK", Modifier.weight(1f))
                MonthlyStatItem("${stats.bestStreak} days", "BEST STREAK", Modifier.weight(1f))
            }
        }
    }
}

@Composable
private fun MonthlyStatItem(value: String, label: String, modifier: Modifier = Modifier) {
    Column(modifier = modifier) {
        Text(value, style = ShTitleStyle.copy(fontSize = 24.sp, color = ShVermillion), fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(4.dp))
        Text(label, style = ShKickerStyle.copy(fontSize = 9.sp, letterSpacing = 1.sp), color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

@Composable
private fun BadgeProgressRow(
    badges: List<Badge>,
    currentStreak: Int,
    nextMilestoneId: String?,
    onBadgeClick: (Badge) -> Unit
) {
    Column {
        Text("Milestones", style = ShH2Style, color = MaterialTheme.colorScheme.onSurface, modifier = Modifier.padding(horizontal = 24.dp))
        Spacer(Modifier.height(16.dp))
        LazyRow(
            contentPadding = PaddingValues(horizontal = 24.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(badges) { badge ->
                MilestoneBadgeCard(
                    badge = badge,
                    currentStreak = currentStreak,
                    isNext = badge.id == nextMilestoneId,
                    onClick = { onBadgeClick(badge) }
                )
            }
        }
    }
}

@Composable
private fun MilestoneBadgeCard(badge: Badge, currentStreak: Int, isNext: Boolean, onClick: () -> Unit) {
    val unlocked = !badge.isLocked
    Column(
        modifier = Modifier
            .width(96.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .then(if (isNext) Modifier.border(2.dp, ShVermillion, RoundedCornerShape(16.dp)) else Modifier)
            .clickable { onClick() }
            .padding(vertical = 16.dp, horizontal = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(if (unlocked) ShVermillion.copy(alpha = 0.12f) else androidx.compose.ui.graphics.Color.Transparent),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(if (unlocked) getBadgeIconRes(badge.icon) else R.drawable.ic_lock),
                contentDescription = null,
                tint = if (unlocked) ShVermillion else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                modifier = Modifier.size(22.dp)
            )
        }
        Spacer(Modifier.height(8.dp))
        Text(
            "${badge.threshold} days",
            style = ShLabelStyle.copy(fontSize = 11.sp, fontWeight = FontWeight.Bold),
            color = if (unlocked) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(Modifier.height(2.dp))
        val caption = when {
            unlocked -> badge.unlockedDate?.takeIf { it > 0 }
                ?.let { "Earned " + SimpleDateFormat("MMM d", Locale.getDefault()).format(Date(it)) } ?: "Earned"
            isNext -> "$currentStreak/${badge.threshold}"
            else -> "${daysUntil(badge.threshold, currentStreak)}d away"
        }
        Text(
            caption,
            style = ShKickerStyle.copy(fontSize = 8.sp),
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun BadgeGallerySection(
    badges: List<Badge>,
    currentStreak: Int,
    nextMilestoneId: String?,
    onBadgeClick: (Badge) -> Unit
) {
    val unlocked = badges.filter { !it.isLocked }
    val inProgress = badges.filter { it.isLocked && it.id == nextMilestoneId }
    val locked = badges.filter { it.isLocked && it.id != nextMilestoneId }

    Column(modifier = Modifier.padding(horizontal = 24.dp)) {
        Text("All badges", style = ShH2Style, color = MaterialTheme.colorScheme.onSurface)
        Spacer(Modifier.height(16.dp))

        if (unlocked.isNotEmpty()) {
            Kicker("UNLOCKED", color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.padding(bottom = 10.dp))
            unlocked.forEach { BadgeGalleryRow(it, currentStreak, isNext = false, onClick = { onBadgeClick(it) }) }
            Spacer(Modifier.height(20.dp))
        }
        if (inProgress.isNotEmpty()) {
            Kicker("IN PROGRESS", color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.padding(bottom = 10.dp))
            inProgress.forEach { BadgeGalleryRow(it, currentStreak, isNext = true, onClick = { onBadgeClick(it) }) }
            Spacer(Modifier.height(20.dp))
        }
        if (locked.isNotEmpty()) {
            Kicker("LOCKED", color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.padding(bottom = 10.dp))
            locked.forEach { BadgeGalleryRow(it, currentStreak, isNext = false, onClick = { onBadgeClick(it) }) }
        }
    }
}

@Composable
private fun BadgeGalleryRow(badge: Badge, currentStreak: Int, isNext: Boolean, onClick: () -> Unit) {
    val unlocked = !badge.isLocked
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(if (unlocked) ShVermillion.copy(alpha = 0.06f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
            .clickable { onClick() }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(56.dp)
                .clip(CircleShape)
                .background(if (unlocked) ShVermillion.copy(alpha = 0.12f) else androidx.compose.ui.graphics.Color.Transparent),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(if (unlocked) getBadgeIconRes(badge.icon) else R.drawable.ic_lock),
                contentDescription = null,
                tint = if (unlocked) ShVermillion else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                modifier = Modifier.size(26.dp)
            )
        }
        Spacer(Modifier.width(16.dp))
        Column(Modifier.weight(1f)) {
            Text(
                badge.name,
                style = ShH2Style.copy(fontSize = 15.sp),
                color = if (unlocked) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurfaceVariant,
                fontWeight = FontWeight.Bold
            )
            Text(
                badge.requirementDescription,
                style = ShLabelStyle.copy(fontSize = 11.sp),
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Spacer(Modifier.width(8.dp))
        val status = when {
            unlocked -> badge.unlockedDate?.takeIf { it > 0 }
                ?.let { "Earned " + SimpleDateFormat("MMM d", Locale.getDefault()).format(Date(it)) } ?: "Earned"
            isNext -> "$currentStreak/${badge.threshold}\nprogress"
            else -> "${daysUntil(badge.threshold, currentStreak)}\ndays away"
        }
        Text(
            status,
            style = ShLabelStyle.copy(fontSize = 11.sp, fontWeight = FontWeight.Bold),
            color = if (unlocked) ShVermillion else MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.End
        )
    }
}
