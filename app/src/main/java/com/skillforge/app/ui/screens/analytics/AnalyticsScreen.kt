package com.skillforge.app.ui.screens.analytics

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.skillforge.app.domain.model.Skill
import com.skillforge.app.ui.theme.*
import com.skillforge.app.ui.components.RadarChart
import com.skillforge.app.ui.components.BarChart
import com.skillforge.app.ui.components.ChartDataPoint

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AnalyticsScreen(
    viewModel: AnalyticsViewModel = hiltViewModel()
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
        item {
            Text(
                text = "Analytics",
                style = MaterialTheme.typography.headlineLarge,
                color = Primary
            )
        }

        // Overview Stats
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Surface),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        text = "Overall Performance",
                        style = MaterialTheme.typography.titleMedium,
                        color = OnSurface
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        AnalyticsStatItem(
                            value = "${uiState.totalCompleted}",
                            label = "Completed",
                            color = Secondary
                        )
                        AnalyticsStatItem(
                            value = "${uiState.totalCorrect}",
                            label = "Correct",
                            color = SuccessColor
                        )
                        AnalyticsStatItem(
                            value = "${uiState.daysActive}",
                            label = "Days Active",
                            color = Primary
                        )
                        AnalyticsStatItem(
                            value = "${if (uiState.totalCompleted > 0) (uiState.totalCorrect.toFloat() / uiState.totalCompleted * 100).toInt() else 0}%",
                            label = "Accuracy",
                            color = CriticalThinkingColor
                        )
                    }
                }
            }
        }

        // Skill Radar Chart
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Surface),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        text = "Skill Balance",
                        style = MaterialTheme.typography.titleMedium,
                        color = OnSurface
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Your proficiency across all domains",
                        style = MaterialTheme.typography.bodySmall,
                        color = OnSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    val radarData = listOf(
                        "Logic" to getSkillLevel(uiState.skills, "Logic"),
                        "Problem Solving" to getSkillLevel(uiState.skills, "Problem Solving"),
                        "Science" to getSkillLevel(uiState.skills, "Science"),
                        "History" to getSkillLevel(uiState.skills, "History"),
                        "Memory" to getSkillLevel(uiState.skills, "Memory"),
                        "Focus" to getSkillLevel(uiState.skills, "Focus"),
                        "Empathy" to getSkillLevel(uiState.skills, "Empathy"),
                        "Communication" to getSkillLevel(uiState.skills, "Communication")
                    )

                    RadarChart(
                        data = radarData,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(250.dp),
                        maxValue = 10f,
                        primaryColor = Primary
                    )
                }
            }
        }

        // Skill Bar Chart
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Surface),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        text = "Levels by Category",
                        style = MaterialTheme.typography.titleMedium,
                        color = OnSurface
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    val categoryData = listOf(
                        "CT" to getCategoryAvgLevel(uiState.skills, "Critical Thinking"),
                        "GK" to getCategoryAvgLevel(uiState.skills, "General Knowledge"),
                        "ML" to getCategoryAvgLevel(uiState.skills, "Meta-Learning"),
                        "SE" to getCategoryAvgLevel(uiState.skills, "Social/Emotional")
                    )

                    BarChart(
                        data = categoryData.map { ChartDataPoint(it.first, it.second) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(150.dp),
                        barColor = Secondary
                    )

                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        LegendItem("CT", CriticalThinkingColor)
                        LegendItem("GK", GeneralKnowledgeColor)
                        LegendItem("ML", MetaLearningColor)
                        LegendItem("SE", SocialEmotionalColor)
                    }
                }
            }
        }

        // Skill Breakdown
        item {
            Text(
                text = "Skill Breakdown",
                style = MaterialTheme.typography.titleMedium,
                color = OnSurface
            )
        }

        val groupedSkills = uiState.skills.groupBy { it.category }
        groupedSkills.forEach { (category, skills) ->
            item {
                val categoryColor = when (category) {
                    "Critical Thinking" -> CriticalThinkingColor
                    "General Knowledge" -> GeneralKnowledgeColor
                    "Meta-Learning" -> MetaLearningColor
                    "Social/Emotional" -> SocialEmotionalColor
                    else -> Primary
                }

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Surface),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(12.dp)
                                    .clip(CircleShape)
                                    .background(categoryColor)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = category,
                                style = MaterialTheme.typography.titleSmall,
                                color = OnSurface,
                                fontWeight = FontWeight.SemiBold
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        skills.forEach { skill ->
                            val (correct, total) = uiState.skillAccuracy[skill.id] ?: Pair(0, 0)
                            SkillAnalyticsRow(
                                skill = skill,
                                correct = correct,
                                total = total,
                                color = categoryColor
                            )
                            if (skill != skills.last()) {
                                Spacer(modifier = Modifier.height(8.dp))
                            }
                        }
                    }
                }
            }
        }

        item { Spacer(modifier = Modifier.height(16.dp))         }
    }
}

private fun getSkillLevel(skills: List<Skill>, name: String): Float {
    return skills.find { it.name == name }?.level?.toFloat() ?: 0f
}

private fun getCategoryAvgLevel(skills: List<Skill>, category: String): Float {
    val categorySkills = skills.filter { it.category == category }
    return if (categorySkills.isNotEmpty()) categorySkills.map { it.level }.average().toFloat() else 0f
}

@Composable
fun LegendItem(label: String, color: Color) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(8.dp)
                .clip(CircleShape)
                .background(color)
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = OnSurfaceVariant
        )
    }
}

@Composable
fun AnalyticsStatItem(value: String, label: String, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = value,
            style = MaterialTheme.typography.headlineSmall,
            color = color,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = OnSurfaceVariant,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun SkillAnalyticsRow(skill: Skill, correct: Int, total: Int, color: Color) {
    val accuracy = if (total > 0) correct.toFloat() / total else 0f

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = skill.name,
                    style = MaterialTheme.typography.bodyMedium,
                    color = OnSurface
                )
                Text(
                    text = "Lv.${skill.level}",
                    style = MaterialTheme.typography.labelSmall,
                    color = color
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "$correct/$total correct",
                    style = MaterialTheme.typography.labelSmall,
                    color = OnSurfaceVariant
                )
                Text(
                    text = "${(accuracy * 100).toInt()}%",
                    style = MaterialTheme.typography.labelSmall,
                    color = OnSurfaceVariant
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            LinearProgressIndicator(
                progress = accuracy,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(4.dp)
                    .clip(RoundedCornerShape(2.dp)),
                color = color,
                trackColor = SurfaceVariant,
            )
        }
    }
}
