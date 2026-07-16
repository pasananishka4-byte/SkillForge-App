package com.skillforge.app.ui.screens.games

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class GamesHubViewModel @Inject constructor(
    val gameStatsManager: GameStatsManager
) : ViewModel()
