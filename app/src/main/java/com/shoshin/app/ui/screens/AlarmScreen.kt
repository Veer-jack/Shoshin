package com.Shoshin.app.ui.screens

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
import com.Shoshin.app.alarm.AlarmScheduler
import com.Shoshin.app.data.ShoshinRepository
import com.Shoshin.app.navigation.ShRoutes
import com.Shoshin.app.ui.components.*
import com.Shoshin.app.ui.theme.*
import kotlinx.coroutines.launch

@Composable
fun AlarmScreen(navController: NavController, template: String = "walk") {
    val context = LocalContext.current
    val repo    = remember { ShoshinRepository(context) }
    val scope   = rememberCoroutineScope()

    val savedHour by repo.alarmHour.collectAsState(initial = 5)
    val savedMinute by repo.alarmMinute.collectAsState(initial = 30)
    val savedType by repo.alarmType.collectAsState(initial = "Normal")
    val savedTone by repo.alarmTone.collectAsState(initial = "bell")
    val currentTemplate by repo.template.collectAsState(initial = "walk")

    var hour       by remember(savedHour) { mutableIntStateOf(savedHour) }
    var minute     by remember(savedMinute) { mutableIntStateOf(savedMinute) }
    var alarmType  by remember(savedType) { mutableStateOf(savedType) }
    var selectedTone by remember(savedTone) { mutableStateOf(savedTone) }
    var isAm       by remember(savedHour) { mutableStateOf(savedHour < 12) }
    
    val days       = remember { mutableStateListOf(1, 2, 3, 4, 5) }
    val dayLabels  = listOf("M","T","W","T","F","S","S")

    val toneName = remember(selectedTone) {
        SH_SOUNDS_LIST.find { it.id == selectedTone }?.name ?: "Temple bell"
    }

    Scaffold(
        containerColor = ShPaper,
        bottomBar = {
            Box(modifier = Modifier.padding(24.dp).navigationBarsPadding()) {
                ShoshinButton(
                    onClick = {
                        scope.launch {
                            val finalHour = if (isAm) {
                                if (hour == 12) 0 else hour
                            } else {
                                if (hour == 12) 12 else hour + 12
                            }
                            repo.saveAlarm(finalHour, minute)
                            repo.saveAlarmSettings(selectedTone, alarmType, 7)
                            AlarmScheduler.schedule(context, finalHour, minute, "Morning Practice")
                            navController.popBackStack()
                        }
                    },
                    variant = ShButtonVariant.Accent,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Set alarm for tomorrow", fontWeight = FontWeight.Bold, color = Color.White)
                }
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp)
        ) {
            Spacer(Modifier.height(32.dp))

            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "Set your wake",
                    style = ShTitleStyle.copy(fontSize = 32.sp),
                    color = ShInk
                )
                
                Surface(
                    color = ShInk,
                    shape = RoundedCornerShape(999.dp)
                ) {
                    Text(
                        "Tomorrow",
                        color = Color.White,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        style = ShLabelStyle.copy(fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    )
                }
            }

            Spacer(Modifier.height(24.dp))

            // Time Picker
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(32.dp))
                    .border(1.dp, ShLine, RoundedCornerShape(32.dp))
                    .background(Color.White)
                    .padding(vertical = 32.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        TimeDigit(value = if (hour == 0) 12 else if (hour > 12) hour - 12 else hour, onValueChange = { 
                            hour = it 
                        }, range = 1..12)
                        
                        Column(
                            modifier = Modifier.padding(horizontal = 16.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(ShLine))
                            Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(ShLine))
                        }
                        
                        TimeDigit(value = minute, onValueChange = { minute = it }, range = 0..59)
                        
                        Spacer(Modifier.width(12.dp))
                        
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "AM",
                                style = ShLabelStyle.copy(
                                    fontSize = 18.sp, 
                                    fontWeight = if (isAm) FontWeight.Bold else FontWeight.Medium,
                                    color = if (isAm) ShInk else ShFog2
                                ),
                                modifier = Modifier.clickable { isAm = true }
                            )
                            Spacer(Modifier.height(8.dp))
                            Text(
                                text = "PM",
                                style = ShLabelStyle.copy(
                                    fontSize = 18.sp, 
                                    fontWeight = if (!isAm) FontWeight.Bold else FontWeight.Medium,
                                    color = if (!isAm) ShInk else ShFog2
                                ),
                                modifier = Modifier.clickable { isAm = false }
                            )
                        }
                    }
                    
                    Spacer(Modifier.height(24.dp))
                    
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(painterResource(R.drawable.ic_sun), null, tint = ShFog, modifier = Modifier.size(16.dp))
                        Spacer(Modifier.width(8.dp))
                        Text(
                            "Sunrise 6:14 AM · 44 min of dawn",
                            style = ShLabelStyle,
                            color = ShFog
                        )
                    }
                }
            }

            Spacer(Modifier.height(16.dp))

            // Routine Card
            ShoshinCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { navController.navigate(ShRoutes.GOAL_SELECTION) }
            ) {
                Row(
                    modifier = Modifier.padding(20.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(ShPaper2),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            painter = painterResource(if (currentTemplate == "walk") R.drawable.ic_walk else R.drawable.ic_book), 
                            null, 
                            tint = ShInk, 
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(Modifier.width(16.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Routine", style = ShH2Style.copy(fontSize = 16.sp))
                        Text(
                            text = if (currentTemplate == "walk") "Morning Walk · 5 checkpoints" else "Deep Study · 5 checkpoints",
                            style = ShLabelStyle, 
                            color = ShFog
                        )
                    }
                    Icon(painterResource(R.drawable.ic_arrow_right), null, tint = ShLine2, modifier = Modifier.size(20.dp))
                }
            }

            Spacer(Modifier.height(16.dp))

            // Repeat Section
            ShoshinCard(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Kicker("REPEAT", color = ShFog)
                    Spacer(Modifier.height(16.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        dayLabels.forEachIndexed { i, label ->
                            val isSelected = days.contains(i + 1)
                            Box(
                                modifier = Modifier
                                    .size(38.dp)
                                    .clip(CircleShape)
                                    .background(if (isSelected) ShInk else Color.Transparent)
                                    .border(1.dp, if (isSelected) ShInk else ShLine, CircleShape)
                                    .clickable {
                                        if (isSelected) days.remove(i + 1) else days.add(i + 1)
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = label,
                                    style = ShLabelStyle.copy(
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isSelected) Color.White else ShFog
                                    )
                                )
                            }
                        }
                    }
                }
            }

            Spacer(Modifier.height(16.dp))

            // Alarm Frequency Type Selector
            ShoshinCard(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Kicker("ALARM FREQUENCY", color = ShFog)
                    Spacer(Modifier.height(16.dp))
                    
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(ShPaper2)
                            .padding(4.dp)
                    ) {
                        Row(modifier = Modifier.fillMaxSize()) {
                            listOf("Gentle", "Normal", "Shoshin").forEach { type ->
                                val isSelected = alarmType == type
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .fillMaxHeight()
                                        .clip(RoundedCornerShape(9.dp))
                                        .background(if (isSelected) Color.White else Color.Transparent)
                                        .clickable { alarmType = type },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = type,
                                        style = ShLabelStyle.copy(
                                            fontSize = 14.sp,
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                            color = if (isSelected) ShInk else ShFog
                                        )
                                    )
                                }
                            }
                        }
                    }
                    
                    Spacer(Modifier.height(16.dp))
                    
                    val description = when(alarmType) {
                        "Gentle" -> "Slowly fades in volume over 2 minutes."
                        "Shoshin" -> "One resonant chime. If not awake, repeats every 30s."
                        else -> "Standard repeating alarm with snooze options."
                    }
                    
                    Text(
                        text = description,
                        style = ShLabelStyle,
                        color = ShFog,
                        lineHeight = 18.sp
                    )
                }
            }

            Spacer(Modifier.height(16.dp))

            // Sound Picker Row
            ShoshinCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        navController.navigate(ShRoutes.SOUND_PICKER)
                    }
            ) {
                Row(
                    modifier = Modifier.padding(20.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(ShPaper2),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(painterResource(R.drawable.ic_bell), null, tint = ShInk, modifier = Modifier.size(20.dp))
                    }
                    Spacer(Modifier.width(16.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Wake sound", style = ShH2Style.copy(fontSize = 16.sp))
                        Text(toneName, style = ShLabelStyle, color = ShFog)
                    }
                    Icon(painterResource(R.drawable.ic_arrow_right), null, tint = ShLine2, modifier = Modifier.size(20.dp))
                }
            }

            Spacer(Modifier.height(40.dp))
        }
    }
}

@Composable
private fun TimeDigit(
    value: Int,
    range: IntRange,
    onValueChange: (Int) -> Unit
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        IconButton(onClick = { if (value < range.last) onValueChange(value + 1) else onValueChange(range.first) }) {
            Icon(painterResource(R.drawable.ic_arrow_left), null, modifier = Modifier.size(20.dp).rotate(90f), tint = ShFog)
        }
        Text(
            text = String.format("%02d", value),
            style = ShDisplayStyle.copy(fontSize = 72.sp, fontWeight = FontWeight.Bold, fontFamily = DmSansFamily),
            color = ShInk
        )
        IconButton(onClick = { if (value > range.first) onValueChange(value - 1) else onValueChange(range.last) }) {
            Icon(painterResource(R.drawable.ic_arrow_left), null, modifier = Modifier.size(20.dp).rotate(270f), tint = ShFog)
        }
    }
}
