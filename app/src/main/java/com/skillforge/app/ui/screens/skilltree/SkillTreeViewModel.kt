package com.skillforge.app.ui.screens.skilltree

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.skillforge.app.data.local.SkillForgeDatabase
import com.skillforge.app.domain.model.Skill
import com.skillforge.app.domain.repository.SkillRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SkillTreeUiState(
    val skills: List<Skill> = emptyList(),
    val selectedCategory: String = "All",
    val isLoading: Boolean = true
)

@HiltViewModel
class SkillTreeViewModel @Inject constructor(
    private val skillRepository: SkillRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(SkillTreeUiState())
    val uiState: StateFlow<SkillTreeUiState> = _uiState.asStateFlow()

    val categories = listOf("All", "Critical Thinking", "General Knowledge", "Meta-Learning", "Social/Emotional")

    init {
        viewModelScope.launch {
            SkillForgeDatabase.awaitSeeding()
            skillRepository.getAllSkills().collect { skills ->
                _uiState.value = _uiState.value.copy(skills = skills, isLoading = false)
            }
        }
    }

    fun selectCategory(category: String) {
        _uiState.value = _uiState.value.copy(selectedCategory = category)
    }

    fun getFilteredSkills(): List<Skill> {
        val state = _uiState.value
        return if (state.selectedCategory == "All") state.skills
        else state.skills.filter { it.category == state.selectedCategory }
    }

    fun getCategoryColor(category: String): Long {
        return when (category) {
            "Critical Thinking" -> 0xFF7C4DFF
            "General Knowledge" -> 0xFF448AFF
            "Meta-Learning" -> 0xFFFF6E40
            "Social/Emotional" -> 0xFFFF4081
            else -> 0xFFFFB300
        }
    }
}
