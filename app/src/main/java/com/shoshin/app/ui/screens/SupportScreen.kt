package com.Shoshin.app.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.Shoshin.app.R
import com.Shoshin.app.ui.components.*
import com.Shoshin.app.ui.theme.*
import android.content.Intent
import android.net.Uri
import androidx.compose.ui.platform.LocalContext

@Composable
fun SupportScreen(
    navController: NavController
) {
    var expandedIndex by remember { mutableIntStateOf(-1) }
    val scrollState = rememberScrollState()
    val context = LocalContext.current

    val faqs = listOf(
        "What happens if I miss a morning?" to "Nothing punishing. Your streak resets, but your total mornings kept stays on record. A miss is not a failure — begin again tomorrow.",
        "Can I change my routine after onboarding?" to "Yes. Go to Settings → Edit path to reorder, add, or remove checkpoints at any time.",
        "Why does the app need my camera?" to "Only when you choose photo proof for a checkpoint. Photos stay on your device unless you explicitly share them — Shoshin never uploads them.",
        "How does the 71-Day Discipline challenge work?" to "It's an advanced identity challenge unlocked after your first 21-Day Challenge. Three phases: Foundation, Reinforcement, Integration.",
        "Can I pause my subscription?" to "Yes, from Settings → Shoshin Pro → Manage subscription. You'll keep access until the end of your billing period.",
        "Is my data shared with anyone?" to "Never sold. See Settings → Privacy & Data for a full export or deletion of your account."
    )

    ShoshinTheme(type = ShoshinThemeType.DYNAMIC) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(ShNight)
                .statusBarsPadding()
        ) {
            // App Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(painterResource(R.drawable.ic_arrow_left), contentDescription = "Back", tint = Color.White)
                }
                Spacer(Modifier.width(8.dp))
                Text("Help & support", style = ShTitleStyle.copy(fontSize = 28.sp, color = Color.White))
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(scrollState)
                    .padding(horizontal = 24.dp)
            ) {
                // Search
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .border(1.2.dp, ShNightLine, RoundedCornerShape(16.dp))
                        .background(ShNight2)
                        .padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(painterResource(R.drawable.ic_search), null, modifier = Modifier.size(20.dp), tint = ShNightMuted)
                    Spacer(Modifier.width(12.dp))
                    Text("Search for help", style = ShBodyStyle.copy(color = ShNightMuted))
                }

                Spacer(Modifier.height(24.dp))

                // Contact Card
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(24.dp))
                        .background(ShNight2)
                ) {
                    Column(modifier = Modifier.padding(vertical = 8.dp)) {
                        SupportRowDark(
                            icon = R.drawable.ic_mail, 
                            title = "Message support", 
                            sub = "Usually replies within a day",
                            onClick = {
                                val intent = Intent(Intent.ACTION_SENDTO).apply {
                                    data = Uri.parse("mailto:")
                                    putExtra(Intent.EXTRA_EMAIL, arrayOf("cobwebtechnologies1@gmail.com"))
                                    putExtra(Intent.EXTRA_SUBJECT, "Shoshin App Support")
                                }
                                context.startActivity(Intent.createChooser(intent, "Contact Support"))
                            }
                        )
                        HorizontalDivider(color = ShNightLine, modifier = Modifier.padding(horizontal = 24.dp))
                        SupportRowDark(
                            icon = R.drawable.ic_help, 
                            title = "Report a problem", 
                            sub = "Bugs, crashes, unexpected behaviour",
                            onClick = {
                                val intent = Intent(Intent.ACTION_SENDTO).apply {
                                    data = Uri.parse("mailto:")
                                    putExtra(Intent.EXTRA_EMAIL, arrayOf("cobwebtechnologies1@gmail.com"))
                                    putExtra(Intent.EXTRA_SUBJECT, "Shoshin App - Bug Report")
                                }
                                context.startActivity(Intent.createChooser(intent, "Report a Problem"))
                            }
                        )
                    }
                }

                Spacer(Modifier.height(32.dp))

                Kicker("FREQUENTLY ASKED", color = ShNightMuted)
                
                Spacer(Modifier.height(16.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(24.dp))
                        .background(ShNight2)
                        .padding(horizontal = 20.dp, vertical = 8.dp)
                ) {
                    Column {
                        faqs.forEachIndexed { i, (q, a) ->
                            FAQItemDark(
                                question = q,
                                answer = a,
                                isExpanded = expandedIndex == i,
                                onToggle = { expandedIndex = if (expandedIndex == i) -1 else i }
                            )
                            if (i < faqs.lastIndex) HorizontalDivider(color = ShNightLine)
                        }
                    }
                }

                Spacer(Modifier.height(32.dp))
                Text(
                    text = "Shoshin v1.0 · Beginner's mind, every morning",
                    style = ShLabelStyle.copy(fontSize = 13.sp),
                    color = ShNightMuted,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth().padding(bottom = 32.dp)
                )
            }
        }
    }
}

@Composable
private fun SupportRowDark(
    icon: Int, 
    title: String, 
    sub: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(20.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(painterResource(icon), null, modifier = Modifier.size(20.dp), tint = Color.White)
        Spacer(Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(title, style = ShH2Style.copy(fontSize = 16.sp, color = Color.White))
            Text(sub, style = ShLabelStyle.copy(fontSize = 13.sp, color = ShNightMuted))
        }
        Icon(painterResource(R.drawable.ic_arrow_right), null, modifier = Modifier.size(18.dp), tint = ShNightLine)
    }
}

@Composable
private fun FAQItemDark(
    question: String,
    answer: String,
    isExpanded: Boolean,
    onToggle: () -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onToggle() }
                .padding(vertical = 18.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                question,
                modifier = Modifier.weight(1f),
                style = ShH2Style.copy(fontSize = 16.sp, color = Color.White)
            )
            Icon(
                painter = painterResource(R.drawable.ic_arrow_right),
                null,
                modifier = Modifier
                    .size(18.dp)
                    .rotate(if (isExpanded) 90f else 0f),
                tint = ShNightMuted
            )
        }
        AnimatedVisibility(
            visible = isExpanded,
            enter = expandVertically() + fadeIn(),
            exit = shrinkVertically() + fadeOut()
        ) {
            Text(
                answer,
                style = ShBodyStyle.copy(fontSize = 14.sp, color = ShNightMuted, lineHeight = 20.sp),
                modifier = Modifier.padding(bottom = 20.dp)
            )
        }
    }
}
