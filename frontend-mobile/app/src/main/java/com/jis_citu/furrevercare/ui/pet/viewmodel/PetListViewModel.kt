package com.jis_citu.furrevercare.ui.pet.viewmodel // Or your preferred package for pet-related ViewModels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jis_citu.furrevercare.data.AuthRepository
import com.jis_citu.furrevercare.model.Pet
import com.jis_citu.furrevercare.network.ApiService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class PetListUiState(
    val pets: List<Pet> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val isUserLoggedIn: Boolean = true // Assume user is logged in initially
)

@HiltViewModel
class PetListViewModel @Inject constructor(
    private val apiService: ApiService,
    private val authRepository: AuthRepository // To get the current user ID
) : ViewModel() {

    private val _uiState = MutableStateFlow(PetListUiState())
    val uiState: StateFlow<PetListUiState> = _uiState.asStateFlow()

    private var currentUserId: String? = null

    init {
        currentUserId = authRepository.getCurrentUserId()
        if (currentUserId == null) {
            _uiState.update { it.copy(isLoading = false, errorMessage = "User not logged in.", isUserLoggedIn = false) }
            Log.e("PetListVM", "User ID is null. Cannot load pets.")
        } else {
            fetchUserPets()
        }
    }

    fun fetchUserPets() {
        val userId = currentUserId
        if (userId == null) {
            // This case should ideally be handled by the init block, but as a safeguard:
            _uiState.update { it.copy(isLoading = false, errorMessage = "User not logged in. Please re-login.", isUserLoggedIn = false) }
            Log.w("PetListVM", "fetchUserPets called but user ID is null.")
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            try {
                Log.d("PetListVM", "Fetching pets for user ID: $userId")
                val response = apiService.getUserPets(userId)
                if (response.isSuccessful && response.body() != null) {
                    val fetchedPets = response.body()!!
                    Log.d("PetListVM", "Successfully fetched ${fetchedPets.size} pets.")
                    _uiState.update {
                        it.copy(
                            pets = fetchedPets,
                            isLoading = false
                        )
                    }
                } else {
                    val errorMsg = "Failed to load pets (Code: ${response.code()})"
                    Log.e("PetListVM", "$errorMsg - ${response.message()}")
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = errorMsg
                        )
                    }
                }
            } catch (e: Exception) {
                Log.e("PetListVM", "Exception fetching user pets", e)
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = "Network error: ${e.message}"
                    )
                }
            }
        }
    }

    fun refreshPets() {
        // Re-check user ID in case of user session changes, though init should cover initial state
        currentUserId = authRepository.getCurrentUserId()
        if (currentUserId != null) {
            fetchUserPets()
        } else {
            _uiState.update { it.copy(isLoading = false, errorMessage = "User not logged in. Cannot refresh.", isUserLoggedIn = false, pets = emptyList()) }
            Log.e("PetListVM", "Refresh attempted but User ID is null.")
        }
    }
}