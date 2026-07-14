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
    onCapture: () -> Unit,
    onSkip: () -> Unit,
    database: AppDatabase? = null
) {
    var showCamera by remember { mutableStateOf(true) }
    var capturedImage by remember { mutableStateOf<Bitmap?>(null) }
    var isUploading by remember { mutableStateOf(false) }
    var uploadError by remember { mutableStateOf<String?>(null) }
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    
    val photoStorageManager = remember { PhotoStorageManager(context) }
    val shareManager = remember { SocialShareManager(context, database!!) }
    val userId = FirebaseAuth.getInstance().currentUser?.uid ?: ""

    if (isUploading) {
        LoadingDialog(message = "Uploading proof...")
    }

    ShoshinTheme(darkSurface = true) {
        if (showCamera) {
            CameraPreviewUI(
                label = label,
                onPhotoCapture = { bitmap ->
                    capturedImage = bitmap
                    showCamera = false
                },
                onDismiss = onSkip
            )
        } else {
            CameraConfirmScreen(
                bitmap = capturedImage,
                isUploading = isUploading,
                error = uploadError,
                shareManager = shareManager,
                userId = userId,
                onRetake = {
                    capturedImage = null
                    showCamera = true
                    uploadError = null
                },
                onConfirm = {
                    capturedImage?.let { bitmap ->
                        isUploading = true
                        scope.launch {
                            // 1. Capture location at time of photo
                            val location = LocationHelper.getLastLocation(context)
                            val lat = location?.latitude?.let {
                                Math.round(it * 100.0) / 100.0  // Round to 2 decimal places
                            }
                            val long = location?.longitude?.let {
                                Math.round(it * 100.0) / 100.0  // Round to 2 decimal places
                            }

                            // 2. Log to Firebase Analytics
                            AnalyticsManager.logLocationCaptured(
                                lat = lat ?: 0.0,
                                lng = long ?: 0.0,
                                type = "checkpoint"
                            )

                            // 3. Log capture to analytics
                            AnalyticsManager.logCheckpointCompleted(
                                userType = "professional",
                                streak = 0,
                                hadPhoto = true,
                                timeSeconds = 0
                            )

                            // 4. Upload photo with location metadata
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
    var previewView: PreviewView? by remember { mutableStateOf(null) }

    Box(modifier = Modifier.fillMaxSize().background(Color.Black)) {
        AndroidView(
            factory = { ctx ->
                PreviewView(ctx).also {
                    previewView = it
                }
            },
            modifier = Modifier.fillMaxSize()
        )

        LaunchedEffect(previewView) {
            val cameraProvider = withContext(Dispatchers.IO) {
                cameraProviderFuture.get()
            }
            val preview = Preview.Builder().build().also {
                it.setSurfaceProvider(previewView?.surfaceProvider)
            }
            imageCapture = ImageCapture.Builder()
                .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
                .build()

            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

            try {
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(
                    lifecycleOwner,
                    cameraSelector,
                    preview,
                    imageCapture
                )
            } catch (e: Exception) {
                Log.e("CameraPreviewUI", "Use case binding failed", e)
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
    isUploading: Boolean,
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
        if (!isUploading) {
            TextButton(
                onClick = onRetake, 
                modifier = Modifier.align(Alignment.End).padding(16.dp)
            ) {
                Text("Retake", color = ShVermillion, style = MaterialTheme.typography.labelLarge)
            }
        }

        if (bitmap != null) {
            Image(
                bitmap = bitmap.asImageBitmap(),
                contentDescription = "Captured photo",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
                    .aspectRatio(3f / 4f)
                    .border(1.dp, ShPaper2, RoundedCornerShape(16.dp)),
                contentScale = ContentScale.Crop
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        if (error != null) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
                    .background(ShVermillion.copy(alpha = 0.1f), RoundedCornerShape(12.dp))
                    .padding(16.dp)
            ) {
                Text(
                    "Error: $error", 
                    color = ShVermillion2, 
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

        Text(
            text = if (isUploading) "Uploading to cloud..." else "Photo ready",
            style = MaterialTheme.typography.displayMedium.copy(fontSize = 20.sp),
            modifier = Modifier.padding(horizontal = 24.dp),
            color = MaterialTheme.colorScheme.onBackground
        )

        Spacer(modifier = Modifier.height(32.dp))

        if (isUploading) {
            LinearProgressIndicator(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp),
                color = ShVermillion,
                trackColor = ShPaper2
            )
        } else {
            ShoshinButton(
                onClick = onConfirm,
                modifier = Modifier.padding(horizontal = 24.dp)
            ) {
                Text("Confirm & Continue")
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            var showShareSheet by remember { mutableStateOf(false) }
            ShoshinButton(
                variant = ShButtonVariant.Accent,
                onClick = { showShareSheet = true },
                modifier = Modifier.padding(horizontal = 24.dp)
            ) {
                Text("Share to Social")
            }

            if (showShareSheet) {
                SocialShareSheet(
                    onShare = { platform ->
                        showShareSheet = false
                        shareManager.shareToPlatform(
                            platform = platform,
                            text = "Morning discipline in action! 📸 #ShoshinApp",
                            userId = userId,
                            postId = "camera_temp"
                        )
                    },
                    onDismiss = { showShareSheet = false }
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
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
