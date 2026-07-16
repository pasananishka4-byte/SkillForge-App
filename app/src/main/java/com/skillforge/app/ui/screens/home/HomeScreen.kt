package com.skillforge.app.ui.screens.home

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.VideogameAsset
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.skillforge.app.domain.model.Skill
import com.skillforge.app.ui.Screen
import com.skillforge.app.ui.theme.*
import com.skillforge.app.domain.repository.DailyChallengeRepository
import androidx.compose.runtime.remember

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navController: NavController,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(vertical = 16.dp)
    ) {
        // Header
        item {
            Text(
                text = "SkillForge",
                style = MaterialTheme.typography.headlineLarge,
                color = Primary,
                modifier = Modifier.padding(top = 8.dp)
            )
            Text(
                text = "Level up everything",
                style = MaterialTheme.typography.bodyMedium,
                color = OnSurfaceVariant
            )
        }

        // Level & XP Card
        item {
            LevelCard(
                level = uiState.user.level,
                totalXP = uiState.user.totalXP,
                levelProgress = viewModel.getLevelProgress()
            )
        }

        // Streak Card
        item {
            StreakCard(
                currentStreak = uiState.user.currentStreak,
                longestStreak = uiState.user.longestStreak,
                message = viewModel.getStreakMessage()
            )
        }

        // Today's Progress
        item {
            TodayStatsCard(
                completed = uiState.todayCompleted,
                correct = uiState.todayCorrect
            )
        }

        // Quick Actions
        item {
            Text(
                text = "Quick Start",
                style = MaterialTheme.typography.titleMedium,
                color = OnSurface,
                modifier = Modifier.padding(top = 8.dp)
            )
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                QuickActionButton(
                    text = "Random\nChallenge",
                    icon = Icons.Filled.Bolt,
                    color = Primary,
                    modifier = Modifier.weight(1f),
                    onClick = { navController.navigate(Screen.ChallengeStart.createRoute(0)) }
                )
                QuickActionButton(
                    text = "Skill\nTree",
                    icon = Icons.Filled.TrendingUp,
                    color = Secondary,
                    modifier = Modifier.weight(1f),
                    onClick = { navController.navigate(Screen.SkillTree.route) }
                )
                QuickActionButton(
                    text = "Daily\nChallenge",
                    icon = Icons.Filled.LocalFireDepartment,
                    color = StreakFire,
                    modifier = Modifier.weight(1f),
                    onClick = { navController.navigate(Screen.DailyChallenge.route) }
                )
                QuickActionButton(
                    text = "Mini\nGames",
                    icon = Icons.Filled.VideogameAsset,
                    color = CriticalThinkingColor,
                    modifier = Modifier.weight(1f),
                    onClick = { navController.navigate(Screen.GamesHub.route) }
                )
            }
        }

        // Skill Categories Overview
        item {
            Text(
                text = "Your Skills",
                style = MaterialTheme.typography.titleMedium,
                color = OnSurface,
                modifier = Modifier.padding(top = 8.dp)
            )
        }

        val categories = uiState.skills.groupBy { it.category }
        items(categories.entries.toList()) { (category, skills) ->
            SkillCategoryCard(
                category = category,
                skills = skills,
                onSkillClick = { skillId ->
                    navController.navigate(Screen.SkillDetail.createRoute(skillId))
                }
            )
        }

        item { Spacer(modifier = Modifier.height(16.dp)) }
    }
}

@Composable
fun LevelCard(level: Int, totalXP: Long, levelProgress: Float) {
    val animatedProgress by animateFloatAsState(targetValue = levelProgress, label = "level")

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Surface),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column {
                    Text(
                        text = "Level $level",
                        style = MaterialTheme.typography.headlineMedium,
                        color = Primary,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "$totalXP XP Total",
                        style = MaterialTheme.typography.bodyMedium,
                        color = OnSurfaceVariant
                    )
                }
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .clip(CircleShape)
                        .background(Primary.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "$level",
                        style = MaterialTheme.typography.headlineLarge,
                        color = Primary,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            LinearProgressIndicator(
                progress = animatedProgress,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp)),
                color = Primary,
                trackColor = SurfaceVariant,
            )
        }
    }
}

@Composable
fun StreakCard(currentStreak: Int, longestStreak: Int, message: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Surface),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(20.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(CircleShape)
                    .background(
                        if (currentStreak > 0) StreakFire.copy(alpha = 0.15f)
                        else SurfaceVariant
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Filled.LocalFireDepartment,
                    contentDescription = "Streak",
                    tint = if (currentStreak > 0) StreakFire else StreakCold,
                    modifier = Modifier.size(32.dp)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "$currentStreak Day Streak",
                    style = MaterialTheme.typography.titleMedium,
                    color = if (currentStreak > 0) StreakFire else OnSurface,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = message,
                    style = MaterialTheme.typography.bodySmall,
                    color = OnSurfaceVariant
                )
            }

            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "Best: $longestStreak",
                    style = MaterialTheme.typography.labelSmall,
                    color = OnSurfaceVariant
                )
            }
        }
    }
}

@Composable
fun TodayStatsCard(completed: Int, correct: Int) {
    val accuracy = if (completed > 0) (correct.toFloat() / completed * 100).toInt() else 0

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Surface),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(
                text = "Today's Progress",
                style = MaterialTheme.typography.titleMedium,
                color = OnSurface
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                StatItem(label = "Completed", value = "$completed", color = Secondary)
                StatItem(label = "Correct", value = "$correct", color = SuccessColor)
                StatItem(label = "Accuracy", value = "$accuracy%", color = Primary)
            }
        }
    }
}

@Composable
fun StatItem(label: String, value: String, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = value,
            style = MaterialTheme.typography.headlineMedium,
            color = color,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = OnSurfaceVariant
        )
    }
}

@Composable
fun QuickActionButton(
    text: String,
    icon: ImageVector,
    color: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier
            .height(100.dp)
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(containerColor = color.copy(alpha = 0.12f)),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = color,
                modifier = Modifier.size(28.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = text,
                style = MaterialTheme.typography.labelMedium,
                color = color,
                textAlign = TextAlign.Center,
                lineHeight = 16.sp
            )
        }
    }
}

@Composable
fun SkillCategoryCard(
    category: String,
    skills: List<Skill>,
    onSkillClick: (Long) -> Unit
) {
    val categoryColor = when (category) {
        "Critical Thinking" -> CriticalThinkingColor
        "General Knowledge" -> GeneralKnowledgeColor
        "Meta-Learning" -> MetaLearningColor
        "Social/Emotional" -> SocialEmotionalColor
        else -> Primary
    }

    val avgLevel = if (skills.isNotEmpty()) skills.map { it.level }.average().toInt() else 0

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Surface),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(
                    modifier = Modifier
                        .size(12.dp)
                        .clip(CircleShape)
                        .background(categoryColor)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = category,
                    style = MaterialTheme.typography.titleMedium,
                    color = OnSurface,
                    modifier = Modifier.weight(1f)
                )
                Text(
                    text = "Avg Lv.$avgLevel",
                    style = MaterialTheme.typography.labelSmall,
                    color = OnSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            skills.forEach { skill ->
                SkillProgressRow(
                    skill = skill,
                    color = categoryColor,
                    onClick = { onSkillClick(skill.id) }
                )
                if (skill != skills.last()) {
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }
    }
}

@Composable
fun SkillProgressRow(skill: Skill, color: Color, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = skill.name,
            style = MaterialTheme.typography.bodyMedium,
            color = OnSurface,
            modifier = Modifier.weight(1f)
        )
        Text(
            text = "Lv.${skill.level}",
            style = MaterialTheme.typography.labelSmall,
            color = color,
            modifier = Modifier.padding(end = 8.dp)
        )
        LinearProgressIndicator(
            progress = if (skill.maxLevel > 0) skill.level.toFloat() / skill.maxLevel else 0f,
            modifier = Modifier
                .width(80.dp)
                .height(6.dp)
                .clip(RoundedCornerShape(3.dp)),
            color = color,
            trackColor = SurfaceVariant,
        )
    }
}
