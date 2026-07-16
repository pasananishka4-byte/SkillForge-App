package com.skillforge.app.ui.screens.analytics

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.skillforge.app.data.local.SkillForgeDatabase
import com.skillforge.app.domain.model.Skill
import com.skillforge.app.domain.repository.ProgressRepository
import com.skillforge.app.domain.repository.SkillRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AnalyticsUiState(
    val totalCompleted: Int = 0,
    val totalCorrect: Int = 0,
    val daysActive: Int = 0,
    val skills: List<Skill> = emptyList(),
    val skillAccuracy: Map<Long, Pair<Int, Int>> = emptyMap(),
    val isLoading: Boolean = true
)

@HiltViewModel
class AnalyticsViewModel @Inject constructor(
    private val progressRepository: ProgressRepository,
    private val skillRepository: SkillRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AnalyticsUiState())
    val uiState: StateFlow<AnalyticsUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            SkillForgeDatabase.awaitSeeding()
            loadData()
        }
    }

    private fun loadData() {
        viewModelScope.launch {
            val completed = progressRepository.getTotalCompleted().first()
            val correct = progressRepository.getTotalCorrect().first()
            val days = progressRepository.getDaysActive().first()
            val skills = skillRepository.getAllSkills().first()

            val skillAccuracy = mutableMapOf<Long, Pair<Int, Int>>()
            for (skill in skills) {
                val c = progressRepository.getCorrectBySkill(skill.id).first()
                val t = progressRepository.getTotalBySkill(skill.id).first()
                skillAccuracy[skill.id] = Pair(c, t)
            }

            _uiState.value = AnalyticsUiState(
                totalCompleted = completed,
                totalCorrect = correct,
                daysActive = days,
                skills = skills,
                skillAccuracy = skillAccuracy,
                isLoading = false
            )
        }
    }
}
