package com.Shoshin.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.font.FontWeight
import com.Shoshin.app.R
import com.Shoshin.app.ui.components.*
import com.Shoshin.app.ui.theme.*
import com.Shoshin.app.utils.AnalyticsManager

@Composable
fun AuthScreen(
    onPhoneContinue: (phoneNumber: String, referralCode: String?) -> Unit,
    onGoogleSignIn: () -> Unit,
    onPrivacyClick: () -> Unit,
    onTermsClick: () -> Unit,
    modifier: Modifier = Modifier,
    isGoogleLoading: Boolean = false,
    initialReferralCode: String? = null,
    externalError: String? = null,
    onClearError: () -> Unit = {}
) {
    // Saveable so the number survives the trip to the OTP screen and back —
    // plain remember() is dropped when this destination leaves the composition.
    var phoneInput by rememberSaveable { mutableStateOf("") }
    var referralCodeInput by rememberSaveable(initialReferralCode) { mutableStateOf(initialReferralCode ?: "") }
    var phoneError by rememberSaveable { mutableStateOf<String?>(null) }
    var referralError by rememberSaveable { mutableStateOf<String?>(null) }
    val scrollState = rememberScrollState()

    // Indian mobile numbers are 10 digits and never start below 6.
    fun validatePhone(value: String): String? = when {
        value.isEmpty() -> "Enter your mobile number"
        value.length < 10 -> "Enter all 10 digits"
        value.first() !in '6'..'9' -> "Mobile numbers start with 6, 7, 8 or 9"
        else -> null
    }

    // Optional field: blank is valid, anything present must look like a real code.
    fun validateReferral(value: String): String? = when {
        value.isEmpty() -> null
        value.length < 4 -> "Referral codes are at least 4 characters"
        !value.all { it.isLetterOrDigit() } -> "Letters and numbers only"
        else -> null
    }

    ShoshinTheme(type = ShoshinThemeType.DYNAMIC) {
        val termsText = buildAnnotatedString {
            append("By continuing you agree to our ")
            pushStringAnnotation(tag = "terms", annotation = "terms")
            withStyle(style = ShLabelStyle.toSpanStyle().copy(color = MaterialTheme.colorScheme.onBackground)) {
                append("Terms")
            }
            pop()
            append(" and ")
            pushStringAnnotation(tag = "privacy", annotation = "privacy")
            withStyle(style = ShLabelStyle.toSpanStyle().copy(color = MaterialTheme.colorScheme.onBackground)) {
                append("Privacy Policy")
            }
            pop()
            append(".")
        }

        if (externalError != null) {
            AlertDialog(
                onDismissRequest = onClearError,
                title = { Text("Sign-In Failed", style = ShTitleStyle.copy(fontSize = 20.sp)) },
                text = { Text(externalError, style = ShBodyStyle) },
                confirmButton = {
                    TextButton(onClick = onClearError) {
                        Text("OK", color = ShVermillion, fontWeight = FontWeight.Bold)
                    }
                },
                containerColor = MaterialTheme.colorScheme.surface,
                shape = RoundedCornerShape(24.dp)
            )
        }

        if (isGoogleLoading) {
            LoadingDialog(message = "Signing in...")
        }

        Column(
            modifier = modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .statusBarsPadding()
                .navigationBarsPadding()
                .imePadding()
                .verticalScroll(scrollState)
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(40.dp))
            
            // Logo
            ShoshinLogoMark(modifier = Modifier.size(64.dp))

            Spacer(Modifier.height(32.dp))

            // Headline
            Text(
                text  = stringResource(R.string.auth_title),
                style = ShTitleStyle.copy(fontSize = 32.sp),
                color = MaterialTheme.colorScheme.onBackground,
                textAlign = TextAlign.Center
            )
            Spacer(Modifier.height(40.dp))

            // OAuth Options
            OAuthButton(
                provider = OAuthProvider.Google,
                onClick = {
                    AnalyticsManager.logAuthMethodSelected("google")
                    onGoogleSignIn()
                },
                enabled = !isGoogleLoading
            )

            Spacer(Modifier.height(24.dp))

            ShoshinDivider(label = "or continue with")
            
            Spacer(Modifier.height(24.dp))

            // Phone Input
            ShoshinTextField(
                value = phoneInput,
                onValueChange = { input ->
                    val filtered = input.filter { it.isDigit() }.take(10)
                    phoneInput = filtered
                    // Re-validate as they type, but only once an error is already showing —
                    // otherwise the first keystroke would flag a half-typed number.
                    if (phoneError != null) phoneError = validatePhone(filtered)
                },
                label = "MOBILE NUMBER",
                prefix = "+91 ",
                placeholder = "98765 43210",
                enabled = !isGoogleLoading
            )

            phoneError?.let {
                Text(it, color = MaterialTheme.colorScheme.error, style = ShLabelStyle, modifier = Modifier.padding(top = 4.dp).align(Alignment.Start))
            }

            Spacer(Modifier.height(20.dp))

            // Optional Referral Code
            ShoshinTextField(
                value = referralCodeInput,
                onValueChange = { input ->
                    val filtered = input.filter { it.isLetterOrDigit() }.take(12).uppercase()
                    referralCodeInput = filtered
                    if (referralError != null) referralError = validateReferral(filtered)
                },
                label = "REFERRAL CODE (OPTIONAL)",
                placeholder = "e.g. VINAY142",
                enabled = !isGoogleLoading
            )

            referralError?.let {
                Text(it, color = MaterialTheme.colorScheme.error, style = ShLabelStyle, modifier = Modifier.padding(top = 4.dp).align(Alignment.Start))
            }

            Spacer(Modifier.height(48.dp))

            // CTA
            ShoshinButton(
                onClick = {
                    val phoneProblem = validatePhone(phoneInput)
                    val referralProblem = validateReferral(referralCodeInput)
                    phoneError = phoneProblem
                    referralError = referralProblem
                    if (phoneProblem == null && referralProblem == null) {
                        onPhoneContinue(phoneInput, referralCodeInput.takeIf { it.isNotEmpty() })
                    }
                },
                variant = ShButtonVariant.Accent,
                enabled = !isGoogleLoading,
                modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
                trailingIcon = { Icon(painterResource(R.drawable.ic_arrow_right), null, modifier = Modifier.size(18.dp)) }
            ) {
                Text("Continue", color = Color.White, fontWeight = FontWeight.Bold)
            }

            ClickableText(
                text = termsText,
                style = ShLabelStyle.copy(fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, textAlign = TextAlign.Center),
                onClick = { offset ->
                    termsText.getStringAnnotations(tag = "terms", start = offset, end = offset)
                        .firstOrNull()?.let { onTermsClick() }
                    termsText.getStringAnnotations(tag = "privacy", start = offset, end = offset)
                        .firstOrNull()?.let { onPrivacyClick() }
                },
                modifier = Modifier.padding(bottom = 24.dp)
            )
        }
    }
}
