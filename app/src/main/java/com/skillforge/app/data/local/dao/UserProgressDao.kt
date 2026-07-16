package com.skillforge.app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.skillforge.app.data.local.entity.UserProgressEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface UserProgressDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProgress(progress: UserProgressEntity)

    @Query("SELECT * FROM user_progress ORDER BY completedAt DESC")
    fun getAllProgress(): Flow<List<UserProgressEntity>>

    @Query("SELECT * FROM user_progress WHERE skillId = :skillId ORDER BY completedAt DESC")
    fun getProgressBySkill(skillId: Long): Flow<List<UserProgressEntity>>

    @Query("SELECT * FROM user_progress WHERE date = :date")
    fun getProgressByDate(date: String): Flow<List<UserProgressEntity>>

    @Query("SELECT COUNT(*) FROM user_progress WHERE isCorrect = 1")
    fun getTotalCorrect(): Flow<Int>

    @Query("SELECT COUNT(*) FROM user_progress")
    fun getTotalCompleted(): Flow<Int>

    @Query("SELECT COUNT(*) FROM user_progress WHERE skillId = :skillId AND isCorrect = 1")
    fun getCorrectBySkill(skillId: Long): Flow<Int>

    @Query("SELECT COUNT(*) FROM user_progress WHERE skillId = :skillId")
    fun getTotalBySkill(skillId: Long): Flow<Int>

    @Query("SELECT AVG(timeSpentSeconds) FROM user_progress WHERE skillId = :skillId")
    fun getAvgTimeBySkill(skillId: Long): Flow<Double?>

    @Query("SELECT * FROM user_progress WHERE date = :date")
    suspend fun getProgressByDateOnce(date: String): List<UserProgressEntity>

    @Query("SELECT COUNT(DISTINCT date) FROM user_progress")
    fun getDaysActive(): Flow<Int>
}
