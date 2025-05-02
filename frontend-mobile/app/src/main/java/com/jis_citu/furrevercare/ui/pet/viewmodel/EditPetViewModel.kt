package com.jis_citu.furrevercare.ui.pet.viewmodel

import android.app.Application
import android.content.ContentResolver // Import ContentResolver
import android.net.Uri
import android.provider.OpenableColumns
import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.jis_citu.furrevercare.data.AuthRepository // *** IMPORT YOUR NEW REPO ***
import com.jis_citu.furrevercare.model.Pet
import com.jis_citu.furrevercare.network.ApiService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.RequestBody.Companion.asRequestBody // Import for file request body
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream // Import InputStream
import javax.inject.Inject

// UI State - unchanged from previous version
data class EditPetUiState(
    val petId: String = "",
    val petName: String = "",
    val species: String = "",
    val breed: String = "",
    val age: String = "",
    val gender: String = "",
    val weight: String = "",
    val imageUri: Uri? = null,
    val existingImageBase64: String? = null,
    val isLoading: Boolean = true,
    val errorMessage: String? = null,
    val isSaving: Boolean = false,
    val speciesList: List<String> = listOf("Dog", "Cat", "Bird", "Rabbit", "Hamster", "Fish", "Other"),
    val genderList: List<String> = listOf("Male", "Female"),
    val breedList: List<String> = emptyList()
)

// Navigation Event - unchanged
sealed class EditPetNavigationEvent {
    object NavigateBack : EditPetNavigationEvent()
}

@HiltViewModel
class EditPetViewModel @Inject constructor(
    private val application: Application,
    private val apiService: ApiService,
    private val authRepository: AuthRepository, // *** INJECT AuthRepository ***
    private val gson: Gson,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val petId: String = checkNotNull(savedStateHandle["petId"]) { "petId not found in navigation args" }

    private val _uiState = MutableStateFlow(EditPetUiState(petId = petId))
    val uiState: StateFlow<EditPetUiState> = _uiState.asStateFlow()

    private val _navigationEvent = MutableSharedFlow<EditPetNavigationEvent>()
    val navigationEvent: SharedFlow<EditPetNavigationEvent> = _navigationEvent.asSharedFlow()

    // Store the user ID retrieved synchronously
    private var currentUserId: String? = null

    init {
        // Get user ID synchronously from AuthRepository
        currentUserId = authRepository.getCurrentUserId()
        if (currentUserId == null) {
            _uiState.update { it.copy(isLoading = false, errorMessage = "User not logged in.") }
            Log.e("EditPetVM", "User ID is null. Cannot load or save pet details.")
        } else {
            // Load details only if user ID is available
            loadPetDetails()
        }
    }

    // loadPetDetails - unchanged from previous version (uses currentUserId)
    private fun loadPetDetails() {
        val userId = currentUserId ?: return // Should not be null if init logic is correct

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            try {
                Log.d("EditPetVM", "Fetching pet details for UserID: $userId, PetID: $petId")
                val response = apiService.getPet(userId, petId)
                if (response.isSuccessful && response.body() != null) {
                    val pet = response.body()!!
                    Log.d("EditPetVM", "Pet details fetched: $pet")
                    _uiState.update {
                        it.copy(
                            petName = pet.name,
                            species = pet.species,
                            breed = pet.breed,
                            age = pet.age.toString(),
                            gender = pet.gender,
                            weight = pet.weight.toString(),
                            existingImageBase64 = pet.imageBase64,
                            breedList = getBreedsForSpecies(pet.species),
                            isLoading = false
                        )
                    }
                } else {
                    Log.e("EditPetVM", "Error fetching pet: ${response.code()} - ${response.message()}")
                    _uiState.update { it.copy(isLoading = false, errorMessage = "Failed to load pet details (Code: ${response.code()}).") }
                }
            } catch (e: Exception) {
                Log.e("EditPetVM", "Exception fetching pet", e)
                _uiState.update { it.copy(isLoading = false, errorMessage = "Network Error: ${e.message}") }
            }
        }
    }


    // State Update Functions - unchanged
    fun updatePetName(name: String) = _uiState.update { it.copy(petName = name) }
    fun updateAge(age: String) = _uiState.update { it.copy(age = age.filter { it.isDigit() }) }
    fun updateWeight(weight: String) = _uiState.update { it.copy(weight = weight.filter { it.isDigit() || it == '.' }) }
    fun updateImageUri(uri: Uri?) = _uiState.update { it.copy(imageUri = uri) }
    fun updateSpecies(species: String) {
        _uiState.update {
            it.copy(
                species = species,
                breed = "",
                breedList = getBreedsForSpecies(species)
            )
        }
    }
    fun updateBreed(breed: String) = _uiState.update { it.copy(breed = breed) }
    fun updateGender(gender: String) = _uiState.update { it.copy(gender = gender) }

    // getBreedsForSpecies - unchanged
    private fun getBreedsForSpecies(species: String): List<String> {
        // (Implementation is the same as before)
        return when (species) {
            "Dog" -> listOf("Labrador", "German Shepherd", "Golden Retriever", "Bulldog", "Poodle", "Beagle", "Shih Tzu", "Aspin", "Mixed", "Other")
            "Cat" -> listOf("Persian", "Siamese", "Maine Coon", "Ragdoll", "Bengal", "Puspin", "Mixed", "Other")
            "Bird" -> listOf("Parakeet", "Cockatiel", "Finch", "Canary", "Lovebird", "Other")
            "Rabbit" -> listOf("Holland Lop", "Netherland Dwarf", "Mini Rex", "Other")
            "Hamster" -> listOf("Syrian", "Dwarf Campbell", "Roborovski", "Other")
            "Fish" -> listOf("Goldfish", "Betta", "Guppy", "Other")
            else -> emptyList()
        }
    }

    // savePetChanges - unchanged from previous version (uses currentUserId)
    fun savePetChanges() {
        val userId = currentUserId // Use the ID fetched during init
        if (userId == null) {
            _uiState.update { it.copy(errorMessage = "Cannot save: User not identified.") }
            Log.e("EditPetVM", "Attempted to save changes but userId is null.")
            return
        }

        val currentState = _uiState.value
        // Validation - unchanged
        if (currentState.petName.isBlank() || currentState.species.isBlank() || currentState.gender.isBlank() || currentState.age.isBlank() || currentState.weight.isBlank()) {
            _uiState.update { it.copy(errorMessage = "Please fill in required fields (Name, Species, Age, Gender, Weight).") }
            return
        }
        val ageInt = currentState.age.toIntOrNull()
        val weightDouble = currentState.weight.toDoubleOrNull()
        if (ageInt == null || ageInt < 0) {
            _uiState.update { it.copy(errorMessage = "Please enter a valid age.") }
            return
        }
        if (weightDouble == null || weightDouble <= 0) {
            _uiState.update { it.copy(errorMessage = "Please enter a valid weight.") }
            return
        }

        if (currentState.isSaving) return

        _uiState.update { it.copy(isSaving = true, errorMessage = null) }

        // Prepare Pet Data - unchanged (uses existingImageBase64 logic)
        val petData = Pet(
            petID = currentState.petId,
            ownerID = userId, // Include ownerID if needed
            name = currentState.petName,
            species = currentState.species,
            breed = currentState.breed,
            age = ageInt,
            gender = currentState.gender,
            weight = weightDouble,
            imageBase64 = if (currentState.imageUri == null) currentState.existingImageBase64 else null,
            allergies = null // Assuming allergies are not handled here
        )

        // Create Request Parts - unchanged
        val petJson = gson.toJson(petData)
        val petRequestBody = petJson.toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())
        var imagePart: MultipartBody.Part? = null
        if (currentState.imageUri != null) {
            try {
                imagePart = createImagePart(application.contentResolver, currentState.imageUri, "image")
            } catch (e: Exception) {
                Log.e("EditPetVM", "Error creating image part from Uri", e)
                _uiState.update { it.copy(isSaving = false, errorMessage = "Error processing image file.") }
                return
            }
        }

        // API Call - unchanged (uses userId, petId, petRequestBody, imagePart)
        viewModelScope.launch {
            try {
                Log.d("EditPetVM", "Updating pet. UserID: $userId, PetID: $petId, Data: $petJson, ImagePart Present: ${imagePart != null}")
                val response = apiService.updatePet(
                    userId = userId,
                    petId = currentState.petId,
                    pet = petRequestBody,
                    image = imagePart
                )
                // ... rest of API call handling (success/error/finally) ...
                if (response.isSuccessful) {
                    Log.d("EditPetVM", "Pet update successful.")
                    _navigationEvent.emit(EditPetNavigationEvent.NavigateBack)
                } else {
                    val errorBody = response.errorBody()?.string()
                    Log.e("EditPetVM", "Error updating pet: ${response.code()} - $errorBody")
                    _uiState.update { it.copy(errorMessage = "Failed to update pet (Code: ${response.code()})") }
                }

            } catch (e: Exception) {
                Log.e("EditPetVM", "Exception updating pet", e)
                _uiState.update { it.copy(errorMessage = "Network Error: ${e.message}") }
            } finally {
                _uiState.update { it.copy(isSaving = false) }
            }
        }
    }


    // Helper Function createImagePart - unchanged
    private fun createImagePart(contentResolver: ContentResolver, uri: Uri, partName: String): MultipartBody.Part? {
        return try {
            var fileName: String? = null
            contentResolver.query(uri, null, null, null, null)?.use { cursor ->
                if (cursor.moveToFirst()) {
                    val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                    if (nameIndex != -1) {
                        fileName = cursor.getString(nameIndex)
                    }
                }
            }
            val mimeType = contentResolver.getType(uri)
            fileName = fileName ?: "${System.currentTimeMillis()}.${mimeType?.substringAfter('/') ?: "file"}"

            val inputStream: InputStream = contentResolver.openInputStream(uri) ?: return null
            val tempFile = File(application.cacheDir, fileName)
            val outputStream = FileOutputStream(tempFile)
            inputStream.copyTo(outputStream)
            inputStream.close()
            outputStream.close()

            val requestFile = tempFile.asRequestBody(mimeType?.toMediaTypeOrNull())

            MultipartBody.Part.createFormData(partName, tempFile.name, requestFile).also {
                tempFile.deleteOnExit()
                Log.d("EditPetVM","Created image part: ${tempFile.name}, Type: $mimeType")
            }
        } catch (e: Exception) {
            Log.e("EditPetVM", "Failed to create MultipartBody.Part from Uri: $uri", e)
            null
        }
    }
}