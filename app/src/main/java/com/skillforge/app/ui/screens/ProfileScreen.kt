package com.skillforge.app.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.skillforge.app.data.*
import com.skillforge.app.ui.AppStorage
import com.skillforge.app.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(navController: NavHostController) {
    val storage = remember { AppStorage.storage }
    val user by remember { mutableStateOf(storage.getUser()) }
    val achievements by remember { mutableStateOf(storage.getAchievements()) }
    val unlockedAchievements by remember { mutableStateOf(storage.getUnlockedAchievements()) }

    val level = remember(user) {
        if (user != null) calculateLevelFromXP(user!!.totalXP) else 0
    }

    val currentXP = remember(user) { user?.totalXP ?: 0L }
    val xpForCurrent = remember(level) { xpForCurrentLevel(level) }
    val xpForNext = remember(level) { xpForNextLevel(level) }
    val xpProgress = remember(currentXP, xpForCurrent, xpForNext) {
        if (xpForNext > xpForCurrent) {
            (((currentXP - xpForCurrent).toFloat() / (xpForNext - xpForCurrent).toFloat())).coerceIn(0f, 1f)
        } else 0f
    }

    val unlockedCount = remember(unlockedAchievements) {
        unlockedAchievements.size
    }
    val totalCount = remember(achievements) { achievements.size }

    var showNameDialog by remember { mutableStateOf(false) }
    var showResetDialog by remember { mutableStateOf(false) }
    var userName by remember(user) { mutableStateOf(user?.name ?: "Player") }

    if (showNameDialog) {
        NameChangeDialog(
            currentName = userName,
            onDismiss = { showNameDialog = false },
            onConfirm = { newName ->
                userName = newName
                storage.saveUserName(newName)
                showNameDialog = false
            }
        )
    }

    if (showResetDialog) {
        ResetProgressDialog(
            onDismiss = { showResetDialog = false },
            onConfirm = {
                storage.resetAll()
                showResetDialog = false
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Profile",
                        color = OnBackground,
                        fontWeight = FontWeight.Bold
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Background
                )
            )
        },
        containerColor = Background
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            // Level Badge
            LevelBadge(level = level, xpProgress = xpProgress)

            Spacer(modifier = Modifier.height(16.dp))

            // User Name
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.clickable { showNameDialog = true }
            ) {
                Text(
                    text = userName,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = OnBackground
                )
                Spacer(modifier = Modifier.width(8.dp))
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = "Edit name",
                    tint = Primary,
                    modifier = Modifier.size(18.dp)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // XP and Level
            Text(
                text = "Level $level  •  $currentXP XP",
                fontSize = 14.sp,
                color = OnSurfaceVariant
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Streak Info
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                StreakInfo(
                    label = "Current Streak",
                    value = "${user?.currentStreak ?: 0}",
                    icon = "🔥"
                )
                StreakInfo(
                    label = "Longest Streak",
                    value = "${user?.longestStreak ?: 0}",
                    icon = "👑"
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Member Since
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Surface)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "📅",
                        fontSize = 20.sp
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "Member Since",
                            fontSize = 12.sp,
                            color = OnSurfaceVariant
                        )
                        Text(
                            text = user?.let {
                            java.text.SimpleDateFormat("MMM dd, yyyy", java.util.Locale.getDefault())
                                .format(java.util.Date(it.createdAt))
                        } ?: "Unknown",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            color = OnSurface
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Achievement Progress
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Achievements",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = OnBackground
                )
                Text(
                    text = "$unlockedCount / $totalCount",
                    fontSize = 14.sp,
                    color = Primary
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Achievement Grid
            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(achievements) { achievement ->
                    AchievementBadge(achievement = achievement)
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Reset Button
            OutlinedButton(
                onClick = { showResetDialog = true },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = Color(0xFFFF5252)
                ),
                border = ButtonDefaults.outlinedButtonBorder.copy(
                    brush = androidx.compose.ui.graphics.SolidColor(Color(0xFFFF5252))
                )
            ) {
                Icon(
                    imageVector = Icons.Default.Warning,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Reset Progress")
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
private fun LevelBadge(level: Int, xpProgress: Float) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier.size(100.dp)
    ) {
        // Outer gold border circle
        Canvas(modifier = Modifier.size(100.dp)) {
            // Gold border
            drawCircle(
                color = Color(0xFFFFD700),
                radius = size.minDimension / 2f,
                style = Stroke(width = 4.dp.toPx())
            )

            // Progress arc
            val sweepAngle = xpProgress * 360f
            drawArc(
                color = Primary,
                startAngle = -90f,
                sweepAngle = sweepAngle,
                useCenter = false,
                style = Stroke(width = 6.dp.toPx()),
                topLeft = Offset(6.dp.toPx(), 6.dp.toPx()),
                size = Size(size.width - 12.dp.toPx(), size.height - 12.dp.toPx())
            )
        }

        // Level number
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "$level",
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = OnBackground
            )
            Text(
                text = "LEVEL",
                fontSize = 8.sp,
                color = OnSurfaceVariant,
                letterSpacing = 2.sp
            )
        }
    }
}

@Composable
private fun StreakInfo(label: String, value: String, icon: String) {
    Card(
        modifier = Modifier.width(140.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Surface)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = icon, fontSize = 24.sp)
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = value,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = OnBackground
            )
            Text(
                text = label,
                fontSize = 11.sp,
                color = OnSurfaceVariant
            )
        }
    }
}

@Composable
private fun AchievementBadge(
    achievement: Achievement
) {
    val unlockedSet = AppStorage.storage.getUnlockedAchievements()
    val isUnlocked = achievement.id in unlockedSet

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1f),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isUnlocked) Surface else SurfaceVariant.copy(alpha = 0.5f)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = achievement.icon,
                fontSize = 28.sp,
                modifier = Modifier.then(
                    if (!isUnlocked) Modifier else Modifier
                )
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = achievement.name,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = if (isUnlocked) OnSurface else OnSurfaceVariant.copy(alpha = 0.6f),
                textAlign = TextAlign.Center,
                maxLines = 2
            )
            if (isUnlocked) {
                Text(
                    text = achievement.description,
                    fontSize = 8.sp,
                    color = OnSurfaceVariant,
                    textAlign = TextAlign.Center,
                    maxLines = 2
                )
            } else {
                Text(
                    text = "Locked",
                    fontSize = 8.sp,
                    color = OnSurfaceVariant.copy(alpha = 0.5f),
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Composable
private fun NameChangeDialog(
    currentName: String,
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit
) {
    var name by remember { mutableStateOf(currentName) }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = Surface,
        title = {
            Text(
                text = "Change Name",
                color = OnBackground,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Your Name") },
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = OnBackground,
                    unfocusedTextColor = OnBackground,
                    focusedBorderColor = Primary,
                    unfocusedBorderColor = OnSurfaceVariant,
                    focusedLabelColor = Primary,
                    unfocusedLabelColor = OnSurfaceVariant
                ),
                modifier = Modifier.fillMaxWidth()
            )
        },
        confirmButton = {
            TextButton(
                onClick = { if (name.isNotBlank()) onConfirm(name.trim()) }
            ) {
                Text("Save", color = Primary)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = OnSurfaceVariant)
            }
        }
    )
}

@Composable
private fun ResetProgressDialog(
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = Surface,
        icon = {
            Icon(
                imageVector = Icons.Default.Warning,
                contentDescription = null,
                tint = Color(0xFFFF5252)
            )
        },
        title = {
            Text(
                text = "Reset All Progress?",
                color = OnBackground,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
        },
        text = {
            Text(
                text = "This will permanently delete all your skills, achievements, game records, and XP. This action cannot be undone.",
                color = OnSurfaceVariant,
                textAlign = TextAlign.Center
            )
        },
        confirmButton = {
            TextButton(
                onClick = onConfirm,
                colors = ButtonDefaults.textButtonColors(
                    contentColor = Color(0xFFFF5252)
                )
            ) {
                Text("Reset Everything", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = OnSurfaceVariant)
            }
        }
    )
}
