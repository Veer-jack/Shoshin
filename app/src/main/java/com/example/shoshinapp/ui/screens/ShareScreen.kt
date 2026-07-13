package com.example.shoshinapp.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.*
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.shoshinapp.R
import com.example.shoshinapp.ui.components.*
import com.example.shoshinapp.ui.theme.*
import com.example.shoshinapp.viewmodel.ShareViewModel

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ShareScreen(
    navController: NavController,
    viewModel: ShareViewModel,
    streak: Int,
    habitName: String,
    startDate: Long
) {
    val bitmap by viewModel.shareBitmap.collectAsState()
    val description by viewModel.customDescription.collectAsState()
    val scrollState = rememberScrollState()

    LaunchedEffect(streak, habitName, startDate, description) {
        viewModel.generatePreview(streak, habitName, startDate)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(ShPaper)
            .verticalScroll(scrollState)
            .padding(horizontal = 24.dp, vertical = 16.dp)
    ) {
        // App Bar
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            IconButton(onClick = { navController.popBackStack() }) {
                Icon(painterResource(R.drawable.ic_arrow_left), contentDescription = "Back")
            }
            Text("Share Achievement", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            Spacer(Modifier.width(48.dp))
        }

        Spacer(Modifier.height(24.dp))

        // Card Preview
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(4f / 5f)
                .clip(RoundedCornerShape(24.dp))
                .background(ShSand),
            contentAlignment = Alignment.Center
        ) {
            bitmap?.let {
                Image(
                    bitmap = it.asImageBitmap(),
                    contentDescription = "Share Card Preview",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Fit
                )
            } ?: CircularProgressIndicator(color = ShVermillion)
        }

        Spacer(Modifier.height(24.dp))

        // Custom Description
        Text("Add a custom message", style = ShLabelStyle, color = ShFog)
        Spacer(Modifier.height(8.dp))
        OutlinedTextField(
            value = description,
            onValueChange = { viewModel.updateDescription(it) },
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("How does it feel to keep the morning?") },
            maxLines = 3,
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White,
                focusedIndicatorColor = ShVermillion
            )
        )
        Text(
            text = "${description.length}/250",
            style = ShLabelStyle,
            color = if (description.length > 240) ShError else ShFog,
            modifier = Modifier.align(Alignment.End).padding(top = 4.dp)
        )

        Spacer(Modifier.height(32.dp))

        // Platforms
        Kicker("Choose Platform")
        Spacer(Modifier.height(16.dp))
        
        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            PlatformButton("Instagram", Icons.Default.CameraAlt) { viewModel.shareToPlatform("Instagram", streak) }
            PlatformButton("Twitter/X", Icons.AutoMirrored.Filled.Message) { viewModel.shareToPlatform("Twitter", streak) }
            PlatformButton("Facebook", Icons.Default.Facebook) { viewModel.shareToPlatform("Facebook", streak) }
            PlatformButton("WhatsApp", Icons.AutoMirrored.Filled.Chat) { viewModel.shareToPlatform("WhatsApp", streak) }
        }

        Spacer(Modifier.height(24.dp))

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            ShoshinButton(
                onClick = { viewModel.saveToGallery() },
                variant = ShButtonVariant.Dark,
                modifier = Modifier.weight(1f)
            ) {
                Icon(Icons.Default.Download, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(8.dp))
                Text("Save Image")
            }
            
            ShoshinButton(
                onClick = { viewModel.shareToPlatform("Generic", streak) },
                variant = ShButtonVariant.Accent,
                modifier = Modifier.weight(1f)
            ) {
                Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(8.dp))
                Text("Other Apps")
            }
        }

        Spacer(Modifier.height(48.dp))
    }
}

@Composable
private fun PlatformButton(label: String, icon: androidx.compose.ui.graphics.vector.ImageVector, onClick: () -> Unit) {
    InputChip(
        selected = false,
        onClick = onClick,
        label = { Text(label) },
        leadingIcon = { Icon(icon, contentDescription = null, modifier = Modifier.size(18.dp)) },
        colors = InputChipDefaults.inputChipColors(
            containerColor = ShSand,
            labelColor = ShInk
        ),
        border = null
    )
}
