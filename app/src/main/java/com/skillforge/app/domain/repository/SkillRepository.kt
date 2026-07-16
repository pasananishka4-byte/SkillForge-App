package com.skillforge.app.domain.repository

import com.skillforge.app.data.local.dao.SkillDao
import com.skillforge.app.data.local.entity.SkillEntity
import com.skillforge.app.domain.model.Skill
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SkillRepository @Inject constructor(
    private val skillDao: SkillDao
) {
    fun getAllSkills(): Flow<List<Skill>> {
        return skillDao.getAllSkills().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    fun getSkillsByCategory(category: String): Flow<List<Skill>> {
        return skillDao.getSkillsByCategory(category).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    fun getSkillById(skillId: Long): Flow<Skill?> {
        return skillDao.getSkillById(skillId).map { it?.toDomain() }
    }

    suspend fun updateSkillProgress(skillId: Long, xp: Long, level: Int) {
        skillDao.updateSkillProgress(skillId, xp, level)
    }

    fun getTotalSkillXP(): Flow<Long?> {
        return skillDao.getTotalSkillXP()
    }

    private fun SkillEntity.toDomain() = Skill(
        id = id,
        name = name,
        category = category,
        description = description,
        icon = icon,
        currentXP = currentXP,
        level = level,
        maxLevel = maxLevel,
        sortOrder = sortOrder,
        isUnlocked = isUnlocked,
        prerequisiteSkillId = prerequisiteSkillId
    )
}
