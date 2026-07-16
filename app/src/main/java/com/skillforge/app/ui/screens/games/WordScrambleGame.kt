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
import androidx.compose.ui.unit.sp
import com.skillforge.app.ui.theme.*
import kotlinx.coroutines.delay
import kotlin.random.Random

private data class WordLevel(
    val word: String,
    val hint: String,
    val category: String
)

private val wordLevels = listOf(
    WordLevel("ALGORITHM", "Step-by-step procedure", "Tech"),
    WordLevel("PHILOSOPHY", "Study of fundamental truths", "Academic"),
    WordLevel("QUANTUM", "Smallest discrete unit in physics", "Science"),
    WordLevel("SYMPHONY", "Large-scale orchestral composition", "Music"),
    WordLevel("VOLCANO", "Mountain that erupts", "Nature"),
    WordLevel("CRYPTIC", "Mysterious or obscure", "Language"),
    WordLevel("DIPLOMAT", "Official envoy between nations", "Politics"),
    WordLevel("ECLIPSE", "One celestial body blocks another", "Space"),
    WordLevel("FJORD", "Narrow sea inlet between cliffs", "Geography"),
    WordLevel("HARMONY", "Beautiful arrangement of parts", "Music"),
    WordLevel("JUXTAPOSE", "Place side by side for contrast", "Language"),
    WordLevel("KALEIDOSCOPE", "Tube with colored patterns", "Science"),
    WordLevel("LABYRINTH", "Complex maze structure", "Mythology"),
    WordLevel("METAPHOR", "Figure of speech comparing unlike things", "Language"),
    WordLevel("NEBULA", "Giant cloud of dust in space", "Space"),
    WordLevel("OBSTACLE", "Something that blocks your path", "General"),
    WordLevel("PARADOX", "Self-contradictory statement", "Logic"),
    WordLevel("RESILIENCE", "Ability to recover quickly", "Psychology"),
    WordLevel("SOPHISTICATED", "Highly developed and complex", "Language"),
    WordLevel("TELESCOPE", "Instrument for viewing distant objects", "Science"),
    WordLevel("UBIQUITOUS", "Present everywhere", "Language"),
    WordLevel("VIGILANTE", "Self-appointed enforcer", "Society"),
    WordLevel("WANDERLUST", "Strong desire to travel", "General"),
    WordLevel("ZEALOUS", "Having great energy or enthusiasm", "Language")
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WordScrambleGame(
    onBack: () -> Unit,
    difficulty: String = "Normal",
    onGameComplete: (xpEarned: Int, score: Int) -> Unit = { _, _ -> }
) {
    val levelsNeeded = when (difficulty) { "Easy" -> 5; "Hard" -> 15; else -> 10 }
    val levels = remember { wordLevels.shuffled().take(levelsNeeded) }

    var currentLevel by remember { mutableIntStateOf(0) }
    var score by remember { mutableIntStateOf(0) }
    var streak by remember { mutableIntStateOf(0) }
    var isComplete by remember { mutableStateOf(false) }
    var timeLeft by remember { mutableIntStateOf(0) }
    var timerRunning by remember { mutableStateOf(false) }
    var showHint by remember { mutableStateOf(false) }
    var selectedLetters by remember { mutableStateOf(listOf<Int>()) }
    var showResult by remember { mutableStateOf(false) }
    var isCorrect by remember { mutableStateOf(false) }
    var hintCount by remember { mutableIntStateOf(0) }
    var pendingAdvance by remember { mutableStateOf(false) }

    val timerEnabled = difficulty != "Easy"

    LaunchedEffect(pendingAdvance) {
        if (pendingAdvance) {
            delay(1500)
            showResult = false
            selectedLetters = emptyList()
            currentLevel++
            showHint = false
            pendingAdvance = false
            if (currentLevel >= levels.size) {
                isComplete = true
                onGameComplete(score, levels.size)
            }
        }
    }

    LaunchedEffect(timerRunning) {
        if (timerEnabled && timerRunning) {
            val maxTime = when (difficulty) { "Hard" -> 15; else -> 30 }
            timeLeft = maxTime
            while (timeLeft > 0 && !isComplete) {
                delay(1000)
                timeLeft--
            }
            if (timeLeft <= 0 && !isComplete) {
                isComplete = true
                timerRunning = false
                onGameComplete(score, currentLevel)
            }
        }
    }

    LaunchedEffect(currentLevel) {
        if (currentLevel < levels.size && !isComplete) {
            timerRunning = true
        }
    }

    if (isComplete) {
        Column(
            modifier = Modifier.fillMaxSize().background(Background).padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center
        ) {
            Icon(Icons.Filled.EmojiEvents, null, tint = GeneralKnowledgeColor, modifier = Modifier.size(80.dp))
            Spacer(Modifier.height(16.dp))
            Text("Word Master!", style = MaterialTheme.typography.headlineMedium, color = OnSurface, fontWeight = FontWeight.Bold)
            Text("Unscrambled $currentLevel words", style = MaterialTheme.typography.bodyMedium, color = OnSurfaceVariant)
            Spacer(Modifier.height(8.dp))
            Text("Streak best: $streak", style = MaterialTheme.typography.bodyMedium, color = Primary)
            Spacer(Modifier.height(12.dp))
            Text("+$score XP", style = MaterialTheme.typography.titleLarge, color = Primary, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(24.dp))
            Button(onClick = onBack, modifier = Modifier.fillMaxWidth(), colors = ButtonDefaults.buttonColors(containerColor = GeneralKnowledgeColor), shape = RoundedCornerShape(12.dp)) {
                Text("Done", color = OnPrimary, fontWeight = FontWeight.Bold)
            }
        }
        return
    }

    val level = levels[currentLevel]
    val scrambled = remember(currentLevel) {
        level.word.toList().shuffled().map { it.toString() }
    }

    Column(
        modifier = Modifier.fillMaxSize().background(Background).padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onBack) { Icon(Icons.Filled.ArrowBack, "Back", tint = OnSurface) }
            Column(modifier = Modifier.weight(1f)) {
                Text("Word Scramble", style = MaterialTheme.typography.titleLarge, color = GeneralKnowledgeColor, fontWeight = FontWeight.Bold)
                Text("Unscramble the letters", style = MaterialTheme.typography.bodySmall, color = OnSurfaceVariant)
            }
        }

        Row(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp), horizontalArrangement = Arrangement.SpaceBetween) {
            Surface(shape = RoundedCornerShape(8.dp), color = GeneralKnowledgeColor.copy(alpha = 0.15f)) {
                Text("Level ${currentLevel + 1}/${levels.size}", style = MaterialTheme.typography.labelMedium, color = GeneralKnowledgeColor, modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp))
            }
            Surface(shape = RoundedCornerShape(8.dp), color = Primary.copy(alpha = 0.15f)) {
                Text("Score: $score", style = MaterialTheme.typography.labelMedium, color = Primary, modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp))
            }
            if (timerEnabled) {
                Surface(shape = RoundedCornerShape(8.dp), color = if (timeLeft > 10) Secondary.copy(alpha = 0.15f) else ErrorColor.copy(alpha = 0.15f)) {
                    Text("${timeLeft}s", style = MaterialTheme.typography.labelMedium, color = if (timeLeft > 10) Secondary else ErrorColor, modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp))
                }
            }
        }

        Spacer(Modifier.height(12.dp))

        // Category & hint
        Surface(shape = RoundedCornerShape(8.dp), color = SurfaceVariant) {
            Text("Category: ${level.category}", style = MaterialTheme.typography.labelSmall, color = OnSurfaceVariant, modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp))
        }
        Spacer(Modifier.height(8.dp))
        TextButton(onClick = { showHint = !showHint; if (!showHint) hintCount++ }) {
            Icon(Icons.Filled.Lightbulb, null, tint = WarningColor, modifier = Modifier.size(18.dp))
            Spacer(Modifier.width(4.dp))
            Text(if (showHint) "Hide Hint" else "Show Hint (${3 - hintCount} left)", color = WarningColor)
        }
        if (showHint) {
            Text(level.hint, style = MaterialTheme.typography.bodySmall, color = OnSurfaceVariant)
        }

        Spacer(Modifier.height(16.dp))

        // Selected answer area
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Surface),
            shape = RoundedCornerShape(12.dp)
        ) {
            Row(
                modifier = Modifier.padding(16.dp).fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (selectedLetters.isEmpty()) {
                    Text("_ _ _ _ _", style = MaterialTheme.typography.headlineSmall, color = SurfaceVariant, letterSpacing = 8.sp)
                } else {
                    selectedLetters.forEach { idx ->
                        Text(
                            text = scrambled[idx],
                            style = MaterialTheme.typography.headlineSmall,
                            color = GeneralKnowledgeColor,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 4.dp)
                        )
                    }
                }
            }
        }

        Spacer(Modifier.height(8.dp))

        if (showResult) {
            AnimatedVisibility(visible = true) {
                Text(
                    if (isCorrect) "Correct! +${if (streak >= 3) 20 else 10} XP" else "Wrong! It was: ${level.word}",
                    style = MaterialTheme.typography.titleMedium,
                    color = if (isCorrect) SuccessColor else ErrorColor,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Spacer(Modifier.height(16.dp))

        // Letter buttons
        Text("Available letters:", style = MaterialTheme.typography.labelMedium, color = OnSurfaceVariant)
        Spacer(Modifier.height(8.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            scrambled.forEachIndexed { idx, letter ->
                val isSelected = idx in selectedLetters
                Box(
                    modifier = Modifier.padding(horizontal = 3.dp)
                        .size(44.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (isSelected) SurfaceVariant.copy(alpha = 0.5f) else GeneralKnowledgeColor.copy(alpha = 0.15f))
                        .clickable(enabled = !isSelected && !showResult) {
                            selectedLetters = selectedLetters + idx
                            if (selectedLetters.size == level.word.length) {
                                val attempt = selectedLetters.joinToString("") { scrambled[it] }
                                isCorrect = attempt == level.word
                                if (isCorrect) {
                                    streak++
                                    score += if (streak >= 3) 20 else 10
                                } else {
                                    streak = 0
                                }
                                showResult = true
                                timerRunning = false
                                pendingAdvance = true
                            }
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        letter,
                        style = MaterialTheme.typography.titleMedium,
                        color = if (isSelected) SurfaceVariant else GeneralKnowledgeColor,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        Spacer(Modifier.height(16.dp))

        // Clear button
        OutlinedButton(
            onClick = { selectedLetters = emptyList() },
            enabled = selectedLetters.isNotEmpty() && !showResult
        ) {
            Icon(Icons.Filled.Refresh, null, modifier = Modifier.size(18.dp))
            Spacer(Modifier.width(4.dp))
            Text("Clear")
        }
    }
}
