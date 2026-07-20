package com.skillforge.app.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.skillforge.app.data.Challenge
import com.skillforge.app.data.GameHistoryEntry
import com.skillforge.app.data.SkillWithProgress
import com.skillforge.app.data.TrainingProtocol
import com.skillforge.app.data.calculateLevelFromXP
import com.skillforge.app.data.xpForCurrentLevel
import com.skillforge.app.data.xpForNextLevel
import com.skillforge.app.data.SeedData
import com.skillforge.app.ui.AppStorage
import com.skillforge.app.ui.Screen
import com.skillforge.app.ui.theme.Background
import com.skillforge.app.ui.theme.EasyColor
import com.skillforge.app.ui.theme.ErrorColor
import com.skillforge.app.ui.theme.HardColor
import com.skillforge.app.ui.theme.MediumColor
import com.skillforge.app.ui.theme.OnBackground
import com.skillforge.app.ui.theme.OnSurface
import com.skillforge.app.ui.theme.OnSurfaceVariant
import com.skillforge.app.ui.theme.Primary
import com.skillforge.app.ui.theme.Secondary
import com.skillforge.app.ui.theme.SuccessColor
import com.skillforge.app.ui.theme.Surface
import com.skillforge.app.ui.theme.SurfaceVariant
import com.skillforge.app.ui.theme.AttentionalControlColor
import com.skillforge.app.ui.theme.ExecutiveControlColor
import com.skillforge.app.ui.theme.FluidReasoningColor
import com.skillforge.app.ui.theme.ProcessingSpeedColor
import com.skillforge.app.ui.theme.WorkingMemoryColor

private data class DifficultyInfo(
    val name: String,
    val color: androidx.compose.ui.graphics.Color,
    val xpReward: Int
)

private val difficulties = listOf(
    DifficultyInfo("Easy", EasyColor, 50),
    DifficultyInfo("Medium", MediumColor, 100),
    DifficultyInfo("Hard", HardColor, 150)
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SkillDetailScreen(
    skillId: String,
    navController: NavHostController
) {
    val storage = AppStorage.storage

    var skill by remember { mutableStateOf<SkillWithProgress?>(null) }
    var recentRecords by remember { mutableStateOf<List<GameHistoryEntry>>(emptyList()) }
    var challengesByDifficulty by remember { mutableStateOf<Map<String, List<Challenge>>>(emptyMap()) }
    var protocol by remember { mutableStateOf<TrainingProtocol?>(null) }

    LaunchedEffect(skillId) {
        skill = storage.getSkills().find { it.id.toString() == skillId }
        if (skill != null) {
            val skillChallenges = storage.getChallenges().filter { it.skillId.toString() == skillId }
            challengesByDifficulty = skillChallenges.groupBy { it.difficulty }

            val allHistory = storage.getGameHistory().filter { it.skillId.toString() == skillId }
            recentRecords = allHistory.sortedByDescending { it.challengeId }.take(5)

            protocol = SeedData.protocols[skill!!.id]
        }
    }

    val currentSkill = skill

    Scaffold(
        containerColor = Background,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = currentSkill?.name ?: "Skill Detail",
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Background,
                    titleContentColor = OnBackground,
                    navigationIconContentColor = OnBackground
                )
            )
        }
    ) { padding ->
        if (currentSkill == null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Text("Skill not found", color = OnSurfaceVariant)
            }
            return@Scaffold
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Spacer(modifier = Modifier.height(4.dp))

            SkillHeader(currentSkill)

            if (protocol != null) {
                ProtocolSection(protocol!!)
            }

            XpSection(currentSkill)

            Divider(color = SurfaceVariant)

            Text(
                text = "Choose Difficulty",
                style = MaterialTheme.typography.titleMedium,
                color = OnBackground,
                fontWeight = FontWeight.Bold
            )

            difficulties.forEach { difficulty ->
                val matchingChallenges = challengesByDifficulty[difficulty.name] ?: emptyList()
                DifficultyCard(
                    difficulty = difficulty,
                    challengeCount = matchingChallenges.size,
                    onStart = {
                        navController.navigate(
                            Screen.Challenge.createRoute(skillId, difficulty.name)
                        )
                    }
                )
            }

            if (recentRecords.isNotEmpty()) {
                Divider(color = SurfaceVariant)
                Text(
                    text = "Recent Challenge History",
                    style = MaterialTheme.typography.titleMedium,
                    color = OnBackground,
                    fontWeight = FontWeight.Bold
                )
                recentRecords.forEach { record ->
                    RecentRecordCard(record)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
private fun SkillHeader(skill: SkillWithProgress) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Surface),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = skill.icon,
                style = MaterialTheme.typography.displayLarge
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = skill.name,
                style = MaterialTheme.typography.headlineSmall,
                color = OnSurface,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = skill.description,
                style = MaterialTheme.typography.bodyMedium,
                color = OnSurfaceVariant,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun ProtocolSection(protocol: TrainingProtocol) {
    val domColor = when (protocol.name) {
        "Dual N-Back" -> WorkingMemoryColor
        "Task Switching + Inhibition" -> ExecutiveControlColor
        "Matrix Reasoning" -> FluidReasoningColor
        "Choice Reaction Time" -> ProcessingSpeedColor
        "Stroop + Sustained Attention" -> AttentionalControlColor
        else -> Primary
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Surface),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Scientific Protocol",
                style = MaterialTheme.typography.titleSmall,
                color = OnSurfaceVariant,
                fontWeight = FontWeight.Medium
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = protocol.name,
                style = MaterialTheme.typography.titleLarge,
                color = domColor,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = protocol.description,
                style = MaterialTheme.typography.bodyMedium,
                color = OnSurface
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "Procedure",
                style = MaterialTheme.typography.labelMedium,
                color = OnSurfaceVariant,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = protocol.procedure,
                style = MaterialTheme.typography.bodySmall,
                color = OnSurface
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "Effect Size: ${protocol.effectSize}",
                style = MaterialTheme.typography.labelMedium,
                color = Secondary,
                fontWeight = FontWeight.Medium
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = protocol.citation,
                style = MaterialTheme.typography.labelSmall,
                color = OnSurfaceVariant,
                lineHeight = 16.sp
            )
        }
    }
}

@Composable
private fun XpSection(skill: SkillWithProgress) {
    val skillLevel = calculateLevelFromXP(skill.progress.currentXP)
    val currentLevelXp = xpForCurrentLevel(skillLevel)
    val nextLevelXp = xpForNextLevel(skillLevel)
    val progress = if (nextLevelXp > currentLevelXp) {
        (skill.progress.currentXP - currentLevelXp).toFloat() / (nextLevelXp - currentLevelXp).toFloat()
    } else 0f

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Surface),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Level $skillLevel",
                    style = MaterialTheme.typography.titleLarge,
                    color = OnSurface,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "${skill.progress.currentXP} XP",
                    style = MaterialTheme.typography.bodyMedium,
                    color = OnSurfaceVariant
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            LinearProgressIndicator(
                progress = progress.coerceIn(0f, 1f),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(10.dp)
                    .clip(RoundedCornerShape(5.dp)),
                color = Primary,
                trackColor = SurfaceVariant,
            )
            Spacer(modifier = Modifier.height(4.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Current: ${skill.progress.currentXP - currentLevelXp} XP",
                    style = MaterialTheme.typography.labelSmall,
                    color = OnSurfaceVariant
                )
                Text(
                    text = "Next: ${nextLevelXp - currentLevelXp} XP",
                    style = MaterialTheme.typography.labelSmall,
                    color = OnSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun DifficultyCard(
    difficulty: DifficultyInfo,
    challengeCount: Int,
    onStart: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Surface),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = difficulty.name,
                            style = MaterialTheme.typography.titleMedium,
                            color = OnSurface,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "$challengeCount challenges available",
                        style = MaterialTheme.typography.bodySmall,
                        color = OnSurfaceVariant
                    )
                    Text(
                        text = "+${difficulty.xpReward} XP per challenge",
                        style = MaterialTheme.typography.bodySmall,
                        color = difficulty.color,
                        fontWeight = FontWeight.Medium
                    )
                }
                Button(
                    onClick = onStart,
                    enabled = challengeCount > 0,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = difficulty.color,
                        contentColor = OnSurface
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = "Start Challenge",
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }
}

@Composable
private fun RecentRecordCard(record: GameHistoryEntry) {
    val challenge = AppStorage.storage.getChallenges().find { it.id == record.challengeId }
    val challengeTitle = challenge?.let { it.question.take(40) + "..." } ?: "Challenge #${record.challengeId}"

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
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = challengeTitle,
                    style = MaterialTheme.typography.bodyMedium,
                    color = OnSurface,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = record.difficulty,
                    style = MaterialTheme.typography.bodySmall,
                    color = OnSurfaceVariant
                )
            }
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "Completed",
                    style = MaterialTheme.typography.labelMedium,
                    color = SuccessColor,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = "+${challenge?.xpReward ?: 0} XP",
                    style = MaterialTheme.typography.labelSmall,
                    color = OnSurfaceVariant
                )
            }
        }
    }
}
