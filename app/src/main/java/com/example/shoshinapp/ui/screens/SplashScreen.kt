package com.example.shoshinapp.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.shoshinapp.data.ShoshinRepository
import com.example.shoshinapp.navigation.ShRoutes
import com.example.shoshinapp.ui.theme.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first

@Composable
fun SplashScreen(navController: NavController) {
    val context = LocalContext.current
    val repo    = remember { ShoshinRepository(context) }

    LaunchedEffect(Unit) {
        delay(2000)
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

    ShoshinTheme(darkSurface = true) {
        Box(
            modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background),
            contentAlignment = Alignment.Center
        ) {
            // Enso circle motif
            Box(
                modifier = Modifier
                    .size(280.dp)
                    .background(Color.Transparent)
            ) {
                androidx.compose.foundation.Canvas(modifier = Modifier.fillMaxSize()) {
                    drawArc(
                        color = ShVermillion.copy(alpha = 0.10f),
                        startAngle = -90f,
                        sweepAngle = 310f,
                        useCenter = false,
                        style = androidx.compose.ui.graphics.drawscope.Stroke(
                            width = 9.dp.toPx(),
                            cap = androidx.compose.ui.graphics.StrokeCap.Round
                        )
                    )
                }
            }

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                // Logo mark — three rising bars
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.Bottom,
                    modifier = Modifier.padding(bottom = 24.dp)
                ) {
                    Box(modifier = Modifier.width(12.dp).height(20.dp).background(ShInk.copy(alpha = 0.5f), RoundedCornerShape(3.dp)))
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
                    fontSize = 13.sp,
                    fontFamily = DMSans,
                    color = ShNightMuted,
                    letterSpacing = 0.5.sp,
                    modifier = Modifier.padding(top = 16.dp)
                )
            }
        }
    }
}
