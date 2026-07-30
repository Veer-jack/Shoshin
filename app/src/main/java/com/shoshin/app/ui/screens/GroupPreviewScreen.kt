package com.Shoshin.app.ui.screens

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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.Shoshin.app.R
import com.Shoshin.app.ui.components.*
import com.Shoshin.app.ui.theme.*
import com.Shoshin.app.viewmodel.GroupViewModel

@Composable
fun GroupPreviewScreen(
    navController: NavController,
    groupId: String,
    viewModel: GroupViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
) {
    val group by viewModel.currentGroup.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    
    LaunchedEffect(groupId) {
        viewModel.loadGroupMembers(groupId) // This also loads group details
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(ShPaper)
            .padding(horizontal = 24.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(vertical = 16.dp),
            horizontalArrangement = Arrangement.End
        ) {
            IconButton(onClick = { navController.popBackStack() }, modifier = Modifier.size(24.dp)) {
                Icon(painterResource(R.drawable.ic_close), contentDescription = "Close", tint = ShFog)
            }
        }

        if (isLoading && group == null) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = ShVermillion)
            }
        } else {
            Spacer(Modifier.weight(0.5f))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1.5f)
                    .clip(RoundedCornerShape(32.dp))
                    .background(ShInk),
                contentAlignment = Alignment.Center
            ) {
                Enso(size = 180, color = ShVermillion.copy(alpha = 0.15f), strokeWidth = 8f, modifier = Modifier.align(Alignment.TopEnd).offset(x = 60.dp, y = (-40).dp))

                Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(24.dp)) {
                    // Member Avatars mockup (Stacking real ones if we had urls)
                    Row(
                        horizontalArrangement = Arrangement.Center,
                        modifier = Modifier.padding(bottom = 20.dp)
                    ) {
                        group?.members?.take(3)?.forEachIndexed { i, _ ->
                            Box(
                                modifier = Modifier
                                    .size(56.dp)
                                    .offset(x = if (i > 0) ((-12) * i).dp else 0.dp)
                                    .border(2.dp, ShInk, CircleShape)
                                    .clip(CircleShape)
                                    .background(ShPaper2),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(painterResource(R.drawable.ic_user), null, tint = ShFog, modifier = Modifier.size(24.dp))
                            }
                        }
                    }

                    Kicker("YOU'VE BEEN INVITED TO", color = ShPaper.copy(alpha = 0.4f))
                    Spacer(Modifier.height(8.dp))
                    Text(group?.name ?: "Circle", style = ShTitleStyle.copy(fontSize = 32.sp, color = Color.White), textAlign = TextAlign.Center)
                    Spacer(Modifier.height(8.dp))
                    Text(
                        "${group?.members?.size ?: 0} members · Rising together",
                        style = ShLabelStyle,
                        color = Color.White.copy(alpha = 0.4f)
                    )
                }
            }

            Spacer(Modifier.height(32.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                PreviewStat(value = "86%", label = "GROUP CONSISTENCY")
                PreviewStat(value = "22", label = "AVG STREAK")
            }

            Spacer(Modifier.weight(1f))

            Column(modifier = Modifier.padding(bottom = 32.dp)) {
                ShoshinButton(
                    onClick = { 
                        group?.inviteCode?.let { viewModel.joinGroup(it) }
                        navController.popBackStack()
                    },
                    variant = ShButtonVariant.Accent,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Join the circle", fontWeight = FontWeight.Bold)
                }
                
                Spacer(Modifier.height(12.dp))
                
                ShoshinButton(
                    onClick = { navController.popBackStack() },
                    variant = ShButtonVariant.Ghost,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Not now", color = ShInk)
                }
            }
        }
    }
}

@Composable
private fun PreviewStat(value: String, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(value, style = ShNumeralStyle.copy(fontSize = 36.sp, color = ShInk))
        Text(label, style = ShKickerStyle.copy(fontSize = 9.sp, letterSpacing = 1.sp), color = ShFog)
    }
}
