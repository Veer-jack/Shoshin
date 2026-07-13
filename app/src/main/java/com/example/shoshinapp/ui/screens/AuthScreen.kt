package com.example.shoshinapp.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.shoshinapp.R
import com.example.shoshinapp.ui.components.*
import com.example.shoshinapp.ui.theme.*
import com.example.shoshinapp.utils.AnalyticsManager
import android.util.Patterns

enum class AuthInputMode { Phone, Email }

@Composable
fun AuthScreen(
    onPhoneContinue: (phoneNumber: String, referralCode: String?) -> Unit,
    onEmailContinue: (email: String, pass: String, referralCode: String?) -> Unit,
    onGoogleSignIn: () -> Unit,
    onPrivacyClick: () -> Unit,
    onTermsClick: () -> Unit,
    modifier: Modifier = Modifier,
    isGoogleLoading: Boolean = false,
    initialReferralCode: String? = null
) {
    var inputMode by remember { mutableStateOf(AuthInputMode.Phone) }
    var phoneInput by remember { mutableStateOf("") }
    var emailInput by remember { mutableStateOf("") }
    var passwordInput by remember { mutableStateOf("") }
    var referralCodeInput by remember { mutableStateOf(initialReferralCode ?: "") }
    
    var emailError by remember { mutableStateOf<String?>(null) }
    var phoneError by remember { mutableStateOf<String?>(null) }
    var passwordError by remember { mutableStateOf<String?>(null) }
    var referralError by remember { mutableStateOf<String?>(null) }

    val termsText = buildAnnotatedString {
        append("By continuing you agree to our ")
        pushStringAnnotation(tag = "terms", annotation = "terms")
        withStyle(style = ShLabelStyle.toSpanStyle().copy(color = ShVermillion)) {
            append("Terms")
        }
        pop()
        append(" and ")
        pushStringAnnotation(tag = "privacy", annotation = "privacy")
        withStyle(style = ShLabelStyle.toSpanStyle().copy(color = ShVermillion)) {
            append("Privacy Policy")
        }
        pop()
        append(".")
    }

    fun validatePhone(phone: String): Boolean {
        return if (phone.length == 10 && phone.all { it.isDigit() }) {
            phoneError = null
            true
        } else {
            phoneError = "Please enter a valid 10-digit phone number"
            false
        }
    }

    fun validateEmail(email: String): Boolean {
        return if (Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailError = null
            true
        } else {
            emailError = "Please enter a valid email address"
            false
        }
    }

    fun validatePassword(password: String): Boolean {
        return when {
            password.length < 8 -> {
                passwordError = "Password must be at least 8 characters"
                false
            }
            !password.any { it.isUpperCase() } -> {
                passwordError = "Password must contain at least one uppercase letter"
                false
            }
            !password.any { it.isDigit() } -> {
                passwordError = "Password must contain at least one number"
                false
            }
            else -> {
                passwordError = null
                true
            }
        }
    }

    if (isGoogleLoading) {
        LoadingDialog(message = "Signing in with Google...")
    }

    Box(modifier = modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp)
                .systemBarsPadding(),
        ) {
            // Logo
            Spacer(Modifier.height(24.dp))
            ShoshinLogoMark(size = 40.dp)
            Spacer(Modifier.height(36.dp))

            // Headline
            Text(
                text  = stringResource(R.string.auth_title),
                style = ShTitleStyle,
            )
            Spacer(Modifier.height(8.dp))
            Text(
                text  = stringResource(R.string.auth_subtitle),
                style = ShBodyStyle,
            )
            Spacer(Modifier.height(28.dp))

            // OAuth buttons
            OAuthButton(
                provider = OAuthProvider.Google, 
                onClick = {
                    AnalyticsManager.logAuthMethodSelected("google")
                    onGoogleSignIn()
                },
                enabled = !isGoogleLoading
            )
            Spacer(Modifier.height(22.dp))

            // Divider
            ShoshinDivider(label = stringResource(R.string.auth_or_continue_with))
            Spacer(Modifier.height(22.dp))

            // Phone / Email segmented
            ShoshinSegmented(
                options  = listOf(
                    SegmentOption(AuthInputMode.Phone, stringResource(R.string.auth_tab_phone)),
                    SegmentOption(AuthInputMode.Email, stringResource(R.string.auth_tab_email)),
                ),
                selected = inputMode,
                onSelect = { 
                    if (!isGoogleLoading) {
                        AnalyticsManager.logAuthMethodSelected(if (it == AuthInputMode.Phone) "phone" else "email")
                        inputMode = it
                        emailError = null
                        phoneError = null
                        passwordError = null
                    }
                },
            )
            Spacer(Modifier.height(16.dp))

            // Text field
            when (inputMode) {
                AuthInputMode.Phone -> {
                    ShoshinTextField(
                        value       = phoneInput,
                        onValueChange = { 
                            phoneInput = it
                            if (phoneError != null) validatePhone(it)
                        },
                        label       = stringResource(R.string.auth_phone_label),
                        prefix      = stringResource(R.string.auth_phone_prefix),
                        placeholder = stringResource(R.string.auth_phone_placeholder),
                        enabled     = !isGoogleLoading
                    )
                    phoneError?.let {
                        Text(
                            text = it,
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.padding(top = 4.dp, start = 4.dp)
                        )
                    }
                }
                AuthInputMode.Email -> {
                    Column {
                        ShoshinTextField(
                            value       = emailInput,
                            onValueChange = { 
                                emailInput = it
                                if (emailError != null) validateEmail(it)
                            },
                            label       = stringResource(R.string.auth_email_label),
                            placeholder = stringResource(R.string.auth_email_placeholder),
                            enabled     = !isGoogleLoading
                        )
                        emailError?.let {
                            Text(
                                text = it,
                                color = MaterialTheme.colorScheme.error,
                                style = MaterialTheme.typography.bodySmall,
                                modifier = Modifier.padding(top = 4.dp, start = 4.dp)
                            )
                        }
                        
                        Spacer(Modifier.height(12.dp))
                        
                        ShoshinTextField(
                            value       = passwordInput,
                            onValueChange = { 
                                passwordInput = it
                                if (passwordError != null) validatePassword(it)
                            },
                            label       = "Password",
                            placeholder = "At least 8 characters",
                            enabled     = !isGoogleLoading
                        )
                        passwordError?.let {
                            Text(
                                text = it,
                                color = MaterialTheme.colorScheme.error,
                                style = MaterialTheme.typography.bodySmall,
                                modifier = Modifier.padding(top = 4.dp, start = 4.dp)
                            )
                        }
                    }
                }
            }

            Spacer(Modifier.height(24.dp))

            // Optional Referral Code
            ShoshinTextField(
                value = referralCodeInput,
                onValueChange = { 
                    referralCodeInput = it.uppercase()
                    referralError = null
                },
                label = "Referral Code (Optional)",
                placeholder = "e.g. VINAY142",
                enabled = !isGoogleLoading
            )

            Spacer(Modifier.weight(1f))

            // CTA
            ShoshinButton(
                onClick  = {
                    val code = referralCodeInput.takeIf { it.isNotEmpty() }
                    when (inputMode) {
                        AuthInputMode.Phone -> {
                            if (validatePhone(phoneInput)) onPhoneContinue(phoneInput, code)
                        }
                        AuthInputMode.Email -> {
                            val isEmailValid = validateEmail(emailInput)
                            val isPassValid = validatePassword(passwordInput)
                            if (isEmailValid && isPassValid) onEmailContinue(emailInput, passwordInput, code)
                        }
                    }
                },
                variant  = ShButtonVariant.Accent,
                enabled  = !isGoogleLoading
            ) {
                Text(stringResource(R.string.auth_continue))
            }
            Spacer(Modifier.height(16.dp))

            ClickableText(
                text = termsText,
                style = ShLabelStyle.copy(
                    fontSize = 11.5.sp,
                    color = ShFog2,
                ),
                onClick = { offset ->
                    if (!isGoogleLoading) {
                        termsText.getStringAnnotations(tag = "terms", start = offset, end = offset)
                            .firstOrNull()?.let { onTermsClick() }
                        termsText.getStringAnnotations(tag = "privacy", start = offset, end = offset)
                            .firstOrNull()?.let { onPrivacyClick() }
                    }
                }
            )
            Spacer(Modifier.height(8.dp))
        }
    }
}
