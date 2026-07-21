package com.example.shoshinapp.ui.screens

import androidx.compose.foundation.background
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
fun RoutineEditorScreen(navController: NavController) {
    val scrollState = rememberScrollState()
    var pathName by remember { mutableStateOf("Morning Walk") }
    
    val steps = listOf(
        Pair(R.drawable.ic_brain, "Mind awake"),
        Pair(R.drawable.ic_droplet, "Freshen up"),
        Pair(R.drawable.ic_shirt, "Dressed"),
        Pair(R.drawable.ic_sun, "Out the door"),
        Pair(R.drawable.ic_walk, "Walk begun")
    )
    val verify = listOf("Tap", "Photo", "Photo", "Photo + GPS", "Photo + GPS")

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
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("Edit path", style = ShTitleStyle.copy(fontSize = 28.sp), color = MaterialTheme.colorScheme.onBackground)
            ShoshinPill(label = "Active", variant = ShPillVariant.Accent)
        }

        // Path Name
        Kicker("Path name", modifier = Modifier.padding(bottom = 10.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .clip(RoundedCornerShape(14.dp))
                .background(MaterialTheme.colorScheme.surface)
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(pathName, fontSize = 16.sp, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onSurface)
            Icon(painterResource(R.drawable.ic_edit), null, modifier = Modifier.size(19.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
        }

        Spacer(Modifier.height(22.dp))
        Kicker("Checkpoints · drag to reorder", modifier = Modifier.padding(bottom = 14.dp))

        steps.forEachIndexed { i, step ->
            ShoshinCard(modifier = Modifier.fillMaxWidth().padding(bottom = 10.dp)) {
                Row(
                    modifier = Modifier.padding(14.dp, 16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Drag Handle
                    Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
                        repeat(3) {
                            Row(horizontalArrangement = Arrangement.spacedBy(3.dp)) {
                                repeat(2) {
                                    Box(modifier = Modifier.size(3.dp).background(MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.3f), CircleShape))
                                }
                            }
                        }
                    }

                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(RoundedCornerShape(11.dp))
                            .background(MaterialTheme.colorScheme.surfaceVariant),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(painterResource(step.first), null, modifier = Modifier.size(19.dp), tint = MaterialTheme.colorScheme.onSurface)
                    }

                    Column(modifier = Modifier.weight(1f)) {
                        Text(step.second, fontSize = 15.5.sp, fontWeight = FontWeight.Medium, color = MaterialTheme.colorScheme.onSurface)
                        Text(verify[i], fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }

                    Icon(painterResource(R.drawable.ic_arrow_right), null, modifier = Modifier.size(18.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f))
                }
            }
        }

        Spacer(Modifier.height(14.dp))

        ShoshinButton(
            onClick = { /* Add checkpoint */ },
            variant = ShButtonVariant.Ghost,
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(painterResource(R.drawable.ic_plus), null, modifier = Modifier.size(19.dp))
            Spacer(Modifier.width(8.dp))
            Text("Add checkpoint")
        }

        Spacer(Modifier.height(24.dp))

        ShoshinButton(
            onClick = { navController.popBackStack() },
            variant = ShButtonVariant.Accent,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Save path")
        }

        Spacer(Modifier.height(48.dp))
    }
}
