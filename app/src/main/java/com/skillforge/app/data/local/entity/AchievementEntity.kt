package com.skillforge.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "achievements")
data class AchievementEntity(
    @PrimaryKey val id: Long = 0,
    val name: String,
    val description: String,
    val icon: String,
    val category: String,
    val requirementType: String,
    val requirementValue: Int,
    val xpReward: Int = 0
)
