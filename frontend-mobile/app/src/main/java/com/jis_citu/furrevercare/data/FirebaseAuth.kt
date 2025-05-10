package com.jis_citu.furrevercare.data // Or your repository package

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import javax.inject.Inject
import javax.inject.Singleton

@Singleton // Makes sense for auth state
class AuthRepository @Inject constructor(
    private val firebaseAuth: FirebaseAuth // Inject FirebaseAuth from FirebaseModule
) {

    /**
     * Gets the current Firebase User's unique ID (UID).
     * Returns null if no user is currently signed in.
     */
    fun getCurrentUserId(): String? {
        val userId = firebaseAuth.currentUser?.uid
        Log.d("AuthRepository", "Current Firebase User ID: $userId") // Added logging
        return userId
    }

    /**
     * Checks if a user is currently logged in.
     */
    fun isUserLoggedIn(): Boolean {
        return firebaseAuth.currentUser != null
    }

    // You can add other useful functions here:
    // fun getCurrentUserEmail(): String? = firebaseAuth.currentUser?.email
    // fun signOut() = firebaseAuth.signOut() // Example sign out
}