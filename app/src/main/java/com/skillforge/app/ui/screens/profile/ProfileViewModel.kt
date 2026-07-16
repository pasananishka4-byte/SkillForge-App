package com.skillforge.app.ui.screens.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.skillforge.app.domain.model.User
import com.skillforge.app.domain.repository.AchievementRepository
import com.skillforge.app.domain.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ProfileUiState(
    val user: User = User(),
    val achievementsUnlocked: Int = 0,
    val totalAchievements: Int = 20,
    val isLoading: Boolean = true
)

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val achievementRepository: AchievementRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    init {
        loadData()
    }

    private fun loadData() {
        viewModelScope.launch {
            userRepository.getUser().collect { user ->
                achievementRepository.getUnlockedCount().collect { unlocked ->
                    _uiState.value = ProfileUiState(
                        user = user,
                        achievementsUnlocked = unlocked,
                        isLoading = false
                    )
                }
            }
        }
    }
}
