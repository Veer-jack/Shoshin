package com.shoshin.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.shoshin.app.ui.theme.*

@Composable
fun ShoshinLogoMark(modifier: Modifier = Modifier) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.Bottom
    ) {
        Box(modifier = Modifier.width(8.dp).height(14.dp).background(ShFog, RoundedCornerShape(2.dp)))
        Box(modifier = Modifier.width(8.dp).height(22.dp).background(ShMatcha, RoundedCornerShape(2.dp)))
        Box(modifier = Modifier.width(8.dp).height(32.dp).background(ShVermillion, RoundedCornerShape(2.dp)))
    }
}
