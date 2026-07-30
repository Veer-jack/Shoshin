package com.Shoshin.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.res.painterResource
import com.Shoshin.app.R
import com.Shoshin.app.ui.components.*
import com.Shoshin.app.ui.theme.*
import java.util.*
import java.text.SimpleDateFormat

private data class Problem(val question: String, val answer: Int)

private val additionPool = listOf(
    Problem("12 + 34", 46), Problem("45 + 23", 68), Problem("56 + 21", 77), 
    Problem("78 + 15", 93), Problem("22 + 55", 77), Problem("31 + 19", 50),
    Problem("64 + 28", 92), Problem("17 + 82", 99)
)

private val subtractionPool = listOf(
    Problem("45 - 23", 22), Problem("67 - 28", 39), Problem("89 - 41", 48),
    Problem("92 - 35", 57), Problem("54 - 17", 37), Problem("71 - 22", 49),
    Problem("60 - 15", 45), Problem("83 - 29", 54)
)

private val multiplicationPool = listOf(
    Problem("6 × 7", 42), Problem("8 × 9", 72), Problem("4 × 12", 48),
    Problem("11 × 5", 55), Problem("7 × 8", 56), Problem("9 × 6", 54),
    Problem("13 × 3", 39), Problem("15 × 2", 30)
)

private fun generateProblems(): List<Problem> {
    return listOf(
        additionPool.random(),
        subtractionPool.random(),
        multiplicationPool.random()
    )
}

@Composable
fun MorningActivationScreen(onBegin: () -> Unit) {
    val problems = remember { generateProblems() }
    var step  by remember { mutableIntStateOf(0) }
    var entry by remember { mutableStateOf("") }
    var error by remember { mutableStateOf(false) }
    val prob  = problems[step]

    val calendar = Calendar.getInstance()
    val timeStr = SimpleDateFormat("HH:mm", Locale.getDefault()).format(calendar.time)

    fun press(k: String) {
        val targetLen = prob.answer.toString().length
        when (k) {
            "del" -> { entry = entry.dropLast(1); error = false }
            "ok"  -> {
                if (entry.toIntOrNull() == prob.answer) {
                    if (step < problems.lastIndex) { step++; entry = ""; error = false }
                    else onBegin()
                } else { error = true; entry = "" }
            }
            else  -> if (entry.length < targetLen) { entry += k; error = false }
        }
    }

    ShoshinTheme(type = ShoshinThemeType.ALWAYS_DARK) {
        Box(modifier = Modifier.fillMaxSize().background(ShNight)) {
            if (error) {
                WrongAnswerUI(onRetry = { error = false }, time = timeStr)
            } else {
                Column(modifier = Modifier.fillMaxSize().statusBarsPadding()) {
                    Column(modifier = Modifier.fillMaxWidth().padding(top = 12.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(timeStr, fontSize = 48.sp, fontWeight = FontWeight.Bold, fontFamily = DmSansFamily, color = Color.White)
                    }

                    Column(modifier = Modifier.weight(1f), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
                        Box(modifier = Modifier.clip(RoundedCornerShape(999.dp)).background(ShNight2).padding(horizontal = 16.dp, vertical = 7.dp).padding(bottom = 16.dp)) {
                            Text("MIND AWAKE · ${step+1} OF ${problems.size}", fontSize = 11.sp, fontWeight = FontWeight.SemiBold, fontFamily = DmSansFamily, color = ShNightMuted)
                        }
                        Kicker("Solve to begin", color = ShVermillionLight)
                        Spacer(Modifier.height(14.dp))
                        Text(prob.question, fontSize = 56.sp, fontWeight = FontWeight.SemiBold, fontFamily = CormorantFamily, color = Color.White)
                        Spacer(Modifier.height(20.dp))
                        
                        val targetLen = prob.answer.toString().length
                        ShoshinOtpBoxes(value = entry, length = targetLen, dark = true, modifier = Modifier.padding(horizontal = 40.dp))
                    }

                    ShoshinKeypad(onDigit = { press(it) }, onClear = { press("del") }, onOk = { press("ok") }, modifier = Modifier.padding(24.dp))
                    Text("Snooze rests until your mind wakes.", fontSize = 13.sp, color = ShNightMuted, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp))
                }
            }
        }
    }
}

@Composable
private fun WrongAnswerUI(onRetry: () -> Unit, time: String) {
    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(time, style = ShNumeralStyle.copy(fontSize = 56.sp, color = Color.White), modifier = Modifier.padding(top = 12.dp))
        
        Spacer(Modifier.weight(1f))

        Box(
            modifier = Modifier.size(80.dp).clip(CircleShape).background(ShNight2),
            contentAlignment = Alignment.Center
        ) {
            Icon(painterResource(R.drawable.ic_info), null, tint = ShVermillionLight, modifier = Modifier.size(32.dp))
        }

        Spacer(Modifier.height(32.dp))
        Kicker("NOT QUITE", color = ShVermillionLight)
        Spacer(Modifier.height(12.dp))
        Text("Breathe. Look again.", style = ShTitleStyle.copy(fontSize = 32.sp, color = Color.White))
        Spacer(Modifier.height(16.dp))
        Text(
            "A wrong answer wakes the mind faster than a right one. No penalty — just try once more.",
            style = ShBodyStyle,
            color = ShNightMuted,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 24.dp)
        )

        Spacer(Modifier.height(32.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Box(modifier = Modifier.size(10.dp).clip(CircleShape).background(ShVermillionLight))
            Box(modifier = Modifier.size(10.dp).clip(CircleShape).background(ShNight3))
            Box(modifier = Modifier.size(10.dp).clip(CircleShape).background(ShNight3))
        }
        Spacer(Modifier.height(8.dp))
        Text("1 of 3 attempts used", style = ShLabelStyle, color = ShNightMuted)

        Spacer(Modifier.weight(1f))

        ShoshinButton(
            onClick = onRetry,
            variant = ShButtonVariant.Accent,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Try again", fontWeight = FontWeight.Bold)
        }

        Spacer(Modifier.height(24.dp))
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Center) {
            Icon(painterResource(R.drawable.ic_lock), null, tint = ShNightMuted, modifier = Modifier.size(14.dp))
            Spacer(Modifier.width(8.dp))
            Text("Snooze rests until your mind wakes.", style = ShLabelStyle, color = ShNightMuted)
        }
        Spacer(Modifier.height(12.dp))
    }
}
