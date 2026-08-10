package com.Shoshin.app.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.Shoshin.app.R
import com.Shoshin.app.data.ShoshinRepository
import com.Shoshin.app.navigation.ShRoutes
import com.Shoshin.app.ui.components.*
import com.Shoshin.app.ui.theme.*
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

@Composable
fun SplashScreen(navController: NavController) {
    val context = LocalContext.current
    val repo    = remember { ShoshinRepository(context) }
    val scope   = rememberCoroutineScope()

    // Infinite rotation animation for the Enso circle
    val infiniteTransition = rememberInfiniteTransition(label = "enso_rotation")
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(25000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "rotation"
    )

    fun onBegin() {
        scope.launch {
            val loggedIn      = repo.isLoggedIn.first()
            val onboardingDone = repo.onboardingDone.first()
            
            if (loggedIn && onboardingDone) {
                navController.navigate(ShRoutes.MAIN) {
                    popUpTo(ShRoutes.SPLASH) { inclusive = true }
                }
            } else {
                // New users go to Auth then Onboarding
                navController.navigate(ShRoutes.AUTH) {
                    popUpTo(ShRoutes.SPLASH) { inclusive = true }
                }
            }
        }
    }

    fun onReturn() {
        // Returning users (who have an account) go directly to Auth/Login
        navController.navigate(ShRoutes.AUTH)
    }

    ShoshinTheme(type = ShoshinThemeType.ALWAYS_DARK) {
        Box(
            modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background),
            contentAlignment = Alignment.Center
        ) {
            // Enso circle motif with animation
            val ensoColor = ShVermillion
            Box(
                modifier = Modifier
                    .size(320.dp)
                    .rotate(rotation)
                    .background(Color.Transparent)
            ) {
                androidx.compose.foundation.Canvas(modifier = Modifier.fillMaxSize()) {
                    drawArc(
                        color = ensoColor.copy(alpha = 0.15f),
                        startAngle = -90f,
                        sweepAngle = 310f,
                        useCenter = false,
                        style = androidx.compose.ui.graphics.drawscope.Stroke(
                            width = 6.dp.toPx(),
                            cap = androidx.compose.ui.graphics.StrokeCap.Round
                        )
                    )
                }
            }

            Column(
                modifier = Modifier.fillMaxSize().padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(Modifier.weight(1.2f))

                // Logo mark — using the updated component with enclosure
                ShoshinLogoMark(on = "night", modifier = Modifier.padding(bottom = 24.dp))

                Text(
                    text = "Shoshin",
                    fontSize = 52.sp,
                    fontWeight = FontWeight.SemiBold,
                    fontFamily = CormorantGaramond,
                    color = MaterialTheme.colorScheme.onBackground,
                    letterSpacing = 0.5.sp
                )
                Text(
                    text = "初心",
                    fontSize = 22.sp,
                    fontFamily = NotoSerifJP,
                    color = ShVermillion,
                    letterSpacing = 8.sp,
                    modifier = Modifier.padding(top = 8.dp)
                )
                
                Spacer(Modifier.height(100.dp))

                Text(
                    text = "Beginner's mind.\nEvery morning.",
                    style = ShBodyStyle.copy(
                        fontSize = 18.sp,
                        lineHeight = 24.sp,
                        textAlign = TextAlign.Center,
                        color = Color.White.copy(alpha = 0.8f)
                    ),
                    modifier = Modifier.padding(top = 16.dp)
                )

                Spacer(Modifier.weight(1f))

                val interactionSource = remember { MutableInteractionSource() }

                ShoshinButton(
                    onClick = { onBegin() },
                    variant = ShButtonVariant.Accent,
                    modifier = Modifier.fillMaxWidth().padding(bottom = 20.dp),
                    interactionSource = interactionSource,
                    pressedColor = Color.Black,
                    trailingIcon = {
                        Icon(painterResource(R.drawable.ic_arrow_right), null, modifier = Modifier.size(18.dp))
                    }
                ) {
                    Text(
                        text = "Begin",
                        style = ShButtonStyle.copy(color = Color.White, fontSize = 18.sp)
                    )
                }

                Row(
                    modifier = Modifier
                        .padding(bottom = 24.dp)
                        .clickable { onReturn() },
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Already practicing? ",
                        style = ShLabelStyle.copy(color = ShNightMuted, fontSize = 13.sp)
                    )
                    Text(
                        text = "Return",
                        style = ShLabelStyle.copy(color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    )
                }
            }
        }
    }
}
