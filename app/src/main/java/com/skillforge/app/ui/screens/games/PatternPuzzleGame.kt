package com.skillforge.app.ui.screens.games

import androidx.compose.animation.*
import androidx.compose.foundation.background
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

data class PatternLevel(
    val sequence: List<Int>,
    val options: List<Int>,
    val correctIndex: Int,
    val hint: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PatternPuzzleGame(onBack: () -> Unit) {
    val levels = remember { generatePatternLevels() }
    var currentLevel by remember { mutableIntStateOf(0) }
    var score by remember { mutableIntStateOf(0) }
    var isComplete by remember { mutableStateOf(false) }
    var selectedAnswer by remember { mutableIntStateOf(-1) }
    var showResult by remember { mutableStateOf(false) }
    var showHint by remember { mutableStateOf(false) }

    if (isComplete) {
        Column(
            modifier = Modifier.fillMaxSize().background(Background).padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center
        ) {
            Icon(Icons.Filled.EmojiEvents, null, tint = CriticalThinkingColor, modifier = Modifier.size(80.dp))
            Spacer(Modifier.height(16.dp))
            Text("Puzzle Master!", style = MaterialTheme.typography.headlineMedium, color = OnSurface, fontWeight = FontWeight.Bold)
            Text("Completed all $currentLevel patterns", style = MaterialTheme.typography.bodyMedium, color = OnSurfaceVariant)
            Spacer(Modifier.height(12.dp))
            Text("+$score XP", style = MaterialTheme.typography.titleLarge, color = Primary, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(24.dp))
            Button(onClick = onBack, modifier = Modifier.fillMaxWidth(), colors = ButtonDefaults.buttonColors(containerColor = CriticalThinkingColor), shape = RoundedCornerShape(12.dp)) {
                Text("Done", color = OnPrimary, fontWeight = FontWeight.Bold)
            }
        }
        return
    }

    val level = levels[currentLevel]

    Column(
        modifier = Modifier.fillMaxSize().background(Background).padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onBack) { Icon(Icons.Filled.ArrowBack, "Back", tint = OnSurface) }
            Column(modifier = Modifier.weight(1f)) {
                Text("Pattern Puzzle", style = MaterialTheme.typography.titleLarge, color = CriticalThinkingColor, fontWeight = FontWeight.Bold)
                Text("Find the pattern and predict the next number", style = MaterialTheme.typography.bodySmall, color = OnSurfaceVariant)
            }
        }

        Row(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp), horizontalArrangement = Arrangement.SpaceBetween) {
            Surface(shape = RoundedCornerShape(8.dp), color = CriticalThinkingColor.copy(alpha = 0.15f)) {
                Text("Level ${currentLevel + 1}/${levels.size}", style = MaterialTheme.typography.labelMedium, color = CriticalThinkingColor, modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp))
            }
            Surface(shape = RoundedCornerShape(8.dp), color = Primary.copy(alpha = 0.15f)) {
                Text("Score: $score", style = MaterialTheme.typography.labelMedium, color = Primary, modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp))
            }
        }

        Spacer(Modifier.height(24.dp))

        // Sequence display
        Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = Surface), shape = RoundedCornerShape(16.dp)) {
            Column(modifier = Modifier.padding(20.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                Text("What comes next?", style = MaterialTheme.typography.bodyMedium, color = OnSurfaceVariant)
                Spacer(Modifier.height(16.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    level.sequence.forEachIndexed { index, num ->
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = CriticalThinkingColor.copy(alpha = 0.1f)
                        ) {
                            Text(
                                text = "$num",
                                style = MaterialTheme.typography.titleLarge,
                                color = CriticalThinkingColor,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                            )
                        }
                        if (index < level.sequence.size - 1) {
                            Text("  ", style = MaterialTheme.typography.bodyMedium)
                        }
                    }
                    Text("  ?", style = MaterialTheme.typography.headlineMedium, color = Primary, fontWeight = FontWeight.Bold)
                }
            }
        }

        Spacer(Modifier.height(16.dp))

        // Hint button
        TextButton(onClick = { showHint = !showHint }) {
            Icon(Icons.Filled.Lightbulb, null, tint = WarningColor, modifier = Modifier.size(18.dp))
            Spacer(Modifier.width(4.dp))
            Text(if (showHint) "Hide Hint" else "Show Hint", color = WarningColor)
        }
        if (showHint) {
            Text(level.hint, style = MaterialTheme.typography.bodySmall, color = OnSurfaceVariant, textAlign = TextAlign.Center)
        }

        Spacer(Modifier.height(16.dp))

        // Answer options
        Text("Choose the next number:", style = MaterialTheme.typography.bodyMedium, color = OnSurfaceVariant)
        Spacer(Modifier.height(8.dp))

        level.options.chunked(2).forEach { row ->
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                row.forEachIndexed { colIndex, option ->
                    val globalIndex = (level.options.indexOf(option))
                    val isCorrect = option == level.options[level.correctIndex]
                    val containerColor = when {
                        showResult && isCorrect -> SuccessColor.copy(alpha = 0.2f)
                        showResult && globalIndex == selectedAnswer && !isCorrect -> ErrorColor.copy(alpha = 0.2f)
                        globalIndex == selectedAnswer -> CriticalThinkingColor.copy(alpha = 0.15f)
                        else -> Surface
                    }

                    Card(
                        modifier = Modifier.weight(1f).padding(vertical = 4.dp)
                            .clickable(enabled = !showResult) {
                                selectedAnswer = globalIndex
                                showResult = true
                                if (isCorrect) score += 20
                            },
                        colors = CardDefaults.cardColors(containerColor = containerColor),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Box(modifier = Modifier.fillMaxWidth().padding(16.dp), contentAlignment = Alignment.Center) {
                            Text("$option", style = MaterialTheme.typography.titleLarge, color = OnSurface, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        LaunchedEffect(showResult) {
            if (showResult) {
                delay(1000)
                currentLevel++
                selectedAnswer = -1
                showResult = false
                showHint = false
                if (currentLevel >= levels.size) isComplete = true
            }
        }
    }
}

private fun generatePatternLevels(): List<PatternLevel> = listOf(
    PatternLevel(listOf(2, 4, 6, 8), listOf(9, 10, 11, 12), 1, "Add 2 each time (even numbers)"),
    PatternLevel(listOf(3, 6, 9, 12), listOf(14, 15, 16, 18), 2, "Add 3 each time (multiples of 3)"),
    PatternLevel(listOf(1, 2, 4, 8), listOf(12, 14, 16, 32), 3, "Double each number"),
    PatternLevel(listOf(1, 1, 2, 3, 5), listOf(6, 7, 8, 9), 2, "Fibonacci: add the last two"),
    PatternLevel(listOf(10, 8, 6, 4), listOf(1, 2, 3, 0), 2, "Subtract 2 each time"),
    PatternLevel(listOf(1, 4, 9, 16), listOf(20, 24, 25, 30), 2, "Perfect squares: 1,4,9,16,25..."),
    PatternLevel(listOf(2, 6, 12, 20), listOf(28, 30, 32, 36), 0, "n*(n+1): 2,6,12,20,28..."),
    PatternLevel(listOf(1, 3, 7, 15), listOf(28, 30, 31, 32), 2, "Each = prev*2+1"),
    PatternLevel(listOf(0, 1, 1, 2, 3, 5, 8), listOf(10, 11, 12, 13), 3, "Fibonacci: 0,1,1,2,3,5,8,13"),
    PatternLevel(listOf(1, 8, 27, 64), listOf(100, 125, 216, 128), 1, "Perfect cubes: 1,8,27,64,125..."),
    PatternLevel(listOf(2, 3, 5, 7, 11), listOf(12, 13, 14, 15), 1, "Prime numbers"),
    PatternLevel(listOf(1, 2, 6, 24, 120), listOf(480, 600, 720, 720), 2, "Factorials: 1,2,6,24,120,720")
)
