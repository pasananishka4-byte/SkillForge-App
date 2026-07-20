package com.skillforge.app.ui.screens.games

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
import kotlin.random.Random

private data class NBackTrial(val gridPos: Int, val soundLabel: String)

private val soundLabels = listOf("A", "B", "C", "D", "E", "F", "G", "H")

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GameDualNBackScreen(difficulty: String, navController: NavHostController) {
    val totalTrials = when(difficulty) { "hard" -> 30; "medium" -> 25; else -> 20 }
    val baseInterval = when(difficulty) { "hard" -> 2000L; "medium" -> 2500L; else -> 3000L }

    var nLevel by remember { mutableIntStateOf(1) }
    var currentTrial by remember { mutableIntStateOf(0) }
    var trials by remember { mutableStateOf(listOf<NBackTrial>()) }
    var score by remember { mutableIntStateOf(0) }
    var hits by remember { mutableIntStateOf(0) }
    var misses by remember { mutableIntStateOf(0) }
    var falseAlarms by remember { mutableIntStateOf(0) }
    var gameOver by remember { mutableStateOf(false) }
    var showGrid by remember { mutableStateOf(false) }
    var activePos by remember { mutableIntStateOf(-1) }
    var activeSound by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        val generated = mutableListOf<NBackTrial>()
        repeat(totalTrials) {
            generated.add(NBackTrial(Random.nextInt(9), soundLabels.random()))
        }
        trials = generated
        showGrid = true

        for (i in 0 until totalTrials) {
            activePos = trials[i].gridPos
            activeSound = trials[i].soundLabel
            delay(500)
            activePos = -1
            activeSound = ""
            delay(baseInterval)
            currentTrial = i + 1
        }
        gameOver = true
    }

    val isMatch = remember(currentTrial, nLevel, trials) {
        if (currentTrial > nLevel && currentTrial <= trials.size) {
            val curr = trials.getOrNull(currentTrial - 1)
            val prev = trials.getOrNull(currentTrial - 1 - nLevel)
            curr != null && prev != null && curr.gridPos == prev.gridPos
        } else false
    }

    val isSoundMatch = remember(currentTrial, nLevel, trials) {
        if (currentTrial > nLevel && currentTrial <= trials.size) {
            val curr = trials.getOrNull(currentTrial - 1)
            val prev = trials.getOrNull(currentTrial - 1 - nLevel)
            curr != null && prev != null && curr.soundLabel == prev.soundLabel
        } else false
    }

    var positionResponse by remember { mutableStateOf(false) }
    var soundResponse by remember { mutableStateOf(false) }
    var responseWindow by remember { mutableStateOf(false) }

    LaunchedEffect(currentTrial) {
        responseWindow = true
        delay(baseInterval / 2)
        responseWindow = false
        if (!positionResponse && isMatch) misses++
        if (!soundResponse && isSoundMatch) misses++
        positionResponse = false
        soundResponse = false
    }

    if (gameOver) {
        val xp = score * when(difficulty) { "hard" -> 5; "medium" -> 3; else -> 2 }
        LaunchedEffect(Unit) { AppStorage.storage.saveGameResult("dual_n_back", score, xp) }
        GameOverScreen(
            title = "Dual N-Back Complete!",
            stats = listOf("N-Level: $nLevel", "Hits: $hits", "Misses: $misses", "False Alarms: $falseAlarms"),
            score = score, xpEarned = xp,
            onPlayAgain = { navController.popBackStack(); navController.navigate("game_dual_n_back?difficulty=$difficulty") },
            onBack = { navController.popBackStack() }
        )
        return
    }

    Column(modifier = Modifier.fillMaxSize()) {
        TopAppBar(
            title = { Text("Dual N-Back") },
            navigationIcon = { IconButton(onClick = { navController.popBackStack() }) {             Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back") } },
            colors = TopAppBarDefaults.topAppBarColors(containerColor = Surface, titleContentColor = OnBackground)
        )
        Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 4.dp), horizontalArrangement = Arrangement.SpaceBetween) {
            Text("N-Level: $nLevel", color = WorkingMemoryColor, fontWeight = FontWeight.Bold)
            Text("Trial: $currentTrial/$totalTrials", color = OnSurfaceVariant)
            Text("Score: $score", color = Primary)
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text("Is this POSITION the same as $nLevel steps ago?", color = OnSurfaceVariant, fontSize = 12.sp, modifier = Modifier.padding(horizontal = 16.dp), textAlign = TextAlign.Center)

        Spacer(modifier = Modifier.height(12.dp))

        Box(modifier = Modifier.fillMaxWidth().padding(horizontal = 32.dp), contentAlignment = Alignment.Center) {
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                for (row in 0 until 3) {
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        for (col in 0 until 3) {
                            val idx = row * 3 + col
                            val isActive = idx == activePos
                            Box(
                                modifier = Modifier
                                    .size(90.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(if (isActive) WorkingMemoryColor else Surface)
                                    .border(2.dp, if (isActive) WorkingMemoryColor else SurfaceVariant, RoundedCornerShape(12.dp))
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
            if (activeSound.isNotEmpty()) {
                Text(activeSound, fontSize = 36.sp, fontWeight = FontWeight.Bold, color = Secondary)
            }
        }
        Text("Is this SOUND the same as $nLevel steps ago?", color = OnSurfaceVariant, fontSize = 12.sp, modifier = Modifier.padding(horizontal = 16.dp), textAlign = TextAlign.Center)

        Spacer(modifier = Modifier.weight(1f))

        if (responseWindow) {
            Row(modifier = Modifier.fillMaxWidth().padding(16.dp), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Button(
                    onClick = {
                        soundResponse = true
                        if (isSoundMatch) { score += 15; hits++ } else falseAlarms++
                    },
                    modifier = Modifier.weight(1f).height(60.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Secondary),
                    shape = RoundedCornerShape(12.dp),
                    enabled = !soundResponse
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("SOUND", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        Text("Match?", fontSize = 9.sp)
                    }
                }
                Button(
                    onClick = {
                        positionResponse = true
                        if (isMatch) { score += 15; hits++ } else falseAlarms++
                    },
                    modifier = Modifier.weight(1f).height(60.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = WorkingMemoryColor),
                    shape = RoundedCornerShape(12.dp),
                    enabled = !positionResponse
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("POSITION", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        Text("Match?", fontSize = 9.sp)
                    }
                }
            }
            if (!positionResponse && !soundResponse) {
                Button(
                    onClick = { /* skip - counts as miss */ },
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp).height(44.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = SurfaceVariant),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("No Match (skip)", color = OnSurfaceVariant)
                }
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
    }
}
