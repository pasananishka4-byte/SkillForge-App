package com.skillforge.app.ui.screens.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.skillforge.app.domain.model.calculateLevelFromXP
import com.skillforge.app.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    viewModel: ProfileViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(vertical = 16.dp)
    ) {
        item {
            Text(
                text = "Profile",
                style = MaterialTheme.typography.headlineLarge,
                color = Primary
            )
        }

        // User Profile Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Surface),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(80.dp)
                            .clip(CircleShape)
                            .background(Primary.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Person,
                            contentDescription = "Profile",
                            tint = Primary,
                            modifier = Modifier.size(48.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = uiState.user.name,
                        style = MaterialTheme.typography.headlineSmall,
                        color = OnSurface,
                        fontWeight = FontWeight.Bold
                    )

                    Text(
                        text = "Level ${uiState.user.level}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Primary
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        ProfileStatItem(
                            value = "${uiState.user.totalXP}",
                            label = "Total XP",
                            icon = Icons.Filled.Bolt
                        )
                        ProfileStatItem(
                            value = "${uiState.user.currentStreak}",
                            label = "Streak",
                            icon = Icons.Filled.LocalFireDepartment
                        )
                        ProfileStatItem(
                            value = "${uiState.achievementsUnlocked}/${uiState.totalAchievements}",
                            label = "Badges",
                            icon = Icons.Filled.EmojiEvents
                        )
                    }
                }
            }
        }

        // Stats Section
        item {
            Text(
                text = "Statistics",
                style = MaterialTheme.typography.titleMedium,
                color = OnSurface
            )
        }

        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Surface),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    ProfileStatRow(
                        label = "Overall Level",
                        value = "Level ${uiState.user.level}",
                        icon = Icons.Filled.TrendingUp,
                        color = Primary
                    )
                    Divider(color = SurfaceVariant, modifier = Modifier.padding(vertical = 8.dp))
                    ProfileStatRow(
                        label = "Total XP Earned",
                        value = "${uiState.user.totalXP} XP",
                        icon = Icons.Filled.Bolt,
                        color = Secondary
                    )
                    Divider(color = SurfaceVariant, modifier = Modifier.padding(vertical = 8.dp))
                    ProfileStatRow(
                        label = "Current Streak",
                        value = "${uiState.user.currentStreak} days",
                        icon = Icons.Filled.LocalFireDepartment,
                        color = StreakFire
                    )
                    Divider(color = SurfaceVariant, modifier = Modifier.padding(vertical = 8.dp))
                    ProfileStatRow(
                        label = "Best Streak",
                        value = "${uiState.user.longestStreak} days",
                        icon = Icons.Filled.EmojiEvents,
                        color = WarningColor
                    )
                }
            }
        }

        // About
        item {
            Text(
                text = "About",
                style = MaterialTheme.typography.titleMedium,
                color = OnSurface
            )
        }

        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Surface),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    ProfileStatRow(
                        label = "App Version",
                        value = "1.0.0",
                        icon = Icons.Filled.Info,
                        color = OnSurfaceVariant
                    )
                    Divider(color = SurfaceVariant, modifier = Modifier.padding(vertical = 8.dp))
                    ProfileStatRow(
                        label = "Build",
                        value = "Beta",
                        icon = Icons.Filled.Build,
                        color = OnSurfaceVariant
                    )
                }
            }
        }

        item { Spacer(modifier = Modifier.height(16.dp)) }
    }
}

@Composable
fun ProfileStatItem(value: String, label: String, icon: ImageVector) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = Primary,
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = value,
            style = MaterialTheme.typography.titleMedium,
            color = OnSurface,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = OnSurfaceVariant
        )
    }
}

@Composable
fun ProfileStatRow(label: String, value: String, icon: ImageVector, color: Color) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = color,
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = OnSurface,
            modifier = Modifier.weight(1f)
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            color = OnSurfaceVariant,
            fontWeight = FontWeight.Medium
        )
    }
}
