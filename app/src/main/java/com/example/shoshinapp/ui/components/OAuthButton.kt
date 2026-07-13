package com.example.shoshinapp.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.shoshinapp.ui.theme.*
import com.example.shoshinapp.R

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
    val (bg, fg, border, label, iconRes) = when (provider) {
        OAuthProvider.Google -> OAuthConfig(
            bg      = ShSurface,
            fg      = ShInk,
            border  = ShLine2,
            label   = "Continue with Google",
            iconRes = R.drawable.ic_google,
        )
        OAuthProvider.Apple -> OAuthConfig(
            bg      = ShInk,
            fg      = Color.White,
            border  = ShInk,
            label   = "Continue with Apple",
            iconRes = R.drawable.ic_apple,
        )
    }

    OutlinedButton(
        onClick  = onClick,
        modifier = modifier.fillMaxWidth().height(54.dp),
        shape    = ShRadiusButton,
        border   = BorderStroke(1.5.dp, border),
        enabled  = enabled,
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
