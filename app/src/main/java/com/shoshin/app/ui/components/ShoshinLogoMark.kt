package com.Shoshin.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.foundation.border
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.Shoshin.app.ui.theme.*

@Composable
fun ShoshinLogoMark(modifier: Modifier = Modifier, on: String = "light") {
    val isNight = on == "night"
    
    Box(
        modifier = modifier
            .size(56.dp)
            .background(Color.Transparent, RoundedCornerShape(16.dp))
            .border(1.5.dp, if (isNight) ShNightMuted else ShLine2, RoundedCornerShape(16.dp)),
        contentAlignment = Alignment.Center
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalAlignment = Alignment.Bottom,
            modifier = Modifier.padding(bottom = 10.dp)
        ) {
            Box(modifier = Modifier.width(7.dp).height(12.dp).background(if (isNight) Color.White else ShInk, RoundedCornerShape(2.dp)))
            Box(modifier = Modifier.width(7.dp).height(20.dp).background(ShMatcha, RoundedCornerShape(2.dp)))
            Box(modifier = Modifier.width(7.dp).height(28.dp).background(ShVermillion, RoundedCornerShape(2.dp)))
        }
    }
}
