package com.skillforge.app.ui.screens.games

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.skillforge.app.ui.AppStorage
import com.skillforge.app.ui.theme.*
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GameTicTacToeScreen(difficulty: String, navController: NavHostController) {
    var board by remember { mutableStateOf(Array(9) { "" }) }
    var isPlayerTurn by remember { mutableStateOf(true) }
    var winner by remember { mutableStateOf<String?>(null) }
    var scores by remember { mutableStateOf(Pair(0, 0)) }
    var gameOver by remember { mutableStateOf(false) }
    val isHard = difficulty == "hard"

    fun checkWinner(b: Array<String>): String? {
        val lines = listOf(
            listOf(0,1,2), listOf(3,4,5), listOf(6,7,8),
            listOf(0,3,6), listOf(1,4,7), listOf(2,5,8),
            listOf(0,4,8), listOf(2,4,6)
        )
        for (line in lines) {
            if (b[line[0]].isNotEmpty() && b[line[0]] == b[line[1]] && b[line[1]] == b[line[2]])
                return b[line[0]]
        }
        if (b.all { it.isNotEmpty() }) return "tie"
        return null
    }

    // Minimax for hard AI
    fun minimax(b: Array<String>, depth: Int, isMax: Boolean): Pair<Int, Int> {
        val w = checkWinner(b)
        if (w == "X") return Pair(-1, 10 - depth)
        if (w == "O") return Pair(-1, depth - 10)
        if (w == "tie") return Pair(-1, 0)

        val empty = b.indices.filter { b[it].isEmpty() }
        if (isMax) {
            var best = Int.MIN_VALUE
            var bestMove = empty.first()
            for (m in empty) {
                b[m] = "O"
                val score = minimax(b, depth + 1, false).second
                b[m] = ""
                if (score > best) { best = score; bestMove = m }
            }
            return Pair(bestMove, best)
        } else {
            var best = Int.MAX_VALUE
            var bestMove = empty.first()
            for (m in empty) {
                b[m] = "X"
                val score = minimax(b, depth + 1, true).second
                b[m] = ""
                if (score < best) { best = score; bestMove = m }
            }
            return Pair(bestMove, best)
        }
    }

    fun aiMove(b: Array<String>) {
        val empty = b.indices.filter { b[it].isEmpty() }
        if (empty.isEmpty()) return

        val move = if (isHard) {
            val result = minimax(b.map { it }.toTypedArray(), 0, false)
            result.first
        } else {
            empty.random()
        }

        val newBoard = b.map { it }.toTypedArray()
        newBoard[move] = "O"
        board = newBoard
        val w = checkWinner(newBoard)
        if (w != null) {
            winner = w
            if (w == "O") scores = Pair(scores.first, scores.second + 1)
            gameOver = true
        } else {
            isPlayerTurn = true
        }
    }

    fun playerMove(idx: Int) {
        if (board[idx].isNotEmpty() || !isPlayerTurn || winner != null || gameOver) return
        val newBoard = board.map { it }.toTypedArray()
        newBoard[idx] = "X"
        board = newBoard
        val w = checkWinner(newBoard)
        if (w != null) {
            winner = w
            if (w == "X") scores = Pair(scores.first + 1, scores.second)
            gameOver = true
        } else {
            isPlayerTurn = false
        }
    }

    LaunchedEffect(isPlayerTurn) {
        if (!isPlayerTurn && winner == null) {
            delay(500)
            aiMove(board)
        }
    }

    if (gameOver) {
        val msg = when (winner) { "X" -> "You Win!"; "O" -> "AI Wins!"; else -> "It's a Tie!" }
        val xp = if (winner == "X") 50 * (if (isHard) 3 else 1) else 10
        LaunchedEffect(Unit) {
            AppStorage.storage.saveGameResult("tic_tac_toe", scores.first * 100 + scores.second * -50, xp)
        }
        GameOverScreen(
            title = msg,
            stats = listOf("You ${scores.first} - ${scores.second} AI", "Difficulty: ${difficulty.replaceFirstChar{it.uppercase()}}"),
            score = scores.first * 100,
            xpEarned = xp,
            onPlayAgain = { board = Array(9) { "" }; isPlayerTurn = true; winner = null; gameOver = false },
            onBack = { navController.popBackStack() }
        )
        return
    }

    Column(modifier = Modifier.fillMaxSize()) {
        TopAppBar(
            title = { Text("Tic Tac Toe") },
            navigationIcon = { IconButton(onClick = { navController.popBackStack() }) { Icon(Icons.Filled.ArrowBack, "Back") } },
            colors = TopAppBarDefaults.topAppBarColors(containerColor = Surface, titleContentColor = OnBackground)
        )
        Text(text = "You (X) vs AI (O)", color = OnSurfaceVariant, modifier = Modifier.padding(16.dp), fontWeight = FontWeight.Bold)
        Text(text = "Score: You ${scores.first} - ${scores.second} AI", color = Primary, modifier = Modifier.padding(horizontal = 16.dp))

        Spacer(modifier = Modifier.height(24.dp))
        Column(modifier = Modifier.fillMaxWidth().padding(32.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            for (row in 0..2) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    for (col in 0..2) {
                        val idx = row * 3 + col
                        val mark = board[idx]
                        Box(
                            modifier = Modifier.size(90.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(if (mark == "X") Primary.copy(alpha = 0.2f) else if (mark == "O") ErrorColor.copy(alpha = 0.2f) else SurfaceVariant)
                                .border(2.dp, Surface.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                                .clickable(enabled = isPlayerTurn && winner == null) { playerMove(idx) },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = mark,
                                fontSize = 40.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (mark == "X") Primary else ErrorColor
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
        if (!isPlayerTurn) {
            Text(text = "AI is thinking...", color = OnSurfaceVariant, modifier = Modifier.fillMaxWidth().padding(16.dp), textAlign = TextAlign.Center)
        }
    }
}
