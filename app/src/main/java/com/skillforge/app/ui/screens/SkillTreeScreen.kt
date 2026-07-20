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

private enum class CategoryFilter(val label: String) {
    ALL("All"),
    CRITICAL_THINKING("Critical Thinking"),
    GENERAL_KNOWLEDGE("General Knowledge"),
    META_LEARNING("Meta-Learning"),
    SOCIAL_EMOTIONAL("Social/Emotional")
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SkillTreeScreen(navController: NavHostController) {
    val storage = AppStorage.storage
    var allSkills by remember { mutableStateOf<List<SkillWithProgress>>(emptyList()) }
    var selectedCategory by remember { mutableStateOf(CategoryFilter.ALL) }

    LaunchedEffect(Unit) { allSkills = storage.getSkills() }

    val filteredSkills = remember(allSkills, selectedCategory) {
        when (selectedCategory) {
            CategoryFilter.ALL -> allSkills
            CategoryFilter.CRITICAL_THINKING -> allSkills.filter { s ->
                s.category.equals("Critical Thinking", ignoreCase = true)
            }
            CategoryFilter.GENERAL_KNOWLEDGE -> allSkills.filter { s ->
                s.category.equals("General Knowledge", ignoreCase = true)
            }
            CategoryFilter.META_LEARNING -> allSkills.filter { s ->
                s.category.equals("Meta-Learning", ignoreCase = true)
            }
            CategoryFilter.SOCIAL_EMOTIONAL -> allSkills.filter { s ->
                s.category.equals("Social/Emotional", ignoreCase = true) ||
                    s.category.equals("SocialEmotional", ignoreCase = true) ||
                    s.category.equals("Social Emotional", ignoreCase = true)
            }
        }
    }

    GradientBackground {
        Scaffold(
            containerColor = Color.Transparent,
            topBar = {
                TopAppBar(
                    title = { Text("Your Skills", fontWeight = FontWeight.Bold) },
                    actions = { SoundToggleButton() },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent, titleContentColor = OnBackground)
                )
            }
        ) { padding ->
            Column(modifier = Modifier.fillMaxSize().padding(padding)) {
                CategoryFilterChips(selectedCategory, onCategorySelected = { selectedCategory = it })

                Spacer(modifier = Modifier.height(8.dp))

                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(filteredSkills, key = { it.id }) { skill ->
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
private fun CategoryFilterChips(selectedCategory: CategoryFilter, onCategorySelected: (CategoryFilter) -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        CategoryFilter.entries.forEach { category ->
            val isSelected = selectedCategory == category
            val bgColor = if (isSelected) Primary else Primary.copy(alpha = 0.15f)
            val txtColor = if (isSelected) OnPrimary else Primary
            Box(
                modifier = Modifier
                    .clip(MaterialTheme.shapes.small)
                    .clickable {
                        SoundManager.playTap()
                        onCategorySelected(category)
                    }
                    .then(
                        if (isSelected) Modifier else Modifier
                    )
            ) {
                Surface(
                    color = bgColor,
                    shape = MaterialTheme.shapes.small
                ) {
                    Text(
                        text = category.label,
                        style = MaterialTheme.typography.labelMedium,
                        color = txtColor,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                        maxLines = 1
                    )
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

    val catColor = rememberCategoryColor(skill.category)

    PremiumCard(
        onClick = onClick,
        gradient = Brush.verticalGradient(
            listOf(catColor.copy(alpha = 0.12f), Color.Transparent)
        )
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier.size(40.dp).clip(MaterialTheme.shapes.small),
                    contentAlignment = Alignment.Center
                ) {
                    androidx.compose.foundation.Canvas(modifier = Modifier.fillMaxSize()) {
                        drawRoundRect(color = catColor.copy(alpha = 0.25f))
                    }
                    Text(text = skill.icon, fontSize = 20.sp)
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(skill.name, style = MaterialTheme.typography.bodyMedium, color = OnSurface, fontWeight = FontWeight.SemiBold, maxLines = 1)
                    Text(skill.category, style = MaterialTheme.typography.labelSmall, color = catColor)
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
                    color = catColor,
                    trackColor = SurfaceVariant,
                    strokeCap = StrokeCap.Round
                )
            }
        }
    }
}

@Composable
private fun rememberCategoryColor(category: String): Color {
    return remember(category) {
        when (category.lowercase().replace(" ", "").replace("/", "")) {
            "criticalthinking" -> CriticalThinkingColor
            "generalknowledge" -> GeneralKnowledgeColor
            "metalearning", "meta-learning" -> MetaLearningColor
            "socialemotional", "social/emotional" -> SocialEmotionalColor
            else -> Primary
        }
    }
}
