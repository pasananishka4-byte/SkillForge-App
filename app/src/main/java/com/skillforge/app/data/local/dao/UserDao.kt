package com.skillforge.app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.skillforge.app.data.local.entity.UserEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
    @Query("SELECT * FROM user WHERE id = 1")
    fun getUser(): Flow<UserEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: UserEntity)

    @Query("UPDATE user SET totalXP = :xp WHERE id = 1")
    suspend fun updateXP(xp: Long)

    @Query("UPDATE user SET level = :level WHERE id = 1")
    suspend fun updateLevel(level: Int)

    @Query("UPDATE user SET currentStreak = :streak, longestStreak = :longestStreak, lastActiveDate = :date WHERE id = 1")
    suspend fun updateStreak(streak: Int, longestStreak: Int, date: String)

    @Query("SELECT * FROM user WHERE id = 1")
    suspend fun getUserOnce(): UserEntity?
}
