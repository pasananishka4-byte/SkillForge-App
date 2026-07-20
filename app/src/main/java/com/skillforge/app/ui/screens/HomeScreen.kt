package com.skillforge.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.skillforge.app.data.Challenge
import com.skillforge.app.data.SkillWithProgress
import com.skillforge.app.data.calculateLevelFromXP
import com.skillforge.app.data.xpForCurrentLevel
import com.skillforge.app.data.xpForNextLevel
import com.skillforge.app.ui.AppStorage
import com.skillforge.app.ui.Screen
import com.skillforge.app.ui.theme.Background
import com.skillforge.app.ui.theme.OnBackground
import com.skillforge.app.ui.theme.OnSurface
import com.skillforge.app.ui.theme.OnSurfaceVariant
import com.skillforge.app.ui.theme.Primary
import com.skillforge.app.ui.theme.Secondary
import com.skillforge.app.ui.theme.Surface
import com.skillforge.app.ui.theme.SurfaceVariant
import kotlin.random.Random

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(navController: NavHostController) {
    val storage = AppStorage.storage

    var userName by remember { mutableStateOf("") }
    var userLevel by remember { mutableStateOf(0) }
    var userXp by remember { mutableStateOf(0L) }
    var streak by remember { mutableStateOf(0) }
    var topSkills by remember { mutableStateOf<List<SkillWithProgress>>(emptyList()) }
    var todayChallenges by remember { mutableStateOf<List<Challenge>>(emptyList()) }

    LaunchedEffect(Unit) {
        val user = storage.getUser()
        userName = user.name
        userXp = user.totalXP
        userLevel = calculateLevelFromXP(userXp)

        streak = user.currentStreak

        val allSkills = storage.getSkills().sortedByDescending { it.progress.currentXP }
        topSkills = allSkills.take(6)

        val allChallenges = storage.getChallenges().toMutableList()
        val upcoming = mutableListOf<Challenge>()
        val indices = allChallenges.indices.toMutableList()
        repeat(minOf(3, allChallenges.size)) {
            val idx = indices.removeAt(Random.nextInt(indices.size))
            upcoming.add(allChallenges[idx])
        }
        todayChallenges = upcoming
    }

    Scaffold(
        containerColor = Background,
        topBar = {
            TopAppBar(
                title = { Text("SkillForge", fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Background,
                    titleContentColor = OnBackground
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Spacer(modifier = Modifier.height(4.dp))

            GreetingSection(userName, userLevel)

            XpProgressBar(userXp, userLevel)

            StreakDisplay(streak)

            TodayChallengesSection(todayChallenges, navController)

            YourSkillsSection(topSkills, navController)

            QuickGamesSection(navController)

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
private fun GreetingSection(userName: String, level: Int) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(
                text = "Welcome back, $userName!",
                style = MaterialTheme.typography.headlineSmall,
                color = OnBackground,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = "Level $level",
                style = MaterialTheme.typography.titleMedium,
                color = OnSurfaceVariant
            )
        }
        Box(
            modifier = Modifier
                .size(56.dp)
                .clip(RoundedCornerShape(28.dp))
                .background(SurfaceVariant),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "$level",
                style = MaterialTheme.typography.headlineMedium,
                color = Primary,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun XpProgressBar(currentXp: Long, level: Int) {
    val currentLevelXp = xpForCurrentLevel(level)
    val nextLevelXp = xpForNextLevel(level)
    val progress = if (nextLevelXp > currentLevelXp) {
        (currentXp - currentLevelXp).toFloat() / (nextLevelXp - currentLevelXp).toFloat()
    } else 0f

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Surface),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("XP Progress", style = MaterialTheme.typography.bodyMedium, color = OnSurface)
                Text(
                    "$currentXp / $nextLevelXp XP",
                    style = MaterialTheme.typography.bodySmall,
                    color = OnSurfaceVariant
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            LinearProgressIndicator(
                progress = progress.coerceIn(0f, 1f),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(12.dp)
                    .clip(RoundedCornerShape(6.dp)),
                color = Primary,
                trackColor = SurfaceVariant,
                strokeCap = StrokeCap.Round
            )
        }
    }
}

@Composable
private fun StreakDisplay(streak: Int) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Surface),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = Icons.Default.LocalFireDepartment,
                contentDescription = "Streak",
                tint = Secondary,
                modifier = Modifier.size(28.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "$streak-day streak!",
                style = MaterialTheme.typography.titleMedium,
                color = OnSurface,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@Composable
private fun TodayChallengesSection(
    challenges: List<Challenge>,
    navController: NavHostController
) {
    Column {
        Text(
            text = "Today's Challenges",
            style = MaterialTheme.typography.titleMedium,
            color = OnBackground,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(8.dp))
        challenges.forEach { challenge ->
            val skill = AppStorage.storage.getSkills().find { it.id == challenge.skillId }
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
                    .clickable {
                        navController.navigate(
                            Screen.Challenge.createRoute(challenge.skillId.toString(), challenge.difficulty)
                        )
                    },
                colors = CardDefaults.cardColors(containerColor = Surface),
                shape = RoundedCornerShape(12.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = challenge.question,
                            style = MaterialTheme.typography.bodyLarge,
                            color = OnSurface,
                            fontWeight = FontWeight.Medium
                        )
                        if (skill != null) {
                            Text(
                                text = skill.name,
                                style = MaterialTheme.typography.bodySmall,
                                color = OnSurfaceVariant
                            )
                        }
                    }
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = null,
                        tint = Secondary,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun YourSkillsSection(
    skills: List<SkillWithProgress>,
    navController: NavHostController
) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Your Skills",
                style = MaterialTheme.typography.titleMedium,
                color = OnBackground,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "See all",
                style = MaterialTheme.typography.bodySmall,
                color = Primary,
                modifier = Modifier.clickable {
                    navController.navigate(Screen.SkillTree.route)
                }
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            skills.forEach { skill ->
                SkillMiniCard(skill, onClick = {
                    navController.navigate(Screen.SkillDetail.createRoute(skill.id.toString()))
                })
            }
        }
    }
}

@Composable
private fun SkillMiniCard(skill: SkillWithProgress, onClick: () -> Unit) {
    val skillLevel = calculateLevelFromXP(skill.progress.currentXP)
    val currentLevelXp = xpForCurrentLevel(skillLevel)
    val nextLevelXp = xpForNextLevel(skillLevel)
    val progress = if (nextLevelXp > currentLevelXp) {
        (skill.progress.currentXP - currentLevelXp).toFloat() / (nextLevelXp - currentLevelXp).toFloat()
    } else 0f

    Card(
        modifier = Modifier
            .width(140.dp)
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(containerColor = Surface),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = skill.icon,
                style = MaterialTheme.typography.headlineLarge
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = skill.name,
                style = MaterialTheme.typography.bodyMedium,
                color = OnSurface,
                fontWeight = FontWeight.Medium,
                maxLines = 1
            )
            Text(
                text = "Lv. $skillLevel",
                style = MaterialTheme.typography.bodySmall,
                color = OnSurfaceVariant
            )
            Spacer(modifier = Modifier.height(6.dp))
            LinearProgressIndicator(
                progress = progress.coerceIn(0f, 1f),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp)),
                color = Primary,
                trackColor = SurfaceVariant,
                strokeCap = StrokeCap.Round
            )
        }
    }
}

@Composable
private fun QuickGamesSection(navController: NavHostController) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { navController.navigate(Screen.GamesHub.route) },
        colors = CardDefaults.cardColors(containerColor = Surface),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = "Quick Games",
                    style = MaterialTheme.typography.titleMedium,
                    color = OnSurface,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Test your skills with fast-paced challenges",
                    style = MaterialTheme.typography.bodySmall,
                    color = OnSurfaceVariant
                )
            }
            Icon(
                imageVector = Icons.Default.Bolt,
                contentDescription = null,
                tint = Secondary,
                modifier = Modifier.size(32.dp)
            )
        }
    }
}
