package com.jis_citu.furrevercare.ui.auth.viewmodel

import android.util.Log
import android.util.Patterns
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import com.jis_citu.furrevercare.model.User
import com.jis_citu.furrevercare.navigation.Routes // Keep if needed for events
import com.jis_citu.furrevercare.network.ApiService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class RegisterUiState(
    val email: String = "",
    val password: String = "",
    val confirmPassword: String = "",
    val firstName: String = "",
    val lastName: String = "",
    val currentStep: Int = 1,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val isEmailValid: Boolean = false,
    val isPasswordValid: Boolean = false,
    val doPasswordsMatch: Boolean = true,
    val isNameValid: Boolean = false
)

sealed class RegisterNavigationEvent {
    object NavigateToSuccess : RegisterNavigationEvent()
}


@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val auth: FirebaseAuth,
    private val apiService: ApiService
) : ViewModel() {

    private val _uiState = MutableStateFlow(RegisterUiState())
    val uiState: StateFlow<RegisterUiState> = _uiState.asStateFlow()

    private val _navigationEvent = MutableSharedFlow<RegisterNavigationEvent>()
    val navigationEvent: SharedFlow<RegisterNavigationEvent> = _navigationEvent.asSharedFlow()

    private val totalSteps = 3

    fun updateEmail(newEmail: String) {
        val trimmedEmail = newEmail.trim()
        _uiState.update {
            it.copy(
                email = trimmedEmail,
                isEmailValid = trimmedEmail.isNotBlank() && Patterns.EMAIL_ADDRESS.matcher(trimmedEmail).matches()
            )
        }
    }

    fun updatePassword(newPassword: String) {
        _uiState.update {
            it.copy(
                password = newPassword,
                isPasswordValid = newPassword.length >= 8,
                doPasswordsMatch = newPassword == it.confirmPassword
            )
        }
    }

    fun updateConfirmPassword(newConfirmPassword: String) {
        _uiState.update {
            it.copy(
                confirmPassword = newConfirmPassword,
                doPasswordsMatch = it.password == newConfirmPassword
            )
        }
    }

    fun updateFirstName(newFirstName: String) {
        val trimmed = newFirstName.trimStart()
        _uiState.update {
            it.copy(
                firstName = trimmed,
                isNameValid = trimmed.isNotBlank() && it.lastName.isNotBlank()
            )
        }
    }

    fun updateLastName(newLastName: String) {
        val trimmed = newLastName.trimStart()
        _uiState.update {
            it.copy(
                lastName = trimmed,
                isNameValid = it.firstName.isNotBlank() && trimmed.isNotBlank()
            )
        }
    }

    fun nextStep() {
        if (_uiState.value.currentStep < totalSteps) {
            _uiState.update { it.copy(currentStep = it.currentStep + 1) }
        }
    }

    fun previousStep() {
        if (_uiState.value.currentStep > 1) {
            _uiState.update { it.copy(currentStep = it.currentStep - 1) }
        }
    }

    fun registerUser() {
        val currentState = _uiState.value
        if (currentState.isLoading || !currentState.isNameValid) return // Basic validation

        _uiState.update { it.copy(isLoading = true, errorMessage = null) }
        Log.d("RegisterViewModel", "Attempting registration for: ${currentState.email}")

        auth.createUserWithEmailAndPassword(currentState.email, currentState.password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d("RegisterViewModel", "Firebase Auth user created successfully.")
                    val firebaseUser = auth.currentUser
                    if (firebaseUser != null) {
                        createBackendProfile(firebaseUser.uid, currentState)
                    } else {
                        handleRegistrationError("Registration failed: Firebase user is null after creation.")
                        Log.e("RegisterViewModel", "Firebase user was null after task.isSuccessful")
                    }
                } else {
                    val exception = task.exception
                    val message = when (exception) {
                        is FirebaseAuthUserCollisionException -> "This email address is already registered."
                        is FirebaseAuthWeakPasswordException -> "Password is too weak (minimum 8 characters)."
                        else -> "Registration failed: ${exception?.localizedMessage ?: "Unknown error"}"
                    }
                    handleRegistrationError(message)
                    Log.e("RegisterViewModel", "Firebase user creation failed", exception)
                }
            }
    }

    private fun createBackendProfile(uid: String, currentState: RegisterUiState) {
        val name = "${currentState.firstName} ${currentState.lastName}".trim()
        val userProfile = User(
            userID = uid,
            name = name,
            email = currentState.email,
            password = "",
            phone = ""
        )

        viewModelScope.launch {
            try {
                Log.d("RegisterViewModel", "Calling backend createUserProfile for UID: $uid")
                val response = apiService.createUserProfile(userProfile)

                if (response.isSuccessful) {
                    Log.d("RegisterViewModel", "Backend profile creation successful: ${response.body()}")
                    _navigationEvent.emit(RegisterNavigationEvent.NavigateToSuccess)
                } else {
                    val errorBody = response.errorBody()?.string()
                    val message = "Failed to create user profile (Code: ${response.code()}). Please try again."
                    handleRegistrationError(message)
                    Log.e("RegisterViewModel", "Backend profile creation failed: ${response.code()} - $errorBody")
                    auth.currentUser?.delete()?.addOnCompleteListener { dt -> Log.w("RegisterViewModel", "Orphaned Firebase user deleted after backend failure: ${dt.isSuccessful}") }
                }
            } catch (e: Exception) {
                handleRegistrationError("An error occurred: ${e.message}")
                Log.e("RegisterViewModel", "Backend API call exception", e)
                auth.currentUser?.delete()?.addOnCompleteListener { dt -> Log.w("RegisterViewModel", "Orphaned Firebase user deleted after exception: ${dt.isSuccessful}") }
            }
        }
    }

    private fun handleRegistrationError(message: String) {
        _uiState.update { it.copy(isLoading = false, errorMessage = message) }
    }
}