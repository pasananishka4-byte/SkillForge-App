package com.skillforge.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_progress")
data class UserProgressEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val challengeId: Long,
    val skillId: Long,
    val isCorrect: Boolean,
    val timeSpentSeconds: Int,
    val completedAt: Long = System.currentTimeMillis(),
    val date: String
)
