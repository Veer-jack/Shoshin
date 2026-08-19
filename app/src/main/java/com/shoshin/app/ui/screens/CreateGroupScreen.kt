package com.Shoshin.app.ui.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.FileProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.ui.graphics.Color
import com.Shoshin.app.R
import com.Shoshin.app.ui.components.ShoshinButton
import com.Shoshin.app.ui.components.ShButtonVariant
import com.Shoshin.app.ui.components.ShoshinTextField
import com.Shoshin.app.ui.theme.*
import com.Shoshin.app.utils.decodeBitmapFromUri
import com.Shoshin.app.viewmodel.GroupViewModel
import java.io.File

@Composable
fun CreateGroupScreen(navController: NavController, viewModel: GroupViewModel = viewModel()) {
    var name by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()
    val success by viewModel.creationSuccess.collectAsState()

    val context = LocalContext.current
    var photoBitmap by remember { mutableStateOf<android.graphics.Bitmap?>(null) }
    var showImageChooser by remember { mutableStateOf(false) }
    var pendingCameraUri by remember { mutableStateOf<Uri?>(null) }

    val galleryLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri?.let { photoBitmap = context.decodeBitmapFromUri(it) }
    }
    val cameraLauncher = rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) { capturedOk ->
        if (capturedOk) pendingCameraUri?.let { photoBitmap = context.decodeBitmapFromUri(it) }
    }

    // Pop on success
    LaunchedEffect(success) {
        if (success) {
            viewModel.resetCreationState()
            navController.popBackStack()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
    ) {
        // App Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(horizontal = 16.dp, vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { navController.popBackStack() }) {
                Icon(
                    painter = painterResource(R.drawable.ic_arrow_left),
                    contentDescription = "Back",
                    tint = ShInk
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text("Create Circle", style = ShTitleStyle.copy(fontSize = 28.sp), color = ShInk)
        }

        Column(modifier = Modifier.padding(horizontal = 24.dp)) {
            Spacer(modifier = Modifier.height(16.dp))

            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(100.dp)
                        .clip(CircleShape)
                        .background(ShSand)
                        .border(2.dp, ShLine, CircleShape)
                        .clickable { showImageChooser = true },
                    contentAlignment = Alignment.Center
                ) {
                    val bitmap = photoBitmap
                    if (bitmap != null) {
                        androidx.compose.foundation.Image(
                            bitmap = bitmap.asImageBitmap(),
                            contentDescription = "Group image",
                            modifier = Modifier.fillMaxSize().clip(CircleShape),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Icon(painterResource(R.drawable.ic_camera), contentDescription = null, tint = ShFog, modifier = Modifier.size(32.dp))
                    }
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = if (photoBitmap != null) "Change group image" else "Add group image",
                style = ShLabelStyle.copy(fontSize = 13.sp),
                color = ShFog,
                modifier = Modifier.fillMaxWidth().clickable { showImageChooser = true },
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
            Spacer(modifier = Modifier.height(24.dp))

            ShoshinTextField(
                value = name,
                onValueChange = { name = it },
                label = "Circle Name",
                placeholder = "e.g., Morning Warriors"
            )
            Spacer(modifier = Modifier.height(24.dp))

            ShoshinTextField(
                value = description,
                onValueChange = { description = it },
                label = "Description",
                placeholder = "What is this circle about?",
                modifier = Modifier.height(120.dp)
            )
            Spacer(modifier = Modifier.height(32.dp))

            if (error != null) {
                Text(error!!, color = ShVermillion, fontSize = 12.sp)
                Spacer(modifier = Modifier.height(16.dp))
            }

            val interactionSource = remember { MutableInteractionSource() }

            ShoshinButton(
                onClick = {
                    viewModel.createGroup(name, description, photoBitmap)
                },
                variant = ShButtonVariant.Accent,
                enabled = name.isNotEmpty() && !isLoading,
                modifier = Modifier.fillMaxWidth(),
                interactionSource = interactionSource,
                pressedColor = Color.Black
            ) {
                Text(
                    text = if (isLoading) "Creating..." else "Create Circle",
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }

    if (showImageChooser) {
        AlertDialog(
            onDismissRequest = { showImageChooser = false },
            title = { Text("Add group image", style = ShTitleStyle.copy(fontSize = 20.sp, color = ShInk)) },
            text = {
                Column {
                    TextButton(onClick = {
                        showImageChooser = false
                        galleryLauncher.launch("image/*")
                    }) {
                        Text("Choose from gallery", color = ShInk)
                    }
                    TextButton(onClick = {
                        showImageChooser = false
                        val file = File.createTempFile("group_avatar_", ".jpg", context.cacheDir)
                        val uri = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
                        pendingCameraUri = uri
                        cameraLauncher.launch(uri)
                    }) {
                        Text("Take photo", color = ShInk)
                    }
                }
            },
            confirmButton = {},
            dismissButton = {
                TextButton(onClick = { showImageChooser = false }) {
                    Text("Cancel", color = ShFog)
                }
            }
        )
    }
}
