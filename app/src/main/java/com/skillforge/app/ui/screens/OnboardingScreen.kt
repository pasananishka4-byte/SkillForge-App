package com.skillforge.app.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.skillforge.app.data.SoundManager
import com.skillforge.app.ui.AppStorage
import com.skillforge.app.ui.Screen
import com.skillforge.app.ui.theme.*

@Composable
fun OnboardingScreen(navController: NavHostController) {
    val storage = remember { AppStorage.storage }
    var userName by remember { mutableStateOf("") }
    var isStarting by remember { mutableStateOf(false) }

    val infiniteTransition = rememberInfiniteTransition(label = "ambient")
    val pulseAlpha by infiniteTransition.animateFloat(0.3f, 0.8f,
        infiniteRepeatable(tween(2000, easing = FastOutSlowInEasing), RepeatMode.Reverse), label = "pulseAlpha"
    )
    val floatOffset by infiniteTransition.animateFloat(0f, -10f,
        infiniteRepeatable(tween(1500, easing = FastOutSlowInEasing), RepeatMode.Reverse), label = "floatOffset"
    )
    val glowScale by infiniteTransition.animateFloat(1f, 1.3f,
        infiniteRepeatable(tween(3000, easing = FastOutSlowInEasing), RepeatMode.Reverse), label = "glowScale"
    )

    Box(modifier = Modifier.fillMaxSize().background(Background)) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawCircle(color = Primary.copy(alpha = pulseAlpha * 0.15f), radius = 140.dp.toPx() * glowScale, center = Offset(size.width * 0.2f, size.height * 0.15f))
            drawCircle(color = Secondary.copy(alpha = pulseAlpha * 0.1f), radius = 100.dp.toPx() * (1.2f - glowScale + 1f), center = Offset(size.width * 0.85f, size.height * 0.25f))
            drawCircle(color = PrimaryDark.copy(alpha = pulseAlpha * 0.12f), radius = 120.dp.toPx() * glowScale, center = Offset(size.width * 0.5f, size.height * 0.85f))
            drawCircle(color = CriticalThinkingColor.copy(alpha = pulseAlpha * 0.08f), radius = 80.dp.toPx(), center = Offset(size.width * 0.75f, size.height * 0.7f))
        }

        Column(
            modifier = Modifier.fillMaxSize().padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Spacer(modifier = Modifier.height(40.dp))

            Text(text = "⚒", fontSize = 64.sp, modifier = Modifier.offset(y = floatOffset.dp))
            Spacer(modifier = Modifier.height(12.dp))

            Text("SkillForge", style = MaterialTheme.typography.displayLarge, color = OnBackground, letterSpacing = 2.sp)
            Spacer(modifier = Modifier.height(8.dp))
            Text("Forge your skills through play", style = MaterialTheme.typography.bodyLarge, color = OnSurfaceVariant, textAlign = TextAlign.Center)

            Spacer(modifier = Modifier.height(40.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.medium,
                colors = CardDefaults.cardColors(containerColor = Surface)
            ) {
                Column(modifier = Modifier.fillMaxWidth().padding(20.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("What is SkillForge?", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = OnBackground)
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        "An interactive learning companion that helps you build real-world skills through gamified challenges, puzzles, and brain teasers.",
                        style = MaterialTheme.typography.bodyMedium, color = OnSurfaceVariant, textAlign = TextAlign.Center, lineHeight = 22.sp
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    FeatureHighlight("🧠  Sharpen critical thinking")
                    FeatureHighlight("📚  Expand general knowledge")
                    FeatureHighlight("🔄  Master meta-learning")
                    FeatureHighlight("💪  Build social-emotional skills")
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            OutlinedTextField(
                value = userName, onValueChange = { userName = it },
                label = { Text("What should we call you?") }, singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = OnBackground, unfocusedTextColor = OnBackground,
                    focusedBorderColor = Primary, unfocusedBorderColor = OnSurfaceVariant,
                    focusedLabelColor = Primary, unfocusedLabelColor = OnSurfaceVariant, cursorColor = Primary
                ),
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.small
            )

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    if (userName.isNotBlank() && !isStarting) {
                        SoundManager.playSuccess()
                        isStarting = true
                        storage.saveUserName(userName.trim())
                        storage.setOnboardingComplete()
                        navController.navigate(Screen.Home.route) { popUpTo(0) { inclusive = true } }
                    }
                },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                enabled = userName.isNotBlank() && !isStarting,
                colors = ButtonDefaults.buttonColors(containerColor = Primary, disabledContainerColor = Primary.copy(alpha = 0.4f)),
                shape = MaterialTheme.shapes.medium
            ) {
                Text(if (isStarting) "Starting..." else "Start Your Journey", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = OnPrimary)
            }

            Spacer(modifier = Modifier.height(16.dp))
            Text("Tap to begin your skill-building adventure", style = MaterialTheme.typography.labelSmall, color = OnSurfaceVariant.copy(alpha = 0.6f), textAlign = TextAlign.Center)
        }
    }
}

@Composable
private fun FeatureHighlight(text: String) {
    Text(text = text, style = MaterialTheme.typography.bodySmall, color = OnSurface,
        modifier = Modifier.fillMaxWidth().padding(vertical = 3.dp), textAlign = TextAlign.Start)
}
