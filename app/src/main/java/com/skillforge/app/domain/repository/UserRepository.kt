package com.skillforge.app.domain.repository

import com.skillforge.app.data.local.dao.UserDao
import com.skillforge.app.data.local.entity.UserEntity
import com.skillforge.app.domain.model.User
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserRepository @Inject constructor(
    private val userDao: UserDao
) {
    fun getUser(): Flow<User> {
        return userDao.getUser().map { entity ->
            entity?.toDomain() ?: User()
        }
    }

    suspend fun getUserOnce(): User {
        return userDao.getUserOnce()?.toDomain() ?: User()
    }

    suspend fun updateXP(newXP: Long) {
        val user = getUserOnce()
        val newLevel = com.skillforge.app.domain.model.calculateLevelFromXP(newXP)
        userDao.updateXP(newXP)
        userDao.updateLevel(newLevel)
    }

    suspend fun updateStreak(streak: Int, longestStreak: Int, date: String) {
        userDao.updateStreak(streak, longestStreak, date)
    }

    suspend fun ensureUserExists() {
        val existing = userDao.getUserOnce()
        if (existing == null) {
            userDao.insertUser(UserEntity())
        }
    }

    private fun UserEntity.toDomain() = User(
        id = id,
        name = name,
        totalXP = totalXP,
        level = level,
        currentStreak = currentStreak,
        longestStreak = longestStreak,
        lastActiveDate = lastActiveDate,
        createdAt = createdAt
    )
}
