package com.Shoshin.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.platform.LocalContext
import android.app.Activity
import androidx.compose.ui.res.painterResource
import androidx.navigation.NavHostController
import com.Shoshin.app.R
import com.Shoshin.app.PhoneAuthManager
import com.Shoshin.app.data.ShoshinRepository
import com.Shoshin.app.navigation.ShRoutes
import com.Shoshin.app.ui.components.*
import com.Shoshin.app.ui.theme.*
import com.Shoshin.app.utils.ErrorHandler
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

enum class OtpMode { Phone }

@Composable
fun OTPVerifyScreen(
    navController: NavHostController,
    shoshinRepository: ShoshinRepository,
    phone: String = "",
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
                onAutoVerified = { userId ->
                    isSending = false
                    onSuccess(userId, phone, referralCode)
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

    val onBackgroundColor = MaterialTheme.colorScheme.onBackground
    // `phone` already carries its own country-code prefix (e.g. "+14155551234").
    val displayPhone = if (phone.startsWith("+")) phone else "+91 $phone"
    val subtitleText = buildAnnotatedString {
        append("Sent to ")
        withStyle(style = ShBodyStyle.toSpanStyle().copy(fontWeight = FontWeight.Bold, color = onBackgroundColor)) {
            append(displayPhone)
        }
    }

    fun onVerify() {
        // Guard re-entry: the keypad's OK key and the auto-submit below can both fire.
        if (isLoading) return
        if (code.length != 6) {
            errorMessage = "OTP must be 6 digits"
            return
        }
        if (phoneAuthManager.verificationId == null) {
            errorMessage = "Still sending the code — try again in a moment"
            return
        }
        isLoading = true
        errorMessage = ""
        successMessage = ""

        phoneAuthManager.verifyOTP(
            code,
            onSuccess = {
                val userId = FirebaseAuth.getInstance().currentUser?.uid ?: ""
                isLoading = false
                onSuccess(userId, phone, referralCode)
            },
            onError = {
                errorMessage = ErrorHandler.mapFirebaseError(it)
                // Clear the boxes so the next attempt starts clean.
                code = ""
                isLoading = false
            }
        )
    }

    // Submit as soon as the sixth digit lands — no extra tap needed.
    LaunchedEffect(code) {
        if (code.length == 6 && !isLoading) onVerify()
    }

    ShoshinTheme(type = ShoshinThemeType.DYNAMIC) {
        val isDark = MaterialTheme.colorScheme.background == ShNight
        
        Scaffold(
            topBar = {
                Box(modifier = Modifier.statusBarsPadding().padding(horizontal = 8.dp, vertical = 8.dp)) {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(painterResource(R.drawable.ic_arrow_left), contentDescription = "Back", tint = MaterialTheme.colorScheme.onBackground)
                    }
                }
            },
            containerColor = MaterialTheme.colorScheme.background
        ) { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(horizontal = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(Modifier.height(32.dp))

                Text(
                    text = "Enter the code",
                    style = ShTitleStyle.copy(fontSize = 32.sp),
                    color = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Start
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = subtitleText,
                    style = ShBodyStyle.copy(fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurfaceVariant),
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Start
                )

                Spacer(modifier = Modifier.height(48.dp))

                ShoshinOtpBoxes(
                    value = code,
                    length = 6,
                    dark = isDark, // Use isDark to determine styling
                    isError = errorMessage.isNotEmpty(),
                    modifier = Modifier.padding(bottom = 24.dp)
                )

                Row(
                    modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(text = "Didn’t receive it? ", style = ShLabelStyle.copy(color = MaterialTheme.colorScheme.onSurfaceVariant))
                    Text(
                        text = if (resendCooldown > 0) {
                            "Resend in 0:${String.format(java.util.Locale.US, "%02d", resendCooldown)}"
                        } else {
                            "Resend"
                        },
                        style = ShLabelStyle.copy(color = ShVermillion, fontWeight = FontWeight.Bold),
                        modifier = Modifier.clickable(enabled = resendCooldown == 0) {
                            if (resendCooldown == 0 && !isSending && !isLoading && activity != null) {
                                errorMessage = ""
                                successMessage = ""
                                code = ""
                                isSending = true
                                phoneAuthManager.resendOTP(
                                    phone = phone,
                                    activity = activity,
                                    onCodeSent = {
                                        isSending = false
                                        resendCooldown = 60
                                        successMessage = "New code sent!"
                                    },
                                    onAutoVerified = { userId ->
                                        isSending = false
                                        onSuccess(userId, phone, referralCode)
                                    },
                                    onError = { e ->
                                        errorMessage = ErrorHandler.mapFirebaseError(e)
                                        isSending = false
                                    }
                                )
                            }
                        }
                    )
                }

                // Inline status line — replaces the blocking progress dialogs so the
                // keypad stays visible and usable while a code is in flight.
                val statusText = when {
                    errorMessage.isNotEmpty() -> errorMessage
                    isLoading -> "Verifying…"
                    isSending -> "Sending code…"
                    successMessage.isNotEmpty() -> successMessage
                    else -> ""
                }
                if (statusText.isNotEmpty()) {
                    Text(
                        text = statusText,
                        color = if (errorMessage.isNotEmpty()) MaterialTheme.colorScheme.error
                                else MaterialTheme.colorScheme.onSurfaceVariant,
                        style = ShLabelStyle,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)
                    )
                }

                Spacer(modifier = Modifier.weight(1f))

                ShoshinKeypad(
                    onDigit = {
                        if (code.length < 6 && !isLoading) {
                            // Typing again dismisses the previous failure, so the boxes
                            // stop rendering their error state.
                            errorMessage = ""
                            successMessage = ""
                            code += it
                        }
                    },
                    onClear = {
                        if (code.isNotEmpty() && !isLoading) {
                            errorMessage = ""
                            code = code.dropLast(1)
                        }
                    },
                    onOk = { onVerify() },
                    modifier = Modifier.padding(bottom = 24.dp)
                )
            }
        }
    }
}
