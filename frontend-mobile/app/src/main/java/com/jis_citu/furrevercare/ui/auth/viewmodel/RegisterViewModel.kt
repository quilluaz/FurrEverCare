package com.jis_citu.furrevercare.ui.auth.viewmodel

import android.util.Log
import android.util.Patterns
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import com.jis_citu.furrevercare.model.User // Your User model
import com.jis_citu.furrevercare.network.ApiService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

data class RegisterUiState(
    val email: String = "",
    val password: String = "",
    val confirmPassword: String = "",
    val fullName: String = "", // Changed from firstName, lastName
    // val phone: String = "", // If you collect phone during registration, add it here
    val currentStep: Int = 1,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val isEmailValid: Boolean = false,
    val isPasswordValid: Boolean = false,
    val doPasswordsMatch: Boolean = true,
    val isFullNameValid: Boolean = false // Changed from isNameValid
    // val isPhoneValid: Boolean = false
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

    private val totalSteps = 3 // 1=Email, 2=Password, 3=Name (now Full Name)

    fun updateEmail(newEmail: String) {
        val trimmedEmail = newEmail.trim()
        _uiState.update {
            it.copy(
                email = trimmedEmail,
                isEmailValid = trimmedEmail.isNotBlank() && Patterns.EMAIL_ADDRESS.matcher(trimmedEmail).matches(),
                errorMessage = null
            )
        }
    }

    fun updatePassword(newPassword: String) {
        _uiState.update {
            it.copy(
                password = newPassword,
                isPasswordValid = newPassword.length >= 8,
                doPasswordsMatch = newPassword == it.confirmPassword,
                errorMessage = null
            )
        }
    }

    fun updateConfirmPassword(newConfirmPassword: String) {
        _uiState.update {
            it.copy(
                confirmPassword = newConfirmPassword,
                doPasswordsMatch = it.password == newConfirmPassword,
                errorMessage = null
            )
        }
    }

    fun updateFullName(newFullName: String) { // Changed from updateFirstName/LastName
        val trimmed = newFullName.trimStart() // Trim only start to allow spaces in middle
        _uiState.update {
            it.copy(
                fullName = trimmed,
                isFullNameValid = trimmed.isNotBlank() && trimmed.length >= 2, // Example validation
                errorMessage = null
            )
        }
    }

    // fun updatePhone(newPhone: String) { ... } // Keep if you add phone to registration

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
        if (!currentState.isEmailValid || !currentState.isPasswordValid || !currentState.doPasswordsMatch || !currentState.isFullNameValid) {
            _uiState.update { it.copy(errorMessage = "Please correct the errors above.") }
            return
        }
        if (currentState.isLoading) return

        _uiState.update { it.copy(isLoading = true, errorMessage = null) }
        Log.d("RegisterViewModel", "Attempting Firebase registration for: ${currentState.email}")

        auth.createUserWithEmailAndPassword(currentState.email, currentState.password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d("RegisterViewModel", "Firebase Auth user created successfully.")
                    val firebaseUser = auth.currentUser
                    if (firebaseUser != null) {
                        createBackendProfile(firebaseUser.uid, currentState)
                    } else {
                        handleRegistrationError("Registration failed: Firebase user is null after creation.")
                        Log.e("RegisterViewModel", "Firebase user was null after task.isSuccessful for createUserWithEmailAndPassword.")
                    }
                } else {
                    val exception = task.exception
                    val message = when (exception) {
                        is FirebaseAuthUserCollisionException -> "This email address is already registered."
                        is FirebaseAuthWeakPasswordException -> "Password is too weak (minimum 8 characters recommended)."
                        else -> "Registration failed: ${exception?.localizedMessage ?: "Unknown Firebase error"}"
                    }
                    handleRegistrationError(message)
                    Log.e("RegisterViewModel", "Firebase user creation failed.", exception)
                }
            }
    }

    private fun createBackendProfile(firebaseUid: String, currentState: RegisterUiState) {
        // Use the single fullName field directly
        val userProfile = User(
            userID = firebaseUid,
            name = currentState.fullName.trim(), // Use the collected fullName
            email = currentState.email,
            phone = null // Set to currentState.phone if you collect phone during registration
        )

        viewModelScope.launch {
            try {
                Log.d("RegisterViewModel", "Calling backend createUserProfile for UID: $firebaseUid with payload: $userProfile")
                val response = apiService.createUserProfile(userProfile)

                if (response.isSuccessful) {
                    val responseBodyString = response.body()?.string()
                    Log.d("RegisterViewModel", "Backend profile creation successful. Response: $responseBodyString")
                    _navigationEvent.emit(RegisterNavigationEvent.NavigateToSuccess)
                } else {
                    val errorBody = response.errorBody()?.string()
                    val displayMessage = "Failed to create user profile on backend (Code: ${response.code()}). Error: ${errorBody ?: "Unknown backend error"}"
                    handleRegistrationError(displayMessage)
                    Log.e("RegisterViewModel", "Backend profile creation failed: ${response.code()} - $errorBody")
                    auth.currentUser?.delete()?.addOnCompleteListener { deleteTask ->
                        Log.w("RegisterViewModel", "Orphaned Firebase user deletion attempt after backend failure. Success: ${deleteTask.isSuccessful}")
                    }
                }
            } catch (e: HttpException) {
                Log.e("RegisterViewModel", "HTTP exception during backend API call", e)
                handleRegistrationError("Registration error (HTTP ${e.code()}): ${e.message()}")
                auth.currentUser?.delete()?.addOnCompleteListener { deleteTask -> Log.w("RegisterViewModel", "Orphaned Firebase user deletion attempt after HTTP exception. Success: ${deleteTask.isSuccessful}") }
            } catch (e: IOException) {
                Log.e("RegisterViewModel", "Network exception during backend API call", e)
                handleRegistrationError("Network error. Please check your connection and try again.")
                auth.currentUser?.delete()?.addOnCompleteListener { deleteTask -> Log.w("RegisterViewModel", "Orphaned Firebase user deletion attempt after IO exception. Success: ${deleteTask.isSuccessful}") }
            } catch (e: Exception) {
                Log.e("RegisterViewModel", "Generic exception during backend API call", e)
                handleRegistrationError("An error occurred: ${e.message}")
                auth.currentUser?.delete()?.addOnCompleteListener { deleteTask ->
                    Log.w("RegisterViewModel", "Orphaned Firebase user deletion attempt after generic exception. Success: ${deleteTask.isSuccessful}")
                }
            } finally {
                _uiState.update { it.copy(isLoading = false) }
            }
        }
    }

    private fun handleRegistrationError(message: String) {
        _uiState.update { it.copy(isLoading = false, errorMessage = message) }
    }
}