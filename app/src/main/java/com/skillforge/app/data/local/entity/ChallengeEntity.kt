package com.skillforge.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "challenges")
data class ChallengeEntity(
    @PrimaryKey val id: Long = 0,
    val skillId: Long,
    val difficulty: String,
    val type: String,
    val question: String,
    val options: String,
    val correctAnswerIndex: Int,
    val explanation: String,
    val xpReward: Int
)
