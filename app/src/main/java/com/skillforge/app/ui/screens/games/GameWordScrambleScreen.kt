package com.skillforge.app.ui.screens.games

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
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

private data class WordEntry(val word: String, val hint: String)

private val easyWords = listOf(
    WordEntry("cat", "A small furry pet"),
    WordEntry("dog", "A loyal pet companion"),
    WordEntry("sun", "The bright star in our sky"),
    WordEntry("moon", "Shines at night"),
    WordEntry("fish", "Swims in water"),
    WordEntry("bird", "Has wings and can fly"),
    WordEntry("tree", "Has leaves and branches"),
    WordEntry("book", "You read this"),
    WordEntry("rain", "Falls from clouds"),
    WordEntry("star", "Twinkles in the night sky"),
    WordEntry("lamp", "Provides light indoors"),
    WordEntry("door", "You walk through this"),
    WordEntry("cake", "A sweet dessert"),
    WordEntry("hand", "Part of your body"),
    WordEntry("foot", "You walk on these"),
    WordEntry("blue", "The color of the sky"),
    WordEntry("red", "The color of fire"),
    WordEntry("green", "Color of grass"),
    WordEntry("gold", "A precious metal"),
    WordEntry("silver", "A shiny grey metal"),
    WordEntry("light", "Opposite of dark"),
    WordEntry("dream", "What happens when you sleep"),
    WordEntry("magic", "Sorcery and spells"),
    WordEntry("brave", "Having courage"),
    WordEntry("quick", "Moving fast"),
    WordEntry("jump", "To leap upward"),
    WordEntry("fire", "Produces heat and light"),
    WordEntry("ocean", "A vast body of saltwater"),
    WordEntry("stone", "A hard piece of rock"),
    WordEntry("cloud", "Floats in the sky")
)

private val mediumWords = listOf(
    WordEntry("garden", "Where plants grow"),
    WordEntry("library", "A place full of books"),
    WordEntry("puzzle", "A brain-teasing game"),
    WordEntry("forest", "A large area of trees"),
    WordEntry("bridge", "Crosses over water"),
    WordEntry("castle", "A medieval fortress"),
    WordEntry("dragon", "A mythical fire-breathing creature"),
    WordEntry("mirror", "Shows your reflection"),
    WordEntry("planet", "Orbits a star"),
    WordEntry("secret", "Something hidden"),
    WordEntry("travel", "To go on a journey"),
    WordEntry("window", "Lets light into a room"),
    WordEntry("future", "Time that has not come yet"),
    WordEntry("golden", "Made of or like gold"),
    WordEntry("shadow", "Dark shape cast by light"),
    WordEntry("music", "Sound organized in time"),
    WordEntry("island", "Land surrounded by water"),
    WordEntry("blanket", "Keeps you warm in bed"),
    WordEntry("hammer", "A tool for hitting nails"),
    WordEntry("breeze", "A gentle wind")
)

private val hardWords = listOf(
    WordEntry("algorithm", "A step-by-step procedure"),
    WordEntry("knowledge", "Facts and information gained"),
    WordEntry("discovery", "Finding something new"),
    WordEntry("adventure", "An exciting experience"),
    WordEntry("universe", "Everything that exists"),
    WordEntry("mountain", "A very tall landform"),
    WordEntry("diamond", "The hardest natural material"),
    WordEntry("thunder", "The loud sound after lightning"),
    WordEntry("mystery", "Something unexplained"),
    WordEntry("champion", "A winner of a competition"),
    WordEntry("balanced", "Evenly distributed"),
    WordEntry("colorful", "Full of colors"),
    WordEntry("dynamic", "Full of energy and change"),
    WordEntry("empower", "To give power or authority"),
    WordEntry("faithful", "Loyal and devoted"),
    WordEntry("graceful", "Moving with elegance"),
    WordEntry("strategy", "A plan to achieve a goal"),
    WordEntry("magnetic", "Attracted to metal"),
    WordEntry("elephant", "The largest land animal"),
    WordEntry("umbrella", "Protects from rain")
)

@Composable
fun GameWordScrambleScreen(difficulty: String, navController: NavHostController) {
    val (totalRounds, timePerWord, difficultyMultiplier) = when (difficulty.lowercase()) {
        "easy" -> Triple(8, 30, 1)
        "medium" -> Triple(10, 25, 2)
        "hard" -> Triple(12, 20, 3)
        else -> Triple(8, 30, 1)
    }

    val wordBank = remember {
        when (difficulty.lowercase()) {
            "medium" -> mediumWords
            "hard" -> hardWords
            else -> easyWords
        }
    }

    var currentRound by remember { mutableIntStateOf(0) }
    var currentWord by remember { mutableStateOf(WordEntry("", "")) }
    var scrambledLetters by remember { mutableStateOf(emptyList<Char>()) }
    var selectedIndices by remember { mutableStateOf(emptyList<Int>()) }
    var answerDisplay by remember { mutableStateOf("") }
    var correctCount by remember { mutableIntStateOf(0) }
    var timeLeft by remember { mutableIntStateOf(timePerWord) }
    var score by remember { mutableIntStateOf(0) }
    var xpEarned by remember { mutableIntStateOf(0) }
    var usedWords by remember { mutableStateOf(emptySet<Int>()) }
    var gameComplete by remember { mutableStateOf(false) }

    var showFeedback by remember { mutableStateOf(false) }
    var feedbackCorrect by remember { mutableStateOf(false) }
    var gamePhase by remember { mutableStateOf("waiting") }

    fun pickNextWord(): Boolean {
        val available = wordBank.indices.filter { it !in usedWords }
        if (available.isEmpty() || currentRound >= totalRounds) return false
        val idx = available.random()
        usedWords = usedWords + idx
        currentWord = wordBank[idx]
        val letters = currentWord.word.toList()
        scrambledLetters = letters.shuffled()
        selectedIndices = emptyList()
        answerDisplay = ""
        timeLeft = timePerWord
        showFeedback = false
        feedbackCorrect = false
        return true
    }

    fun advanceToNext() {
        currentRound++
        if (currentRound >= totalRounds || !pickNextWord()) {
            gameComplete = true
            score = correctCount * 10 * difficultyMultiplier
            xpEarned = score / 2
            AppStorage.storage.saveGameResult("Word Scramble", score, xpEarned)
        } else {
            gamePhase = "input"
        }
    }

    fun handleTimeout() {
        showFeedback = true
        feedbackCorrect = false
        gamePhase = "feedback"
    }

    fun handleSubmitAnswer() {
        if (answerDisplay.equals(currentWord.word, ignoreCase = true)) {
            correctCount++
            showFeedback = true
            feedbackCorrect = true
            gamePhase = "feedback"
        } else {
            showFeedback = true
            feedbackCorrect = false
            gamePhase = "feedback"
        }
    }

    LaunchedEffect(Unit) {
        if (pickNextWord()) {
            gamePhase = "input"
        } else {
            gameComplete = true
            score = correctCount * 10 * difficultyMultiplier
            xpEarned = score / 2
            AppStorage.storage.saveGameResult("Word Scramble", score, xpEarned)
        }
    }

    LaunchedEffect(gamePhase, currentRound) {
        when (gamePhase) {
            "input" -> {
                while (timeLeft > 0) {
                    delay(1000L)
                    timeLeft--
                }
                handleTimeout()
            }
            "feedback" -> {
                delay(2000L)
                advanceToNext()
            }
        }
    }

    if (gameComplete) {
        GameCompletionOverlay(
            gameName = "Word Scramble",
            score = score,
            xpEarned = xpEarned,
            message = when {
                score > 200 -> "Amazing!"
                correctCount > totalRounds / 2 -> "Great job!"
                correctCount > 0 -> "Not bad!"
                else -> "Keep practicing!"
            },
            detail = "$correctCount / $totalRounds words unscrambled correctly",
            onPlayAgain = {
                currentRound = 0
                correctCount = 0
                usedWords = emptySet()
                gameComplete = false
                score = 0
                xpEarned = 0
                gamePhase = "waiting"
                if (pickNextWord()) {
                    gamePhase = "input"
                }
            },
            onBack = { navController.popBackStack() }
        )
        return
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Word Scramble", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Primary)
        Text(difficulty.replaceFirstChar { it.uppercase() }, fontSize = 14.sp, color = OnSurfaceVariant)
        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("Round: ${currentRound + 1} / $totalRounds", color = OnSurface, fontSize = 14.sp)
            Text(
                text = "${timeLeft}s",
                color = when {
                    timeLeft > 10 -> OnSurface
                    timeLeft > 5 -> WarningColor
                    else -> ErrorColor
                },
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
        }
        Spacer(modifier = Modifier.height(4.dp))

        LinearProgressIndicator(
            progress = timeLeft.toFloat() / timePerWord,
            modifier = Modifier.fillMaxWidth().height(6.dp).clip(RoundedCornerShape(3.dp)),
            color = when {
                timeLeft > 10 -> Primary
                timeLeft > 5 -> WarningColor
                else -> ErrorColor
            },
            trackColor = SurfaceVariant,
        )
        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Hint: ${currentWord.hint}",
            fontSize = 14.sp,
            color = InfoColor,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(12.dp))

        Row(
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth(),
        ) {
            repeat(currentWord.word.length) { idx ->
                val displayChar = if (idx < answerDisplay.length) answerDisplay[idx].toString() else "_"
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Surface)
                        .border(
                            2.dp,
                            if (idx < answerDisplay.length) Primary else OnSurfaceVariant.copy(alpha = 0.3f),
                            RoundedCornerShape(8.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = displayChar,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (idx < answerDisplay.length) Primary else OnSurfaceVariant.copy(alpha = 0.3f)
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(16.dp))

        Row(
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            modifier = Modifier.fillMaxWidth(),
        ) {
            scrambledLetters.forEachIndexed { displayIdx, char ->
                val isUsed = displayIdx in selectedIndices
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (isUsed) Primary.copy(alpha = 0.3f) else SurfaceVariant)
                        .border(
                            2.dp,
                            if (isUsed) Primary.copy(alpha = 0.3f) else OnSurfaceVariant.copy(alpha = 0.5f),
                            RoundedCornerShape(8.dp)
                        )
                        .then(
                            if (!isUsed && gamePhase == "input") Modifier.clickable {
                                selectedIndices = selectedIndices + displayIdx
                                answerDisplay = answerDisplay + char
                                if (answerDisplay.length == currentWord.word.length) {
                                    handleSubmitAnswer()
                                }
                            } else Modifier
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = char.toString(),
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isUsed) OnSurfaceVariant.copy(alpha = 0.3f) else OnSurface
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(16.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(
                onClick = {
                    if (selectedIndices.isNotEmpty()) {
                        selectedIndices = selectedIndices.dropLast(1)
                        answerDisplay = answerDisplay.dropLast(1)
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = ErrorColor.copy(alpha = 0.7f)),
                enabled = selectedIndices.isNotEmpty() && gamePhase == "input",
                shape = RoundedCornerShape(8.dp),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
            ) { Text("Backspace", fontSize = 13.sp) }
        }

        if (showFeedback) {
            Spacer(modifier = Modifier.height(16.dp))
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = if (feedbackCorrect) SuccessColor.copy(alpha = 0.15f) else ErrorColor.copy(alpha = 0.15f)
                ),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = if (feedbackCorrect) "Correct!" else "Wrong!",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (feedbackCorrect) SuccessColor else ErrorColor
                    )
                    if (!feedbackCorrect) {
                        Text(
                            text = "Answer: ${currentWord.word}",
                            fontSize = 16.sp,
                            color = OnSurface
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.weight(1f))
        Text(
            text = "Score: ${correctCount * 10 * difficultyMultiplier}",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = OnSurface
        )
    }
}

@Composable
private fun GameCompletionOverlay(
    gameName: String,
    score: Int,
    xpEarned: Int,
    message: String,
    detail: String,
    onPlayAgain: () -> Unit,
    onBack: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = if (score > 0) "\uD83C\uDF89" else "\uD83D\uDE22", fontSize = 64.sp)
        Spacer(modifier = Modifier.height(16.dp))
        Text(text = message, fontSize = 28.sp, fontWeight = FontWeight.Bold, color = if (score > 0) SuccessColor else ErrorColor)
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = detail, fontSize = 14.sp, color = OnSurfaceVariant, textAlign = TextAlign.Center)
        Spacer(modifier = Modifier.height(24.dp))

        Card(
            colors = CardDefaults.cardColors(containerColor = Surface),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("Score", fontSize = 14.sp, color = OnSurfaceVariant)
                Text("$score", fontSize = 36.sp, fontWeight = FontWeight.Bold, color = Primary)
                Spacer(modifier = Modifier.height(8.dp))
                Text("XP Earned: +$xpEarned", fontSize = 16.sp, color = SuccessColor)
            }
        }
        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = onPlayAgain,
            colors = ButtonDefaults.buttonColors(containerColor = Primary),
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp)
        ) { Text("Play Again", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.Black) }
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedButton(
            onClick = onBack,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp)
        ) { Text("Back to Games", fontSize = 16.sp, color = OnSurface) }
    }
}
