package com.skillforge.app.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.skillforge.app.data.SkillWithProgress
import com.skillforge.app.data.SoundManager
import com.skillforge.app.data.xpForCurrentLevel
import com.skillforge.app.data.xpForNextLevel
import com.skillforge.app.ui.AppStorage
import com.skillforge.app.ui.Screen
import com.skillforge.app.ui.components.GradientBackground
import com.skillforge.app.ui.components.PremiumCard
import com.skillforge.app.ui.components.SoundToggleButton
import com.skillforge.app.ui.theme.*
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SkillTreeScreen(navController: NavHostController) {
    val storage = AppStorage.storage
    var allSkills by remember { mutableStateOf<List<SkillWithProgress>>(emptyList()) }

    LaunchedEffect(Unit) { allSkills = storage.getSkills() }

    GradientBackground {
        Scaffold(
            containerColor = Color.Transparent,
            topBar = {
                TopAppBar(
                    title = { Text("Cognitive Domains", fontWeight = FontWeight.Bold) },
                    actions = { SoundToggleButton() },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent, titleContentColor = OnBackground)
                )
            }
        ) { padding ->
            Column(modifier = Modifier.fillMaxSize().padding(padding)) {
                Text(
                    text = "5 evidence-based cognitive training domains",
                    style = MaterialTheme.typography.bodySmall,
                    color = OnSurfaceVariant,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
                )

                Spacer(modifier = Modifier.height(8.dp))

                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(allSkills, key = { it.id }) { skill ->
                        SkillCard(skill = skill, onClick = {
                            SoundManager.playTap()
                            navController.navigate(Screen.SkillDetail.createRoute(skill.id.toString()))
                        })
                    }
                }
            }
        }
    }
}

@Composable
private fun SkillCard(skill: SkillWithProgress, onClick: () -> Unit) {
    val skillLevel = skill.progress.level
    val currentLevelXp = xpForCurrentLevel(skillLevel)
    val nextLevelXp = xpForNextLevel(skillLevel)
    val progress = if (nextLevelXp > currentLevelXp) {
        (skill.progress.currentXP - currentLevelXp).toFloat() / (nextLevelXp - currentLevelXp).toFloat()
    } else 0f

    val domColor = rememberDomainColor(skill.id)

    PremiumCard(
        onClick = onClick,
        gradient = Brush.verticalGradient(
            listOf(domColor.copy(alpha = 0.12f), Color.Transparent)
        )
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier.size(40.dp).clip(MaterialTheme.shapes.small),
                    contentAlignment = Alignment.Center
                ) {
                    androidx.compose.foundation.Canvas(modifier = Modifier.fillMaxSize()) {
                        drawRoundRect(color = domColor.copy(alpha = 0.25f))
                    }
                    Text(text = skill.icon, fontSize = 20.sp)
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(skill.name, style = MaterialTheme.typography.bodyMedium, color = OnSurface, fontWeight = FontWeight.SemiBold, maxLines = 1)
                    if (skill.protocol.isNotBlank()) {
                        Text(skill.protocol, style = MaterialTheme.typography.labelSmall, color = domColor, maxLines = 1)
                    }
                }
            }
            Spacer(modifier = Modifier.height(10.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Level $skillLevel", style = MaterialTheme.typography.labelMedium, color = OnSurfaceVariant, fontWeight = FontWeight.Medium)
                Text("${skill.progress.currentXP} XP", style = MaterialTheme.typography.labelSmall, color = OnSurfaceLow)
            }
            Spacer(modifier = Modifier.height(6.dp))
            Box(modifier = Modifier.fillMaxWidth().height(6.dp).clip(RoundedCornerShape(3.dp))) {
                LinearProgressIndicator(
                    progress = progress.coerceIn(0f, 1f),
                    modifier = Modifier.fillMaxSize(),
                    color = domColor,
                    trackColor = SurfaceVariant,
                    strokeCap = StrokeCap.Round
                )
            }
        }
    }
}

@Composable
private fun rememberDomainColor(skillId: Long): Color {
    return remember(skillId) {
        when (skillId) {
            1L -> WorkingMemoryColor
            2L -> ExecutiveControlColor
            3L -> FluidReasoningColor
            4L -> ProcessingSpeedColor
            5L -> AttentionalControlColor
            else -> Primary
        }
    }
}
