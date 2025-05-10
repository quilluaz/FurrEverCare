package com.jis_citu.furrevercare.ui.pet.viewmodel

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.jis_citu.furrevercare.model.Pet
import com.jis_citu.furrevercare.network.ApiService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

// PetDetailsUiState (Keep your existing definition)
data class PetDetailsUiState(
    val pet: Pet? = null,
    val petImage: Bitmap? = null,
    val isLoading: Boolean = true,
    val errorMessage: String? = null,
)

@HiltViewModel
class PetDetailsViewModel @Inject constructor(
    private val apiService: ApiService,
    private val auth: FirebaseAuth,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val petId: String = checkNotNull(savedStateHandle["petId"])
    // It's safer to get the userId within the function call that needs it,
    // or ensure it's refreshed if the user can log out while the VM is alive.
    // For init, this is okay.
    private val currentUserId: String? = auth.currentUser?.uid

    private val _uiState = MutableStateFlow(PetDetailsUiState())
    val uiState: StateFlow<PetDetailsUiState> = _uiState.asStateFlow()

    init {
        loadPetDetails()
    }

    fun loadPetDetails() {
        val userId = auth.currentUser?.uid // Re-fetch in case of user change, or use stored currentUserId
        if (userId == null) {
            _uiState.update { it.copy(isLoading = false, errorMessage = "User not logged in.") }
            return
        }
        if (petId.isBlank()) {
            _uiState.update { it.copy(isLoading = false, errorMessage = "Pet ID missing.") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            try {
                Log.d("PetDetailsVM", "Fetching pet $petId for user $userId")
                // *** CORRECTED METHOD CALL HERE ***
                val response = apiService.getPetById(userId, petId)

                if (response.isSuccessful && response.body() != null) {
                    val petData = response.body()!!
                    val decodedBitmap = decodeBase64Image(petData.imageBase64)
                    _uiState.update {
                        it.copy(
                            pet = petData,
                            petImage = decodedBitmap,
                            isLoading = false
                        )
                    }
                    Log.d("PetDetailsVM", "Pet details loaded successfully.")
                } else {
                    Log.e("PetDetailsVM", "Error fetching pet: ${response.code()} - ${response.message()}")
                    _uiState.update { it.copy(isLoading = false, errorMessage = "Failed to load pet details (Code: ${response.code()}).") }
                }
            } catch (e: Exception) {
                Log.e("PetDetailsVM", "Exception fetching pet", e)
                _uiState.update { it.copy(isLoading = false, errorMessage = "Network Error: ${e.message}") }
            }
        }
    }

    private fun decodeBase64Image(base64String: String?): Bitmap? {
        if (base64String.isNullOrBlank()) return null
        return try {
            val imageBytes = Base64.decode(base64String, Base64.DEFAULT)
            BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
        } catch (e: IllegalArgumentException) {
            Log.e("PetDetailsVM", "Base64 decoding failed", e)
            null
        } catch (e: Exception) {
            Log.e("PetDetailsVM", "Bitmap decoding failed", e)
            null
        }
    }
}