package com.example.shoshinapp.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.res.painterResource
import com.example.shoshinapp.R
import com.example.shoshinapp.ui.theme.*

// ── ShoshinKeypad ─────────────────────────────────────────────
@Composable
fun ShoshinKeypad(
    onDigit: (String) -> Unit,
    onClear: () -> Unit,
    onOk: () -> Unit,
    modifier: Modifier = Modifier,
    dark: Boolean = true
) {
    val keys = listOf("1", "2", "3", "4", "5", "6", "7", "8", "9", "del", "0", "ok")
    Column(modifier = modifier) {
        keys.chunked(3).forEach { row ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 10.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                row.forEach { k ->
                    val isOk = k == "ok"
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(58.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .background(
                                when {
                                    isOk -> ShVermillion
                                    dark -> ShNightText.copy(alpha = 0.05f)
                                    else -> ShPaper2
                                }
                            )
                            .border(
                                width = 1.dp,
                                color = when {
                                    isOk -> ShVermillion
                                    dark -> ShNightText.copy(alpha = 0.08f)
                                    else -> ShLine
                                },
                                shape = RoundedCornerShape(14.dp)
                            )
                            .clickable {
                                when (k) {
                                    "del" -> onClear()
                                    "ok" -> onOk()
                                    else -> onDigit(k)
                                }
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        when (k) {
                            "del" -> Icon(
                                painter = painterResource(id = R.drawable.ic_backspace),
                                contentDescription = "Delete",
                                tint = if (dark) ShNightText else ShInk,
                                modifier = Modifier.size(22.dp)
                            )
                            "ok" -> Icon(
                                painter = painterResource(id = R.drawable.ic_check),
                                contentDescription = "OK",
                                tint = ShPaper,
                                modifier = Modifier.size(24.dp)
                            )
                            else -> Text(
                                text = k,
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = DmSansFamily,
                                color = if (dark) ShNightText else ShInk
                            )
                        }
                    }
                }
            }
        }
    }
}

// ── ShoshinCard ───────────────────────────────────────────────
@Composable
fun ShoshinCard(
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = modifier,
        shape    = RoundedCornerShape(20.dp),
        colors   = CardDefaults.cardColors(containerColor = ShSurface),
        border   = androidx.compose.foundation.BorderStroke(1.dp, ShLine),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(content = content)
    }
}

// ── Kicker Label ──────────────────────────────────────────────
@Composable
fun Kicker(
    text: String,
    color: Color = ShFog,
    modifier: Modifier = Modifier
) {
    Text(
        text          = text.uppercase(),
        fontSize      = 11.sp,
        fontWeight    = FontWeight.Medium,
        fontFamily    = DmSansFamily,
        letterSpacing = 2.2.sp,
        color         = color,
        modifier      = modifier
    )
}

// ── Checkpoint Row ────────────────────────────────────────────
enum class CheckpointState { PENDING, ACTIVE, DONE }

@Composable
fun CheckpointRow(
    number: Int,
    label: String,
    state: CheckpointState = CheckpointState.PENDING,
    time: String? = null
) {
    val nodeColor = when (state) {
        CheckpointState.DONE   -> ShMatcha
        CheckpointState.ACTIVE -> ShInk
        CheckpointState.PENDING -> Color.Transparent
    }
    val nodeBorder = when (state) {
        CheckpointState.DONE   -> ShMatcha
        CheckpointState.ACTIVE -> ShInk
        CheckpointState.PENDING -> ShLine2
    }
    val labelColor = when (state) {
        CheckpointState.DONE, CheckpointState.PENDING -> ShFog
        CheckpointState.ACTIVE -> ShInk
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 11.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(34.dp)
                .clip(CircleShape)
                .background(nodeColor)
                .border(1.5.dp, nodeBorder, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = if (state == CheckpointState.DONE) "✓" else number.toString(),
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = if (state == CheckpointState.DONE) ShPaper
                        else if (state == CheckpointState.ACTIVE) ShInk
                        else ShFog
            )
        }
        Spacer(Modifier.width(14.dp))
        Text(
            text = label,
            fontSize = 15.sp,
            fontWeight = FontWeight.Medium,
            fontFamily = DmSansFamily,
            color = labelColor,
            modifier = Modifier.weight(1f)
        )
        time?.let {
            Text(
                text = it,
                fontSize = 12.sp,
                color = ShFog2,
                fontFamily = DmSansFamily
            )
        }
    }
}

// ── Settings Row ──────────────────────────────────────────────
@Composable
fun SettingsRow(
    title: String,
    subtitle: String? = null,
    value: String? = null,
    toggle: Boolean? = null,
    onToggle: ((Boolean) -> Unit)? = null,
    onClick: (() -> Unit)? = null,
    danger: Boolean = false
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(enabled = onClick != null) { onClick?.invoke() }
            .padding(vertical = 15.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                fontSize = 15.5.sp,
                fontWeight = FontWeight.Medium,
                fontFamily = DmSansFamily,
                color = if (danger) ShVermillion else ShInk
            )
            subtitle?.let {
                Text(
                    text = it,
                    fontSize = 12.5.sp,
                    color = ShFog,
                    fontFamily = DmSansFamily,
                    modifier = Modifier.padding(top = 1.dp)
                )
            }
        }
        value?.let {
            Text(text = it, fontSize = 14.sp, color = ShFog, fontFamily = DmSansFamily)
            Spacer(Modifier.width(4.dp))
        }
        toggle?.let {
            ShoshinToggle(checked = it, onCheckedChange = { v -> onToggle?.invoke(v) })
        }
        if (onClick != null && toggle == null) {
            Text(text = "›", fontSize = 18.sp, color = ShFog2)
        }
    }
    HorizontalDivider(color = ShLine, thickness = 1.dp)
}

// ── Ring Progress ─────────────────────────────────────────────
@Composable
fun RingProgress(
    percentage: Int,
    size: Int = 120,
    strokeWidth: Float = 10f,
    label: String? = null,
    valueText: String,
    color: Color = ShInk,
    trackColor: Color = ShSand,
    dark: Boolean = false
) {
    Box(
        modifier = Modifier.size(size.dp),
        contentAlignment = Alignment.Center
    ) {
        androidx.compose.foundation.Canvas(modifier = Modifier.size(size.dp)) {
            val sweepAngle = (percentage / 100f) * 360f
            drawArc(
                color = trackColor,
                startAngle = -90f,
                sweepAngle = 360f,
                useCenter = false,
                style = androidx.compose.ui.graphics.drawscope.Stroke(
                    width = strokeWidth.dp.toPx(),
                    cap = androidx.compose.ui.graphics.StrokeCap.Round
                )
            )
            drawArc(
                color = color,
                startAngle = -90f,
                sweepAngle = sweepAngle,
                useCenter = false,
                style = androidx.compose.ui.graphics.drawscope.Stroke(
                    width = strokeWidth.dp.toPx(),
                    cap = androidx.compose.ui.graphics.StrokeCap.Round
                )
            )
        }
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = valueText,
                fontSize = (size * 0.26).sp,
                fontWeight = FontWeight.Bold,
                fontFamily = DmSansFamily,
                color = if (dark) ShNightText else ShInk
            )
            label?.let {
                Text(
                    text = it.uppercase(),
                    fontSize = 10.sp,
                    fontWeight = FontWeight.SemiBold,
                    letterSpacing = 1.5.sp,
                    color = ShFog,
                    fontFamily = DmSansFamily
                )
            }
        }
    }
}

// ── Streak Grid ───────────────────────────────────────────────
@Composable
fun StreakGrid(
    total: Int = 21,
    done: Int = 0,
    today: Int? = null,
    columns: Int = 7,
    dark: Boolean = false
) {
    val todayIdx = today ?: done
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        (0 until total).chunked(columns).forEach { row ->
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                row.forEach { i ->
                    val isDone    = i < done
                    val isToday   = i == todayIdx
                    val bg        = when { isDone -> ShInk; else -> if (dark) ShNight3 else ShSurface }
                    val borderClr = when { isToday -> ShVermillion; isDone -> ShInk; else -> ShLine }
                    val borderW   = if (isToday) 2.dp else 1.dp
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .aspectRatio(1f)
                            .clip(RoundedCornerShape(8.dp))
                            .background(bg)
                            .border(borderW, borderClr, RoundedCornerShape(8.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = if (isDone) "✓" else (i + 1).toString(),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = when { isDone -> ShPaper; isToday -> ShVermillion; else -> ShFog }
                        )
                    }
                }
                // fill empty spots in last row
                repeat(columns - row.size) {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }
    }
}

