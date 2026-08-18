package com.Shoshin.app.ui.screens

import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.util.Patterns
import android.widget.Toast
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
import com.Shoshin.app.data.db.entities.avatarUrl
import com.Shoshin.app.ui.components.*
import com.Shoshin.app.ui.theme.*
import com.Shoshin.app.viewmodel.ProfileViewModel

private fun isValidName(name: String): Boolean {
    val trimmed = name.trim()
    return trimmed.isNotEmpty() && trimmed.length <= 50
}

private fun isValidPhone(digits: String): Boolean = digits.length in 6..14

private fun isValidEmail(email: String): Boolean =
    email.isBlank() || Patterns.EMAIL_ADDRESS.matcher(email.trim()).matches()

@Composable
fun EditProfileScreen(
    navController: NavController,
    viewModel: ProfileViewModel
) {
    val user by viewModel.user.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val context = LocalContext.current

    var name by remember { mutableStateOf("") }
    var dialCode by remember { mutableStateOf(SH_COUNTRIES.first()) }
    var phoneDigits by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }

    var nameError by remember { mutableStateOf<String?>(null) }
    var phoneError by remember { mutableStateOf<String?>(null) }
    var emailError by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(user) {
        user?.let {
            name = it.displayName
            val phone = it.phone.orEmpty()
            val detected = detectDialCode(phone)
            dialCode = detected
            phoneDigits = phone.removePrefix(detected.dial).filter { c -> c.isDigit() }
            email = it.email.orEmpty()
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

    fun save() {
        nameError = if (isValidName(name)) null else "Enter a name under 50 characters"
        phoneError = if (isValidPhone(phoneDigits)) null else "Enter a valid mobile number"
        emailError = if (isValidEmail(email)) null else "Enter a valid email address"

        if (nameError != null || phoneError != null || emailError != null) return

        viewModel.updateProfile(
            name = name.trim(),
            phone = "${dialCode.dial}$phoneDigits",
            email = email.trim()
        )
        Toast.makeText(context, "Profile updated", Toast.LENGTH_SHORT).show()
        navController.popBackStack()
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
            Spacer(Modifier.size(48.dp))
        }

        Spacer(Modifier.height(32.dp))

        // Profile Picture Edit
        Box(modifier = Modifier.align(Alignment.CenterHorizontally)) {
            Box(
                modifier = Modifier
                    .size(140.dp)
                    .clip(CircleShape)
                    .background(ShSand)
                    .border(2.dp, ShLine, CircleShape)
                    .clickable { launcher.launch("image/*") },
                contentAlignment = Alignment.Center
            ) {
                val currentAvatar = user?.avatarUrl
                if (currentAvatar != null) {
                    AsyncImage(
                        model = currentAvatar,
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
                    .size(36.dp)
                    .clip(CircleShape)
                    .clickable { launcher.launch("image/*") },
                color = ShInk,
                tonalElevation = 4.dp
            ) {
                Icon(painterResource(R.drawable.ic_camera), contentDescription = "Change photo", tint = Color.White, modifier = Modifier.padding(9.dp))
            }
        }

        Spacer(Modifier.height(40.dp))

        // Fields
        ShoshinTextField(
            value = name,
            onValueChange = { name = it; nameError = null },
            label = "Full name",
            placeholder = "Your name",
            enabled = !isLoading
        )
        if (nameError != null) {
            Spacer(Modifier.height(6.dp))
            Text(nameError!!, style = ShLabelStyle.copy(fontSize = 12.sp), color = ShError)
        }

        Spacer(Modifier.height(20.dp))

        ShoshinPhoneField(
            dialCode = dialCode,
            onDialCodeChange = { dialCode = it },
            digits = phoneDigits,
            onDigitsChange = { new -> phoneDigits = new.filter { it.isDigit() }.take(14); phoneError = null },
            enabled = !isLoading
        )
        if (phoneError != null) {
            Spacer(Modifier.height(6.dp))
            Text(phoneError!!, style = ShLabelStyle.copy(fontSize = 12.sp), color = ShError)
        }

        Spacer(Modifier.height(20.dp))

        ShoshinTextField(
            value = email,
            onValueChange = { email = it; emailError = null },
            label = "Email address (optional)",
            placeholder = "you@example.com",
            leadingIcon = R.drawable.ic_mail,
            enabled = !isLoading
        )
        if (emailError != null) {
            Spacer(Modifier.height(6.dp))
            Text(emailError!!, style = ShLabelStyle.copy(fontSize = 12.sp), color = ShError)
        }

        Spacer(Modifier.height(40.dp))

        ShoshinButton(
            onClick = { save() },
            variant = ShButtonVariant.Accent,
            enabled = !isLoading,
            modifier = Modifier.fillMaxWidth()
        ) {
            if (isLoading) {
                CircularProgressIndicator(color = Color.White, modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
            } else {
                Text("Save changes", fontWeight = FontWeight.Bold)
            }
        }

        Spacer(Modifier.height(24.dp))
    }
}
