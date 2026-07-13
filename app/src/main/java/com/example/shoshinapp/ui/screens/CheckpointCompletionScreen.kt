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
import com.example.shoshinapp.ui.components.*
import com.example.shoshinapp.ui.theme.*

private data class Checkpoint(val label: String, val type: String)
private val CHECKPOINTS = listOf(
    Checkpoint("Mind awake", "math"),
    Checkpoint("Freshen up", "done"),
    Checkpoint("Dressed", "done"),
    Checkpoint("Out the door", "photo"),
    Checkpoint("Walk begun", "done")
)

@Composable
fun CheckpointCompletionScreen(
    onPhotoRequired: (Int, String) -> Unit,
    onComplete: () -> Unit
) {
    var currentIndex by remember { mutableStateOf(0) }
    val current = CHECKPOINTS[currentIndex]

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(ShPaper)
            .verticalScroll(rememberScrollState())
            .padding(24.dp)
    ) {
        Kicker("Morning Practice", color = ShVermillion)
        Spacer(modifier = Modifier.height(10.dp))
        Text("Day 1", fontSize = 32.sp, fontWeight = FontWeight.SemiBold, fontFamily = CormorantFamily, color = ShInk)
        Spacer(modifier = Modifier.height(32.dp))

        CHECKPOINTS.forEachIndexed { index, checkpoint ->
            val state = when {
                index < currentIndex -> CheckpointState.DONE
                index == currentIndex -> CheckpointState.ACTIVE
                else -> CheckpointState.PENDING
            }
            CheckpointRow(
                number = index + 1,
                label = checkpoint.label,
                state = state
            )
            if (index < CHECKPOINTS.lastIndex) {
                HorizontalDivider(color = ShLine, thickness = 1.dp, modifier = Modifier.padding(start = 48.dp))
            }
        }

        Spacer(modifier = Modifier.height(48.dp))

        ShoshinButton(
            onClick = {
                if (current.type == "photo") {
                    onPhotoRequired(currentIndex, current.label)
                    // In a real app, you'd wait for camera return, but for testing:
                    currentIndex++
                } else {
                    if (currentIndex < CHECKPOINTS.lastIndex) {
                        currentIndex++
                    } else {
                        onComplete()
                    }
                }
            },
            variant = ShButtonVariant.Accent
        ) {
            Text(if (currentIndex < CHECKPOINTS.lastIndex) "Next Checkpoint" else "Complete Morning")
        }
    }
}
