package com.skillforge.app.ui.screens.games

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.skillforge.app.ui.AppStorage
import com.skillforge.app.ui.Screen
import com.skillforge.app.ui.theme.*

data class GameCard(
    val name: String,
    val icon: String,
    val description: String,
    val route: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GamesHubScreen(navController: NavHostController) {
    var difficulty by remember { mutableStateOf("easy") }
    val stats = remember { AppStorage.storage.getGameStats() }

    val games = listOf(
        GameCard("Memory Match", "🧠", "Find matching pairs of cards", Screen.GameMemoryMatch.createRoute(difficulty)),
        GameCard("Speed Round", "⚡", "Answer as many questions as you can", Screen.GameSpeedRound.createRoute(difficulty)),
        GameCard("Pattern Puzzle", "🔮", "Remember and recreate the pattern", Screen.GamePatternPuzzle.createRoute(difficulty)),
        GameCard("Simon Says", "🎵", "Repeat the color sequence", Screen.GameSimonSays.createRoute(difficulty)),
        GameCard("Code Breaker", "🔓", "Crack the secret code", Screen.GameCodeBreaker.createRoute(difficulty)),
        GameCard("Word Scramble", "📝", "Unscramble the letters", Screen.GameWordScramble.createRoute(difficulty)),
        GameCard("Math Duel", "🧮", "Solve math problems fast", Screen.GameMathDuel.createRoute(difficulty)),
        GameCard("Visual Memory", "👁️", "Remember lit tile positions", Screen.GameVisualMemory.createRoute(difficulty))
    )

    Column(modifier = Modifier.fillMaxSize()) {
        TopAppBar(
            title = { Text("Games") },
            navigationIcon = {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(containerColor = Surface, titleContentColor = OnBackground)
        )

        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            listOf("easy", "medium", "hard").forEach { d ->
                FilterChip(
                    selected = difficulty == d,
                    onClick = { difficulty = d },
                    label = { Text(d.replaceFirstChar { it.uppercase() }) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = when(d) { "easy" -> EasyColor; "medium" -> MediumColor; "hard" -> HardColor; else -> Primary },
                        selectedLabelColor = OnPrimary
                    )
                )
            }
        }

        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            contentPadding = PaddingValues(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(games) { game ->
                val gameKey = game.name.lowercase().replace(" ", "_")
                val gameStats = stats[gameKey]

                Card(
                    modifier = Modifier.fillMaxWidth().clickable { navController.navigate(game.route) },
                    colors = CardDefaults.cardColors(containerColor = Surface),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = game.icon, fontSize = 32.sp)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(text = game.name, color = OnBackground, fontWeight = FontWeight.Bold, fontSize = 14.sp, textAlign = TextAlign.Center)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(text = game.description, color = OnSurfaceVariant, fontSize = 11.sp, textAlign = TextAlign.Center)
                        Spacer(modifier = Modifier.height(8.dp))
                        if (gameStats != null) {
                            Text(text = "Best: ${gameStats.bestScore}", color = Primary, fontSize = 12.sp)
                            Text(text = "Played: ${gameStats.gamesPlayed}", color = OnSurfaceVariant, fontSize = 10.sp)
                        } else {
                            Text(text = "Tap to play!", color = OnSurfaceVariant, fontSize = 11.sp)
                        }
                    }
                }
            }
        }
    }
}
