package com.skillforge.app.ui.screens.games

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
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

private val gameColors = listOf(
    Color(0xFFE53935), // Red
    Color(0xFF43A047), // Green
    Color(0xFF1E88E5), // Blue
    Color(0xFFFDD835)  // Yellow
)

private val buttonPositions = listOf(
    "Top" to Modifier.offset(x = (-50).dp, y = 0.dp),
    "Right" to Modifier.offset(x = 50.dp, y = 0.dp),
    "Bottom" to Modifier.offset(x = 0.dp, y = 50.dp),
    "Left" to Modifier.offset(x = (-50).dp, y = 0.dp)
)

enum class SimonState { SHOWING, PLAYER_TURN, GAME_OVER }

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SimonSaysGame(
    onBack: () -> Unit,
    difficulty: String = "Normal",
    onGameComplete: (xpEarned: Int, score: Int) -> Unit = { _, _ -> }
) {
    var sequence by remember { mutableStateOf(listOf(Random.nextInt(4)) ) }
    var playerInput by remember { mutableStateOf(listOf<Int>()) }
    var state by remember { mutableStateOf(SimonState.SHOWING) }
    var score by remember { mutableIntStateOf(0) }
    var highScore by remember { mutableIntStateOf(0) }
    var highlightedButton by remember { mutableIntStateOf(-1) }
    var round by remember { mutableIntStateOf(1) }

    // Show the sequence
    LaunchedEffect(sequence, state) {
        if (state == SimonState.SHOWING) {
            delay(500)
            for (i in sequence.indices) {
                highlightedButton = sequence[i]
                delay(500)
                highlightedButton = -1
                delay(200)
            }
            state = SimonState.PLAYER_TURN
        }
    }

    fun handlePlayerInput(index: Int) {
        if (state != SimonState.PLAYER_TURN) return
        highlightedButton = index
        playerInput = playerInput + index

        val currentStep = playerInput.size - 1
        if (playerInput[currentStep] != sequence[currentStep]) {
            state = SimonState.GAME_OVER
            if (score > highScore) highScore = score
            onGameComplete(score, round - 1)
            return
        }

        if (playerInput.size == sequence.size) {
            score += 10 * round
            round++
            playerInput = emptyList()
            state = SimonState.SHOWING
            sequence = sequence + Random.nextInt(4)
        }
    }

    Column(
        modifier = Modifier.fillMaxSize().background(Background).padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onBack) { Icon(Icons.Filled.ArrowBack, "Back", tint = OnSurface) }
            Column(modifier = Modifier.weight(1f)) {
                Text("Simon Says", style = MaterialTheme.typography.titleLarge, color = GeneralKnowledgeColor, fontWeight = FontWeight.Bold)
                Text("Watch, remember, repeat!", style = MaterialTheme.typography.bodySmall, color = OnSurfaceVariant)
            }
        }

        // Score display
        Row(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp), horizontalArrangement = Arrangement.SpaceEvenly) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("$round", style = MaterialTheme.typography.headlineSmall, color = GeneralKnowledgeColor, fontWeight = FontWeight.Bold)
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

        Spacer(Modifier.weight(1f))

        if (state == SimonState.GAME_OVER) {
            Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = ErrorColor.copy(alpha = 0.1f)), shape = RoundedCornerShape(16.dp)) {
                Column(modifier = Modifier.padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Filled.Close, null, tint = ErrorColor, modifier = Modifier.size(48.dp))
                    Spacer(Modifier.height(8.dp))
                    Text("Game Over!", style = MaterialTheme.typography.headlineSmall, color = OnSurface, fontWeight = FontWeight.Bold)
                    Text("You reached round $round with $score points", style = MaterialTheme.typography.bodyMedium, color = OnSurfaceVariant)
                    Spacer(Modifier.height(8.dp))
                    Text("+$score XP", style = MaterialTheme.typography.titleMedium, color = Primary, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(16.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        Button(onClick = onBack, colors = ButtonDefaults.buttonColors(containerColor = Surface), shape = RoundedCornerShape(12.dp)) {
                            Text("Exit", color = OnSurface)
                        }
                        Button(onClick = {
                            sequence = listOf(Random.nextInt(4))
                            playerInput = emptyList()
                            state = SimonState.SHOWING
                            score = 0; round = 1
                        }, colors = ButtonDefaults.buttonColors(containerColor = GeneralKnowledgeColor), shape = RoundedCornerShape(12.dp)) {
                            Text("Retry", color = OnPrimary)
                        }
                    }
                }
            }
        } else {
            // Status
            val statusText = when (state) {
                SimonState.SHOWING -> "Watch the pattern..."
                SimonState.PLAYER_TURN -> "Your turn! Tap the colors"
                else -> ""
            }
            Text(statusText, style = MaterialTheme.typography.bodyMedium, color = OnSurfaceVariant)

            Spacer(Modifier.height(16.dp))

            // Simon buttons - 2x2 grid
            Column(
                modifier = Modifier.size(220.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Top row
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    SimonButton(
                        color = gameColors[0],
                        isHighlighted = highlightedButton == 0,
                        onClick = { handlePlayerInput(0) }
                    )
                    SimonButton(
                        color = gameColors[1],
                        isHighlighted = highlightedButton == 1,
                        onClick = { handlePlayerInput(1) }
                    )
                }
                // Bottom row
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    SimonButton(
                        color = gameColors[2],
                        isHighlighted = highlightedButton == 2,
                        onClick = { handlePlayerInput(2) }
                    )
                    SimonButton(
                        color = gameColors[3],
                        isHighlighted = highlightedButton == 3,
                        onClick = { handlePlayerInput(3) }
                    )
                }
            }

            // Player progress dots
            if (state == SimonState.PLAYER_TURN) {
                Spacer(Modifier.height(24.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    sequence.forEachIndexed { index, _ ->
                        Box(
                            modifier = Modifier
                                .size(10.dp)
                                .clip(CircleShape)
                                .background(if (index < playerInput.size) SuccessColor else SurfaceVariant)
                        )
                    }
                }
            }
        }

        Spacer(Modifier.weight(1f))
    }
}

@Composable
fun SimonButton(color: Color, isHighlighted: Boolean, onClick: () -> Unit) {
    val scale by animateFloatAsState(
        targetValue = if (isHighlighted) 1.1f else 1f,
        animationSpec = spring(dampingRatio = 0.3f, stiffness = 300f), label = "simon"
    )
    val alpha = if (isHighlighted) 1f else 0.5f

    Box(
        modifier = Modifier
            .size(100.dp)
            .scale(scale)
            .clip(RoundedCornerShape(16.dp))
            .background(color.copy(alpha = alpha))
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        if (isHighlighted) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.3f))
            )
        }
    }
}
