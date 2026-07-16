package com.skillforge.app.ui.screens.challenge

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.skillforge.app.data.local.SkillForgeDatabase
import com.skillforge.app.domain.model.Challenge
import com.skillforge.app.domain.model.Skill
import com.skillforge.app.domain.model.User
import com.skillforge.app.domain.repository.ChallengeRepository
import com.skillforge.app.domain.repository.ProgressRepository
import com.skillforge.app.domain.repository.SkillRepository
import com.skillforge.app.domain.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

data class ChallengeUiState(
    val challenges: List<Challenge> = emptyList(),
    val currentChallengeIndex: Int = 0,
    val selectedAnswer: Int? = null,
    val isAnswered: Boolean = false,
    val score: Int = 0,
    val totalXP: Int = 0,
    val timeSpent: Int = 0,
    val isComplete: Boolean = false,
    val skill: Skill? = null,
    val isLoading: Boolean = true,
    val correctCount: Int = 0,
    val totalAnswered: Int = 0
) {
    val currentChallenge: Challenge?
        get() = challenges.getOrNull(currentChallengeIndex)

    val progress: Float
        get() = if (challenges.isNotEmpty()) currentChallengeIndex.toFloat() / challenges.size else 0f

    val accuracy: Float
        get() = if (totalAnswered > 0) correctCount.toFloat() / totalAnswered else 0f
}

@HiltViewModel
class ChallengeViewModel @Inject constructor(
    private val challengeRepository: ChallengeRepository,
    private val skillRepository: SkillRepository,
    private val progressRepository: ProgressRepository,
    private val userRepository: UserRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ChallengeUiState())
    val uiState: StateFlow<ChallengeUiState> = _uiState.asStateFlow()

    private var startTime: Long = 0

    fun loadChallenges(skillId: Long, difficulty: String) {
        viewModelScope.launch {
            SkillForgeDatabase.awaitSeeding()
            val challenges = if (skillId == 0L) {
                challengeRepository.getRandomChallenges(10)
            } else {
                challengeRepository.getRandomChallengesBySkill(skillId, 10)
            }

            val skill = if (skillId != 0L) {
                skillRepository.getSkillById(skillId).let {
                    // We need a snapshot, not a flow
                    null // Will be set from skillId
                }
                null
            } else null

            _uiState.value = _uiState.value.copy(
                challenges = challenges,
                isLoading = false
            )

            startTimer()
        }
    }

    fun loadSkillInfo(skillId: Long) {
        viewModelScope.launch {
            val allSkills = mutableListOf<Skill>()
            skillRepository.getAllSkills().collect { skills ->
                allSkills.clear()
                allSkills.addAll(skills)
            }
        }
    }

    fun selectAnswer(index: Int) {
        if (_uiState.value.isAnswered) return
        _uiState.value = _uiState.value.copy(selectedAnswer = index)
    }

    fun confirmAnswer() {
        val state = _uiState.value
        if (state.selectedAnswer == null || state.isAnswered) return

        val challenge = state.currentChallenge ?: return
        val isCorrect = state.selectedAnswer == challenge.correctAnswerIndex
        val timeSpent = ((System.currentTimeMillis() - startTime) / 1000).toInt()

        val newCorrectCount = if (isCorrect) state.correctCount + 1 else state.correctCount
        val xpEarned = if (isCorrect) challenge.xpReward else 0

        _uiState.value = state.copy(
            isAnswered = true,
            correctCount = newCorrectCount,
            totalAnswered = state.totalAnswered + 1,
            totalXP = state.totalXP + xpEarned,
            timeSpent = timeSpent
        )

        // Save progress
        viewModelScope.launch {
            val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
            progressRepository.insertProgress(
                com.skillforge.app.domain.model.UserProgress(
                    challengeId = challenge.id,
                    skillId = challenge.skillId,
                    isCorrect = isCorrect,
                    timeSpentSeconds = timeSpent,
                    date = today
                )
            )

            // Update user XP
            val user = userRepository.getUserOnce()
            userRepository.updateXP(user.totalXP + xpEarned)

            // Update skill XP
            val skill = skillRepository.getSkillById(challenge.skillId).first()
            skill?.let {
                val newXp = it.currentXP + xpEarned
                val newLevel = com.skillforge.app.domain.model.calculateLevelFromXP(newXp)
                skillRepository.updateSkillProgress(it.id, newXp, newLevel)
            }
        }
    }

    fun nextChallenge() {
        val state = _uiState.value
        if (state.currentChallengeIndex + 1 >= state.challenges.size) {
            _uiState.value = state.copy(isComplete = true)
            return
        }

        _uiState.value = state.copy(
            currentChallengeIndex = state.currentChallengeIndex + 1,
            selectedAnswer = null,
            isAnswered = false
        )
        startTimer()
    }

    private fun startTimer() {
        startTime = System.currentTimeMillis()
    }

    fun getDifficultyColor(): Long {
        return when (_uiState.value.currentChallenge?.difficulty) {
            "Easy" -> 0xFF4CAF50
            "Medium" -> 0xFFFFB300
            "Hard" -> 0xFFFF5252
            "Expert" -> 0xFFE040FB
            else -> 0xFFFFB300
        }
    }
}
