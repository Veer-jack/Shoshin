package com.Shoshin.app.ui.screens

import android.media.MediaPlayer
import android.media.RingtoneManager
import android.net.Uri
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.Shoshin.app.R
import com.Shoshin.app.data.ShoshinRepository
import com.Shoshin.app.ui.components.*
import com.Shoshin.app.ui.theme.*
import kotlinx.coroutines.launch

data class ShSound(val id: String, val name: String, val note: String, val previewUri: Uri? = null)

val SH_SOUNDS_LIST = listOf(
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
    val context = LocalContext.current
    val repo = remember { ShoshinRepository(context) }
    val scope = rememberCoroutineScope()

    val savedTone by repo.alarmTone.collectAsState(initial = "bell")
    val savedIntensity by repo.alarmIntensity.collectAsState(initial = 7)
    val savedType by repo.alarmType.collectAsState(initial = "Normal")

    var selectedSoundId by remember(savedTone) { mutableStateOf(savedTone) }
    var volume by remember(savedIntensity) { mutableFloatStateOf(savedIntensity.toFloat() / 10f) }
    val scrollState = rememberScrollState()
    
    var mediaPlayer by remember { mutableStateOf<MediaPlayer?>(null) }

    DisposableEffect(Unit) {
        onDispose {
            mediaPlayer?.release()
            mediaPlayer = null
        }
    }

    fun playPreview(sound: ShSound) {
        mediaPlayer?.stop()
        mediaPlayer?.release()
        val uri = sound.previewUri ?: RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
        try {
            mediaPlayer = MediaPlayer.create(context, uri).apply {
                if (this != null) {
                    setVolume(volume, volume)
                    start()
                    setOnCompletionListener { 
                        it.release()
                        if (mediaPlayer == it) mediaPlayer = null
                    }
                }
            }
        } catch (e: Exception) {
            android.util.Log.e("SoundPicker", "Failed to play preview", e)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(ShNight)
    ) {
        // App Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { navController.popBackStack() }) {
                Icon(painterResource(R.drawable.ic_arrow_left), contentDescription = "Back", tint = Color.White)
            }
            Text("Wake sound", style = ShTitleStyle.copy(fontSize = 32.sp, color = Color.White))
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(horizontal = 24.dp)
        ) {
            Spacer(Modifier.height(24.dp))

            // Volume Card
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(24.dp))
                    .background(ShNight2)
                    .padding(24.dp)
            ) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Kicker("VOLUME", color = ShNightMuted)
                        Text(
                            text = "${(volume * 100).toInt()}%",
                            style = ShNumeralStyle.copy(fontSize = 15.sp, color = Color.White)
                        )
                    }
                    
                    Spacer(Modifier.height(16.dp))
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Icon(painterResource(R.drawable.ic_moon), null, modifier = Modifier.size(18.dp), tint = ShNightMuted)
                        Slider(
                            value = volume,
                            onValueChange = { 
                                volume = it 
                                mediaPlayer?.setVolume(it, it)
                                scope.launch { repo.saveAlarmSettings(selectedSoundId, savedType, (it * 10).toInt()) }
                            },
                            modifier = Modifier.weight(1f),
                            colors = SliderDefaults.colors(
                                thumbColor = ShVermillion,
                                activeTrackColor = ShVermillion,
                                inactiveTrackColor = ShNight3
                            )
                        )
                        Icon(painterResource(R.drawable.ic_bell), null, modifier = Modifier.size(18.dp), tint = ShNightMuted)
                    }
                }
            }

            Spacer(Modifier.height(32.dp))
            Kicker("SOUNDS", color = ShNightMuted, modifier = Modifier.padding(bottom = 16.dp))

            // Sounds List Card
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(24.dp))
                    .background(ShNight2)
                    .padding(horizontal = 8.dp, vertical = 12.dp)
            ) {
                Column {
                    SH_SOUNDS_LIST.forEachIndexed { index, sound ->
                        val isSelected = selectedSoundId == sound.id
                        SoundRowDark(
                            sound = sound,
                            isSelected = isSelected,
                            onClick = { 
                                selectedSoundId = sound.id
                                playPreview(sound)
                                scope.launch { repo.saveAlarmSettings(sound.id, savedType, (volume * 10).toInt()) }
                            }
                        )
                        if (index < SH_SOUNDS_LIST.lastIndex) {
                            HorizontalDivider(color = ShNightLine, thickness = 1.dp, modifier = Modifier.padding(horizontal = 16.dp))
                        }
                    }
                }
            }

            Spacer(Modifier.height(48.dp))
        }
    }
}

@Composable
private fun SoundRowDark(
    sound: ShSound,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(if (isSelected) Color.White else ShNight3),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_play),
                contentDescription = "Play",
                tint = if (isSelected) Color.Black else Color.White,
                modifier = Modifier.size(16.dp)
            )
        }
        
        Column(modifier = Modifier.weight(1f)) {
            Text(sound.name, style = ShH2Style.copy(fontSize = 16.sp, color = Color.White))
            Text(sound.note, style = ShLabelStyle.copy(fontSize = 13.sp, color = ShNightMuted))
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
