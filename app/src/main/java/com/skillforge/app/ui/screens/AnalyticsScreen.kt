package com.skillforge.app.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.skillforge.app.data.*
import com.skillforge.app.ui.AppStorage
import com.skillforge.app.ui.components.GradientBackground
import com.skillforge.app.ui.components.PremiumCard
import com.skillforge.app.ui.components.SoundToggleButton
import com.skillforge.app.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AnalyticsScreen(navController: NavHostController) {
    val storage = remember { AppStorage.storage }
    val user by remember { mutableStateOf(storage.getUser()) }
    val skills by remember { mutableStateOf(storage.getSkills()) }
    val challenges by remember { mutableStateOf(storage.getChallenges()) }
    val achievements by remember { mutableStateOf(storage.getAchievements()) }
    val gameRecords by remember { mutableStateOf(storage.getGameRecords()) }
    val completedChallengeIds by remember { mutableStateOf(storage.getCompletedChallenges()) }

    val level = remember(user) { calculateLevelFromXP(user.totalXP) }

    val completedChallenges = remember(challenges, completedChallengeIds) {
        challenges?.filter { it.id in completedChallengeIds } ?: emptyList()
    }

    val currentStreak = remember(user) { user.currentStreak }
    val categoryProgress = remember(skills) {
        val grouped = skills?.groupBy { it.category } ?: emptyMap()
        grouped.mapValues { (_, skillList) ->
            skillList.map { it.progress.level }.average()
        }
    }

    val difficultyBreakdown = remember(completedChallenges) {
        completedChallenges.groupBy { it.difficulty }.mapValues { it.value.size }
    }

    val totalGamesPlayed = remember(gameRecords) { gameRecords.values.sumOf { it.gamesPlayed } }
    val bestScore = remember(gameRecords) { gameRecords.values.maxOfOrNull { it.bestScore } ?: 0 }

    GradientBackground {
        Scaffold(
            containerColor = Color.Transparent,
            topBar = {
                TopAppBar(
                    title = { Text("Analytics", fontWeight = FontWeight.Bold) },
                    actions = { SoundToggleButton() },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent, titleContentColor = OnBackground)
                )
            }
        ) { padding ->
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(padding).padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                contentPadding = PaddingValues(bottom = 32.dp)
            ) {
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        StatCard(title = "Total XP", value = "${user.totalXP}", color = Primary, modifier = Modifier.weight(1f))
                        StatCard(title = "Level", value = "$level", color = Secondary, modifier = Modifier.weight(1f))
                        StatCard(title = "Completed", value = "${completedChallenges.size}", color = CriticalThinkingColor, modifier = Modifier.weight(1f))
                        StatCard(title = "Streak", value = "$currentStreak", color = SocialEmotionalColor, modifier = Modifier.weight(1f))
                    }
                }

                item {
                    SectionHeader("Skill Progress")
                }
                item {
                    Card(shape = MaterialTheme.shapes.medium, colors = CardDefaults.cardColors(containerColor = Surface)) {
                        Column(modifier = Modifier.fillMaxWidth().padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            if (skills.isNullOrEmpty()) {
                                Text("No skills yet", color = OnSurfaceVariant, modifier = Modifier.padding(8.dp))
                            } else {
                                skills.forEach { skill -> SkillBarRow(skill = skill) }
                            }
                        }
                    }
                }

                item {
                    SectionHeader("Category Breakdown")
                }
                item {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        CategoryCard("Critical Thinking", categoryProgress["Critical Thinking"] ?: 0.0, CriticalThinkingColor, Modifier.weight(1f))
                        CategoryCard("General Knowledge", categoryProgress["General Knowledge"] ?: 0.0, GeneralKnowledgeColor, Modifier.weight(1f))
                    }
                }
                item {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        CategoryCard("Meta-Learning", categoryProgress["Meta-Learning"] ?: 0.0, MetaLearningColor, Modifier.weight(1f))
                        CategoryCard("Social/Emotional", categoryProgress["Social/Emotional"] ?: 0.0, SocialEmotionalColor, Modifier.weight(1f))
                    }
                }

                item {
                    SectionHeader("Difficulty Breakdown")
                }
                item {
                    DifficultyBreakdownChart(difficultyBreakdown = difficultyBreakdown)
                }

                item {
                    SectionHeader("Games Statistics")
                }
                item {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        StatCard(title = "Games Played", value = "$totalGamesPlayed", color = Primary, modifier = Modifier.weight(1f))
                        StatCard(title = "Best Score", value = "$bestScore", color = PrimaryDark, modifier = Modifier.weight(1f))
                    }
                }
            }
        }
    }
}

@Composable
private fun StatCard(title: String, value: String, color: Color, modifier: Modifier = Modifier) {
    PremiumCard(glassEffect = true) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = value, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = color)
            Spacer(modifier = Modifier.height(2.dp))
            Text(text = title, style = MaterialTheme.typography.labelSmall, color = OnSurfaceVariant)
        }
    }
}

@Composable
private fun SectionHeader(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.Bold,
        color = OnBackground,
        modifier = Modifier.padding(top = 8.dp)
    )
}

@Composable
private fun SkillBarRow(skill: SkillWithProgress) {
    val catColor = when (skill.category) {
        "Critical Thinking" -> CriticalThinkingColor
        "General Knowledge" -> GeneralKnowledgeColor
        "Meta-Learning" -> MetaLearningColor
        "Social/Emotional" -> SocialEmotionalColor
        else -> Primary
    }
    val maxLevel = 50f
    val fillFraction = (skill.progress.level / maxLevel).coerceIn(0f, 1f)

    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        Text(skill.name, style = MaterialTheme.typography.bodySmall, color = OnSurface, modifier = Modifier.width(90.dp), maxLines = 1)
        Box(modifier = Modifier.weight(1f).height(16.dp)) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                drawRoundRect(color = SurfaceVariant, cornerRadius = androidx.compose.ui.geometry.CornerRadius(6f, 6f))
                if (fillFraction > 0f) {
                    drawRoundRect(color = catColor, size = Size(size.width * fillFraction, size.height), cornerRadius = androidx.compose.ui.geometry.CornerRadius(6f, 6f))
                }
            }
        }
        Spacer(modifier = Modifier.width(8.dp))
        Text("${skill.progress.level}", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold, color = catColor, modifier = Modifier.width(24.dp))
    }
}

@Composable
private fun CategoryCard(name: String, progress: Double, color: Color, modifier: Modifier = Modifier) {
    val fillFraction = (progress / 50.0).coerceIn(0.0, 1.0).toFloat()
    PremiumCard {
        Column(modifier = Modifier.fillMaxWidth().padding(14.dp)) {
            Text(name, style = MaterialTheme.typography.labelSmall, color = OnSurfaceVariant)
            Spacer(modifier = Modifier.height(8.dp))
            Box(modifier = Modifier.fillMaxWidth().height(8.dp)) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    drawRoundRect(color = SurfaceVariant, cornerRadius = androidx.compose.ui.geometry.CornerRadius(4f, 4f))
                    if (fillFraction > 0f) {
                        drawRoundRect(color = color, size = Size(size.width * fillFraction, size.height), cornerRadius = androidx.compose.ui.geometry.CornerRadius(4f, 4f))
                    }
                }
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text("Avg: ${String.format("%.1f", progress)}", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold, color = color)
        }
    }
}

@Composable
private fun DifficultyBreakdownChart(difficultyBreakdown: Map<String, Int>) {
    val difficulties = listOf("Easy", "Medium", "Hard", "Expert")
    val chartColors = listOf(EasyColor, MediumColor, HardColor, ExpertColor)
    val maxCount = (difficultyBreakdown.values.maxOrNull() ?: 1).coerceAtLeast(1)

    PremiumCard {
        Column(modifier = Modifier.fillMaxWidth().padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Box(modifier = Modifier.fillMaxWidth().height(120.dp)) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val barWidth = size.width / (difficulties.size * 2f)
                    val spacing = barWidth
                    difficulties.forEachIndexed { index, difficulty ->
                        val count = difficultyBreakdown[difficulty] ?: 0
                        val barHeight = (count.toFloat() / maxCount.toFloat()) * (size.height - 20f)
                        val x = index * (barWidth + spacing) + spacing / 2

                        drawRoundRect(color = chartColors[index], topLeft = Offset(x, size.height - barHeight - 16f), size = Size(barWidth, barHeight), cornerRadius = androidx.compose.ui.geometry.CornerRadius(4f, 4f))
                        drawContext.canvas.nativeCanvas.apply {
                            val paint = android.graphics.Paint().apply {
                                this.color = android.graphics.Color.WHITE
                                textSize = 28f
                                textAlign = android.graphics.Paint.Align.CENTER
                            }
                            drawText(count.toString(), x + barWidth / 2, size.height - barHeight - 24f, paint)
                        }
                    }
                }
            }
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                difficulties.forEachIndexed { index, difficulty ->
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Canvas(modifier = Modifier.size(8.dp)) { drawCircle(color = chartColors[index]) }
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(difficulty, style = MaterialTheme.typography.labelSmall, color = OnSurfaceVariant)
                    }
                }
            }
        }
    }
}
