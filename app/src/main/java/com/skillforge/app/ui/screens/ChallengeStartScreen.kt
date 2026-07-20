package com.skillforge.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChallengeStartScreen(
    navController: NavHostController,
    skillId: String,
) {
    val skillIdLong = remember { skillId.toLongOrNull() ?: 0L }
    val skill = remember { SeedData.skills.find { it.id == skillIdLong } }
    val allChallenges = remember { SeedData.challenges.filter { it.skillId == skillIdLong } }

    val easyCount = remember { allChallenges.count { it.difficulty.equals("Easy", ignoreCase = true) } }
    val mediumCount = remember { allChallenges.count { it.difficulty.equals("Medium", ignoreCase = true) } }
    val hardCount = remember { allChallenges.count { it.difficulty.equals("Hard", ignoreCase = true) } }

    val completedChallengeIds = remember { mutableStateListOf<Long>() }

    LaunchedEffect(Unit) {
        AppStorage.storage.getCompletedChallenges().forEach { completedChallengeIds.add(it) }
    }

    val easyCompleted = remember {
        mutableStateOf(
            allChallenges.count {
                it.difficulty.equals("Easy", ignoreCase = true) && it.id in completedChallengeIds
            }
        )
    }
    val mediumCompleted = remember {
        mutableStateOf(
            allChallenges.count {
                it.difficulty.equals("Medium", ignoreCase = true) && it.id in completedChallengeIds
            }
        )
    }
    val hardCompleted = remember {
        mutableStateOf(
            allChallenges.count {
                it.difficulty.equals("Hard", ignoreCase = true) && it.id in completedChallengeIds
            }
        )
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
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(8.dp))

            skill?.let { s ->
                Icon(
                    imageVector = when (s.icon) {
                        "code" -> Icons.Default.Star
                        "palette" -> Icons.Default.Favorite
                        else -> Icons.Default.Star
                    },
                    contentDescription = s.name,
                    tint = Primary,
                    modifier = Modifier.size(48.dp)
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = s.name,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = OnBackground
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Choose difficulty",
                    fontSize = 16.sp,
                    color = OnSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                item {
                    DifficultyCard(
                        difficulty = "Easy",
                        color = EasyColor,
                        challengeCount = easyCount,
                        completedCount = easyCompleted.value,
                        xpReward = 10,
                        description = "Warm up with simple questions",
                        onClick = {
                            navController.navigate(
                                Screen.Challenge.createRoute(skillId, "Easy")
                            )
                        }
                    )
                }
                item {
                    DifficultyCard(
                        difficulty = "Medium",
                        color = MediumColor,
                        challengeCount = mediumCount,
                        completedCount = mediumCompleted.value,
                        xpReward = 20,
                        description = "Test your knowledge",
                        onClick = {
                            navController.navigate(
                                Screen.Challenge.createRoute(skillId, "Medium")
                            )
                        }
                    )
                }
                item {
                    DifficultyCard(
                        difficulty = "Hard",
                        color = HardColor,
                        challengeCount = hardCount,
                        completedCount = hardCompleted.value,
                        xpReward = 30,
                        description = "Push your limits",
                        onClick = {
                            navController.navigate(
                                Screen.Challenge.createRoute(skillId, "Hard")
                            )
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun DifficultyCard(
    difficulty: String,
    color: androidx.compose.ui.graphics.Color,
    challengeCount: Int,
    completedCount: Int,
    xpReward: Int,
    description: String,
    onClick: () -> Unit,
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Surface)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = difficulty,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = color
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = description,
                        fontSize = 14.sp,
                        color = OnSurfaceVariant
                    )
                }
                Column(
                    horizontalAlignment = Alignment.End
                ) {
                    Text(
                        text = "+${xpReward} XP",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = color
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Divider(
                color = SurfaceVariant,
                thickness = 1.dp
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "$completedCount / $challengeCount completed",
                    fontSize = 14.sp,
                    color = OnSurfaceVariant
                )
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(color.copy(alpha = 0.15f))
                        .padding(horizontal = 12.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "${challengeCount - completedCount} available",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = color
                    )
                }
            }
        }
    }
}
