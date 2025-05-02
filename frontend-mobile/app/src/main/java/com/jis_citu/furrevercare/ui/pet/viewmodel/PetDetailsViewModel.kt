package com.jis_citu.furrevercare.ui.pet.viewmodel // Or your preferred package

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64 // Use android.util.Base64 for decoding
import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth // Needed to get current userID
import com.jis_citu.furrevercare.model.Pet
import com.jis_citu.furrevercare.network.ApiService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class PetDetailsUiState(
    val pet: Pet? = null,
    val petImage: Bitmap? = null, // Decoded image
    val isLoading: Boolean = true,
    val errorMessage: String? = null,
    // Add states for medical records, schedules later
)

@HiltViewModel
class PetDetailsViewModel @Inject constructor(
    private val apiService: ApiService,
    private val auth: FirebaseAuth, // Inject FirebaseAuth
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val petId: String = checkNotNull(savedStateHandle["petId"])
    private val userId: String? = auth.currentUser?.uid // Get current user ID

    private val _uiState = MutableStateFlow(PetDetailsUiState())
    val uiState: StateFlow<PetDetailsUiState> = _uiState.asStateFlow()

    init {
        loadPetDetails()
        // TODO: Load medical records and schedules later
    }

    fun loadPetDetails() {
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
                // Use correct API path: /api/users/{userID}/pets/{petID}
                // Assumes ApiService is updated with correct @GET path and parameters
                Log.d("PetDetailsVM", "Fetching pet $petId for user $userId")
                // *** NOTE: Ensure ApiService getPet method signature matches backend path ***
                // It should probably be getPet(userId: String, petId: String)
                val response = apiService.getPet(userId, petId) // Pass userId AND petId

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
                    _uiState.update { it.copy(isLoading = false, errorMessage = "Failed to load pet details.") }
                }
            } catch (e: Exception) {
                Log.e("PetDetailsVM", "Exception fetching pet", e)
                _uiState.update { it.copy(isLoading = false, errorMessage = "Error: ${e.message}") }
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