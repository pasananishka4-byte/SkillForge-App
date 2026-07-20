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
import androidx.compose.material.icons.filled.Star
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChallengePlayScreen(
    navController: NavHostController,
    skillId: String,
    difficulty: String,
) {
    val skillIdLong = remember { skillId.toLongOrNull() ?: 0L }
    val skill = remember { SeedData.skills.find { it.id == skillIdLong } }

    val challenges = remember {
        SeedData.challenges.filter {
            it.skillId == skillIdLong &&
                    it.difficulty.equals(difficulty, ignoreCase = true)
        }.shuffled()
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

    if (challenges.isEmpty()) {
        EmptyChallengeScreen(navController = navController)
        return
    }

    if (showCompletion) {
        ChallengeCompletionScreen(
            navController = navController,
            score = score,
            total = challenges.size,
            skillId = skillId,
            skillName = skill?.name ?: "Skill",
            difficulty = difficulty
        )
        return
    }

    val currentChallenge = challenges[currentIndex]
    val options = remember(currentChallenge) {
        currentChallenge.options.shuffled()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { },
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
            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "${currentIndex + 1} / ${challenges.size}",
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = OnSurfaceVariant
            )

            Spacer(modifier = Modifier.height(8.dp))

            LinearProgressIndicator(
                progress = (currentIndex + 1).toFloat() / challenges.size,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp)),
                color = Primary,
                trackColor = SurfaceVariant,
            )

            Spacer(modifier = Modifier.height(32.dp))

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

            if (currentIndex + 1 < challenges.size) {
                currentIndex++
            } else {
                showCompletion = true
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun EmptyChallengeScreen(
    navController: NavHostController,
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { },
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
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = Icons.Default.Star,
                contentDescription = null,
                tint = OnSurfaceVariant,
                modifier = Modifier.size(64.dp)
            )
            Spacer(modifier = Modifier.height(24.dp))
            Text(
                text = "No challenges available",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = OnBackground,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "There are no challenges for this difficulty yet.",
                fontSize = 16.sp,
                color = OnSurfaceVariant,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(32.dp))
            Button(
                onClick = { navController.popBackStack() },
                colors = ButtonDefaults.buttonColors(containerColor = Primary),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = "Go Back",
                    color = OnBackground,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ChallengeCompletionScreen(
    navController: NavHostController,
    score: Int,
    total: Int,
    skillId: String,
    skillName: String,
    difficulty: String,
) {
    val xpMultiplier = when (difficulty.lowercase()) {
        "easy" -> 10
        "medium" -> 20
        "hard" -> 30
        else -> 10
    }
    val xpEarned = score * xpMultiplier
    val percentage = if (total > 0) (score * 100) / total else 0

    LaunchedEffect(Unit) {
        val storage = AppStorage.storage
        val skillIdLong = skillId.toLongOrNull() ?: 0L

        for (i in 0 until total) {
            val challenge = SeedData.challenges.filter {
                it.skillId == skillIdLong &&
                        it.difficulty.equals(difficulty, ignoreCase = true)
            }.getOrNull(i)
            challenge?.let {
                storage.markChallengeCompleted(it.id)
            }
        }

        val currentXP = storage.getSkillXP(skillIdLong)
        storage.setSkillXP(skillIdLong, currentXP + xpEarned)

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
                text = skillName,
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
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "$percentage% accuracy",
                        fontSize = 14.sp,
                        color = OnSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = {
                    navController.navigate(Screen.Challenge.createRoute(skillId, difficulty)) {
                        popUpTo(Screen.Challenge.route) { inclusive = true }
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = Primary),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Play Again",
                    color = OnBackground,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(vertical = 4.dp)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedButton(
                onClick = {
                    navController.popBackStack(Screen.SkillTree.route, false)
                },
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = OnBackground
                ),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Back to Skills",
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(vertical = 4.dp)
                )
            }
        }
    }
}
