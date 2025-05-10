// In: com/jis_citu/furrevercare/data/PreferenceManager.kt
package com.jis_citu.furrevercare.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

private val Context.userPreferencesDataStoreInstance: DataStore<Preferences> by preferencesDataStore(
    name = "user_settings_preferences" // Use a distinct name if you have another DataStore
)

@Singleton
class PreferenceManager @Inject constructor(@ApplicationContext private val context: Context) {

    private val dataStore: DataStore<Preferences>
        get() = context.userPreferencesDataStoreInstance

    companion object { // Keys can remain in companion for easy access if needed elsewhere, though VM encapsulates usage
        val UNIT_KEY = stringPreferencesKey("unit_preference")
        val DARK_THEME_KEY = booleanPreferencesKey("dark_theme_preference") // Made key slightly more specific
        // Auth keys if this manager also handles them
        val AUTH_TOKEN_KEY = stringPreferencesKey("auth_token")
        val USER_ID_KEY = stringPreferencesKey("user_id")
    }

    // Unit Preference
    suspend fun setUnitPreference(unit: String) {
        dataStore.edit { prefs -> prefs[UNIT_KEY] = unit }
    }

    val unitPreferenceFlow: Flow<String> = dataStore.data
        .catch { if (it is IOException) emit(emptyPreferences()) else throw it }
        .map { prefs -> prefs[UNIT_KEY] ?: "Metric" }

    // Theme Preference
    suspend fun setDarkTheme(isDark: Boolean) {
        dataStore.edit { prefs -> prefs[DARK_THEME_KEY] = isDark }
    }

    val darkThemeFlow: Flow<Boolean> = dataStore.data
        .catch { if (it is IOException) emit(emptyPreferences()) else throw it }
        .map { prefs -> prefs[DARK_THEME_KEY] ?: false }

    // --- Auth related methods from before ---
    suspend fun saveAuthToken(token: String) {
        dataStore.edit { prefs -> prefs[AUTH_TOKEN_KEY] = token }
    }
    val authTokenFlow: Flow<String?> = dataStore.data
        .catch { if (it is IOException) emit(emptyPreferences()) else throw it }
        .map { prefs -> prefs[AUTH_TOKEN_KEY] }

    suspend fun saveUserId(userId: String) {
        dataStore.edit { prefs -> prefs[USER_ID_KEY] = userId }
    }
    val userIdFlow: Flow<String?> = dataStore.data
        .catch { if (it is IOException) emit(emptyPreferences()) else throw it }
        .map { prefs -> prefs[USER_ID_KEY] }


    suspend fun clearAuthData() {
        dataStore.edit { prefs ->
            prefs.remove(AUTH_TOKEN_KEY)
            prefs.remove(USER_ID_KEY)
        }
    }
}