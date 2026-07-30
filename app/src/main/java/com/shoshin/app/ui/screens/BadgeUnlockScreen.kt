package com.Shoshin.app.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.Shoshin.app.R
import com.Shoshin.app.navigation.ShRoutes
import com.Shoshin.app.ui.components.*
import com.Shoshin.app.ui.theme.*
import com.Shoshin.app.viewmodel.BadgeViewModel

@Composable
fun BadgeUnlockScreen(
    navController: NavController,
    viewModel: BadgeViewModel,
    badgeId: String
) {
    val badges by viewModel.badges.collectAsState()
    val badge = badges.find { it.id == badgeId } ?: return

    ShoshinTheme(type = ShoshinThemeType.ALWAYS_DARK) {
        val infiniteTransition = rememberInfiniteTransition(label = "rotation")
        val rotation by infiniteTransition.animateFloat(
            initialValue = 0f,
            targetValue = 360f,
            animationSpec = infiniteRepeatable(
                animation = tween(20000, easing = LinearEasing),
                repeatMode = RepeatMode.Restart
            ),
            label = "rotation"
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(ShNight)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.weight(1f))

            // Badge Medallion
            Box(
                modifier = Modifier
                    .size(200.dp)
                    .rotate(rotation)
                    .drawBehind {
                        drawCircle(
                            color = ShVermillionLight,
                            radius = 92.dp.toPx(),
                            style = Stroke(
                                width = 1.2.dp.toPx(),
                                pathEffect = PathEffect.dashPathEffect(floatArrayOf(6.dp.toPx(), 10.dp.toPx()), 0f)
                            ),
                            alpha = 0.4f
                        )
                    },
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(128.dp)
                        .clip(CircleShape)
                        .background(ShNight2),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(88.dp)
                            .clip(CircleShape)
                            .background(ShVermillionLight),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            painter = painterResource(getBadgeIconRes(badge.icon)),
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(40.dp)
                        )
                    }
                }
            }

            Spacer(Modifier.height(48.dp))

            Kicker("BADGE EARNED", color = ShVermillionLight)
            
            Spacer(Modifier.height(12.dp))

            Text(
                text = badge.name,
                style = ShTitleStyle.copy(fontSize = 40.sp, color = Color.White),
                textAlign = TextAlign.Center
            )

            Spacer(Modifier.height(12.dp))

            Text(
                text = badge.description,
                style = ShBodyStyle.copy(fontSize = 16.sp, color = ShNightMuted),
                textAlign = TextAlign.Center,
                modifier = Modifier.widthIn(max = 280.dp),
                lineHeight = 22.sp
            )

            Spacer(modifier = Modifier.weight(1f))

            Column(modifier = Modifier.padding(bottom = 28.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                ShoshinButton(
                    onClick = { 
                        navController.navigate(ShRoutes.streakShare(1, badge.name, System.currentTimeMillis()))
                    },
                    variant = ShButtonVariant.Accent,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(painterResource(R.drawable.ic_share), null, modifier = Modifier.size(20.dp), tint = Color.White)
                    Spacer(Modifier.width(10.dp))
                    Text("Share this moment", fontWeight = FontWeight.Bold)
                }
                
                ShoshinButton(
                    onClick = { navController.popBackStack() },
                    variant = ShButtonVariant.Dark,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Continue", color = Color.White, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
