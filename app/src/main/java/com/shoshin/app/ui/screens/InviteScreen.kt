package com.Shoshin.app.ui.screens

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
import com.Shoshin.app.R
import com.Shoshin.app.data.models.UserSummary
import com.Shoshin.app.ui.components.*
import com.Shoshin.app.ui.theme.*
import com.Shoshin.app.viewmodel.InviteViewModel

@Composable
fun InviteScreen(
    navController: NavController,
    viewModel: InviteViewModel
) {
    val context = LocalContext.current
    var query by remember { mutableStateOf("") }
    val searchResults by viewModel.searchResults.collectAsState()
    val suggestions by viewModel.suggestedFriends.collectAsState()

    ShoshinTheme(type = ShoshinThemeType.DYNAMIC) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(horizontal = 24.dp)
        ) {
            // App Bar
            Row(
                modifier = Modifier.fillMaxWidth().statusBarsPadding().padding(top = 16.dp, bottom = 22.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { navController.popBackStack() }, modifier = Modifier.size(24.dp)) {
                    Icon(painterResource(R.drawable.ic_arrow_left), contentDescription = "Back", tint = MaterialTheme.colorScheme.onBackground)
                }
                Spacer(Modifier.width(14.dp))
                Text("Add a friend", style = ShTitleStyle.copy(fontSize = 32.sp), color = MaterialTheme.colorScheme.onBackground)
            }

            // Search Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(painterResource(R.drawable.ic_search), null, modifier = Modifier.size(20.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
                Spacer(Modifier.width(12.dp))
                BasicTextField(
                    value = query,
                    onValueChange = { 
                        query = it
                        viewModel.searchFriends(it)
                    },
                    modifier = Modifier.fillMaxWidth(),
                    textStyle = androidx.compose.ui.text.TextStyle(fontFamily = DmSansFamily, fontSize = 15.sp, color = MaterialTheme.colorScheme.onSurface),
                    decorationBox = { innerTextField ->
                        Box(contentAlignment = Alignment.CenterStart) {
                            if (query.isEmpty()) {
                                Text("Search Shoshin users by name", fontSize = 15.sp, color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f))
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
                variant = ShButtonVariant.Accent,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(painterResource(R.drawable.ic_share), null, modifier = Modifier.size(18.dp), tint = Color.White)
                Spacer(Modifier.width(10.dp))
                Text("Share invite link", fontWeight = FontWeight.Bold)
            }

            Spacer(Modifier.height(32.dp))

            Text(
                text = "SUGGESTED",
                style = ShKickerStyle.copy(fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            )
            Spacer(Modifier.height(16.dp))

            ShoshinCard(modifier = Modifier.fillMaxWidth().weight(1f)) {
                val listToDisplay = if (query.length >= 2) searchResults else suggestions
                if (listToDisplay.isEmpty() && query.isNotEmpty()) {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("No users found", style = ShLabelStyle, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                } else {
                    androidx.compose.foundation.lazy.LazyColumn(modifier = Modifier.padding(vertical = 4.dp)) {
                        items(listToDisplay.size) { index ->
                            val user = listToDisplay[index]
                            SuggestedFriendRow(user) {
                                // Invite logic
                            }
                            if (index < listToDisplay.lastIndex) HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f), modifier = Modifier.padding(start = 72.dp, end = 24.dp))
                        }
                    }
                }
            }
            
            Spacer(Modifier.height(48.dp))
        }
    }
}

@Composable
fun SuggestedFriendRow(user: UserSummary, onInvite: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.surface),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = user.userName.take(1).uppercase(),
                style = ShH2Style.copy(fontSize = 17.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
            )
        }
        Spacer(Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(user.userName, style = ShH2Style.copy(fontSize = 16.sp), color = MaterialTheme.colorScheme.onSurface)
            val streakText = if (user.currentStreak > 0) "${user.currentStreak} day streak" else "New to Shoshin"
            Text(streakText, style = ShLabelStyle, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        Button(
            onClick = onInvite,
            shape = RoundedCornerShape(10.dp),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.onSurface, contentColor = MaterialTheme.colorScheme.surface),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
        ) {
            Text("Invite", fontSize = 13.sp, fontWeight = FontWeight.Bold)
        }
    }
}
