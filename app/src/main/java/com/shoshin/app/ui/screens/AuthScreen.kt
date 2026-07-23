package com.shoshin.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.shoshin.app.R
import com.shoshin.app.ui.components.*
import com.shoshin.app.ui.theme.*
import com.shoshin.app.utils.AnalyticsManager
import android.util.Patterns

enum class AuthInputMode { Phone }

@Composable
fun AuthScreen(
    onPhoneContinue: (phoneNumber: String, referralCode: String?) -> Unit,
    onGoogleSignIn: () -> Unit,
    onPrivacyClick: () -> Unit,
    onTermsClick: () -> Unit,
    modifier: Modifier = Modifier,
    isGoogleLoading: Boolean = false,
    initialReferralCode: String? = null
) {
    var inputMode by remember { mutableStateOf(AuthInputMode.Phone) }
    var phoneInput by remember { mutableStateOf("") }
    var referralCodeInput by remember { mutableStateOf(initialReferralCode ?: "") }
    
    var phoneError by remember { mutableStateOf<String?>(null) }
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

    if (isGoogleLoading) {
        LoadingDialog(message = "Signing in with Google...")
    }

    Box(modifier = modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp)
                .systemBarsPadding(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(24.dp))
            
            // Logo middle aligned
            ShoshinLogoMark()

            Spacer(Modifier.height(32.dp))

            // Headline
            Text(
                text  = stringResource(R.string.auth_title),
                style = ShTitleStyle,
                color = MaterialTheme.colorScheme.onBackground,
                textAlign = TextAlign.Center
            )
            Spacer(Modifier.height(8.dp))
            Text(
                text  = stringResource(R.string.auth_subtitle),
                style = ShBodyStyle,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
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

            // Text field
            ShoshinTextField(
                value       = phoneInput,
                onValueChange = { input ->
                    // Allow only digits and limit to 10
                    val filtered = input.filter { it.isDigit() }.take(10)
                    phoneInput = filtered
                    if (phoneError != null) validatePhone(filtered)
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
            val interactionSource = remember { MutableInteractionSource() }
            
            ShoshinButton(
                onClick  = {
                    val code = referralCodeInput.takeIf { it.isNotEmpty() }
                    if (validatePhone(phoneInput)) onPhoneContinue(phoneInput, code)
                },
                variant  = ShButtonVariant.Accent,
                enabled  = !isGoogleLoading,
                modifier = Modifier.fillMaxWidth(),
                interactionSource = interactionSource,
                pressedColor = Color.Black
            ) {
                Text(
                    text = stringResource(R.string.auth_continue),
                    color = Color.White
                )
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
