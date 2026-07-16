package com.skillforge.app.ui.screens.daily

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
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
import com.skillforge.app.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DailyChallengeScreen(
    onBack: () -> Unit,
    viewModel: DailyChallengeViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    if (uiState.isLoading) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = Primary)
        }
        return
    }

    if (uiState.isCompleted) {
        DailyCompletedScreen(
            streakDays = uiState.streakDays,
            onBack = onBack
        )
        return
    }

    if (uiState.isComplete) {
        DailyCompleteScreen(
            score = uiState.totalXP,
            correct = uiState.correctCount,
            total = uiState.challenges.size,
            streakDays = uiState.streakDays,
            onBack = onBack
        )
        return
    }

    val challenge = uiState.currentChallenge

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
    ) {
        // Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(Icons.Filled.ArrowBack, "Back", tint = OnSurface)
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Daily Challenge",
                    style = MaterialTheme.typography.titleLarge,
                    color = StreakFire,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "${uiState.currentChallengeIndex + 1}/${uiState.challenges.size} • Double XP!",
                    style = MaterialTheme.typography.bodySmall,
                    color = OnSurfaceVariant
                )
            }
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = StreakFire.copy(alpha = 0.15f)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Filled.LocalFireDepartment, null,
                        tint = StreakFire, modifier = Modifier.size(16.dp)
                    )
                    Spacer(Modifier.width(4.dp))
                    Text(
                        "${uiState.streakDays} day streak",
                        style = MaterialTheme.typography.labelSmall,
                        color = StreakFire
                    )
                }
            }
        }

        // Progress
        LinearProgressIndicator(
            progress = uiState.currentChallengeIndex.toFloat() / uiState.challenges.size,
            modifier = Modifier.fillMaxWidth().height(4.dp),
            color = StreakFire, trackColor = SurfaceVariant,
        )

        if (challenge != null) {
            LazyColumn(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
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
                                color = OnSurface, lineHeight = 24.sp
                            )
                        }
                    }
                }

                items(challenge.options.size) { index ->
                    val isSelected = uiState.selectedAnswer == index
                    val isCorrect = index == challenge.correctAnswerIndex
                    val showResult = uiState.isAnswered

                    val containerColor = when {
                        showResult && isCorrect -> SuccessColor.copy(alpha = 0.2f)
                        showResult && isSelected && !isCorrect -> ErrorColor.copy(alpha = 0.2f)
                        isSelected -> StreakFire.copy(alpha = 0.15f)
                        else -> Surface
                    }

                    Card(
                        modifier = Modifier.fillMaxWidth()
                            .clickable(enabled = !uiState.isAnswered) { viewModel.selectAnswer(index) },
                        colors = CardDefaults.cardColors(containerColor = containerColor),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp).fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier.size(36.dp).clip(CircleShape)
                                    .background(
                                        when {
                                            showResult && isCorrect -> SuccessColor.copy(alpha = 0.2f)
                                            showResult && isSelected && !isCorrect -> ErrorColor.copy(alpha = 0.2f)
                                            isSelected -> StreakFire.copy(alpha = 0.2f)
                                            else -> SurfaceVariant
                                        }
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                if (showResult && isCorrect) {
                                    Icon(Icons.Filled.Check, null, tint = SuccessColor, modifier = Modifier.size(20.dp))
                                } else if (showResult && isSelected && !isCorrect) {
                                    Icon(Icons.Filled.Close, null, tint = ErrorColor, modifier = Modifier.size(20.dp))
                                } else {
                                    Text(
                                        text = ('A' + index).toString(),
                                        style = MaterialTheme.typography.labelLarge,
                                        color = if (isSelected) StreakFire else OnSurfaceVariant
                                    )
                                }
                            }
                            Spacer(Modifier.width(12.dp))
                            Text(
                                text = challenge.options[index],
                                style = MaterialTheme.typography.bodyMedium,
                                color = OnSurface, modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }

                if (uiState.isAnswered) {
                    item {
                        AnimatedVisibility(visible = true, enter = fadeIn(), exit = fadeOut()) {
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(
                                    containerColor = if (uiState.selectedAnswer == challenge.correctAnswerIndex)
                                        SuccessColor.copy(alpha = 0.1f) else ErrorColor.copy(alpha = 0.1f)
                                ),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Text(
                                        text = if (uiState.selectedAnswer == challenge.correctAnswerIndex)
                                            "Correct! +${challenge.xpReward * 2} XP" else "Incorrect",
                                        style = MaterialTheme.typography.titleSmall,
                                        color = if (uiState.selectedAnswer == challenge.correctAnswerIndex)
                                            SuccessColor else ErrorColor,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Spacer(Modifier.height(8.dp))
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

                item {
                    Spacer(Modifier.height(8.dp))
                    Button(
                        onClick = {
                            if (!uiState.isAnswered) viewModel.confirmAnswer()
                            else viewModel.nextChallenge()
                        },
                        modifier = Modifier.fillMaxWidth().height(56.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (!uiState.isAnswered) StreakFire else Secondary
                        ),
                        shape = RoundedCornerShape(16.dp),
                        enabled = uiState.selectedAnswer != null || uiState.isAnswered
                    ) {
                        Text(
                            text = if (!uiState.isAnswered) "Confirm" else "Next",
                            style = MaterialTheme.typography.titleMedium,
                            color = OnPrimary, fontWeight = FontWeight.Bold
                        )
                    }
                }
                item { Spacer(Modifier.height(16.dp)) }
            }
        }
    }
}

@Composable
fun DailyCompleteScreen(score: Int, correct: Int, total: Int, streakDays: Int, onBack: () -> Unit) {
    val accuracy = if (total > 0) (correct.toFloat() / total * 100).toInt() else 0

    Column(
        modifier = Modifier.fillMaxSize().background(Background).padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            Icons.Filled.LocalFireDepartment, null,
            tint = StreakFire, modifier = Modifier.size(80.dp)
        )
        Spacer(Modifier.height(24.dp))
        Text("Daily Challenge Complete!", style = MaterialTheme.typography.headlineMedium,
            color = OnSurface, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
        Spacer(Modifier.height(16.dp))
        Text("Streak: $streakDays days", style = MaterialTheme.typography.titleMedium,
            color = StreakFire)
        Spacer(Modifier.height(32.dp))

        Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = Surface),
            shape = RoundedCornerShape(16.dp)) {
            Column(modifier = Modifier.padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                Text("+$score XP", style = MaterialTheme.typography.displayLarge,
                    color = Primary, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(16.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("$correct/$total", style = MaterialTheme.typography.headlineSmall,
                            color = SuccessColor, fontWeight = FontWeight.Bold)
                        Text("Correct", style = MaterialTheme.typography.labelSmall, color = OnSurfaceVariant)
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("$accuracy%", style = MaterialTheme.typography.headlineSmall,
                            color = Primary, fontWeight = FontWeight.Bold)
                        Text("Accuracy", style = MaterialTheme.typography.labelSmall, color = OnSurfaceVariant)
                    }
                }
            }
        }

        Spacer(Modifier.height(32.dp))
        Button(onClick = onBack, modifier = Modifier.fillMaxWidth().height(56.dp),
            colors = ButtonDefaults.buttonColors(containerColor = StreakFire),
            shape = RoundedCornerShape(16.dp)) {
            Text("Back to Home", style = MaterialTheme.typography.titleMedium,
                color = OnPrimary, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun DailyCompletedScreen(streakDays: Int, onBack: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize().background(Background).padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            Icons.Filled.CheckCircle, null,
            tint = SuccessColor, modifier = Modifier.size(80.dp)
        )
        Spacer(Modifier.height(24.dp))
        Text("Already Completed!", style = MaterialTheme.typography.headlineMedium,
            color = OnSurface, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(8.dp))
        Text("Come back tomorrow for a new challenge.", style = MaterialTheme.typography.bodyMedium,
            color = OnSurfaceVariant, textAlign = TextAlign.Center)
        Spacer(Modifier.height(16.dp))
        Text("🔥 $streakDays day streak", style = MaterialTheme.typography.titleLarge,
            color = StreakFire, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(32.dp))
        Button(onClick = onBack, modifier = Modifier.fillMaxWidth().height(56.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Primary),
            shape = RoundedCornerShape(16.dp)) {
            Text("Back to Home", style = MaterialTheme.typography.titleMedium,
                color = OnPrimary, fontWeight = FontWeight.Bold)
        }
    }
}
