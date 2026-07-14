package com.example.shoshinapp.ui.components

import android.app.TimePickerDialog
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import java.util.*

@Composable
fun TimePickerDialog(
    onDismiss: () -> Unit,
    onTimeSelected: (Int, Int) -> Unit,
    title: String,
    initialHour: Int? = null,
    initialMinute: Int? = null
) {
    val context = LocalContext.current
    
    LaunchedEffect(Unit) {
        val calendar = Calendar.getInstance()
        val hour = initialHour ?: calendar.get(Calendar.HOUR_OF_DAY)
        val minute = initialMinute ?: calendar.get(Calendar.MINUTE)

        val dialog = android.app.TimePickerDialog(
            context,
            { _, h, m -> onTimeSelected(h, m) },
            hour,
            minute,
            true // 24 hour format
        )

        dialog.setOnDismissListener { onDismiss() }
        dialog.setTitle(title)
        dialog.show()
    }
}
