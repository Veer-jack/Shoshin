package com.Shoshin.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.ui.graphics.Color
import com.Shoshin.app.R
import com.Shoshin.app.ui.components.ShoshinButton
import com.Shoshin.app.ui.components.ShButtonVariant
import com.Shoshin.app.ui.components.ShoshinTextField
import com.Shoshin.app.ui.theme.*
import com.Shoshin.app.viewmodel.GroupViewModel

@Composable
fun CreateGroupScreen(navController: NavController, viewModel: GroupViewModel = viewModel()) {
    var name by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()
    val success by viewModel.creationSuccess.collectAsState()

    // Pop on success
    LaunchedEffect(success) {
        if (success) {
            viewModel.resetCreationState()
            navController.popBackStack()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
    ) {
        // App Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(horizontal = 16.dp, vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { navController.popBackStack() }) {
                Icon(
                    painter = painterResource(R.drawable.ic_arrow_left),
                    contentDescription = "Back",
                    tint = ShInk
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text("Create Circle", style = ShTitleStyle.copy(fontSize = 28.sp), color = ShInk)
        }

        Column(modifier = Modifier.padding(horizontal = 24.dp)) {
            Spacer(modifier = Modifier.height(16.dp))
            ShoshinTextField(
                value = name,
                onValueChange = { name = it },
                label = "Circle Name",
                placeholder = "e.g., Morning Warriors"
            )
            Spacer(modifier = Modifier.height(24.dp))

            ShoshinTextField(
                value = description,
                onValueChange = { description = it },
                label = "Description",
                placeholder = "What is this circle about?",
                modifier = Modifier.height(120.dp)
            )
            Spacer(modifier = Modifier.height(32.dp))

            if (error != null) {
                Text(error!!, color = ShVermillion, fontSize = 12.sp)
                Spacer(modifier = Modifier.height(16.dp))
            }

            val interactionSource = remember { MutableInteractionSource() }

            ShoshinButton(
                onClick = {
                    viewModel.createGroup(name, description)
                },
                variant = ShButtonVariant.Accent,
                enabled = name.isNotEmpty() && !isLoading,
                modifier = Modifier.fillMaxWidth(),
                interactionSource = interactionSource,
                pressedColor = Color.Black
            ) {
                Text(
                    text = if (isLoading) "Creating..." else "Create Circle",
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
