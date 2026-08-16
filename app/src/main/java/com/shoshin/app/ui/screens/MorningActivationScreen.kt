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
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import com.Shoshin.app.R
import com.Shoshin.app.ui.components.*
import com.Shoshin.app.ui.theme.*
import com.Shoshin.app.utils.CheckpointNudge
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import java.util.*
import java.text.SimpleDateFormat
import kotlin.random.Random

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

// One pool per step, so a step can be re-rolled with a problem of the same kind.
private val problemPools = listOf(additionPool, subtractionPool, multiplicationPool)

private const val MAX_ATTEMPTS = 3

// Seeded so the same three problems come back after an activity recreation.
private fun generateProblems(random: Random): List<Problem> = problemPools.map { it.random(random) }

/** A different problem from the same pool as [current] — never repeats the one just failed. */
private fun rerollProblem(step: Int, current: Problem): Problem {
    val pool = problemPools[step]
    return pool.filter { it != current }.randomOrNull() ?: current
}

@Composable
fun MorningActivationScreen(onBegin: () -> Unit) {
    // Survives activity recreation, so a rotation mid-challenge doesn't reset progress.
    val seed  = rememberSaveable { System.currentTimeMillis() }
    val problems = remember(seed) { generateProblems(Random(seed)).toMutableStateList() }
    var step  by rememberSaveable { mutableIntStateOf(0) }
    var entry by rememberSaveable { mutableStateOf("") }
    var error by rememberSaveable { mutableStateOf(false) }
    var attempts by rememberSaveable { mutableIntStateOf(0) }
    val prob  = problems[step]

    val context = LocalContext.current
    var showNudgeBanner by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        var secondsUntilNudge = CheckpointNudge.INTERVAL_SECONDS
        while (isActive) {
            delay(1000)
            secondsUntilNudge -= 1
            if (secondsUntilNudge <= 0) {
                showNudgeBanner = true
                CheckpointNudge.ringAndVibrate(context)
                secondsUntilNudge = CheckpointNudge.INTERVAL_SECONDS
            }
        }
    }
    LaunchedEffect(showNudgeBanner) {
        if (showNudgeBanner) {
            delay(4000)
            showNudgeBanner = false
        }
    }

    val calendar = Calendar.getInstance()
    val timeStr = SimpleDateFormat("HH:mm", Locale.getDefault()).format(calendar.time)

    fun press(k: String) {
        val targetLen = prob.answer.toString().length
        when (k) {
            "del" -> { entry = entry.dropLast(1); error = false }
            "ok"  -> {
                if (entry.toIntOrNull() == prob.answer) {
                    if (step < problems.lastIndex) { step++; entry = ""; error = false; attempts = 0 }
                    else onBegin()
                } else { attempts++; error = true; entry = "" }
            }
            else  -> if (entry.length < targetLen) { entry += k; error = false }
        }
    }

    ShoshinTheme(type = ShoshinThemeType.ALWAYS_DARK) {
        Box(modifier = Modifier.fillMaxSize().background(ShNight)) {
            if (error) {
                WrongAnswerUI(
                    onRetry = {
                        error = false
                        // Never dead-end an alarm: after the third miss, swap in a fresh
                        // problem of the same kind and start the attempt count over.
                        if (attempts >= MAX_ATTEMPTS) {
                            problems[step] = rerollProblem(step, prob)
                            attempts = 0
                        }
                    },
                    time = timeStr,
                    attempts = attempts,
                    maxAttempts = MAX_ATTEMPTS
                )
            } else {
                Column(modifier = Modifier.fillMaxSize().statusBarsPadding()) {
                    if (showNudgeBanner) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 24.dp)
                                .clip(RoundedCornerShape(16.dp))
                                .background(ShAmberNight.copy(alpha = 0.15f))
                                .padding(14.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                "Take your time. Solve it whenever you're ready.",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = ShAmberNight,
                                textAlign = TextAlign.Center
                            )
                        }
                        Spacer(Modifier.height(12.dp))
                    }
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
private fun WrongAnswerUI(
    onRetry: () -> Unit,
    time: String,
    attempts: Int,
    maxAttempts: Int
) {
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
            Icon(painterResource(R.drawable.ic_info), null, tint = ShVermillion, modifier = Modifier.size(32.dp))
        }

        Spacer(Modifier.height(32.dp))
        Kicker("NOT QUITE", color = ShVermillion)
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
            repeat(maxAttempts) { index ->
                Box(
                    modifier = Modifier
                        .size(10.dp)
                        .clip(CircleShape)
                        .background(if (index < attempts) ShVermillion else ShNight3)
                )
            }
        }
        Spacer(Modifier.height(8.dp))
        Text("$attempts of $maxAttempts attempts used", style = ShLabelStyle, color = ShNightMuted)

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
