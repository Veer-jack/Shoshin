package com.Shoshin.app.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.Shoshin.app.R
import com.Shoshin.app.ui.components.*
import com.Shoshin.app.ui.theme.*

data class ShSound(val id: String, val name: String, val note: String)

val SH_SOUNDS = listOf(
    ShSound("bell", "Temple bell", "A single resonant strike"),
    ShSound("bowl", "Singing bowl", "Slow rising hum"),
    ShSound("bamboo", "Bamboo (shishi-odoshi)", "Gentle wooden knock"),
    ShSound("birds", "Morning birds", "Dawn chorus"),
    ShSound("koto", "Koto strings", "Soft plucked melody"),
    ShSound("rain", "Light rainfall", "Steady and calm"),
    ShSound("gong", "Distant gong", "Deep, for heavy sleepers")
)

@Composable
fun SoundPickerScreen(navController: NavController) {
    var selectedSoundId by remember { mutableStateOf("bell") }
    var volume by remember { mutableFloatStateOf(0.7f) }
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(scrollState)
            .padding(horizontal = 24.dp)
    ) {
        // App Bar
        Row(
            modifier = Modifier.fillMaxWidth().padding(top = 16.dp, bottom = 24.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            IconButton(onClick = { navController.popBackStack() }, modifier = Modifier.size(24.dp)) {
                Icon(painterResource(R.drawable.ic_arrow_left), contentDescription = "Back", tint = MaterialTheme.colorScheme.onBackground)
            }
            Text("Wake sound", style = ShTitleStyle.copy(fontSize = 26.sp), fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onBackground)
        }

        // Volume Card
        ShoshinCard(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(20.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(bottom = 14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Kicker("Volume")
                    Text("${(volume * 100).toInt()}%", style = ShNumeralStyle.copy(fontSize = 14.sp), color = MaterialTheme.colorScheme.onBackground)
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Icon(painterResource(R.drawable.ic_moon), null, modifier = Modifier.size(18.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f))
                    Slider(
                        value = volume,
                        onValueChange = { volume = it },
                        modifier = Modifier.weight(1f),
                        colors = SliderDefaults.colors(
                            thumbColor = ShVermillion,
                            activeTrackColor = ShVermillion,
                            inactiveTrackColor = MaterialTheme.colorScheme.outline
                        )
                    )
                    Icon(painterResource(R.drawable.ic_bell), null, modifier = Modifier.size(18.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        }

        Spacer(Modifier.height(32.dp))
        Kicker("Sounds", modifier = Modifier.padding(start = 4.dp, bottom = 12.dp))

        ShoshinCard(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(horizontal = 18.dp, vertical = 4.dp)) {
                SH_SOUNDS.forEachIndexed { index, sound ->
                    val isSelected = selectedSoundId == sound.id
                    SoundRow(
                        sound = sound,
                        isSelected = isSelected,
                        onClick = { selectedSoundId = sound.id }
                    )
                    if (index < SH_SOUNDS.lastIndex) {
                        HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f))
                    }
                }
            }
        }

        Spacer(Modifier.height(48.dp))
    }
}

@Composable
private fun SoundRow(
    sound: ShSound,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Box(
            modifier = Modifier
                .size(38.dp)
                .clip(RoundedCornerShape(11.dp))
                .background(if (isSelected) MaterialTheme.colorScheme.onBackground else MaterialTheme.colorScheme.surfaceVariant),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_play), // Need to ensure ic_play exists or use a generic play icon
                contentDescription = null,
                tint = if (isSelected) MaterialTheme.colorScheme.background else MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.size(16.dp)
            )
        }
        
        Column(modifier = Modifier.weight(1f)) {
            Text(sound.name, fontSize = 15.5.sp, fontWeight = FontWeight.Medium, color = MaterialTheme.colorScheme.onSurface)
            Text(sound.note, fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }

        if (isSelected) {
            Icon(
                painter = painterResource(id = R.drawable.ic_check),
                contentDescription = "Selected",
                tint = ShMatcha,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}
