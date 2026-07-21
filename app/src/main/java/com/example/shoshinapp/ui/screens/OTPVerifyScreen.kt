package com.example.shoshinapp.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.platform.LocalContext
import android.app.Activity
import androidx.compose.ui.res.painterResource
import androidx.navigation.NavHostController
import com.example.shoshinapp.R
import com.example.shoshinapp.PhoneAuthManager
import com.example.shoshinapp.EmailAuthManager
import com.example.shoshinapp.data.ShoshinRepository
import com.example.shoshinapp.navigation.ShRoutes
import com.example.shoshinapp.ui.components.*
import com.example.shoshinapp.ui.theme.*
import com.example.shoshinapp.utils.ErrorHandler
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

enum class OtpMode { Phone, Email }

@Composable
fun OTPVerifyScreen(
    navController: NavHostController,
    shoshinRepository: ShoshinRepository,
    phone: String = "",
    email: String = "",
    password: String = "",
    mode: OtpMode = OtpMode.Phone,
    referralCode: String? = null,
    onSuccess: (String, String?, String?) -> Unit = { _, _, _ -> }
) {
    val context = LocalContext.current
    val activity = context as? Activity
    val scope = rememberCoroutineScope()
    val auth = FirebaseAuth.getInstance()
    val phoneAuthManager = remember { PhoneAuthManager(auth) }
    val emailAuthManager = remember { EmailAuthManager(auth) }

    var code by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var isSending by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }
    var successMessage by remember { mutableStateOf("") }
    
    var resendCooldown by remember { mutableIntStateOf(0) }

    LaunchedEffect(Unit) {
        if (mode == OtpMode.Phone && activity != null) {
            isSending = true
            phoneAuthManager.startPhoneAuth(
                phone = phone,
                activity = activity,
                onCodeSent = { 
                    isSending = false
                    resendCooldown = 60
                },
                onError = { e ->
                    errorMessage = ErrorHandler.mapFirebaseError(e)
                    isSending = false
                }
            )
        } else if (mode == OtpMode.Email) {
            isSending = true
            emailAuthManager.sendVerificationEmail(
                email = email,
                password = password,
                onSuccess = {
                    isSending = false
                    resendCooldown = 60
                    successMessage = "Verification email sent!"
                },
                onError = { e ->
                    errorMessage = ErrorHandler.mapFirebaseError(e)
                    isSending = false
                }
            )
        }
    }

    LaunchedEffect(resendCooldown) {
        if (resendCooldown > 0) {
            delay(1000)
            resendCooldown -= 1
        }
    }

    if (isSending) {
        LoadingDialog(message = "Sending verification...")
    }

    Box(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
        // App Bar
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp).align(Alignment.TopStart),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { navController.popBackStack() }) {
                Icon(painterResource(R.drawable.ic_arrow_left), contentDescription = "Back", tint = MaterialTheme.colorScheme.onBackground)
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp)
                .systemBarsPadding(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(100.dp))

            // Logo
            ShoshinLogoMark()
            
            Spacer(Modifier.height(32.dp))

            Text(
                text = if (mode == OtpMode.Phone) "Verify Phone" else "Check your email",
                style = ShTitleStyle,
                color = MaterialTheme.colorScheme.onBackground
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = if (mode == OtpMode.Phone) 
                    "Enter the 6-digit code sent to $phone"
                else 
                    "We sent a verification link to $email",
                style = ShBodyStyle,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )

            Spacer(modifier = Modifier.height(32.dp))

            if (mode == OtpMode.Phone) {
                ShoshinOtpBoxes(
                    value = code,
                    length = 6,
                    dark = false,
                    modifier = Modifier.padding(bottom = 12.dp)
                )
                // Invisible interaction for actual keypad input could go here, 
                // but for now we'll assume a custom keypad or system keyboard.
                // Porting design's ShoshinKeypad:
                ShoshinKeypad(
                    onDigit = { if (code.length < 6) code += it },
                    onClear = { code = code.dropLast(1) },
                    onOk = { /* Submit logic below */ },
                    dark = false,
                    modifier = Modifier.padding(top = 24.dp)
                )
            } else {
                // Email Verification Icon
                Box(
                    modifier = Modifier.size(100.dp).background(MaterialTheme.colorScheme.surfaceVariant, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(painterResource(R.drawable.ic_mail), null, modifier = Modifier.size(40.dp), tint = ShVermillion)
                }
            }

            if (errorMessage.isNotEmpty()) {
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = errorMessage,
                    color = MaterialTheme.colorScheme.error,
                    style = ShLabelStyle,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
            }
            
            if (successMessage.isNotEmpty()) {
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = successMessage,
                    color = ShMatcha,
                    style = ShLabelStyle,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            val interactionSource = remember { MutableInteractionSource() }

            ShoshinButton(
                onClick = {
                    if (mode == OtpMode.Phone && code.length != 6) {
                        errorMessage = "OTP must be 6 digits"
                        return@ShoshinButton
                    }

                    isLoading = true
                    errorMessage = ""
                    successMessage = ""

                    when (mode) {
                        OtpMode.Phone -> {
                            phoneAuthManager.verifyOTP(
                                code,
                                onSuccess = {
                                    val userId = FirebaseAuth.getInstance().currentUser?.uid ?: ""
                                    onSuccess(userId, phone, referralCode)
                                },
                                onError = {
                                    errorMessage = ErrorHandler.mapFirebaseError(it)
                                    isLoading = false
                                }
                            )
                        }
                        OtpMode.Email -> {
                            emailAuthManager.verifyEmail(
                                onSuccess = {
                                    val userId = FirebaseAuth.getInstance().currentUser?.uid ?: ""
                                    onSuccess(userId, email, referralCode)
                                },
                                onError = {
                                    errorMessage = ErrorHandler.mapFirebaseError(it)
                                    isLoading = false
                                }
                            )
                        }
                    }
                },
                variant = ShButtonVariant.Accent,
                enabled = !isLoading && !isSending,
                modifier = Modifier.fillMaxWidth(),
                interactionSource = interactionSource,
                pressedColor = Color.Black
            ) {
                if (isLoading) {
                    CircularProgressIndicator(modifier = Modifier.size(20.dp), color = Color.White)
                } else {
                    Text(
                        text = if (mode == OtpMode.Phone) "Verify & Continue" else "I've Clicked the Link",
                        color = Color.White
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            TextButton(
                onClick = {
                    errorMessage = ""
                    successMessage = ""
                    isSending = true
                    if (mode == OtpMode.Phone && activity != null) {
                        phoneAuthManager.resendOTP(
                            phone = phone,
                            activity = activity,
                            onCodeSent = {
                                isSending = false
                                resendCooldown = 60
                                successMessage = "New code sent!"
                            },
                            onError = { e ->
                                errorMessage = ErrorHandler.mapFirebaseError(e)
                                isSending = false
                            }
                        )
                    } else if (mode == OtpMode.Email) {
                        emailAuthManager.sendVerificationEmail(
                            email = email,
                            password = password,
                            onSuccess = {
                                isSending = false
                                resendCooldown = 60
                                successMessage = "New verification link sent!"
                            },
                            onError = { e ->
                                errorMessage = ErrorHandler.mapFirebaseError(e)
                                isSending = false
                            }
                        )
                    }
                },
                enabled = resendCooldown == 0 && !isSending && !isLoading
            ) {
                Text(
                    text = if (resendCooldown > 0) "Resend in ${resendCooldown}s" 
                    else "Didn't receive code? Resend",
                    color = if (resendCooldown > 0) MaterialTheme.colorScheme.onSurfaceVariant else ShVermillion,
                    style = ShLabelStyle
                )
            }
            
            Spacer(Modifier.height(24.dp))
        }
    }
}
