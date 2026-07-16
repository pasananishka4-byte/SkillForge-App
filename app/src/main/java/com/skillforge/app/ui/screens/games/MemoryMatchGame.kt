package com.skillforge.app.ui.screens.games

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.skillforge.app.ui.theme.*
import kotlinx.coroutines.delay
import kotlin.random.Random

data class MemoryCard(
    val id: Int,
    val symbol: String,
    val color: Color,
    var isFlipped: Boolean = false,
    var isMatched: Boolean = false
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MemoryMatchGame(
    onBack: () -> Unit,
    difficulty: String = "Normal",
    onGameComplete: (xpEarned: Int, score: Int) -> Unit = { _, _ -> }
) {
    val pairCount = when (difficulty) { "Easy" -> 6; "Hard" -> 12; else -> 8 }
    var cards by remember { mutableStateOf(generateCards(pairCount)) }
    var flippedIndices by remember { mutableStateOf(listOf<Int>()) }
    var moves by remember { mutableIntStateOf(0) }
    var matches by remember { mutableIntStateOf(0) }
    var isProcessing by remember { mutableStateOf(false) }
    var gameComplete by remember { mutableStateOf(false) }
    var timer by remember { mutableIntStateOf(0) }
    val totalPairs = cards.size / 2

    LaunchedEffect(gameComplete) {
        if (!gameComplete) {
            while (true) {
                delay(1000)
                timer++
            }
        }
    }

    LaunchedEffect(flippedIndices) {
        if (flippedIndices.size == 2) {
            isProcessing = true
            delay(800)
            val (i1, i2) = flippedIndices
            if (cards[i1].symbol == cards[i2].symbol) {
                cards = cards.toMutableList().apply {
                    this[i1] = this[i1].copy(isMatched = true)
                    this[i2] = this[i2].copy(isMatched = true)
                }
                matches++
                if (matches == totalPairs) {
                    gameComplete = true
                    val xp = calculateMemoryXP(moves, timer)
                    onGameComplete(xp, moves)
                }
            } else {
                cards = cards.toMutableList().apply {
                    this[i1] = this[i1].copy(isFlipped = false)
                    this[i2] = this[i2].copy(isFlipped = false)
                }
            }
            flippedIndices = emptyList()
            isProcessing = false
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(Icons.Filled.ArrowBack, "Back", tint = OnSurface)
            }
            Column(modifier = Modifier.weight(1f)) {
                Text("Memory Match", style = MaterialTheme.typography.titleLarge, color = Secondary, fontWeight = FontWeight.Bold)
                Text("Find all matching pairs", style = MaterialTheme.typography.bodySmall, color = OnSurfaceVariant)
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            GameStat("Time", "${timer}s", Primary)
            GameStat("Moves", "$moves", Secondary)
            GameStat("Pairs", "$matches/$totalPairs", SuccessColor)
        }

        if (gameComplete) {
            Card(
                modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp),
                colors = CardDefaults.cardColors(containerColor = SuccessColor.copy(alpha = 0.15f)),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Filled.EmojiEvents, null, tint = Primary, modifier = Modifier.size(48.dp))
                    Spacer(Modifier.height(8.dp))
                    Text("You Win!", style = MaterialTheme.typography.headlineSmall, color = SuccessColor, fontWeight = FontWeight.Bold)
                    Text("${moves} moves in ${timer}s", style = MaterialTheme.typography.bodyMedium, color = OnSurfaceVariant)
                    val xpEarned = calculateMemoryXP(moves, timer)
                    Text("+$xpEarned XP", style = MaterialTheme.typography.titleMedium, color = Primary, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(12.dp))
                    Button(
                        onClick = {
                            cards = generateCards(pairCount)
                            flippedIndices = emptyList()
                            moves = 0; matches = 0; timer = 0; gameComplete = false
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Secondary)
                    ) { Text("Play Again", color = OnPrimary) }
                }
            }
        }

        LazyVerticalGrid(
            columns = GridCells.Fixed(4),
            modifier = Modifier.weight(1f),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(cards.size) { index ->
                val card = cards[index]
                val isClickable = !card.isFlipped && !card.isMatched && flippedIndices.size < 2 && !isProcessing

                val scale by animateFloatAsState(
                    targetValue = if (card.isFlipped || card.isMatched) 1f else 0.95f,
                    animationSpec = tween(200), label = "flip"
                )

                Box(
                    modifier = Modifier
                        .aspectRatio(0.75f)
                        .scale(scale)
                        .clip(RoundedCornerShape(12.dp))
                        .background(
                            when {
                                card.isMatched -> card.color.copy(alpha = 0.3f)
                                card.isFlipped -> card.color.copy(alpha = 0.2f)
                                else -> SurfaceVariant
                            }
                        )
                        .clickable(enabled = isClickable) {
                            cards = cards.toMutableList().apply {
                                this[index] = this[index].copy(isFlipped = true)
                            }
                            flippedIndices = flippedIndices + index
                            moves = (flippedIndices.size / 2) + (matches * 0).let { moves + if (flippedIndices.size % 2 == 0) 1 else 0 }
                        },
                    contentAlignment = Alignment.Center
                ) {
                    AnimatedContent(
                        targetState = card.isFlipped || card.isMatched,
                        transitionSpec = {
                            if (targetState) {
                                fadeIn(tween(200)) togetherWith fadeOut(tween(200))
                            } else {
                                fadeIn(tween(200)) togetherWith fadeOut(tween(200))
                            }
                        },
                        label = "card"
                    ) { isRevealed ->
                        if (isRevealed) {
                            Text(text = card.symbol, fontSize = 28.sp, textAlign = TextAlign.Center)
                        } else {
                            Icon(
                                Icons.Filled.QuestionMark, null,
                                tint = OnSurfaceVariant.copy(alpha = 0.5f),
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun GameStat(label: String, value: String, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = value, style = MaterialTheme.typography.titleMedium, color = color, fontWeight = FontWeight.Bold)
        Text(text = label, style = MaterialTheme.typography.labelSmall, color = OnSurfaceVariant)
    }
}

private fun calculateMemoryXP(moves: Int, time: Int): Int {
    val base = 30
    val moveBonus = if (moves <= 12) 20 else if (moves <= 18) 10 else 0
    val timeBonus = if (time <= 30) 20 else if (time <= 60) 10 else 0
    return base + moveBonus + timeBonus
}

private fun generateCards(pairCount: Int = 8): List<MemoryCard> {
    val symbols = listOf("🧠", "⚡", "🔥", "💎", "🎯", "🌟", "🚀", "🎨", "🎵", "🔮", "🦋", "🌈")
    val colors = listOf(Primary, Secondary, CriticalThinkingColor, GeneralKnowledgeColor, MetaLearningColor, SocialEmotionalColor, StreakFire, ExpertColor, EasyColor, InfoColor, WarningColor, SuccessColor)
    val selected = symbols.zip(colors).take(pairCount)
    val pairs = selected.flatMap { (s, c) ->
        listOf(MemoryCard(Random.nextInt(), s, c), MemoryCard(Random.nextInt(), s, c))
    }
    return pairs.shuffled()
}
