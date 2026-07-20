package com.skillforge.app.data

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

object LocalStorage {

    private const val PREFS_NAME = "skillforge_prefs"
    private lateinit var prefs: SharedPreferences
    private val gson = Gson()

    private const val KEY_USER = "user"
    private const val KEY_SKILL_PROGRESS = "skill_progress"
    private const val KEY_COMPLETED_CHALLENGES = "completed_challenges"
    private const val KEY_UNLOCKED_ACHIEVEMENTS = "unlocked_achievements"
    private const val KEY_DAILY_DATES = "daily_challenge_dates"
    private const val KEY_GAME_STATS = "game_stats"
    private const val KEY_ONBOARDING_COMPLETE = "onboarding_complete"

    fun init(context: Context) {
        prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    fun getUser(): User {
        val json = prefs.getString(KEY_USER, null)
        return if (json != null) {
            gson.fromJson(json, User::class.java)
        } else {
            User()
        }
    }

    fun saveUser(user: User) {
        prefs.edit().putString(KEY_USER, gson.toJson(user)).apply()
    }

    fun getSkillProgress(skillId: Long): Pair<Int, Long> {
        val json = prefs.getString(KEY_SKILL_PROGRESS, null)
        if (json != null) {
            val type = object : TypeToken<Map<Long, SkillProgress>>() {}.type
            val map: Map<Long, SkillProgress> = gson.fromJson(json, type)
            map[skillId]?.let { return Pair(it.level, it.currentXP) }
        }
        return Pair(1, 0L)
    }

    fun saveSkillProgress(skillId: Long, level: Int, currentXP: Long) {
        val json = prefs.getString(KEY_SKILL_PROGRESS, null)
        val map: MutableMap<Long, SkillProgress> = if (json != null) {
            val type = object : TypeToken<Map<Long, SkillProgress>>() {}.type
            (gson.fromJson(json, type) as Map<Long, SkillProgress>).toMutableMap()
        } else {
            mutableMapOf()
        }
        map[skillId] = SkillProgress(level = level, currentXP = currentXP)
        prefs.edit().putString(KEY_SKILL_PROGRESS, gson.toJson(map)).apply()
    }

    fun getCompletedChallenges(): Set<Long> {
        val json = prefs.getString(KEY_COMPLETED_CHALLENGES, null)
        return if (json != null) {
            val type = object : TypeToken<Set<Long>>() {}.type
            gson.fromJson(json, type)
        } else {
            emptySet()
        }
    }

    fun markChallengeCompleted(challengeId: Long) {
        val current = getCompletedChallenges().toMutableSet()
        current.add(challengeId)
        prefs.edit().putString(KEY_COMPLETED_CHALLENGES, gson.toJson(current)).apply()
    }

    fun getUnlockedAchievements(): Set<Long> {
        val json = prefs.getString(KEY_UNLOCKED_ACHIEVEMENTS, null)
        return if (json != null) {
            val type = object : TypeToken<Set<Long>>() {}.type
            gson.fromJson(json, type)
        } else {
            emptySet()
        }
    }

    fun unlockAchievement(achievementId: Long) {
        val current = getUnlockedAchievements().toMutableSet()
        current.add(achievementId)
        prefs.edit().putString(KEY_UNLOCKED_ACHIEVEMENTS, gson.toJson(current)).apply()
    }

    fun getDailyChallengeDates(): Set<String> {
        val json = prefs.getString(KEY_DAILY_DATES, null)
        return if (json != null) {
            val type = object : TypeToken<Set<String>>() {}.type
            gson.fromJson(json, type)
        } else {
            emptySet()
        }
    }

    fun markDailyCompleted(date: String) {
        val current = getDailyChallengeDates().toMutableSet()
        current.add(date)
        prefs.edit().putString(KEY_DAILY_DATES, gson.toJson(current)).apply()
    }

    fun getGameStats(): Map<String, GameRecord> {
        val json = prefs.getString(KEY_GAME_STATS, null)
        return if (json != null) {
            val type = object : TypeToken<Map<String, GameRecord>>() {}.type
            gson.fromJson(json, type)
        } else {
            emptyMap()
        }
    }

    fun saveGameResult(gameName: String, score: Int, xpEarned: Int) {
        val stats = getGameStats().toMutableMap()
        val existing = stats[gameName] ?: GameRecord()
        stats[gameName] = GameRecord(
            bestScore = maxOf(existing.bestScore, score),
            gamesPlayed = existing.gamesPlayed + 1,
            totalXP = existing.totalXP + xpEarned
        )
        prefs.edit().putString(KEY_GAME_STATS, gson.toJson(stats)).apply()
    }

    fun isOnboardingComplete(): Boolean {
        return prefs.getBoolean(KEY_ONBOARDING_COMPLETE, false)
    }

    fun setOnboardingComplete() {
        prefs.edit().putBoolean(KEY_ONBOARDING_COMPLETE, true).apply()
    }

    fun resetAll() {
        prefs.edit().clear().apply()
    }

    fun getCompletedChallengeIds(): Set<Long> = getCompletedChallenges()

    fun saveUserName(name: String) {
        val user = getUser()
        saveUser(user.copy(name = name))
    }

    fun setUser(user: User) {
        saveUser(user)
    }

    fun markDailyChallengeCompleted(date: String) {
        markDailyCompleted(date)
    }

    fun getSkills(): List<SkillWithProgress> {
        val skills = SeedData.skills
        val allProgress = getAllSkillProgress()
        return skills.map { skill ->
            val progress = allProgress[skill.id] ?: SkillProgress()
            SkillWithProgress(
                id = skill.id,
                name = skill.name,
                category = skill.category,
                description = skill.description,
                icon = skill.icon,
                sortOrder = skill.sortOrder,
                maxLevel = skill.maxLevel,
                protocol = skill.protocol,
                scientificBasis = skill.scientificBasis,
                progress = progress
            )
        }
    }

    fun getSkillXP(skillId: Long): Long {
        return getSkillProgress(skillId).second
    }

    fun setSkillXP(skillId: Long, xp: Long) {
        val p = getSkillProgress(skillId)
        val newLevel = calculateLevelFromXP(xp).coerceAtLeast(1)
        saveSkillProgress(skillId, newLevel, xp)
    }

    fun getGameRecords(): Map<String, GameRecord> = getGameStats()

    fun getGameHistory(): List<GameHistoryEntry> {
        val completed = getCompletedChallenges()
        return SeedData.challenges.filter { it.id in completed }.map {
            GameHistoryEntry(skillId = it.skillId, challengeId = it.id, difficulty = it.difficulty)
        }
    }

    fun getChallenges(): List<Challenge> = SeedData.challenges

    fun getAchievements(): List<Achievement> = SeedData.achievements

    private fun getAllSkillProgress(): Map<Long, SkillProgress> {
        val json = prefs.getString(KEY_SKILL_PROGRESS, null)
        return if (json != null) {
            val type = object : TypeToken<Map<Long, SkillProgress>>() {}.type
            gson.fromJson(json, type)
        } else {
            emptyMap()
        }
    }
}

data class SkillProgress(val level: Int = 1, val currentXP: Long = 0)

data class SkillWithProgress(
    val id: Long,
    val name: String,
    val category: String,
    val description: String,
    val icon: String,
    val sortOrder: Int,
    val maxLevel: Int = 50,
    val protocol: String = "",
    val scientificBasis: String = "",
    val progress: SkillProgress = SkillProgress()
)

data class User(
    val name: String = "Player",
    val totalXP: Long = 0,
    val level: Int = 1,
    val currentStreak: Int = 0,
    val longestStreak: Int = 0,
    val lastActiveDate: String = "",
    val createdAt: Long = System.currentTimeMillis()
)

data class GameRecord(
    val bestScore: Int = 0,
    val gamesPlayed: Int = 0,
    val totalXP: Long = 0
)

data class GameHistoryEntry(
    val skillId: Long,
    val challengeId: Long,
    val difficulty: String
)
