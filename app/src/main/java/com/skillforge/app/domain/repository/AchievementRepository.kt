package com.skillforge.app.domain.repository

import com.skillforge.app.data.local.dao.AchievementDao
import com.skillforge.app.data.local.entity.AchievementEntity
import com.skillforge.app.data.local.entity.UserAchievementEntity
import com.skillforge.app.domain.model.Achievement
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AchievementRepository @Inject constructor(
    private val achievementDao: AchievementDao
) {
    fun getAllAchievements(): Flow<List<Achievement>> {
        return achievementDao.getAllAchievements().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    fun getUnlockedCount(): Flow<Int> = achievementDao.getUnlockedCount()

    suspend fun unlockAchievement(achievementId: Long) {
        val existing = achievementDao.getUserAchievement(achievementId)
        if (existing == null) {
            achievementDao.unlockAchievement(
                UserAchievementEntity(achievementId = achievementId)
            )
        }
    }

    suspend fun isAchievementUnlocked(achievementId: Long): Boolean {
        return achievementDao.getUserAchievement(achievementId) != null
    }

    private fun AchievementEntity.toDomain() = Achievement(
        id = id,
        name = name,
        description = description,
        icon = icon,
        category = category,
        requirementType = requirementType,
        requirementValue = requirementValue,
        xpReward = xpReward
    )
}
