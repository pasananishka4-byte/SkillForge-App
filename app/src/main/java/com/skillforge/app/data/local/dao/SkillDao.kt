package com.skillforge.app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.skillforge.app.data.local.entity.SkillEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SkillDao {
    @Query("SELECT * FROM skills ORDER BY category, sortOrder")
    fun getAllSkills(): Flow<List<SkillEntity>>

    @Query("SELECT * FROM skills WHERE category = :category ORDER BY sortOrder")
    fun getSkillsByCategory(category: String): Flow<List<SkillEntity>>

    @Query("SELECT * FROM skills WHERE id = :skillId")
    fun getSkillById(skillId: Long): Flow<SkillEntity?>

    @Query("SELECT * FROM skills WHERE id = :skillId")
    suspend fun getSkillByIdOnce(skillId: Long): SkillEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSkills(skills: List<SkillEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSkill(skill: SkillEntity)

    @Query("UPDATE skills SET currentXP = :xp, level = :level WHERE id = :skillId")
    suspend fun updateSkillProgress(skillId: Long, xp: Long, level: Int)

    @Query("UPDATE skills SET isUnlocked = 1 WHERE id = :skillId")
    suspend fun unlockSkill(skillId: Long)

    @Query("SELECT SUM(currentXP) FROM skills")
    fun getTotalSkillXP(): Flow<Long?>
}
