package com.Shoshin.app.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.Shoshin.app.R
import com.Shoshin.app.data.ShoshinRepository
import com.Shoshin.app.navigation.ShRoutes
import com.Shoshin.app.ui.components.*
import com.Shoshin.app.ui.theme.*
import kotlinx.coroutines.launch

private data class Template(
    val id: String, 
    val name: String, 
    val tag: String, 
    val duration: String, 
    val icon: Int, 
    val steps: List<Pair<String, Int>>
)
private val TEMPLATES = listOf(
    Template("walk",  "Morning Walk", "Movement", "22", R.drawable.ic_walk, listOf(
        "Mind awake" to R.drawable.ic_brain,
        "Freshen up" to R.drawable.ic_droplet,
        "Dressed" to R.drawable.ic_shirt,
        "Out the door" to R.drawable.ic_sun,
        "Walk begun" to R.drawable.ic_walk
    )),
    Template("study", "Deep Study",   "Focus", "45", R.drawable.ic_book, listOf(
        "Mind awake" to R.drawable.ic_brain,
        "Freshen up" to R.drawable.ic_droplet,
        "Tea brewed" to R.drawable.ic_check,
        "Desk ready" to R.drawable.ic_check,
        "Study begun" to R.drawable.ic_book
    )),
    Template("gym",   "Strength",     "Training", "60", R.drawable.ic_dumbbell, listOf(
        "Mind awake" to R.drawable.ic_brain,
        "Freshen up" to R.drawable.ic_droplet,
        "Kit on" to R.drawable.ic_shirt,
        "Out the door" to R.drawable.ic_sun,
        "Training begun" to R.drawable.ic_dumbbell
    ))
)

@Composable
fun RoutineTemplateScreen(goalKey: String, onContinue: (String) -> Unit) {
    var selected by remember { mutableStateOf(if (goalKey in listOf("study", "gym", "walk")) goalKey else "walk") }
    val t    = TEMPLATES.find { it.id == selected } ?: TEMPLATES[0]
    val context = LocalContext.current
    val repo    = remember { ShoshinRepository(context) }
    val scope   = rememberCoroutineScope()

    Column(modifier = Modifier.fillMaxSize().background(ShPaper)) {
        Column(modifier = Modifier.weight(1f).verticalScroll(rememberScrollState()).padding(horizontal = 24.dp, vertical = 16.dp)) {
            Row(modifier = Modifier.fillMaxWidth().statusBarsPadding(), verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = { /* Back logic */ }) {
                    Icon(painterResource(R.drawable.ic_arrow_left), null, tint = ShInk)
                }
                Text("Change goal", style = ShLabelStyle.copy(color = ShFog, fontWeight = FontWeight.Bold))
            }
            
            Spacer(Modifier.height(16.dp))
            Kicker("A BEGINNING · 2 OF 2", color = ShFog2)
            Spacer(Modifier.height(8.dp))
            Text("Choose your path", style = ShTitleStyle.copy(fontSize = 36.sp), color = ShInk)
            
            Spacer(Modifier.height(32.dp))
            
            // Chips
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                TEMPLATES.forEach { tpl ->
                    val sel = selected == tpl.id
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .height(100.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(if (sel) Color.White else ShPaper2) // Per screenshot: White for selected in dark mode
                            .clickable { selected = tpl.id }
                            .padding(12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            painter = painterResource(id = tpl.icon),
                            contentDescription = null,
                            modifier = Modifier.size(24.dp),
                            tint = if (sel) Color.Black else ShInk
                        )
                        Spacer(Modifier.height(12.dp))
                        Text(tpl.name, fontSize = 11.sp, fontWeight = FontWeight.Bold, fontFamily = DmSansFamily, color = if (sel) Color.Black else ShInk, textAlign = TextAlign.Center)
                    }
                }
            }
            
            Spacer(Modifier.height(32.dp))

            // Main Detail Card
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(24.dp))
                    .background(ShNight2) // Dark card always
                    .padding(24.dp)
            ) {
                Column {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        PillTag(label = t.tag, color = ShVermillion.copy(alpha = 0.15f), textColor = ShVermillion)
                        PillTag(label = "${t.steps.size} checkpoints", color = ShNight3, textColor = ShNightMuted)
                        PillTag(label = "~${t.duration} min", color = ShNight3, textColor = ShNightMuted)
                    }
                    
                    Spacer(Modifier.height(32.dp))

                    t.steps.forEachIndexed { i, (label, icon) ->
                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(bottom = 24.dp)) {
                            Box(
                                modifier = Modifier.size(32.dp).clip(CircleShape).background(ShNight3),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("${i+1}", style = ShLabelStyle.copy(fontSize = 11.sp, fontWeight = FontWeight.Bold, color = ShNightMuted))
                            }
                            Spacer(Modifier.width(16.dp))
                            Icon(painterResource(icon), null, modifier = Modifier.size(18.dp), tint = ShNightMuted)
                            Spacer(Modifier.width(16.dp))
                            Text(label, style = ShBodyStyle.copy(fontSize = 16.sp, color = ShNightText))
                        }
                    }
                }
            }
        }
        
        Column(modifier = Modifier.padding(24.dp).navigationBarsPadding()) {
            ShoshinButton(
                onClick = {
                    scope.launch { 
                        repo.saveTemplate(selected)
                        onContinue(selected)
                    }
                },
                variant = ShButtonVariant.Accent,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Set this path", fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
private fun PillTag(label: String, color: Color, textColor: Color) {
    Surface(
        color = color,
        shape = RoundedCornerShape(99.dp)
    ) {
        Text(
            text = label,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            style = ShLabelStyle.copy(fontSize = 10.sp, fontWeight = FontWeight.Bold, color = textColor)
        )
    }
}
