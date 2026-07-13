package com.example.shoshinapp.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.shoshinapp.R
import com.example.shoshinapp.ui.theme.*

@Composable
fun ShoshinKeypad(
    onDigit: (String) -> Unit,
    onClear: () -> Unit,
    onOk: () -> Unit,
    modifier: Modifier = Modifier
) {
    val keys = listOf(
        listOf("1", "2", "3"),
        listOf("4", "5", "6"),
        listOf("7", "8", "9"),
        listOf("clear", "0", "ok")
    )

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        keys.forEach { row ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                row.forEach { key ->
                    KeypadButton(
                        key = key,
                        onClick = {
                            when (key) {
                                "clear" -> onClear()
                                "ok" -> onOk()
                                else -> onDigit(key)
                            }
                        },
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}

@Composable
private fun KeypadButton(
    key: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .height(60.dp)
            .clip(ShRadiusButton)
            .clickable { onClick() }
            .background(ShPaper2),
        contentAlignment = Alignment.Center
    ) {
        when (key) {
            "clear" -> Icon(
                painter = painterResource(R.drawable.ic_arrow_left),
                contentDescription = "Clear",
                tint = ShInk,
                modifier = Modifier.size(24.dp)
            )
            "ok" -> Icon(
                painter = painterResource(R.drawable.ic_check),
                contentDescription = "OK",
                tint = ShInk,
                modifier = Modifier.size(24.dp)
            )
            else -> Text(
                text = key,
                fontFamily = DmSansFamily,
                fontWeight = FontWeight.SemiBold,
                fontSize = 24.sp,
                color = ShInk
            )
        }
    }
}
