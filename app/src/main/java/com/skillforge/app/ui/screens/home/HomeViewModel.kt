package com.skillforge.app.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

data class HomeUiState(
    val user: User = User(),
    val todayCompleted: Int = 0,
    val todayCorrect: Int = 0,
    val totalCompleted: Int = 0,
    val totalCorrect: Int = 0,
    val skills: List<Skill> = emptyList(),
    val isLoading: Boolean = true
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val skillRepository: SkillRepository,
    private val progressRepository: ProgressRepository,
    private val challengeRepository: ChallengeRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            userRepository.ensureUserExists()
            loadData()
        }
    }

    private fun loadData() {
        viewModelScope.launch {
            combine(
                userRepository.getUser(),
                skillRepository.getAllSkills(),
                progressRepository.getTotalCompleted(),
                progressRepository.getTotalCorrect()
            ) { user, skills, totalCompleted, totalCorrect ->
                val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
                val todayProgress = progressRepository.getTodayProgress()
                HomeUiState(
                    user = user,
                    todayCompleted = todayProgress.size,
                    todayCorrect = todayProgress.count { it.isCorrect },
                    totalCompleted = totalCompleted,
                    totalCorrect = totalCorrect,
                    skills = skills,
                    isLoading = false
                )
            }.collect { state ->
                _uiState.value = state
            }
        }
    }

    fun getStreakMessage(): String {
        val streak = _uiState.value.user.currentStreak
        return when {
            streak == 0 -> "Start your streak today!"
            streak == 1 -> "1 day streak! Keep going!"
            streak < 7 -> "$streak day streak! Building momentum!"
            streak < 30 -> "$streak day streak! You're on fire!"
            else -> "$streak day streak! Legendary dedication!"
        }
    }

    fun getLevelProgress(): Float {
        val user = _uiState.value.user
        val currentLevelXP = com.skillforge.app.domain.model.xpForCurrentLevel(user.level)
        val nextLevelXP = com.skillforge.app.domain.model.xpForNextLevel(user.level)
        val progressXP = user.totalXP - currentLevelXP
        return if (nextLevelXP > 0) (progressXP.toFloat() / nextLevelXP).coerceIn(0f, 1f) else 0f
    }
}
