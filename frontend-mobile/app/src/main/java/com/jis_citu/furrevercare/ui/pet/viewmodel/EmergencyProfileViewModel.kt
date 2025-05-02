package com.jis_citu.furrevercare.ui.pet.viewmodel // Or your preferred package

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.jis_citu.furrevercare.model.EmergencyProfile
import com.jis_citu.furrevercare.network.ApiService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class EmergencyProfileUiState(
    val profile: EmergencyProfile? = null,
    // Editable fields as strings
    val bloodType: String = "",
    val chronicConditions: String = "", // Use multiline TextField, parse later
    val emergencyContact: String = "",
    val specialInstructions: String = "",
    // UI control states
    val isLoading: Boolean = true,
    val isSaving: Boolean = false,
    val errorMessage: String? = null,
    val saveSuccess: Boolean = false
)

@HiltViewModel
class EmergencyProfileViewModel @Inject constructor(
    private val apiService: ApiService,
    private val auth: FirebaseAuth,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val petId: String = checkNotNull(savedStateHandle["petId"])
    private val userId: String? = auth.currentUser?.uid

    private val _uiState = MutableStateFlow(EmergencyProfileUiState())
    val uiState: StateFlow<EmergencyProfileUiState> = _uiState.asStateFlow()

    init {
        loadProfile()
    }

    fun loadProfile() {
        if (userId == null) {
            _uiState.update { it.copy(isLoading = false, errorMessage = "User not logged in.") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null, saveSuccess = false) }
            try {
                Log.d("EmergencyProfileVM", "Fetching profile for pet $petId, user $userId")
                val response = apiService.getEmergencyProfile(userId, petId)
                if (response.isSuccessful) {
                    val profileData = response.body()
                    Log.d("EmergencyProfileVM", "Profile fetched: $profileData")
                    _uiState.update {
                        it.copy(
                            profile = profileData,
                            bloodType = profileData?.bloodType ?: "",
                            // Join list to string for TextField display
                            chronicConditions = profileData?.chronicConditions?.joinToString("\n") ?: "",
                            emergencyContact = profileData?.emergencyContact ?: "",
                            specialInstructions = profileData?.specialInstructions ?: "",
                            isLoading = false
                        )
                    }
                } else if (response.code() == 404) { // Handle case where profile doesn't exist yet
                    Log.d("EmergencyProfileVM", "No existing profile found (404).")
                    _uiState.update { it.copy(isLoading = false, profile = null) } // Clear existing profile state if any
                }
                else {
                    Log.e("EmergencyProfileVM", "Error fetching profile: ${response.code()} - ${response.message()}")
                    _uiState.update { it.copy(isLoading = false, errorMessage = "Failed to load profile.") }
                }
            } catch (e: Exception) {
                Log.e("EmergencyProfileVM", "Exception fetching profile", e)
                _uiState.update { it.copy(isLoading = false, errorMessage = "Error: ${e.message}") }
            }
        }
    }

    // Update functions for TextFields
    fun updateBloodType(value: String) = _uiState.update { it.copy(bloodType = value, saveSuccess = false) }
    fun updateChronicConditions(value: String) = _uiState.update { it.copy(chronicConditions = value, saveSuccess = false) }
    fun updateEmergencyContact(value: String) = _uiState.update { it.copy(emergencyContact = value, saveSuccess = false) }
    fun updateSpecialInstructions(value: String) = _uiState.update { it.copy(specialInstructions = value, saveSuccess = false) }


    fun saveProfile() {
        if (userId == null) {
            _uiState.update { it.copy(errorMessage = "User not logged in.") }
            return
        }
        if (uiState.value.isSaving) return // Prevent double save

        _uiState.update { it.copy(isSaving = true, errorMessage = null, saveSuccess = false) }

        // Prepare data for saving
        val conditionsList = uiState.value.chronicConditions
            .split('\n') // Split by newline
            .map { it.trim() } // Trim whitespace
            .filter { it.isNotBlank() } // Remove empty lines

        val profileToSave = EmergencyProfile(
            bloodType = uiState.value.bloodType.takeIf { it.isNotBlank() }, // Send null if blank
            chronicConditions = conditionsList.takeIf { it.isNotEmpty() }, // Send null if empty
            emergencyContact = uiState.value.emergencyContact.takeIf { it.isNotBlank() },
            specialInstructions = uiState.value.specialInstructions.takeIf { it.isNotBlank() }
        )

        viewModelScope.launch {
            try {
                // Backend uses PUT for add/update on the fixed document ID "profile"
                Log.d("EmergencyProfileVM", "Saving profile for pet $petId, user $userId: $profileToSave")
                // Using update which should also create if not exists due to backend logic using set()
                val response = apiService.updateEmergencyProfile(userId, petId, profileToSave)

                if (response.isSuccessful) {
                    Log.d("EmergencyProfileVM", "Profile saved successfully.")
                    // Optionally reload profile data after save or just update success flag
                    _uiState.update { it.copy(isSaving = false, saveSuccess = true, errorMessage = null) }
                    // Reload to ensure state reflects saved data
                    loadProfile() // Reload after save
                } else {
                    Log.e("EmergencyProfileVM", "Error saving profile: ${response.code()} - ${response.message()}")
                    _uiState.update { it.copy(isSaving = false, errorMessage = "Failed to save profile (Code: ${response.code()})", saveSuccess = false) }
                }
            } catch (e: Exception) {
                Log.e("EmergencyProfileVM", "Exception saving profile", e)
                _uiState.update { it.copy(isSaving = false, errorMessage = "Error: ${e.message}", saveSuccess = false) }
            }
        }
    }
}