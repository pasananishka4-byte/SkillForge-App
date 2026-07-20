package com.skillforge.app.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.skillforge.app.data.Challenge
import com.skillforge.app.data.SkillWithProgress
import com.skillforge.app.data.SoundManager
import com.skillforge.app.data.calculateLevelFromXP
import com.skillforge.app.data.xpForCurrentLevel
import com.skillforge.app.data.xpForNextLevel
import com.skillforge.app.ui.AppStorage
import com.skillforge.app.ui.Screen
import com.skillforge.app.ui.components.GradientBackground
import com.skillforge.app.ui.components.PremiumButton
import com.skillforge.app.ui.components.PremiumCard
import com.skillforge.app.ui.components.SectionHeader
import com.skillforge.app.ui.components.SoundToggleButton
import com.skillforge.app.ui.theme.*
import androidx.compose.ui.graphics.StrokeCap
import kotlin.random.Random

private fun domainColor(skillId: Long): Color = when (skillId) {
    1L -> WorkingMemoryColor
    2L -> ExecutiveControlColor
    3L -> FluidReasoningColor
    4L -> ProcessingSpeedColor
    5L -> AttentionalControlColor
    else -> Primary
}

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

    val infiniteTransition = rememberInfiniteTransition(label = "ambient")
    val glowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.3f, targetValue = 0.6f,
        animationSpec = infiniteRepeatable(tween(2500, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "glowAlpha"
    )

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

    GradientBackground {
        Scaffold(
            containerColor = Color.Transparent,
            topBar = {
                TopAppBar(
                    title = { Text("SkillForge", fontWeight = FontWeight.Bold) },
                    actions = { SoundToggleButton() },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent, titleContentColor = OnBackground)
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

                GreetingSection(userName, userLevel, glowAlpha)
                XpProgressBar(userXp, userLevel)
                StreakDisplay(streak, animateIn = true, animateDelayMs = 100)
                TodayChallengesSection(todayChallenges, navController)
                YourSkillsSection(topSkills, navController)
                QuickGamesSection(navController)
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Composable
private fun GreetingSection(userName: String, level: Int, glowAlpha: Float) {
    Box(modifier = Modifier.fillMaxWidth()) {
        if (userName.isNotEmpty()) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                drawCircle(
                    color = Primary.copy(alpha = glowAlpha * 0.3f),
                    radius = 80.dp.toPx(),
                    center = Offset(size.width * 0.6f, size.height * 0.3f)
                )
            }
        }
        PremiumCard(
            gradient = Brush.horizontalGradient(
                listOf(Primary.copy(alpha = 0.15f), Primary.copy(alpha = 0.05f))
            )
        ) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Welcome back,",
                        style = MaterialTheme.typography.bodyMedium,
                        color = OnSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = userName,
                        style = MaterialTheme.typography.headlineSmall,
                        color = OnBackground,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.TrendingUp, contentDescription = null, tint = Primary, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Level $level", style = MaterialTheme.typography.bodySmall, color = Primary)
                    }
                }
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .clip(CircleShape)
                        .then(
                            Modifier
                                .clip(CircleShape)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        drawCircle(color = Primary.copy(alpha = 0.2f))
                        drawCircle(color = Primary, radius = size.minDimension * 0.35f)
                    }
                    Text(
                        text = "$level",
                        style = MaterialTheme.typography.headlineMedium,
                        color = OnPrimary,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
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

    PremiumCard(glassEffect = true) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("XP Progress", style = MaterialTheme.typography.bodyMedium, color = OnSurface, fontWeight = FontWeight.Medium)
                Text(
                    "$currentXp / $nextLevelXp XP",
                    style = MaterialTheme.typography.bodySmall,
                    color = OnSurfaceVariant
                )
            }
            Spacer(modifier = Modifier.height(10.dp))
            Box(modifier = Modifier.fillMaxWidth().height(12.dp).clip(RoundedCornerShape(6.dp))) {
                LinearProgressIndicator(
                    progress = progress.coerceIn(0f, 1f),
                    modifier = Modifier.fillMaxSize(),
                    color = Primary,
                    trackColor = SurfaceVariant,
                    strokeCap = StrokeCap.Round
                )
            }
        }
    }
}

@Composable
private fun StreakDisplay(streak: Int, animateIn: Boolean = false, animateDelayMs: Int = 0) {
    PremiumCard(animateIn = animateIn, animateDelayMs = animateDelayMs, onClick = { SoundManager.playTap() }) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = Icons.Default.Star,
                contentDescription = "Streak",
                tint = if (streak > 0) StreakFire else StreakCold,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = if (streak > 0) "$streak-day streak!" else "Start your streak today!",
                style = MaterialTheme.typography.titleSmall,
                color = OnSurface,
                fontWeight = FontWeight.SemiBold
            )
            if (streak > 0) {
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "🔥",
                    fontSize = 18.sp
                )
            }
        }
    }
}

@Composable
private fun TodayChallengesSection(challenges: List<Challenge>, navController: NavHostController) {
    Column {
        SectionHeader(
            title = "Today's Challenges",
            subtitle = "Complete them to earn bonus XP"
        )
        Spacer(modifier = Modifier.height(8.dp))
        challenges.forEach { challenge ->
            val skill = AppStorage.storage.getSkills().find { it.id == challenge.skillId }
            PremiumCard(
                onClick = {
                    SoundManager.playTap()
                    navController.navigate(Screen.Challenge.createRoute(challenge.skillId.toString(), challenge.difficulty))
                }
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier.size(40.dp).clip(RoundedCornerShape(10.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Canvas(modifier = Modifier.fillMaxSize()) {
                            val catColor = if (skill != null) domainColor(skill.id) else Primary
                            drawRoundRect(color = catColor.copy(alpha = 0.2f))
                        }
                        Text(text = skill?.icon?.firstOrNull()?.toString() ?: "?", fontSize = 20.sp)
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = challenge.question,
                            style = MaterialTheme.typography.bodyMedium,
                            color = OnSurface,
                            fontWeight = FontWeight.Medium,
                            maxLines = 2
                        )
                        if (skill != null) {
                            Text(
                                text = "${skill.name} • ${challenge.difficulty.replaceFirstChar { it.uppercase() }}",
                                style = MaterialTheme.typography.bodySmall,
                                color = OnSurfaceVariant
                            )
                        }
                    }
                    Icon(Icons.Default.PlayArrow, contentDescription = null, tint = Primary, modifier = Modifier.size(20.dp))
                }
            }
            Spacer(modifier = Modifier.height(4.dp))
        }
    }
}

@Composable
private fun YourSkillsSection(skills: List<SkillWithProgress>, navController: NavHostController) {
    Column {
        SectionHeader(
            title = "Your Skills",
            subtitle = "Keep building your strengths",
            action = {
                Text(
                    text = "See all",
                    style = MaterialTheme.typography.bodySmall,
                    color = Primary,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.clickable {
                        SoundManager.playTap()
                        navController.navigate(Screen.SkillTree.route)
                    }
                )
            }
        )
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            skills.take(3).forEach { skill ->
                SkillMiniCard(skill, onClick = {
                    SoundManager.playTap()
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

    val catColor = domainColor(skill.id)

    PremiumCard(onClick = onClick) {
        Column(
            modifier = Modifier.padding(12.dp).fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier.size(40.dp).clip(CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    drawCircle(color = catColor.copy(alpha = 0.2f))
                }
                Text(text = skill.icon, fontSize = 20.sp)
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = skill.name,
                style = MaterialTheme.typography.bodySmall,
                color = OnSurface,
                fontWeight = FontWeight.Medium,
                maxLines = 1
            )
            Text(
                text = "Lv. $skillLevel",
                style = MaterialTheme.typography.labelSmall,
                color = OnSurfaceVariant
            )
            Spacer(modifier = Modifier.height(6.dp))
            Box(modifier = Modifier.fillMaxWidth().height(4.dp).clip(RoundedCornerShape(2.dp))) {
                LinearProgressIndicator(
                    progress = progress.coerceIn(0f, 1f),
                    modifier = Modifier.fillMaxSize(),
                    color = catColor,
                    trackColor = SurfaceVariant,
                    strokeCap = StrokeCap.Round
                )
            }
        }
    }
}

@Composable
private fun QuickGamesSection(navController: NavHostController) {
    PremiumCard(
        gradient = Brush.horizontalGradient(listOf(Primary.copy(alpha = 0.2f), GradientGoldEnd.copy(alpha = 0.1f))),
        onClick = {
            SoundManager.playTap()
            navController.navigate(Screen.GamesHub.route)
        }
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = "Quick Games",
                    style = MaterialTheme.typography.titleMedium,
                    color = OnBackground,
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
                tint = Primary,
                modifier = Modifier.size(28.dp)
            )
        }
    }
}
