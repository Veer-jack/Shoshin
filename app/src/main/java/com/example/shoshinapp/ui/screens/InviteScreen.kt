package com.example.shoshinapp.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.shoshinapp.R
import com.example.shoshinapp.data.models.UserSummary
import com.example.shoshinapp.ui.components.*
import com.example.shoshinapp.ui.theme.*
import com.example.shoshinapp.viewmodel.InviteViewModel

@Composable
fun InviteScreen(
    navController: NavController,
    viewModel: InviteViewModel
) {
    val context = LocalContext.current
    var query by remember { mutableStateOf("") }
    val searchResults by viewModel.searchResults.collectAsState()
    val suggestions by viewModel.suggestedFriends.collectAsState()

    // Permission handling
    val permissionLauncher = androidx.activity.compose.rememberLauncherForActivityResult(
        androidx.activity.result.contract.ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            viewModel.loadContacts()
        }
    }

    LaunchedEffect(Unit) {
        if (androidx.core.content.ContextCompat.checkSelfPermission(
                context,
                android.Manifest.permission.READ_CONTACTS
            ) == android.content.pm.PackageManager.PERMISSION_GRANTED
        ) {
            viewModel.loadContacts()
        } else {
            permissionLauncher.launch(android.Manifest.permission.READ_CONTACTS)
        }
    }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 24.dp)
    ) {
        // App Bar
        Row(
            modifier = Modifier.fillMaxWidth().padding(top = 16.dp, bottom = 22.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            IconButton(onClick = { navController.popBackStack() }, modifier = Modifier.size(24.dp)) {
                Icon(painterResource(R.drawable.ic_arrow_left), contentDescription = "Back")
            }
            Text("Invite friends", style = ShTitleStyle.copy(fontSize = 26.sp), fontWeight = FontWeight.SemiBold)
        }

        // Search Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
                .clip(RoundedCornerShape(14.dp))
                .background(ShSurface)
                .border(1.5.dp, ShLine2, RoundedCornerShape(14.dp))
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(painterResource(R.drawable.ic_search), null, modifier = Modifier.size(18.dp), tint = ShFog)
            Spacer(Modifier.width(10.dp))
            BasicTextField(
                value = query,
                onValueChange = { 
                    query = it
                    viewModel.searchFriends(it)
                },
                modifier = Modifier.fillMaxWidth(),
                textStyle = androidx.compose.ui.text.TextStyle(fontFamily = DmSansFamily, fontSize = 15.sp, color = ShInk),
                decorationBox = { innerTextField ->
                    Box(contentAlignment = Alignment.CenterStart) {
                        if (query.isEmpty()) {
                            Text("Search contacts", fontSize = 15.sp, color = ShFog2)
                        }
                        innerTextField()
                    }
                }
            )
        }

        Spacer(Modifier.height(20.dp))

        val inviteLink = viewModel.getInviteLink()
        ShoshinButton(
            onClick = { 
                val intent = android.content.Intent(android.content.Intent.ACTION_SEND).apply {
                    type = "text/plain"
                    putExtra(android.content.Intent.EXTRA_TEXT, "Join me on Shoshin! Build your morning consistency: $inviteLink")
                }
                context.startActivity(android.content.Intent.createChooser(intent, "Share invite link"))
            },
            variant = ShButtonVariant.Ghost,
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(painterResource(R.drawable.ic_share), null, modifier = Modifier.size(18.dp), tint = ShInk)
            Spacer(Modifier.width(8.dp))
            Text("Share invite link instead")
        }

        Spacer(Modifier.height(22.dp))

        Kicker("From your contacts", modifier = Modifier.padding(start = 4.dp, bottom = 12.dp))

        ShoshinCard(modifier = Modifier.fillMaxWidth().weight(1f)) {
            val listToDisplay = if (query.length >= 2) searchResults else suggestions
            if (listToDisplay.isEmpty()) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No contacts found", style = ShLabelStyle, color = ShFog)
                }
            } else {
                androidx.compose.foundation.lazy.LazyColumn(modifier = Modifier.padding(horizontal = 18.dp, vertical = 4.dp)) {
                    items(listToDisplay.size) { index ->
                        val user = listToDisplay[index]
                        SuggestedFriendRow(user) {
                            val intent = android.content.Intent(android.content.Intent.ACTION_SEND).apply {
                                type = "text/plain"
                                putExtra(android.content.Intent.EXTRA_TEXT, "Hey ${user.userName}, join me on Shoshin! $inviteLink")
                            }
                            context.startActivity(android.content.Intent.createChooser(intent, "Invite ${user.userName}"))
                        }
                        if (index < listToDisplay.lastIndex) HorizontalDivider(color = ShLine)
                    }
                }
            }
        }
        
        Spacer(Modifier.height(24.dp))
    }
}

@Composable
fun SuggestedFriendRow(user: UserSummary, onInvite: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(ShSand),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = user.userName.take(1).uppercase(),
                style = ShTitleStyle.copy(fontSize = 17.sp),
                color = ShInk
            )
        }
        Spacer(Modifier.width(14.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(user.userName, fontSize = 15.5.sp, fontWeight = FontWeight.Medium, color = ShInk)
            val subLabel = if (user.activityStatus == "On Shoshin") {
                "On Shoshin — ${user.currentStreak} days"
            } else {
                "In your contacts"
            }
            Text(subLabel, fontSize = 12.5.sp, color = ShFog)
        }
        Button(
            onClick = onInvite,
            shape = RoundedCornerShape(10.dp),
            colors = ButtonDefaults.buttonColors(containerColor = ShInk, contentColor = ShPaper),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
        ) {
            val buttonText = if (user.activityStatus == "On Shoshin") "Add" else "Invite"
            Text(buttonText, fontSize = 12.5.sp, fontWeight = FontWeight.SemiBold)
        }
    }
}
