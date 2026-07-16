package com.skillforge.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "daily_challenges")
data class DailyChallengeEntity(
    @PrimaryKey val date: String,
    val challengeIds: String,
    val isCompleted: Boolean = false
)
