package com.skillforge.app.ui.screens.games

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.navigation.NavHostController
import com.skillforge.app.ui.AppStorage
import com.skillforge.app.ui.theme.*
import kotlinx.coroutines.delay
import kotlin.random.Random

private data class MathQuestion(
    val questionText: String,
    val correctAnswer: Int,
    val options: List<Int>
)

@Composable
fun GameMathDuelScreen(difficulty: String, navController: NavHostController) {
    val (totalQuestions, timePerQuestion, difficultyMultiplier) = when (difficulty.lowercase()) {
        "easy" -> Triple(15, 15, 1)
        "medium" -> Triple(20, 10, 2)
        "hard" -> Triple(25, 7, 3)
        else -> Triple(15, 15, 1)
    }

    fun generateQuestion(): MathQuestion {
        when (difficulty.lowercase()) {
            "easy" -> {
                val a = Random.nextInt(1, 21)
                val b = Random.nextInt(1, 21)
                val isAdd = Random.nextBoolean()
                val (text, answer) = if (isAdd) {
                    Pair("$a + $b", a + b)
                } else {
                    val (x, y) = if (a >= b) Pair(a, b) else Pair(b, a)
                    Pair("$x - $y", x - y)
                }
                val opts = mutableSetOf(answer)
                while (opts.size < 4) {
                    val delta = Random.nextInt(-5, 6)
                    if (delta != 0) opts.add(answer + delta)
                }
                return MathQuestion(text, answer, opts.shuffled())
            }
            "medium" -> {
                val ops = listOf("*", "/")
                val op = ops.random()
                return if (op == "*") {
                    val a = Random.nextInt(2, 13)
                    val b = Random.nextInt(2, 13)
                    val answer = a * b
                    val opts = mutableSetOf(answer)
                    while (opts.size < 4) {
                        val delta = Random.nextInt(-4, 5)
                        if (delta != 0) opts.add(answer + delta)
                    }
                    MathQuestion("$a * $b", answer, opts.shuffled())
                } else {
                    val b = Random.nextInt(2, 13)
                    val answer = Random.nextInt(2, 13)
                    val a = b * answer
                    val opts = mutableSetOf(answer)
                    while (opts.size < 4) {
                        val delta = Random.nextInt(-4, 5)
                        if (delta != 0) opts.add(answer + delta)
                    }
                    MathQuestion("$a / $b", answer, opts.shuffled())
                }
            }
            else -> {
                val opType = Random.nextInt(3)
                return when (opType) {
                    0 -> {
                        val a = Random.nextInt(10, 100)
                        val b = Random.nextInt(10, 100)
                        val answer = a + b
                        val opts = mutableSetOf(answer)
                        while (opts.size < 4) {
                            val delta = Random.nextInt(-10, 11)
                            if (delta != 0) opts.add(answer + delta)
                        }
                        MathQuestion("$a + $b", answer, opts.shuffled())
                    }
                    1 -> {
                        val b = Random.nextInt(2, 15)
                        val answer = Random.nextInt(10, 50)
                        val a = b * answer
                        val opts = mutableSetOf(answer)
                        while (opts.size < 4) {
                            val delta = Random.nextInt(-6, 7)
                            if (delta != 0) opts.add(answer + delta)
                        }
                        MathQuestion("$a / $b", answer, opts.shuffled())
                    }
                    else -> {
                        val a = Random.nextInt(2, 15)
                        val b = Random.nextInt(2, 15)
                        val c = Random.nextInt(1, 10)
                        val answer = a * b + c
                        val opts = mutableSetOf(answer)
                        while (opts.size < 4) {
                            val delta = Random.nextInt(-8, 9)
                            if (delta != 0) opts.add(answer + delta)
                        }
                        MathQuestion("$a × $b + $c", answer, opts.shuffled())
                    }
                }
            }
        }
    }

    var currentQuestion by remember { mutableStateOf(generateQuestion()) }
    var questionNumber by remember { mutableIntStateOf(1) }
    var correctCount by remember { mutableIntStateOf(0) }
    var streak by remember { mutableIntStateOf(0) }
    var bestStreak by remember { mutableIntStateOf(0) }
    var timeLeft by remember { mutableIntStateOf(timePerQuestion) }
    var score by remember { mutableIntStateOf(0) }
    var xpEarned by remember { mutableIntStateOf(0) }
    var gameComplete by remember { mutableStateOf(false) }
    var answeredCurrent by remember { mutableStateOf(false) }
    var selectedAnswer by remember { mutableIntStateOf(-1) }
    var totalTimeUsed by remember { mutableIntStateOf(0) }

    LaunchedEffect(gameComplete, questionNumber, answeredCurrent) {
        if (!gameComplete && !answeredCurrent) {
            while (timeLeft > 0) {
                delay(1000L)
                timeLeft--
                totalTimeUsed++
                if (timeLeft <= 0 && !answeredCurrent) {
                    answeredCurrent = true
                    streak = 0
                    delay(1200L)
                    if (questionNumber >= totalQuestions) {
                        val base = correctCount * 10
                        val timeBonus = maxOf(0, (totalQuestions * timePerQuestion - totalTimeUsed))
                        score = (base + timeBonus / 5) * difficultyMultiplier
                        xpEarned = score / 2
                        gameComplete = true
                        AppStorage.storage.saveGameResult("Math Duel", score, xpEarned)
                    } else {
                        questionNumber++
                        currentQuestion = generateQuestion()
                        timeLeft = timePerQuestion
                        answeredCurrent = false
                    }
                }
            }
        }
    }

    if (gameComplete) {
        val accuracy = if (totalQuestions > 0) correctCount * 100 / totalQuestions else 0
        GameCompletionOverlay(
            gameName = "Math Duel",
            score = score,
            xpEarned = xpEarned,
            message = when {
                accuracy >= 90 -> "Mathematical genius!"
                accuracy >= 70 -> "Great work!"
                accuracy >= 50 -> "Not bad!"
                else -> "Keep practicing!"
            },
            detail = "Accuracy: $accuracy% | Best Streak: $bestStreak",
            onPlayAgain = {
                questionNumber = 1
                correctCount = 0
                streak = 0
                bestStreak = 0
                score = 0
                xpEarned = 0
                totalTimeUsed = 0
                gameComplete = false
                answeredCurrent = false
                currentQuestion = generateQuestion()
                timeLeft = timePerQuestion
            },
            onBack = { navController.popBackStack() }
        )
        return
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Math Duel", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Primary)
        Text(difficulty.replaceFirstChar { it.uppercase() }, fontSize = 14.sp, color = OnSurfaceVariant)
        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("Q $questionNumber / $totalQuestions", color = OnSurface, fontSize = 14.sp)
            Text(
                text = "${timeLeft}s",
                color = if (timeLeft > 5) OnSurface else ErrorColor,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
        }
        Spacer(modifier = Modifier.height(4.dp))

        LinearProgressIndicator(
            progress = timeLeft.toFloat() / timePerQuestion,
            modifier = Modifier.fillMaxWidth().height(6.dp).clip(RoundedCornerShape(3.dp)),
            color = if (timeLeft > 5) Primary else ErrorColor,
            trackColor = SurfaceVariant,
        )
        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("Correct: $correctCount", color = SuccessColor, fontSize = 13.sp)
            Text(
                text = "Streak: $streak 🔥",
                color = if (streak >= 3) Primary else OnSurfaceVariant,
                fontSize = 13.sp,
                fontWeight = if (streak >= 3) FontWeight.Bold else FontWeight.Normal
            )
        }
        Spacer(modifier = Modifier.height(32.dp))

        Text(
            text = currentQuestion.questionText,
            fontSize = 40.sp,
            fontWeight = FontWeight.Bold,
            color = OnSurface,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(32.dp))

        Column(
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            currentQuestion.options.chunked(2).forEach { row ->
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    row.forEach { option ->
                        val isSelected = selectedAnswer == option && answeredCurrent
                        val isCorrect = option == currentQuestion.correctAnswer && answeredCurrent
                        val bgColor = when {
                            isCorrect -> SuccessColor
                            isSelected && !isCorrect -> ErrorColor
                            else -> Surface
                        }
                        val borderColor = when {
                            isCorrect -> SuccessColor
                            isSelected && !isCorrect -> ErrorColor
                            else -> OnSurfaceVariant.copy(alpha = 0.3f)
                        }

                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(72.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(bgColor)
                                .border(2.dp, borderColor, RoundedCornerShape(12.dp))
                                .then(
                                    if (!answeredCurrent) Modifier.clickable {
                                        selectedAnswer = option
                                        answeredCurrent = true
                                        if (option == currentQuestion.correctAnswer) {
                                            correctCount++
                                            streak++
                                            if (streak > bestStreak) bestStreak = streak
                                        } else {
                                            streak = 0
                                        }
                                    } else Modifier
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "$option",
                                fontSize = 28.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isCorrect || isSelected && !isCorrect) Color.White else OnSurface
                            )
                        }
                    }
                }
            }
        }

        if (answeredCurrent) {
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = if (selectedAnswer == currentQuestion.correctAnswer) "Correct!" else "Wrong! Answer: ${currentQuestion.correctAnswer}",
                fontSize = 16.sp,
                color = if (selectedAnswer == currentQuestion.correctAnswer) SuccessColor else ErrorColor,
                fontWeight = FontWeight.Bold
            )
            LaunchedEffect(answeredCurrent) {
                delay(1200L)
                if (questionNumber >= totalQuestions) {
                    val base = correctCount * 10
                    val timeBonus = maxOf(0, (totalQuestions * timePerQuestion - totalTimeUsed))
                    score = (base + timeBonus / 5) * difficultyMultiplier
                    xpEarned = score / 2
                    gameComplete = true
                    AppStorage.storage.saveGameResult("Math Duel", score, xpEarned)
                } else {
                    questionNumber++
                    currentQuestion = generateQuestion()
                    timeLeft = timePerQuestion
                    answeredCurrent = false
                }
            }
        }

        Spacer(modifier = Modifier.weight(1f))
        Text(
            text = "Score: $score",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = OnSurface
        )
    }
}

@Composable
private fun GameCompletionOverlay(
    gameName: String,
    score: Int,
    xpEarned: Int,
    message: String,
    detail: String,
    onPlayAgain: () -> Unit,
    onBack: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = if (score > 0) "🎉" else "😢", fontSize = 64.sp)
        Spacer(modifier = Modifier.height(16.dp))
        Text(text = message, fontSize = 28.sp, fontWeight = FontWeight.Bold, color = if (score > 0) SuccessColor else ErrorColor)
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = detail, fontSize = 14.sp, color = OnSurfaceVariant, textAlign = TextAlign.Center)
        Spacer(modifier = Modifier.height(24.dp))

        Card(
            colors = CardDefaults.cardColors(containerColor = Surface),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("Score", fontSize = 14.sp, color = OnSurfaceVariant)
                Text("$score", fontSize = 36.sp, fontWeight = FontWeight.Bold, color = Primary)
                Spacer(modifier = Modifier.height(8.dp))
                Text("XP Earned: +$xpEarned", fontSize = 16.sp, color = SuccessColor)
            }
        }
        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = onPlayAgain,
            colors = ButtonDefaults.buttonColors(containerColor = Primary),
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp)
        ) { Text("Play Again", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.Black) }
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedButton(
            onClick = onBack,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp)
        ) { Text("Back to Games", fontSize = 16.sp, color = OnSurface) }
    }
}
