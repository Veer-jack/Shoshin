package com.Shoshin.app.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.LocalIndication
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
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.res.painterResource
import com.Shoshin.app.R
import com.Shoshin.app.ui.theme.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

// ── ShoshinKeypad ─────────────────────────────────────────────
@Composable
fun ShoshinKeypad(
    onDigit: (String) -> Unit,
    onClear: () -> Unit,
    onOk: () -> Unit,
    modifier: Modifier = Modifier
) {
    val keys = listOf("1", "2", "3", "4", "5", "6", "7", "8", "9", "del", "0", "ok")
    val scope = rememberCoroutineScope()
    val haptic = LocalHapticFeedback.current
    var okExecuting by remember { mutableStateOf(false) }

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
                    val isDel = k == "del"
                    
                    val keyInteractionSource = remember { MutableInteractionSource() }
                    val isKeyPressed by keyInteractionSource.collectIsPressedAsState()
                    val keyScale by animateFloatAsState(
                        targetValue = if (isKeyPressed || (isOk && okExecuting)) 0.96f else 1f,
                        animationSpec = tween(100),
                        label = "key_scale"
                    )

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(64.dp)
                            .scale(keyScale)
                            .clip(RoundedCornerShape(12.dp))
                            .background(
                                when {
                                    isOk -> ShVermillion
                                    else -> MaterialTheme.colorScheme.surfaceVariant
                                }
                            )
                            .then(
                                if (isOk && (isKeyPressed || okExecuting)) 
                                    Modifier.shShadow(ShShadowLevel.Small, RoundedCornerShape(12.dp))
                                else Modifier
                            )
                            .clickable(
                                interactionSource = keyInteractionSource,
                                indication = LocalIndication.current
                            ) {
                                when (k) {
                                    "del" -> onClear()
                                    "ok" -> {
                                        if (!okExecuting) {
                                            scope.launch {
                                                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                                okExecuting = true
                                                delay(150)
                                                onOk()
                                                okExecuting = false
                                            }
                                        }
                                    }
                                    else -> onDigit(k)
                                }
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        when {
                            isDel -> Icon(
                                painter = painterResource(id = R.drawable.ic_backspace),
                                contentDescription = "Delete",
                                tint = MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier.size(24.dp)
                            )
                            isOk -> Icon(
                                painter = painterResource(id = R.drawable.ic_arrow_right),
                                contentDescription = "Continue",
                                tint = Color.White,
                                modifier = Modifier.size(24.dp)
                            )
                            else -> Text(
                                text = k,
                                fontSize = 24.sp,
                                fontWeight = FontWeight.SemiBold,
                                fontFamily = DmSansFamily,
                                color = MaterialTheme.colorScheme.onSurface
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
    val shape = RoundedCornerShape(20.dp)
    Card(
        modifier = modifier.shShadow(ShShadowLevel.Medium, shape),
        shape    = shape,
        colors   = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.onSurface
        ),
        border   = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
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

    // Draws in from 0 on mount and eases to the new value on change, per motion spec (900ms, never re-animates on plain re-render)
    val reducedMotion = rememberReducedMotion()
    val animatedPercentage by animateFloatAsState(
        targetValue = percentage.toFloat(),
        animationSpec = androidx.compose.animation.core.tween(
            if (reducedMotion) 0 else 900,
            easing = androidx.compose.animation.core.FastOutSlowInEasing,
        ),
        label = "ring_progress"
    )

    Box(
        modifier = Modifier.size(size.dp),
        contentAlignment = Alignment.Center
    ) {
        androidx.compose.foundation.Canvas(modifier = Modifier.size(size.dp)) {
            val sweepAngle = (animatedPercentage / 100f) * 360f
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
    val scope = rememberCoroutineScope()
    val haptic = LocalHapticFeedback.current
    var isExecuting by remember { mutableStateOf(false) }

    val scale by animateFloatAsState(
        targetValue = if (isPressed || isExecuting) 0.985f else 1f,
        animationSpec = androidx.compose.animation.core.tween(120),
        label = "button_scale",
    )

    val isDark = MaterialTheme.colorScheme.background == ShNight

    val defaultContainerColor = when (variant) {
        ShButtonVariant.Primary -> if (!isDark) Color.White else ShNightText
        ShButtonVariant.Accent -> ShVermillion
        ShButtonVariant.Ghost -> MaterialTheme.colorScheme.surface
        ShButtonVariant.Dark -> ShNight2
        ShButtonVariant.Matcha -> ShMatcha
    }
    
    val containerColor = if ((isPressed || isExecuting) && pressedColor != null) pressedColor else defaultContainerColor

    val contentColor = when (variant) {
        ShButtonVariant.Primary -> if (!isDark) ShInk else ShNight
        ShButtonVariant.Accent -> Color.White
        ShButtonVariant.Ghost -> MaterialTheme.colorScheme.onBackground
        ShButtonVariant.Dark -> Color.White
        ShButtonVariant.Matcha -> Color.White
    }
    val border = if (variant == ShButtonVariant.Ghost || (variant == ShButtonVariant.Primary && !isDark)) {
        androidx.compose.foundation.BorderStroke(1.5.dp, MaterialTheme.colorScheme.outline)
    } else if (variant == ShButtonVariant.Dark) {
        androidx.compose.foundation.BorderStroke(1.dp, ShNightBorder)
    } else null

    val shadowLevel = when {
        isPressed || isExecuting -> ShShadowLevel.Medium
        variant == ShButtonVariant.Ghost || (variant == ShButtonVariant.Primary && !isDark) -> ShShadowLevel.Small
        else -> null
    }

    Button(
        onClick = {
            if (!isExecuting && enabled) {
                scope.launch {
                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                    isExecuting = true
                    delay(150)
                    onClick()
                    isExecuting = false
                }
            }
        },
        modifier = modifier
            .height(56.dp)
            .scale(scale)
            .then(if (shadowLevel != null) Modifier.shShadow(shadowLevel, RoundedCornerShape(14.dp)) else Modifier),
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

// ── ConfettiBurst (celebration moments, motion spec §3.3) ─────
// 8-12 small vermillion/matcha dots, radiate + fade, 800ms, one-shot.
// Respects reduced motion (skipped entirely — decorative only).
@Composable
fun ConfettiBurst(
    trigger: Boolean,
    modifier: Modifier = Modifier,
    particleCount: Int = 10,
) {
    if (rememberReducedMotion()) return

    val progress = remember { androidx.compose.animation.core.Animatable(0f) }
    LaunchedEffect(trigger) {
        if (trigger) {
            progress.snapTo(0f)
            progress.animateTo(
                1f,
                animationSpec = androidx.compose.animation.core.tween(
                    800,
                    easing = androidx.compose.animation.core.FastOutSlowInEasing,
                ),
            )
        }
    }

    val particles = remember {
        List(particleCount) {
            val angle = kotlin.random.Random.nextFloat() * (2 * Math.PI).toFloat()
            val distance = 70f + kotlin.random.Random.nextFloat() * 50f
            Triple(angle, distance, it % 2 == 0)
        }
    }

    androidx.compose.foundation.Canvas(modifier = modifier.fillMaxSize()) {
        val progressValue = progress.value
        if (progressValue <= 0f) return@Canvas
        val center = androidx.compose.ui.geometry.Offset(size.width / 2f, size.height / 2f)
        val alpha = (1f - progressValue).coerceIn(0f, 1f)
        particles.forEach { (angle, distance, isVermillion) ->
            val r = distance.dp.toPx() * progressValue
            drawCircle(
                color = (if (isVermillion) ShVermillionLight else ShMatchaLightToken).copy(alpha = alpha),
                radius = 3.dp.toPx(),
                center = androidx.compose.ui.geometry.Offset(
                    center.x + kotlin.math.cos(angle) * r,
                    center.y + kotlin.math.sin(angle) * r,
                ),
            )
        }
    }
}

// ── Badge Helpers ─────────────────────────────────────────────
fun getBadgeIconRes(iconName: String): Int {
    return when (iconName) {
        "streak_7", "streak_30", "streak_100", "streak_365", "flame" -> R.drawable.ic_flame
        "milestone", "check" -> R.drawable.ic_check
        "groups", "community" -> R.drawable.ic_groups
        "plus" -> R.drawable.ic_plus
        "sun", "early_riser" -> R.drawable.ic_sun
        "book", "scholar" -> R.drawable.ic_book
        "share" -> R.drawable.ic_share
        "shield", "security" -> R.drawable.ic_shield
        "trophy", "best" -> R.drawable.ic_trophy
        else -> R.drawable.ic_trophy
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
