package com.example.shoshinapp.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.shoshinapp.ui.components.*
import com.example.shoshinapp.ui.theme.*

private data class Slide(val kicker: String, val title: String, val body: String)
private val SLIDES = listOf(
    Slide("The Problem", "Most routines die\nin the first ten\nminutes.", "The gap between your alarm and your habit is where discipline is won or lost. Shoshin owns that gap."),
    Slide("The Method", "Cross the bridge,\ncheckpoint by\ncheckpoint.", "From half-asleep to in-motion. Each guided step removes one excuse — until starting is the only option."),
    Slide("The Payoff", "Become who\nyou return as.", "Each morning kept is a vote for the person you're practicing to be. Begin again, every day.")
)

@Composable
fun OnboardingScreen(
    index: Int = 0,
    onNext: () -> Unit,
    onSkip: () -> Unit
) {
    val slide  = SLIDES.getOrElse(index) { SLIDES[0] }
    val isLast = index == SLIDES.lastIndex

    Column(modifier = Modifier.fillMaxSize().background(ShPaper)) {
        Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp, vertical = 8.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Text("Shoshin", fontSize = 22.sp, fontWeight = FontWeight.SemiBold, fontFamily = CormorantFamily, color = ShInk)
            TextButton(onClick = onSkip) { Text("Skip", color = ShFog, fontFamily = DmSansFamily) }
        }

        // Image placeholder
        Box(modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp).height(260.dp).clip(RoundedCornerShape(20.dp)).background(ShPaper2), contentAlignment = Alignment.Center) {
            // Enso circle
            androidx.compose.foundation.Canvas(modifier = Modifier.size(180.dp)) {
                drawArc(color = ShVermillion.copy(alpha = 0.12f), startAngle = -90f, sweepAngle = 320f, useCenter = false, style = androidx.compose.ui.graphics.drawscope.Stroke(width = 8.dp.toPx(), cap = androidx.compose.ui.graphics.StrokeCap.Round))
            }
            Text(slide.kicker.lowercase(), fontSize = 11.sp, fontWeight = FontWeight.Medium, fontFamily = DmSansFamily, color = ShFog2, letterSpacing = 2.sp)
        }

        Column(modifier = Modifier.weight(1f).padding(horizontal = 24.dp, vertical = 24.dp)) {
            Kicker(slide.kicker, color = ShVermillion)
            Spacer(Modifier.height(10.dp))
            Text(slide.title, fontSize = 34.sp, fontWeight = FontWeight.SemiBold, fontFamily = CormorantFamily, color = ShInk, lineHeight = 38.sp)
            Spacer(Modifier.height(14.dp))
            Text(slide.body, fontSize = 15.sp, color = ShFog, fontFamily = DmSansFamily, lineHeight = 23.sp)
        }

        Column(modifier = Modifier.padding(horizontal = 24.dp, vertical = 24.dp)) {
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                SLIDES.forEachIndexed { i, _ ->
                    Box(modifier = Modifier.weight(if (i == index) 2.5f else 1f).height(4.dp).clip(RoundedCornerShape(99.dp)).background(if (i == index) ShInk else ShSand))
                }
            }
            Spacer(Modifier.height(18.dp))
            ShoshinButton(onClick = onNext) {
                Text(if (isLast) "Choose your path" else "Continue")
            }
        }
    }
}
