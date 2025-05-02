package com.jis_citu.furrevercare.ui.auth.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.jis_citu.furrevercare.network.ApiService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

// UI State
data class LoginUiState(
    val email: String = "",
    val password: String = "",
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val passwordVisible: Boolean = false
)

// Navigation Events
sealed class LoginNavigationEvent {
    object NavigateToHome : LoginNavigationEvent()
    // Add other specific failure events if needed
}

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val auth: FirebaseAuth,
    private val apiService: ApiService
) : ViewModel() {

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    private val _navigationEvent = MutableSharedFlow<LoginNavigationEvent>()
    val navigationEvent: SharedFlow<LoginNavigationEvent> = _navigationEvent.asSharedFlow()

    fun updateEmail(newEmail: String) {
        _uiState.update { it.copy(email = newEmail.trim()) }
    }

    fun updatePassword(newPassword: String) {
        _uiState.update { it.copy(password = newPassword) }
    }

    fun togglePasswordVisibility() {
        _uiState.update { it.copy(passwordVisible = !it.passwordVisible) }
    }

    fun loginUser() {
        val currentState = _uiState.value
        if (currentState.isLoading || currentState.email.isBlank() || currentState.password.isBlank()) {
            _uiState.update { it.copy(errorMessage = "Please enter both email and password.") }
            return
        }

        _uiState.update { it.copy(isLoading = true, errorMessage = null) }

        auth.signInWithEmailAndPassword(currentState.email, currentState.password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val firebaseUser = auth.currentUser
                    if (firebaseUser != null) {
                        Log.d("LoginViewModel", "Firebase Sign In Successful. UID: ${firebaseUser.uid}")
                        // Optional: Verify backend profile exists after Firebase login
                        verifyBackendProfile(firebaseUser.uid)
                    } else {
                        // Should not happen if task is successful, but handle defensively
                        handleLoginError("Login failed: Firebase user is null after success.")
                        Log.e("LoginViewModel", "Firebase user null after successful sign in.")
                    }
                } else {
                    val exception = task.exception
                    val message = when(exception) {
                        is FirebaseAuthInvalidUserException -> "No account found with this email."
                        is FirebaseAuthInvalidCredentialsException -> "Incorrect password. Please try again."
                        else -> "Login failed: ${exception?.localizedMessage ?: "Unknown error"}"
                    }
                    handleLoginError(message)
                    Log.e("LoginViewModel", "Firebase Sign In failed.", exception)
                }
            }
    }

    // Optional: Verify user exists in your backend *after* Firebase confirms login.
    // If a user exists in Firebase Auth but was deleted from your backend,
    // you might want to prevent login or handle it gracefully.
    private fun verifyBackendProfile(uid: String) {
        viewModelScope.launch {
            try {
                Log.d("LoginViewModel", "Verifying backend profile for UID: $uid")
                val response = apiService.getUser(uid) // Use suspend function
                if (response.isSuccessful && response.body() != null) {
                    Log.d("LoginViewModel", "Backend profile verified. Navigating to home.")
                    // User exists in Firebase and Backend - Login successful
                    _navigationEvent.emit(LoginNavigationEvent.NavigateToHome)
                    // Reset loading state after navigation event emitted
                    _uiState.update { it.copy(isLoading = false) }
                } else {
                    // User exists in Firebase, but not in backend (or error fetching)
                    val errorBody = response.errorBody()?.string()
                    Log.e("LoginViewModel", "Backend profile not found or error. Code: ${response.code()} Body: $errorBody. UID: $uid")
                    handleLoginError("Login failed: User profile issue. Please contact support.")
                    // Sign out the user from Firebase as their backend profile is missing/invalid
                    auth.signOut()
                }
            } catch (e: Exception) {
                Log.e("LoginViewModel", "Error verifying backend profile.", e)
                handleLoginError("Login failed: Could not verify user profile (${e.message})")
                // Sign out the user from Firebase due to the error
                auth.signOut()
            }
        }
    }


    private fun handleLoginError(message: String) {
        _uiState.update { it.copy(isLoading = false, errorMessage = message) }
        // Optionally emit failure event:
        // viewModelScope.launch { _navigationEvent.emit(LoginNavigationEvent.LoginFailed) }
    }
}