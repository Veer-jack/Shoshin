package com.example.shoshinapp.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.shoshinapp.ui.theme.*

data class SegmentOption<T>(val id: T, val label: String)

@Composable
fun <T> ShoshinSegmented(
    options: List<SegmentOption<T>>,
    selected: T,
    onSelect: (T) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(48.dp)
            .background(ShPaper2, RoundedCornerShape(12.dp))
            .padding(4.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        options.forEach { option ->
            val isSelected = option.id == selected
            val bgColor by animateColorAsState(
                targetValue = if (isSelected) Color.White else Color.Transparent,
                label = "segment_bg"
            )
            val textColor by animateColorAsState(
                targetValue = if (isSelected) ShInk else ShFog,
                label = "segment_text"
            )

            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .background(
                        color = bgColor,
                        shape = RoundedCornerShape(8.dp)
                    )
                    .clickable { onSelect(option.id) },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = option.label,
                    color = textColor,
                    fontFamily = DmSansFamily,
                    fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Medium,
                    fontSize = 14.sp
                )
            }
        }
    }
}
