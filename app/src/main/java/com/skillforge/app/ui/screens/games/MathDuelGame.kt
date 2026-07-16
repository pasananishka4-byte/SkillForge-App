package com.skillforge.app.ui.screens.games

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
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
import com.skillforge.app.ui.theme.*
import kotlinx.coroutines.delay
import kotlin.random.Random

private data class MathProblem(
    val question: String,
    val answer: Int,
    val options: List<Int>,
    val correctIndex: Int
)

private fun generateProblems(difficulty: String, count: Int): List<MathProblem> {
    val problems = mutableListOf<MathProblem>()
    val random = Random.Default

    for (i in 0 until count) {
        val a: Int
        val b: Int
        val op: String
        val ans: Int
        when (difficulty) {
            "Easy" -> {
                a = random.nextInt(1, 21)
                b = random.nextInt(1, 21)
                op = listOf("+", "-").random()
                ans = if (op == "+") a + b else a - b
            }
            "Hard" -> {
                a = random.nextInt(10, 51)
                b = random.nextInt(2, 13)
                op = listOf("+", "-", "x").random()
                ans = when (op) { "x" -> a * b; "-" -> a - b; else -> a + b }
            }
            else -> {
                a = random.nextInt(2, 31)
                b = random.nextInt(2, 13)
                op = listOf("+", "-", "x").random()
                ans = when (op) { "x" -> a * b; "-" -> a - b; else -> a + b }
            }
        }

        val wrongAnswers = mutableSetOf<Int>()
        while (wrongAnswers.size < 3) {
            val wrong = ans + random.nextInt(-10, 11).coerceAtLeast(-10).coerceAtMost(10)
            if (wrong != ans && wrong > 0) wrongAnswers.add(wrong)
        }
        val options = (wrongAnswers.toList() + ans).shuffled()
        val correctIdx = options.indexOf(ans)
        problems.add(MathProblem("$a $op $b", ans, options, correctIdx))
    }
    return problems
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MathDuelGame(
    onBack: () -> Unit,
    difficulty: String = "Normal",
    onGameComplete: (xpEarned: Int, score: Int) -> Unit = { _, _ -> }
) {
    val problemsCount = when (difficulty) { "Easy" -> 8; "Hard" -> 20; else -> 12 }
    val timerDuration = when (difficulty) { "Easy" -> 60; "Hard" -> 30; else -> 45 }
    val problems = remember { generateProblems(difficulty, problemsCount) }

    var currentIndex by remember { mutableIntStateOf(0) }
    var score by remember { mutableIntStateOf(0) }
    var streak by remember { mutableIntStateOf(0) }
    var correctCount by remember { mutableIntStateOf(0) }
    var isComplete by remember { mutableStateOf(false) }
    var hasReported by remember { mutableStateOf(false) }
    var timeLeft by remember { mutableIntStateOf(timerDuration) }
    var selectedAnswer by remember { mutableIntStateOf(-1) }
    var showResult by remember { mutableStateOf(false) }

    LaunchedEffect(isComplete) {
        if (!isComplete) {
            while (timeLeft > 0) {
                delay(1000)
                timeLeft--
            }
            isComplete = true
            if (!hasReported) {
                hasReported = true
                val base = score
                onGameComplete(base, correctCount)
            }
        }
    }

    Column(
        modifier = Modifier.fillMaxSize().background(Background).padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onBack) { Icon(Icons.Filled.ArrowBack, "Back", tint = OnSurface) }
            Column(modifier = Modifier.weight(1f)) {
                Text("Math Duel", style = MaterialTheme.typography.titleLarge, color = MetaLearningColor, fontWeight = FontWeight.Bold)
                Text("$difficulty - Solve as many as you can!", style = MaterialTheme.typography.bodySmall, color = OnSurfaceVariant)
            }
        }

        // Timer
        LinearProgressIndicator(
            progress = timeLeft.toFloat() / timerDuration,
            modifier = Modifier.fillMaxWidth().height(8.dp).padding(vertical = 8.dp).clip(RoundedCornerShape(4.dp)),
            color = if (timeLeft > timerDuration / 3) MetaLearningColor else ErrorColor,
            trackColor = SurfaceVariant,
        )

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text("${timeLeft}s", style = MaterialTheme.typography.labelLarge, color = if (timeLeft > timerDuration / 3) OnSurface else ErrorColor)
            Text("Score: $score", style = MaterialTheme.typography.labelLarge, color = Primary)
            Text("Streak: $streak", style = MaterialTheme.typography.labelLarge, color = if (streak >= 3) StreakFire else OnSurfaceVariant)
            Text("${currentIndex + 1}/$problemsCount", style = MaterialTheme.typography.labelLarge, color = OnSurfaceVariant)
        }

        if (isComplete) {
            Spacer(Modifier.height(32.dp))
            Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = Surface), shape = RoundedCornerShape(16.dp)) {
                Column(modifier = Modifier.padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Filled.EmojiEvents, null, tint = MetaLearningColor, modifier = Modifier.size(56.dp))
                    Spacer(Modifier.height(8.dp))
                    Text("Time's Up!", style = MaterialTheme.typography.headlineSmall, color = OnSurface, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(16.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("$score", style = MaterialTheme.typography.headlineMedium, color = Primary, fontWeight = FontWeight.Bold)
                            Text("Score", style = MaterialTheme.typography.labelSmall, color = OnSurfaceVariant)
                        }
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("$correctCount/$problemsCount", style = MaterialTheme.typography.headlineMedium, color = SuccessColor, fontWeight = FontWeight.Bold)
                            Text("Correct", style = MaterialTheme.typography.labelSmall, color = OnSurfaceVariant)
                        }
                    }
                    Spacer(Modifier.height(12.dp))
                    Text("+$score XP", style = MaterialTheme.typography.titleLarge, color = Primary, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(16.dp))
                    Button(onClick = onBack, modifier = Modifier.fillMaxWidth(), colors = ButtonDefaults.buttonColors(containerColor = MetaLearningColor), shape = RoundedCornerShape(12.dp)) {
                        Text("Done", color = OnPrimary, fontWeight = FontWeight.Bold)
                    }
                }
            }
            return
        }

        Spacer(Modifier.height(24.dp))

        // Problem display
        if (currentIndex < problems.size) {
            val problem = problems[currentIndex]
            Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = Surface), shape = RoundedCornerShape(16.dp)) {
                Column(modifier = Modifier.padding(32.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Solve:", style = MaterialTheme.typography.bodyMedium, color = OnSurfaceVariant)
                    Spacer(Modifier.height(16.dp))
                    Text(problem.question, style = MaterialTheme.typography.displaySmall, color = MetaLearningColor, fontWeight = FontWeight.Bold)
                    Text("= ?", style = MaterialTheme.typography.headlineMedium, color = OnSurfaceVariant)
                }
            }

            Spacer(Modifier.height(24.dp))

            // Answer options (2x2 grid)
            problem.options.chunked(2).forEach { row ->
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    row.forEachIndexed { _, option ->
                        val globalIdx = problem.options.indexOf(option)
                        val isCorrectOpt = option == problem.answer
                        val containerColor = when {
                            showResult && isCorrectOpt -> SuccessColor.copy(alpha = 0.2f)
                            showResult && globalIdx == selectedAnswer && !isCorrectOpt -> ErrorColor.copy(alpha = 0.2f)
                            globalIdx == selectedAnswer -> MetaLearningColor.copy(alpha = 0.15f)
                            else -> Surface
                        }

                        Card(
                            modifier = Modifier.weight(1f).padding(vertical = 4.dp)
                                .clickable(enabled = !showResult) {
                                    selectedAnswer = globalIdx
                                    showResult = true
                                    if (isCorrectOpt) {
                                        streak++
                                        val streakBonus = if (streak >= 5) 5 else if (streak >= 3) 3 else 1
                                        score += 10 * streakBonus
                                        correctCount++
                                    } else {
                                        streak = 0
                                    }
                                },
                            colors = CardDefaults.cardColors(containerColor = containerColor),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Box(modifier = Modifier.fillMaxWidth().padding(16.dp), contentAlignment = Alignment.Center) {
                                Text(
                                    "$option",
                                    style = MaterialTheme.typography.headlineSmall,
                                    color = OnSurface,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }

            LaunchedEffect(showResult) {
                if (showResult) {
                    delay(800)
                    currentIndex++
                    selectedAnswer = -1
                    showResult = false
                    if (currentIndex >= problems.size) {
                        isComplete = true
                        if (!hasReported) {
                            hasReported = true
                            onGameComplete(score, correctCount)
                        }
                    }
                }
            }
        }
    }
}
