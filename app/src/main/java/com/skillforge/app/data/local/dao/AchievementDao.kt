package com.skillforge.app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.skillforge.app.data.local.entity.AchievementEntity
import com.skillforge.app.data.local.entity.UserAchievementEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface AchievementDao {
    @Query("SELECT * FROM achievements")
    fun getAllAchievements(): Flow<List<AchievementEntity>>

    @Query("SELECT * FROM user_achievements")
    fun getUserAchievements(): Flow<List<UserAchievementEntity>>

    @Query("SELECT * FROM user_achievements WHERE achievementId = :achievementId")
    suspend fun getUserAchievement(achievementId: Long): UserAchievementEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAchievements(achievements: List<AchievementEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun unlockAchievement(userAchievement: UserAchievementEntity)

    @Query("SELECT COUNT(*) FROM user_achievements")
    fun getUnlockedCount(): Flow<Int>
}
