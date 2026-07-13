package com.example.shoshinapp.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp

// ============================================================
// Shoshin (初心) — Shape Tokens
// ============================================================

val ShShapes = Shapes(
    // Material3 slots
    extraSmall = RoundedCornerShape(8.dp),   // sh_radius_sm
    small      = RoundedCornerShape(12.dp),  // sh_radius_md
    medium     = RoundedCornerShape(14.dp),  // sh_radius_button
    large      = RoundedCornerShape(20.dp),  // sh_radius_card
    extraLarge = RoundedCornerShape(28.dp),
)

// Named tokens (use directly when precise control needed)
val ShRadiusSm     = RoundedCornerShape(8.dp)
val ShRadiusMd     = RoundedCornerShape(12.dp)
val ShRadiusButton = RoundedCornerShape(14.dp)
val ShRadiusCard   = RoundedCornerShape(20.dp)
val ShRadiusOption = RoundedCornerShape(16.dp)
val ShRadiusChip   = RoundedCornerShape(13.dp)
val ShRadiusPill   = RoundedCornerShape(999.dp)
val ShRadiusOtp    = RoundedCornerShape(12.dp)
