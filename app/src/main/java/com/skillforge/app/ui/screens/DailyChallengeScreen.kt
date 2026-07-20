package com.skillforge.app.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Divider
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.skillforge.app.data.*
import com.skillforge.app.ui.AppStorage
import com.skillforge.app.ui.Screen
import com.skillforge.app.ui.theme.*
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DailyChallengeScreen(
    navController: NavHostController,
) {
    val today = remember {
        SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Date())
    }

    val isCompleted = remember { mutableStateOf(false) }
    val streak = remember { mutableIntStateOf(0) }

    LaunchedEffect(Unit) {
        val storage = AppStorage.storage
        isCompleted.value = storage.getDailyChallengeDates().contains(today)

        val dates = storage.getDailyChallengeDates().sorted().reversed()
        var currentStreak = 0
        val cal = Calendar.getInstance()

        for (dateStr in dates) {
            try {
                val date = SimpleDateFormat("yyyy-MM-dd", Locale.US).parse(dateStr)
                if (date != null) {
                    val expected = Calendar.getInstance()
                    expected.time = cal.time
                    expected.add(Calendar.DAY_OF_YEAR, -currentStreak)

                    val expectedStr = SimpleDateFormat("yyyy-MM-dd", Locale.US).format(expected.time)
                    if (dateStr == expectedStr) {
                        currentStreak++
                    } else {
                        break
                    }
                }
            } catch (_: Exception) {
                break
            }
        }
        streak.intValue = currentStreak
    }

    if (isCompleted.value) {
        DailyCompletedScreen(
            navController = navController,
            streak = streak.intValue
        )
        return
    }

    val dailyChallenges = remember {
        SeedData.challenges.shuffled().take(5)
    }

    var currentIndex by remember { mutableIntStateOf(0) }
    var score by remember { mutableIntStateOf(0) }
    var totalAnswered by remember { mutableIntStateOf(0) }
    var selectedAnswer by remember { mutableStateOf<String?>(null) }
    var isAnswerRevealed by remember { mutableStateOf(false) }
    var showCompletion by remember { mutableStateOf(false) }
    var showCorrectMessage by remember { mutableStateOf(false) }
    var showWrongMessage by remember { mutableStateOf(false) }
    var wrongExplanation by remember { mutableStateOf("") }

    if (showCompletion) {
        DailyCompletionScreen(
            navController = navController,
            score = score,
            total = dailyChallenges.size,
            streak = streak.intValue
        )
        return
    }

    val currentChallenge = dailyChallenges[currentIndex]
    val options = remember(currentChallenge) {
        currentChallenge.options.shuffled()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Daily Challenge",
                        color = OnBackground,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = OnBackground
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Background
                )
            )
        },
        containerColor = Background
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 24.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = Icons.Default.DateRange,
                    contentDescription = null,
                    tint = Primary,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = today,
                    fontSize = 14.sp,
                    color = OnSurfaceVariant
                )
                if (streak.intValue > 0) {
                    Spacer(modifier = Modifier.width(16.dp))
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = null,
                        tint = SuccessColor,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "${streak.intValue} day streak",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = SuccessColor
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "${currentIndex + 1} / ${dailyChallenges.size}",
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = OnSurfaceVariant
            )

            Spacer(modifier = Modifier.height(8.dp))

            LinearProgressIndicator(
                progress = (currentIndex + 1).toFloat() / dailyChallenges.size,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp)),
                color = Primary,
                trackColor = SurfaceVariant,
            )

            Spacer(modifier = Modifier.height(32.dp))

            val diffColor = when (currentChallenge.difficulty.lowercase()) {
                "easy" -> EasyColor
                "medium" -> MediumColor
                "hard" -> HardColor
                else -> Primary
            }
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(diffColor.copy(alpha = 0.15f))
                    .padding(horizontal = 12.dp, vertical = 4.dp)
            ) {
                Text(
                    text = currentChallenge.difficulty,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = diffColor
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = currentChallenge.question,
                fontSize = 22.sp,
                fontWeight = FontWeight.SemiBold,
                color = OnBackground,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(32.dp))

            options.forEach { option ->
                val isSelected = selectedAnswer == option
                val isCorrect = option == currentChallenge.options[currentChallenge.correctAnswerIndex]
                val buttonColor = when {
                    isAnswerRevealed && isCorrect -> SuccessColor
                    isAnswerRevealed && isSelected && !isCorrect -> ErrorColor
                    isSelected -> Primary
                    else -> Surface
                }
                val textColor = when {
                    isAnswerRevealed && (isCorrect || isSelected) -> OnBackground
                    isSelected -> OnBackground
                    else -> OnSurface
                }

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp)
                        .clickable(enabled = !isAnswerRevealed) {
                            selectedAnswer = option
                            isAnswerRevealed = true
                            totalAnswered++

                            if (option == currentChallenge.options[currentChallenge.correctAnswerIndex]) {
                                score++
                                showCorrectMessage = true
                                showWrongMessage = false
                            } else {
                                showCorrectMessage = false
                                showWrongMessage = true
                                wrongExplanation = currentChallenge.explanation
                            }
                        },
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = buttonColor)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (isAnswerRevealed && isCorrect) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = "Correct",
                                tint = OnBackground,
                                modifier = Modifier.size(24.dp)
                            )
                        } else if (isAnswerRevealed && isSelected && !isCorrect) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Wrong",
                                tint = OnBackground,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = option,
                            fontSize = 16.sp,
                            color = textColor,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }

            AnimatedVisibility(
                visible = showCorrectMessage,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = SuccessColor.copy(alpha = 0.15f)
                    )
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = null,
                            tint = SuccessColor,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = "Correct!",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = SuccessColor
                        )
                    }
                }
            }

            AnimatedVisibility(
                visible = showWrongMessage,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = ErrorColor.copy(alpha = 0.15f)
                    )
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = null,
                                tint = ErrorColor,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                text = "Incorrect",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = ErrorColor
                            )
                        }
                        if (wrongExplanation.isNotBlank()) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = wrongExplanation,
                                fontSize = 14.sp,
                                color = OnSurfaceVariant
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }

    LaunchedEffect(isAnswerRevealed) {
        if (isAnswerRevealed) {
            delay(1500)
            showCorrectMessage = false
            showWrongMessage = false
            wrongExplanation = ""
            selectedAnswer = null
            isAnswerRevealed = false

            if (currentIndex + 1 < dailyChallenges.size) {
                currentIndex++
            } else {
                showCompletion = true
            }
        }
    }
}

@Composable
private fun DailyCompletedScreen(
    navController: NavHostController,
    streak: Int,
) {
    Scaffold(
        containerColor = Background
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Card(
                modifier = Modifier.size(120.dp),
                shape = RoundedCornerShape(60.dp),
                colors = CardDefaults.cardColors(
                    containerColor = SuccessColor.copy(alpha = 0.15f)
                )
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = null,
                        tint = SuccessColor,
                        modifier = Modifier.size(64.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.height(32.dp))
            Text(
                text = "Today's Challenge Complete!",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = OnBackground,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "Come back tomorrow!",
                fontSize = 18.sp,
                color = OnSurfaceVariant,
                textAlign = TextAlign.Center
            )
            if (streak > 0) {
                Spacer(modifier = Modifier.height(24.dp))
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Surface)
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = null,
                            tint = SuccessColor,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = "$streak day streak!",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = SuccessColor
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(32.dp))
            Button(
                onClick = { navController.popBackStack() },
                colors = ButtonDefaults.buttonColors(containerColor = Primary),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = "Back",
                    color = OnBackground,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(horizontal = 24.dp, vertical = 4.dp)
                )
            }
        }
    }
}

@Composable
private fun DailyCompletionScreen(
    navController: NavHostController,
    score: Int,
    total: Int,
    streak: Int,
) {
    val baseXP = 15
    val xpEarned = ((score * baseXP) * 1.5).toInt()
    val percentage = if (total > 0) (score * 100) / total else 0
    val today = remember {
        SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Date())
    }

    LaunchedEffect(Unit) {
        val storage = AppStorage.storage
        storage.markDailyChallengeCompleted(today)

        val user = storage.getUser()
        user?.let {
            storage.setUser(it.copy(totalXP = it.totalXP + xpEarned))
        }
    }

    Scaffold(
        containerColor = Background
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = Icons.Default.Star,
                contentDescription = null,
                tint = Primary,
                modifier = Modifier.size(80.dp)
            )
            Spacer(modifier = Modifier.height(24.dp))
            Text(
                text = "Great Job!",
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = OnBackground,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Daily Challenge Complete",
                fontSize = 18.sp,
                color = OnSurfaceVariant,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(32.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Surface)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "$score / $total",
                        fontSize = 48.sp,
                        fontWeight = FontWeight.Bold,
                        color = Primary
                    )
                    Text(
                        text = "questions correct",
                        fontSize = 16.sp,
                        color = OnSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Divider(color = SurfaceVariant)
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "+$xpEarned XP",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = SuccessColor
                    )
                    Text(
                        text = "1.5x daily bonus applied!",
                        fontSize = 14.sp,
                        color = SuccessColor
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "$percentage% accuracy",
                        fontSize = 14.sp,
                        color = OnSurfaceVariant
                    )
                }
            }

            if (streak > 0) {
                Spacer(modifier = Modifier.height(24.dp))
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Surface)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = null,
                            tint = SuccessColor,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Current streak: ${streak + 1} days",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = SuccessColor
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = { navController.popBackStack() },
                colors = ButtonDefaults.buttonColors(containerColor = Primary),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Back to Home",
                    color = OnBackground,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(vertical = 4.dp)
                )
            }
        }
    }
}
