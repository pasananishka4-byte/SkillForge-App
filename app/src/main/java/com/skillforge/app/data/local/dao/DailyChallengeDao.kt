package com.skillforge.app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.skillforge.app.data.local.entity.DailyChallengeEntity

@Dao
interface DailyChallengeDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDailyChallenge(dailyChallenge: DailyChallengeEntity)

    @Query("SELECT * FROM daily_challenges WHERE date = :date")
    suspend fun getDailyChallenge(date: String): DailyChallengeEntity?

    @Query("UPDATE daily_challenges SET isCompleted = 1 WHERE date = :date")
    suspend fun markCompleted(date: String)

    @Query("SELECT COUNT(*) FROM daily_challenges WHERE isCompleted = 1")
    suspend fun getCompletedDaysCount(): Int
}
