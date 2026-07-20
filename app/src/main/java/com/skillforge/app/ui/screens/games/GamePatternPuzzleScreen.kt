package com.skillforge.app.ui.screens.games

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.skillforge.app.ui.AppStorage
import com.skillforge.app.ui.theme.*
import kotlinx.coroutines.delay

private val tileColors = listOf(
    Color(0xFFEF5350), Color(0xFF42A5F5), Color(0xFF66BB6A),
    Color(0xFFFFCA28), Color(0xFFAB47BC), Color(0xFFFF7043)
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GamePatternPuzzleScreen(difficulty: String, navController: NavHostController) {
    val gridSize = when(difficulty) { "hard" -> 9; "medium" -> 6; else -> 4 }
    val seqLength = when(difficulty) { "hard" -> 6; "medium" -> 4; else -> 3 }
    val multiplier = when(difficulty) { "hard" -> 3; "medium" -> 2; else -> 1 }
    val showTime = when(difficulty) { "hard" -> 700L; "medium" -> 1000L; else -> 1500L }

    var sequence by remember { mutableStateOf(listOf<Int>()) }
    var playerSequence by remember { mutableStateOf(listOf<Int>()) }
    var round by remember { mutableIntStateOf(1) }
    var score by remember { mutableIntStateOf(0) }
    var lives by remember { mutableIntStateOf(3) }
    var phase by remember { mutableStateOf("showing") } // showing, input, checking, round_over, game_over
    var currentRoundSeq by remember { mutableStateOf(listOf<Int>()) }
    var highlitIndex by remember { mutableIntStateOf(-1) }

    fun generateSequence(length: Int) = (1..length).map { (0 until gridSize).random() }

    LaunchedEffect(Unit) {
        val seq = generateSequence(seqLength)
        currentRoundSeq = seq
        phase = "showing"
        for (i in seq.indices) {
            highlitIndex = i
            delay(showTime)
            highlitIndex = -1
            delay(300)
        }
        phase = "input"
    }

    LaunchedEffect(playerSequence.size) {
        if (phase == "input" && playerSequence.isNotEmpty()) {
            val idx = playerSequence.size - 1
            if (playerSequence[idx] == currentRoundSeq[idx]) {
                if (playerSequence.size == currentRoundSeq.size) {
                    phase = "checking"
                    score += currentRoundSeq.size * 10
                    delay(500)
                    round++
                    val newSeq = generateSequence((seqLength + round - 1).coerceAtMost(gridSize))
                    currentRoundSeq = newSeq
                    playerSequence = emptyList()
                    phase = "showing"
                    for (i in newSeq.indices) {
                        highlitIndex = i
                        delay(showTime)
                        highlitIndex = -1
                        delay(300)
                    }
                    phase = "input"
                }
            } else {
                lives--
                if (lives <= 0) {
                    phase = "game_over"
                } else {
                    phase = "checking"
                    delay(800)
                    playerSequence = emptyList()
                    phase = "showing"
                    for (i in currentRoundSeq.indices) {
                        highlitIndex = i
                        delay(showTime)
                        highlitIndex = -1
                        delay(300)
                    }
                    phase = "input"
                }
            }
        }
    }

    if (phase == "game_over") {
        val xp = score * multiplier
        LaunchedEffect(Unit) {
            AppStorage.storage.saveGameResult("pattern_puzzle", score, xp)
        }
        GameOverScreen(
            title = "Pattern Puzzle Over!",
            stats = listOf("Rounds: ${round - 1}", "Lives remaining: $lives", "Difficulty: ${difficulty.replaceFirstChar{it.uppercase()}}"),
            score = score,
            xpEarned = xp,
            onPlayAgain = { navController.popBackStack(); navController.navigate("game_pattern_puzzle?difficulty=$difficulty") },
            onBack = { navController.popBackStack() }
        )
        return
    }

    val gridCols = when(gridSize) { 9 -> 3; 6 -> 3; else -> 2 }

    Column(modifier = Modifier.fillMaxSize()) {
        TopAppBar(
            title = { Text("Pattern Puzzle") },
            navigationIcon = { IconButton(onClick = { navController.popBackStack() }) { Icon(Icons.Filled.ArrowBack, "Back") } },
            colors = TopAppBarDefaults.topAppBarColors(containerColor = Surface, titleContentColor = OnBackground)
        )

        Row(modifier = Modifier.fillMaxWidth().padding(16.dp), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(text = "Round: $round", color = OnBackground)
            Text(text = "Lives: ${"❤️".repeat(lives)}", color = ErrorColor)
            Text(text = "Score: $score", color = Primary)
        }

        Text(
            text = if (phase == "showing") "Watch the pattern..." else "Your turn! Tap the tiles",
            color = OnSurfaceVariant, modifier = Modifier.padding(16.dp), fontSize = 14.sp
        )

        Column(modifier = Modifier.fillMaxWidth().padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            for (row in 0 until gridSize / gridCols + if (gridSize % gridCols > 0) 1 else 0) {
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    for (col in 0 until gridCols) {
                        val idx = row * gridCols + col
                        if (idx < gridSize) {
                            val isHighlit = phase == "showing" && highlitIndex >= 0 && currentRoundSeq.getOrNull(highlitIndex) == idx
                            Box(
                                modifier = Modifier.size(80.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(if (isHighlit) tileColors[idx % tileColors.size] else SurfaceVariant)
                                    .border(2.dp, if (isHighlit) tileColors[idx % tileColors.size].copy(alpha = 0.8f) else OnSurfaceVariant.copy(alpha = 0.3f), RoundedCornerShape(12.dp))
                                    .clickable(enabled = phase == "input") {
                                        playerSequence = playerSequence + idx
                                    }
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))
            }
        }
    }
}
