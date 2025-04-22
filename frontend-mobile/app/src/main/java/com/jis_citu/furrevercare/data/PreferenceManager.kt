package com.jis_citu.furrevercare.data

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "user_preferences")

object PreferenceManager {
    private val UNIT_KEY = stringPreferencesKey("unit_preference")
    private val DARK_THEME_KEY = booleanPreferencesKey("dark_theme")

    // Unit Preference
    suspend fun setUnitPreference(context: Context, unit: String) {
        context.dataStore.edit { prefs ->
            prefs[UNIT_KEY] = unit
        }
    }

    fun getUnitPreference(context: Context): Flow<String> {
        return context.dataStore.data.map { prefs ->
            prefs[UNIT_KEY] ?: "Metric"
        }
    }

    // Theme Preference
    suspend fun setDarkTheme(context: Context, isDark: Boolean) {
        context.dataStore.edit { prefs ->
            prefs[DARK_THEME_KEY] = isDark
        }
    }

    fun isDarkTheme(context: Context): Flow<Boolean> {
        return context.dataStore.data.map { prefs ->
            prefs[DARK_THEME_KEY] ?: false
        }
    }
}
