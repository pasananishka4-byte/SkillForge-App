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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.skillforge.app.ui.AppStorage
import com.skillforge.app.ui.Screen
import com.skillforge.app.ui.theme.*

@Composable
fun OnboardingScreen(navController: NavHostController) {
    val storage = remember { AppStorage.storage }
    var userName by remember { mutableStateOf("") }
    var isStarting by remember { mutableStateOf(false) }

    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 0.8f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseAlpha"
    )

    val floatOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = -8f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "floatOffset"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
    ) {
        // Decorative background elements
        Canvas(modifier = Modifier.fillMaxSize()) {
            // Glowing orbs
            drawCircle(
                color = Primary.copy(alpha = pulseAlpha * 0.15f),
                radius = 120.dp.toPx(),
                center = Offset(size.width * 0.2f, size.height * 0.15f)
            )
            drawCircle(
                color = Secondary.copy(alpha = pulseAlpha * 0.1f),
                radius = 80.dp.toPx(),
                center = Offset(size.width * 0.85f, size.height * 0.25f)
            )
            drawCircle(
                color = PrimaryDark.copy(alpha = pulseAlpha * 0.12f),
                radius = 100.dp.toPx(),
                center = Offset(size.width * 0.5f, size.height * 0.85f)
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Spacer(modifier = Modifier.height(40.dp))

            // App Logo / Name
            Text(
                text = "⚒",
                fontSize = 64.sp,
                modifier = Modifier.offset(y = floatOffset.dp)
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "SkillForge",
                fontSize = 40.sp,
                fontWeight = FontWeight.Black,
                color = OnBackground,
                letterSpacing = 2.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Forge your skills through play",
                fontSize = 16.sp,
                color = OnSurfaceVariant,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(40.dp))

            // Description
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Surface)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "What is SkillForge?",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = OnBackground
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "An interactive learning companion that helps you build real-world skills through gamified challenges, puzzles, and brain teasers.",
                        fontSize = 14.sp,
                        color = OnSurfaceVariant,
                        textAlign = TextAlign.Center,
                        lineHeight = 22.sp
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    // Feature highlights
                    FeatureHighlight(text = "🧠  Sharpen critical thinking")
                    FeatureHighlight(text = "📚  Expand general knowledge")
                    FeatureHighlight(text = "🔄  Master meta-learning")
                    FeatureHighlight(text = "💪  Build social-emotional skills")
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Name Input
            OutlinedTextField(
                value = userName,
                onValueChange = { userName = it },
                label = { Text("What should we call you?") },
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = OnBackground,
                    unfocusedTextColor = OnBackground,
                    focusedBorderColor = Primary,
                    unfocusedBorderColor = OnSurfaceVariant,
                    focusedLabelColor = Primary,
                    unfocusedLabelColor = OnSurfaceVariant,
                    cursorColor = Primary
                ),
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Start Button
            Button(
                onClick = {
                    if (userName.isNotBlank() && !isStarting) {
                        isStarting = true
                        storage.saveUserName(userName.trim())
                        storage.setOnboardingComplete()
                        navController.navigate(Screen.Home.route) {
                            popUpTo(0) { inclusive = true }
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                enabled = userName.isNotBlank() && !isStarting,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Primary,
                    disabledContainerColor = Primary.copy(alpha = 0.4f)
                ),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text(
                    text = if (isStarting) "Starting..." else "Start Your Journey",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Tap to begin your skill-building adventure",
                fontSize = 12.sp,
                color = OnSurfaceVariant.copy(alpha = 0.6f),
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun FeatureHighlight(text: String) {
    Text(
        text = text,
        fontSize = 13.sp,
        color = OnSurface,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        textAlign = TextAlign.Start
    )
}
