package com.skillforge.app.ui.screens.games

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

val Context.gameDataStore: DataStore<Preferences> by preferencesDataStore(name = "game_stats")

data class GameRecord(
    val gameName: String,
    val bestScore: Int = 0,
    val gamesPlayed: Int = 0,
    val totalScore: Long = 0,
    val bestTime: Int = Int.MAX_VALUE,
    val difficulty: String = "Normal"
)

data class AllGameStats(
    val records: Map<String, GameRecord> = emptyMap(),
    val totalGamesPlayed: Int = 0,
    val totalXPFromGames: Long = 0
)

@Singleton
class GameStatsManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val gson = Gson()

    companion object {
        private val KEY_RECORDS = stringPreferencesKey("game_records")
        private val KEY_TOTAL_GAMES = intPreferencesKey("total_games_played")
        private val KEY_TOTAL_XP = longPreferencesKey("total_xp_from_games")
        private val KEY_SELECTED_DIFFICULTY = stringPreferencesKey("selected_difficulty")
    }

    val allStats: Flow<AllGameStats> = context.gameDataStore.data.map { prefs ->
        val json = prefs[KEY_RECORDS] ?: "{}"
        val type = object : TypeToken<Map<String, GameRecord>>() {}.type
        val records: Map<String, GameRecord> = try {
            gson.fromJson(json, type) ?: emptyMap()
        } catch (e: Exception) {
            emptyMap()
        }
        AllGameStats(
            records = records,
            totalGamesPlayed = prefs[KEY_TOTAL_GAMES] ?: 0,
            totalXPFromGames = prefs[KEY_TOTAL_XP] ?: 0
        )
    }

    val selectedDifficulty: Flow<String> = context.gameDataStore.data.map { prefs ->
        prefs[KEY_SELECTED_DIFFICULTY] ?: "Normal"
    }

    fun getRecordFlow(gameName: String): Flow<GameRecord> = context.gameDataStore.data.map { prefs ->
        val json = prefs[KEY_RECORDS] ?: "{}"
        val type = object : TypeToken<Map<String, GameRecord>>() {}.type
        val records: Map<String, GameRecord> = try {
            gson.fromJson(json, type) ?: emptyMap()
        } catch (e: Exception) {
            emptyMap()
        }
        records[gameName] ?: GameRecord(gameName = gameName)
    }

    suspend fun recordGameResult(
        gameName: String,
        score: Int,
        timeSeconds: Int = 0,
        xpEarned: Int = 0
    ) {
        context.gameDataStore.edit { prefs ->
            val json = prefs[KEY_RECORDS] ?: "{}"
            val type = object : TypeToken<Map<String, GameRecord>>() {}.type
            val records: MutableMap<String, GameRecord> = try {
                (gson.fromJson<Map<String, GameRecord>>(json, type) ?: emptyMap()).toMutableMap()
            } catch (e: Exception) {
                mutableMapOf()
            }

            val existing = records[gameName] ?: GameRecord(gameName = gameName)
            records[gameName] = existing.copy(
                bestScore = maxOf(existing.bestScore, score),
                gamesPlayed = existing.gamesPlayed + 1,
                totalScore = existing.totalScore + score,
                bestTime = if (timeSeconds > 0) minOf(existing.bestTime, timeSeconds) else existing.bestTime
            )

            prefs[KEY_RECORDS] = gson.toJson(records)
            prefs[KEY_TOTAL_GAMES] = (prefs[KEY_TOTAL_GAMES] ?: 0) + 1
            prefs[KEY_TOTAL_XP] = (prefs[KEY_TOTAL_XP] ?: 0) + xpEarned
        }
    }

    suspend fun setDifficulty(difficulty: String) {
        context.gameDataStore.edit { prefs ->
            prefs[KEY_SELECTED_DIFFICULTY] = difficulty
        }
    }

    suspend fun clearStats() {
        context.gameDataStore.edit { prefs ->
            prefs.remove(KEY_RECORDS)
            prefs.remove(KEY_TOTAL_GAMES)
            prefs.remove(KEY_TOTAL_XP)
        }
    }
}
