package com.Shoshin.app.ui.screens

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
import com.Shoshin.app.R
import com.Shoshin.app.ui.components.*
import com.Shoshin.app.ui.theme.*
import com.Shoshin.app.viewmodel.ProfileViewModel

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
    var height by remember { mutableStateOf("") }
    var heightUnit by remember { mutableStateOf("cm") }
    var weight by remember { mutableStateOf("") }
    var weightUnit by remember { mutableStateOf("kg") }

    LaunchedEffect(user) {
        user?.let {
            name = it.displayName
            bio = it.bio ?: ""
            height = it.height?.toString() ?: ""
            heightUnit = it.heightUnit
            weight = it.weight?.toString() ?: ""
            weightUnit = it.weightUnit
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
                    viewModel.updateProfile(
                        name = name,
                        bio = bio,
                        height = height.toFloatOrNull(),
                        heightUnit = heightUnit,
                        weight = weight.toFloatOrNull(),
                        weightUnit = weightUnit
                    )
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

        Spacer(Modifier.height(24.dp))

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Column(modifier = Modifier.weight(1f)) {
                ShoshinTextField(
                    value = height,
                    onValueChange = { new -> height = new.filter { it.isDigit() || it == '.' } },
                    label = "Height",
                    placeholder = "Optional",
                    enabled = !isLoading
                )
                Spacer(Modifier.height(8.dp))
                UnitToggle(options = listOf("cm", "in"), selected = heightUnit, onSelect = { heightUnit = it }, enabled = !isLoading)
            }
            Column(modifier = Modifier.weight(1f)) {
                ShoshinTextField(
                    value = weight,
                    onValueChange = { new -> weight = new.filter { it.isDigit() || it == '.' } },
                    label = "Weight",
                    placeholder = "Optional",
                    enabled = !isLoading
                )
                Spacer(Modifier.height(8.dp))
                UnitToggle(options = listOf("kg", "lbs"), selected = weightUnit, onSelect = { weightUnit = it }, enabled = !isLoading)
            }
        }

        Spacer(Modifier.height(32.dp))

        Text("Your sign-in identity", style = ShLabelStyle, color = ShFog)
        Spacer(Modifier.height(8.dp))
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(ShSand)
                .padding(16.dp)
        ) {
            if (!user?.email.isNullOrBlank()) {
                Text(user?.email.orEmpty(), style = ShH2Style.copy(fontSize = 15.sp, color = ShInk))
            }
            if (!user?.phone.isNullOrBlank()) {
                Text(user?.phone.orEmpty(), style = ShH2Style.copy(fontSize = 15.sp, color = ShInk))
            }
            Spacer(Modifier.height(4.dp))
            Text("Contact support to change this", style = ShLabelStyle.copy(fontSize = 12.sp), color = ShFog)
        }

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

@Composable
private fun UnitToggle(
    options: List<String>,
    selected: String,
    onSelect: (String) -> Unit,
    enabled: Boolean
) {
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        options.forEach { option ->
            val isSelected = option == selected
            Surface(
                onClick = { if (enabled) onSelect(option) },
                shape = RoundedCornerShape(999.dp),
                color = if (isSelected) ShInk else ShSand,
                contentColor = if (isSelected) ShPaper else ShFog
            ) {
                Text(
                    option,
                    style = ShLabelStyle.copy(fontWeight = FontWeight.Bold, fontSize = 12.sp),
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
                )
            }
        }
    }
}
