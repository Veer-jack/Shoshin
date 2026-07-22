package com.shoshin.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.ui.graphics.Color
import com.shoshin.app.ui.components.ShoshinButton
import com.shoshin.app.ui.components.ShButtonVariant
import com.shoshin.app.ui.components.ShoshinTextField
import com.shoshin.app.ui.theme.*
import com.shoshin.app.viewmodel.GroupViewModel

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
            .background(MaterialTheme.colorScheme.background) // Fixed consistency with other screens
            .verticalScroll(rememberScrollState())
            .padding(24.dp)
    ) {
        Text("Create Group", style = ShTitleStyle, color = ShInk)
        Spacer(modifier = Modifier.height(24.dp))

        ShoshinTextField(
            value = name,
            onValueChange = { name = it },
            label = "Group Name",
            placeholder = "e.g., Morning Warriors"
        )
        Spacer(modifier = Modifier.height(16.dp))

        ShoshinTextField(
            value = description,
            onValueChange = { description = it },
            label = "Description",
            placeholder = "What is this group about?",
            modifier = Modifier.height(120.dp)
        )
        Spacer(modifier = Modifier.height(32.dp))

        if (error != null) {
            Text(error!!, color = Vermillion, fontSize = 12.sp)
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
                text = if (isLoading) "Creating..." else "Create",
                color = Color.White
            )
        }
    }
}
