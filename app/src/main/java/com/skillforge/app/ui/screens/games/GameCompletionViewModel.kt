package com.skillforge.app.ui.screens.games

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.skillforge.app.domain.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GameCompletionViewModel @Inject constructor(
    private val userRepository: UserRepository,
    val gameStatsManager: GameStatsManager
) : ViewModel() {

    fun onGameComplete(gameName: String, xpEarned: Int, score: Int, timeSeconds: Int = 0) {
        viewModelScope.launch {
            if (xpEarned > 0) {
                val current = userRepository.getUserOnce()
                userRepository.updateXP(current.totalXP + xpEarned)
            }
            gameStatsManager.recordGameResult(
                gameName = gameName,
                score = score,
                timeSeconds = timeSeconds,
                xpEarned = xpEarned
            )
        }
    }
}
