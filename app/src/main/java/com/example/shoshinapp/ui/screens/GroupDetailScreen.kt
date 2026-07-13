package com.example.shoshinapp.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.Alignment
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.shoshinapp.R
import com.example.shoshinapp.ui.components.*
import com.example.shoshinapp.ui.theme.*
import com.example.shoshinapp.viewmodel.GroupViewModel
import com.example.shoshinapp.utils.ErrorHandler
import com.google.firebase.auth.FirebaseAuth
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun GroupDetailScreen(
    navController: NavController,
    groupId: String,
    viewModel: GroupViewModel = viewModel()
) {
    var selectedTab by remember { mutableStateOf(0) }
    val group by viewModel.currentGroup.collectAsState()
    val members by viewModel.groupMembers.collectAsState()
    val posts by viewModel.groupPosts.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()

    var showPostSheet by remember { mutableStateOf(false) }
    val userId = FirebaseAuth.getInstance().currentUser?.uid ?: ""
    val context = LocalContext.current

    LaunchedEffect(groupId) {
        viewModel.loadGroupMembers(groupId)
        viewModel.loadGroupPosts(groupId)
    }

    if (isLoading && group == null) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = ShVermillion)
        }
    } else {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(ShPaper)
                .padding(24.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(painterResource(R.drawable.ic_arrow_left), "Back", tint = ShInk)
                }
                Text(
                    text = group?.name ?: "Group Details",
                    style = MaterialTheme.typography.displayMedium.copy(fontSize = 24.sp),
                    color = ShInk
                )
                Spacer(Modifier.width(48.dp))
            }

            Spacer(modifier = Modifier.height(8.dp))
            
            // Description & Invite Code
            group?.let { g ->
                Text(
                    text = g.description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = ShFog
                )
                Spacer(Modifier.height(12.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "Invite Code: ${g.inviteCode}",
                        style = ShKickerStyle,
                        color = ShInk
                    )
                    Spacer(Modifier.width(8.dp))
                    TextButton(onClick = {
                        // Copy to clipboard logic
                        val clipboard = context.getSystemService(android.content.Context.CLIPBOARD_SERVICE) as android.content.ClipboardManager
                        val clip = android.content.ClipData.newPlainText("Invite Code", g.inviteCode)
                        clipboard.setPrimaryClip(clip)
                        ErrorHandler.showMessageToast(context, "Code copied to clipboard")
                    }) {
                        Text("Copy", color = ShVermillion)
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            ShoshinSegmented(
                options = listOf(
                    SegmentOption(0, "Feed"),
                    SegmentOption(1, "Members")
                ),
                selected = selectedTab,
                onSelect = { selectedTab = it }
            )

            Spacer(modifier = Modifier.height(24.dp))

            Box(modifier = Modifier.weight(1f)) {
                if (selectedTab == 0) {
                    FeedTab(posts, onShareToGroup = { showPostSheet = true })
                } else {
                    MembersTab(members)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            ShoshinButton(
                onClick = {
                    viewModel.leaveGroup(groupId)
                    navController.popBackStack()
                },
                variant = ShButtonVariant.Ghost
            ) {
                Text("Leave Group")
            }
        }
    }

    if (showPostSheet) {
        var postContent by remember { mutableStateOf("") }
        AlertDialog(
            onDismissRequest = { showPostSheet = false },
            title = { Text("New Post", style = MaterialTheme.typography.headlineSmall, color = ShInk) },
            text = {
                ShoshinTextField(
                    value = postContent,
                    onValueChange = { postContent = it },
                    label = "Your message",
                    placeholder = "Share your morning progress...",
                    modifier = Modifier.height(120.dp)
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.postToGroup(groupId, userId, postContent)
                        showPostSheet = false
                    },
                    enabled = postContent.isNotEmpty()
                ) {
                    Text("Post", color = ShVermillion, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showPostSheet = false }) {
                    Text("Cancel", color = ShFog)
                }
            },
            containerColor = ShSurface,
            shape = RoundedCornerShape(20.dp)
        )
    }

    error?.let {
        ErrorHandler.showMessageToast(context, it)
        viewModel.clearError()
    }
}

@Composable
fun FeedTab(posts: List<com.example.shoshinapp.data.db.entities.GroupPostEntity>, onShareToGroup: () -> Unit) {
    Box(Modifier.fillMaxSize()) {
        if (posts.isEmpty()) {
            EmptyState(
                title = "No posts yet",
                description = "Be the first to share your morning discipline with the circle.",
                iconRes = R.drawable.ic_pulse,
                actionLabel = "Post to Group",
                onAction = onShareToGroup
            )
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                contentPadding = PaddingValues(bottom = 80.dp)
            ) {
                items(posts) { post ->
                    PostCard(post)
                }
            }
            
            ShoshinButton(
                onClick = onShareToGroup,
                modifier = Modifier.align(Alignment.BottomCenter).padding(16.dp),
                variant = ShButtonVariant.Accent
            ) {
                Text("Post to Group")
            }
        }
    }
}

@Composable
fun PostCard(post: com.example.shoshinapp.data.db.entities.GroupPostEntity) {
    ShoshinCard(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Member", // In real app, fetch name from userId
                style = MaterialTheme.typography.labelLarge,
                color = ShInk
            )
            Spacer(Modifier.height(8.dp))
            Text(
                text = post.content,
                style = MaterialTheme.typography.bodyLarge,
                color = ShInk
            )
            Spacer(Modifier.height(8.dp))
            Text(
                text = SimpleDateFormat("MMM dd, HH:mm", Locale.getDefault()).format(Date(post.createdAt)),
                style = MaterialTheme.typography.labelSmall,
                color = ShFog
            )
        }
    }
}

@Composable
fun MembersTab(members: List<com.example.shoshinapp.data.groups.GroupMember>) {
    LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        items(members) { member ->
            MemberDetailRow(member.name, member.consistencyStreak, member.joinedAt)
        }
    }
}

@Composable
fun MemberDetailRow(name: String, streak: Int, joinedAt: Date?) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(ShSurface, RoundedCornerShape(12.dp))
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(
                text = if (name.isEmpty()) "Member" else name,
                style = MaterialTheme.typography.bodyLarge,
                color = ShInk,
                fontWeight = FontWeight.SemiBold
            )
            joinedAt?.let {
                Text(
                    text = "Joined ${SimpleDateFormat("MMM yyyy", Locale.getDefault()).format(it)}",
                    style = MaterialTheme.typography.labelSmall,
                    color = ShFog
                )
            }
        }
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                painter = painterResource(R.drawable.ic_flame),
                contentDescription = "Streak",
                tint = ShVermillion,
                modifier = Modifier.size(20.dp)
            )
            Spacer(Modifier.width(4.dp))
            Text(
                text = "$streak",
                style = MaterialTheme.typography.bodyLarge,
                color = ShInk,
                fontWeight = FontWeight.Bold
            )
        }
    }
}
