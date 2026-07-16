package com.skillforge.app.ui

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.AccountTree
import androidx.compose.material.icons.outlined.BarChart
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.AccountTree
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.skillforge.app.ui.theme.Primary
import com.skillforge.app.ui.theme.Surface
import com.skillforge.app.ui.theme.SurfaceVariant
import com.skillforge.app.ui.screens.home.HomeScreen
import com.skillforge.app.ui.screens.skilltree.SkillTreeScreen
import com.skillforge.app.ui.screens.challenge.ChallengeScreen
import com.skillforge.app.ui.screens.analytics.AnalyticsScreen
import com.skillforge.app.ui.screens.profile.ProfileScreen
import com.skillforge.app.ui.screens.challenge.ChallengeStartScreen
import com.skillforge.app.ui.screens.skilltree.SkillDetailScreen
import com.skillforge.app.ui.screens.onboarding.OnboardingScreen
import com.skillforge.app.ui.screens.onboarding.OnboardingViewModel
import com.skillforge.app.ui.screens.daily.DailyChallengeScreen
import com.skillforge.app.ui.screens.games.GamesHubScreen
import com.skillforge.app.ui.screens.games.MemoryMatchGame
import com.skillforge.app.ui.screens.games.SpeedRoundGame
import com.skillforge.app.ui.screens.games.PatternPuzzleGame
import com.skillforge.app.ui.screens.games.SimonSaysGame
import com.skillforge.app.ui.screens.games.CodeBreakerGame
import com.skillforge.app.ui.screens.games.WordScrambleGame
import com.skillforge.app.ui.screens.games.MathDuelGame
import com.skillforge.app.ui.screens.games.VisualMemoryGame

sealed class Screen(val route: String) {
    data object Onboarding : Screen("onboarding")
    data object Home : Screen("home")
    data object SkillTree : Screen("skill_tree")
    data object Analytics : Screen("analytics")
    data object Profile : Screen("profile")
    data object Challenge : Screen("challenge/{skillId}/{difficulty}") {
        fun createRoute(skillId: Long, difficulty: String) = "challenge/$skillId/$difficulty"
    }
    data object ChallengeStart : Screen("challenge_start/{skillId}") {
        fun createRoute(skillId: Long) = "challenge_start/$skillId"
    }
    data object SkillDetail : Screen("skill_detail/{skillId}") {
        fun createRoute(skillId: Long) = "skill_detail/$skillId"
    }
    data object DailyChallenge : Screen("daily_challenge")
    data object GamesHub : Screen("games_hub")
    data object MemoryMatch : Screen("game_memory_match?difficulty={difficulty}") {
        fun createRoute(difficulty: String = "Normal") = "game_memory_match?difficulty=$difficulty"
    }
    data object SpeedRound : Screen("game_speed_round?difficulty={difficulty}") {
        fun createRoute(difficulty: String = "Normal") = "game_speed_round?difficulty=$difficulty"
    }
    data object PatternPuzzle : Screen("game_pattern_puzzle?difficulty={difficulty}") {
        fun createRoute(difficulty: String = "Normal") = "game_pattern_puzzle?difficulty=$difficulty"
    }
    data object SimonSays : Screen("game_simon_says?difficulty={difficulty}") {
        fun createRoute(difficulty: String = "Normal") = "game_simon_says?difficulty=$difficulty"
    }
    data object CodeBreaker : Screen("game_code_breaker?difficulty={difficulty}") {
        fun createRoute(difficulty: String = "Normal") = "game_code_breaker?difficulty=$difficulty"
    }
    data object WordScramble : Screen("game_word_scramble?difficulty={difficulty}") {
        fun createRoute(difficulty: String = "Normal") = "game_word_scramble?difficulty=$difficulty"
    }
    data object MathDuel : Screen("game_math_duel?difficulty={difficulty}") {
        fun createRoute(difficulty: String = "Normal") = "game_math_duel?difficulty=$difficulty"
    }
    data object VisualMemory : Screen("game_visual_memory?difficulty={difficulty}") {
        fun createRoute(difficulty: String = "Normal") = "game_visual_memory?difficulty=$difficulty"
    }
}

data class BottomNavItem(
    val label: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    val route: String
)

val bottomNavItems = listOf(
    BottomNavItem("Home", Icons.Filled.Home, Icons.Outlined.Home, Screen.Home.route),
    BottomNavItem("Skills", Icons.Filled.AccountTree, Icons.Outlined.AccountTree, Screen.SkillTree.route),
    BottomNavItem("Analytics", Icons.Filled.BarChart, Icons.Outlined.BarChart, Screen.Analytics.route),
    BottomNavItem("Profile", Icons.Filled.Person, Icons.Outlined.Person, Screen.Profile.route)
)

@Composable
fun SkillForgeApp() {
    val onboardingViewModel: OnboardingViewModel = hiltViewModel()
    val isOnboardingComplete by onboardingViewModel.isOnboardingComplete.collectAsState()

    if (!isOnboardingComplete) {
        OnboardingScreen(onComplete = { onboardingViewModel.completeOnboarding() })
        return
    }

    MainAppContent()
}

@Composable
fun MainAppContent() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    val showBottomBar = bottomNavItems.any { it.route == currentDestination?.route }

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                NavigationBar(containerColor = Surface) {
                    bottomNavItems.forEach { item ->
                        val selected = currentDestination?.hierarchy?.any { it.route == item.route } == true
                        NavigationBarItem(
                            icon = {
                                Icon(
                                    imageVector = if (selected) item.selectedIcon else item.unselectedIcon,
                                    contentDescription = item.label
                                )
                            },
                            label = { Text(item.label) },
                            selected = selected,
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = Primary,
                                selectedTextColor = Primary,
                                unselectedIconColor = SurfaceVariant,
                                unselectedTextColor = SurfaceVariant,
                                indicatorColor = Primary.copy(alpha = 0.12f)
                            ),
                            onClick = {
                                navController.navigate(item.route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Home.route,
            modifier = Modifier.padding(innerPadding),
            enterTransition = { fadeIn(animationSpec = tween(300)) },
            exitTransition = { fadeOut(animationSpec = tween(300)) }
        ) {
            composable(Screen.Home.route) {
                HomeScreen(navController = navController)
            }
            composable(Screen.SkillTree.route) {
                SkillTreeScreen(navController = navController)
            }
            composable(Screen.Analytics.route) {
                AnalyticsScreen()
            }
            composable(Screen.Profile.route) {
                ProfileScreen()
            }
            composable(
                route = Screen.ChallengeStart.route,
                arguments = listOf(navArgument("skillId") { type = NavType.LongType })
            ) { backStackEntry ->
                val skillId = backStackEntry.arguments?.getLong("skillId") ?: 0L
                ChallengeStartScreen(
                    skillId = skillId,
                    navController = navController
                )
            }
            composable(
                route = Screen.Challenge.route,
                arguments = listOf(
                    navArgument("skillId") { type = NavType.LongType },
                    navArgument("difficulty") { type = NavType.StringType }
                )
            ) { backStackEntry ->
                val skillId = backStackEntry.arguments?.getLong("skillId") ?: 0L
                val difficulty = backStackEntry.arguments?.getString("difficulty") ?: "Easy"
                ChallengeScreen(
                    skillId = skillId,
                    difficulty = difficulty,
                    navController = navController
                )
            }
            composable(
                route = Screen.SkillDetail.route,
                arguments = listOf(navArgument("skillId") { type = NavType.LongType })
            ) { backStackEntry ->
                val skillId = backStackEntry.arguments?.getLong("skillId") ?: 0L
                SkillDetailScreen(
                    skillId = skillId,
                    navController = navController
                )
            }
            composable(Screen.DailyChallenge.route) {
                DailyChallengeScreen(
                    onBack = { navController.popBackStack() }
                )
            }
            composable(Screen.GamesHub.route) {
                GamesHubScreen(navController = navController)
            }
            composable(
                route = Screen.MemoryMatch.route,
                arguments = listOf(navArgument("difficulty") { type = NavType.StringType; defaultValue = "Normal" })
            ) { backStackEntry ->
                val diff = backStackEntry.arguments?.getString("difficulty") ?: "Normal"
                MemoryMatchGame(
                    onBack = { navController.popBackStack() },
                    difficulty = diff,
                    onGameComplete = { _, _ -> }
                )
            }
            composable(
                route = Screen.SpeedRound.route,
                arguments = listOf(navArgument("difficulty") { type = NavType.StringType; defaultValue = "Normal" })
            ) { backStackEntry ->
                val diff = backStackEntry.arguments?.getString("difficulty") ?: "Normal"
                SpeedRoundGame(
                    onBack = { navController.popBackStack() },
                    difficulty = diff,
                    onGameComplete = { _, _ -> }
                )
            }
            composable(
                route = Screen.PatternPuzzle.route,
                arguments = listOf(navArgument("difficulty") { type = NavType.StringType; defaultValue = "Normal" })
            ) { backStackEntry ->
                val diff = backStackEntry.arguments?.getString("difficulty") ?: "Normal"
                PatternPuzzleGame(
                    onBack = { navController.popBackStack() },
                    difficulty = diff,
                    onGameComplete = { _, _ -> }
                )
            }
            composable(
                route = Screen.SimonSays.route,
                arguments = listOf(navArgument("difficulty") { type = NavType.StringType; defaultValue = "Normal" })
            ) { backStackEntry ->
                val diff = backStackEntry.arguments?.getString("difficulty") ?: "Normal"
                SimonSaysGame(
                    onBack = { navController.popBackStack() },
                    difficulty = diff,
                    onGameComplete = { _, _ -> }
                )
            }
            composable(
                route = Screen.CodeBreaker.route,
                arguments = listOf(navArgument("difficulty") { type = NavType.StringType; defaultValue = "Normal" })
            ) { backStackEntry ->
                val diff = backStackEntry.arguments?.getString("difficulty") ?: "Normal"
                CodeBreakerGame(
                    onBack = { navController.popBackStack() },
                    difficulty = diff,
                    onGameComplete = { _, _ -> }
                )
            }
            composable(
                route = Screen.WordScramble.route,
                arguments = listOf(navArgument("difficulty") { type = NavType.StringType; defaultValue = "Normal" })
            ) { backStackEntry ->
                val diff = backStackEntry.arguments?.getString("difficulty") ?: "Normal"
                WordScrambleGame(
                    onBack = { navController.popBackStack() },
                    difficulty = diff,
                    onGameComplete = { _, _ -> }
                )
            }
            composable(
                route = Screen.MathDuel.route,
                arguments = listOf(navArgument("difficulty") { type = NavType.StringType; defaultValue = "Normal" })
            ) { backStackEntry ->
                val diff = backStackEntry.arguments?.getString("difficulty") ?: "Normal"
                MathDuelGame(
                    onBack = { navController.popBackStack() },
                    difficulty = diff,
                    onGameComplete = { _, _ -> }
                )
            }
            composable(
                route = Screen.VisualMemory.route,
                arguments = listOf(navArgument("difficulty") { type = NavType.StringType; defaultValue = "Normal" })
            ) { backStackEntry ->
                val diff = backStackEntry.arguments?.getString("difficulty") ?: "Normal"
                VisualMemoryGame(
                    onBack = { navController.popBackStack() },
                    difficulty = diff,
                    onGameComplete = { _, _ -> }
                )
            }
        }
    }
}
