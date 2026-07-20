package com.skillforge.app.ui.screens.games

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.skillforge.app.data.SoundManager
import com.skillforge.app.ui.AppStorage
import com.skillforge.app.ui.Screen
import com.skillforge.app.ui.components.GradientBackground
import com.skillforge.app.ui.components.PremiumCard
import com.skillforge.app.ui.components.SoundToggleButton
import com.skillforge.app.ui.theme.*
import androidx.compose.foundation.clickable

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
        GameCard("Memory Match", "🧠", "Find matching pairs", Screen.GameMemoryMatch.createRoute(difficulty)),
        GameCard("Speed Round", "⚡", "Answer fast", Screen.GameSpeedRound.createRoute(difficulty)),
        GameCard("Pattern Puzzle", "🔮", "Recreate patterns", Screen.GamePatternPuzzle.createRoute(difficulty)),
        GameCard("Simon Says", "🎵", "Repeat sequences", Screen.GameSimonSays.createRoute(difficulty)),
        GameCard("Code Breaker", "🔓", "Crack the code", Screen.GameCodeBreaker.createRoute(difficulty)),
        GameCard("Word Scramble", "📝", "Unscramble words", Screen.GameWordScramble.createRoute(difficulty)),
        GameCard("Math Duel", "🧮", "Solve math fast", Screen.GameMathDuel.createRoute(difficulty)),
        GameCard("Visual Memory", "👁️", "Remember positions", Screen.GameVisualMemory.createRoute(difficulty)),
        GameCard("Tic Tac Toe", "❌", "Play vs AI", Screen.GameTicTacToe.createRoute(difficulty)),
        GameCard("Color Match", "🎨", "Stroop test", Screen.GameColorMatch.createRoute(difficulty)),
        GameCard("Hangman", "💀", "Guess the word", Screen.GameHangman.createRoute(difficulty)),
        GameCard("Reaction Time", "🏃", "Tap to react", Screen.GameReactionTime.createRoute(difficulty))
    )

    GradientBackground {
        Scaffold(
            containerColor = Color.Transparent,
            topBar = {
                TopAppBar(
                    title = { Text("Games") },
                    navigationIcon = { IconButton(onClick = { navController.popBackStack() }) { Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back", tint = OnBackground) } },
                    actions = { SoundToggleButton() },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent, titleContentColor = OnBackground)
                )
            }
        ) { padding ->
            Column(modifier = Modifier.fillMaxSize().padding(padding)) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf("easy", "medium", "hard").forEach { d ->
                        val isSelected = difficulty == d
                        val chipColor = when (d) { "easy" -> EasyColor; "medium" -> MediumColor; "hard" -> HardColor; else -> Primary }
                        Box(
                            modifier = Modifier
                                .then(
                                    if (isSelected) Modifier else Modifier
                                )
                        ) {
                            Surface(
                                color = if (isSelected) chipColor else chipColor.copy(alpha = 0.15f),
                                shape = MaterialTheme.shapes.small
                            ) {
                                Text(
                                    text = d.replaceFirstChar { it.uppercase() },
                                    color = if (isSelected) OnPrimary else chipColor,
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                    modifier = Modifier.clickable {
                                        SoundManager.playTap()
                                        difficulty = d
                                    }.padding(horizontal = 14.dp, vertical = 8.dp)
                                )
                            }
                        }
                    }
                }

                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    contentPadding = PaddingValues(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(games) { game ->
                        val gameKey = game.name.lowercase().replace(" ", "_")
                        val gameStats = stats[gameKey]

                        PremiumCard(
                            onClick = {
                                SoundManager.playTap()
                                navController.navigate(game.route)
                            }
                        ) {
                            Column(modifier = Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(text = game.icon, fontSize = 32.sp)
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(text = game.name, color = OnBackground, fontWeight = FontWeight.Bold, fontSize = 13.sp, textAlign = TextAlign.Center)
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(text = game.description, color = OnSurfaceVariant, fontSize = 10.sp, textAlign = TextAlign.Center)
                                Spacer(modifier = Modifier.height(8.dp))
                                if (gameStats != null) {
                                    Text(text = "Best: ${gameStats.bestScore}", color = Primary, fontSize = 11.sp)
                                    Text(text = "Played: ${gameStats.gamesPlayed}", color = OnSurfaceVariant, fontSize = 9.sp)
                                } else {
                                    Text(text = "Tap to play!", color = OnSurfaceVariant, fontSize = 10.sp)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
