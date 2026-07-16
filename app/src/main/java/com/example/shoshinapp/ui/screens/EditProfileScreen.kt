package com.example.shoshinapp.ui.screens

import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.shoshinapp.R
import com.example.shoshinapp.ui.components.*
import com.example.shoshinapp.ui.theme.*
import com.example.shoshinapp.viewmodel.ProfileViewModel

@Composable
fun EditProfileScreen(
    navController: NavController,
    viewModel: ProfileViewModel
) {
    val user by viewModel.user.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val context = LocalContext.current

    var name by remember { mutableStateOf("") }
    var bio by remember { mutableStateOf("") }
    
    LaunchedEffect(user) {
        user?.let {
            name = it.displayName
            bio = it.bio ?: ""
        }
    }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            try {
                val bitmap = if (Build.VERSION.SDK_INT < 28) {
                    @Suppress("DEPRECATION")
                    MediaStore.Images.Media.getBitmap(context.contentResolver, it)
                } else {
                    val source = ImageDecoder.createSource(context.contentResolver, it)
                    ImageDecoder.decodeBitmap(source) { decoder, _, _ ->
                        decoder.allocator = ImageDecoder.ALLOCATOR_SOFTWARE
                    }
                }
                viewModel.uploadPicture(bitmap)
            } catch (e: Exception) {
                android.util.Log.e("EditProfile", "Failed to decode image", e)
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
            .padding(24.dp)
    ) {
        // App Bar
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { navController.popBackStack() }) {
                Icon(painterResource(R.drawable.ic_arrow_left), contentDescription = "Back", tint = ShInk)
            }
            Text("Edit Profile", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            TextButton(
                onClick = { 
                    viewModel.updateProfile(name, bio)
                    navController.popBackStack()
                },
                enabled = !isLoading && name.isNotEmpty()
            ) {
                Text("Save", color = ShVermillion, fontWeight = FontWeight.Bold)
            }
        }

        Spacer(Modifier.height(32.dp))

        // Profile Picture Edit
        Box(
            modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {
            Box(
                modifier = Modifier
                    .size(140.dp)
                    .clip(CircleShape)
                    .background(ShSand)
                    .border(2.dp, ShLine, CircleShape)
                    .clickable { launcher.launch("image/*") },
                contentAlignment = Alignment.Center
            ) {
                if (user?.profilePictureUrl != null) {
                    AsyncImage(
                        model = user?.profilePictureUrl,
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Icon(painterResource(R.drawable.ic_camera), contentDescription = null, tint = ShFog, modifier = Modifier.size(40.dp))
                }
                
                if (isLoading) {
                    Box(Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.3f)), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = Color.White, modifier = Modifier.size(32.dp))
                    }
                }
            }
            
            Surface(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .offset(x = (-4).dp, y = (-4).dp)
                    .size(40.dp)
                    .clip(CircleShape)
                    .clickable { launcher.launch("image/*") },
                color = ShVermillion,
                tonalElevation = 4.dp
            ) {
                Icon(painterResource(R.drawable.ic_plus), contentDescription = null, tint = Color.White, modifier = Modifier.padding(8.dp))
            }
        }

        Spacer(Modifier.height(48.dp))

        // Fields
        ShoshinTextField(
            value = name,
            onValueChange = { name = it },
            label = "Display Name",
            placeholder = "Your name",
            enabled = !isLoading
        )

        Spacer(Modifier.height(24.dp))

        ShoshinTextField(
            value = bio,
            onValueChange = { bio = it },
            label = "Bio",
            placeholder = "A bit about you...",
            enabled = !isLoading,
            modifier = Modifier.height(120.dp)
        )

        Spacer(Modifier.height(40.dp))

        if (user?.profilePictureUrl != null) {
            TextButton(
                onClick = { /* Delete picture logic */ },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.textButtonColors(contentColor = ShError)
            ) {
                Text("Remove Profile Picture")
            }
        }
    }
}
