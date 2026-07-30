package com.Shoshin.app.ui.screens

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.animation.*
import androidx.compose.foundation.Image
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
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.Shoshin.app.R
import com.Shoshin.app.data.db.AppDatabase
import com.Shoshin.app.data.db.entities.PhotoEntity
import com.Shoshin.app.ui.components.*
import com.Shoshin.app.ui.theme.*
import com.Shoshin.app.utils.LocationHelper
import com.Shoshin.app.utils.PhotoStorageManager
import com.Shoshin.app.utils.VerificationResult
import com.Shoshin.app.utils.ImageVerificationManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.*
import java.io.ByteArrayOutputStream
import java.util.UUID

@Composable
fun CameraVerificationScreen(
    checkpointIndex: Int,
    label: String,
    targetLabels: List<String> = emptyList(),
    onCapture: () -> Unit,
    onSkip: () -> Unit,
    database: AppDatabase? = null
) {
    var showCamera by remember { mutableStateOf(true) }
    var capturedImage by remember { mutableStateOf<Bitmap?>(null) }
    var isUploading by remember { mutableStateOf(false) }
    var isVerifying by remember { mutableStateOf(false) }
    var verificationResult by remember { mutableStateOf<VerificationResult?>(null) }
    
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val photoStorageManager = remember(context) { PhotoStorageManager(context) }

    if (isUploading) LoadingDialog(message = "Saving proof...")
    if (isVerifying) LoadingDialog(message = "Verifying...")

    ShoshinTheme(type = ShoshinThemeType.ALWAYS_DARK) {
        Box(modifier = Modifier.fillMaxSize().background(ShNight)) {
            if (showCamera) {
                CameraViewDark(
                    label = label,
                    onPhotoCapture = { bitmap ->
                        capturedImage = bitmap
                        showCamera = false
                        
                        if (targetLabels.isNotEmpty()) {
                            isVerifying = true
                            scope.launch {
                                verificationResult = ImageVerificationManager.verifyImage(bitmap, targetLabels)
                                isVerifying = false
                            }
                        }
                    },
                    onSkip = onSkip
                )
            } else {
                CameraConfirmUIDark(
                    bitmap = capturedImage,
                    label = label,
                    verificationResult = verificationResult,
                    onRetake = {
                        capturedImage = null
                        showCamera = true
                        verificationResult = null
                    },
                    onConfirm = {
                        capturedImage?.let { bitmap ->
                            isUploading = true
                            scope.launch {
                                val location = LocationHelper.getLastLocation(context)
                                uploadPhotoToFirebase(
                                    bitmap = bitmap,
                                    context = context,
                                    database = database!!,
                                    photoStorageManager = photoStorageManager,
                                    latitude = location?.latitude,
                                    longitude = location?.longitude,
                                    onSuccess = { 
                                        isUploading = false
                                        onCapture() 
                                    },
                                    onError = { 
                                        isUploading = false
                                    }
                                )
                            }
                        }
                    }
                )
            }
        }
    }
}

@Composable
private fun CameraViewDark(
    label: String,
    onPhotoCapture: (Bitmap) -> Unit,
    onSkip: () -> Unit
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val cameraProviderFuture = remember { ProcessCameraProvider.getInstance(context) }
    var imageCapture: ImageCapture? by remember { mutableStateOf(null) }

    Box(modifier = Modifier.fillMaxSize()) {
        AndroidView(
            factory = { ctx -> PreviewView(ctx).apply { implementationMode = PreviewView.ImplementationMode.COMPATIBLE } },
            modifier = Modifier.fillMaxSize(),
            update = { previewView ->
                cameraProviderFuture.addListener({
                    val cameraProvider = cameraProviderFuture.get()
                    val preview = androidx.camera.core.Preview.Builder().build().also { it.setSurfaceProvider(previewView.surfaceProvider) }
                    imageCapture = ImageCapture.Builder().build()
                    try {
                        cameraProvider.unbindAll()
                        cameraProvider.bindToLifecycle(lifecycleOwner, CameraSelector.DEFAULT_BACK_CAMERA, preview, imageCapture)
                    } catch (e: Exception) { }
                }, ContextCompat.getMainExecutor(context))
            }
        )

        // Viewfinder Corners
        Box(modifier = Modifier.fillMaxSize().padding(40.dp)) {
            Box(modifier = Modifier.size(40.dp).border(width = 2.dp, color = ShVermillionLight, shape = RoundedCornerShape(topStart = 24.dp)).align(Alignment.TopStart))
            Box(modifier = Modifier.size(40.dp).border(width = 2.dp, color = ShVermillionLight, shape = RoundedCornerShape(topEnd = 24.dp)).align(Alignment.TopEnd))
            Box(modifier = Modifier.size(40.dp).border(width = 2.dp, color = ShVermillionLight, shape = RoundedCornerShape(bottomStart = 24.dp)).align(Alignment.BottomStart))
            Box(modifier = Modifier.size(40.dp).border(width = 2.dp, color = ShVermillionLight, shape = RoundedCornerShape(bottomEnd = 24.dp)).align(Alignment.BottomEnd))
        }

        Column(modifier = Modifier.fillMaxSize().padding(24.dp).statusBarsPadding()) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Surface(color = ShNight2, shape = RoundedCornerShape(999.dp), modifier = Modifier.clickable { }) {
                    Row(modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp), verticalAlignment = Alignment.CenterVertically) {
                         Icon(painterResource(R.drawable.ic_camera), null, modifier = Modifier.size(14.dp), tint = Color.White)
                         Spacer(Modifier.width(8.dp))
                         Text("VERIFY CHECKPOINT", style = ShLabelStyle.copy(fontSize = 11.sp, color = Color.White))
                    }
                }
                Text("Skip · -1 proof", style = ShLabelStyle.copy(color = ShNightMuted), modifier = Modifier.clickable { onSkip() })
            }
            
            Spacer(Modifier.weight(1f))
            
            Box(modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(24.dp)).background(Color.Black.copy(alpha = 0.6f)).padding(24.dp)) {
                Column {
                    Kicker("TAKE A PHOTO OF: ${label.uppercase()}", color = ShVermillionLight)
                    Text(label, style = ShTitleStyle.copy(fontSize = 24.sp, color = Color.White))
                }
            }

            Spacer(Modifier.height(32.dp))

            Row(modifier = Modifier.fillMaxWidth().padding(bottom = 24.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = { }, modifier = Modifier.size(48.dp).clip(CircleShape).background(ShNight2)) {
                    Icon(painterResource(R.drawable.ic_moon), null, tint = Color.White)
                }
                
                Box(
                    modifier = Modifier.size(80.dp).clip(CircleShape).background(Color.White).padding(4.dp).clickable {
                        capturePhoto(context, imageCapture, onPhotoCapture)
                    },
                    contentAlignment = Alignment.Center
                ) {
                    Box(modifier = Modifier.fillMaxSize().clip(CircleShape).background(ShVermillionLight))
                }

                IconButton(onClick = { }, modifier = Modifier.size(48.dp).clip(CircleShape).background(ShNight2)) {
                    Icon(painterResource(R.drawable.ic_user), null, tint = Color.White)
                }
            }
        }
    }
}

@Composable
private fun CameraConfirmUIDark(
    bitmap: Bitmap?,
    label: String,
    verificationResult: VerificationResult?,
    onRetake: () -> Unit,
    onConfirm: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize().background(ShNight).padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(Modifier.height(40.dp))
        if (bitmap != null) {
            Image(
                bitmap = bitmap.asImageBitmap(),
                contentDescription = null,
                modifier = Modifier.weight(1f).fillMaxWidth().clip(RoundedCornerShape(32.dp)),
                contentScale = ContentScale.Crop
            )
        }
        Spacer(Modifier.height(32.dp))

        when (verificationResult) {
            is VerificationResult.Success -> {
                Kicker("VERIFICATION SUCCESSFUL", color = ShMatchaDark)
                Text("Detected: ${verificationResult.label}", style = ShTitleStyle.copy(fontSize = 20.sp, color = Color.White))
            }
            is VerificationResult.Failure -> {
                Kicker("VERIFICATION FAILED", color = ShVermillionLight)
                Text(verificationResult.message, style = ShBodyStyle.copy(color = ShNightMuted), textAlign = TextAlign.Center)
            }
            else -> {
                Kicker("READY TO SAVE", color = ShNightMuted)
                Text(label, style = ShTitleStyle.copy(color = Color.White))
            }
        }

        Spacer(Modifier.height(32.dp))
        
        val canConfirm = verificationResult is VerificationResult.Success || verificationResult == null
        
        ShoshinButton(
            onClick = onConfirm, 
            variant = if (canConfirm) ShButtonVariant.Ghost else ShButtonVariant.Dark, 
            modifier = Modifier.fillMaxWidth(),
            enabled = canConfirm
        ) {
            Text("Confirm & Continue", color = if (canConfirm) Color.Black else ShNightMuted, fontWeight = FontWeight.Bold)
        }
        Spacer(Modifier.height(16.dp))
        Text("Retake", color = ShVermillionLight, modifier = Modifier.clickable { onRetake() })
        Spacer(Modifier.height(40.dp))
    }
}

private fun capturePhoto(context: Context, imageCapture: ImageCapture?, onPhotoCapture: (Bitmap) -> Unit) {
    val capture = imageCapture ?: return
    capture.takePicture(
        ContextCompat.getMainExecutor(context),
        object : ImageCapture.OnImageCapturedCallback() {
            override fun onCaptureSuccess(image: ImageProxy) {
                val bitmap = imageProxyToBitmap(image)
                image.close()
                onPhotoCapture(bitmap)
            }
            override fun onError(exception: ImageCaptureException) { }
        }
    )
}

private fun imageProxyToBitmap(image: ImageProxy): Bitmap {
    val buffer = image.planes[0].buffer
    val bytes = ByteArray(buffer.remaining())
    buffer.get(bytes)
    return BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
}

suspend fun uploadPhotoToFirebase(
    bitmap: Bitmap,
    context: Context,
    database: AppDatabase,
    photoStorageManager: PhotoStorageManager,
    latitude: Double?,
    longitude: Double?,
    onSuccess: () -> Unit,
    onError: (String) -> Unit
) {
    try {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: throw Exception("User not authenticated")
        val compressionResult = photoStorageManager.saveCompressedPhoto(bitmap)
        val localPath = compressionResult.getOrNull() ?: ""
        val photoId = UUID.randomUUID().toString()
        val photoEntity = PhotoEntity(photoId = photoId, userId = userId, localCompressedPath = localPath, firebaseUrl = null, date = java.time.LocalDate.now().toString(), timestamp = System.currentTimeMillis(), latitude = latitude, longitude = longitude, syncStatus = "pending")
        database.photoDao().insertPhoto(photoEntity)
        onSuccess()
        val storage = FirebaseStorage.getInstance()
        val storageRef = storage.reference.child("checkpoints/${userId}/${photoId}.jpg")
        val baos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 90, baos)
        storageRef.putBytes(baos.toByteArray()).addOnSuccessListener {
            storageRef.downloadUrl.addOnSuccessListener { uri ->
                CoroutineScope(Dispatchers.IO).launch {
                    database.photoDao().updatePhoto(photoEntity.copy(firebaseUrl = uri.toString(), syncStatus = "synced"))
                }
            }
        }
    } catch (e: Exception) { onError(e.localizedMessage ?: "Unknown error") }
}
