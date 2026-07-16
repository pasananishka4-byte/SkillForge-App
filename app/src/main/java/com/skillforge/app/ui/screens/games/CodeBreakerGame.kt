package com.skillforge.app.ui.screens.games

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
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
import kotlin.random.Random

private val pegColors = listOf(
    Color(0xFFE53935), Color(0xFF43A047), Color(0xFF1E88E5),
    Color(0xFFFDD835), Color(0xFF8E24AA), Color(0xFFFF6D00)
)
private val pegColorNames = listOf("Red", "Green", "Blue", "Yellow", "Purple", "Orange")

private data class CodeGuess(
    val colors: List<Int>,
    val correctPosition: Int,
    val correctColor: Int
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CodeBreakerGame(
    onBack: () -> Unit,
    difficulty: String = "Normal",
    onGameComplete: (xpEarned: Int, score: Int) -> Unit = { _, _ -> }
) {
    val codeLength = when (difficulty) { "Easy" -> 4; "Hard" -> 6; else -> 5 }
    val maxGuesses = when (difficulty) { "Easy" -> 12; "Hard" -> 8; else -> 10 }
    val colorCount = when (difficulty) { "Easy" -> 4; "Hard" -> 8; else -> 6 }

    val secretCode = remember {
        List(codeLength) { Random.nextInt(colorCount) }
    }

    var guesses by remember { mutableStateOf<List<CodeGuess>>(emptyList()) }
    var currentPegs by remember { mutableStateOf(mutableListOf<Int>()) }
    var isComplete by remember { mutableStateOf(false) }
    var hasWon by remember { mutableStateOf(false) }

    fun evaluateGuess(guess: List<Int>): CodeGuess {
        var correctPos = 0
        var correctCol = 0
        val secretUsed = BooleanArray(codeLength)
        val guessUsed = BooleanArray(codeLength)

        for (i in guess.indices) {
            if (guess[i] == secretCode[i]) {
                correctPos++
                secretUsed[i] = true
                guessUsed[i] = true
            }
        }
        for (i in guess.indices) {
            if (!guessUsed[i]) {
                for (j in secretCode.indices) {
                    if (!secretUsed[j] && guess[i] == secretCode[j]) {
                        correctCol++
                        secretUsed[j] = true
                        break
                    }
                }
            }
        }
        return CodeGuess(guess, correctPos, correctCol)
    }

    fun submitGuess() {
        if (currentPegs.size != codeLength) return
        val result = evaluateGuess(currentPegs.toList())
        val newGuesses = guesses + result
        guesses = newGuesses

        if (result.correctPosition == codeLength) {
            hasWon = true
            isComplete = true
            val base = 50
            val diffBonus = when (difficulty) { "Hard" -> 30; "Easy" -> 0; else -> 15 }
            val guessBonus = maxOf(0, (maxGuesses - newGuesses.size) * 5)
            onGameComplete(base + diffBonus + guessBonus, newGuesses.size)
        } else if (newGuesses.size >= maxGuesses) {
            isComplete = true
            onGameComplete(10, 0)
        }
        currentPegs = mutableListOf()
    }

    Column(
        modifier = Modifier.fillMaxSize().background(Background).padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onBack) { Icon(Icons.Filled.ArrowBack, "Back", tint = OnSurface) }
            Column(modifier = Modifier.weight(1f)) {
                Text("Code Breaker", style = MaterialTheme.typography.titleLarge, color = CriticalThinkingColor, fontWeight = FontWeight.Bold)
                Text("$difficulty - Crack the $codeLength-color code", style = MaterialTheme.typography.bodySmall, color = OnSurfaceVariant)
            }
            Surface(shape = RoundedCornerShape(8.dp), color = Primary.copy(alpha = 0.15f)) {
                Text("${guesses.size}/$maxGuesses", style = MaterialTheme.typography.labelMedium, color = Primary, modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp))
            }
        }

        if (isComplete) {
            Card(
                modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp),
                colors = CardDefaults.cardColors(containerColor = if (hasWon) SuccessColor.copy(alpha = 0.15f) else ErrorColor.copy(alpha = 0.1f)),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        if (hasWon) Icons.Filled.EmojiEvents else Icons.Filled.Lock,
                        null, tint = if (hasWon) Primary else ErrorColor,
                        modifier = Modifier.size(48.dp)
                    )
                    Spacer(Modifier.height(8.dp))
                    Text(
                        if (hasWon) "Code Cracked!" else "Out of Guesses!",
                        style = MaterialTheme.typography.headlineSmall,
                        color = if (hasWon) SuccessColor else ErrorColor,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(Modifier.height(8.dp))
                    Text("The code was:", style = MaterialTheme.typography.bodyMedium, color = OnSurfaceVariant)
                    Row(modifier = Modifier.padding(vertical = 8.dp), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        secretCode.forEach { colorIdx ->
                            Box(modifier = Modifier.size(28.dp).clip(CircleShape).background(pegColors[colorIdx]))
                        }
                    }
                }
            }
            return
        }

        // Guess history
        LazyColumn(
            modifier = Modifier.weight(1f).fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(6.dp),
            contentPadding = PaddingValues(vertical = 8.dp)
        ) {
            itemsIndexed(guesses) { index, guess ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Surface),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(10.dp).fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("${index + 1}.", style = MaterialTheme.typography.labelMedium, color = OnSurfaceVariant)
                        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            guess.colors.forEach { colorIdx ->
                                Box(modifier = Modifier.size(24.dp).clip(CircleShape).background(pegColors[colorIdx]))
                            }
                        }
                        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            repeat(guess.correctPosition) {
                                Box(modifier = Modifier.size(10.dp).clip(CircleShape).background(SuccessColor))
                            }
                            repeat(guess.correctColor) {
                                Box(modifier = Modifier.size(10.dp).clip(CircleShape).background(WarningColor))
                            }
                            repeat(codeLength - guess.correctPosition - guess.correctColor) {
                                Box(modifier = Modifier.size(10.dp).clip(CircleShape).background(SurfaceVariant))
                            }
                        }
                    }
                }
            }
        }

        // Current guess display
        if (!isComplete) {
            Card(
                modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                colors = CardDefaults.cardColors(containerColor = Surface),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(modifier = Modifier.padding(12.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Your guess:", style = MaterialTheme.typography.labelMedium, color = OnSurfaceVariant)
                    Spacer(Modifier.height(8.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        for (i in 0 until codeLength) {
                            val pegColor = if (i < currentPegs.size) pegColors[currentPegs[i]] else null
                            Box(
                                modifier = Modifier.size(36.dp).clip(CircleShape)
                                    .then(
                                        if (pegColor != null) Modifier.background(pegColor)
                                        else Modifier.border(2.dp, SurfaceVariant, CircleShape)
                                    )
                                    .clickable(enabled = !isComplete) {
                                        if (i < currentPegs.size) {
                                            val newList = currentPegs.toMutableList()
                                            newList[i] = (newList[i] + 1) % colorCount
                                            currentPegs = newList
                                        }
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                if (pegColor == null) {
                                    Text("${i + 1}", style = MaterialTheme.typography.labelSmall, color = OnSurfaceVariant)
                                }
                            }
                        }
                    }

                    Spacer(Modifier.height(8.dp))

                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        for (c in 0 until colorCount) {
                            val idx = c
                            Box(
                                modifier = Modifier.size(28.dp).clip(CircleShape).background(pegColors[idx])
                                    .clickable(enabled = !isComplete && currentPegs.size < codeLength) {
                                        currentPegs = (currentPegs + idx).toMutableList()
                                    }
                            )
                        }
                    }

                    Spacer(Modifier.height(8.dp))

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedButton(
                            onClick = { if (currentPegs.isNotEmpty()) currentPegs = currentPegs.dropLast(1).toMutableList() },
                            enabled = currentPegs.isNotEmpty() && !isComplete
                        ) { Icon(Icons.Filled.Backspace, null, modifier = Modifier.size(18.dp)); Spacer(Modifier.width(4.dp)); Text("Undo") }

                        Button(
                            onClick = { submitGuess() },
                            enabled = currentPegs.size == codeLength && !isComplete,
                            colors = ButtonDefaults.buttonColors(containerColor = CriticalThinkingColor)
                        ) { Text("Submit", color = OnPrimary, fontWeight = FontWeight.Bold) }
                    }
                }
            }

            // Legend
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(SuccessColor))
                    Spacer(Modifier.width(4.dp))
                    Text("Right place", style = MaterialTheme.typography.labelSmall, color = OnSurfaceVariant)
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(WarningColor))
                    Spacer(Modifier.width(4.dp))
                    Text("Right color", style = MaterialTheme.typography.labelSmall, color = OnSurfaceVariant)
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(SurfaceVariant))
                    Spacer(Modifier.width(4.dp))
                    Text("Wrong", style = MaterialTheme.typography.labelSmall, color = OnSurfaceVariant)
                }
            }
        }
    }
}
