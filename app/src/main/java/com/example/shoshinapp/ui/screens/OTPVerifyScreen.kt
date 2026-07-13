package com.example.shoshinapp.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.platform.LocalContext
import android.app.Activity
import androidx.navigation.NavHostController
import com.example.shoshinapp.PhoneAuthManager
import com.example.shoshinapp.EmailAuthManager
import com.example.shoshinapp.data.ShoshinRepository
import com.example.shoshinapp.navigation.ShRoutes
import com.example.shoshinapp.ui.components.*
import com.example.shoshinapp.ui.theme.ShVermillion
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
    mode: OtpMode = OtpMode.Phone
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
    
    // Resend logic
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

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // ... (rest of the file remains same)
        Text(
            "Verify ${if (mode == OtpMode.Phone) "Phone" else "Email"}",
            style = MaterialTheme.typography.headlineSmall
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            if (mode == OtpMode.Phone) 
                "Enter the 6-digit code sent to $phone"
            else 
                "We sent a verification link to $email",
            style = MaterialTheme.typography.bodyMedium,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )

        if (mode == OtpMode.Email) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                "Please click the link in the email to verify your account. Don't forget to check your spam folder.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        if (mode == OtpMode.Phone) {
            TextField(
                value = code,
                onValueChange = { if (it.length <= 6) code = it },
                label = { Text("OTP Code") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                enabled = !isLoading && !isSending
            )
        }

        if (errorMessage.isNotEmpty()) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                errorMessage,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
        }
        
        if (successMessage.isNotEmpty()) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                successMessage,
                color = ShVermillion,
                style = MaterialTheme.typography.bodySmall,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                if (mode == OtpMode.Phone && code.length != 6) {
                    errorMessage = "OTP must be 6 digits"
                    return@Button
                }

                isLoading = true
                errorMessage = ""
                successMessage = ""

                when (mode) {
                    OtpMode.Phone -> {
                        phoneAuthManager.verifyOTP(
                            code,
                            onSuccess = {
                                scope.launch {
                                    shoshinRepository.saveUser(name = "User", phone = phone)
                                    navController.navigate(ShRoutes.onboarding(0)) {
                                        popUpTo(ShRoutes.AUTH) { inclusive = true }
                                    }
                                }
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
                                scope.launch {
                                    shoshinRepository.saveUser(name = "User", email = email)
                                    navController.navigate(ShRoutes.onboarding(0)) {
                                        popUpTo(ShRoutes.AUTH) { inclusive = true }
                                    }
                                }
                            },
                            onError = {
                                errorMessage = ErrorHandler.mapFirebaseError(it)
                                isLoading = false
                            }
                        )
                    }
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            enabled = !isLoading && !isSending
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(20.dp),
                    color = MaterialTheme.colorScheme.onPrimary
                )
            } else {
                Text(if (mode == OtpMode.Phone) "Verify & Continue" else "I've Clicked the Link")
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
                if (resendCooldown > 0) "Resend in ${resendCooldown}s" 
                else "Didn't receive code? Resend"
            )
        }
    }
}
