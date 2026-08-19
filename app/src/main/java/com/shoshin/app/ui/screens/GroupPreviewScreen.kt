package com.Shoshin.app.ui.screens

import android.widget.Toast
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
import androidx.compose.ui.platform.LocalContext
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
    inviteCode: String,
    viewModel: GroupViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
) {
    val group by viewModel.currentGroup.collectAsState()
    val members by viewModel.groupMembers.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    val joinSuccess by viewModel.joinSuccess.collectAsState()
    val limitReached by viewModel.limitReached.collectAsState()
    val groupFull by viewModel.groupFull.collectAsState()
    val joinError by viewModel.error.collectAsState()
    val context = LocalContext.current
    var isJoining by remember { mutableStateOf(false) }

    LaunchedEffect(inviteCode) {
        viewModel.loadGroupPreviewByCode(inviteCode)
    }

    // Join succeeded — leave the preview screen.
    LaunchedEffect(joinSuccess) {
        if (joinSuccess) {
            viewModel.resetJoinState()
            navController.popBackStack()
        }
    }

    // Join failed — surface why and stay put so the user actually sees it,
    // instead of popping back before the async result ever comes in.
    LaunchedEffect(limitReached, groupFull, joinError) {
        val message = when {
            limitReached != null -> "You've reached max groups (5). Invite more friends to expand your limit."
            groupFull != null -> "This group is full. Ask the owner to expand capacity."
            joinError != null -> joinError
            else -> null
        }
        if (message != null) {
            isJoining = false
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
            viewModel.clearLimitError()
            if (joinError != null) viewModel.clearError()
        }
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

            if (members.isNotEmpty()) {
                Kicker("TOP OF THE CIRCLE", color = ShFog)
                Spacer(Modifier.height(12.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(20.dp))
                        .background(ShPaper2)
                ) {
                    Column(modifier = Modifier.padding(vertical = 4.dp)) {
                        members.take(3).forEachIndexed { index, m ->
                            Row(
                                modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("#${index + 1}", style = ShLabelStyle, color = ShFog, modifier = Modifier.width(28.dp))
                                Text(m.name, style = ShH2Style.copy(fontSize = 15.sp, color = ShInk), modifier = Modifier.weight(1f))
                                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                    Icon(painterResource(R.drawable.ic_flame), null, modifier = Modifier.size(12.dp), tint = ShVermillion)
                                    Text("${m.consistencyStreak}", style = ShNumeralStyle.copy(fontSize = 14.sp, color = ShInk))
                                }
                            }
                            if (index < minOf(members.size, 3) - 1) {
                                HorizontalDivider(color = ShLine, modifier = Modifier.padding(horizontal = 20.dp))
                            }
                        }
                    }
                }
            }

            Spacer(Modifier.weight(1f))

            Column(modifier = Modifier.padding(bottom = 32.dp)) {
                ShoshinButton(
                    onClick = {
                        // Dialog/screen is dismissed by the joinSuccess LaunchedEffect above,
                        // not here — popping back unconditionally is the silent-failure bug.
                        group?.inviteCode?.let {
                            isJoining = true
                            viewModel.joinGroup(it)
                        }
                    },
                    variant = ShButtonVariant.Accent,
                    enabled = !isJoining,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(if (isJoining) "Joining…" else "Join the circle", fontWeight = FontWeight.Bold)
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
