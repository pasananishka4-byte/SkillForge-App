package com.skillforge.app.ui.screens.daily

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.skillforge.app.data.local.SkillForgeDatabase
import com.skillforge.app.domain.model.Challenge
import com.skillforge.app.domain.model.Skill
import com.skillforge.app.domain.repository.ChallengeRepository
import com.skillforge.app.domain.repository.DailyChallengeRepository
import com.skillforge.app.domain.repository.ProgressRepository
import com.skillforge.app.domain.repository.SkillRepository
import com.skillforge.app.domain.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

data class DailyChallengeUiState(
    val challenges: List<Challenge> = emptyList(),
    val currentChallengeIndex: Int = 0,
    val selectedAnswer: Int? = null,
    val isAnswered: Boolean = false,
    val totalXP: Int = 0,
    val correctCount: Int = 0,
    val isComplete: Boolean = false,
    val isCompleted: Boolean = false,
    val isLoading: Boolean = true,
    val streakDays: Int = 0
) {
    val currentChallenge: Challenge?
        get() = challenges.getOrNull(currentChallengeIndex)

    val accuracy: Float
        get() = if (currentChallengeIndex > 0) correctCount.toFloat() / currentChallengeIndex else 0f
}

@HiltViewModel
class DailyChallengeViewModel @Inject constructor(
    private val dailyChallengeRepository: DailyChallengeRepository,
    private val challengeRepository: ChallengeRepository,
    private val skillRepository: SkillRepository,
    private val progressRepository: ProgressRepository,
    private val userRepository: UserRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(DailyChallengeUiState())
    val uiState: StateFlow<DailyChallengeUiState> = _uiState.asStateFlow()

    private var startTime: Long = 0

    init {
        loadDailyChallenges()
    }

    private fun loadDailyChallenges() {
        viewModelScope.launch {
            SkillForgeDatabase.awaitSeeding()
            val completed = dailyChallengeRepository.isDailyCompleted()
            val streakDays = dailyChallengeRepository.getCompletedDaysCount()

            if (completed) {
                _uiState.value = _uiState.value.copy(
                    isCompleted = true,
                    isLoading = false,
                    streakDays = streakDays
                )
                return@launch
            }

            val challenges = dailyChallengeRepository.getTodayChallenges()
            _uiState.value = _uiState.value.copy(
                challenges = challenges,
                isLoading = false,
                streakDays = streakDays
            )
            startTimer()
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
        val xpEarned = if (isCorrect) challenge.xpReward * 2 else 0 // Double XP for daily!

        _uiState.value = state.copy(
            isAnswered = true,
            correctCount = newCorrectCount,
            totalXP = state.totalXP + xpEarned
        )

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

            val user = userRepository.getUserOnce()
            userRepository.updateXP(user.totalXP + xpEarned)
        }
    }

    fun nextChallenge() {
        val state = _uiState.value
        if (state.currentChallengeIndex + 1 >= state.challenges.size) {
            viewModelScope.launch {
                dailyChallengeRepository.markDailyCompleted()
                val streakDays = dailyChallengeRepository.getCompletedDaysCount()
                _uiState.value = state.copy(
                    isComplete = true,
                    streakDays = streakDays
                )
            }
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
}
