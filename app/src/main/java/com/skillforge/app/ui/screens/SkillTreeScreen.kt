@file:OptIn(ExperimentalMaterial3Api::class)

package com.skillforge.app.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.skillforge.app.data.SkillWithProgress
import com.skillforge.app.data.xpForCurrentLevel
import com.skillforge.app.data.xpForNextLevel
import com.skillforge.app.ui.AppStorage
import com.skillforge.app.ui.Screen
import com.skillforge.app.ui.theme.Background
import com.skillforge.app.ui.theme.CriticalThinkingColor
import com.skillforge.app.ui.theme.EasyColor
import com.skillforge.app.ui.theme.GeneralKnowledgeColor
import com.skillforge.app.ui.theme.HardColor
import com.skillforge.app.ui.theme.MediumColor
import com.skillforge.app.ui.theme.MetaLearningColor
import com.skillforge.app.ui.theme.OnBackground
import com.skillforge.app.ui.theme.OnSurface
import com.skillforge.app.ui.theme.OnSurfaceVariant
import com.skillforge.app.ui.theme.Primary
import com.skillforge.app.ui.theme.SocialEmotionalColor
import com.skillforge.app.ui.theme.Surface
import com.skillforge.app.ui.theme.SurfaceVariant

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

    LaunchedEffect(Unit) {
        allSkills = storage.getSkills()
    }

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

    Scaffold(
        containerColor = Background,
        topBar = {
            TopAppBar(
                title = { Text("Your Skills", fontWeight = FontWeight.Bold) },
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
        ) {
            CategoryFilterChips(
                selectedCategory = selectedCategory,
                onCategorySelected = { selectedCategory = it }
            )

            Spacer(modifier = Modifier.height(8.dp))

            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(filteredSkills, key = { it.id }) { skill ->
                    SkillCard(
                        skill = skill,
                        onClick = {
                            navController.navigate(Screen.SkillDetail.createRoute(skill.id.toString()))
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun CategoryFilterChips(
    selectedCategory: CategoryFilter,
    onCategorySelected: (CategoryFilter) -> Unit
) {
    val categories = CategoryFilter.entries
    LazyColumn(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp)
    ) {
        item {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                categories.forEach { category ->
                    FilterChip(
                        selected = selectedCategory == category,
                        onClick = { onCategorySelected(category) },
                        label = {
                            Text(
                                text = category.label,
                                style = MaterialTheme.typography.labelMedium,
                                maxLines = 1
                            )
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            containerColor = Primary.copy(alpha = 0.2f),
                            labelColor = Primary
                        )
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

    val categoryColor = rememberCategoryColor(skill.category)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(containerColor = Surface),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, categoryColor.copy(alpha = 0.5f))
    ) {
        Column(
            modifier = Modifier.padding(12.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = skill.icon,
                    style = MaterialTheme.typography.headlineMedium
                )
                Spacer(modifier = Modifier.width(8.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = skill.name,
                        style = MaterialTheme.typography.bodyLarge,
                        color = OnSurface,
                        fontWeight = FontWeight.SemiBold,
                        maxLines = 1
                    )
                    CategoryBadge(skill.category, categoryColor)
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Level $skillLevel",
                style = MaterialTheme.typography.titleSmall,
                color = OnSurfaceVariant,
                fontWeight = FontWeight.Medium
            )
            Spacer(modifier = Modifier.height(6.dp))
            LinearProgressIndicator(
                progress = progress.coerceIn(0f, 1f),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp)),
                color = categoryColor,
                trackColor = SurfaceVariant,
            )
        }
    }
}

@Composable
private fun CategoryBadge(category: String, color: Color) {
    Card(
        colors = CardDefaults.cardColors(containerColor = color.copy(alpha = 0.15f)),
        shape = RoundedCornerShape(6.dp)
    ) {
        Text(
            text = category,
            style = MaterialTheme.typography.labelSmall,
            color = color,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
            maxLines = 1
        )
    }
}

@Composable
private fun rememberCategoryColor(category: String): Color {
    return remember(category) {
        when (category.lowercase().replace(" ", "").replace("/", "")) {
            "criticalthinking" -> CriticalThinkingColor
            "generalknowledge" -> GeneralKnowledgeColor
            "metalearning", "meta-learning" -> MetaLearningColor
            "socialemotional", "social/emotional", "socialemotional" -> SocialEmotionalColor
            "easy" -> EasyColor
            "medium" -> MediumColor
            "hard" -> HardColor
            else -> Primary
        }
    }
}
