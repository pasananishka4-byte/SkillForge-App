package com.skillforge.app.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Widgets
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.skillforge.app.data.LocalStorage
import com.skillforge.app.ui.theme.Background
import com.skillforge.app.ui.theme.OnBackground
import com.skillforge.app.ui.theme.OnSurfaceVariant
import com.skillforge.app.ui.theme.Primary
import com.skillforge.app.ui.theme.Surface
import com.skillforge.app.ui.theme.SkillForgeTheme
import com.skillforge.app.ui.screens.*
import com.skillforge.app.ui.screens.games.*

object AppStorage {
    lateinit var storage: LocalStorage
}

sealed class Screen(val route: String) {
    object Onboarding : Screen("onboarding")
    object Home : Screen("home")
    object SkillTree : Screen("skill_tree")
    object Analytics : Screen("analytics")
    object Profile : Screen("profile")

    object ChallengeStart : Screen("challenge_start/{skillId}") {
        fun createRoute(skillId: String) = "challenge_start/$skillId"
    }
    object Challenge : Screen("challenge/{skillId}/{difficulty}") {
        fun createRoute(skillId: String, difficulty: String) = "challenge/$skillId/$difficulty"
    }
    object SkillDetail : Screen("skill_detail/{skillId}") {
        fun createRoute(skillId: String) = "skill_detail/$skillId"
    }
    object DailyChallenge : Screen("daily_challenge")
    object GamesHub : Screen("games_hub")

    object GameMemoryMatch : Screen("game_memory_match?difficulty={difficulty}") {
        fun createRoute(difficulty: String = "easy") = "game_memory_match?difficulty=$difficulty"
    }
    object GameSpeedRound : Screen("game_speed_round?difficulty={difficulty}") {
        fun createRoute(difficulty: String = "easy") = "game_speed_round?difficulty=$difficulty"
    }
    object GamePatternPuzzle : Screen("game_pattern_puzzle?difficulty={difficulty}") {
        fun createRoute(difficulty: String = "easy") = "game_pattern_puzzle?difficulty=$difficulty"
    }
    object GameSimonSays : Screen("game_simon_says?difficulty={difficulty}") {
        fun createRoute(difficulty: String = "easy") = "game_simon_says?difficulty=$difficulty"
    }
    object GameCodeBreaker : Screen("game_code_breaker?difficulty={difficulty}") {
        fun createRoute(difficulty: String = "easy") = "game_code_breaker?difficulty=$difficulty"
    }
    object GameWordScramble : Screen("game_word_scramble?difficulty={difficulty}") {
        fun createRoute(difficulty: String = "easy") = "game_word_scramble?difficulty=$difficulty"
    }
    object GameMathDuel : Screen("game_math_duel?difficulty={difficulty}") {
        fun createRoute(difficulty: String = "easy") = "game_math_duel?difficulty=$difficulty"
    }
    object GameVisualMemory : Screen("game_visual_memory?difficulty={difficulty}") {
        fun createRoute(difficulty: String = "easy") = "game_visual_memory?difficulty=$difficulty"
    }
    object GameTicTacToe : Screen("game_tic_tac_toe?difficulty={difficulty}") {
        fun createRoute(difficulty: String = "easy") = "game_tic_tac_toe?difficulty=$difficulty"
    }
    object GameColorMatch : Screen("game_color_match?difficulty={difficulty}") {
        fun createRoute(difficulty: String = "easy") = "game_color_match?difficulty=$difficulty"
    }
    object GameHangman : Screen("game_hangman?difficulty={difficulty}") {
        fun createRoute(difficulty: String = "easy") = "game_hangman?difficulty=$difficulty"
    }
    object GameReactionTime : Screen("game_reaction_time?difficulty={difficulty}") {
        fun createRoute(difficulty: String = "easy") = "game_reaction_time?difficulty=$difficulty"
    }
}

data class BottomNavItem(
    val label: String,
    val icon: ImageVector,
    val route: String
)

val bottomNavItems = listOf(
    BottomNavItem("Home", Icons.Filled.Home, Screen.Home.route),
    BottomNavItem("Skills", Icons.Filled.Widgets, Screen.SkillTree.route),
    BottomNavItem("Analytics", Icons.Filled.BarChart, Screen.Analytics.route),
    BottomNavItem("Profile", Icons.Filled.Person, Screen.Profile.route)
)

@Composable
fun SkillForgeApp() {
    SkillForgeTheme {
        val navController = rememberNavController()
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry?.destination?.route

        LaunchedEffect(Unit) {
            if (!AppStorage.storage.isOnboardingComplete()) {
                navController.navigate(Screen.Onboarding.route) {
                    popUpTo(0) { inclusive = true }
                }
            }
        }

        val showBottomBar = currentRoute in bottomNavItems.map { it.route }

        Scaffold(
            containerColor = Background,
            bottomBar = {
                if (showBottomBar) {
                    NavigationBar(
                        containerColor = Surface,
                        contentColor = OnBackground
                    ) {
                        bottomNavItems.forEach { item ->
                            val selected = currentRoute == item.route
                            NavigationBarItem(
                                selected = selected,
                                onClick = {
                                    if (currentRoute != item.route) {
                                        navController.navigate(item.route) {
                                            popUpTo(navController.graph.findStartDestination().id) {
                                                saveState = true
                                            }
                                            launchSingleTop = true
                                            restoreState = true
                                        }
                                    }
                                },
                                icon = {
                                    Icon(
                                        imageVector = item.icon,
                                        contentDescription = item.label
                                    )
                                },
                                label = { Text(item.label) },
                                colors = NavigationBarItemDefaults.colors(
                                    selectedIconColor = Primary,
                                    selectedTextColor = Primary,
                                    unselectedIconColor = OnSurfaceVariant,
                                    unselectedTextColor = OnSurfaceVariant,
                                    indicatorColor = Surface
                                )
                            )
                        }
                    }
                }
            }
            ) { paddingValues ->
            NavHost(
                navController = navController,
                startDestination = Screen.Home.route,
                modifier = Modifier.padding(paddingValues)
            ) {
                composable(Screen.Onboarding.route) {
                    OnboardingScreen(navController = navController)
                }
                composable(Screen.Home.route) {
                    HomeScreen(navController = navController)
                }
                composable(Screen.SkillTree.route) {
                    SkillTreeScreen(navController = navController)
                }
                composable(Screen.Analytics.route) {
                    AnalyticsScreen(navController = navController)
                }
                composable(Screen.Profile.route) {
                    ProfileScreen(navController = navController)
                }

                composable(
                    route = Screen.ChallengeStart.route,
                    arguments = listOf(navArgument("skillId") { type = NavType.StringType })
                ) { backStackEntry ->
                    val skillId = backStackEntry.arguments?.getString("skillId") ?: ""
                    ChallengeStartScreen(skillId = skillId, navController = navController)
                }
                composable(
                    route = Screen.Challenge.route,
                    arguments = listOf(
                        navArgument("skillId") { type = NavType.StringType },
                        navArgument("difficulty") { type = NavType.StringType }
                    )
                ) { backStackEntry ->
                    val skillId = backStackEntry.arguments?.getString("skillId") ?: ""
                    val difficulty = backStackEntry.arguments?.getString("difficulty") ?: ""
                    ChallengePlayScreen(
                        skillId = skillId,
                        difficulty = difficulty,
                        navController = navController
                    )
                }
                composable(
                    route = Screen.SkillDetail.route,
                    arguments = listOf(navArgument("skillId") { type = NavType.StringType })
                ) { backStackEntry ->
                    val skillId = backStackEntry.arguments?.getString("skillId") ?: ""
                    SkillDetailScreen(skillId = skillId, navController = navController)
                }
                composable(Screen.DailyChallenge.route) {
                    DailyChallengeScreen(navController = navController)
                }
                composable(Screen.GamesHub.route) {
                    GamesHubScreen(navController = navController)
                }

                composable(
                    route = Screen.GameMemoryMatch.route,
                    arguments = listOf(navArgument("difficulty") {
                        type = NavType.StringType
                        defaultValue = "easy"
                    })
                ) { backStackEntry ->
                    val difficulty = backStackEntry.arguments?.getString("difficulty") ?: "easy"
                    GameMemoryMatchScreen(difficulty = difficulty, navController = navController)
                }
                composable(
                    route = Screen.GameSpeedRound.route,
                    arguments = listOf(navArgument("difficulty") {
                        type = NavType.StringType
                        defaultValue = "easy"
                    })
                ) { backStackEntry ->
                    val difficulty = backStackEntry.arguments?.getString("difficulty") ?: "easy"
                    GameSpeedRoundScreen(difficulty = difficulty, navController = navController)
                }
                composable(
                    route = Screen.GamePatternPuzzle.route,
                    arguments = listOf(navArgument("difficulty") {
                        type = NavType.StringType
                        defaultValue = "easy"
                    })
                ) { backStackEntry ->
                    val difficulty = backStackEntry.arguments?.getString("difficulty") ?: "easy"
                    GamePatternPuzzleScreen(difficulty = difficulty, navController = navController)
                }
                composable(
                    route = Screen.GameSimonSays.route,
                    arguments = listOf(navArgument("difficulty") {
                        type = NavType.StringType
                        defaultValue = "easy"
                    })
                ) { backStackEntry ->
                    val difficulty = backStackEntry.arguments?.getString("difficulty") ?: "easy"
                    GameSimonSaysScreen(difficulty = difficulty, navController = navController)
                }
                composable(
                    route = Screen.GameCodeBreaker.route,
                    arguments = listOf(navArgument("difficulty") {
                        type = NavType.StringType
                        defaultValue = "easy"
                    })
                ) { backStackEntry ->
                    val difficulty = backStackEntry.arguments?.getString("difficulty") ?: "easy"
                    GameCodeBreakerScreen(difficulty = difficulty, navController = navController)
                }
                composable(
                    route = Screen.GameWordScramble.route,
                    arguments = listOf(navArgument("difficulty") {
                        type = NavType.StringType
                        defaultValue = "easy"
                    })
                ) { backStackEntry ->
                    val difficulty = backStackEntry.arguments?.getString("difficulty") ?: "easy"
                    GameWordScrambleScreen(difficulty = difficulty, navController = navController)
                }
                composable(
                    route = Screen.GameMathDuel.route,
                    arguments = listOf(navArgument("difficulty") {
                        type = NavType.StringType
                        defaultValue = "easy"
                    })
                ) { backStackEntry ->
                    val difficulty = backStackEntry.arguments?.getString("difficulty") ?: "easy"
                    GameMathDuelScreen(difficulty = difficulty, navController = navController)
                }
                composable(
                    route = Screen.GameVisualMemory.route,
                    arguments = listOf(navArgument("difficulty") {
                        type = NavType.StringType
                        defaultValue = "easy"
                    })
                ) { backStackEntry ->
                    val difficulty = backStackEntry.arguments?.getString("difficulty") ?: "easy"
                    GameVisualMemoryScreen(difficulty = difficulty, navController = navController)
                }
                composable(
                    route = Screen.GameTicTacToe.route,
                    arguments = listOf(navArgument("difficulty") {
                        type = NavType.StringType
                        defaultValue = "easy"
                    })
                ) { backStackEntry ->
                    val difficulty = backStackEntry.arguments?.getString("difficulty") ?: "easy"
                    GameTicTacToeScreen(difficulty = difficulty, navController = navController)
                }
                composable(
                    route = Screen.GameColorMatch.route,
                    arguments = listOf(navArgument("difficulty") {
                        type = NavType.StringType
                        defaultValue = "easy"
                    })
                ) { backStackEntry ->
                    val difficulty = backStackEntry.arguments?.getString("difficulty") ?: "easy"
                    GameColorMatchScreen(difficulty = difficulty, navController = navController)
                }
                composable(
                    route = Screen.GameHangman.route,
                    arguments = listOf(navArgument("difficulty") {
                        type = NavType.StringType
                        defaultValue = "easy"
                    })
                ) { backStackEntry ->
                    val difficulty = backStackEntry.arguments?.getString("difficulty") ?: "easy"
                    GameHangmanScreen(difficulty = difficulty, navController = navController)
                }
                composable(
                    route = Screen.GameReactionTime.route,
                    arguments = listOf(navArgument("difficulty") {
                        type = NavType.StringType
                        defaultValue = "easy"
                    })
                ) { backStackEntry ->
                    val difficulty = backStackEntry.arguments?.getString("difficulty") ?: "easy"
                    GameReactionTimeScreen(difficulty = difficulty, navController = navController)
                }
            }
        }
    }
}
