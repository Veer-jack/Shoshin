package com.example.shoshinapp.ui.screens

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
import com.example.shoshinapp.R
import com.example.shoshinapp.ui.components.*
import com.example.shoshinapp.ui.theme.*
import android.content.Intent
import android.net.Uri
import androidx.compose.ui.platform.LocalContext

@Composable
fun SupportScreen(
    navController: NavController
) {
    var expandedIndex by remember { mutableStateOf<Int?>(null) }
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

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(scrollState)
            .padding(horizontal = 24.dp)
    ) {
        // App Bar
        Row(
            modifier = Modifier.fillMaxWidth().padding(top = 16.dp, bottom = 22.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            IconButton(onClick = { navController.popBackStack() }, modifier = Modifier.size(24.dp)) {
                Icon(painterResource(R.drawable.ic_arrow_left), contentDescription = "Back")
            }
            Text("Help & support", style = ShTitleStyle.copy(fontSize = 26.sp), fontWeight = FontWeight.SemiBold)
        }

        // Search
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
                .clip(RoundedCornerShape(14.dp))
                .background(ShSurface)
                .border(1.5.dp, ShLine2, RoundedCornerShape(14.dp))
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(painterResource(R.drawable.ic_search), null, modifier = Modifier.size(18.dp), tint = ShFog)
            Spacer(Modifier.width(10.dp))
            Text("Search for help", fontSize = 15.sp, color = ShFog2)
        }

        Spacer(Modifier.height(20.dp))

        // Contact Rows
        ShoshinCard(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(horizontal = 18.dp, vertical = 4.dp)) {
                SupportRow(
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
                HorizontalDivider(color = ShLine)
                SupportRow(
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

        Spacer(Modifier.height(24.dp))

        Kicker("Frequently asked", modifier = Modifier.padding(start = 4.dp, bottom = 12.dp))
        
        ShoshinCard(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(horizontal = 18.dp, vertical = 4.dp)) {
                faqs.forEachIndexed { i, (q, a) ->
                    FAQItem(
                        question = q,
                        answer = a,
                        isExpanded = expandedIndex == i,
                        onToggle = { expandedIndex = if (expandedIndex == i) null else i }
                    )
                    if (i < faqs.lastIndex) HorizontalDivider(color = ShLine)
                }
            }
        }

        Spacer(Modifier.height(24.dp))
        Text(
            text = "Shoshin v1.0 · Beginner's mind, every morning",
            style = ShLabelStyle,
            color = ShFog2,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth().padding(bottom = 24.dp)
        )
    }
}

@Composable
private fun SupportRow(
    icon: Int, 
    title: String, 
    sub: String,
    onClick: () -> Unit = {}
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(painterResource(icon), null, modifier = Modifier.size(20.dp), tint = ShInk)
        Spacer(Modifier.width(14.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(title, fontSize = 15.5.sp, fontWeight = FontWeight.Medium, color = ShInk)
            Text(sub, fontSize = 12.5.sp, color = ShFog)
        }
        Icon(painterResource(R.drawable.ic_arrow_right), null, modifier = Modifier.size(17.dp), tint = ShFog2)
    }
}

@Composable
private fun FAQItem(
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
                .padding(vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                question,
                modifier = Modifier.weight(1f),
                fontSize = 15.sp,
                fontWeight = FontWeight.Medium,
                color = ShInk
            )
            Icon(
                painter = painterResource(R.drawable.ic_arrow_right),
                null,
                modifier = Modifier
                    .size(17.dp)
                    .rotate(if (isExpanded) 90f else 0f),
                tint = ShFog2
            )
        }
        AnimatedVisibility(
            visible = isExpanded,
            enter = expandVertically() + fadeIn(),
            exit = shrinkVertically() + fadeOut()
        ) {
            Text(
                answer,
                fontSize = 13.5.sp,
                color = ShInk,
                lineHeight = 20.sp,
                modifier = Modifier.padding(bottom = 16.dp)
            )
        }
    }
}
