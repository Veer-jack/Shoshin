package com.shoshin.app.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.shoshin.app.data.ShoshinRepository
import com.shoshin.app.navigation.ShRoutes
import com.shoshin.app.ui.components.*
import com.shoshin.app.ui.theme.*
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
                navController.navigate(ShRoutes.AUTH) {
                    popUpTo(ShRoutes.SPLASH) { inclusive = true }
                }
            }
        }
    }

    ShoshinTheme(darkSurface = true) {
        Box(
            modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background),
            contentAlignment = Alignment.Center
        ) {
            // Enso circle motif with animation
            Box(
                modifier = Modifier
                    .size(320.dp)
                    .rotate(rotation)
                    .background(Color.Transparent)
            ) {
                androidx.compose.foundation.Canvas(modifier = Modifier.fillMaxSize()) {
                    drawArc(
                        color = ShVermillion.copy(alpha = 0.15f),
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

                // Logo mark — three rising bars
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.Bottom,
                    modifier = Modifier.padding(bottom = 24.dp)
                ) {
                    Box(modifier = Modifier.width(12.dp).height(20.dp).background(Color.White, RoundedCornerShape(3.dp)))
                    Box(modifier = Modifier.width(12.dp).height(32.dp).background(ShMatcha, RoundedCornerShape(3.dp)))
                    Box(modifier = Modifier.width(12.dp).height(46.dp).background(ShVermillion, RoundedCornerShape(3.dp)))
                }

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
                    fontFamily = CormorantGaramond,
                    color = ShVermillion,
                    letterSpacing = 8.sp,
                    modifier = Modifier.padding(top = 8.dp)
                )
                Text(
                    text = "Beginner's mind. Every morning.",
                    fontSize = 14.sp,
                    fontFamily = DMSans,
                    color = ShNightMuted,
                    letterSpacing = 0.5.sp,
                    modifier = Modifier.padding(top = 16.dp)
                )

                Spacer(Modifier.weight(1f))

                val interactionSource = remember { MutableInteractionSource() }

                ShoshinButton(
                    onClick = { onBegin() },
                    variant = ShButtonVariant.Accent,
                    modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
                    interactionSource = interactionSource,
                    pressedColor = Color.Black
                ) {
                    Text("LET’S BEGIN", color = Color.White, fontWeight = FontWeight.Bold, letterSpacing = 2.sp)
                }
            }
        }
    }
}
