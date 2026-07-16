package com.skillforge.app.domain.repository

import com.skillforge.app.data.local.dao.ChallengeDao
import com.skillforge.app.data.local.entity.ChallengeEntity
import com.skillforge.app.domain.model.Challenge
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ChallengeRepository @Inject constructor(
    private val challengeDao: ChallengeDao
) {
    private val gson = Gson()

    fun getChallengesBySkill(skillId: Long): Flow<List<Challenge>> {
        return challengeDao.getChallengesBySkill(skillId).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    suspend fun getChallengeById(challengeId: Long): Challenge? {
        return challengeDao.getChallengeById(challengeId)?.toDomain()
    }

    suspend fun getRandomChallenges(count: Int): List<Challenge> {
        return challengeDao.getRandomChallenges(count).map { it.toDomain() }
    }

    suspend fun getRandomChallengesBySkill(skillId: Long, count: Int): List<Challenge> {
        return challengeDao.getRandomChallengesBySkill(skillId, count).map { it.toDomain() }
    }

    suspend fun getChallengeCount(): Int {
        return challengeDao.getChallengeCount()
    }

    private fun ChallengeEntity.toDomain() = Challenge(
        id = id,
        skillId = skillId,
        difficulty = difficulty,
        type = type,
        question = question,
        options = gson.fromJson(options, object : TypeToken<List<String>>() {}.type),
        correctAnswerIndex = correctAnswerIndex,
        explanation = explanation,
        xpReward = xpReward
    )
}
