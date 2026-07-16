package com.skillforge.app.ui.screens.challenge

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.skillforge.app.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChallengeStartScreen(
    skillId: Long,
    navController: NavController,
    viewModel: ChallengeViewModel = hiltViewModel()
) {
    var selectedDifficulty by remember { mutableStateOf("Easy") }
    val difficulties = listOf("Easy", "Medium", "Hard")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Choose Difficulty",
            style = MaterialTheme.typography.headlineMedium,
            color = OnSurface
        )

        Spacer(modifier = Modifier.height(32.dp))

        difficulties.forEach { difficulty ->
            val color = when (difficulty) {
                "Easy" -> EasyColor
                "Medium" -> MediumColor
                "Hard" -> HardColor
                else -> Primary
            }
            val isSelected = selectedDifficulty == difficulty

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 6.dp)
                    .clickable { selectedDifficulty = difficulty },
                colors = CardDefaults.cardColors(
                    containerColor = if (isSelected) color.copy(alpha = 0.2f) else Surface
                ),
                shape = RoundedCornerShape(12.dp),
                border = if (isSelected) CardDefaults.outlinedCardBorder().takeIf { false } else null
            ) {
                Row(
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(color.copy(alpha = if (isSelected) 0.3f else 0.1f)),
                        contentAlignment = Alignment.Center
                    ) {
                        if (isSelected) {
                            Icon(
                                imageVector = Icons.Filled.Check,
                                contentDescription = "Selected",
                                tint = color,
                                modifier = Modifier.size(24.dp)
                            )
                        } else {
                            Text(
                                text = difficulty.first().toString(),
                                style = MaterialTheme.typography.titleMedium,
                                color = color
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Column {
                        Text(
                            text = difficulty,
                            style = MaterialTheme.typography.titleMedium,
                            color = OnSurface,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                        )
                        Text(
                            text = when (difficulty) {
                                "Easy" -> "Warm up with basics"
                                "Medium" -> "Test your understanding"
                                "Hard" -> "Push your limits"
                                else -> ""
                            },
                            style = MaterialTheme.typography.bodySmall,
                            color = OnSurfaceVariant
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = {
                navController.navigate(
                    com.skillforge.app.ui.Screen.Challenge.createRoute(skillId, selectedDifficulty)
                ) {
                    popUpTo(com.skillforge.app.ui.Screen.ChallengeStart.createRoute(skillId)) {
                        inclusive = true
                    }
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Primary),
            shape = RoundedCornerShape(16.dp)
        ) {
            Text(
                text = "Start Challenge",
                style = MaterialTheme.typography.titleMedium,
                color = OnPrimary,
                fontWeight = FontWeight.Bold
            )
        }
    }
}
