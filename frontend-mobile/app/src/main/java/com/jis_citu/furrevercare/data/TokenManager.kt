// Create a new file, e.g., data/TokenManager.kt
package com.jis_citu.furrevercare.data

import android.content.SharedPreferences
import javax.inject.Inject
import javax.inject.Singleton

interface TokenManager {
    fun saveToken(token: String)
    fun getToken(): String?
    fun clearToken()
    fun getUserId(): String? // Optional: if you store userId from token response
    fun saveUserId(userId: String) // Optional
}

@Singleton
class SharedPreferencesTokenManager @Inject constructor(
    private val prefs: SharedPreferences // Inject SharedPreferences
) : TokenManager {

    companion object {
        private const val KEY_AUTH_TOKEN = "auth_token"
        private const val KEY_USER_ID = "user_id" // Optional
    }

    override fun saveToken(token: String) {
        prefs.edit().putString(KEY_AUTH_TOKEN, token).apply()
    }

    override fun getToken(): String? {
        return prefs.getString(KEY_AUTH_TOKEN, null)
    }

    override fun clearToken() {
        prefs.edit().remove(KEY_AUTH_TOKEN).remove(KEY_USER_ID).apply()
    }

    // Optional: if your token exchange response also gives userId separately
    // and you want to store it.
    override fun saveUserId(userId: String) {
        prefs.edit().putString(KEY_USER_ID, userId).apply()
    }

    override fun getUserId(): String? {
        return prefs.getString(KEY_USER_ID, null)
    }
}