package com.Shoshin.app.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.Shoshin.app.ui.theme.*
import com.Shoshin.app.R
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

// OAuthButton — Google & Apple sign-in
// Height: 54dp · Radius: 14dp · Font: DM Sans 600 15sp
// Google: white bg, ShLine2 border
// Apple:  ShInk bg (#1C1C1E), white text

enum class OAuthProvider { Google, Apple }

@Composable
fun OAuthButton(
    provider: OAuthProvider,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    val isDark = MaterialTheme.colorScheme.background == ShNight
    val scope = rememberCoroutineScope()
    val haptic = LocalHapticFeedback.current
    var isExecuting by remember { mutableStateOf(false) }
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val scale by animateFloatAsState(
        targetValue = if (isPressed || isExecuting) 0.985f else 1f,
        label = "oauth_scale"
    )
    
    val (bg, fg, border, label, iconRes) = when (provider) {
        OAuthProvider.Google -> OAuthConfig(
            bg      = Color.White,
            fg      = Color.Black,
            border  = if (isDark) Color.White else MaterialTheme.colorScheme.outline,
            label   = "Continue with Google",
            iconRes = R.drawable.ic_google,
        )
        OAuthProvider.Apple -> OAuthConfig(
            // Fixed hex, no theme swap: pill stays ShInk in both themes (spec: assetReferences.fixedHexNoSwap)
            bg      = ShInk,
            fg      = Color.White,
            border  = ShInk,
            label   = "Continue with Apple",
            iconRes = R.drawable.ic_apple,
        )
    }

    OutlinedButton(
        onClick  = {
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
            .fillMaxWidth()
            .height(54.dp)
            .scale(scale)
            .then(
                if (isPressed || isExecuting) Modifier.shShadow(ShShadowLevel.Small, ShRadiusButton)
                else Modifier
            ),
        shape    = ShRadiusButton,
        border   = BorderStroke(1.5.dp, border),
        enabled  = enabled,
        interactionSource = interactionSource,
        colors   = ButtonDefaults.outlinedButtonColors(
            containerColor = bg,
            contentColor   = fg,
            disabledContainerColor = bg.copy(alpha = 0.5f),
            disabledContentColor = fg.copy(alpha = 0.5f)
        ),
    ) {
        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment     = Alignment.CenterVertically,
        ) {
            Icon(
                painter           = painterResource(iconRes),
                contentDescription = null,
                modifier           = Modifier.size(20.dp),
                tint               = Color.Unspecified,
            )
            Spacer(Modifier.width(10.dp))
            Text(
                text       = label,
                fontFamily = DmSansFamily,
                style      = ShButtonStyle.copy(fontSize = 15.sp),
            )
        }
    }
}

private data class OAuthConfig(
    val bg: Color, val fg: Color, val border: Color,
    val label: String, val iconRes: Int,
)
