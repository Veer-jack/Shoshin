package com.example.shoshinapp.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.shoshinapp.ui.components.*
import com.example.shoshinapp.ui.theme.*

@Composable
fun PhoneAuthScreen(navController: NavController, mode: String = "signup") {
    var phone by remember { mutableStateOf("") }
    var error by remember { mutableStateOf("") }

    fun onSend() {
        val cleaned = phone.replace(" ", "")
        if (cleaned.length < 10) { error = "Enter a valid phone number"; return }
        val full = if (cleaned.startsWith("+")) cleaned else "+91$cleaned"
        navController.navigate("otp_verify/$full/$mode")
    }

    Column(modifier = Modifier.fillMaxSize().background(Paper)) {
        // Back button
        TextButton(onClick = { navController.popBackStack() }) {
            Text("← Back", color = Fog, fontFamily = DMSans)
        }

        Column(modifier = Modifier.weight(1f).padding(horizontal = 24.dp)) {
            Spacer(Modifier.height(16.dp))
            Kicker(if (mode == "forgot") "Reset Password" else "Verify Phone", color = Vermillion)
            Spacer(Modifier.height(10.dp))
            Text(
                if (mode == "forgot") "Enter your\nphone number" else "What's your\nnumber?",
                fontSize = 34.sp, fontWeight = FontWeight.SemiBold, fontFamily = CormorantGaramond, color = Ink
            )
            Spacer(Modifier.height(10.dp))
            Text(
                if (mode == "forgot") "We'll send a code to reset your password." else "We'll send a one-time code to verify.",
                fontSize = 15.sp, color = Fog, fontFamily = DMSans, lineHeight = 22.sp
            )
            Spacer(Modifier.height(28.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(10.dp), verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier.height(52.dp).wrapContentWidth()
                        .background(Surface, RoundedCornerShape(14.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Text("+91", fontSize = 16.sp, fontWeight = FontWeight.SemiBold, fontFamily = DMSans, modifier = Modifier.padding(horizontal = 14.dp))
                }
                OutlinedTextField(
                    value = phone, onValueChange = { phone = it },
                    placeholder = { Text("98765 43210", color = Fog2) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(14.dp),
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Ink, unfocusedBorderColor = Line)
                )
            }
            if (error.isNotEmpty()) { Spacer(Modifier.height(10.dp)); Text(error, fontSize = 13.sp, color = Vermillion, fontFamily = DMSans) }
            Spacer(Modifier.height(16.dp))
        }

        Column(modifier = Modifier.padding(24.dp)) {
            ShoshinButton(onClick = ::onSend) {
                Text("Send OTP")
            }
        }
    }
}
