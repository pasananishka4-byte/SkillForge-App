package com.skillforge.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "skills")
data class SkillEntity(
    @PrimaryKey val id: Long = 0,
    val name: String,
    val category: String,
    val description: String,
    val icon: String,
    val currentXP: Long = 0,
    val level: Int = 1,
    val maxLevel: Int = 50,
    val sortOrder: Int = 0,
    val isUnlocked: Boolean = true,
    val prerequisiteSkillId: Long? = null
)
