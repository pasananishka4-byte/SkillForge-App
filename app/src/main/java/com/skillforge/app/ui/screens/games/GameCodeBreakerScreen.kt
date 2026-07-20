package com.skillforge.app.ui.screens.games

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
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

@Composable
fun GameCodeBreakerScreen(difficulty: String, navController: NavHostController) {
    val (colorsAvailable, maxAttempts, difficultyMultiplier) = when (difficulty.lowercase()) {
        "easy" -> Triple(6, 10, 1)
        "medium" -> Triple(8, 8, 2)
        "hard" -> Triple(10, 6, 3)
        else -> Triple(6, 10, 1)
    }

    val pegColors = listOf(
        Color(0xFFE53935),
        Color(0xFF43A047),
        Color(0xFF1E88E5),
        Color(0xFFFDD835),
        Color(0xFF8E24AA),
        Color(0xFFFF6D00),
        Color(0xFF00ACC1),
        Color(0xFFD81B60),
        Color(0xFF5C6BC0),
        Color(0xFF26A69A)
    )
    val displayColors = pegColors.take(colorsAvailable)

    var secretCode by remember { mutableStateOf(emptyList<Int>()) }
    var guesses by remember { mutableStateOf(emptyList<Pair<List<Int>, List<Int>>>()) }
    var currentGuess by remember { mutableStateOf(mutableListOf<Int>()) }
    var remainingAttempts by remember { mutableIntStateOf(maxAttempts) }
    var gameWon by remember { mutableStateOf(false) }
    var gameLost by remember { mutableStateOf(false) }
    var gameComplete by remember { mutableStateOf(false) }
    var score by remember { mutableIntStateOf(0) }
    var xpEarned by remember { mutableIntStateOf(0) }
    var showReveal by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        secretCode = List(4) { Random.nextInt(colorsAvailable) }
    }

    fun calculateFeedback(guess: List<Int>, code: List<Int>): List<Int> {
        val feedback = MutableList(4) { 0 }
        val codeUsed = BooleanArray(4) { false }
        val guessUsed = BooleanArray(4) { false }

        for (i in 0 until 4) {
            if (guess[i] == code[i]) {
                feedback[i] = 2
                codeUsed[i] = true
                guessUsed[i] = true
            }
        }
        for (i in 0 until 4) {
            if (!guessUsed[i]) {
                for (j in 0 until 4) {
                    if (!codeUsed[j] && guess[i] == code[j]) {
                        feedback[i] = 1
                        codeUsed[j] = true
                        break
                    }
                }
            }
        }
        return feedback
    }

    fun submitGuess() {
        if (currentGuess.size != 4 || gameComplete) return
        val feedback = calculateFeedback(currentGuess.toList(), secretCode)
        guesses = guesses + Pair(currentGuess.toList(), feedback)
        remainingAttempts--

        if (feedback.all { it == 2 }) {
            val base = remainingAttempts * 10
            score = base * difficultyMultiplier
            xpEarned = score / 2
            gameWon = true
            gameComplete = true
            AppStorage.storage.saveGameResult("Code Breaker", score, xpEarned)
        } else if (remainingAttempts <= 0) {
            gameLost = true
            gameComplete = true
            score = 0
            xpEarned = 0
            showReveal = true
            AppStorage.storage.saveGameResult("Code Breaker", score, xpEarned)
        }
        currentGuess = mutableListOf()
    }

    if (gameComplete) {
        GameCompletionOverlay(
            gameName = "Code Breaker",
            score = score,
            xpEarned = xpEarned,
            message = if (gameWon) "Cracked the code!" else "Out of attempts!",
            detail = if (gameWon) "Solved with $remainingAttempts attempts remaining" else "The code was: ${secretCode.joinToString(" ")}",
            onPlayAgain = {
                secretCode = List(4) { Random.nextInt(colorsAvailable) }
                guesses = emptyList()
                currentGuess = mutableListOf()
                remainingAttempts = maxAttempts
                gameWon = false
                gameLost = false
                gameComplete = false
                score = 0
                xpEarned = 0
                showReveal = false
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
        Text(
            text = "Code Breaker",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = Primary
        )
        Text(
            text = difficulty.replaceFirstChar { it.uppercase() },
            fontSize = 14.sp,
            color = OnSurfaceVariant
        )
        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("Attempts: $remainingAttempts / $maxAttempts", color = OnSurface, fontSize = 14.sp)
            Text(
                text = if (currentGuess.size == 4) "Tap Submit" else "Pick 4 colors",
                color = if (currentGuess.size == 4) SuccessColor else OnSurfaceVariant,
                fontSize = 14.sp
            )
        }
        Spacer(modifier = Modifier.height(8.dp))

        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            currentGuess.forEachIndexed { index, colorIdx ->
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(displayColors[colorIdx])
                        .border(2.dp, OnSurfaceVariant, CircleShape)
                )
            }
            repeat(4 - currentGuess.size) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(SurfaceVariant)
                        .border(2.dp, OnSurfaceVariant.copy(alpha = 0.5f), CircleShape)
                )
            }
        }
        Spacer(modifier = Modifier.height(12.dp))

        Row(
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            displayColors.forEachIndexed { index, color ->
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                        .background(color)
                        .border(2.dp, if (currentGuess.size < 4) Primary else Primary.copy(alpha = 0.3f), CircleShape)
                        .then(
                            if (currentGuess.size < 4) Modifier.clickable {
                                if (currentGuess.size < 4) currentGuess = (currentGuess + index).toMutableList()
                            } else Modifier
                        )
                )
            }
        }
        Spacer(modifier = Modifier.height(8.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(
                onClick = { if (currentGuess.isNotEmpty()) currentGuess = currentGuess.dropLast(1).toMutableList() },
                colors = ButtonDefaults.buttonColors(containerColor = ErrorColor.copy(alpha = 0.7f)),
                enabled = currentGuess.isNotEmpty() && !gameComplete,
                shape = RoundedCornerShape(8.dp),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
            ) { Text("Undo", fontSize = 13.sp) }

            Button(
                onClick = { currentGuess = mutableListOf() },
                colors = ButtonDefaults.buttonColors(containerColor = WarningColor.copy(alpha = 0.7f)),
                enabled = currentGuess.isNotEmpty() && !gameComplete,
                shape = RoundedCornerShape(8.dp),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
            ) { Text("Clear", fontSize = 13.sp) }

            Button(
                onClick = { submitGuess() },
                colors = ButtonDefaults.buttonColors(containerColor = SuccessColor),
                enabled = currentGuess.size == 4 && !gameComplete,
                shape = RoundedCornerShape(8.dp),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
            ) { Text("Submit", fontSize = 13.sp, color = Color.Black, fontWeight = FontWeight.Bold) }
        }
        Spacer(modifier = Modifier.height(12.dp))

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(4.dp),
            modifier = Modifier.weight(1f)
        ) {
            itemsIndexed(guesses.reversed()) { index, (guess, feedback) ->
                val attemptNum = guesses.size - index
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(Surface)
                        .padding(8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "#$attemptNum",
                        color = OnSurfaceVariant,
                        fontSize = 12.sp,
                        modifier = Modifier.width(28.dp)
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        guess.forEach { colorIdx ->
                            Box(
                                modifier = Modifier
                                    .size(28.dp)
                                    .clip(CircleShape)
                                    .background(displayColors[colorIdx])
                            )
                        }
                    }
                    Row(horizontalArrangement = Arrangement.spacedBy(3.dp)) {
                        val sortedFeedback = feedback.sortedByDescending { it }
                        sortedFeedback.forEach { f ->
                            Box(
                                modifier = Modifier
                                    .size(12.dp)
                                    .clip(CircleShape)
                                    .background(if (f == 2) SuccessColor else if (f == 1) Color.White else Color.Gray.copy(alpha = 0.3f))
                            )
                        }
                    }
                }
            }
        }

        if (gameLost) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Code was revealed above!",
                color = ErrorColor,
                fontSize = 14.sp,
                textAlign = TextAlign.Center
            )
        }
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
        Text(
            text = if (score > 0) "🎉" else "😢",
            fontSize = 64.sp
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = message,
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = if (score > 0) SuccessColor else ErrorColor
        )
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
