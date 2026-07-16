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
import androidx.compose.runtime.Composable
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
    val skill: String
)

val availableGames = listOf(
    GameInfo(
        name = "Memory Match",
        description = "Flip cards and find matching pairs",
        icon = Icons.Filled.GridOn,
        color = Secondary,
        route = "game_memory_match",
        skill = "Memory"
    ),
    GameInfo(
        name = "Speed Round",
        description = "Answer as many questions as you can in 30s",
        icon = Icons.Filled.Bolt,
        color = StreakFire,
        route = "game_speed_round",
        skill = "General Knowledge"
    ),
    GameInfo(
        name = "Pattern Puzzle",
        description = "Find the pattern and predict what comes next",
        icon = Icons.Filled.Psychology,
        color = CriticalThinkingColor,
        route = "game_pattern_puzzle",
        skill = "Logic"
    ),
    GameInfo(
        name = "Simon Says",
        description = "Watch the sequence, then repeat it",
        icon = Icons.Filled.VideogameAsset,
        color = GeneralKnowledgeColor,
        route = "game_simon_says",
        skill = "Focus"
    )
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GamesHubScreen(navController: NavController) {
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
                Text(
                    text = "Mini Games",
                    style = MaterialTheme.typography.headlineLarge,
                    color = Primary
                )
                Text(
                    text = "Fun games to boost your skills",
                    style = MaterialTheme.typography.bodyMedium,
                    color = OnSurfaceVariant
                )
                Spacer(Modifier.height(8.dp))
            }
        }

        items(availableGames) { game ->
            GameCard(
                game = game,
                onClick = { navController.navigate(game.route) }
            )
        }

        item(span = { GridItemSpan(2) }) {
            Spacer(Modifier.height(8.dp))
        }
    }
}

@Composable
fun GameCard(game: GameInfo, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(180.dp)
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(containerColor = Surface),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(game.color.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = game.icon,
                    contentDescription = null,
                    tint = game.color,
                    modifier = Modifier.size(28.dp)
                )
            }

            Column {
                Text(
                    text = game.name,
                    style = MaterialTheme.typography.titleMedium,
                    color = OnSurface,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = game.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = OnSurfaceVariant,
                    lineHeight = 16.sp
                )
            }

            Surface(
                shape = RoundedCornerShape(6.dp),
                color = game.color.copy(alpha = 0.1f)
            ) {
                Text(
                    text = game.skill,
                    style = MaterialTheme.typography.labelSmall,
                    color = game.color,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                )
            }
        }
    }
}
