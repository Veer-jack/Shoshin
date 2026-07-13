package com.example.shoshinapp.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.shoshinapp.data.db.AppDatabase
import com.example.shoshinapp.data.db.entities.ReflectionEntity
import com.example.shoshinapp.ui.components.*
import com.example.shoshinapp.ui.theme.*
import com.example.shoshinapp.utils.SocialShareManager
import com.example.shoshinapp.utils.ErrorHandler
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.*
import java.util.UUID

@Composable
fun EditorScreen(
    navController: NavController,
    template: String = "walk",
    database: AppDatabase? = null
) {
    var text by remember { mutableStateOf("") }
    var isSaving by remember { mutableStateOf(false) }
    var saveStatus by remember { mutableStateOf("") }
    val scope = rememberCoroutineScope()
    
    val userId = FirebaseAuth.getInstance().currentUser?.uid ?: ""
    val context = androidx.compose.ui.platform.LocalContext.current
    val shareManager = remember { SocialShareManager(context, database!!) }

    if (isSaving) {
        LoadingDialog(message = "Saving your thoughts...")
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Washi)
            .verticalScroll(rememberScrollState())
            .padding(24.dp)
    ) {
        Text(
            text = "Reflection",
            style = MaterialTheme.typography.displayLarge,
            color = Sumi
        )
        Text(
            text = "Write about your morning",
            style = MaterialTheme.typography.bodyMedium,
            color = Fog
        )

        Spacer(modifier = Modifier.height(24.dp))

        ShoshinTextField(
            value = text,
            onValueChange = { text = it },
            label = "Reflection",
            placeholder = "Start typing your thoughts...",
            enabled = !isSaving,
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp)
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = if (isSaving) "Saving locally..." else "Ready to save",
            style = MaterialTheme.typography.labelLarge.copy(fontSize = 12.sp),
            color = if (isSaving) Vermillion else Matcha
        )

        Spacer(modifier = Modifier.height(16.dp))

        if (saveStatus.isNotEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        if (saveStatus.contains("✅")) Matcha.copy(alpha = 0.1f) else Vermillion.copy(alpha = 0.1f),
                        RoundedCornerShape(12.dp)
                    )
                    .padding(16.dp)
            ) {
                Text(
                    text = saveStatus,
                    style = MaterialTheme.typography.bodyMedium,
                    color = if (saveStatus.contains("✅")) Matcha else Vermillion2
                )
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        ShoshinButton(
            onClick = {
                if (text.isNotEmpty()) {
                    isSaving = true
                    scope.launch {
                        saveReflectionToFirebase(text, userId, database!!) { success ->
                            isSaving = false
                            saveStatus = if (success) "✅ Saved (syncing...)" else "❌ Failed"
                        }
                    }
                } else {
                    saveStatus = "Please write something"
                }
            },
            enabled = !isSaving
        ) {
            Text(if (isSaving) "Saving..." else "Save Reflection")
        }

        if (saveStatus.contains("✅")) {
            Spacer(modifier = Modifier.height(16.dp))
            var showShareSheet by remember { mutableStateOf(false) }
            
            ShoshinButton(
                variant = ShButtonVariant.Accent,
                onClick = { showShareSheet = true }
            ) {
                Text("Share Achievement")
            }

            if (showShareSheet) {
                SocialShareSheet(
                    onShare = { platform ->
                        showShareSheet = false
                        shareManager.shareToPlatform(
                            platform = platform,
                            text = "Just completed my morning routine! 🌅 #ShoshinApp\n\n$text",
                            userId = userId,
                            postId = "reflection_temp" 
                        )
                    },
                    onDismiss = { showShareSheet = false }
                )
            }
        }
    }
}

suspend fun saveReflectionToFirebase(
    text: String,
    userId: String,
    database: AppDatabase,
    onComplete: (Boolean) -> Unit
) {
    try {
        val reflectionId = UUID.randomUUID().toString()
        val reflection = ReflectionEntity(
            reflectionId = reflectionId,
            userId = userId,
            content = text,
            date = java.time.LocalDate.now().toString(),
            timestamp = System.currentTimeMillis(),
            syncStatus = "pending"
        )

        database.reflectionDao().insertReflection(reflection)
        onComplete(true)

        val firestore = FirebaseFirestore.getInstance()
        firestore
            .collection("reflections")
            .document(userId)
            .collection("entries")
            .document(reflectionId)
            .set(reflection)
            .addOnSuccessListener {
                CoroutineScope(Dispatchers.IO).launch {
                    database.reflectionDao().updateReflection(
                        reflection.copy(syncStatus = "synced")
                    )
                }
            }
    } catch (e: Exception) {
        onComplete(false)
    }
}
