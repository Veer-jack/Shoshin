package com.example.shoshinapp.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.shoshinapp.R
import com.example.shoshinapp.data.ShoshinRepository
import com.example.shoshinapp.navigation.ShRoutes
import com.example.shoshinapp.ui.components.*
import com.example.shoshinapp.ui.theme.*
import kotlinx.coroutines.launch

private data class Template(val id: String, val name: String, val tag: String, val icon: Int, val steps: List<String>)
private val TEMPLATES = listOf(
    Template("walk",  "Morning Walk", "Movement", R.drawable.ic_walk, listOf("Mind awake","Freshen up","Dressed","Out the door","Walk begun")),
    Template("study", "Deep Study",   "Focus",    R.drawable.ic_book, listOf("Mind awake","Freshen up","Tea brewed","Desk ready","Study begun")),
    Template("gym",   "Strength",     "Training", R.drawable.ic_dumbbell, listOf("Mind awake","Freshen up","Kit on","Out the door","Training begun"))
)

@Composable
fun RoutineTemplateScreen(goalKey: String, onContinue: (String) -> Unit) {
    var selected by remember { mutableStateOf(if (goalKey in listOf("study", "gym", "walk")) goalKey else "walk") }
    val t    = TEMPLATES.find { it.id == selected } ?: TEMPLATES[0]
    val context = LocalContext.current
    val repo    = remember { ShoshinRepository(context) }
    val scope   = rememberCoroutineScope()

    Column(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
        Column(modifier = Modifier.weight(1f).verticalScroll(rememberScrollState()).padding(horizontal = 24.dp, vertical = 16.dp)) {
            Kicker("Step 2 of 2", color = ShVermillion)
            Spacer(Modifier.height(8.dp))
            Text("Pick your\npath", fontSize = 34.sp, fontWeight = FontWeight.SemiBold, fontFamily = CormorantFamily, color = ShInk)
            Spacer(Modifier.height(8.dp))
            Text("Start from a proven sequence. You can edit every checkpoint later.", fontSize = 15.sp, color = ShFog, fontFamily = DmSansFamily, lineHeight = 22.sp)
            Spacer(Modifier.height(20.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                TEMPLATES.forEach { tpl ->
                    val sel = selected == tpl.id
                    Column(
                        modifier = Modifier.weight(1f).clip(RoundedCornerShape(14.dp)).background(if (sel) ShInk else ShSurface).border(1.5.dp, if (sel) ShInk else ShLine, RoundedCornerShape(14.dp)).clickable { selected = tpl.id }.padding(14.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            painter = painterResource(id = tpl.icon),
                            contentDescription = null,
                            modifier = Modifier.size(28.dp),
                            tint = if (sel) ShPaper else ShInk
                        )
                        Spacer(Modifier.height(10.dp))
                        Text(tpl.name, fontSize = 12.sp, fontWeight = FontWeight.SemiBold, fontFamily = DmSansFamily, color = if (sel) ShPaper else ShInk, textAlign = androidx.compose.ui.text.style.TextAlign.Center, lineHeight = 16.sp)
                    }
                }
            }
            Spacer(Modifier.height(20.dp))
            ShoshinCard(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(18.dp)) {
                    t.steps.forEachIndexed { i, step ->
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(13.dp), modifier = Modifier.padding(vertical = 11.dp)) {
                            Box(modifier = Modifier.size(28.dp).clip(RoundedCornerShape(8.dp)).background(ShPaper2).border(1.dp, ShLine, RoundedCornerShape(8.dp)), contentAlignment = Alignment.Center) {
                                Text("${i+1}", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = ShFog, fontFamily = DmSansFamily)
                            }
                            Text(step, fontSize = 15.sp, fontWeight = FontWeight.Medium, fontFamily = DmSansFamily, color = ShInk, modifier = Modifier.weight(1f))
                        }
                        if (i < t.steps.lastIndex) HorizontalDivider(color = ShLine, thickness = 1.dp)
                    }
                }
            }
        }
        Column(modifier = Modifier.padding(24.dp)) {
            ShoshinButton(onClick = {
                scope.launch { 
                    repo.saveTemplate(selected)
                    onContinue(selected)
                }
            }) {
                Text("Set this routine")
            }
        }
    }
}
