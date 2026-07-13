package com.example.shoshinapp.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.shoshinapp.R
import com.example.shoshinapp.ui.theme.*

@Composable
fun SyncStatusDialog(
    isVisible: Boolean,
    status: String, // "syncing", "success", "error", "offline"
    message: String = "",
    onDismiss: () -> Unit
) {
    if (!isVisible) return

    AlertDialog(
        onDismissRequest = onDismiss,
        shape = RoundedCornerShape(16.dp),
        containerColor = Washi,
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                when (status) {
                    "syncing" -> {
                        CircularProgressIndicator(
                            color = Vermillion,
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Syncing...",
                            style = MaterialTheme.typography.labelLarge,
                            color = Sumi
                        )
                    }
                    "success" -> {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_check),
                            contentDescription = "Success",
                            tint = Matcha,
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Sync Successful",
                            style = MaterialTheme.typography.labelLarge,
                            color = Sumi
                        )
                    }
                    "error" -> {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_lock),
                            contentDescription = "Error",
                            tint = Vermillion,
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Sync Failed",
                            style = MaterialTheme.typography.labelLarge,
                            color = Sumi
                        )
                    }
                    "offline" -> {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_bell),
                            contentDescription = "Offline",
                            tint = Vermillion,
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "You're Offline",
                            style = MaterialTheme.typography.labelLarge,
                            color = Sumi
                        )
                    }
                }

                if (message.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = message,
                        style = MaterialTheme.typography.bodyMedium,
                        color = Fog,
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = onDismiss,
                modifier = Modifier.padding(8.dp)
            ) {
                Text("OK", color = Vermillion, style = MaterialTheme.typography.labelLarge)
            }
        }
    )
}

@Composable
fun OfflineIndicator(isOffline: Boolean) {
    if (!isOffline) return

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(Paper2) // Subdued color from DS
            .padding(12.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_bell),
                contentDescription = "Offline",
                tint = Vermillion,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "You're offline - Changes saved locally",
                style = MaterialTheme.typography.labelLarge.copy(fontSize = 12.sp),
                color = Sumi
            )
        }
    }
}

@Composable
fun ConflictResolutionDialog(
    isVisible: Boolean,
    localContent: String,
    remoteContent: String,
    onUseLocal: () -> Unit,
    onUseRemote: () -> Unit,
    onMerge: () -> Unit
) {
    if (!isVisible) return

    AlertDialog(
        onDismissRequest = { },
        shape = RoundedCornerShape(16.dp),
        containerColor = Washi,
        title = {
            Text(
                text = "Conflict Detected",
                style = MaterialTheme.typography.displayMedium.copy(fontSize = 24.sp),
                color = Sumi
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            ) {
                Text(
                    text = "This reflection was edited elsewhere. Which version do you prefer?",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Fog,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                // Local version
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Matcha.copy(alpha = 0.1f), RoundedCornerShape(8.dp))
                        .padding(12.dp)
                ) {
                    Text(
                        text = "Your Version:\n$localContent",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Sumi
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Remote version
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Vermillion.copy(alpha = 0.1f), RoundedCornerShape(8.dp))
                        .padding(12.dp)
                ) {
                    Text(
                        text = "Cloud Version:\n$remoteContent",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Sumi
                    )
                }
            }
        },
        confirmButton = {
            Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp)) {
                Button(
                    onClick = onUseLocal,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = Matcha)
                ) {
                    Text("Keep My Version", color = Washi, style = MaterialTheme.typography.labelLarge)
                }
                
                Spacer(modifier = Modifier.height(8.dp))

                Button(
                    onClick = onUseRemote,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = Vermillion)
                ) {
                    Text("Use Cloud Version", color = Washi, style = MaterialTheme.typography.labelLarge)
                }
                
                Spacer(modifier = Modifier.height(8.dp))

                TextButton(
                    onClick = onMerge,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Merge Both", color = Ink2, style = MaterialTheme.typography.labelLarge)
                }
            }
        }
    )
}
