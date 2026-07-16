package com.example.shoshinapp.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.shoshinapp.R
import com.example.shoshinapp.ui.components.*
import com.example.shoshinapp.ui.theme.*

@Composable
fun GroupPreviewScreen(
    navController: NavController,
    groupId: String
) {
    val previewMembers = emptyList<String>() // Removed dummy members

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 24.dp)
    ) {
        // App Bar
        Row(
            modifier = Modifier.fillMaxWidth().padding(top = 16.dp, bottom = 8.dp),
            horizontalArrangement = Arrangement.End
        ) {
            IconButton(onClick = { navController.popBackStack() }, modifier = Modifier.size(22.dp)) {
                Icon(painterResource(R.drawable.ic_close), contentDescription = "Close", tint = ShFog)
            }
        }

        Column(
            modifier = Modifier.weight(1f).fillMaxWidth(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Hero Card - Ink
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(24.dp))
                    .background(ShInk)
                    .padding(26.dp),
                contentAlignment = Alignment.Center
            ) {
                Enso(
                    size = 130,
                    color = ShVermillion.copy(alpha = 0.3f),
                    strokeWidth = 5f,
                    modifier = Modifier.align(Alignment.TopEnd).offset(x = 30.dp, y = (-30).dp)
                )

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    // Member Avatars
                    if (previewMembers.isNotEmpty()) {
                        Row(
                            horizontalArrangement = Arrangement.Center,
                            modifier = Modifier.padding(bottom = 16.dp)
                        ) {
                            previewMembers.forEachIndexed { i, initial ->
                                Box(
                                    modifier = Modifier
                                        .size(44.dp)
                                        .offset(x = if (i > 0) ((-12) * i).dp else 0.dp)
                                        .border(3.dp, ShInk, CircleShape)
                                        .clip(CircleShape)
                                        .background(ShSand),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(initial, fontSize = 17.sp, fontWeight = FontWeight.Bold, color = ShInk, fontFamily = DmSansFamily)
                                }
                            }
                        }
                    }

                    Kicker("You've been invited to", color = ShPaper.copy(alpha = 0.6f), modifier = Modifier.padding(bottom = 8.dp))
                    Text("Dawn Circle", style = ShTitleStyle.copy(fontSize = 28.sp, color = ShPaper), textAlign = TextAlign.Center)
                    Text(
                        "Rising together since March", // Simplified
                        fontSize = 13.5.sp,
                        color = ShPaper.copy(alpha = 0.7f),
                        modifier = Modifier.padding(top = 10.dp)
                    )
                }
            }

            Spacer(Modifier.height(20.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                ShoshinStat(value = "--", label = "Group consistency")
                ShoshinStat(value = "--", label = "Avg streak")
            }
        }

        Column(modifier = Modifier.padding(bottom = 24.dp)) {
            ShoshinButton(
                onClick = { navController.popBackStack() },
                variant = ShButtonVariant.Accent,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Join the circle")
            }
            
            Spacer(Modifier.height(10.dp))
            
            ShoshinButton(
                onClick = { navController.popBackStack() },
                variant = ShButtonVariant.Ghost,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Not now")
            }
        }
    }
}
