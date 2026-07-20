package com.skillforge.app.ui.screens.games

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.skillforge.app.ui.AppStorage
import com.skillforge.app.ui.theme.*
import kotlinx.coroutines.delay

private val simonColors = listOf(
    Color(0xFFEF5350), Color(0xFF42A5F5), Color(0xFF66BB6A), Color(0xFFFFCA28)
)
private val colorNames = listOf("Red", "Blue", "Green", "Yellow")

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GameSimonSaysScreen(difficulty: String, navController: NavHostController) {
    val maxColors = 4
    val showSpeed = when(difficulty) { "hard" -> 700L; "medium" -> 1000L; else -> 1500L }
    val multiplier = when(difficulty) { "hard" -> 3; "medium" -> 2; else -> 1 }

    var sequence by remember { mutableStateOf(listOf<Int>()) }
    var playerInput by remember { mutableStateOf(listOf<Int>()) }
    var score by remember { mutableIntStateOf(0) }
    var phase by remember { mutableStateOf("idle") } // idle, showing, input, game_over
    var highlitColor by remember { mutableIntStateOf(-1) }

    fun startNewRound(currentSequence: List<Int>) {
        sequence = currentSequence
        playerInput = emptyList()
    }

    fun playSequence(seq: List<Int>) {
        phase = "showing"
    }

    LaunchedEffect(Unit) {
        startNewRound(listOf((0 until maxColors).random()))
        phase = "showing"
    }

    LaunchedEffect(phase) {
        if (phase == "showing") {
            delay(500)
            for (i in sequence.indices) {
                highlitColor = sequence[i]
                delay(showSpeed)
                highlitColor = -1
                delay(300)
            }
            phase = "input"
        }
    }

    LaunchedEffect(playerInput.size) {
        if (phase == "input" && playerInput.isNotEmpty()) {
            val idx = playerInput.size - 1
            if (playerInput[idx] != sequence[idx]) {
                phase = "game_over"
            } else if (playerInput.size == sequence.size) {
                score += sequence.size * 10
                delay(500)
                val nextSeq = sequence + (0 until maxColors).random()
                startNewRound(nextSeq)
                phase = "showing"
            }
        }
    }

    if (phase == "game_over") {
        val xp = score * multiplier
        LaunchedEffect(Unit) {
            AppStorage.storage.saveGameResult("simon_says", score, xp)
        }
        GameOverScreen(
            title = "Simon Says Game Over!",
            stats = listOf("Sequence reached: ${sequence.size}", "Difficulty: ${difficulty.replaceFirstChar{it.uppercase()}}"),
            score = score,
            xpEarned = xp,
            onPlayAgain = { navController.popBackStack(); navController.navigate("game_simon_says?difficulty=$difficulty") },
            onBack = { navController.popBackStack() }
        )
        return
    }

    Column(modifier = Modifier.fillMaxSize()) {
        TopAppBar(
            title = { Text("Simon Says") },
            navigationIcon = { IconButton(onClick = { navController.popBackStack() }) { Icon(Icons.Filled.ArrowBack, "Back") } },
            colors = TopAppBarDefaults.topAppBarColors(containerColor = Surface, titleContentColor = OnBackground)
        )

        Text(text = "Score: $score", color = Primary, modifier = Modifier.padding(16.dp), fontWeight = FontWeight.Bold)
        Text(
            text = if (phase == "showing") "Watch carefully..." else "Repeat the sequence!",
            color = OnSurfaceVariant, modifier = Modifier.padding(horizontal = 16.dp), fontSize = 14.sp
        )

        Spacer(modifier = Modifier.weight(1f))

        Column(
            modifier = Modifier.fillMaxWidth().padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                for (i in 0 until 2) {
                    val colorIdx = i
                    val isHighlit = highlitColor == colorIdx
                    Box(
                        modifier = Modifier.size(120.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(if (isHighlit) simonColors[colorIdx] else simonColors[colorIdx].copy(alpha = 0.4f))
                            .border(3.dp, simonColors[colorIdx], RoundedCornerShape(16.dp))
                            .clickable(enabled = phase == "input") {
                                playerInput = playerInput + colorIdx
                            }
                    )
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                for (i in 2 until 4) {
                    val colorIdx = i
                    val isHighlit = highlitColor == colorIdx
                    Box(
                        modifier = Modifier.size(120.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(if (isHighlit) simonColors[colorIdx] else simonColors[colorIdx].copy(alpha = 0.4f))
                            .border(3.dp, simonColors[colorIdx], RoundedCornerShape(16.dp))
                            .clickable(enabled = phase == "input") {
                                playerInput = playerInput + colorIdx
                            }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        Text(text = "Sequence: ${sequence.size}", color = OnSurfaceVariant, modifier = Modifier.padding(16.dp))
    }
}
