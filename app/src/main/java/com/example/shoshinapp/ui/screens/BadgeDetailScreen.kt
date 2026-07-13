package com.example.shoshinapp.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.shoshinapp.R
import com.example.shoshinapp.ui.components.*
import com.example.shoshinapp.ui.theme.*
import com.example.shoshinapp.viewmodel.BadgeViewModel
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun BadgeDetailScreen(
    navController: NavController,
    viewModel: BadgeViewModel,
    badgeId: String
) {
    val badges by viewModel.badges.collectAsState()
    val badge = badges.find { it.id == badgeId } ?: return

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(ShPaper)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp)
    ) {
        // App Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 24.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { navController.popBackStack() }) {
                Icon(painterResource(R.drawable.ic_arrow_left), contentDescription = "Back")
            }
            Spacer(Modifier.width(16.dp))
            Text("Badge Details", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
        }

        Spacer(Modifier.height(32.dp))

        // Large Badge Icon
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(160.dp)
                    .background(Color.parseColor(badge.color).copy(alpha = 0.15f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(badge.icon, fontSize = 80.sp)
            }
            
            Spacer(Modifier.height(24.dp))
            
            Text(
                text = badge.name.uppercase(),
                style = ShTitleStyle,
                color = ShInk
            )
            
            Kicker(badge.category.name, color = ShFog)
        }

        Spacer(Modifier.height(40.dp))

        // Description Card
        ShoshinCard(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(24.dp)) {
                Text(
                    text = badge.description,
                    style = ShBodyStyle,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
                
                Spacer(Modifier.height(24.dp))
                
                DetailItem(label = "Requirement", value = badge.requirementDescription)
                
                if (!badge.isLocked) {
                    HorizontalDivider(color = ShLine, modifier = Modifier.padding(vertical = 12.dp))
                    DetailItem(
                        label = "Earned on", 
                        value = badge.unlockedDate?.let { SimpleDateFormat("MMM d, yyyy", Locale.getDefault()).format(Date(it)) } ?: "Unknown"
                    )
                }
                
                HorizontalDivider(color = ShLine, modifier = Modifier.padding(vertical = 12.dp))
                DetailItem(label = "Rarity", value = badge.rarity.name.lowercase().replaceFirstChar { it.uppercase() })
            }
        }

        Spacer(Modifier.height(32.dp))

        if (!badge.isLocked) {
            ShoshinButton(
                onClick = { /* Share Badge logic would go here */ },
                variant = ShButtonVariant.Accent
            ) {
                Text("Share This Achievement")
            }
        } else {
            // Progress if locked
            val progress = badge.currentProgress.toFloat() / badge.threshold
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                LinearProgressIndicator(
                    progress = { progress.coerceIn(0f, 1f) },
                    modifier = Modifier.fillMaxWidth().height(8.dp).clip(CircleShape),
                    color = ShVermillion,
                    trackColor = ShSand
                )
                Spacer(Modifier.height(8.dp))
                Text(
                    text = "${(progress * 100).toInt()}% towards this badge",
                    style = ShLabelStyle,
                    color = ShFog
                )
            }
        }

        Spacer(Modifier.height(48.dp))
    }
}

@Composable
private fun DetailItem(label: String, value: String) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(label, style = ShLabelStyle, color = ShFog)
        Text(value, style = ShBodyStyle, fontWeight = FontWeight.SemiBold, color = ShInk)
    }
}

private fun Color.Companion.parseColor(colorString: String): Color {
    return Color(android.graphics.Color.parseColor(colorString))
}
