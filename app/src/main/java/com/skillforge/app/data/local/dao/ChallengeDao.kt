package com.skillforge.app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.skillforge.app.data.local.entity.ChallengeEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ChallengeDao {
    @Query("SELECT * FROM challenges WHERE skillId = :skillId")
    fun getChallengesBySkill(skillId: Long): Flow<List<ChallengeEntity>>

    @Query("SELECT * FROM challenges WHERE skillId = :skillId AND difficulty = :difficulty")
    fun getChallengesBySkillAndDifficulty(skillId: Long, difficulty: String): Flow<List<ChallengeEntity>>

    @Query("SELECT * FROM challenges WHERE id = :challengeId")
    suspend fun getChallengeById(challengeId: Long): ChallengeEntity?

    @Query("SELECT * FROM challenges ORDER BY RANDOM() LIMIT :count")
    suspend fun getRandomChallenges(count: Int): List<ChallengeEntity>

    @Query("SELECT * FROM challenges WHERE skillId = :skillId ORDER BY RANDOM() LIMIT :count")
    suspend fun getRandomChallengesBySkill(skillId: Long, count: Int): List<ChallengeEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertChallenges(challenges: List<ChallengeEntity>)

    @Query("SELECT COUNT(*) FROM challenges")
    suspend fun getChallengeCount(): Int
}
