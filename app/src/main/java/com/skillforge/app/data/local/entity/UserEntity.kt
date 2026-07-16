package com.skillforge.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user")
data class UserEntity(
    @PrimaryKey val id: Int = 1,
    val name: String = "Player",
    val totalXP: Long = 0,
    val level: Int = 1,
    val currentStreak: Int = 0,
    val longestStreak: Int = 0,
    val lastActiveDate: String = "",
    val createdAt: Long = System.currentTimeMillis()
)
