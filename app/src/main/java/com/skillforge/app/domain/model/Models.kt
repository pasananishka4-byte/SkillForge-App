package com.skillforge.app.domain.model

data class User(
    val id: Int = 1,
    val name: String = "Player",
    val totalXP: Long = 0,
    val level: Int = 1,
    val currentStreak: Int = 0,
    val longestStreak: Int = 0,
    val lastActiveDate: String = "",
    val createdAt: Long = System.currentTimeMillis()
)

data class Skill(
    val id: Long,
    val name: String,
    val category: String,
    val description: String,
    val icon: String,
    val currentXP: Long,
    val level: Int,
    val maxLevel: Int,
    val sortOrder: Int,
    val isUnlocked: Boolean,
    val prerequisiteSkillId: Long?
)

data class Challenge(
    val id: Long,
    val skillId: Long,
    val difficulty: String,
    val type: String,
    val question: String,
    val options: List<String>,
    val correctAnswerIndex: Int,
    val explanation: String,
    val xpReward: Int
)

data class UserProgress(
    val id: Long = 0,
    val challengeId: Long,
    val skillId: Long,
    val isCorrect: Boolean,
    val timeSpentSeconds: Int,
    val completedAt: Long = System.currentTimeMillis(),
    val date: String
)

data class Achievement(
    val id: Long,
    val name: String,
    val description: String,
    val icon: String,
    val category: String,
    val requirementType: String,
    val requirementValue: Int,
    val xpReward: Int
)

data class SkillCategory(
    val name: String,
    val displayName: String,
    val icon: String,
    val color: Long,
    val skills: List<Skill>
)

enum class Difficulty(val displayName: String, val xpMultiplier: Int) {
    EASY("Easy", 1),
    MEDIUM("Medium", 2),
    HARD("Hard", 3),
    EXPERT("Expert", 4)
}

fun calculateLevelFromXP(xp: Long): Int {
    var level = 1
    var xpNeeded = 100L
    var totalXP = 0L
    while (totalXP + xpNeeded <= xp) {
        totalXP += xpNeeded
        level++
        xpNeeded = (xpNeeded * 1.2).toLong()
    }
    return level
}

fun xpForNextLevel(currentLevel: Int): Long {
    var xpNeeded = 100L
    repeat(currentLevel - 1) {
        xpNeeded = (xpNeeded * 1.2).toLong()
    }
    return xpNeeded
}

fun xpForCurrentLevel(currentLevel: Int): Long {
    var totalXP = 0L
    var xpNeeded = 100L
    repeat(currentLevel - 1) {
        totalXP += xpNeeded
        xpNeeded = (xpNeeded * 1.2).toLong()
    }
    return totalXP
}
