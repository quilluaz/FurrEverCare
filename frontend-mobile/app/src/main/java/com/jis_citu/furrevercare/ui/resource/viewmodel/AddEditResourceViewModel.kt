package com.jis_citu.furrevercare.ui.resource.viewmodel

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jis_citu.furrevercare.data.AuthRepository
import com.jis_citu.furrevercare.model.resources.ResourceContact // Your Android ResourceContact model
import com.jis_citu.furrevercare.model.resources.ResourceItem    // Your Android ResourceItem model
import com.jis_citu.furrevercare.model.resources.ResourceType    // Your Android ResourceType enum
import com.jis_citu.furrevercare.network.ApiService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

// Represents the input fields in the form
data class ResourceFormState(
    val name: String = "",
    val selectedType: ResourceType = ResourceType.default(), // Default to the first type
    val customTypeName: String = "", // For ResourceType.OTHER
    val contactPhoneNumber: String = "",
    val contactEmail: String = "",
    val contactWebsite: String = "",
    val contactAddress: String = "",
    // LatLng later if needed
    val notes: String = "",
    val operatingHours: String = ""
)

data class AddEditResourceUiState(
    val isLoading: Boolean = false,
    val isEditing: Boolean = false, // True if resourceId is provided
    val resourceId: String? = null,
    val formState: ResourceFormState = ResourceFormState(),
    val saveResult: SaveResult? = null, // To signal success or failure
    val loadError: String? = null,
    val generalMessage: String? = null // For other messages like "Saved successfully"
)

sealed class SaveResult {
    data object Success : SaveResult()
    data class Error(val message: String) : SaveResult()
}

@HiltViewModel
class AddEditResourceViewModel @Inject constructor(
    private val apiService: ApiService,
    private val authRepository: AuthRepository,
    savedStateHandle: SavedStateHandle // To get resourceId from navigation arguments
) : ViewModel() {

    private val _uiState = MutableStateFlow(AddEditResourceUiState())
    val uiState: StateFlow<AddEditResourceUiState> = _uiState.asStateFlow()

    private val currentUserId: String? = authRepository.getCurrentUserId()
    private var originalResourceItem: ResourceItem? = null // To compare for changes if editing

    companion object {
        private const val TAG = "AddEditResourceVM"
        const val NAV_ARG_RESOURCE_ID = "resourceId" // Matches argument name in AppNavGraph
    }

    init {
        val resourceId: String? = savedStateHandle[NAV_ARG_RESOURCE_ID]
        if (resourceId != null) {
            _uiState.value = _uiState.value.copy(isLoading = true, isEditing = true, resourceId = resourceId)
            loadResourceDetails(resourceId)
        } else {
            _uiState.value = _uiState.value.copy(isEditing = false) // Adding new resource
            // Initialize with currentUserId if needed for new resource creation on backend immediately
        }
    }

    private fun loadResourceDetails(resourceId: String) {
        if (currentUserId == null) {
            _uiState.value = _uiState.value.copy(isLoading = false, loadError = "User not authenticated.")
            Log.e(TAG, "Cannot load resource details: User not authenticated.")
            return
        }
        viewModelScope.launch {
            try {
                Log.d(TAG, "Loading resource details for ID: $resourceId, UserID: $currentUserId")
                val response = apiService.getResourceById(userId = currentUserId, resourceId = resourceId)
                if (response.isSuccessful) {
                    val resource = response.body()
                    if (resource != null) {
                        originalResourceItem = resource // Store original
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            formState = mapResourceItemToFormState(resource),
                            loadError = null
                        )
                        Log.i(TAG, "Successfully loaded resource: ${resource.name}")
                    } else {
                        _uiState.value = _uiState.value.copy(isLoading = false, loadError = "Resource not found.")
                        Log.e(TAG, "Resource not found for ID: $resourceId")
                    }
                } else {
                    val errorMsg = "Error loading resource: ${response.code()} - ${response.message()}"
                    _uiState.value = _uiState.value.copy(isLoading = false, loadError = errorMsg)
                    Log.e(TAG, errorMsg)
                }
            } catch (e: Exception) {
                Log.e(TAG, "Exception loading resource details", e)
                _uiState.value = _uiState.value.copy(isLoading = false, loadError = "Failed to load resource: ${e.localizedMessage}")
            }
        }
    }

    private fun mapResourceItemToFormState(resource: ResourceItem): ResourceFormState {
        return ResourceFormState(
            name = resource.name,
            selectedType = resource.resourceTypeEnum, // Uses transient property
            customTypeName = if (resource.resourceTypeEnum == ResourceType.OTHER) resource.name else "", // Or a dedicated custom_type_name from backend
            contactPhoneNumber = resource.contact.phoneNumber ?: "",
            contactEmail = resource.contact.emailAddress ?: "",
            contactWebsite = resource.contact.website ?: "",
            contactAddress = resource.contact.address ?: "",
            notes = resource.notes ?: "",
            operatingHours = resource.operatingHours ?: ""
        )
    }

    fun onFormEvent(event: ResourceFormEvent) {
        val currentFormState = _uiState.value.formState
        _uiState.value = _uiState.value.copy(
            formState = when (event) {
                is ResourceFormEvent.NameChanged -> currentFormState.copy(name = event.name)
                is ResourceFormEvent.TypeChanged -> currentFormState.copy(selectedType = event.type)
                is ResourceFormEvent.CustomTypeNameChanged -> currentFormState.copy(customTypeName = event.customTypeName)
                is ResourceFormEvent.PhoneNumberChanged -> currentFormState.copy(contactPhoneNumber = event.phone)
                is ResourceFormEvent.EmailChanged -> currentFormState.copy(contactEmail = event.email)
                is ResourceFormEvent.WebsiteChanged -> currentFormState.copy(contactWebsite = event.website)
                is ResourceFormEvent.AddressChanged -> currentFormState.copy(contactAddress = event.address)
                is ResourceFormEvent.NotesChanged -> currentFormState.copy(notes = event.notes)
                is ResourceFormEvent.OperatingHoursChanged -> currentFormState.copy(operatingHours = event.hours)
            },
            saveResult = null, // Clear previous save result on form change
            generalMessage = null
        )
    }


    fun saveResource() {
        if (currentUserId == null) {
            _uiState.value = _uiState.value.copy(saveResult = SaveResult.Error("User not authenticated."))
            Log.e(TAG, "Cannot save resource: User not authenticated.")
            return
        }

        val form = _uiState.value.formState
        if (form.name.isBlank()) {
            _uiState.value = _uiState.value.copy(saveResult = SaveResult.Error("Resource name cannot be empty."))
            return
        }
        if (form.selectedType == ResourceType.OTHER && form.customTypeName.isBlank()) {
            // In your ResourceItem, typeString is derived from selectedType. For OTHER, the name field often serves as custom type
            // Or, ensure your ResourceItem and backend Resource can store a separate custom_type_name if selectedType is OTHER
        }


        _uiState.value = _uiState.value.copy(isLoading = true)

        // Construct the ResourceItem from formState
        // Backend expects contactDetails as Map<String, String>
        val contactDetailsMap = mutableMapOf<String, String>()
        if (form.contactPhoneNumber.isNotBlank()) contactDetailsMap["phone"] = form.contactPhoneNumber
        if (form.contactEmail.isNotBlank()) contactDetailsMap["email"] = form.contactEmail
        if (form.contactWebsite.isNotBlank()) contactDetailsMap["website"] = form.contactWebsite
        if (form.contactAddress.isNotBlank()) contactDetailsMap["address"] = form.contactAddress
        // Add lat/lng to map if you collect them

        val resourceToSave = ResourceItem(
            id = if (_uiState.value.isEditing) _uiState.value.resourceId ?: "" else "", // Backend generates ID on create, send existing for update
            userId = currentUserId,
            name = form.name,
            typeString = form.selectedType.name, // Send the enum's name string
            operatingHours = form.operatingHours.ifBlank { null },
            notes = form.notes.ifBlank { null },
            contactDetailsMap = contactDetailsMap.ifEmpty { null }
            // Ensure other fields like customTypeName are handled if your backend Resource entity expects it
            // e.g., add a customTypeName field to ResourceItem that maps to backend
        )

        viewModelScope.launch {
            try {
                val response = if (_uiState.value.isEditing) {
                    Log.d(TAG, "Updating resource ID: ${resourceToSave.id}")
                    apiService.updateResource(userId = currentUserId, resourceId = resourceToSave.id, resource = resourceToSave)
                } else {
                    Log.d(TAG, "Adding new resource for user ID: $currentUserId")
                    apiService.addResource(userId = currentUserId, resource = resourceToSave)
                }

                if (response.isSuccessful) {
                    val savedResource = response.body()
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        saveResult = SaveResult.Success,
                        generalMessage = if (_uiState.value.isEditing) "Resource updated successfully!" else "Resource added successfully!",
                        // Optionally update formState if backend returns modified data (e.g., generated ID for new resource)
                        resourceId = savedResource?.id ?: _uiState.value.resourceId // Keep existing or update with new
                    )
                    Log.i(TAG, "Resource saved successfully. ID: ${savedResource?.id}")
                } else {
                    val errorMsg = "Error saving resource: ${response.code()} - ${response.errorBody()?.string()}"
                    _uiState.value = _uiState.value.copy(isLoading = false, saveResult = SaveResult.Error(errorMsg))
                    Log.e(TAG, errorMsg)
                }
            } catch (e: Exception) {
                Log.e(TAG, "Exception saving resource", e)
                _uiState.value = _uiState.value.copy(isLoading = false, saveResult = SaveResult.Error("Failed to save resource: ${e.localizedMessage}"))
            }
        }
    }

    fun clearSaveResult() {
        _uiState.value = _uiState.value.copy(saveResult = null, generalMessage = null)
    }
}

// Sealed class for form events
sealed class ResourceFormEvent {
    data class NameChanged(val name: String) : ResourceFormEvent()
    data class TypeChanged(val type: ResourceType) : ResourceFormEvent()
    data class CustomTypeNameChanged(val customTypeName: String) : ResourceFormEvent() // If you add a dedicated field
    data class PhoneNumberChanged(val phone: String) : ResourceFormEvent()
    data class EmailChanged(val email: String) : ResourceFormEvent()
    data class WebsiteChanged(val website: String) : ResourceFormEvent()
    data class AddressChanged(val address: String) : ResourceFormEvent()
    data class NotesChanged(val notes: String) : ResourceFormEvent()
    data class OperatingHoursChanged(val hours: String) : ResourceFormEvent()
}