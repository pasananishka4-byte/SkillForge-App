package com.skillforge.app.ui.screens.onboarding

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

@HiltViewModel
class OnboardingViewModel @Inject constructor(
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val _isOnboardingComplete = MutableStateFlow(false)
    val isOnboardingComplete: StateFlow<Boolean> = _isOnboardingComplete.asStateFlow()

    companion object {
        val ONBOARDING_COMPLETE = booleanPreferencesKey("onboarding_complete")
    }

    init {
        viewModelScope.launch {
            val prefs = context.dataStore.data.first()
            _isOnboardingComplete.value = prefs[ONBOARDING_COMPLETE] ?: false
        }
    }

    fun completeOnboarding() {
        viewModelScope.launch {
            context.dataStore.edit { prefs ->
                prefs[ONBOARDING_COMPLETE] = true
            }
            _isOnboardingComplete.value = true
        }
    }
}
