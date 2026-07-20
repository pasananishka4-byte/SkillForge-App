package com.skillforge.app.ui.screens.games

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.skillforge.app.data.SeedData
import com.skillforge.app.ui.AppStorage
import com.skillforge.app.ui.theme.*
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GameSpeedRoundScreen(difficulty: String, navController: NavHostController) {
    val allChallenges = remember { SeedData.challenges.shuffled() }
    val maxQuestions = when(difficulty) { "hard" -> 25; "medium" -> 20; else -> 15 }
    val timeLimit = when(difficulty) { "hard" -> 30; "medium" -> 45; else -> 60 }
    val multiplier = when(difficulty) { "hard" -> 3; "medium" -> 2; else -> 1 }

    var currentIdx by remember { mutableIntStateOf(0) }
    var score by remember { mutableIntStateOf(0) }
    var correctCount by remember { mutableIntStateOf(0) }
    var streak by remember { mutableIntStateOf(0) }
    var bestStreak by remember { mutableIntStateOf(0) }
    var timeLeft by remember { mutableIntStateOf(timeLimit) }
    var answered by remember { mutableStateOf(false) }
    var selectedAnswer by remember { mutableIntStateOf(-1) }
    var gameOver by remember { mutableStateOf(false) }
    val questions = remember { allChallenges.take(maxQuestions) }

    LaunchedEffect(Unit) {
        while (timeLeft > 0 && currentIdx < questions.size) {
            delay(1000)
            timeLeft--
            if (timeLeft == 0) { gameOver = true }
        }
    }

    LaunchedEffect(answered) {
        if (answered) {
            delay(800)
            answered = false
            selectedAnswer = -1
            currentIdx++
            if (currentIdx >= questions.size) { gameOver = true }
        }
    }

    if (gameOver) {
        val xp = (score * multiplier)
        LaunchedEffect(Unit) {
            AppStorage.storage.saveGameResult("speed_round", score, xp)
        }
        GameOverScreen(
            title = "Speed Round Over!",
            stats = listOf("Correct: $correctCount/${questions.size}", "Best Streak: $bestStreak", "Time: ${timeLimit}s"),
            score = score,
            xpEarned = xp,
            onPlayAgain = { navController.popBackStack(); navController.navigate("game_speed_round?difficulty=$difficulty") },
            onBack = { navController.popBackStack() }
        )
        return
    }

    if (questions.isEmpty()) { navController.popBackStack(); return }

    val q = questions[currentIdx]

    Column(modifier = Modifier.fillMaxSize()) {
        TopAppBar(
            title = { Text("Speed Round") },
            navigationIcon = { IconButton(onClick = { navController.popBackStack() }) { Icon(Icons.Filled.ArrowBack, "Back") } },
            colors = TopAppBarDefaults.topAppBarColors(containerColor = Surface, titleContentColor = OnBackground)
        )

        Row(modifier = Modifier.fillMaxWidth().padding(16.dp), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(text = "${currentIdx + 1}/${questions.size}", color = OnSurfaceVariant)
            Text(text = "⏱ ${timeLeft}s", color = if (timeLeft < 10) ErrorColor else Primary, fontWeight = FontWeight.Bold)
            Text(text = "🔥 $streak", color = StreakFire)
        }

        LinearProgressIndicator(
            progress = timeLeft.toFloat() / timeLimit,
            modifier = Modifier.fillMaxWidth().height(4.dp).padding(horizontal = 16.dp),
            color = if (timeLeft < 10) ErrorColor else Primary,
            trackColor = SurfaceVariant
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text(text = q.question, color = OnBackground, fontSize = 18.sp, modifier = Modifier.padding(horizontal = 16.dp))
        Spacer(modifier = Modifier.height(24.dp))

        q.options.forEachIndexed { index, option ->
            Card(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 4.dp).clickable(enabled = !answered) {
                    selectedAnswer = index
                    answered = true
                    if (index == q.correctAnswerIndex) {
                        streak++
                        if (streak > bestStreak) bestStreak = streak
                        score += 10 + if (streak >= 3) 2 else 0
                        correctCount++
                    } else {
                        streak = 0
                    }
                },
                colors = CardDefaults.cardColors(
                    containerColor = when {
                        answered && index == q.correctAnswerIndex -> SuccessColor
                        answered && index == selectedAnswer -> ErrorColor
                        else -> SurfaceVariant
                    }
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(text = option, color = OnBackground, modifier = Modifier.padding(16.dp).fillMaxWidth())
            }
        }

        Spacer(modifier = Modifier.weight(1f))
        Text(text = "Score: $score", color = Primary, modifier = Modifier.padding(16.dp), fontWeight = FontWeight.Bold)
    }
}
