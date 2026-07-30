package com.Shoshin.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.Shoshin.app.R
import com.Shoshin.app.ui.components.*
import com.Shoshin.app.ui.theme.*

@Composable
fun GroupInviteScreen(
    navController: NavController,
    groupId: String,
    viewModel: com.Shoshin.app.viewmodel.GroupViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
) {
    val context = LocalContext.current
    val group by viewModel.currentGroup.collectAsState()
    var isCopied by remember { mutableStateOf(false) }

    LaunchedEffect(groupId) {
        viewModel.loadGroupMembers(groupId) // This loads group details too
    }

    LaunchedEffect(isCopied) {
        if (isCopied) {
            kotlinx.coroutines.delay(2000)
            isCopied = false
        }
    }

    ShoshinTheme(type = ShoshinThemeType.DYNAMIC) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(ShNight)
                .statusBarsPadding()
                .padding(horizontal = 24.dp)
                .verticalScroll(rememberScrollState())
        ) {
            // App Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { navController.popBackStack() }, modifier = Modifier.size(24.dp)) {
                    Icon(painterResource(R.drawable.ic_arrow_left), contentDescription = "Back", tint = Color.White)
                }
                Spacer(Modifier.width(16.dp))
                Text("Invite the circle", style = ShTitleStyle.copy(fontSize = 28.sp, color = Color.White))
            }

            Spacer(Modifier.height(16.dp))

            Text(
                "Share this link with anyone you want in your circle. They'll see your morning activity, and you'll see theirs.",
                style = ShBodyStyle.copy(fontSize = 15.sp, color = ShNightMuted, lineHeight = 22.sp)
            )

            Spacer(Modifier.height(32.dp))

            // Invitation Preview Card
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1.4f)
                    .clip(RoundedCornerShape(32.dp))
                    .background(ShNight2),
                contentAlignment = Alignment.Center
            ) {
                Enso(
                    size = 180, 
                    color = ShVermillionLight.copy(alpha = 0.12f), 
                    strokeWidth = 8f,
                    modifier = Modifier.align(Alignment.TopEnd).offset(x = 40.dp, y = (-20).dp)
                )
                
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(
                        modifier = Modifier
                            .size(64.dp)
                            .clip(CircleShape)
                            .background(ShNightText.copy(alpha = 0.05f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.ic_gift), 
                            null, 
                            tint = ShVermillionLight, 
                            modifier = Modifier.size(28.dp)
                        )
                    }
                    
                    Spacer(Modifier.height(24.dp))
                    
                    Text(
                        group?.name ?: "Dawn Circle", 
                        style = ShTitleStyle.copy(fontSize = 24.sp, color = Color.White)
                    )
                    
                    Spacer(Modifier.height(8.dp))
                    
                    Text(
                        "A month of Pro, on us", 
                        style = ShLabelStyle.copy(color = ShNightMuted)
                    )
                }
            }

            Spacer(Modifier.height(40.dp))

            // Invite Code Section
            Kicker("YOUR CODE", color = ShNightMuted)
            Spacer(Modifier.height(12.dp))
            
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(ShNight2)
                    .padding(horizontal = 20.dp, vertical = 20.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = group?.inviteCode ?: "ARJUN-M14K",
                        style = ShNumeralStyle.copy(fontSize = 24.sp, color = Color.White, letterSpacing = 1.sp)
                    )
                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(ShNight3)
                            .padding(horizontal = 12.dp, vertical = 8.dp)
                            .clickable {
                                val clipboard = context.getSystemService(android.content.Context.CLIPBOARD_SERVICE) as android.content.ClipboardManager
                                val clip = android.content.ClipData.newPlainText("Invite Code", group?.inviteCode)
                                clipboard.setPrimaryClip(clip)
                                isCopied = true
                                android.widget.Toast.makeText(context, "Code copied!", android.widget.Toast.LENGTH_SHORT).show()
                            },
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            painter = painterResource(if (isCopied) R.drawable.ic_check else R.drawable.ic_check), // Or ic_copy if available
                            null, 
                            tint = if (isCopied) ShMatchaDark else Color.White, 
                            modifier = Modifier.size(14.dp)
                        )
                        Text(
                            if (isCopied) "Copied" else "Copy", 
                            style = ShLabelStyle.copy(fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        )
                    }
                }
            }

            Spacer(Modifier.height(48.dp))

            ShoshinButton(
                onClick = { 
                    val message = "Join my circle on Shoshin! We keep our mornings together. Use code ${group?.inviteCode} or join via https://shoshin.app/join/${group?.inviteCode}"
                    val intent = android.content.Intent(android.content.Intent.ACTION_SEND).apply {
                        type = "text/plain"
                        putExtra(android.content.Intent.EXTRA_TEXT, message)
                    }
                    context.startActivity(android.content.Intent.createChooser(intent, "Invite to circle"))
                },
                variant = ShButtonVariant.Accent,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(painterResource(R.drawable.ic_share), null, modifier = Modifier.size(20.dp), tint = Color.White)
                Spacer(Modifier.width(10.dp))
                Text("Share invite link", fontWeight = FontWeight.Bold)
            }
            
            Spacer(Modifier.height(16.dp))
            
            ShoshinButton(
                onClick = { /* Email logic */ },
                variant = ShButtonVariant.Dark,
                modifier = Modifier.fillMaxWidth().padding(bottom = 32.dp)
            ) {
                Icon(painterResource(R.drawable.ic_mail), null, modifier = Modifier.size(20.dp), tint = Color.White)
                Spacer(Modifier.width(10.dp))
                Text("Invite by email", color = Color.White, fontWeight = FontWeight.Bold)
            }
        }
    }
}
