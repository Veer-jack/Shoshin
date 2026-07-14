package com.example.shoshinapp.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.shoshinapp.ui.components.*
import com.example.shoshinapp.ui.theme.*
import java.util.*
import java.text.SimpleDateFormat

private data class Problem(val question: String, val answer: Int)

private fun generateProblems(): List<Problem> {
    val random = Random()
    return listOf(
        // 1. Addition (Double Digit)
        run {
            val a = 20 + random.nextInt(70)
            val b = 20 + random.nextInt(70)
            Problem("$a + $b", a + b)
        },
        // 2. Subtraction (Result positive)
        run {
            val a = 50 + random.nextInt(49)
            val b = 10 + random.nextInt(40)
            Problem("$a − $b", a - b)
        },
        // 3. Multiplication (Single * Small Double)
        run {
            val a = 3 + random.nextInt(10) // 3 to 12
            val b = 2 + random.nextInt(7)  // 2 to 8
            Problem("$a × $b", a * b)
        }
    )
}

@Composable
fun MorningActivationScreen(onBegin: () -> Unit) {
    val problems = remember { generateProblems() }
    var step  by remember { mutableStateOf(0) }
    var entry by remember { mutableStateOf("") }
    var error by remember { mutableStateOf(false) }
    val prob  = problems[step]

    val calendar = Calendar.getInstance()
    val timeStr = SimpleDateFormat("HH:mm", Locale.getDefault()).format(calendar.time)
    val dateStr = SimpleDateFormat("EEEE, d MMMM", Locale.getDefault()).format(calendar.time)

    fun press(k: String) {
        when (k) {
            "del" -> { entry = entry.dropLast(1); error = false }
            "ok"  -> {
                if (entry.toIntOrNull() == prob.answer) {
                    if (step < problems.lastIndex) { step++; entry = ""; error = false }
                    else onBegin()
                } else { error = true; entry = "" }
            }
            else  -> if (entry.length < 4) { entry += k; error = false }
        }
    }

    ShoshinTheme(darkSurface = true) {
        Box(modifier = Modifier.fillMaxSize().background(ShNight)) {
            // Enso
            Box(modifier = Modifier.size(300.dp).align(Alignment.Center)) {
                androidx.compose.foundation.Canvas(modifier = Modifier.fillMaxSize()) {
                    drawArc(color = ShVermillion.copy(alpha = 0.10f), startAngle = -90f, sweepAngle = 310f, useCenter = false, style = androidx.compose.ui.graphics.drawscope.Stroke(width = 9.dp.toPx(), cap = androidx.compose.ui.graphics.StrokeCap.Round))
                }
            }

            Column(modifier = Modifier.fillMaxSize().systemBarsPadding()) {
                // Clock
                Column(modifier = Modifier.fillMaxWidth().padding(top = 12.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(timeStr, fontSize = 48.sp, fontWeight = FontWeight.Bold, fontFamily = DmSansFamily, color = ShNightText, letterSpacing = 1.sp)
                    Text(dateStr.uppercase(), fontSize = 11.sp, fontWeight = FontWeight.Medium, fontFamily = DmSansFamily, color = ShNightMuted, letterSpacing = 2.sp)
                }

                // Challenge
                Column(modifier = Modifier.weight(1f), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
                    Box(modifier = Modifier.clip(RoundedCornerShape(999.dp)).background(ShNightText.copy(alpha = 0.06f)).padding(horizontal = 16.dp, vertical = 7.dp).padding(bottom = 16.dp)) {
                        Text("MIND AWAKE · ${step+1} OF ${problems.size}", fontSize = 11.sp, fontWeight = FontWeight.SemiBold, fontFamily = DmSansFamily, color = ShNightMuted, letterSpacing = 1.5.sp)
                    }
                    Kicker("Solve to begin", color = ShVermillion); Spacer(Modifier.height(14.dp))
                    Text(prob.question, fontSize = 56.sp, fontWeight = FontWeight.SemiBold, fontFamily = CormorantFamily, color = ShNightText, letterSpacing = (-0.5).sp)
                    Spacer(Modifier.height(20.dp))
                    
                    ShoshinOtpBoxes(value = entry, length = 4, dark = true, modifier = Modifier.padding(horizontal = 40.dp))
                    
                    Spacer(Modifier.height(12.dp))
                    if (error) Text("Not yet. Breathe, look again.", fontSize = 13.sp, fontWeight = FontWeight.Medium, fontFamily = DmSansFamily, color = ShVermillion)
                }

                // Keypad
                ShoshinKeypad(
                    onDigit = { press(it) },
                    onClear = { press("del") },
                    onOk = { press("ok") },
                    modifier = Modifier.padding(24.dp)
                )
                
                Text("Snooze rests until your mind wakes.", fontSize = 13.sp, color = ShNightMuted, fontFamily = DmSansFamily, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp))
            }
        }
    }
}
