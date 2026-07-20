package com.skillforge.app.ui.screens.games

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.skillforge.app.ui.AppStorage
import com.skillforge.app.ui.theme.*
import kotlinx.coroutines.delay

private val cardEmojis = listOf("🐱","🐶","🐼","🦊","🐸","🐵","🦋","🌻","🍕","🎸","🚀","⭐","🌙","🌈","🎪","🎭","🎨","🍎","🎈","🎯")

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GameMemoryMatchScreen(difficulty: String, navController: NavHostController) {
    val cols = when(difficulty) { "hard" -> 6; "medium" -> 4; else -> 4 }
    val rows = when(difficulty) { "hard" -> 4; "medium" -> 4; else -> 3 }
    val numPairs = (cols * rows) / 2
    val multiplier = when(difficulty) { "hard" -> 3; "medium" -> 2; else -> 1 }

    var cards by remember { mutableStateOf(listOf<CardData>()) }
    var flippedIndices by remember { mutableStateOf(setOf<Int>()) }
    var matchedIndices by remember { mutableStateOf(setOf<Int>()) }
    var moves by remember { mutableIntStateOf(0) }
    var gameOver by remember { mutableStateOf(false) }
    var gameStarted by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        val selectedEmojis = cardEmojis.shuffled().take(numPairs)
        val pairs = (selectedEmojis + selectedEmojis).shuffled()
        cards = pairs.mapIndexed { index, emoji -> CardData(index, emoji) }
        gameStarted = true
    }

    fun checkMatch(idx1: Int, idx2: Int) {
        moves++
        if (cards[idx1].emoji == cards[idx2].emoji) {
            matchedIndices = matchedIndices + idx1 + idx2
        }
    }

    LaunchedEffect(matchedIndices) {
        if (cards.isNotEmpty() && matchedIndices.size == cards.size && gameStarted) {
            delay(500)
            gameOver = true
        }
    }

    if (gameOver) {
        val score = (numPairs * 20) - (moves * 2)
        val xp = (score.coerceAtLeast(10) * multiplier)
        LaunchedEffect(Unit) {
            AppStorage.storage.saveGameResult("memory_match", score.coerceAtLeast(0), xp)
        }
        GameOverScreen(
            title = "Memory Match Complete!",
            stats = listOf("Pairs found: $numPairs", "Moves: $moves", "Difficulty: ${difficulty.replaceFirstChar{it.uppercase()}}"),
            score = score.coerceAtLeast(0),
            xpEarned = xp,
            onPlayAgain = { navController.popBackStack(); navController.navigate("game_memory_match?difficulty=$difficulty") },
            onBack = { navController.popBackStack() }
        )
        return
    }

    Column(modifier = Modifier.fillMaxSize()) {
        TopAppBar(
            title = { Text("Memory Match") },
            navigationIcon = { IconButton(onClick = { navController.popBackStack() }) { Icon(Icons.Filled.ArrowBack, "Back") } },
            colors = TopAppBarDefaults.topAppBarColors(containerColor = Surface, titleContentColor = OnBackground)
        )
        Text(text = "Moves: $moves", color = Primary, modifier = Modifier.padding(16.dp), fontWeight = FontWeight.Bold)

        Column(
            modifier = Modifier.fillMaxWidth().padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            for (row in 0 until rows) {
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    for (col in 0 until cols) {
                        val idx = row * cols + col
                        if (idx < cards.size) {
                            val isFlipped = idx in flippedIndices || idx in matchedIndices
                            val isMatched = idx in matchedIndices
                            Card(
                                modifier = Modifier.size(70.dp).clickable(enabled = !isFlipped && flippedIndices.size < 2) {
                                    flippedIndices = flippedIndices + idx
                                    if (flippedIndices.size == 2) {
                                        val (i1, i2) = flippedIndices.toList()
                                        checkMatch(i1, i2)
                                        val m1 = i1; val m2 = i2
                                        flippedIndices = emptySet()
                                    }
                                },
                                colors = CardDefaults.cardColors(containerColor = if (isMatched) SuccessColor.copy(alpha = 0.3f) else if (isFlipped) Primary.copy(alpha = 0.2f) else SurfaceVariant),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                    if (isFlipped) {
                                        Text(text = cards[idx].emoji, fontSize = 28.sp)
                                    } else {
                                        Text(text = "?", fontSize = 24.sp, color = OnSurfaceVariant)
                                    }
                                }
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(6.dp))
            }
        }
    }
}

private data class CardData(val index: Int, val emoji: String)
