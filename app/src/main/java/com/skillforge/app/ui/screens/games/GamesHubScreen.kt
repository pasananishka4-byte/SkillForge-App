package com.skillforge.app.ui.screens.games

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.skillforge.app.ui.Screen
import com.skillforge.app.ui.theme.*

data class GameInfo(
    val name: String,
    val description: String,
    val icon: ImageVector,
    val color: Color,
    val route: String,
    val skill: String,
    val category: String
)

val availableGames = listOf(
    GameInfo("Memory Match", "Flip cards and find matching pairs", Icons.Filled.GridOn, Secondary, "game_memory_match", "Memory", "Meta-Learning"),
    GameInfo("Speed Round", "Answer questions in 30 seconds", Icons.Filled.Bolt, StreakFire, "game_speed_round", "GK Speed", "General Knowledge"),
    GameInfo("Pattern Puzzle", "Find the pattern, predict next", Icons.Filled.Psychology, CriticalThinkingColor, "game_pattern_puzzle", "Logic", "Critical Thinking"),
    GameInfo("Simon Says", "Watch and repeat the sequence", Icons.Filled.VideogameAsset, GeneralKnowledgeColor, "game_simon_says", "Focus", "Meta-Learning"),
    GameInfo("Code Breaker", "Crack the secret color code", Icons.Filled.Lock, ExpertColor, "game_code_breaker", "Logic", "Critical Thinking"),
    GameInfo("Word Scramble", "Unscramble letters to form words", Icons.Filled.Create, GeneralKnowledgeColor, "game_word_scramble", "Vocabulary", "General Knowledge"),
    GameInfo("Math Duel", "Solve math problems against time", Icons.Filled.Calculate, MetaLearningColor, "game_math_duel", "Math", "General Knowledge"),
    GameInfo("Visual Memory", "Remember tile positions on a grid", Icons.Filled.Visibility, SocialEmotionalColor, "game_visual_memory", "Memory", "Meta-Learning")
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GamesHubScreen(
    navController: NavController
) {
    var selectedDifficulty by remember { mutableStateOf("Normal") }
    val difficulties = listOf("Easy", "Normal", "Hard")

    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
            .padding(16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(bottom = 16.dp)
    ) {
        item(span = { GridItemSpan(2) }) {
            Column {
                Text("Mini Games", style = MaterialTheme.typography.headlineLarge, color = Primary)
                Text("Fun games to boost your skills", style = MaterialTheme.typography.bodyMedium, color = OnSurfaceVariant)
                Spacer(Modifier.height(8.dp))
            }
        }

        item(span = { GridItemSpan(2) }) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Surface),
                shape = RoundedCornerShape(16.dp)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp).fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("${availableGames.size}", style = MaterialTheme.typography.headlineSmall, color = Primary, fontWeight = FontWeight.Bold)
                        Text("Games", style = MaterialTheme.typography.labelSmall, color = OnSurfaceVariant)
                    }
                }
            }
        }

        item(span = { GridItemSpan(2) }) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Surface),
                shape = RoundedCornerShape(12.dp)
            ) {
                Row(
                    modifier = Modifier.padding(8.dp).fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    difficulties.forEach { diff ->
                        val isSelected = diff == selectedDifficulty
                        val diffColor = when (diff) {
                            "Easy" -> EasyColor
                            "Hard" -> HardColor
                            else -> MediumColor
                        }
                        Surface(
                            modifier = Modifier.weight(1f).clickable {
                                selectedDifficulty = diff
                            },
                            shape = RoundedCornerShape(8.dp),
                            color = if (isSelected) diffColor.copy(alpha = 0.2f) else Color.Transparent
                        ) {
                            Text(
                                diff,
                                style = MaterialTheme.typography.labelMedium,
                                color = if (isSelected) diffColor else OnSurfaceVariant,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(vertical = 10.dp)
                            )
                        }
                    }
                }
            }
        }

        item(span = { GridItemSpan(2) }) {
            Text("Logic & Strategy", style = MaterialTheme.typography.titleMedium, color = OnSurface, modifier = Modifier.padding(top = 4.dp))
        }

        val logicGames = availableGames.filter { it.category == "Critical Thinking" }
        items(logicGames.size) { idx ->
            val game = logicGames[idx]
            GameCard(game = game, onClick = {
                navController.navigate("${game.route}?difficulty=$selectedDifficulty")
            })
        }

        item(span = { GridItemSpan(2) }) {
            Text("Knowledge & Speed", style = MaterialTheme.typography.titleMedium, color = OnSurface, modifier = Modifier.padding(top = 4.dp))
        }

        val knowledgeGames = availableGames.filter { it.category == "General Knowledge" }
        items(knowledgeGames.size) { idx ->
            val game = knowledgeGames[idx]
            GameCard(game = game, onClick = {
                navController.navigate("${game.route}?difficulty=$selectedDifficulty")
            })
        }

        item(span = { GridItemSpan(2) }) {
            Text("Memory & Focus", style = MaterialTheme.typography.titleMedium, color = OnSurface, modifier = Modifier.padding(top = 4.dp))
        }

        val memoryGames = availableGames.filter { it.category == "Meta-Learning" || it.category == "Social/Emotional" }
        items(memoryGames.size) { idx ->
            val game = memoryGames[idx]
            GameCard(game = game, onClick = {
                navController.navigate("${game.route}?difficulty=$selectedDifficulty")
            })
        }

        item(span = { GridItemSpan(2) }) { Spacer(Modifier.height(8.dp)) }
    }
}

@Composable
fun GameCard(game: GameInfo, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(190.dp)
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(containerColor = Surface),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(14.dp).fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(game.color.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(game.icon, null, tint = game.color, modifier = Modifier.size(24.dp))
            }

            Column {
                Text(game.name, style = MaterialTheme.typography.titleSmall, color = OnSurface, fontWeight = FontWeight.Bold, maxLines = 1)
                Text(game.description, style = MaterialTheme.typography.bodySmall, color = OnSurfaceVariant, lineHeight = 14.sp, maxLines = 2)
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = game.color.copy(alpha = 0.1f)
                ) {
                    Text(game.skill, style = MaterialTheme.typography.labelSmall, color = game.color, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                }
            }
        }
    }
}
