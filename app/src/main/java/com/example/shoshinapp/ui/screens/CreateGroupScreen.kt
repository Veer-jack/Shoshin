package com.example.shoshinapp.ui.screens

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
import com.example.shoshinapp.ui.components.ShoshinButton
import com.example.shoshinapp.ui.components.ShoshinTextField
import com.example.shoshinapp.ui.theme.*
import com.example.shoshinapp.viewmodel.GroupViewModel

@Composable
fun CreateGroupScreen(navController: NavController, viewModel: GroupViewModel = viewModel()) {
    var name by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Paper)
            .verticalScroll(rememberScrollState())
            .padding(24.dp)
    ) {
        Text("Create Group", style = MaterialTheme.typography.displayMedium, color = Ink)
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

        ShoshinButton(
            onClick = {
                viewModel.createGroup(name, description)
                navController.popBackStack()
            },
            enabled = name.isNotEmpty() && !isLoading
        ) {
            Text("Create")
        }
    }
}
