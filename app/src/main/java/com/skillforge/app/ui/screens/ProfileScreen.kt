package com.skillforge.app.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.skillforge.app.data.*
import com.skillforge.app.ui.AppStorage
import com.skillforge.app.ui.components.GradientBackground
import com.skillforge.app.ui.components.PremiumCard
import com.skillforge.app.ui.components.SoundToggleButton
import com.skillforge.app.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(navController: NavHostController) {
    val storage = remember { AppStorage.storage }
    val user by remember { mutableStateOf(storage.getUser()) }
    val achievements by remember { mutableStateOf(storage.getAchievements()) }
    val unlockedAchievements by remember { mutableStateOf(storage.getUnlockedAchievements()) }

    val level = remember(user) { calculateLevelFromXP(user.totalXP) }
    val currentXP = remember(user) { user.totalXP }
    val xpForCurrent = remember(level) { xpForCurrentLevel(level) }
    val xpForNext = remember(level) { xpForNextLevel(level) }
    val xpProgress = remember(currentXP, xpForCurrent, xpForNext) {
        if (xpForNext > xpForCurrent) (((currentXP - xpForCurrent).toFloat() / (xpForNext - xpForCurrent).toFloat())).coerceIn(0f, 1f) else 0f
    }

    val unlockedCount = remember(unlockedAchievements) { unlockedAchievements.size }
    val totalCount = remember(achievements) { achievements.size }

    var showNameDialog by remember { mutableStateOf(false) }
    var showResetDialog by remember { mutableStateOf(false) }
    var userName by remember(user) { mutableStateOf(user.name) }

    if (showNameDialog) {
        NameChangeDialog(currentName = userName, onDismiss = { showNameDialog = false }, onConfirm = { newName ->
            userName = newName; storage.saveUserName(newName); showNameDialog = false
        })
    }
    if (showResetDialog) {
        ResetProgressDialog(onDismiss = { showResetDialog = false }, onConfirm = { storage.resetAll(); showResetDialog = false })
    }

    GradientBackground {
        Scaffold(
            containerColor = Color.Transparent,
            topBar = {
                TopAppBar(
                    title = { Text("Profile", fontWeight = FontWeight.Bold) },
                    actions = { SoundToggleButton() },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent, titleContentColor = OnBackground)
                )
            }
        ) { padding ->
            Column(
                modifier = Modifier.fillMaxSize().padding(padding).padding(horizontal = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(16.dp))

                LevelBadge(level = level, xpProgress = xpProgress)

                Spacer(modifier = Modifier.height(16.dp))

                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.clickable { showNameDialog = true }) {
                    Text(userName, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold, color = OnBackground)
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(Icons.Default.Edit, "Edit name", tint = Primary, modifier = Modifier.size(18.dp))
                }

                Spacer(modifier = Modifier.height(4.dp))
                Text("Level $level  •  $currentXP XP", style = MaterialTheme.typography.bodySmall, color = OnSurfaceVariant)

                Spacer(modifier = Modifier.height(16.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                    StreakInfo("Current Streak", "${user.currentStreak}", "🔥")
                    StreakInfo("Longest Streak", "${user.longestStreak}", "👑")
                }

                Spacer(modifier = Modifier.height(16.dp))

                PremiumCard {
                    Row(modifier = Modifier.fillMaxWidth().padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                        Text("📅", fontSize = 20.sp)
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text("Member Since", style = MaterialTheme.typography.labelSmall, color = OnSurfaceVariant)
                            Text(
                                java.text.SimpleDateFormat("MMM dd, yyyy", java.util.Locale.getDefault()).format(java.util.Date(user.createdAt)),
                                style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium, color = OnSurface
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Text("Achievements", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = OnBackground)
                    Text("$unlockedCount / $totalCount", style = MaterialTheme.typography.bodyMedium, color = Primary, fontWeight = FontWeight.SemiBold)
                }

                Spacer(modifier = Modifier.height(12.dp))

                LazyVerticalGrid(
                    columns = GridCells.Fixed(3),
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(achievements) { achievement ->
                        AchievementBadge(achievement = achievement, isUnlocked = achievement.id in unlockedAchievements)
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedButton(
                    onClick = { showResetDialog = true },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = ErrorColor),
                    border = ButtonDefaults.outlinedButtonBorder.copy(brush = androidx.compose.ui.graphics.SolidColor(ErrorColor))
                ) {
                    Icon(Icons.Default.Warning, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Reset Progress")
                }

                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Composable
private fun LevelBadge(level: Int, xpProgress: Float) {
    Box(contentAlignment = Alignment.Center, modifier = Modifier.size(100.dp)) {
        Canvas(modifier = Modifier.size(100.dp)) {
            drawCircle(color = PrimaryDark, radius = size.minDimension / 2f, style = Stroke(width = 4.dp.toPx()))
            val sweepAngle = xpProgress * 360f
            drawArc(color = Primary, startAngle = -90f, sweepAngle = sweepAngle, useCenter = false, style = Stroke(width = 6.dp.toPx()), topLeft = Offset(6.dp.toPx(), 6.dp.toPx()), size = Size(size.width - 12.dp.toPx(), size.height - 12.dp.toPx()))
        }
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("$level", style = MaterialTheme.typography.displayLarge, fontWeight = FontWeight.Bold, color = OnBackground)
            Text("LEVEL", style = MaterialTheme.typography.labelSmall, color = OnSurfaceVariant, letterSpacing = 2.sp)
        }
    }
}

@Composable
private fun StreakInfo(label: String, value: String, icon: String) {
    PremiumCard(glassEffect = true) {
        Column(modifier = Modifier.fillMaxWidth().padding(12.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Text(icon, fontSize = 24.sp)
            Spacer(modifier = Modifier.height(4.dp))
            Text(value, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = OnBackground)
            Text(label, style = MaterialTheme.typography.labelSmall, color = OnSurfaceVariant)
        }
    }
}

@Composable
private fun AchievementBadge(achievement: Achievement, isUnlocked: Boolean) {
    Card(
        modifier = Modifier.fillMaxWidth().aspectRatio(1f),
        shape = MaterialTheme.shapes.small,
        colors = CardDefaults.cardColors(containerColor = if (isUnlocked) Surface else SurfaceVariant.copy(alpha = 0.5f))
    ) {
        Column(modifier = Modifier.fillMaxSize().padding(8.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
            Text(achievement.icon, fontSize = 28.sp)
            Spacer(modifier = Modifier.height(4.dp))
            Text(achievement.name, style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = if (isUnlocked) OnSurface else OnSurfaceVariant.copy(alpha = 0.6f), textAlign = TextAlign.Center, maxLines = 2)
            if (isUnlocked) {
                Text(achievement.description, style = MaterialTheme.typography.labelSmall, color = OnSurfaceVariant, textAlign = TextAlign.Center, maxLines = 2)
            } else {
                Text("Locked", style = MaterialTheme.typography.labelSmall, color = OnSurfaceVariant.copy(alpha = 0.5f), textAlign = TextAlign.Center)
            }
        }
    }
}

@Composable
private fun NameChangeDialog(currentName: String, onDismiss: () -> Unit, onConfirm: (String) -> Unit) {
    var name by remember { mutableStateOf(currentName) }
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = Surface,
        title = { Text("Change Name", color = OnBackground, fontWeight = FontWeight.Bold) },
        text = {
            OutlinedTextField(
                value = name, onValueChange = { name = it }, label = { Text("Your Name") }, singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(focusedTextColor = OnBackground, unfocusedTextColor = OnBackground, focusedBorderColor = Primary, unfocusedBorderColor = OnSurfaceVariant, focusedLabelColor = Primary, unfocusedLabelColor = OnSurfaceVariant),
                modifier = Modifier.fillMaxWidth()
            )
        },
        confirmButton = { TextButton(onClick = { if (name.isNotBlank()) onConfirm(name.trim()) }) { Text("Save", color = Primary) } },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel", color = OnSurfaceVariant) } }
    )
}

@Composable
private fun ResetProgressDialog(onDismiss: () -> Unit, onConfirm: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = Surface,
        icon = { Icon(Icons.Default.Warning, contentDescription = null, tint = ErrorColor) },
        title = { Text("Reset All Progress?", color = OnBackground, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center) },
        text = { Text("This will permanently delete all your skills, achievements, game records, and XP. This action cannot be undone.", color = OnSurfaceVariant, textAlign = TextAlign.Center) },
        confirmButton = { TextButton(onClick = onConfirm, colors = ButtonDefaults.textButtonColors(contentColor = ErrorColor)) { Text("Reset Everything", fontWeight = FontWeight.Bold) } },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel", color = OnSurfaceVariant) } }
    )
}
