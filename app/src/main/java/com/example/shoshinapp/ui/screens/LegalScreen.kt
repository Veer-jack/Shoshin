package com.example.shoshinapp.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.shoshinapp.R
import com.example.shoshinapp.ui.theme.*
import java.io.InputStream

@Composable
fun LegalScreen(navController: NavController, assetPath: String, title: String) {
    val context = LocalContext.current
    var content by remember { mutableStateOf("Loading...") }

    LaunchedEffect(assetPath) {
        try {
            val inputStream: InputStream = context.assets.open(assetPath)
            content = inputStream.bufferedReader().use { it.readText() }
        } catch (e: Exception) {
            content = "Error loading document."
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(ShPaper)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
        ) {
            IconButton(onClick = { navController.popBackStack() }) {
                Icon(painterResource(R.drawable.ic_arrow_left), contentDescription = "Back", tint = ShInk)
            }
            Spacer(Modifier.width(8.dp))
            Text(title, style = ShH2Style, color = ShInk)
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(24.dp)
        ) {
            Text(
                text = content,
                style = ShLabelStyle.copy(
                    fontSize = 14.sp,
                    lineHeight = 22.sp,
                    color = ShInk
                )
            )
            Spacer(Modifier.height(48.dp))
        }
    }
}
