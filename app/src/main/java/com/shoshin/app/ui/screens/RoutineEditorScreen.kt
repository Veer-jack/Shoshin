package com.Shoshin.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
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
import com.Shoshin.app.data.db.entities.RoutineCheckpointEntity
import com.Shoshin.app.data.routine.RoutineDefinitions
import com.Shoshin.app.ui.components.*
import com.Shoshin.app.ui.theme.*
import com.Shoshin.app.viewmodel.RoutineEditorViewModel

@Composable
fun RoutineEditorScreen(navController: NavController, viewModel: RoutineEditorViewModel) {
    val scrollState = rememberScrollState()
    val checkpoints by viewModel.checkpoints.collectAsState()
    val templateKey by viewModel.templateKey.collectAsState()
    val saved by viewModel.saved.collectAsState()
    val defs = RoutineDefinitions.forTemplate(templateKey)

    var editingCheckpoint by remember { mutableStateOf<RoutineCheckpointEntity?>(null) }
    var editingLabel by remember { mutableStateOf("") }
    var pathName by remember { mutableStateOf("Morning Walk") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(ShNight)
            .statusBarsPadding()
    ) {
        // App Bar
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(painterResource(R.drawable.ic_arrow_left), null, tint = Color.White)
                }
                Text("Back", style = ShLabelStyle.copy(color = ShNightMuted, fontWeight = FontWeight.Bold))
            }
            Surface(
                color = ShVermillion.copy(alpha = 0.15f),
                shape = RoundedCornerShape(999.dp)
            ) {
                Text(
                    if (templateKey == "walk") "Movement" else "Study",
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                    style = ShLabelStyle.copy(fontSize = 11.sp, fontWeight = FontWeight.Bold, color = ShVermillion)
                )
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(horizontal = 24.dp)
        ) {
            Text("Edit path", style = ShTitleStyle.copy(fontSize = 32.sp, color = Color.White))

            Spacer(Modifier.height(24.dp))

            Kicker("PATH NAME", color = ShNightMuted)
            Spacer(Modifier.height(10.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .border(1.2.dp, ShNightLine, RoundedCornerShape(16.dp))
                    .background(ShNight2)
                    .padding(horizontal = 16.dp),
                contentAlignment = Alignment.CenterStart
            ) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Text(pathName, style = ShH2Style.copy(fontSize = 16.sp, color = Color.White))
                    Icon(painterResource(R.drawable.ic_edit), null, modifier = Modifier.size(18.dp), tint = ShNightMuted)
                }
            }

            Spacer(Modifier.height(32.dp))
            Kicker("CHECKPOINTS · 5 MIN APART", color = ShNightMuted)
            Spacer(Modifier.height(14.dp))

            checkpoints.forEach { row ->
                val icon = defs.getOrNull(row.slotIndex)?.icon ?: R.drawable.ic_check
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp)
                        .clip(RoundedCornerShape(24.dp))
                        .background(ShNight2)
                        .padding(18.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                            IconButton(
                                onClick = { viewModel.moveUp(row.displayOrder) },
                                enabled = row.displayOrder > 0,
                                modifier = Modifier.size(20.dp)
                            ) {
                                Icon(
                                    painterResource(R.drawable.ic_arrow_up), null,
                                    modifier = Modifier.size(12.dp),
                                    tint = if (row.displayOrder > 0) ShNightMuted else ShNightLine
                                )
                            }
                            IconButton(
                                onClick = { viewModel.moveDown(row.displayOrder) },
                                enabled = row.displayOrder < checkpoints.lastIndex,
                                modifier = Modifier.size(20.dp)
                            ) {
                                Icon(
                                    painterResource(R.drawable.ic_arrow_down), null,
                                    modifier = Modifier.size(12.dp),
                                    tint = if (row.displayOrder < checkpoints.lastIndex) ShNightMuted else ShNightLine
                                )
                            }
                        }

                        Spacer(Modifier.width(16.dp))

                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(ShNightText.copy(alpha = 0.05f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(painterResource(icon), null, modifier = Modifier.size(20.dp), tint = Color.White)
                        }

                        Spacer(Modifier.width(16.dp))
                        Text(
                            row.label,
                            style = ShH2Style.copy(fontSize = 16.sp, color = Color.White),
                            modifier = Modifier.weight(1f)
                        )

                        IconButton(onClick = {
                            editingCheckpoint = row
                            editingLabel = row.label
                        }) {
                            Icon(painterResource(R.drawable.ic_edit), null, modifier = Modifier.size(18.dp), tint = ShNightMuted)
                        }
                    }
                }
            }

            Spacer(Modifier.height(32.dp))

            ShoshinButton(
                onClick = { viewModel.save() },
                variant = ShButtonVariant.Accent,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(if (saved) "Saved" else "Save path", fontWeight = FontWeight.Bold)
            }

            Spacer(Modifier.height(48.dp))
        }
    }

    val checkpointBeingEdited = editingCheckpoint
    if (checkpointBeingEdited != null) {
        AlertDialog(
            onDismissRequest = { editingCheckpoint = null },
            title = { Text("Rename checkpoint", style = ShTitleStyle.copy(fontSize = 20.sp)) },
            text = {
                OutlinedTextField(
                    value = editingLabel,
                    onValueChange = { editingLabel = it },
                    singleLine = true
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    if (editingLabel.isNotBlank()) {
                        viewModel.updateLabel(checkpointBeingEdited.slotIndex, editingLabel.trim())
                    }
                    editingCheckpoint = null
                }) {
                    Text("Save")
                }
            },
            dismissButton = {
                TextButton(onClick = { editingCheckpoint = null }) {
                    Text("Cancel")
                }
            }
        )
    }
}
