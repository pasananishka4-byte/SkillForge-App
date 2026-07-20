package com.skillforge.app.ui.screens.games

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.skillforge.app.ui.AppStorage
import com.skillforge.app.ui.theme.*
import kotlinx.coroutines.delay
import kotlin.random.Random

private val hangmanWords = listOf(
    "kotlin", "android", "compose", "skill", "forge", "puzzle", "logic",
    "memory", "focus", "brain", "train", "speed", "smart", "learn",
    "challenge", "master", "growth", "coding", "debug", "build"
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GameHangmanScreen(difficulty: String, navController: NavHostController) {
    val maxWrong = 6
    val multiplier = when(difficulty) { "hard" -> 3; "medium" -> 2; else -> 1 }

    var word by remember { mutableStateOf("") }
    var guessedLetters by remember { mutableStateOf(setOf<Char>()) }
    var wrongCount by remember { mutableIntStateOf(0) }
    var score by remember { mutableIntStateOf(0) }
    var round by remember { mutableIntStateOf(1) }
    var gameOver by remember { mutableStateOf(false) }
    var message by remember { mutableStateOf("") }

    val displayWord = word.map { if (it in guessedLetters) it else '_' }.joinToString(" ")
    val isWon = word.isNotEmpty() && word.all { it in guessedLetters }

    LaunchedEffect(Unit) { word = hangmanWords.random(); guessedLetters = emptySet(); wrongCount = 0 }

    fun guessLetter(c: Char) {
        if (c in guessedLetters || gameOver || isWon) return
        guessedLetters = guessedLetters + c
        if (c !in word) {
            wrongCount++
            if (wrongCount >= maxWrong) {
                message = "Game Over! The word was: $word"
                gameOver = true
            }
        }
        if (word.all { it in guessedLetters } && !gameOver) {
            score += word.length * 10 * multiplier
            message = "Correct! +${word.length * 10 * multiplier} XP"
        }
    }

    LaunchedEffect(isWon) {
        if (isWon && !gameOver) {
            delay(1500)
            round++
            word = hangmanWords.random()
            guessedLetters = emptySet()
            wrongCount = 0
        }
    }

    if (gameOver) {
        val xp = score
        LaunchedEffect(Unit) { AppStorage.storage.saveGameResult("hangman", score, xp) }
        GameOverScreen(
            title = message,
            stats = listOf("Rounds: ${round - 1}", "Score: $score", "Difficulty: ${difficulty.replaceFirstChar{it.uppercase()}}"),
            score = score, xpEarned = xp,
            onPlayAgain = { navController.popBackStack(); navController.navigate("game_hangman?difficulty=$difficulty") },
            onBack = { navController.popBackStack() }
        )
        return
    }

    Column(modifier = Modifier.fillMaxSize()) {
        TopAppBar(
            title = { Text("Hangman") },
            navigationIcon = { IconButton(onClick = { navController.popBackStack() }) { Icon(Icons.Filled.ArrowBack, "Back") } },
            colors = TopAppBarDefaults.topAppBarColors(containerColor = Surface, titleContentColor = OnBackground)
        )
        Text(text = "Round $round  |  Score: $score", color = Primary, modifier = Modifier.padding(16.dp))

        // Hangman visual
        val hangmanParts = listOf("○", "|", "/", "\\", "/", "\\")
        Column(modifier = Modifier.fillMaxWidth().padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Text(text = "______", fontSize = 24.sp, color = OnSurfaceVariant)
            Text(text = if (wrongCount >= 1) hangmanParts[0] else " ", fontSize = 24.sp, color = ErrorColor)
            Text(text = if (wrongCount >= 2) hangmanParts[1] else " ", fontSize = 24.sp, color = ErrorColor)
            Text(
                text = "${if (wrongCount >= 3) hangmanParts[2] else " "} ${if (wrongCount >= 4) hangmanParts[3] else " "}",
                fontSize = 24.sp, color = ErrorColor
            )
            Text(
                text = "${if (wrongCount >= 5) hangmanParts[4] else " "} ${if (wrongCount >= 6) hangmanParts[5] else " "}",
                fontSize = 24.sp, color = ErrorColor
            )
            Spacer(modifier = Modifier.height(8.dp))

            Text(text = displayWord, fontSize = 36.sp, color = OnBackground, fontWeight = FontWeight.Bold, letterSpacing = 8.sp)
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = "Wrong: ${wrongCount}/$maxWrong", color = ErrorColor, fontSize = 14.sp)
            Spacer(modifier = Modifier.height(16.dp))

            // Letter buttons
            val alphabet = "abcdefghijklmnopqrstuvwxyz"
            alphabet.chunked(7).forEach { row ->
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp), modifier = Modifier.padding(vertical = 3.dp)) {
                    row.forEach { letter ->
                        val used = letter in guessedLetters
                        Box(
                            modifier = Modifier.size(36.dp)
                                .clip(RoundedCornerShape(6.dp))
                                .background(if (used) SurfaceVariant.copy(alpha = 0.5f) else Primary)
                                .clickable(enabled = !used) { guessLetter(letter) },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = letter.uppercase(),
                                color = if (used) OnSurfaceVariant.copy(alpha = 0.5f) else OnPrimary,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    }
}
