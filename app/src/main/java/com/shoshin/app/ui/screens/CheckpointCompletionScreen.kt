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
import androidx.navigation.NavController
import com.Shoshin.app.R
import com.Shoshin.app.data.RoutineRepository
import com.Shoshin.app.data.ShoshinRepository
import com.Shoshin.app.data.db.AppDatabase
import com.Shoshin.app.data.routine.RoutineCheckpointDef as Checkpoint
import com.Shoshin.app.data.routine.RoutineDefinitions
import com.Shoshin.app.ui.components.*
import com.Shoshin.app.ui.theme.*
import com.Shoshin.app.utils.AnalyticsManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun CheckpointCompletionScreen(
    onPhotoRequired: (Int, String, List<String>) -> Unit,
    onComplete: () -> Unit,
    onForfeit: () -> Unit,
    navController: NavController,
    database: AppDatabase,
    streakViewModel: com.Shoshin.app.viewmodel.StreakViewModel? = null
) {
    val context = LocalContext.current
    val repo = remember { ShoshinRepository(context) }
    val routineRepository = remember { RoutineRepository(database.routineCheckpointDao(), FirebaseFirestore.getInstance()) }
    val userId = remember { FirebaseAuth.getInstance().currentUser?.uid }
    val user by streakViewModel?.user?.collectAsState(initial = null) ?: remember { mutableStateOf(null) }

    val templateKey by repo.template.collectAsState(initial = "walk")
    val defs = RoutineDefinitions.forTemplate(templateKey)
    val routineFlow = remember(userId, templateKey) {
        userId?.let { routineRepository.getRoutineFlow(it, templateKey) } ?: kotlinx.coroutines.flow.flowOf(emptyList())
    }
    val persistedRows by routineFlow.collectAsState(initial = emptyList())

    LaunchedEffect(userId, templateKey) {
        userId?.let { routineRepository.ensureSeeded(it, templateKey) }
    }

    val checkpoints = if (persistedRows.size == defs.size) {
        persistedRows.sortedBy { it.displayOrder }.map { row -> defs[row.slotIndex].copy(label = row.label) }
    } else {
        defs
    }

    var currentIndex by remember { mutableIntStateOf(0) }
    var showForfeitDialog by remember { mutableStateOf(false) }

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

    // Consume the "checkpoint completed" signal set by CameraVerificationScreen on a
    // successful/auto-accepted photo, since there is no rest screen to gate advancement anymore.
    val latestCheckpoints = rememberUpdatedState(checkpoints)
    LaunchedEffect(navController) {
        navController.currentBackStackEntry?.savedStateHandle
            ?.getStateFlow("checkpoint_completed", false)
            ?.collect { done ->
                if (done) {
                    navController.currentBackStackEntry?.savedStateHandle?.set("checkpoint_completed", false)
                    val cps = latestCheckpoints.value
                    if (currentIndex < cps.lastIndex) {
                        val nextIndex = currentIndex + 1
                        currentIndex = nextIndex
                        streakViewModel?.saveRoutineProgress(nextIndex)
                    } else {
                        streakViewModel?.incrementStreak()
                        streakViewModel?.resetRoutineProgress()
                        onComplete()
                    }
                }
            }
    }

    val current = checkpoints.getOrNull(currentIndex) ?: checkpoints.last()

    ShoshinTheme(type = ShoshinThemeType.ALWAYS_DARK) {
        Box(modifier = Modifier.fillMaxSize().background(ShNight)) {
            InProgressUIDark(
                templateName = when (templateKey) { "walk" -> "Morning Walk"; "gym" -> "Gym Session"; else -> "Deep Study" },
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
                    }
                },
                onForfeitClick = { showForfeitDialog = true }
            )
        }
    }

    if (showForfeitDialog) {
        AlertDialog(
            onDismissRequest = { showForfeitDialog = false },
            title = { Text("Skip today's routine?") },
            text = { Text("You'll lose your streak. Are you sure?") },
            confirmButton = {
                TextButton(onClick = {
                    showForfeitDialog = false
                    streakViewModel?.resetStreak()
                    streakViewModel?.resetRoutineProgress()
                    AnalyticsManager.logStreakReset("forfeit")
                    onForfeit()
                }) {
                    Text("Yes, skip", color = ShVermillion)
                }
            },
            dismissButton = {
                TextButton(onClick = { showForfeitDialog = false }) {
                    Text("Continue")
                }
            }
        )
    }
}

@Composable
private fun InProgressUIDark(
    templateName: String,
    currentTime: String,
    checkpoints: List<Checkpoint>,
    currentIndex: Int,
    onStepClick: (Int) -> Unit,
    onAction: () -> Unit,
    onForfeitClick: () -> Unit
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

        Spacer(Modifier.height(24.dp))
        Text(
            "Skip today's routine?",
            style = ShLabelStyle.copy(color = ShNightMuted, fontWeight = FontWeight.Bold),
            modifier = Modifier.fillMaxWidth().clickable { onForfeitClick() },
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(48.dp))
    }
}
