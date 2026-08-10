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
    var phoneInput by remember { mutableStateOf("") }
    var referralCodeInput by remember { mutableStateOf(initialReferralCode ?: "") }
    var phoneError by remember { mutableStateOf<String?>(null) }
    val scrollState = rememberScrollState()

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
            Spacer(Modifier.height(12.dp))
            Text(
                text  = stringResource(R.string.auth_subtitle),
                style = ShBodyStyle,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 16.dp)
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
                    phoneError = null
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
                onValueChange = { referralCodeInput = it.uppercase() },
                label = "REFERRAL CODE (OPTIONAL)",
                placeholder = "e.g. VINAY142",
                enabled = !isGoogleLoading
            )

            Spacer(Modifier.height(48.dp))

            // CTA
            ShoshinButton(
                onClick = {
                    if (phoneInput.length == 10) {
                        onPhoneContinue(phoneInput, referralCodeInput.takeIf { it.isNotEmpty() })
                    } else {
                        phoneError = "Enter a valid 10-digit number"
                    }
                },
                variant = ShButtonVariant.Primary,
                modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
                trailingIcon = { Icon(painterResource(R.drawable.ic_arrow_right), null, modifier = Modifier.size(18.dp)) }
            ) {
                Text("Continue", fontWeight = FontWeight.Bold)
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
