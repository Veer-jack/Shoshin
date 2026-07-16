package com.example.shoshinapp.ui.screens

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Log
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.example.shoshinapp.R
import com.example.shoshinapp.data.db.AppDatabase
import com.example.shoshinapp.data.db.entities.PhotoEntity
import com.example.shoshinapp.ui.components.*
import com.example.shoshinapp.ui.theme.*
import com.example.shoshinapp.utils.LocationHelper
import com.example.shoshinapp.utils.AnalyticsManager
import com.example.shoshinapp.utils.PhotoStorageManager
import com.example.shoshinapp.utils.SocialShareManager
import com.google.common.util.concurrent.ListenableFuture
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.*
import java.io.ByteArrayOutputStream
import java.io.File
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
    var verificationResult by remember { mutableStateOf<com.example.shoshinapp.utils.VerificationResult?>(null) }
    var uploadError by remember { mutableStateOf<String?>(null) }
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    
    val photoStorageManager = remember { com.example.shoshinapp.utils.PhotoStorageManager(context) }
    val shareManager = remember { com.example.shoshinapp.utils.SocialShareManager(context, database!!) }
    val userId = FirebaseAuth.getInstance().currentUser?.uid ?: ""

    if (isUploading) {
        LoadingDialog(message = "Uploading proof...")
    }
    
    if (isVerifying) {
        LoadingDialog(message = "Verifying photo...")
    }

    ShoshinTheme(darkSurface = true) {
        if (showCamera) {
            CameraPreviewUI(
                label = label,
                onPhotoCapture = { bitmap ->
                    capturedImage = bitmap
                    showCamera = false
                    
                    // Trigger Verification automatically
                    if (targetLabels.isNotEmpty()) {
                        isVerifying = true
                        scope.launch {
                            verificationResult = com.example.shoshinapp.utils.ImageVerificationManager.verifyImage(bitmap, targetLabels)
                            isVerifying = false
                        }
                    }
                },
                onDismiss = onSkip
            )
        } else {
            CameraConfirmScreen(
                bitmap = capturedImage,
                label = label,
                targetLabels = targetLabels,
                isUploading = isUploading,
                isVerifying = isVerifying,
                verificationResult = verificationResult,
                error = uploadError,
                shareManager = shareManager,
                userId = userId,
                onRetake = {
                    capturedImage = null
                    showCamera = true
                    uploadError = null
                    verificationResult = null
                },
                onConfirm = {
                    capturedImage?.let { bitmap ->
                        isUploading = true
                        scope.launch {
                            // ... existing upload logic
                            val location = com.example.shoshinapp.utils.LocationHelper.getLastLocation(context)
                            val lat = location?.latitude?.let { Math.round(it * 100.0) / 100.0 }
                            val long = location?.longitude?.let { Math.round(it * 100.0) / 100.0 }

                            AnalyticsManager.logLocationCaptured(lat ?: 0.0, long ?: 0.0, "checkpoint")
                            AnalyticsManager.logCheckpointCompleted("professional", 0, true, 0)

                            uploadPhotoToFirebase(
                                bitmap = bitmap,
                                context = context,
                                database = database!!,
                                photoStorageManager = photoStorageManager,
                                latitude = lat,
                                longitude = long,
                                onSuccess = {
                                    isUploading = false
                                    onCapture()
                                },
                                onError = { error ->
                                    isUploading = false
                                    uploadError = error
                                }
                            )
                        }
                    }
                }
            )
        }
    }
}

@Composable
fun CameraPreviewUI(label: String, onPhotoCapture: (Bitmap) -> Unit, onDismiss: () -> Unit) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val cameraProviderFuture: ListenableFuture<ProcessCameraProvider> = remember { 
        ProcessCameraProvider.getInstance(context) 
    }
    
    var imageCapture: ImageCapture? by remember { mutableStateOf(null) }
    val previewView = remember { mutableStateOf<PreviewView?>(null) }

    // Check permissions
    var hasPermission by remember {
        mutableStateOf(
            androidx.core.content.ContextCompat.checkSelfPermission(context, android.Manifest.permission.CAMERA) == android.content.pm.PackageManager.PERMISSION_GRANTED
        )
    }

    if (!hasPermission) {
        val launcher = androidx.activity.compose.rememberLauncherForActivityResult(
            androidx.activity.result.contract.ActivityResultContracts.RequestPermission()
        ) { hasPermission = it }
        
        LaunchedEffect(Unit) { launcher.launch(android.Manifest.permission.CAMERA) }
        
        Box(Modifier.fillMaxSize().background(Color.Black), contentAlignment = Alignment.Center) {
            Text("Camera permission required", color = Color.White)
        }
        return
    }

    Box(modifier = Modifier.fillMaxSize().background(Color.Black)) {
        AndroidView(
            factory = { ctx ->
                PreviewView(ctx).also {
                    previewView.value = it
                }
            },
            modifier = Modifier.fillMaxSize()
        )

        LaunchedEffect(previewView.value) {
            val view = previewView.value ?: return@LaunchedEffect
            android.util.Log.d("CameraVerification", "LaunchedEffect triggered with previewView: $view")
            
            // Small delay to ensure view is ready
            delay(500)
            
            try {
                val cameraProvider = withContext(Dispatchers.IO) {
                    cameraProviderFuture.get()
                }
                android.util.Log.d("CameraVerification", "CameraProvider obtained")
                
                val preview = Preview.Builder().build().also {
                    it.setSurfaceProvider(view.surfaceProvider)
                }
                
                imageCapture = ImageCapture.Builder()
                    .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
                    .setTargetRotation(view.display?.rotation ?: android.view.Surface.ROTATION_0)
                    .build()

                val cameraSelector = if (cameraProvider.hasCamera(CameraSelector.DEFAULT_BACK_CAMERA)) {
                    CameraSelector.DEFAULT_BACK_CAMERA
                } else if (cameraProvider.hasCamera(CameraSelector.DEFAULT_FRONT_CAMERA)) {
                    CameraSelector.DEFAULT_FRONT_CAMERA
                } else {
                    null
                }

                if (cameraSelector != null) {
                    cameraProvider.unbindAll()
                    cameraProvider.bindToLifecycle(
                        lifecycleOwner,
                        cameraSelector,
                        preview,
                        imageCapture
                    )
                    android.util.Log.d("CameraVerification", "Camera bound successfully: $cameraSelector")
                } else {
                    android.util.Log.e("CameraVerification", "No camera selector found")
                }
            } catch (e: Exception) {
                android.util.Log.e("CameraPreviewUI", "Camera initialization failed", e)
            }
        }

        // Overlay UI
        Column(modifier = Modifier.fillMaxSize()) {
            Box(modifier = Modifier.fillMaxWidth().weight(1f).padding(top = 40.dp), contentAlignment = Alignment.TopCenter) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        "Capture Proof", 
                        color = ShNightMuted, 
                        style = ShLabelStyle
                    )
                    Spacer(Modifier.height(8.dp))
                    Text(
                        label, 
                        color = Color.White, 
                        style = MaterialTheme.typography.displayMedium.copy(fontSize = 24.sp)
                    )
                }
            }

            Box(
                modifier = Modifier.fillMaxWidth().height(160.dp).background(ShInk2.copy(alpha = 0.8f)),
                contentAlignment = Alignment.Center
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 32.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = onDismiss) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_arrow_left),
                            contentDescription = "Back", 
                            tint = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    // Capture Button
                    Box(
                        modifier = Modifier
                            .size(72.dp)
                            .border(2.dp, ShVermillion, CircleShape)
                            .padding(4.dp)
                            .clickable {
                                capturePhoto(context, imageCapture, onPhotoCapture)
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(ShVermillion, CircleShape)
                        )
                    }

                    IconButton(onClick = { }) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_sun),
                            contentDescription = "Flash",
                            tint = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            }
        }
    }
}

fun capturePhoto(
    context: Context,
    imageCapture: ImageCapture?,
    onPhotoCapture: (Bitmap) -> Unit
) {
    val imageCaptureUseCase = imageCapture ?: return

    val outputOptions = ImageCapture.OutputFileOptions.Builder(
        File(context.cacheDir, "temp_capture.jpg")
    ).build()

    imageCaptureUseCase.takePicture(
        ContextCompat.getMainExecutor(context),
        object : ImageCapture.OnImageCapturedCallback() {
            override fun onCaptureSuccess(image: ImageProxy) {
                val bitmap = imageProxyToBitmap(image)
                image.close()
                onPhotoCapture(bitmap)
            }

            override fun onError(exception: ImageCaptureException) {
                Log.e("CameraPreviewUI", "Photo capture failed: ${exception.message}", exception)
            }
        }
    )
}

fun imageProxyToBitmap(image: ImageProxy): Bitmap {
    val buffer = image.planes[0].buffer
    val bytes = ByteArray(buffer.remaining())
    buffer.get(bytes)
    return BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
}

@Composable
fun CameraConfirmScreen(
    bitmap: Bitmap?,
    label: String,
    targetLabels: List<String>,
    isUploading: Boolean,
    isVerifying: Boolean,
    verificationResult: com.example.shoshinapp.utils.VerificationResult?,
    error: String?,
    shareManager: SocialShareManager,
    userId: String,
    onRetake: () -> Unit,
    onConfirm: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
    ) {
        if (!isUploading && !isVerifying) {
            TextButton(
                onClick = onRetake, 
                modifier = Modifier.align(Alignment.End).padding(16.dp)
            ) {
                Text("Retake", color = ShVermillion, style = MaterialTheme.typography.labelLarge)
            }
        }

        if (bitmap != null) {
            Box(modifier = Modifier.padding(horizontal = 24.dp)) {
                Image(
                    bitmap = bitmap.asImageBitmap(),
                    contentDescription = "Captured photo",
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(3f / 4f)
                        .clip(RoundedCornerShape(24.dp))
                        .border(1.dp, ShLine, RoundedCornerShape(24.dp)),
                    contentScale = ContentScale.Crop
                )
                
                // Verification Badge Overlay
                verificationResult?.let { result ->
                    val (icon, color, text) = when (result) {
                        is com.example.shoshinapp.utils.VerificationResult.Success -> Triple(R.drawable.ic_check, ShMatcha, "Verified: ${result.label}")
                        is com.example.shoshinapp.utils.VerificationResult.Failure -> Triple(R.drawable.ic_info, ShVermillion, "Detection Mismatch")
                        is com.example.shoshinapp.utils.VerificationResult.Error -> Triple(R.drawable.ic_info, ShFog, "Scan Error")
                    }
                    
                    Surface(
                        modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 16.dp),
                        color = color.copy(alpha = 0.9f),
                        shape = CircleShape
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(painterResource(icon), null, tint = Color.White, modifier = Modifier.size(16.dp))
                            Text(text, color = Color.White, style = ShLabelStyle, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Info / Error text
        Column(modifier = Modifier.padding(horizontal = 24.dp)) {
            if (isVerifying) {
                Text("Checking image for ${targetLabels.joinToString("/")}...", color = ShFog, style = ShBodyStyle)
            } else if (verificationResult is com.example.shoshinapp.utils.VerificationResult.Failure) {
                Text(verificationResult.message, color = ShVermillion, style = ShBodyStyle)
                Text("You can still confirm if the detection was incorrect.", color = ShFog, fontSize = 12.sp)
            } else if (error != null) {
                Text("Error: $error", color = ShVermillion, style = ShBodyStyle)
            } else {
                Text(label, style = ShTitleStyle.copy(fontSize = 24.sp), color = ShInk)
                Text("Photo proof ready for validation.", style = ShBodyStyle, color = ShFog)
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        if (isUploading) {
            LinearProgressIndicator(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp),
                color = ShVermillion,
                trackColor = ShLine
            )
        } else {
            ShoshinButton(
                onClick = onConfirm,
                modifier = Modifier.padding(horizontal = 24.dp),
                variant = if (verificationResult is com.example.shoshinapp.utils.VerificationResult.Success) ShButtonVariant.Accent else ShButtonVariant.Primary
            ) {
                Text("Confirm & Continue")
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            ShoshinButton(
                variant = ShButtonVariant.Ghost,
                onClick = { onRetake() },
                modifier = Modifier.padding(horizontal = 24.dp)
            ) {
                Text("Retake Photo")
            }
        }

        Spacer(modifier = Modifier.height(48.dp))
    }
}

suspend fun uploadPhotoToFirebase(
    bitmap: Bitmap,
    context: Context,
    database: AppDatabase,
    photoStorageManager: PhotoStorageManager,
    latitude: Double? = null,
    longitude: Double? = null,
    onSuccess: () -> Unit,
    onError: (String) -> Unit
) {
    try {
        val userId = FirebaseAuth.getInstance().currentUser?.uid
            ?: throw Exception("User not authenticated")

        val compressionResult = photoStorageManager.saveCompressedPhoto(bitmap)
        if (!compressionResult.isSuccess) {
            onError("Failed to compress photo")
            return
        }

        val localPath = compressionResult.getOrNull() ?: ""
        val photoId = UUID.randomUUID().toString()

        val photoEntity = PhotoEntity(
            photoId = photoId,
            userId = userId,
            localCompressedPath = localPath,
            firebaseUrl = null,
            date = java.time.LocalDate.now().toString(),
            timestamp = System.currentTimeMillis(),
            latitude = latitude,
            longitude = longitude,
            syncStatus = "pending"
        )

        database.photoDao().insertPhoto(photoEntity)
        onSuccess()

        val storage = FirebaseStorage.getInstance()
        val filename = "checkpoints/${userId}/${photoId}.jpg"
        val storageRef = storage.reference.child(filename)

        val baos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 90, baos)
        val imageData = baos.toByteArray()

        storageRef.putBytes(imageData)
            .addOnSuccessListener {
                storageRef.downloadUrl.addOnSuccessListener { uri ->
                    CoroutineScope(Dispatchers.IO).launch {
                        database.photoDao().updatePhoto(
                            photoEntity.copy(
                                firebaseUrl = uri.toString(),
                                syncStatus = "synced"
                            )
                        )
                    }
                }
            }
    } catch (e: Exception) {
        onError(e.localizedMessage ?: "Unknown error")
    }
}
