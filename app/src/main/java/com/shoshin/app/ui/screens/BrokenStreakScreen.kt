package com.shoshin.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.shoshin.app.R
import com.shoshin.app.ui.components.*
import com.shoshin.app.ui.theme.*

@Composable
fun BrokenStreakScreen(navController: NavController) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 24.dp, vertical = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(Modifier.weight(1f))

        // Broken chain motif
        Box(modifier = Modifier.size(120.dp, 80.dp), contentAlignment = Alignment.Center) {
             androidx.compose.foundation.Canvas(modifier = Modifier.fillMaxSize()) {
                 // Drawing a simplified broken chain representation or using icon
             }
             Icon(
                 painter = painterResource(id = R.drawable.ic_info), // Fallback to info or specific motif
                 contentDescription = null,
                 tint = ShVermillion,
                 modifier = Modifier.size(32.dp)
             )
        }

        Spacer(Modifier.height(8.dp))
        Kicker("The chain rested")
        Spacer(Modifier.height(4.dp))
        Text(
            "A miss is not\na failure",
            style = ShTitleStyle.copy(fontSize = 30.sp),
            color = MaterialTheme.colorScheme.onBackground,
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(8.dp))
        Text(
            "You held 14 mornings. That practice is yours to keep. Shoshin means beginning again — without judgement.",
            style = ShBodyStyle,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            modifier = Modifier.widthIn(max = 300.dp)
        )

        Spacer(Modifier.height(24.dp))

        // Preserved Stats
        ShoshinCard(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier.padding(18.dp),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                ShoshinStat(value = "14", label = "Best held", color = ShMatcha)
                Box(Modifier.width(1.dp).height(30.dp).background(MaterialTheme.colorScheme.outline))
                ShoshinStat(value = "148", label = "Total kept")
                Box(Modifier.width(1.dp).height(30.dp).background(MaterialTheme.colorScheme.outline))
                ShoshinStat(value = "86", unit = "%", label = "All-time")
            }
        }

        Spacer(Modifier.weight(1f))

        ShoshinButton(
            onClick = { navController.popBackStack() },
            variant = ShButtonVariant.Accent,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Begin again")
        }
        
        Spacer(Modifier.height(16.dp))
        Text(
            "Tomorrow is day one of the next chain.",
            style = ShLabelStyle,
            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
            textAlign = TextAlign.Center
        )
        
        Spacer(Modifier.height(28.dp))
    }
}
