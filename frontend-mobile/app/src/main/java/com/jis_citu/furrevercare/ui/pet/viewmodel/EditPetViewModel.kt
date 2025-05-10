package com.jis_citu.furrevercare.ui.pet.viewmodel

import android.app.Application
import android.content.ContentResolver
import android.net.Uri
import android.provider.OpenableColumns
import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.jis_citu.furrevercare.data.AuthRepository
import com.jis_citu.furrevercare.model.Pet
import com.jis_citu.furrevercare.network.ApiService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import javax.inject.Inject

data class EditPetUiState(
    val isEditMode: Boolean = false,
    val petId: String? = null, // Nullable for add mode
    val petName: String = "",
    val species: String = "",
    val breed: String = "",
    val age: String = "",
    val gender: String = "",
    val weight: String = "",
    val imageUri: Uri? = null, // For newly picked image
    val existingImageBase64: String? = null, // For displaying/retaining existing image
    val isLoading: Boolean = false, // Default false; true if loading existing pet
    val errorMessage: String? = null,
    val isSaving: Boolean = false,
    val saveSuccess: Boolean = false,
    val speciesList: List<String> = listOf("Dog", "Cat", "Bird", "Rabbit", "Hamster", "Fish", "Other"),
    val genderList: List<String> = listOf("Male", "Female", "Unknown"),
    val breedList: List<String> = emptyList()
)

sealed class EditPetNavigationEvent {
    object NavigateBack : EditPetNavigationEvent()
}

@HiltViewModel
class EditPetViewModel @Inject constructor(
    private val application: Application,
    private val apiService: ApiService,
    private val authRepository: AuthRepository,
    private val gson: Gson,
    savedStateHandle: SavedStateHandle // Injected SavedStateHandle
) : ViewModel() {

    // Initialize petIdFromArgs from savedStateHandle first
    private val petIdFromArgs: String? = savedStateHandle["petId"]

    private val _uiState = MutableStateFlow(
        EditPetUiState( // Initialize state based on petIdFromArgs
            isEditMode = petIdFromArgs != null,
            petId = petIdFromArgs,
            isLoading = petIdFromArgs != null // Start loading only if it's edit mode
        )
    )
    val uiState: StateFlow<EditPetUiState> = _uiState.asStateFlow()

    private val _navigationEvent = MutableSharedFlow<EditPetNavigationEvent>()
    val navigationEvent: SharedFlow<EditPetNavigationEvent> = _navigationEvent.asSharedFlow()

    private var currentUserId: String? = null

    init {
        currentUserId = authRepository.getCurrentUserId()

        if (currentUserId == null) {
            _uiState.update { it.copy(isLoading = false, errorMessage = "User not logged in.") }
            Log.e("EditPetVM", "User ID is null. Cannot proceed.")
        } else {
            // If in edit mode (petIdFromArgs is not null), load pet details
            if (_uiState.value.isEditMode && petIdFromArgs != null) {
                loadPetDetails(petIdFromArgs)
            } else {
                // For "Add Pet" mode, ensure initial species and breed list are set if speciesList is not empty
                val initialSpecies = _uiState.value.speciesList.firstOrNull() ?: ""
                _uiState.update {
                    it.copy(
                        isLoading = false, // Not loading existing data for add mode
                        species = initialSpecies,
                        breedList = if (initialSpecies.isNotEmpty()) getBreedsForSpecies(initialSpecies) else emptyList()
                    )
                }
            }
        }
    }

    private fun loadPetDetails(petIdToLoad: String) {
        val userId = currentUserId ?: return // Should have been checked in init
        Log.d("EditPetVM", "loadPetDetails called for petId: $petIdToLoad by user: $userId")
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null, saveSuccess = false) } // Set loading true
            try {
                val response = apiService.getPetById(userId, petIdToLoad)
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

    fun updatePetName(name: String) = _uiState.update { it.copy(petName = name, saveSuccess = false) }
    fun updateAge(age: String) = _uiState.update { it.copy(age = age.filter { char -> char.isDigit() }, saveSuccess = false) }
    fun updateWeight(weight: String) = _uiState.update { it.copy(weight = weight.filter { char -> char.isDigit() || char == '.' }, saveSuccess = false) }
    fun updateImageUri(uri: Uri?) = _uiState.update { it.copy(imageUri = uri, saveSuccess = false) }

    fun updateSpecies(species: String) {
        _uiState.update {
            it.copy(
                species = species,
                breed = "", // Clear breed when species changes
                breedList = getBreedsForSpecies(species),
                saveSuccess = false
            )
        }
    }

    fun updateBreed(breed: String) = _uiState.update { it.copy(breed = breed, saveSuccess = false) }
    fun updateGender(gender: String) = _uiState.update { it.copy(gender = gender, saveSuccess = false) }

    // Using the version from EditPetViewModelPreviewHelper for consistency,
    // assuming it's the desired list.
    private fun getBreedsForSpecies(species: String): List<String> {
        return when (species.lowercase()) {
            "dog" -> listOf("Labrador Retriever", "German Shepherd", "Golden Retriever", "Bulldog", "Poodle", "Beagle", "Rottweiler", "Dachshund", "Shih Tzu", "Siberian Husky", "Pomeranian", "Chihuahua", "Aspin (Askal)", "Mixed Breed", "Other")
            "cat" -> listOf("Persian", "Siamese", "Maine Coon", "Ragdoll", "Bengal", "Sphinx", "British Shorthair", "Scottish Fold", "Puspin (Pusang Pinoy)", "Mixed Breed", "Other")
            "bird" -> listOf("Parakeet (Budgerigar)", "Cockatiel", "African Grey Parrot", "Lovebird", "Finch", "Canary", "Other")
            "rabbit" -> listOf("Holland Lop", "Netherland Dwarf", "Mini Rex", "Flemish Giant", "Lionhead", "Other")
            "hamster" -> listOf("Syrian (Golden)", "Dwarf Winter White", "Roborovski", "Campbell's Dwarf", "Chinese", "Other")
            "fish" -> listOf("Goldfish", "Betta (Siamese Fighting Fish)", "Guppy", "Angelfish", "Koi", "Oscar", "Other")
            else -> listOf("Other", "Mixed Breed", "Not Applicable")
        }
    }

    fun savePet() {
        val userId = currentUserId
        if (userId == null) {
            _uiState.update { it.copy(errorMessage = "Cannot save: User not identified.") }
            return
        }

        val currentState = _uiState.value
        // Basic Validation
        if (currentState.petName.isBlank()) {
            _uiState.update { it.copy(errorMessage = "Pet name cannot be empty.") }
            return
        }
        if (currentState.species.isBlank()) {
            _uiState.update { it.copy(errorMessage = "Please select a species.") }
            return
        }
        // Breed can be optional or "Other"
        if (currentState.age.isBlank()) {
            _uiState.update { it.copy(errorMessage = "Please enter pet's age.") }
            return
        }
        val ageInt = currentState.age.toIntOrNull()
        if (ageInt == null || ageInt < 0) {
            _uiState.update { it.copy(errorMessage = "Please enter a valid age (0 or greater).") }
            return
        }
        if (currentState.gender.isBlank()) {
            _uiState.update { it.copy(errorMessage = "Please select a gender.") }
            return
        }
        if (currentState.weight.isBlank()) {
            _uiState.update { it.copy(errorMessage = "Please enter pet's weight.") }
            return
        }
        val weightDouble = currentState.weight.toDoubleOrNull()
        if (weightDouble == null || weightDouble <= 0) {
            _uiState.update { it.copy(errorMessage = "Please enter a valid weight (greater than 0).") }
            return
        }

        if (currentState.isSaving) return
        _uiState.update { it.copy(isSaving = true, errorMessage = null, saveSuccess = false) }

        val petData = Pet(
            petID = if (currentState.isEditMode) currentState.petId ?: "" else "",
            ownerID = userId,
            name = currentState.petName.trim(),
            species = currentState.species.trim(),
            breed = currentState.breed.trim(),
            age = ageInt,
            gender = currentState.gender,
            weight = weightDouble,
            // TEMPORARILY DISABLED NEW IMAGE:
            // If editing, use existing image. If adding, image will be null.
            imageBase64 = if (currentState.isEditMode) currentState.existingImageBase64 else null,
            allergies = null // TODO: Add allergy input fields if needed
        )

        val petJson = gson.toJson(petData)
        Log.d("EditPetVM", "Pet JSON for save: $petJson")
        val petRequestBody = petJson.toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())

        // *** IMAGE UPLOAD TEMPORARILY DISABLED ***
        val imagePart: MultipartBody.Part? = null
        // Original image part creation logic commented out:
        /*
        currentState.imageUri?.let { uri ->
            try {
                imagePart = createImagePart(application.contentResolver, uri, "image")
            } catch (e: Exception) {
                Log.e("EditPetVM", "Error creating image part from Uri caught in savePet", e)
                _uiState.update { it.copy(isSaving = false, errorMessage = "Error processing image file: ${e.message}") }
                return@savePet
            }
        }
        */

        viewModelScope.launch {
            try {
                val response = if (currentState.isEditMode) {
                    Log.d("EditPetVM", "Updating pet. UserID: $userId, PetID: ${currentState.petId}, RequestBody: $petRequestBody, ImagePart: $imagePart")
                    apiService.updatePet(userId, currentState.petId!!, petRequestBody, imagePart)
                } else {
                    Log.d("EditPetVM", "Adding new pet. UserID: $userId, RequestBody: $petRequestBody, ImagePart: $imagePart")
                    apiService.addPet(userId, petRequestBody, imagePart)
                }

                if (response.isSuccessful) {
                    val action = if (currentState.isEditMode) "update" else "add"
                    Log.d("EditPetVM", "Pet $action successful. Response: ${response.body()?.string()}") // Log response body if any
                    _uiState.update { it.copy(saveSuccess = true) }
                    _navigationEvent.emit(EditPetNavigationEvent.NavigateBack)
                } else {
                    val errorBody = response.errorBody()?.string()
                    val action = if (currentState.isEditMode) "update" else "add"
                    Log.e("EditPetVM", "Error ${action}ing pet: ${response.code()} - $errorBody")
                    _uiState.update { it.copy(errorMessage = "Failed to $action pet (Code: ${response.code()}). ${errorBody ?: ""}".trim()) }
                }
            } catch (e: Exception) {
                val action = if (currentState.isEditMode) "update" else "add"
                Log.e("EditPetVM", "Exception ${action}ing pet", e)
                _uiState.update { it.copy(errorMessage = "Network Error while ${action}ing pet: ${e.message}") }
            } finally {
                _uiState.update { it.copy(isSaving = false) }
            }
        }
    }

    // This function is currently unused since image upload is disabled,
    // but kept for future re-enablement.
    private fun createImagePart(contentResolver: ContentResolver, uri: Uri, partName: String): MultipartBody.Part {
        try {
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
            val safeFileName = fileName?.replace(Regex("[^a-zA-Z0-9._-]"), "_") ?: "${System.currentTimeMillis()}.${mimeType?.substringAfterLast('/') ?: "file"}"

            val tempFile = File(application.cacheDir, safeFileName).apply { createNewFile() }

            contentResolver.openInputStream(uri)?.use { inputStream ->
                FileOutputStream(tempFile).use { outputStream ->
                    inputStream.copyTo(outputStream)
                }
            } ?: throw IOException("Unable to open input stream for URI: $uri")


            val requestFile = tempFile.asRequestBody(mimeType?.toMediaTypeOrNull())
            return MultipartBody.Part.createFormData(partName, tempFile.name, requestFile).also {
                Log.d("EditPetVM", "Created image part: ${tempFile.name}, Type: $mimeType, Size: ${tempFile.length()}")
                // Consider deleting tempFile after the API call in savePet's finally block or on success
                tempFile.delete() // Example: delete immediately after creating the part if it's copied by Retrofit
            }
        } catch (e: Exception) {
            Log.e("EditPetVM", "Failed to create MultipartBody.Part from Uri: $uri", e)
            throw IOException("Failed to process image for upload: ${e.localizedMessage}", e)
        }
    }
}