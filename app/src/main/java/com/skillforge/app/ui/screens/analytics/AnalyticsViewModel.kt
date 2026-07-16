package com.skillforge.app.ui.screens.analytics

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.skillforge.app.domain.model.Skill
import com.skillforge.app.domain.repository.ProgressRepository
import com.skillforge.app.domain.repository.SkillRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AnalyticsUiState(
    val totalCompleted: Int = 0,
    val totalCorrect: Int = 0,
    val daysActive: Int = 0,
    val skills: List<Skill> = emptyList(),
    val skillAccuracy: Map<Long, Pair<Int, Int>> = emptyMap(), // skillId -> (correct, total)
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
        loadData()
    }

    private fun loadData() {
        viewModelScope.launch {
            progressRepository.getTotalCompleted().collect { completed ->
                progressRepository.getTotalCorrect().collect { correct ->
                    progressRepository.getDaysActive().collect { days ->
                        skillRepository.getAllSkills().collect { skills ->
                            val skillAccuracy = mutableMapOf<Long, Pair<Int, Int>>()
                            skills.forEach { skill ->
                                progressRepository.getCorrectBySkill(skill.id).collect { c ->
                                    progressRepository.getTotalBySkill(skill.id).collect { t ->
                                        skillAccuracy[skill.id] = Pair(c, t)
                                    }
                                }
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
            }
        }
    }
}
