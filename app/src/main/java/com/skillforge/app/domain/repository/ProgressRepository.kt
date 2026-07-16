package com.skillforge.app.domain.repository

import com.skillforge.app.data.local.dao.UserProgressDao
import com.skillforge.app.data.local.entity.UserProgressEntity
import com.skillforge.app.domain.model.UserProgress
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ProgressRepository @Inject constructor(
    private val userProgressDao: UserProgressDao
) {
    fun getAllProgress(): Flow<List<UserProgress>> {
        return userProgressDao.getAllProgress().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    fun getProgressBySkill(skillId: Long): Flow<List<UserProgress>> {
        return userProgressDao.getProgressBySkill(skillId).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    fun getTotalCorrect(): Flow<Int> = userProgressDao.getTotalCorrect()
    fun getTotalCompleted(): Flow<Int> = userProgressDao.getTotalCompleted()
    fun getCorrectBySkill(skillId: Long): Flow<Int> = userProgressDao.getCorrectBySkill(skillId)
    fun getTotalBySkill(skillId: Long): Flow<Int> = userProgressDao.getTotalBySkill(skillId)
    fun getAvgTimeBySkill(skillId: Long): Flow<Double?> = userProgressDao.getAvgTimeBySkill(skillId)
    fun getDaysActive(): Flow<Int> = userProgressDao.getDaysActive()

    suspend fun insertProgress(progress: UserProgress) {
        userProgressDao.insertProgress(progress.toEntity())
    }

    suspend fun getTodayProgress(): List<UserProgress> {
        val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        return userProgressDao.getProgressByDateOnce(today).map { it.toDomain() }
    }

    private fun UserProgress.toEntity() = UserProgressEntity(
        id = id,
        challengeId = challengeId,
        skillId = skillId,
        isCorrect = isCorrect,
        timeSpentSeconds = timeSpentSeconds,
        completedAt = completedAt,
        date = date
    )

    private fun UserProgressEntity.toDomain() = UserProgress(
        id = id,
        challengeId = challengeId,
        skillId = skillId,
        isCorrect = isCorrect,
        timeSpentSeconds = timeSpentSeconds,
        completedAt = completedAt,
        date = date
    )
}
