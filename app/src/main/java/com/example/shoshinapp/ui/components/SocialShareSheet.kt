package com.example.shoshinapp.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
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

data class SharePlatform(val name: String, val icon: Int, val color: Color)

val PLATFORMS = listOf(
    SharePlatform("Instagram", R.drawable.ic_camera, Color(0xFFE4405F)),
    SharePlatform("WhatsApp", R.drawable.ic_mail, Color(0xFF25D366)),
    SharePlatform("Snapchat", R.drawable.ic_bolt, Color(0xFFFFFC00)),
    SharePlatform("Facebook", R.drawable.ic_profile, Color(0xFF1877F2)),
    SharePlatform("Twitter", R.drawable.ic_share, Color(0xFF1DA1F2)),
    SharePlatform("Telegram", R.drawable.ic_share, Color(0xFF0088CC)),
    SharePlatform("TikTok", R.drawable.ic_bolt, Color(0xFF010101)),
    SharePlatform("More", R.drawable.ic_grid, Color(0xFF666666))
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SocialShareSheet(
    onShare: (String) -> Unit,
    onDismiss: () -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = Surface,
        dragHandle = { BottomSheetDefaults.DragHandle(color = Line) }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp)
                .padding(bottom = 32.dp)
        ) {
            Text(
                "Share your practice",
                style = MaterialTheme.typography.displayMedium,
                color = Ink
            )
            Text(
                "Inspire others with your consistency.",
                style = MaterialTheme.typography.bodyMedium,
                color = Fog
            )
            
            Spacer(modifier = Modifier.height(32.dp))

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(20.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(PLATFORMS) { platform ->
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.clickable { onShare(platform.name) }
                    ) {
                        Box(
                            modifier = Modifier
                                .size(56.dp)
                                .clip(CircleShape)
                                .background(platform.color.copy(alpha = 0.1f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                painter = painterResource(id = platform.icon),
                                contentDescription = platform.name,
                                tint = platform.color,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            platform.name,
                            style = MaterialTheme.typography.labelLarge,
                            fontSize = 12.sp,
                            color = Ink
                        )
                    }
                }
            }
        }
    }
}
