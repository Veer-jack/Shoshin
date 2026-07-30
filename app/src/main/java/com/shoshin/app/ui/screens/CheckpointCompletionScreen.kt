package com.Shoshin.app.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.Shoshin.app.R
import com.Shoshin.app.data.ShoshinRepository
import com.Shoshin.app.ui.components.*
import com.Shoshin.app.ui.theme.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

private data class Checkpoint(
    val label: String,
    val icon: Int,
    val type: String,
    val targets: List<String> = emptyList(),
    val hint: String = "",
    val description: String = ""
)

private val TEMPLATE_CHECKPOINTS = mapOf(
    "walk" to listOf(
        Checkpoint("Mind awake", R.drawable.ic_brain, "math", description = "Solve three patterns to bridge the dream state to the day."),
        Checkpoint("Freshen up", R.drawable.ic_droplet, "photo", listOf("Sink", "Toothbrush", "Bathroom", "Mirror", "Water", "Tap", "Faucet"), "Photo of sink or toothbrush", "Cold water resets the system. Thirty seconds is enough."),
        Checkpoint("Dressed", R.drawable.ic_shirt, "photo", listOf("Person", "Clothing", "Selfie", "Fashion"), "Selfie in your gear", "Armor for the morning. Ready to move."),
        Checkpoint("Out the door", R.drawable.ic_sun, "photo", listOf("Tree", "Street", "Sky", "Building", "Outdoor", "Plant"), "Photo of a tree or street", "The threshold is the hardest part. Cross it now."),
        Checkpoint("Walk begun", R.drawable.ic_walk, "done", description = "The path is open. Walk for at least 15 minutes.")
    ),
    "study" to listOf(
        Checkpoint("Mind awake", R.drawable.ic_brain, "math", description = "Sharpen the focus with a quick mental warmup."),
        Checkpoint("Freshen up", R.drawable.ic_droplet, "photo", listOf("Sink", "Toothbrush", "Water tap", "Bathroom"), "Photo of sink", "Awaken the senses with cold water."),
        Checkpoint("Tea brewed", R.drawable.ic_check, "photo", listOf("Cup", "Mug", "Tea", "Drink", "Coffee"), "Photo of your tea", "A warm ritual to settle into deep work."),
        Checkpoint("Desk ready", R.drawable.ic_check, "photo", listOf("Book", "Laptop", "Computer", "Paper", "Desk", "Writing", "Office"), "Photo of your desk", "Clear space, clear mind. The work begins."),
        Checkpoint("Study begun", R.drawable.ic_book, "done", description = "Enter the deep state. Keep the door closed.")
    )
)

enum class RoutineState { IN_PROGRESS, WAITING }

@Composable
fun CheckpointCompletionScreen(
    onPhotoRequired: (Int, String, List<String>) -> Unit,
    onComplete: () -> Unit,
    streakViewModel: com.Shoshin.app.viewmodel.StreakViewModel? = null
) {
    val context = LocalContext.current
    val repo = remember { ShoshinRepository(context) }
    val user by streakViewModel?.user?.collectAsState(initial = null) ?: remember { mutableStateOf(null) }
    
    val templateKey by repo.template.collectAsState(initial = "walk")
    val checkpoints = TEMPLATE_CHECKPOINTS[templateKey] ?: TEMPLATE_CHECKPOINTS["walk"]!!

    var currentIndex by remember { mutableIntStateOf(0) }
    var routineState by remember { mutableStateOf(RoutineState.IN_PROGRESS) }
    var waitTimeSeconds by remember { mutableIntStateOf(0) }
    
    val startTime = remember { SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date()) }

    // Load saved progress
    LaunchedEffect(user) {
        user?.let { u ->
            val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
            if (u.lastRoutineDate == today) {
                currentIndex = u.lastRoutineStepIndex.coerceIn(0, checkpoints.lastIndex)
            }
        }
    }

    // Auto-advance logic if at step 0 (e.g. math done)
    LaunchedEffect(currentIndex) {
        if (currentIndex == 0 && checkpoints[0].type == "math") {
             currentIndex = 1
        }
    }

    LaunchedEffect(routineState, waitTimeSeconds) {
        if (routineState == RoutineState.WAITING && waitTimeSeconds > 0) {
            delay(1000)
            waitTimeSeconds--
            if (waitTimeSeconds <= 0) { routineState = RoutineState.IN_PROGRESS }
        }
    }

    val current = checkpoints.getOrNull(currentIndex) ?: checkpoints.last()

    ShoshinTheme(type = ShoshinThemeType.ALWAYS_DARK) {
        Box(modifier = Modifier.fillMaxSize().background(ShNight)) {
            when (routineState) {
                RoutineState.IN_PROGRESS -> {
                    InProgressUIDark(
                        templateName = if (templateKey == "walk") "Morning Walk" else "Deep Study",
                        currentTime = startTime,
                        checkpoints = checkpoints,
                        currentIndex = currentIndex,
                        onStepClick = { index ->
                             // Allow resuming from any incomplete step
                             if (index < checkpoints.size) {
                                 currentIndex = index
                                 streakViewModel?.saveRoutineProgress(index)
                             }
                        },
                        onAction = {
                            if (current.type == "photo") {
                                onPhotoRequired(currentIndex, current.label, current.targets)
                            } else if (current.type == "done") {
                                streakViewModel?.incrementStreak()
                                streakViewModel?.resetRoutineProgress()
                                onComplete()
                            } else {
                                if (currentIndex < checkpoints.lastIndex) {
                                    val nextIndex = currentIndex + 1
                                    currentIndex = nextIndex
                                    streakViewModel?.saveRoutineProgress(nextIndex)
                                    routineState = RoutineState.WAITING
                                    waitTimeSeconds = 300
                                } else {
                                    streakViewModel?.resetRoutineProgress()
                                    onComplete()
                                }
                            }
                        }
                    )
                }
                RoutineState.WAITING -> {
                    WaitingUIDark(
                        completedCheckpoint = checkpoints[currentIndex - 1],
                        nextCheckpoint = current,
                        timeLeftSeconds = waitTimeSeconds,
                        onSkip = { routineState = RoutineState.IN_PROGRESS }
                    )
                }
            }
        }
    }
}

@Composable
private fun InProgressUIDark(
    templateName: String,
    currentTime: String,
    checkpoints: List<Checkpoint>,
    currentIndex: Int,
    onStepClick: (Int) -> Unit,
    onAction: () -> Unit
) {
    val current = checkpoints[currentIndex]
    val progress = ((currentIndex + 1).toFloat() / checkpoints.size.toFloat()) // +1 to show progress of current step
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp)
    ) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth().padding(vertical = 20.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top
        ) {
            Column {
                Kicker("CROSSING THE BRIDGE", color = ShVermillion)
                Text(templateName, style = ShTitleStyle.copy(fontSize = 22.sp, color = Color.White))
            }
            Column(horizontalAlignment = Alignment.End) {
                Text(currentTime, style = ShNumeralStyle.copy(fontSize = 24.sp, color = Color.White))
                Text("+16 min elapsed", style = ShLabelStyle.copy(fontSize = 11.sp), color = ShNightMuted)
            }
        }

        // Progress
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text("${currentIndex + 1} of ${checkpoints.size} complete", style = ShLabelStyle, color = ShNightMuted)
            Text("${(progress * 100).toInt()}%", style = ShLabelStyle, fontWeight = FontWeight.Bold, color = ShMatchaDark)
        }
        Spacer(Modifier.height(8.dp))
        LinearProgressIndicator(
            progress = { progress },
            modifier = Modifier.fillMaxWidth().height(6.dp).clip(CircleShape),
            color = ShMatchaDark,
            trackColor = ShNight3,
            strokeCap = androidx.compose.ui.graphics.StrokeCap.Round
        )

        Spacer(Modifier.height(24.dp))

        // Active Card
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(24.dp))
                .border(2.dp, Color.White, RoundedCornerShape(24.dp))
                .background(ShNight2)
                .padding(24.dp)
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Box(
                    modifier = Modifier.size(64.dp).clip(RoundedCornerShape(16.dp)).background(ShPaper2),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(painterResource(current.icon), null, tint = ShInk, modifier = Modifier.size(32.dp))
                }
                Spacer(Modifier.height(16.dp))
                Kicker("CURRENT CHECKPOINT", color = ShNightMuted)
                Text(current.label, style = ShTitleStyle.copy(fontSize = 28.sp, color = Color.White))
                Spacer(Modifier.height(12.dp))
                Text(current.description, style = ShBodyStyle, color = ShNightMuted, textAlign = TextAlign.Center)
                Spacer(Modifier.height(32.dp))
                ShoshinButton(onClick = onAction, variant = ShButtonVariant.Ghost, modifier = Modifier.fillMaxWidth()) {
                    Icon(painterResource(R.drawable.ic_camera), null, modifier = Modifier.size(20.dp), tint = Color.Black)
                    Spacer(Modifier.width(10.dp))
                    Text(if (current.type == "photo") "Take verification photo" else "Complete Checkpoint", color = Color.Black, fontWeight = FontWeight.Bold)
                }
            }
        }

        Spacer(Modifier.height(24.dp))

        // Checkpoint List
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(24.dp))
                .background(ShNight2)
                .padding(20.dp)
        ) {
            Column {
                checkpoints.forEachIndexed { index, cp ->
                    val isDone = index < currentIndex
                    val isActive = index == currentIndex
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { if (!isDone) onStepClick(index) }
                            .padding(vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(if (isDone) ShMatchaDark else Color.Transparent)
                                .border(1.5.dp, if (isDone) ShMatchaDark else if (isActive) Color.White else ShNightLine, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            if (isDone) Icon(painterResource(R.drawable.ic_check), null, tint = Color.White, modifier = Modifier.size(16.dp))
                            else Text((index + 1).toString(), style = ShLabelStyle, color = if (isActive) Color.White else ShNightMuted)
                        }
                        Spacer(Modifier.width(16.dp))
                        Icon(painterResource(cp.icon), null, modifier = Modifier.size(18.dp), tint = if (isActive) Color.White else ShNightMuted)
                        Spacer(Modifier.width(16.dp))
                        Text(cp.label, style = ShBodyStyle, color = if (isActive) Color.White else if (isDone) Color.White.copy(alpha = 0.5f) else ShNightMuted, modifier = Modifier.weight(1f))
                        
                        if (isDone) {
                             Icon(painterResource(R.drawable.ic_check), null, modifier = Modifier.size(14.dp), tint = ShMatchaDark)
                        } else if (isActive) {
                             Icon(painterResource(R.drawable.ic_progress), null, modifier = Modifier.size(14.dp), tint = Color.White)
                        }
                    }
                }
            }
        }
        Spacer(Modifier.height(48.dp))
    }
}

@Composable
private fun WaitingUIDark(
    completedCheckpoint: Checkpoint,
    nextCheckpoint: Checkpoint,
    timeLeftSeconds: Int,
    onSkip: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Kicker("CHECKPOINT KEPT", color = ShNightMuted)
        Spacer(Modifier.height(12.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(modifier = Modifier.size(28.dp).clip(RoundedCornerShape(8.dp)).background(ShMatchaDark), contentAlignment = Alignment.Center) {
                Icon(painterResource(R.drawable.ic_check), null, tint = Color.White, modifier = Modifier.size(16.dp))
            }
            Spacer(Modifier.width(12.dp))
            Text(completedCheckpoint.label, style = ShH2Style.copy(fontSize = 17.sp, color = Color.White))
        }

        Spacer(Modifier.height(64.dp))

        Box(contentAlignment = Alignment.Center) {
            val progress = timeLeftSeconds.toFloat() / 300f
            CircularProgressIndicator(
                progress = { progress },
                modifier = Modifier.size(240.dp),
                color = ShVermillion,
                strokeWidth = 2.dp,
                trackColor = ShNight3,
                strokeCap = androidx.compose.ui.graphics.StrokeCap.Round
            )
            // Progress dots mockup
            Box(modifier = Modifier.size(240.dp)) {
                Box(modifier = Modifier.size(6.dp).clip(CircleShape).background(ShVermillion).align(Alignment.TopCenter).offset(y = (-2).dp))
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(formatSeconds(timeLeftSeconds), style = ShNumeralStyle.copy(fontSize = 64.sp, color = Color.White))
                Text("UNTIL NEXT", style = ShKickerStyle.copy(color = ShNightMuted))
            }
        }

        Spacer(Modifier.height(64.dp))

        Kicker("NEXT CHECKPOINT", color = ShNightMuted)
        Spacer(Modifier.height(12.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(painterResource(nextCheckpoint.icon), null, modifier = Modifier.size(24.dp), tint = ShNightMuted)
            Spacer(Modifier.width(12.dp))
            Text(nextCheckpoint.label, style = ShTitleStyle.copy(fontSize = 24.sp, color = Color.White))
        }
        
        Spacer(Modifier.height(12.dp))
        Text("Give the practice room to breathe.\nFive minutes, then cross to the\nnext checkpoint.", style = ShBodyStyle, color = ShNightMuted, textAlign = TextAlign.Center)

        Spacer(Modifier.weight(1f))

        ShoshinButton(
            onClick = { },
            variant = ShButtonVariant.Dark,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Wait ${formatSeconds(timeLeftSeconds)}", color = Color.White.copy(alpha = 0.6f))
        }

        Spacer(Modifier.height(16.dp))
        Text("Skip wait (done)", style = ShLabelStyle.copy(color = ShNightMuted, fontWeight = FontWeight.Bold), modifier = Modifier.clickable { onSkip() })
        Spacer(Modifier.height(24.dp))
    }
}

private fun formatSeconds(totalSeconds: Int): String {
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60
    return String.format("%02d:%02d", minutes, seconds)
}
