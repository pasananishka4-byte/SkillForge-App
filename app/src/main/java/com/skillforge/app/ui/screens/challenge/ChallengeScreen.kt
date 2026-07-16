package com.skillforge.app.ui.screens.challenge

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
import androidx.compose.material.icons.filled.Bolt
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
fun ChallengeScreen(
    skillId: Long,
    difficulty: String,
    navController: NavController,
    viewModel: ChallengeViewModel = hiltViewModel()
) {
    LaunchedEffect(skillId, difficulty) {
        viewModel.loadChallenges(skillId, difficulty)
    }

    val uiState by viewModel.uiState.collectAsState()

    if (uiState.isLoading) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = Primary)
        }
        return
    }

    if (uiState.isComplete) {
        ChallengeCompleteScreen(
            score = uiState.totalXP,
            correct = uiState.correctCount,
            total = uiState.totalAnswered,
            onBack = { navController.popBackStack() }
        )
        return
    }

    val challenge = uiState.currentChallenge
    if (challenge == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("No challenges available", color = OnSurface)
        }
        return
    }

    val difficultyColor = Color(viewModel.getDifficultyColor())

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
    ) {
        // Top bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { navController.popBackStack() }) {
                Icon(
                    imageVector = Icons.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = OnSurface
                )
            }

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "${uiState.currentChallengeIndex + 1}/${uiState.challenges.size}",
                    style = MaterialTheme.typography.labelMedium,
                    color = OnSurfaceVariant
                )
            }

            Surface(
                shape = RoundedCornerShape(8.dp),
                color = difficultyColor.copy(alpha = 0.15f)
            ) {
                Text(
                    text = difficulty,
                    style = MaterialTheme.typography.labelSmall,
                    color = difficultyColor,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }
        }

        // Progress bar
        LinearProgressIndicator(
            progress = uiState.progress,
            modifier = Modifier
                .fillMaxWidth()
                .height(4.dp),
            color = Primary,
            trackColor = SurfaceVariant,
        )

        LazyColumn(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Question
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Surface),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Text(
                            text = challenge.question,
                            style = MaterialTheme.typography.bodyLarge,
                            color = OnSurface,
                            lineHeight = 24.sp
                        )
                    }
                }
            }

            // Options
            items(challenge.options.size) { index ->
                val isSelected = uiState.selectedAnswer == index
                val isCorrect = index == challenge.correctAnswerIndex
                val showResult = uiState.isAnswered

                val containerColor = when {
                    showResult && isCorrect -> SuccessColor.copy(alpha = 0.2f)
                    showResult && isSelected && !isCorrect -> ErrorColor.copy(alpha = 0.2f)
                    isSelected -> Primary.copy(alpha = 0.15f)
                    else -> Surface
                }

                val borderColor = when {
                    showResult && isCorrect -> SuccessColor
                    showResult && isSelected && !isCorrect -> ErrorColor
                    isSelected -> Primary
                    else -> Color.Transparent
                }

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable(enabled = !uiState.isAnswered) {
                            viewModel.selectAnswer(index)
                        },
                    colors = CardDefaults.cardColors(containerColor = containerColor),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .padding(16.dp)
                            .fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(borderColor.copy(alpha = if (showResult && (isCorrect || isSelected)) 0.2f else 0.1f)),
                            contentAlignment = Alignment.Center
                        ) {
                            if (showResult && isCorrect) {
                                Icon(
                                    imageVector = Icons.Filled.Check,
                                    contentDescription = "Correct",
                                    tint = SuccessColor,
                                    modifier = Modifier.size(20.dp)
                                )
                            } else if (showResult && isSelected && !isCorrect) {
                                Icon(
                                    imageVector = Icons.Filled.Close,
                                    contentDescription = "Wrong",
                                    tint = ErrorColor,
                                    modifier = Modifier.size(20.dp)
                                )
                            } else {
                                Text(
                                    text = ('A' + index).toString(),
                                    style = MaterialTheme.typography.labelLarge,
                                    color = if (isSelected) Primary else OnSurfaceVariant
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = challenge.options[index],
                            style = MaterialTheme.typography.bodyMedium,
                            color = OnSurface,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }

            // Explanation (after answering)
            if (uiState.isAnswered) {
                item {
                    AnimatedVisibility(visible = true, enter = fadeIn(), exit = fadeOut()) {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(
                                containerColor = if (uiState.selectedAnswer == challenge.correctAnswerIndex)
                                    SuccessColor.copy(alpha = 0.1f)
                                else ErrorColor.copy(alpha = 0.1f)
                            ),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = if (uiState.selectedAnswer == challenge.correctAnswerIndex)
                                            Icons.Filled.Check else Icons.Filled.Close,
                                        contentDescription = null,
                                        tint = if (uiState.selectedAnswer == challenge.correctAnswerIndex)
                                            SuccessColor else ErrorColor
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = if (uiState.selectedAnswer == challenge.correctAnswerIndex)
                                            "Correct!" else "Incorrect",
                                        style = MaterialTheme.typography.titleSmall,
                                        color = if (uiState.selectedAnswer == challenge.correctAnswerIndex)
                                            SuccessColor else ErrorColor,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = challenge.explanation,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = OnSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }

            // Action button
            item {
                Spacer(modifier = Modifier.height(8.dp))
                Button(
                    onClick = {
                        if (!uiState.isAnswered) {
                            viewModel.confirmAnswer()
                        } else {
                            viewModel.nextChallenge()
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (!uiState.isAnswered) Primary else Secondary
                    ),
                    shape = RoundedCornerShape(16.dp),
                    enabled = uiState.selectedAnswer != null || uiState.isAnswered
                ) {
                    Text(
                        text = if (!uiState.isAnswered) "Confirm" else "Next",
                        style = MaterialTheme.typography.titleMedium,
                        color = OnPrimary,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // XP earned display
            if (uiState.isAnswered) {
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Bolt,
                            contentDescription = null,
                            tint = Primary,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "+${challenge.xpReward} XP",
                            style = MaterialTheme.typography.labelLarge,
                            color = Primary
                        )
                    }
                }
            }

            item { Spacer(modifier = Modifier.height(16.dp)) }
        }
    }
}

@Composable
fun ChallengeCompleteScreen(
    score: Int,
    correct: Int,
    total: Int,
    onBack: () -> Unit
) {
    val accuracy = if (total > 0) (correct.toFloat() / total * 100).toInt() else 0

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Filled.Bolt,
            contentDescription = null,
            tint = Primary,
            modifier = Modifier.size(80.dp)
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Challenge Complete!",
            style = MaterialTheme.typography.headlineMedium,
            color = OnSurface,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Score card
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Surface),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "+$score XP",
                    style = MaterialTheme.typography.displayLarge,
                    color = Primary,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "$correct/$total",
                            style = MaterialTheme.typography.headlineSmall,
                            color = SuccessColor,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Correct",
                            style = MaterialTheme.typography.labelSmall,
                            color = OnSurfaceVariant
                        )
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "$accuracy%",
                            style = MaterialTheme.typography.headlineSmall,
                            color = Primary,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Accuracy",
                            style = MaterialTheme.typography.labelSmall,
                            color = OnSurfaceVariant
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = onBack,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Primary),
            shape = RoundedCornerShape(16.dp)
        ) {
            Text(
                text = "Back to Home",
                style = MaterialTheme.typography.titleMedium,
                color = OnPrimary,
                fontWeight = FontWeight.Bold
            )
        }
    }
}
