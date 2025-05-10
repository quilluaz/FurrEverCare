package com.jis_citu.furrevercare.ui.auth.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.jis_citu.furrevercare.data.TokenManager // Import TokenManager interface
import com.jis_citu.furrevercare.network.ApiService
import com.jis_citu.furrevercare.network.dto.FirebaseIdTokenRequest
import com.jis_citu.furrevercare.network.dto.BackendAuthResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

data class LoginUiState(
    val email: String = "",
    val password: String = "",
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val passwordVisible: Boolean = false
)

sealed class LoginNavigationEvent {
    data object NavigateToHome : LoginNavigationEvent()
}

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val auth: FirebaseAuth,
    private val apiService: ApiService,
    private val tokenManager: TokenManager // Injecting the TokenManager interface
) : ViewModel() {

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    private val _navigationEvent = MutableSharedFlow<LoginNavigationEvent>()
    val navigationEvent: SharedFlow<LoginNavigationEvent> = _navigationEvent.asSharedFlow()

    fun updateEmail(newEmail: String) {
        _uiState.update { it.copy(email = newEmail.trim(), errorMessage = null) }
    }

    fun updatePassword(newPassword: String) {
        _uiState.update { it.copy(password = newPassword, errorMessage = null) }
    }

    fun togglePasswordVisibility() {
        _uiState.update { it.copy(passwordVisible = !it.passwordVisible) }
    }

    fun loginUser() {
        val currentState = _uiState.value
        if (currentState.email.isBlank() || currentState.password.isBlank()) {
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
                        firebaseUser.getIdToken(true)
                            .addOnCompleteListener { tokenTask ->
                                if (tokenTask.isSuccessful) {
                                    val firebaseIdToken = tokenTask.result?.token
                                    if (firebaseIdToken != null) {
                                        Log.d("LoginViewModel", "Successfully retrieved Firebase ID token.")
                                        exchangeFirebaseTokenForCustomJwt(firebaseIdToken, firebaseUser.uid)
                                    } else {
                                        Log.e("LoginViewModel", "Firebase ID token is null.")
                                        handleLoginError("Login failed: Could not obtain Firebase ID token.")
                                    }
                                } else {
                                    Log.e("LoginViewModel", "Failed to get Firebase ID token.", tokenTask.exception)
                                    handleLoginError("Login failed: ${tokenTask.exception?.localizedMessage ?: "Could not get Firebase ID token."}")
                                }
                            }
                    } else {
                        Log.e("LoginViewModel", "Firebase user null after successful sign in (unexpected).")
                        handleLoginError("Login failed: Firebase user is null after success.")
                    }
                } else {
                    val exception = task.exception
                    val message = when (exception) {
                        is FirebaseAuthInvalidUserException -> "No account found with this email."
                        is FirebaseAuthInvalidCredentialsException -> "Incorrect password. Please try again."
                        else -> "Login failed: ${exception?.localizedMessage ?: "Unknown Firebase error."}"
                    }
                    Log.e("LoginViewModel", "Firebase Sign In failed.", exception)
                    handleLoginError(message)
                }
            }
    }

    private fun exchangeFirebaseTokenForCustomJwt(firebaseIdToken: String, firebaseUid: String) {
        viewModelScope.launch {
            try {
                Log.d("LoginViewModel", "Exchanging Firebase ID token for custom JWT.")
                val request = FirebaseIdTokenRequest(idToken = firebaseIdToken)
                val response = apiService.exchangeFirebaseToken(request)

                if (response.isSuccessful && response.body() != null) {
                    val backendAuthResponse = response.body()!!
                    val customToken = backendAuthResponse.token
                    val backendReportedUid = backendAuthResponse.user.userID

                    if (customToken.isNotBlank() && backendReportedUid == firebaseUid) {
                        tokenManager.saveToken(customToken) // Use TokenManager interface method
                        tokenManager.saveUserId(backendReportedUid) // Use TokenManager interface method
                        Log.d("LoginViewModel", "Custom JWT received and saved. Verifying backend profile.")
                        verifyBackendProfile(firebaseUid)
                    } else {
                        Log.e("LoginViewModel", "Custom JWT or UID mismatch. Token blank: ${customToken.isBlank()}, BackendUID: $backendReportedUid, ExpectedFirebaseUID: $firebaseUid")
                        handleLoginError("Login failed: Backend authentication data error.")
                        auth.signOut()
                        tokenManager.clearToken() // Use TokenManager interface method
                    }
                } else {
                    val errorBody = response.errorBody()?.string()
                    Log.e("LoginViewModel", "Backend token exchange failed. Code: ${response.code()} Body: $errorBody")
                    handleLoginError("Login failed: Could not authenticate with backend (Code: ${response.code()}).")
                    auth.signOut()
                    tokenManager.clearToken()
                }
            } catch (e: HttpException) {
                val errorBody = e.response()?.errorBody()?.string()
                Log.e("LoginViewModel", "HTTP error exchanging token. Code: ${e.code()}, Body: $errorBody", e)
                handleLoginError("Login failed: Backend server error (${e.code()}).")
                auth.signOut()
                tokenManager.clearToken()
            } catch (e: IOException) {
                Log.e("LoginViewModel", "Network error exchanging token.", e)
                handleLoginError("Login failed: Network error during backend authentication.")
                auth.signOut()
                tokenManager.clearToken()
            } catch (e: Exception) {
                Log.e("LoginViewModel", "Unexpected error exchanging token.", e)
                handleLoginError("Login failed: An unexpected error occurred during token exchange.")
                auth.signOut()
                tokenManager.clearToken()
            }
        }
    }

    private fun verifyBackendProfile(uid: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                Log.d("LoginViewModel", "Verifying backend profile for UID: $uid")
                val response = apiService.getUser(uid)

                if (response.isSuccessful && response.body() != null) {
                    Log.d("LoginViewModel", "Backend profile verified. Login successful.")
                    _navigationEvent.emit(LoginNavigationEvent.NavigateToHome)
                    _uiState.update { it.copy(isLoading = false, errorMessage = null) }
                } else {
                    val errorBody = response.errorBody()?.string()
                    Log.e("LoginViewModel", "Backend profile issue. Code: ${response.code()} Body: $errorBody. UID: $uid")
                    handleLoginError("Login failed: User profile access issue (Code: ${response.code()}). Please contact support.")
                    auth.signOut()
                    tokenManager.clearToken()
                }
            } catch (e: HttpException) {
                val errorBody = e.response()?.errorBody()?.string()
                Log.e("LoginViewModel", "HTTP error verifying profile. Code: ${e.code()}, Body: $errorBody", e)
                handleLoginError("Login failed: Server error verifying profile (${e.code()}).")
                auth.signOut()
                tokenManager.clearToken()
            } catch (e: IOException) {
                Log.e("LoginViewModel", "Network error verifying profile.", e)
                handleLoginError("Login failed: Network error verifying profile.")
                auth.signOut()
                tokenManager.clearToken()
            } catch (e: Exception) {
                Log.e("LoginViewModel", "Error verifying backend profile.", e)
                handleLoginError("Login failed: Could not verify user profile (${e.message}).")
                auth.signOut()
                tokenManager.clearToken()
            } finally {
                if (_uiState.value.isLoading && navigationEvent.replayCache.isEmpty()) {
                    _uiState.update { it.copy(isLoading = false) }
                }
            }
        }
    }

    private fun handleLoginError(message: String) {
        _uiState.update { it.copy(isLoading = false, errorMessage = message) }
    }

    fun logout() {
        auth.signOut()
        tokenManager.clearToken() // Use TokenManager interface method
        Log.d("LoginViewModel", "User logged out.")
    }
}