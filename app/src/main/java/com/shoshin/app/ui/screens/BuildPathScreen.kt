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
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.Shoshin.app.R
import com.Shoshin.app.ui.components.*
import com.Shoshin.app.ui.theme.*

@Composable
fun BuildPathScreen(
    navController: NavController,
    onComplete: (String, List<String>) -> Unit
) {
    var pathName by remember { mutableStateOf("") }
    var checkpoints by remember { mutableStateOf(listOf("Checkpoint 1", "Checkpoint 2")) }

    ShoshinTheme(type = ShoshinThemeType.DYNAMIC) {
        Scaffold(
            topBar = {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .statusBarsPadding()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(painterResource(R.drawable.ic_arrow_left), null, tint = MaterialTheme.colorScheme.onBackground)
                    }
                    Text("Change goal", style = ShLabelStyle.copy(color = MaterialTheme.colorScheme.onSurfaceVariant, fontWeight = FontWeight.SemiBold))
                }
            },
            containerColor = MaterialTheme.colorScheme.background,
            bottomBar = {
                Box(modifier = Modifier.padding(24.dp).navigationBarsPadding()) {
                    ShoshinButton(
                        onClick = {
                            if (pathName.isNotEmpty()) onComplete(pathName, checkpoints)
                        },
                        variant = ShButtonVariant.Accent,
                        modifier = Modifier.fillMaxWidth().alpha(if (pathName.isEmpty()) 0.5f else 1f),
                        enabled = pathName.isNotEmpty(),
                        trailingIcon = {
                            Icon(painterResource(R.drawable.ic_arrow_right), null, modifier = Modifier.size(18.dp))
                        }
                    ) {
                        Text("Set this path", style = ShButtonStyle.copy(color = Color.White))
                    }
                }
            }
        ) { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 24.dp)
            ) {
                Spacer(Modifier.height(32.dp))
                
                Kicker("A BEGINNING · 2 OF 2", color = MaterialTheme.colorScheme.onSurfaceVariant)
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Text(
                    text = "Build your own path",
                    style = ShTitleStyle.copy(fontSize = 32.sp),
                    color = MaterialTheme.colorScheme.onBackground
                )
                
                Spacer(modifier = Modifier.height(12.dp))
                
                Text(
                    text = "Name it, then add each checkpoint. We'll pause 5 minutes between one checkpoint and the next.",
                    style = ShBodyStyle.copy(fontSize = 15.sp, color = MaterialTheme.colorScheme.onSurfaceVariant),
                    lineHeight = 22.sp
                )

                Spacer(modifier = Modifier.height(40.dp))

                Kicker("PATH NAME", color = MaterialTheme.colorScheme.onSurfaceVariant)
                Spacer(modifier = Modifier.height(10.dp))
                
                OutlinedTextField(
                    value = pathName,
                    onValueChange = { pathName = it },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("e.g. Early Rise", color = MaterialTheme.colorScheme.onSurfaceVariant) },
                    shape = RoundedCornerShape(20.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        cursorColor = MaterialTheme.colorScheme.primary,
                        unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                        focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                )

                Spacer(modifier = Modifier.height(32.dp))

                Kicker("CHECKPOINTS", color = MaterialTheme.colorScheme.onSurfaceVariant)
                Spacer(modifier = Modifier.height(12.dp))

                checkpoints.forEachIndexed { index, checkpoint ->
                    CustomCheckpointRow(label = checkpoint, icon = if (index == 0) R.drawable.ic_clock else R.drawable.ic_droplet)
                    Spacer(modifier = Modifier.height(10.dp))
                }

                // Add Checkpoint Button
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(64.dp)
                        .clip(RoundedCornerShape(20.dp))
                        .border(1.2.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(20.dp))
                        .clickable { /* Add checkpoint logic */ }
                        .padding(horizontal = 18.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(painterResource(R.drawable.ic_plus), null, modifier = Modifier.size(16.dp), tint = MaterialTheme.colorScheme.onSurface)
                        Spacer(Modifier.width(8.dp))
                        Text("Add checkpoint", style = ShLabelStyle.copy(color = MaterialTheme.colorScheme.onSurface, fontWeight = FontWeight.Bold))
                    }
                }
                
                Spacer(modifier = Modifier.height(40.dp))
            }
        }
    }
}

@Composable
private fun CustomCheckpointRow(label: String, icon: Int) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .padding(18.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.05f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(painterResource(icon), null, modifier = Modifier.size(20.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Spacer(Modifier.width(16.dp))
            Text(label, style = ShH2Style.copy(fontSize = 16.sp, color = MaterialTheme.colorScheme.onSurface))
        }
    }
}
