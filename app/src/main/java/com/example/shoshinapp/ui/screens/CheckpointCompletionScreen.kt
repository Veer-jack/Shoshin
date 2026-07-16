package com.example.shoshinapp.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.platform.LocalContext
import com.example.shoshinapp.ui.components.*
import com.example.shoshinapp.ui.theme.*
import com.example.shoshinapp.utils.AnalyticsManager
import com.example.shoshinapp.utils.LocationHelper
import kotlinx.coroutines.launch
import com.example.shoshinapp.data.ShoshinRepository
import kotlinx.coroutines.flow.first

private data class Checkpoint(
    val label: String, 
    val type: String, 
    val targets: List<String> = emptyList(),
    val hint: String = ""
)

private val TEMPLATE_CHECKPOINTS = mapOf(
    "walk" to listOf(
        Checkpoint("Mind awake", "math"),
        Checkpoint("Freshen up", "photo", listOf("Sink", "Toothbrush", "Bathroom", "Water tap"), "Photo of sink or toothbrush"),
        Checkpoint("Dressed", "photo", listOf("Person", "Clothing", "Selfie"), "Selfie in your walking gear"),
        Checkpoint("Out the door", "photo", listOf("Tree", "Street", "Sky", "Building"), "Photo of a tree or the street"),
        Checkpoint("Walk begun", "done")
    ),
    "study" to listOf(
        Checkpoint("Mind awake", "math"),
        Checkpoint("Freshen up", "photo", listOf("Sink", "Toothbrush", "Water tap"), "Quick freshen up photo"),
        Checkpoint("Tea brewed", "photo", listOf("Cup", "Mug", "Tea", "Drink"), "Photo of your tea or coffee"),
        Checkpoint("Desk ready", "photo", listOf("Book", "Laptop", "Computer", "Paper", "Desk"), "Photo of your study desk"),
        Checkpoint("Study begun", "done")
    ),
    "gym" to listOf(
        Checkpoint("Mind awake", "math"),
        Checkpoint("Freshen up", "photo", listOf("Sink", "Toothbrush", "Towel"), "Morning refresh photo"),
        Checkpoint("Kit on", "photo", listOf("Person", "Clothing", "Mirror"), "Selfie in gym kit"),
        Checkpoint("Gym reached", "photo", listOf("Gym", "Weights", "Dumbbell", "Barbell", "Building"), "Photo at the gym"),
        Checkpoint("Training begun", "done")
    )
)

@Composable
fun CheckpointCompletionScreen(
    onPhotoRequired: (Int, String, List<String>) -> Unit,
    onComplete: () -> Unit,
    streakViewModel: com.example.shoshinapp.viewmodel.StreakViewModel? = null
) {
    val context = LocalContext.current
    val repo = remember { ShoshinRepository(context) }
    val templateKey by repo.template.collectAsState(initial = "walk")
    val checkpoints = TEMPLATE_CHECKPOINTS[templateKey] ?: TEMPLATE_CHECKPOINTS["walk"]!!
    
    var currentIndex by remember { mutableStateOf(0) }
    
    // Auto-skip "Mind awake" if we just came from math
    LaunchedEffect(Unit) {
        if (checkpoints.isNotEmpty() && checkpoints[0].type == "math") {
            currentIndex = 1
        }
    }

    val current = checkpoints.getOrNull(currentIndex) ?: checkpoints.last()
    val scope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
            .padding(24.dp)
    ) {
        Kicker("Morning Practice", color = ShVermillion)
        Spacer(modifier = Modifier.height(10.dp))
        val user by streakViewModel?.user?.collectAsState(initial = null) ?: remember { mutableStateOf(null) }
        Text("Day ${user?.currentStreak ?: 1}", fontSize = 32.sp, fontWeight = FontWeight.SemiBold, fontFamily = CormorantFamily, color = ShInk)
        Spacer(modifier = Modifier.height(32.dp))

        checkpoints.forEachIndexed { index, checkpoint ->
            val state = when {
                index < currentIndex -> CheckpointState.DONE
                index == currentIndex -> CheckpointState.ACTIVE
                else -> CheckpointState.PENDING
            }
            CheckpointRow(
                number = index + 1,
                label = checkpoint.label,
                state = state,
                time = if (state == CheckpointState.DONE) "Completed" else null
            )
            if (index < checkpoints.lastIndex) {
                HorizontalDivider(color = ShLine, thickness = 1.dp, modifier = Modifier.padding(start = 48.dp))
            }
        }

        Spacer(modifier = Modifier.height(48.dp))

        ShoshinButton(
            onClick = {
                if (current.type == "photo") {
                    onPhotoRequired(currentIndex, current.label, current.targets)
                    // We don't increment here, we wait for return from camera
                } else {
                    if (currentIndex < checkpoints.lastIndex) {
                        currentIndex++
                    } else {
                        streakViewModel?.incrementStreak()
                        
                        AnalyticsManager.logCheckpointCompleted(
                            userType = "professional",
                            streak = user?.currentStreak ?: 0,
                            hadPhoto = checkpoints.any { it.type == "photo" },
                            timeSeconds = 0 
                        )
                        
                        scope.launch {
                            val location = LocationHelper.getLastLocation(context)
                            location?.let {
                                AnalyticsManager.logLocationCaptured(it.latitude, it.longitude, "checkpoint")
                            }
                        }

                        onComplete()
                    }
                }
            },
            variant = ShButtonVariant.Accent
        ) {
            Text(if (currentIndex < checkpoints.lastIndex) "Next: ${checkpoints.getOrNull(currentIndex)?.label}" else "Complete Morning")
        }
        
        if (current.type == "photo") {
            Spacer(Modifier.height(12.dp))
            Text(
                text = "Verification: ${current.hint}",
                style = ShLabelStyle,
                color = ShFog,
                modifier = Modifier.fillMaxWidth(),
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
        }
    }
}
