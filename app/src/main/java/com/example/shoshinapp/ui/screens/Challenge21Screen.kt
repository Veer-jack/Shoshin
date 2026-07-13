package com.example.shoshinapp.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.shoshinapp.ui.components.*
import com.example.shoshinapp.ui.theme.*

@Composable
fun Challenge21Screen(navController: NavController, template: String = "walk") {
    Column(
        modifier = Modifier.fillMaxSize().background(Paper),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(120.dp)
                .background(Matcha, RoundedCornerShape(60.dp))
                .border(3.dp, Matcha, RoundedCornerShape(60.dp)),
            contentAlignment = Alignment.Center
        ) {
            Text("21", fontSize = 48.sp, fontWeight = FontWeight.Bold, color = Paper, fontFamily = CormorantGaramond)
        }

        Spacer(modifier = Modifier.height(24.dp))
        Text("21-Day Challenge", fontSize = 24.sp, fontWeight = FontWeight.SemiBold, fontFamily = CormorantGaramond, color = Ink)
        Text("You've built a habit!", fontSize = 14.sp, color = Fog, fontFamily = DMSans)

        Spacer(modifier = Modifier.height(32.dp))
        ShoshinButton(onClick = { navController.navigate("home") }, modifier = Modifier.padding(24.dp)) {
            Text("Continue")
        }
    }
}
