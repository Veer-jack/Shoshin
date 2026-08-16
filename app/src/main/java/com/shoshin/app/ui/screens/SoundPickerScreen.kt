package com.Shoshin.app.ui.screens

import android.content.Context
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

/**
 * Shoshin ships no audio of its own, so each slot in [SH_SOUNDS_LIST] is bound to a
 * distinct tone already installed on the device. Slots are assigned by list position
 * so a given id always resolves to the same tone, and the resolution is shared by the
 * picker preview and [com.Shoshin.app.alarm.AlarmService] — otherwise the tone a user
 * auditions would not be the one that actually wakes them.
 */
object ShAlarmTones {

    @Volatile private var cachedTones: List<Pair<Uri, String>>? = null

    /** Alarm tones first (they're built to wake people), topped up with ringtones. */
    private fun deviceTones(context: Context): List<Pair<Uri, String>> {
        cachedTones?.let { return it }
        val collected = linkedMapOf<String, Pair<Uri, String>>()
        listOf(RingtoneManager.TYPE_ALARM, RingtoneManager.TYPE_RINGTONE).forEach { type ->
            try {
                val manager = RingtoneManager(context).apply { setType(type) }
                val cursor = manager.cursor
                for (i in 0 until cursor.count) {
                    val uri = manager.getRingtoneUri(i) ?: continue
                    val title = cursor.let {
                        it.moveToPosition(i)
                        it.getString(RingtoneManager.TITLE_COLUMN_INDEX)
                    } ?: continue
                    collected.putIfAbsent(uri.toString(), uri to title)
                }
            } catch (e: Exception) {
                android.util.Log.e("ShAlarmTones", "Could not enumerate tones of type $type", e)
            }
        }
        val result = collected.values.toList()
        // Only cache a real answer — caching an empty list would pin the alarm to the
        // system default forever if the first query happened to fail.
        if (result.isNotEmpty()) cachedTones = result
        return result
    }

    private fun slotFor(soundId: String): Int =
        SH_SOUNDS_LIST.indexOfFirst { it.id == soundId }.takeIf { it >= 0 } ?: 0

    /** The tone that plays for [soundId]; falls back to the system alarm default. */
    fun uriFor(context: Context, soundId: String): Uri? {
        val tones = deviceTones(context)
        if (tones.isEmpty()) return RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
        // Wraps when the device exposes fewer tones than we have slots.
        return tones[slotFor(soundId) % tones.size].first
    }

    /** The device's own name for that tone, so the picker isn't claiming a sound it can't play. */
    fun titleFor(context: Context, soundId: String): String? {
        val tones = deviceTones(context)
        if (tones.isEmpty()) return null
        return tones[slotFor(soundId) % tones.size].second
    }
}

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
        val uri = sound.previewUri
            ?: ShAlarmTones.uriFor(context, sound.id)
            ?: RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
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
                        val deviceToneName = remember(sound.id) { ShAlarmTones.titleFor(context, sound.id) }
                        SoundRowDark(
                            sound = sound,
                            deviceToneName = deviceToneName,
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
    deviceToneName: String?,
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
            Text(
                // Name the actual device tone when we resolved one, so the label matches
                // what will play at wake time.
                text = deviceToneName?.let { "${sound.note} · $it" } ?: sound.note,
                style = ShLabelStyle.copy(fontSize = 13.sp, color = ShNightMuted),
                maxLines = 1,
                overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
            )
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
