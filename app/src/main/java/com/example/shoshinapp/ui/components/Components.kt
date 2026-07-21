package com.example.shoshinapp.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
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
    val keys = listOf("1", "2", "3", "4", "5", "6", "7", "8", "9", "+", "0", "del")
    Column(modifier = modifier) {
        keys.chunked(3).forEach { row ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 10.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                row.forEach { k ->
                    val isSpecial = k == "del" || k == "+"
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(58.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .background(
                                when {
                                    k == "del" -> ShVermillion
                                    dark -> ShNightText.copy(alpha = 0.05f)
                                    else -> MaterialTheme.colorScheme.surfaceVariant
                                }
                            )
                            .border(
                                width = 1.dp,
                                color = when {
                                    k == "del" -> ShVermillion
                                    dark -> ShNightText.copy(alpha = 0.08f)
                                    else -> MaterialTheme.colorScheme.outline
                                },
                                shape = RoundedCornerShape(14.dp)
                            )
                            .clickable {
                                when (k) {
                                    "del" -> onClear()
                                    "+" -> onDigit("+")
                                    else -> onDigit(k)
                                }
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        when (k) {
                            "del" -> Icon(
                                painter = painterResource(id = R.drawable.ic_backspace),
                                contentDescription = "Delete",
                                tint = ShPaper,
                                modifier = Modifier.size(22.dp)
                            )
                            "+" -> Text(
                                text = "+",
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = DmSansFamily,
                                color = if (dark) ShNightText else MaterialTheme.colorScheme.onSurface
                            )
                            else -> Text(
                                text = k,
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = DmSansFamily,
                                color = if (dark) ShNightText else MaterialTheme.colorScheme.onSurface
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
        colors   = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.onSurface
        ),
        border   = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)),
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
        CheckpointState.ACTIVE -> MaterialTheme.colorScheme.onBackground
        CheckpointState.PENDING -> Color.Transparent
    }
    val nodeBorder = when (state) {
        CheckpointState.DONE   -> ShMatcha
        CheckpointState.ACTIVE -> MaterialTheme.colorScheme.onBackground
        CheckpointState.PENDING -> MaterialTheme.colorScheme.outline
    }
    val labelColor = when (state) {
        CheckpointState.DONE, CheckpointState.PENDING -> MaterialTheme.colorScheme.onSurfaceVariant
        CheckpointState.ACTIVE -> MaterialTheme.colorScheme.onBackground
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
                color = if (state == CheckpointState.DONE) Color.White
                        else if (state == CheckpointState.ACTIVE) MaterialTheme.colorScheme.background
                        else MaterialTheme.colorScheme.onSurfaceVariant
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
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
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
                color = if (danger) ShVermillion else MaterialTheme.colorScheme.onSurface
            )
            subtitle?.let {
                Text(
                    text = it,
                    fontSize = 12.5.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontFamily = DmSansFamily,
                    modifier = Modifier.padding(top = 1.dp)
                )
            }
        }
        value?.let {
            Text(text = it, fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, fontFamily = DmSansFamily)
            Spacer(Modifier.width(4.dp))
        }
        toggle?.let {
            ShoshinToggle(checked = it, onCheckedChange = { v -> onToggle?.invoke(v) })
        }
        if (onClick != null && toggle == null) {
            Text(text = "›", fontSize = 18.sp, color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f))
        }
    }
    HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f), thickness = 1.dp)
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
    val finalColor = if (color == ShInk) MaterialTheme.colorScheme.onBackground else color
    val finalTrackColor = if (trackColor == ShSand) MaterialTheme.colorScheme.surfaceVariant else trackColor
    
    Box(
        modifier = Modifier.size(size.dp),
        contentAlignment = Alignment.Center
    ) {
        androidx.compose.foundation.Canvas(modifier = Modifier.size(size.dp)) {
            val sweepAngle = (percentage / 100f) * 360f
            drawArc(
                color = finalTrackColor,
                startAngle = -90f,
                sweepAngle = 360f,
                useCenter = false,
                style = androidx.compose.ui.graphics.drawscope.Stroke(
                    width = strokeWidth.dp.toPx(),
                    cap = androidx.compose.ui.graphics.StrokeCap.Round
                )
            )
            drawArc(
                color = finalColor,
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
                color = if (dark) ShNightText else MaterialTheme.colorScheme.onBackground
            )
            label?.let {
                Text(
                    text = it.uppercase(),
                    fontSize = 10.sp,
                    fontWeight = FontWeight.SemiBold,
                    letterSpacing = 1.5.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
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
                    val bg        = when { 
                        isDone -> MaterialTheme.colorScheme.onBackground
                        else -> if (dark) ShNight3 else MaterialTheme.colorScheme.surface 
                    }
                    val borderClr = when { 
                        isToday -> ShVermillion
                        isDone -> MaterialTheme.colorScheme.onBackground
                        else -> MaterialTheme.colorScheme.outline 
                    }
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
                            color = when { 
                                isDone -> MaterialTheme.colorScheme.background
                                isToday -> ShVermillion
                                else -> MaterialTheme.colorScheme.onSurfaceVariant 
                            }
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

// ── Enso Motif ────────────────────────────────────────────────
@Composable
fun Enso(
    size: Int = 180,
    color: Color = ShVermillion,
    strokeWidth: Float = 7f,
    modifier: Modifier = Modifier
) {
    androidx.compose.foundation.Canvas(modifier = modifier.size(size.dp)) {
        drawArc(
            color = color,
            startAngle = -90f,
            sweepAngle = 310f,
            useCenter = false,
            style = androidx.compose.ui.graphics.drawscope.Stroke(
                width = strokeWidth.dp.toPx(),
                cap = androidx.compose.ui.graphics.StrokeCap.Round
            )
        )
    }
}

// ── ShoshinButton ─────────────────────────────────────────────
enum class ShButtonVariant { Primary, Accent, Ghost, Matcha, Dark }

@Composable
fun ShoshinButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    variant: ShButtonVariant = ShButtonVariant.Primary,
    enabled: Boolean = true,
    leadingIcon: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    pressedColor: Color? = null,
    contentPadding: PaddingValues = PaddingValues(horizontal = 24.dp),
    content: @Composable RowScope.() -> Unit
) {
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.985f else 1f,
        animationSpec = androidx.compose.animation.core.tween(120),
        label = "button_scale",
    )

    val defaultContainerColor = when (variant) {
        ShButtonVariant.Primary -> MaterialTheme.colorScheme.onBackground
        ShButtonVariant.Accent -> ShVermillion
        ShButtonVariant.Ghost -> Color.White
        ShButtonVariant.Dark -> MaterialTheme.colorScheme.surfaceVariant
        ShButtonVariant.Matcha -> ShMatcha
    }
    
    val containerColor = if (isPressed && pressedColor != null) pressedColor else defaultContainerColor

    val contentColor = when (variant) {
        ShButtonVariant.Primary -> MaterialTheme.colorScheme.background
        ShButtonVariant.Accent -> Color.White
        ShButtonVariant.Ghost -> MaterialTheme.colorScheme.onBackground
        ShButtonVariant.Dark -> MaterialTheme.colorScheme.onSurface
        ShButtonVariant.Matcha -> Color.White
    }
    val border = if (variant == ShButtonVariant.Ghost) {
        androidx.compose.foundation.BorderStroke(1.5.dp, MaterialTheme.colorScheme.outline)
    } else if (variant == ShButtonVariant.Dark) {
        androidx.compose.foundation.BorderStroke(1.dp, ShNightBorder)
    } else null

    Button(
        onClick = onClick,
        modifier = modifier
            .height(56.dp)
            .scale(scale),
        enabled = enabled,
        shape = RoundedCornerShape(14.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = containerColor,
            contentColor = contentColor,
            disabledContainerColor = containerColor.copy(alpha = 0.5f),
            disabledContentColor = contentColor.copy(alpha = 0.5f)
        ),
        border = border,
        elevation = null,
        contentPadding = contentPadding,
        interactionSource = interactionSource
    ) {
        if (leadingIcon != null) {
            leadingIcon()
            Spacer(Modifier.width(8.dp))
        }
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            content = content
        )
        if (trailingIcon != null) {
            Spacer(Modifier.width(8.dp))
            trailingIcon()
        }
    }
}

// ── EdgeLayout (Empty / Offline / Error) ──────────────────────
@Composable
fun EdgeLayout(
    icon: Int,
    kicker: String,
    title: String,
    body: String,
    actionLabel: String? = null,
    onAction: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Motif background
        Box(
            modifier = Modifier.size(200.dp),
            contentAlignment = Alignment.Center
        ) {
            Enso(size = 200, color = MaterialTheme.colorScheme.outline.copy(alpha = 0.15f))
            Icon(
                painter = painterResource(id = icon),
                contentDescription = null,
                modifier = Modifier.size(48.dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        
        Spacer(Modifier.height(32.dp))
        
        Kicker(kicker, color = ShVermillion)
        Spacer(Modifier.height(12.dp))
        Text(
            text = title,
            style = ShTitleStyle.copy(fontSize = 28.sp),
            color = MaterialTheme.colorScheme.onBackground,
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(16.dp))
        Text(
            text = body,
            style = ShBodyStyle,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 24.dp)
        )
        
        if (actionLabel != null && onAction != null) {
            Spacer(Modifier.height(40.dp))
            ShoshinButton(
                onClick = onAction,
                variant = ShButtonVariant.Primary,
                modifier = Modifier.widthIn(min = 200.dp)
            ) {
                Text(actionLabel)
            }
        }
    }
}

// ── Shoshin Stat ──────────────────────────────────────────────
@Composable
fun ShoshinStat(
    value: String,
    label: String,
    unit: String? = null,
    color: Color = ShInk,
    align: Alignment.Horizontal = Alignment.CenterHorizontally,
    modifier: Modifier = Modifier
) {
    val finalColor = if (color == ShInk) MaterialTheme.colorScheme.onSurface else color
    
    Column(horizontalAlignment = align, modifier = modifier) {
        Row(verticalAlignment = Alignment.Bottom) {
            Text(
                text = value,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = DmSansFamily,
                color = finalColor,
                lineHeight = 24.sp
            )
            unit?.let {
                Spacer(Modifier.width(2.dp))
                Text(
                    text = it,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    fontFamily = DmSansFamily,
                    color = finalColor.copy(alpha = 0.6f),
                    modifier = Modifier.padding(bottom = 3.dp)
                )
            }
        }
        Spacer(Modifier.height(4.dp))
        Text(
            text = label.uppercase(),
            fontSize = 9.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = DmSansFamily,
            letterSpacing = 1.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
