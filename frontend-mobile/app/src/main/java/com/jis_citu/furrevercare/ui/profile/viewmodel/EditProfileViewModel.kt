package com.jis_citu.furrevercare.ui.profile.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jis_citu.furrevercare.data.AuthRepository
import com.jis_citu.furrevercare.model.User // Your User model
import com.jis_citu.furrevercare.network.ApiService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

// CORRECTED EditProfileUiState
data class EditProfileUiState(
    val name: String = "",    // Directly use 'name'
    val email: String = "",   // Directly use 'email'
    val phone: String = "",   // Directly use 'phone'

    val isLoading: Boolean = false,
    val isSaving: Boolean = false,
    val errorMessage: String? = null,
    val saveSuccess: Boolean = false
)

sealed class EditProfileNavigationEvent {
    object NavigateBack : EditProfileNavigationEvent()
}

@HiltViewModel
class EditProfileViewModel @Inject constructor(
    private val apiService: ApiService,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(EditProfileUiState())
    val uiState: StateFlow<EditProfileUiState> = _uiState.asStateFlow()

    private val _navigationEvent = MutableSharedFlow<EditProfileNavigationEvent>()
    val navigationEvent: SharedFlow<EditProfileNavigationEvent> = _navigationEvent.asSharedFlow()

    private var currentUserId: String? = null

    init {
        loadUserProfile()
    }

    fun loadUserProfile() {
        currentUserId = authRepository.getCurrentUserId()
        if (currentUserId == null) {
            _uiState.update { it.copy(isLoading = false, errorMessage = "User not logged in.", saveSuccess = false) }
            Log.e("EditProfileVM", "User ID is null.")
            return
        }

        _uiState.update { it.copy(isLoading = true, errorMessage = null, saveSuccess = false) }
        viewModelScope.launch {
            try {
                Log.d("EditProfileVM", "Fetching profile for editing, user ID: $currentUserId")
                val response = apiService.getUser(currentUserId!!)
                if (response.isSuccessful && response.body() != null) {
                    val user = response.body()!!
                    _uiState.update {
                        it.copy(
                            name = user.name, // Load into uiState.name
                            email = user.email, // Load into uiState.email
                            phone = user.phone ?: "", // Load into uiState.phone
                            isLoading = false
                        )
                    }
                    Log.d("EditProfileVM", "User profile loaded for editing: ${user.name}")
                } else {
                    Log.e("EditProfileVM", "Error fetching user profile for editing: ${response.code()} - ${response.message()}")
                    _uiState.update { it.copy(isLoading = false, errorMessage = "Failed to load profile data (Code: ${response.code()}).") }
                }
            } catch (e: Exception) {
                Log.e("EditProfileVM", "Exception fetching user profile for editing", e)
                _uiState.update { it.copy(isLoading = false, errorMessage = "Network error loading profile: ${e.message}") }
            }
        }
    }

    fun updateName(newName: String) {
        _uiState.update { it.copy(name = newName, saveSuccess = false) }
    }

    fun updateEmail(newEmail: String) {
        _uiState.update { it.copy(email = newEmail, saveSuccess = false) }
    }

    fun updatePhone(newPhone: String) {
        _uiState.update { it.copy(phone = newPhone, saveSuccess = false) }
    }

    fun saveProfileChanges() {
        val userId = currentUserId
        if (userId == null) {
            _uiState.update { it.copy(errorMessage = "User not identified. Cannot save.", saveSuccess = false) }
            return
        }

        val currentState = _uiState.value
        if (currentState.name.isBlank()) {
            _uiState.update { it.copy(errorMessage = "Name cannot be empty.", saveSuccess = false) }
            return
        }
        if (currentState.email.isBlank() || !android.util.Patterns.EMAIL_ADDRESS.matcher(currentState.email).matches()) {
            _uiState.update { it.copy(errorMessage = "Please enter a valid email address.", saveSuccess = false) }
            return
        }

        _uiState.update { it.copy(isSaving = true, errorMessage = null, saveSuccess = false) }

        val userToUpdate = User(
            userID = userId,
            name = currentState.name.trim(), // Use currentState.name
            email = currentState.email.trim(), // Use currentState.email
            phone = currentState.phone.trim().ifBlank { null } // Use currentState.phone
        )

        viewModelScope.launch {
            try {
                Log.d("EditProfileVM", "Updating user profile for $userId with data: $userToUpdate")
                val response = apiService.updateUserProfile(userToUpdate)
                if (response.isSuccessful) {
                    Log.i("EditProfileVM", "Profile updated successfully.")
                    _uiState.update {
                        it.copy(
                            isSaving = false,
                            saveSuccess = true
                        )
                    }
                    _navigationEvent.emit(EditProfileNavigationEvent.NavigateBack)
                } else {
                    val errorBody = response.errorBody()?.string()
                    Log.e("EditProfileVM", "Failed to update profile: ${response.code()} - $errorBody")
                    _uiState.update { it.copy(isSaving = false, errorMessage = "Update failed (Code: ${response.code()}). ${errorBody ?: ""}".trim(), saveSuccess = false) }
                }
            } catch (e: Exception) {
                Log.e("EditProfileVM", "Exception updating profile", e)
                _uiState.update { it.copy(isSaving = false, errorMessage = "Network error: ${e.message}", saveSuccess = false) }
            }
        }
    }
}