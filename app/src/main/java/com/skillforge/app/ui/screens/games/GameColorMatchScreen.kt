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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.skillforge.app.ui.AppStorage
import com.skillforge.app.ui.theme.*
import kotlinx.coroutines.delay
import kotlin.random.Random

private data class StroopQuestion(val word: String, val displayColor: Color, val colorName: String)

private val stroopColors = listOf(
    "Red" to Color(0xFFEF5350),
    "Blue" to Color(0xFF42A5F5),
    "Green" to Color(0xFF66BB6A),
    "Yellow" to Color(0xFFFFCA28),
    "Purple" to Color(0xFFAB47BC),
    "Orange" to Color(0xFFFF7043)
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GameColorMatchScreen(difficulty: String, navController: NavHostController) {
    val rounds = when(difficulty) { "hard" -> 20; "medium" -> 15; else -> 10 }
    val timeLimit = when(difficulty) { "hard" -> 20; "medium" -> 30; else -> 45 }
    val multiplier = when(difficulty) { "hard" -> 3; "medium" -> 2; else -> 1 }

    var questions by remember { mutableStateOf(listOf<StroopQuestion>()) }
    var currentIdx by remember { mutableIntStateOf(0) }
    var score by remember { mutableIntStateOf(0) }
    var correctCount by remember { mutableIntStateOf(0) }
    var timeLeft by remember { mutableIntStateOf(timeLimit) }
    var gameOver by remember { mutableStateOf(false) }
    var answered by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        val qs = mutableListOf<StroopQuestion>()
        repeat(rounds) {
            val (wordName, _) = stroopColors.random()
            val (_, dispColor) = stroopColors.filter { it.first != wordName }.random()
            val (colorName, _) = stroopColors.random()
            qs.add(StroopQuestion(word = wordName, displayColor = dispColor, colorName = colorName))
        }
        questions = qs
        while (timeLeft > 0 && currentIdx < rounds) {
            delay(1000)
            timeLeft--
            if (timeLeft == 0) gameOver = true
        }
    }

    LaunchedEffect(answered) {
        if (answered) {
            delay(600)
            answered = false
            currentIdx++
            if (currentIdx >= rounds) gameOver = true
        }
    }

    if (gameOver) {
        val xp = (score * multiplier)
        LaunchedEffect(Unit) { AppStorage.storage.saveGameResult("color_match", score, xp) }
        GameOverScreen(
            title = "Color Match Complete!",
            stats = listOf("Correct: $correctCount/$rounds", "Difficulty: ${difficulty.replaceFirstChar{it.uppercase()}}"),
            score = score, xpEarned = xp,
            onPlayAgain = { navController.popBackStack(); navController.navigate("game_color_match?difficulty=$difficulty") },
            onBack = { navController.popBackStack() }
        )
        return
    }

    if (questions.isEmpty()) return

    val q = questions[currentIdx]

    Column(modifier = Modifier.fillMaxSize()) {
        TopAppBar(
            title = { Text("Color Match") },
            navigationIcon = { IconButton(onClick = { navController.popBackStack() }) { Icon(Icons.Filled.ArrowBack, "Back") } },
            colors = TopAppBarDefaults.topAppBarColors(containerColor = Surface, titleContentColor = OnBackground)
        )
        Row(modifier = Modifier.fillMaxWidth().padding(16.dp), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(text = "${currentIdx + 1}/$rounds", color = OnSurfaceVariant)
            Text(text = "⏱ ${timeLeft}s", color = if (timeLeft < 10) ErrorColor else Primary, fontWeight = FontWeight.Bold)
            Text(text = "Score: $score", color = Primary)
        }

        Spacer(modifier = Modifier.height(32.dp))

        Text(text = "Tap the COLOR of the text, not the word!", color = OnSurfaceVariant, fontSize = 14.sp, modifier = Modifier.padding(horizontal = 16.dp), textAlign = TextAlign.Center)
        Spacer(modifier = Modifier.height(16.dp))

        Card(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 32.dp),
            colors = CardDefaults.cardColors(containerColor = Surface),
            shape = RoundedCornerShape(16.dp)
        ) {
            Box(modifier = Modifier.fillMaxWidth().height(120.dp), contentAlignment = Alignment.Center) {
                Text(text = q.word.uppercase(), fontSize = 48.sp, fontWeight = FontWeight.Bold, color = q.displayColor)
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        stroopColors.chunked(2).forEach { row ->
            Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                row.forEach { (name, color) ->
                    Card(
                        modifier = Modifier.weight(1f).clickable(enabled = !answered) {
                            answered = true
                            if (name == q.colorName) { score += 10; correctCount++ }
                        },
                        colors = CardDefaults.cardColors(containerColor = color.copy(alpha = 0.3f)),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = name,
                            color = OnBackground,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(16.dp).fillMaxWidth(),
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
        }
    }
}
