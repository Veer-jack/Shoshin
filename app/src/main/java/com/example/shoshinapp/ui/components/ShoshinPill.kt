package com.example.shoshinapp.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.shoshinapp.ui.theme.*

// ============================================================
// ShoshinPill (Chip / Badge)
// Height: 26dp · Padding: 0 12dp · Radius: 999dp (full pill)
// Font: DM Sans 600 11.5sp · Gap icon+label: 6dp
// ============================================================

enum class ShPillVariant { Default, Ink, Accent, Matcha, Outline, Dark }

@Composable
fun ShoshinPill(
    label: String,
    modifier: Modifier = Modifier,
    variant: ShPillVariant = ShPillVariant.Default,
    leadingContent: @Composable (() -> Unit)? = null,
) {
    val (bg, fg, border) = when (variant) {
        ShPillVariant.Default  -> Triple(ShSand,      ShInk,       Color.Transparent)
        ShPillVariant.Ink      -> Triple(ShInk,        ShNightText, Color.Transparent)
        ShPillVariant.Accent   -> Triple(ShVermillion, Color.White, Color.Transparent)
        ShPillVariant.Matcha   -> Triple(ShMatchaLight, ShMatcha,  Color.Transparent)
        ShPillVariant.Outline  -> Triple(Color.Transparent, ShFog, ShLine2)
        ShPillVariant.Dark     -> Triple(ShNight3,     ShNightText, ShNightBorder)
    }

    Row(
        modifier = modifier
            .height(26.dp)
            .then(
                if (border != Color.Transparent)
                    Modifier.border(1.5.dp, border, RoundedCornerShape(999.dp))
                else Modifier
            )
            .background(bg, RoundedCornerShape(999.dp))
            .padding(horizontal = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        leadingContent?.invoke()
        Text(
            text       = label,
            fontFamily = DmSansFamily,
            fontWeight = FontWeight.SemiBold,
            fontSize   = 11.5.sp,
            color      = fg,
        )
    }
}
