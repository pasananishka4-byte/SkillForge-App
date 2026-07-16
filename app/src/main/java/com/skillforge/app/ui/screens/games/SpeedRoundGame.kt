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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.skillforge.app.ui.theme.*
import kotlinx.coroutines.delay
import kotlin.random.Random

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SpeedRoundGame(onBack: () -> Unit) {
    val questions = remember { generateSpeedQuestions().shuffled().take(15) }
    var currentIndex by remember { mutableIntStateOf(0) }
    var score by remember { mutableIntStateOf(0) }
    var streak by remember { mutableIntStateOf(0) }
    var timeLeft by remember { mutableFloatStateOf(30f) }
    var isStarted by remember { mutableStateOf(false) }
    var isComplete by remember { mutableStateOf(false) }
    var selectedAnswer by remember { mutableIntStateOf(-1) }
    var showResult by remember { mutableStateOf(false) }
    var correctCount by remember { mutableIntStateOf(0) }

    LaunchedEffect(isStarted, isComplete) {
        if (isStarted && !isComplete) {
            while (timeLeft > 0 && currentIndex < questions.size) {
                delay(50)
                timeLeft -= 0.05f
            }
            if (timeLeft <= 0) isComplete = true
        }
    }

    Column(
        modifier = Modifier.fillMaxSize().background(Background).padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onBack) { Icon(Icons.Filled.ArrowBack, "Back", tint = OnSurface) }
            Column(modifier = Modifier.weight(1f)) {
                Text("Speed Round", style = MaterialTheme.typography.titleLarge, color = StreakFire, fontWeight = FontWeight.Bold)
                Text("${30}s timer - answer fast!", style = MaterialTheme.typography.bodySmall, color = OnSurfaceVariant)
            }
        }

        if (!isStarted) {
            Spacer(Modifier.height(48.dp))
            Icon(Icons.Filled.Bolt, null, tint = StreakFire, modifier = Modifier.size(80.dp))
            Spacer(Modifier.height(16.dp))
            Text("Ready to test your speed?", style = MaterialTheme.typography.headlineSmall, color = OnSurface, textAlign = TextAlign.Center)
            Spacer(Modifier.height(8.dp))
            Text("Answer as many questions as you can in 30 seconds.\nStreak bonuses for consecutive correct answers!", style = MaterialTheme.typography.bodyMedium, color = OnSurfaceVariant, textAlign = TextAlign.Center)
            Spacer(Modifier.height(32.dp))
            Button(
                onClick = { isStarted = true },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = StreakFire),
                shape = RoundedCornerShape(16.dp)
            ) { Text("Start!", style = MaterialTheme.typography.titleMedium, color = OnPrimary, fontWeight = FontWeight.Bold) }
            return
        }

        // Timer bar
        LinearProgressIndicator(
            progress = timeLeft / 30f,
            modifier = Modifier.fillMaxWidth().height(8.dp).clip(RoundedCornerShape(4.dp)),
            color = if (timeLeft > 10) StreakFire else ErrorColor,
            trackColor = SurfaceVariant,
        )

        Spacer(Modifier.height(4.dp))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text("${timeLeft.toInt()}s", style = MaterialTheme.typography.labelLarge, color = if (timeLeft > 10) OnSurface else ErrorColor)
            Text("Score: $score", style = MaterialTheme.typography.labelLarge, color = Primary)
            Text("Streak: $streak", style = MaterialTheme.typography.labelLarge, color = if (streak >= 3) StreakFire else OnSurfaceVariant)
        }

        if (isComplete) {
            Spacer(Modifier.height(24.dp))
            Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = Surface), shape = RoundedCornerShape(16.dp)) {
                Column(modifier = Modifier.padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Filled.EmojiEvents, null, tint = StreakFire, modifier = Modifier.size(56.dp))
                    Spacer(Modifier.height(8.dp))
                    Text("Time's Up!", style = MaterialTheme.typography.headlineSmall, color = OnSurface, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(16.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("$score", style = MaterialTheme.typography.headlineMedium, color = Primary, fontWeight = FontWeight.Bold)
                            Text("Score", style = MaterialTheme.typography.labelSmall, color = OnSurfaceVariant)
                        }
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("$correctCount/${questions.size}", style = MaterialTheme.typography.headlineMedium, color = SuccessColor, fontWeight = FontWeight.Bold)
                            Text("Correct", style = MaterialTheme.typography.labelSmall, color = OnSurfaceVariant)
                        }
                    }
                    Spacer(Modifier.height(12.dp))
                    Text("+${score} XP", style = MaterialTheme.typography.titleLarge, color = Primary, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(16.dp))
                    Button(onClick = onBack, modifier = Modifier.fillMaxWidth(), colors = ButtonDefaults.buttonColors(containerColor = StreakFire), shape = RoundedCornerShape(12.dp)) {
                        Text("Done", color = OnPrimary, fontWeight = FontWeight.Bold)
                    }
                }
            }
            return
        }

        Spacer(Modifier.height(16.dp))

        if (currentIndex < questions.size) {
            val q = questions[currentIndex]
            Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = Surface), shape = RoundedCornerShape(16.dp)) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(q.question, style = MaterialTheme.typography.bodyLarge, color = OnSurface, lineHeight = 24.sp)
                }
            }

            Spacer(Modifier.height(12.dp))

            q.options.forEachIndexed { index, option ->
                val isCorrect = index == q.correctIndex
                val containerColor = when {
                    showResult && isCorrect -> SuccessColor.copy(alpha = 0.2f)
                    showResult && index == selectedAnswer && !isCorrect -> ErrorColor.copy(alpha = 0.2f)
                    index == selectedAnswer -> Primary.copy(alpha = 0.15f)
                    else -> Surface
                }

                Card(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
                        .clickable(enabled = !showResult) {
                            selectedAnswer = index
                            showResult = true
                            if (isCorrect) {
                                streak++
                                val streakBonus = if (streak >= 5) 3 else if (streak >= 3) 2 else 1
                                score += 10 * streakBonus
                                correctCount++
                            } else {
                                streak = 0
                            }
                        },
                    colors = CardDefaults.cardColors(containerColor = containerColor),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(modifier = Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
                        Text(option, style = MaterialTheme.typography.bodyMedium, color = OnSurface)
                    }
                }
            }

            LaunchedEffect(showResult) {
                if (showResult) {
                    delay(400)
                    currentIndex++
                    selectedAnswer = -1
                    showResult = false
                    if (currentIndex >= questions.size) isComplete = true
                }
            }
        }
    }
}

private data class SpeedQuestion(val question: String, val options: List<String>, val correctIndex: Int)

private fun generateSpeedQuestions(): List<SpeedQuestion> = listOf(
    SpeedQuestion("What is 7 x 8?", listOf("54", "56", "48", "64"), 1),
    SpeedQuestion("Capital of France?", listOf("London", "Berlin", "Paris", "Madrid"), 2),
    SpeedQuestion("Largest planet?", listOf("Earth", "Mars", "Jupiter", "Saturn"), 2),
    SpeedQuestion("H2O is?", listOf("Hydrogen", "Oxygen", "Water", "Carbon"), 2),
    SpeedQuestion("Fastest land animal?", listOf("Lion", "Cheetah", "Horse", "Gazelle"), 1),
    SpeedQuestion("How many continents?", listOf("5", "6", "7", "8"), 2),
    SpeedQuestion("Square root of 144?", listOf("10", "11", "12", "14"), 2),
    SpeedQuestion("Who wrote Hamlet?", listOf("Dickens", "Shakespeare", "Austen", "Twain"), 1),
    SpeedQuestion("Speed of light is approx?", listOf("300K km/s", "150K km/s", "500K km/s", "100K km/s"), 0),
    SpeedQuestion("Chemical symbol for Iron?", listOf("Ir", "Fe", "In", "Io"), 1),
    SpeedQuestion("Largest ocean?", listOf("Atlantic", "Indian", "Pacific", "Arctic"), 2),
    SpeedQuestion("Year WWII ended?", listOf("1944", "1945", "1946", "1943"), 1),
    SpeedQuestion("Boiling point of water F?", listOf("200F", "212F", "220F", "180F"), 1),
    SpeedQuestion("Smallest prime number?", listOf("0", "1", "2", "3"), 2),
    SpeedQuestion("Currency of Japan?", listOf("Yuan", "Won", "Yen", "Ringgit"), 2),
    SpeedQuestion("How many bones in adult?", listOf("186", "206", "226", "196"), 1),
    SpeedQuestion("Red planet?", listOf("Venus", "Mars", "Jupiter", "Mercury"), 1),
    SpeedQuestion("Gravity m/s2?", listOf("8.9", "9.8", "10.8", "7.8"), 1),
    SpeedQuestion("Hardness scale top?", listOf("9", "10", "11", "12"), 1),
    SpeedQuestion("Closest star?", listOf("Sirius", "Sun", "Alpha Centauri", "Polaris"), 1)
)
