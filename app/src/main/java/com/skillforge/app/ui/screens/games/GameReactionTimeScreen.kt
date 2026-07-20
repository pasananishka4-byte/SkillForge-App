package com.skillforge.app.ui.screens.games

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.skillforge.app.ui.AppStorage
import com.skillforge.app.ui.theme.*
import kotlinx.coroutines.delay
import kotlin.random.Random

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GameReactionTimeScreen(difficulty: String, navController: NavHostController) {
    val rounds = when(difficulty) { "hard" -> 10; "medium" -> 7; else -> 5 }
    val minDelay = when(difficulty) { "hard" -> 800; "medium" -> 1000; else -> 1500 }
    val maxDelay = when(difficulty) { "hard" -> 3000; "medium" -> 4000; else -> 5000 }
    val multiplier = when(difficulty) { "hard" -> 3; "medium" -> 2; else -> 1 }

    var phase by remember { mutableStateOf("waiting") }
    var round by remember { mutableIntStateOf(1) }
    var reactions by remember { mutableStateOf(mutableListOf<Long>()) }
    var flashTime by remember { mutableStateOf(0L) }
    var lastResult by remember { mutableStateOf("") }
    var totalScore by remember { mutableIntStateOf(0) }
    var earlyPress by remember { mutableStateOf(false) }

    LaunchedEffect(round) {
        if (round > rounds) {
            phase = "game_over"
            return@LaunchedEffect
        }
        phase = "waiting"
        delay(1000)
        phase = "ready"
        earlyPress = false
        val delayMs = Random.nextLong(minDelay.toLong(), maxDelay.toLong())
        delay(delayMs)
        if (phase == "ready") {
            flashTime = System.currentTimeMillis()
            phase = "flash"
        }
    }

    LaunchedEffect(phase) {
        if (phase == "result") {
            delay(1500)
            round++
        }
    }

    fun handleTap() {
        when (phase) {
            "flash" -> {
                val reaction = System.currentTimeMillis() - flashTime
                reactions.add(reaction)
                lastResult = "${reaction}ms"
                val score = when {
                    reaction < 200 -> 50; reaction < 300 -> 30; reaction < 400 -> 20
                    reaction < 500 -> 10; else -> 5
                }
                totalScore += score
                phase = "result"
            }
            "ready" -> {
                earlyPress = true
                totalScore = (totalScore - 10).coerceAtLeast(0)
                lastResult = "Too early! -10"
                phase = "result"
            }
        }
    }

    if (phase == "game_over") {
        val avg = if (reactions.isNotEmpty()) reactions.average().toLong() else 0L
        val xp = totalScore * multiplier
        LaunchedEffect(Unit) { AppStorage.storage.saveGameResult("reaction_time", totalScore, xp) }
        GameOverScreen(
            title = "Reaction Time Complete!",
            stats = listOf("Avg: ${avg}ms", "Best: ${reactions.minOrNull() ?: 0}ms", "Rounds: $rounds"),
            score = totalScore, xpEarned = xp,
            onPlayAgain = { navController.popBackStack(); navController.navigate("game_reaction_time?difficulty=$difficulty") },
            onBack = { navController.popBackStack() }
        )
        return
    }

    val bgColor = when (phase) {
        "waiting" -> Background
        "ready" -> ErrorColor.copy(alpha = 0.7f)
        "flash" -> SuccessColor.copy(alpha = 0.7f)
        else -> Background
    }

    Box(
        modifier = Modifier.fillMaxSize()
            .background(bgColor)
            .clickable { handleTap() },
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            TopAppBar(
                title = { Text("Reaction Time", color = OnBackground) },
                navigationIcon = { IconButton(onClick = { navController.popBackStack() }) { Icon(Icons.Filled.ArrowBack, "Back", tint = OnBackground) } },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
            )
            Spacer(modifier = Modifier.weight(1f))

            Text(text = "Round $round / $rounds", color = OnSurfaceVariant, fontSize = 18.sp)
            Spacer(modifier = Modifier.height(8.dp))

            when (phase) {
                "waiting" -> Text("Get ready...", fontSize = 24.sp, color = OnSurfaceVariant)
                "ready" -> Text("TAP NOW!", fontSize = 36.sp, fontWeight = FontWeight.Bold, color = OnBackground)
                "flash" -> Text("TAP!", fontSize = 48.sp, fontWeight = FontWeight.Bold, color = OnBackground)
                "result" -> {
                    Text(lastResult, fontSize = 24.sp, color = if (earlyPress) ErrorColor else SuccessColor)
                    Text("Score: $totalScore", fontSize = 18.sp, color = OnSurfaceVariant)
                }
            }

            Spacer(modifier = Modifier.weight(1f))
            Text(text = "Score: $totalScore", color = Primary, fontSize = 20.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(16.dp))
        }
    }
}
