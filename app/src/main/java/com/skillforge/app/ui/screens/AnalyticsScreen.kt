package com.skillforge.app.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.skillforge.app.data.*
import com.skillforge.app.ui.AppStorage
import com.skillforge.app.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AnalyticsScreen(navController: NavHostController) {
    val storage = remember { AppStorage.storage }
    val user by remember { mutableStateOf<User?>(storage.getUser()) }
    val skills by remember { mutableStateOf<List<SkillWithProgress>?>(storage.getSkills()) }
    val challenges by remember { mutableStateOf<List<Challenge>?>(storage.getChallenges()) }
    val achievements by remember { mutableStateOf<List<Achievement>?>(storage.getAchievements()) }
    val gameRecords by remember { mutableStateOf<Map<String, GameRecord>?>(storage.getGameRecords()) }
    val completedChallengeIds by remember { mutableStateOf<Set<Long>>(storage.getCompletedChallenges()) }

    val level = remember(user) {
        if (user != null) calculateLevelFromXP(user!!.totalXP) else 0
    }

    val completedChallenges = remember(challenges, completedChallengeIds) {
        challenges?.filter { it.id in completedChallengeIds } ?: emptyList()
    }

    val currentStreak = remember(user) { user?.currentStreak ?: 0 }

    val skillProgressMap = remember(skills) {
        skills?.associate { it.name to it.progress.level } ?: emptyMap()
    }

    val categoryProgress = remember(skills) {
        val grouped = skills?.groupBy { it.category } ?: emptyMap()
        grouped.mapValues { (_, skillList) ->
            skillList.map { it.progress.level }.average()
        }
    }

    val difficultyBreakdown = remember(completedChallenges) {
        completedChallenges.groupBy { it.difficulty }.mapValues { it.value.size }
    }

    val totalGamesPlayed = remember(gameRecords) { gameRecords?.size ?: 0 }
    val bestScore = remember(gameRecords) {
        gameRecords?.maxOfOrNull { it.value.bestScore } ?: 0
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Analytics",
                        color = OnBackground,
                        fontWeight = FontWeight.Bold
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Background
                )
            )
        },
        containerColor = Background
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(bottom = 32.dp)
        ) {
            // Stats Cards Row
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    StatCard(
                        title = "Total XP",
                        value = "${user?.totalXP ?: 0}",
                        color = Primary,
                        modifier = Modifier.weight(1f)
                    )
                    StatCard(
                        title = "Level",
                        value = "$level",
                        color = Secondary,
                        modifier = Modifier.weight(1f)
                    )
                    StatCard(
                        title = "Completed",
                        value = "${completedChallenges.size}",
                        color = CriticalThinkingColor,
                        modifier = Modifier.weight(1f)
                    )
                    StatCard(
                        title = "Streak",
                        value = "$currentStreak",
                        color = SocialEmotionalColor,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            // Skill Progress Section
            item {
                SectionHeader("Skill Progress")
            }

            item {
                SkillBarChart(skills = skills ?: emptyList())
            }

            // Category Breakdown
            item {
                SectionHeader("Category Breakdown")
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    CategoryCard(
                        name = "Critical Thinking",
                        progress = categoryProgress["Critical Thinking"] ?: 0.0,
                        color = CriticalThinkingColor,
                        modifier = Modifier.weight(1f)
                    )
                    CategoryCard(
                        name = "General Knowledge",
                        progress = categoryProgress["General Knowledge"] ?: 0.0,
                        color = GeneralKnowledgeColor,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    CategoryCard(
                        name = "Meta-Learning",
                        progress = categoryProgress["Meta-Learning"] ?: 0.0,
                        color = MetaLearningColor,
                        modifier = Modifier.weight(1f)
                    )
                    CategoryCard(
                        name = "Social/Emotional",
                        progress = categoryProgress["Social/Emotional"] ?: 0.0,
                        color = SocialEmotionalColor,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            // Difficulty Breakdown
            item {
                SectionHeader("Difficulty Breakdown")
            }

            item {
                DifficultyBreakdownChart(difficultyBreakdown = difficultyBreakdown)
            }

            // Games Statistics
            item {
                SectionHeader("Games Statistics")
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    StatCard(
                        title = "Games Played",
                        value = "$totalGamesPlayed",
                        color = Primary,
                        modifier = Modifier.weight(1f)
                    )
                    StatCard(
                        title = "Best Score",
                        value = "$bestScore",
                        color = PrimaryDark,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}

@Composable
private fun StatCard(
    title: String,
    value: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Surface)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = value,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = color
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = title,
                fontSize = 11.sp,
                color = OnSurfaceVariant
            )
        }
    }
}

@Composable
private fun SectionHeader(title: String) {
    Text(
        text = title,
        fontSize = 18.sp,
        fontWeight = FontWeight.Bold,
        color = OnBackground,
        modifier = Modifier.padding(top = 8.dp)
    )
}

@Composable
private fun SkillBarChart(skills: List<SkillWithProgress>) {
    val maxLevel = 50f

    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Surface)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            if (skills.isEmpty()) {
                Text(
                    text = "No skills yet",
                    color = OnSurfaceVariant,
                    modifier = Modifier.padding(8.dp)
                )
            } else {
                skills.forEach { skill ->
                    SkillBarRow(skill = skill, maxLevel = maxLevel)
                }
            }
        }
    }
}

@Composable
private fun SkillBarRow(skill: SkillWithProgress, maxLevel: Float) {
    val categoryColor = when (skill.category) {
        "Critical Thinking" -> CriticalThinkingColor
        "General Knowledge" -> GeneralKnowledgeColor
        "Meta-Learning" -> MetaLearningColor
        "Social/Emotional" -> SocialEmotionalColor
        else -> Primary
    }

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = skill.name,
            fontSize = 12.sp,
            color = OnSurface,
            modifier = Modifier.width(100.dp)
        )

        Canvas(
            modifier = Modifier
                .weight(1f)
                .height(20.dp)
        ) {
            val barWidth = size.width
            val barHeight = size.height
            val fillFraction = (skill.progress.level / maxLevel).coerceIn(0f, 1f)
            val fillWidth = barWidth * fillFraction

            // Background bar
            drawRoundRect(
                color = SurfaceVariant,
                topLeft = Offset.Zero,
                size = Size(barWidth, barHeight),
                cornerRadius = androidx.compose.ui.geometry.CornerRadius(6f, 6f)
            )

            // Filled bar
            if (fillWidth > 0f) {
                drawRoundRect(
                    color = categoryColor,
                    topLeft = Offset.Zero,
                    size = Size(fillWidth, barHeight),
                    cornerRadius = androidx.compose.ui.geometry.CornerRadius(6f, 6f)
                )
            }
        }

        Spacer(modifier = Modifier.width(8.dp))

        Text(
            text = "${skill.progress.level}",
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = categoryColor,
            modifier = Modifier.width(30.dp)
        )
    }
}

@Composable
private fun CategoryCard(
    name: String,
    progress: Double,
    color: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Surface)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            Text(
                text = name,
                fontSize = 12.sp,
                color = OnSurfaceVariant
            )
            Spacer(modifier = Modifier.height(8.dp))

            Canvas(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
            ) {
                val barWidth = size.width
                val barHeight = size.height
                val fillFraction = (progress / 50.0).coerceIn(0.0, 1.0).toFloat()
                val fillWidth = barWidth * fillFraction

                drawRoundRect(
                    color = SurfaceVariant,
                    topLeft = Offset.Zero,
                    size = Size(barWidth, barHeight),
                    cornerRadius = androidx.compose.ui.geometry.CornerRadius(4f, 4f)
                )

                if (fillWidth > 0f) {
                    drawRoundRect(
                        color = color,
                        topLeft = Offset.Zero,
                        size = Size(fillWidth, barHeight),
                        cornerRadius = androidx.compose.ui.geometry.CornerRadius(4f, 4f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "Avg Level: ${String.format("%.1f", progress)}",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = color
            )
        }
    }
}

@Composable
private fun DifficultyBreakdownChart(difficultyBreakdown: Map<String, Int>) {
    val difficulties = listOf("Easy", "Medium", "Hard", "Expert")
    val colors = listOf(
        Color(0xFF4CAF50),
        Color(0xFFFFC107),
        Color(0xFFFF5722),
        Color(0xFFE91E63)
    )
    val maxCount = (difficultyBreakdown.values.maxOrNull() ?: 1).coerceAtLeast(1)

    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Surface)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Canvas(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
            ) {
                val barWidth = size.width / (difficulties.size * 2f)
                val spacing = barWidth

                difficulties.forEachIndexed { index, difficulty ->
                    val count = difficultyBreakdown[difficulty] ?: 0
                    val barHeight = (count.toFloat() / maxCount.toFloat()) * (size.height - 20f)
                    val x = index * (barWidth + spacing) + spacing / 2

                    // Bar
                    drawRoundRect(
                        color = colors[index],
                        topLeft = Offset(x, size.height - barHeight - 16f),
                        size = Size(barWidth, barHeight),
                        cornerRadius = androidx.compose.ui.geometry.CornerRadius(4f, 4f)
                    )

                    // Count label
                    drawContext.canvas.nativeCanvas.apply {
                        val paint = android.graphics.Paint().apply {
                            this.color = android.graphics.Color.WHITE
                            textSize = 28f
                            textAlign = android.graphics.Paint.Align.CENTER
                        }
                        drawText(
                            count.toString(),
                            x + barWidth / 2,
                            size.height - barHeight - 24f,
                            paint
                        )
                    }
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                difficulties.forEachIndexed { index, difficulty ->
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Canvas(modifier = Modifier.size(8.dp)) {
                            drawCircle(color = colors[index])
                        }
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = difficulty,
                            fontSize = 10.sp,
                            color = OnSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}
