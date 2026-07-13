package com.example.shoshinapp.ui.screens

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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.shoshinapp.alarm.AlarmScheduler
import com.example.shoshinapp.data.ShoshinRepository
import com.example.shoshinapp.ui.components.*
import com.example.shoshinapp.ui.theme.*
import kotlinx.coroutines.launch

@Composable
fun AlarmScreen(navController: NavController, template: String = "walk") {
    val context = LocalContext.current
    val repo    = remember { ShoshinRepository(context) }
    val scope   = rememberCoroutineScope()

    var hour       by remember { mutableStateOf(5) }
    var minute     by remember { mutableStateOf(30) }
    var windDown   by remember { mutableStateOf(true) }
    var difficulty by remember { mutableStateOf("Standard") }
    val days       = remember { mutableStateListOf(1, 2, 3, 4, 5) }
    val dayLabels  = listOf("S","M","T","W","T","F","S")

    Column(modifier = Modifier.fillMaxSize().background(Paper)) {
        Column(modifier = Modifier.weight(1f).verticalScroll(rememberScrollState()).padding(horizontal = 20.dp, vertical = 8.dp)) {
            Text("Set your wake", fontSize = 28.sp, fontWeight = FontWeight.SemiBold, fontFamily = CormorantGaramond, color = Ink)
            Spacer(Modifier.height(14.dp))

            // Time picker
            ShoshinCard(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("▲", fontSize = 18.sp, color = Ink, fontWeight = FontWeight.Bold, modifier = Modifier.clickable { hour = (hour + 1) % 24 }.padding(8.dp))
                            Text(String.format("%02d", hour), fontSize = 72.sp, fontWeight = FontWeight.Bold, fontFamily = DMSans, color = Ink, letterSpacing = (-2).sp)
                            Text("▼", fontSize = 18.sp, color = Ink, fontWeight = FontWeight.Bold, modifier = Modifier.clickable { hour = (hour - 1 + 24) % 24 }.padding(8.dp))
                        }
                        Text(":", fontSize = 60.sp, fontWeight = FontWeight.Bold, color = Ink)
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("▲", fontSize = 18.sp, color = Ink, fontWeight = FontWeight.Bold, modifier = Modifier.clickable { minute = (minute + 5) % 60 }.padding(8.dp))
                            Text(String.format("%02d", minute), fontSize = 72.sp, fontWeight = FontWeight.Bold, fontFamily = DMSans, color = Ink, letterSpacing = (-2).sp)
                            Text("▼", fontSize = 18.sp, color = Ink, fontWeight = FontWeight.Bold, modifier = Modifier.clickable { minute = (minute - 5 + 60) % 60 }.padding(8.dp))
                        }
                        Text(if (hour < 12) "AM" else "PM", fontSize = 22.sp, fontWeight = FontWeight.SemiBold, fontFamily = DMSans, color = Fog, modifier = Modifier.padding(bottom = 8.dp))
                    }
                }
            }

            Spacer(Modifier.height(14.dp))

            // Repeat days
            ShoshinCard(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Kicker("Repeat"); Spacer(Modifier.height(12.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        dayLabels.forEachIndexed { i, d ->
                            val on = days.contains(i)
                            Box(modifier = Modifier.size(38.dp).clip(CircleShape).background(if (on) Ink else Paper2).border(1.dp, if (on) Ink else Line, CircleShape).clickable { if (on) days.remove(i) else days.add(i) }, contentAlignment = Alignment.Center) {
                                Text(d, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = if (on) Paper else Fog, fontFamily = DMSans)
                            }
                        }
                    }
                }
            }

            Spacer(Modifier.height(14.dp))

            // Difficulty
            ShoshinCard(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Kicker("Wake Challenge"); Spacer(Modifier.height(12.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        listOf("Gentle","Standard","Shoshin").forEach { d ->
                            val on = difficulty == d
                            Box(modifier = Modifier.weight(1f).clip(RoundedCornerShape(11.dp)).background(if (on) Ink else Paper2).border(1.dp, if (on) Ink else Line, RoundedCornerShape(11.dp)).clickable { difficulty = d }.padding(vertical = 11.dp), contentAlignment = Alignment.Center) {
                                Text(d, fontSize = 14.sp, fontWeight = FontWeight.SemiBold, fontFamily = DMSans, color = if (on) Paper else Fog)
                            }
                        }
                    }
                }
            }

            Spacer(Modifier.height(14.dp))

            // Wind down
            ShoshinCard(modifier = Modifier.fillMaxWidth()) {
                Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Wind-down reminder", fontSize = 15.sp, fontWeight = FontWeight.SemiBold, fontFamily = DMSans, color = Ink)
                        Text("9:30 PM nightly", fontSize = 12.sp, color = Fog, fontFamily = DMSans)
                    }
                    ShoshinToggle(checked = windDown, onCheckedChange = { windDown = it })
                }
            }

            Spacer(Modifier.height(20.dp))
        }

        Column(modifier = Modifier.padding(20.dp)) {
            ShoshinButton(onClick = {
                scope.launch {
                    repo.saveAlarm(hour, minute)
                    AlarmScheduler.schedule(context, hour, minute, "Morning Practice")
                }
                navController.popBackStack()
            }, variant = ShButtonVariant.Primary) {
                Text("Arm for tomorrow")
            }
        }
    }
}
