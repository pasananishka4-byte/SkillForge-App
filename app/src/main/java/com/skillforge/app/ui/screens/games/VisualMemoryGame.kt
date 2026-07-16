package com.skillforge.app.ui.screens.games

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import com.skillforge.app.ui.theme.*
import kotlinx.coroutines.delay
import kotlin.random.Random

enum class VisualGameState { SHOWING, PLAYER_TURN, GAME_OVER }

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VisualMemoryGame(
    onBack: () -> Unit,
    difficulty: String = "Normal",
    onGameComplete: (xpEarned: Int, score: Int) -> Unit = { _, _ -> }
) {
    val gridSize = when (difficulty) { "Easy" -> 3; "Hard" -> 5; else -> 4 }
    val totalTiles = gridSize * gridSize

    var state by remember { mutableStateOf(VisualGameState.SHOWING) }
    var highlightedTiles by remember { mutableStateOf(setOf<Int>()) }
    var playerSelected by remember { mutableStateOf(setOf<Int>()) }
    var sequence by remember { mutableStateOf(listOf<Int>()) }
    var round by remember { mutableIntStateOf(1) }
    var score by remember { mutableIntStateOf(0) }
    var highScore by remember { mutableIntStateOf(0) }
    var showFeedback by remember { mutableStateOf(false) }
    var feedbackCorrect by remember { mutableStateOf(false) }
    var tilesToHighlight by remember { mutableIntStateOf(1) }
    var pendingAdvance by remember { mutableStateOf(false) }

    LaunchedEffect(pendingAdvance) {
        if (pendingAdvance) {
            delay(1000)
            showFeedback = false
            playerSelected = emptySet()
            pendingAdvance = false
            state = VisualGameState.SHOWING
        }
    }

    LaunchedEffect(sequence, state) {
        if (state == VisualGameState.SHOWING) {
            delay(600)
            // Generate new tiles to highlight
            val newTiles = mutableSetOf<Int>()
            val count = minOf(tilesToHighlight, totalTiles)
            while (newTiles.size < count) {
                newTiles.add(Random.nextInt(totalTiles))
            }
            highlightedTiles = newTiles
            delay(1000)
            // Flash each highlighted tile briefly
            highlightedTiles.forEach { tile ->
                highlightedTiles = setOf(tile)
                delay(400)
                highlightedTiles = newTiles
                delay(200)
            }
            highlightedTiles = emptySet()
            delay(300)
            state = VisualGameState.PLAYER_TURN
        }
    }

    fun checkPlayerInput() {
        if (playerSelected == highlightedTiles || playerSelected == setOf(*highlightedTiles.toTypedArray())) {
            feedbackCorrect = true
            showFeedback = true
            score += 10 * round
            round++
            tilesToHighlight = minOf(tilesToHighlight + 1, totalTiles)
            pendingAdvance = true
        } else {
            feedbackCorrect = false
            showFeedback = true
            state = VisualGameState.GAME_OVER
            if (score > highScore) highScore = score
            onGameComplete(score, round - 1)
        }
    }

    Column(
        modifier = Modifier.fillMaxSize().background(Background).padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onBack) { Icon(Icons.Filled.ArrowBack, "Back", tint = OnSurface) }
            Column(modifier = Modifier.weight(1f)) {
                Text("Visual Memory", style = MaterialTheme.typography.titleLarge, color = Secondary, fontWeight = FontWeight.Bold)
                Text("$difficulty - $gridSize x $gridSize grid", style = MaterialTheme.typography.bodySmall, color = OnSurfaceVariant)
            }
        }

        // Score display
        Row(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp), horizontalArrangement = Arrangement.SpaceEvenly) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("$round", style = MaterialTheme.typography.headlineSmall, color = Secondary, fontWeight = FontWeight.Bold)
                Text("Round", style = MaterialTheme.typography.labelSmall, color = OnSurfaceVariant)
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("$score", style = MaterialTheme.typography.headlineSmall, color = Primary, fontWeight = FontWeight.Bold)
                Text("Score", style = MaterialTheme.typography.labelSmall, color = OnSurfaceVariant)
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("$highScore", style = MaterialTheme.typography.headlineSmall, color = StreakFire, fontWeight = FontWeight.Bold)
                Text("Best", style = MaterialTheme.typography.labelSmall, color = OnSurfaceVariant)
            }
        }

        Spacer(Modifier.height(8.dp))

        // Status
        val statusText = when (state) {
            VisualGameState.SHOWING -> "Watch the pattern..."
            VisualGameState.PLAYER_TURN -> "Your turn! Tap the tiles"
            VisualGameState.GAME_OVER -> ""
        }
        Text(statusText, style = MaterialTheme.typography.bodyMedium, color = OnSurfaceVariant)

        Spacer(Modifier.height(16.dp))

        if (state == VisualGameState.GAME_OVER) {
            Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = ErrorColor.copy(alpha = 0.1f)), shape = RoundedCornerShape(16.dp)) {
                Column(modifier = Modifier.padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Filled.Close, null, tint = ErrorColor, modifier = Modifier.size(48.dp))
                    Spacer(Modifier.height(8.dp))
                    Text("Game Over!", style = MaterialTheme.typography.headlineSmall, color = OnSurface, fontWeight = FontWeight.Bold)
                    Text("You reached round $round", style = MaterialTheme.typography.bodyMedium, color = OnSurfaceVariant)
                    Spacer(Modifier.height(8.dp))
                    Text("+$score XP", style = MaterialTheme.typography.titleMedium, color = Primary, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(16.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        Button(onClick = onBack, colors = ButtonDefaults.buttonColors(containerColor = Surface), shape = RoundedCornerShape(12.dp)) {
                            Text("Exit", color = OnSurface)
                        }
                        Button(onClick = {
                            sequence = emptyList()
                            playerSelected = emptySet()
                            state = VisualGameState.SHOWING
                            score = 0; round = 1; tilesToHighlight = 1
                            showFeedback = false
                        }, colors = ButtonDefaults.buttonColors(containerColor = Secondary), shape = RoundedCornerShape(12.dp)) {
                            Text("Retry", color = OnPrimary)
                        }
                    }
                }
            }
        } else {
            // Grid
            val cellSize = (320 / gridSize).dp
            Column(
                verticalArrangement = Arrangement.spacedBy(6.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                for (row in 0 until gridSize) {
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        for (col in 0 until gridSize) {
                            val tileIndex = row * gridSize + col
                            val isHighlighted = tileIndex in highlightedTiles
                            val isSelected = tileIndex in playerSelected

                            val bgColor by animateColorAsState(
                                targetValue = when {
                                    state == VisualGameState.SHOWING && isHighlighted -> Secondary
                                    isSelected -> Secondary.copy(alpha = 0.5f)
                                    else -> SurfaceVariant
                                },
                                label = "tile"
                            )

                            Box(
                                modifier = Modifier
                                    .size(cellSize)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(bgColor)
                                    .then(
                                        if (isSelected) Modifier.border(2.dp, Secondary, RoundedCornerShape(8.dp))
                                        else Modifier
                                    )
                                    .clickable(enabled = state == VisualGameState.PLAYER_TURN && !showFeedback) {
                                        playerSelected = if (tileIndex in playerSelected) {
                                            playerSelected - tileIndex
                                        } else {
                                            playerSelected + tileIndex
                                        }
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                if (state == VisualGameState.PLAYER_TURN && isSelected) {
                                    Icon(Icons.Filled.Check, null, tint = OnPrimary, modifier = Modifier.size(20.dp))
                                }
                            }
                        }
                    }
                }
            }

            // Submit button
            if (state == VisualGameState.PLAYER_TURN) {
                Spacer(Modifier.height(16.dp))
                Button(
                    onClick = { checkPlayerInput() },
                    enabled = playerSelected.isNotEmpty() && !showFeedback,
                    colors = ButtonDefaults.buttonColors(containerColor = Secondary),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth().height(48.dp)
                ) {
                    Text("Submit (${playerSelected.size} tiles)", color = OnPrimary, fontWeight = FontWeight.Bold)
                }

                // Progress dots
                Spacer(Modifier.height(12.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    repeat(minOf(tilesToHighlight, 12)) { idx ->
                        Box(
                            modifier = Modifier.size(8.dp).clip(RoundedCornerShape(4.dp))
                                .background(if (idx < tilesToHighlight) Secondary else SurfaceVariant)
                        )
                    }
                }
            }

            // Feedback overlay
            if (showFeedback) {
                Spacer(Modifier.height(12.dp))
                Text(
                    if (feedbackCorrect) "Correct!" else "Wrong pattern!",
                    style = MaterialTheme.typography.titleMedium,
                    color = if (feedbackCorrect) SuccessColor else ErrorColor,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
