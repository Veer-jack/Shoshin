package com.Shoshin.app.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.Shoshin.app.R
import com.Shoshin.app.ui.components.*
import com.Shoshin.app.ui.theme.*
import com.Shoshin.app.viewmodel.OnboardingViewModel

@Composable
fun OnboardingScreen(
    viewModel: OnboardingViewModel,
    onComplete: () -> Unit
) {
    var currentStep by remember { mutableIntStateOf(1) }

    ShoshinTheme(type = ShoshinThemeType.ALWAYS_LIGHT) {
        Scaffold(
            topBar = {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .statusBarsPadding()
                        .padding(horizontal = 24.dp, vertical = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Shoshin",
                        style = ShTitleStyle.copy(fontSize = 20.sp, color = MaterialTheme.colorScheme.onBackground)
                    )
                    Text(
                        text = "Skip",
                        style = ShLabelStyle.copy(color = MaterialTheme.colorScheme.onSurfaceVariant, fontWeight = FontWeight.Bold),
                        modifier = Modifier.clickable { 
                            viewModel.skipOnboarding()
                            onComplete()
                        }
                    )
                }
            },
            containerColor = MaterialTheme.colorScheme.background,
            bottomBar = {
                Column(modifier = Modifier.padding(24.dp).navigationBarsPadding()) {
                    // Segmented Progress Bar
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(bottom = 24.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        repeat(3) { i ->
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(3.dp)
                                    .clip(RoundedCornerShape(2.dp))
                                    .background(if (currentStep >= i + 1) MaterialTheme.colorScheme.onBackground else MaterialTheme.colorScheme.outline)
                            )
                        }
                    }

                    ShoshinButton(
                        onClick = {
                            if (currentStep < 3) {
                                currentStep++
                            } else {
                                viewModel.completeOnboarding("06:00", "22:00")
                                onComplete()
                            }
                        },
                        variant = if (currentStep == 3) ShButtonVariant.Accent else ShButtonVariant.Ghost, 
                        modifier = Modifier.fillMaxWidth(),
                        trailingIcon = if (currentStep == 3) {
                            { Icon(painterResource(R.drawable.ic_arrow_right), null, modifier = Modifier.size(18.dp)) }
                        } else null
                    ) {
                        Text(
                            text = if (currentStep == 3) "Choose your path" else "Continue",
                            fontWeight = FontWeight.Bold,
                            color = if (currentStep == 3) Color.White else Color.Black
                        )
                    }
                    
                    Spacer(Modifier.height(16.dp))
                    
                    Text(
                        text = "Pick up your beginners mind",
                        style = ShLabelStyle.copy(fontSize = 12.sp),
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        ) { padding ->
            Box(modifier = Modifier.padding(padding).fillMaxSize()) {
                AnimatedContent(
                    targetState = currentStep,
                    transitionSpec = {
                        fadeIn() togetherWith fadeOut()
                    },
                    label = "step_transition"
                ) { step ->
                    when (step) {
                        1 -> OnboardingSlide(
                            kicker = "THE PROBLEM",
                            title = "Mornings decide\nthe day",
                            body = "Most routines die in the ten quiet minutes after the alarm — half-asleep, the old self wins.",
                            illustration = {
                                Box(modifier = Modifier.size(240.dp), contentAlignment = Alignment.Center) {
                                    Enso(size = 200, color = MaterialTheme.colorScheme.outline, strokeWidth = 4f)
                                    Icon(
                                        painter = painterResource(R.drawable.ic_clock),
                                        contentDescription = null,
                                        modifier = Modifier.size(80.dp),
                                        tint = MaterialTheme.colorScheme.onBackground
                                    )
                                }
                            }
                        )
                        2 -> OnboardingSlide(
                            kicker = "THE METHOD",
                            title = "Cross the\nbridge",
                            body = "Shoshin walks you from alarm to action through small checkpoints. One step, then the next.",
                            illustration = {
                                Box(modifier = Modifier.size(240.dp), contentAlignment = Alignment.Center) {
                                    // Bridge Arc
                                    androidx.compose.foundation.Canvas(modifier = Modifier.size(180.dp, 80.dp)) {
                                        drawArc(
                                            color = Color.White,
                                            startAngle = 180f,
                                            sweepAngle = 180f,
                                            useCenter = false,
                                            style = androidx.compose.ui.graphics.drawscope.Stroke(width = 4.dp.toPx(), cap = androidx.compose.ui.graphics.StrokeCap.Round)
                                        )
                                    }
                                    // Checkpoints (Dots)
                                    Box(modifier = Modifier.size(200.dp)) {
                                        Box(modifier = Modifier.size(16.dp).clip(CircleShape).background(ShMatcha).align(Alignment.BottomStart).offset(x = 10.dp, y = (-20).dp))
                                        Box(modifier = Modifier.size(16.dp).clip(CircleShape).background(ShMatchaDark).align(Alignment.TopCenter).offset(x = (-40).dp, y = 40.dp))
                                        Box(modifier = Modifier.size(16.dp).clip(CircleShape).background(ShVermillion).align(Alignment.TopCenter).offset(y = 20.dp))
                                        Box(modifier = Modifier.size(16.dp).clip(CircleShape).border(2.dp, ShNightLine, CircleShape).align(Alignment.TopCenter).offset(x = 40.dp, y = 40.dp))
                                    }
                                }
                            }
                        )
                        3 -> OnboardingSlide(
                            kicker = "THE PAYOFF",
                            title = "Become who\nyou return as",
                            body = "Each morning kept is a vote for the person you're practicing to be. Begin again, every day.",
                            illustration = {
                                Box(modifier = Modifier.size(240.dp), contentAlignment = Alignment.Center) {
                                    Enso(size = 200, color = Color.White, strokeWidth = 8f)
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun OnboardingSlide(
    kicker: String,
    title: String,
    body: String,
    illustration: @Composable () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.Start
    ) {
        Spacer(Modifier.height(40.dp))
        
        Box(
            modifier = Modifier.fillMaxWidth().height(240.dp),
            contentAlignment = Alignment.Center
        ) {
            illustration()
        }
        
        Spacer(Modifier.height(48.dp))
        
        Kicker(text = kicker, color = MaterialTheme.colorScheme.primary)
        
        Spacer(Modifier.height(16.dp))
        
        Text(
            text = title,
            style = ShTitleStyle.copy(fontSize = 40.sp, lineHeight = 46.sp),
            color = MaterialTheme.colorScheme.onBackground
        )
        
        Spacer(Modifier.height(20.dp))
        
        Text(
            text = body,
            style = ShBodyStyle.copy(fontSize = 17.sp, lineHeight = 28.sp),
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
