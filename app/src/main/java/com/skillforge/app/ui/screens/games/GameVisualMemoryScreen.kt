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

@Composable
fun GameVisualMemoryScreen(difficulty: String, navController: NavHostController) {
    val (gridSize, tilesToLight, lightUpDuration, difficultyMultiplier) = when (difficulty.lowercase()) {
        "easy" -> Quadruple(3, 3, 2000L, 1)
        "medium" -> Quadruple(4, 5, 1500L, 2)
        "hard" -> Quadruple(5, 8, 1000L, 3)
        else -> Quadruple(3, 3, 2000L, 1)
    }

    var litTiles by remember { mutableStateOf(emptySet<Int>()) }
    var revealedTiles by remember { mutableStateOf(emptySet<Int>()) }
    var selectedTiles by remember { mutableStateOf(emptySet<Int>()) }
    var round by remember { mutableIntStateOf(1) }
    var lives by remember { mutableIntStateOf(3) }
    var score by remember { mutableIntStateOf(0) }
    var xpEarned by remember { mutableIntStateOf(0) }
    var gamePhase by remember { mutableStateOf("showing") }
    var gameComplete by remember { mutableStateOf(false) }
    var showCorrectTiles by remember { mutableStateOf(false) }
    var messageText by remember { mutableStateOf("Watch the tiles!") }

    fun generateRound() {
        val totalTiles = gridSize * gridSize
        val available = (0 until totalTiles).toMutableList()
        val chosen = mutableSetOf<Int>()
        repeat(minOf(tilesToLight, totalTiles)) {
            val idx = available.random()
            chosen.add(idx)
            available.remove(idx)
        }
        litTiles = chosen
        selectedTiles = emptySet()
        revealedTiles = emptySet()
        showCorrectTiles = false
        messageText = "Watch the tiles!"
    }

    LaunchedEffect(Unit) {
        generateRound()
        gamePhase = "showing"
        delay(lightUpDuration)
        gamePhase = "input"
        messageText = "Tap the lit tiles!"
    }

    fun checkAndAdvance() {
        val allCorrect = litTiles.all { it in selectedTiles }
        if (allCorrect) {
            score += (tilesToLight * 10 + round * 5) * difficultyMultiplier
            messageText = "Correct!"
            gamePhase = "feedback"
        } else {
            lives--
            messageText = "Wrong!"
            showCorrectTiles = true
            gamePhase = "feedback"
            if (lives <= 0) {
                gameComplete = true
                xpEarned = score / 2
                AppStorage.storage.saveGameResult("Visual Memory", score, xpEarned)
            }
        }
    }

    LaunchedEffect(gamePhase) {
        if (gamePhase == "feedback" && !gameComplete) {
            delay(2000L)
            if (lives > 0) {
                round++
                generateRound()
                gamePhase = "showing"
                delay(lightUpDuration)
                gamePhase = "input"
                messageText = "Tap the lit tiles!"
            }
        }
    }

    if (gameComplete) {
        GameCompletionOverlay(
            gameName = "Visual Memory",
            score = score,
            xpEarned = xpEarned,
            message = when {
                round > 10 -> "Incredible memory!"
                round > 6 -> "Great memory!"
                round > 3 -> "Not bad!"
                else -> "Keep practicing!"
            },
            detail = "Survived $round rounds | Lives remaining: $lives",
            onPlayAgain = {
                round = 1
                lives = 3
                score = 0
                xpEarned = 0
                gameComplete = false
                gamePhase = "showing"
                generateRound()
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
        Text("Visual Memory", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Primary)
        Text(difficulty.replaceFirstChar { it.uppercase() }, fontSize = 14.sp, color = OnSurfaceVariant)
        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("Round: $round", color = OnSurface, fontSize = 14.sp)
            Text("Score: $score", color = OnSurface, fontSize = 14.sp)
        }
        Spacer(modifier = Modifier.height(8.dp))

        Row(
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            repeat(3) { idx ->
                Box(
                    modifier = Modifier
                        .size(20.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(if (idx < lives) ErrorColor else SurfaceVariant)
                )
            }
        }
        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = messageText,
            fontSize = 16.sp,
            color = when (gamePhase) {
                "showing" -> Primary
                "input" -> InfoColor
                "feedback" -> if (showCorrectTiles) ErrorColor else SuccessColor
                else -> OnSurface
            },
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(16.dp))

        if (gamePhase == "showing" || (gamePhase == "feedback" && showCorrectTiles)) {
            val displayTiles = if (gamePhase == "showing") litTiles else litTiles
            Column(
                verticalArrangement = Arrangement.spacedBy(6.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                for (row in 0 until gridSize) {
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        for (col in 0 until gridSize) {
                            val tileIdx = row * gridSize + col
                            val isLit = tileIdx in displayTiles
                            val tileColor = if (isLit) Primary else SurfaceVariant

                            Box(
                                modifier = Modifier
                                    .size(64.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(tileColor)
                                    .border(
                                        2.dp,
                                        if (isLit) Primary else SurfaceVariant,
                                        RoundedCornerShape(8.dp)
                                    )
                            )
                        }
                    }
                }
            }
        } else if (gamePhase == "input") {
            Column(
                verticalArrangement = Arrangement.spacedBy(6.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                for (row in 0 until gridSize) {
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        for (col in 0 until gridSize) {
                            val tileIdx = row * gridSize + col
                            val isSelected = tileIdx in selectedTiles
                            val tileColor = if (isSelected) Primary.copy(alpha = 0.6f) else SurfaceVariant

                            Box(
                                modifier = Modifier
                                    .size(64.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(tileColor)
                                    .border(
                                        2.dp,
                                        if (isSelected) Primary else OnSurfaceVariant.copy(alpha = 0.3f),
                                        RoundedCornerShape(8.dp)
                                    )
                                    .clickable {
                                        selectedTiles = if (isSelected) {
                                            selectedTiles - tileIdx
                                        } else {
                                            selectedTiles + tileIdx
                                        }
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                if (isSelected) {
                                    Text("✓", fontSize = 20.sp, color = Color.White, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = { checkAndAdvance() },
                colors = ButtonDefaults.buttonColors(containerColor = SuccessColor),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth(),
                enabled = selectedTiles.isNotEmpty()
            ) {
                Text("Submit (${selectedTiles.size} selected)", fontSize = 14.sp, color = Color.Black, fontWeight = FontWeight.Bold)
            }
        } else if (gamePhase == "feedback" && !showCorrectTiles) {
            Column(
                verticalArrangement = Arrangement.spacedBy(6.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                for (row in 0 until gridSize) {
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        for (col in 0 until gridSize) {
                            val tileIdx = row * gridSize + col
                            val isCorrect = tileIdx in litTiles
                            val wasSelected = tileIdx in selectedTiles
                            val tileColor = when {
                                isCorrect && wasSelected -> SuccessColor
                                !isCorrect && wasSelected -> ErrorColor
                                isCorrect -> Primary.copy(alpha = 0.5f)
                                else -> SurfaceVariant
                            }

                            Box(
                                modifier = Modifier
                                    .size(64.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(tileColor)
                                    .border(2.dp, tileColor, RoundedCornerShape(8.dp))
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.weight(1f))
        Text(
            text = "Round $round | Lives: $lives | Score: $score",
            fontSize = 13.sp,
            color = OnSurfaceVariant,
            textAlign = TextAlign.Center
        )
    }
}

private data class Quadruple<A, B, C, D>(val first: A, val second: B, val third: C, val fourth: D)

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
