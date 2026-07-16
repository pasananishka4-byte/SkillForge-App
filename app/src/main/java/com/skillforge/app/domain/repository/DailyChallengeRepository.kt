package com.skillforge.app.domain.repository

import com.skillforge.app.data.local.dao.DailyChallengeDao
import com.skillforge.app.data.local.entity.DailyChallengeEntity
import com.skillforge.app.domain.model.Challenge
import com.skillforge.app.domain.repository.ChallengeRepository
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DailyChallengeRepository @Inject constructor(
    private val dailyChallengeDao: DailyChallengeDao,
    private val challengeRepository: ChallengeRepository
) {
    private val gson = Gson()

    suspend fun getTodayChallenges(): List<Challenge> {
        val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        val dailyChallenge = dailyChallengeDao.getDailyChallenge(today)

        return if (dailyChallenge != null) {
            val ids: List<Long> = gson.fromJson(dailyChallenge.challengeIds, object : TypeToken<List<Long>>() {}.type)
            ids.mapNotNull { challengeRepository.getChallengeById(it) }
        } else {
            val challenges = challengeRepository.getRandomChallenges(5)
            val ids = challenges.map { it.id }
            dailyChallengeDao.insertDailyChallenge(
                DailyChallengeEntity(
                    date = today,
                    challengeIds = gson.toJson(ids)
                )
            )
            challenges
        }
    }

    suspend fun isDailyCompleted(): Boolean {
        val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        val dailyChallenge = dailyChallengeDao.getDailyChallenge(today)
        return dailyChallenge?.isCompleted == true
    }

    suspend fun markDailyCompleted() {
        val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        dailyChallengeDao.markCompleted(today)
    }

    suspend fun getCompletedDaysCount(): Int {
        return dailyChallengeDao.getCompletedDaysCount()
    }
}
