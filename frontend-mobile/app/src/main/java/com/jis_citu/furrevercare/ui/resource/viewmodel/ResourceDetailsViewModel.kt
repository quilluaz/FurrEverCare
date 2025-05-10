package com.jis_citu.furrevercare.ui.resource.viewmodel

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jis_citu.furrevercare.data.AuthRepository
import com.jis_citu.furrevercare.model.resources.ResourceItem
import com.jis_citu.furrevercare.network.ApiService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ResourceDetailsUiState(
    val resourceItem: ResourceItem? = null,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val deletionResult: DeletionResult? = null // To signal deletion success or failure
)

sealed class DeletionResult {
    data object Success : DeletionResult()
    data class Error(val message: String) : DeletionResult()
}


@HiltViewModel
class ResourceDetailsViewModel @Inject constructor(
    private val apiService: ApiService,
    private val authRepository: AuthRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _uiState = MutableStateFlow(ResourceDetailsUiState())
    val uiState: StateFlow<ResourceDetailsUiState> = _uiState.asStateFlow()

    private val resourceId: String = savedStateHandle[NAV_ARG_RESOURCE_ID] ?: ""
    private val currentUserId: String? = authRepository.getCurrentUserId()

    companion object {
        private const val TAG = "ResourceDetailsVM"
        const val NAV_ARG_RESOURCE_ID = "resourceId" // Matches arg name in AppNavGraph
    }

    init {
        if (resourceId.isNotBlank() && currentUserId != null) {
            loadResourceDetails()
        } else if (currentUserId == null) {
            _uiState.value = _uiState.value.copy(errorMessage = "User not authenticated.")
            Log.e(TAG, "Cannot load resource details: User not authenticated.")
        } else {
            _uiState.value = _uiState.value.copy(errorMessage = "Resource ID is missing.")
            Log.e(TAG, "Cannot load resource details: Resource ID is blank.")
        }
    }

    fun loadResourceDetails() {
        if (currentUserId == null || resourceId.isBlank()) {
            Log.e(TAG, "loadResourceDetails: Invalid state - UserID: $currentUserId, ResourceID: $resourceId")
            _uiState.value = _uiState.value.copy(isLoading = false, errorMessage = "User or Resource ID missing.")
            return
        }

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null, deletionResult = null)
            try {
                Log.d(TAG, "Fetching details for resource ID: $resourceId, UserID: $currentUserId")
                val response = apiService.getResourceById(userId = currentUserId, resourceId = resourceId)
                if (response.isSuccessful) {
                    _uiState.value = _uiState.value.copy(
                        resourceItem = response.body(),
                        isLoading = false
                    )
                    Log.i(TAG, "Successfully fetched resource details: ${response.body()?.name}")
                } else {
                    val errorMsg = "Error fetching resource details: ${response.code()} - ${response.message()}"
                    _uiState.value = _uiState.value.copy(isLoading = false, errorMessage = errorMsg)
                    Log.e(TAG, errorMsg)
                }
            } catch (e: Exception) {
                Log.e(TAG, "Exception fetching resource details", e)
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = "Failed to fetch resource details: ${e.localizedMessage}"
                )
            }
        }
    }

    fun deleteResource() {
        if (currentUserId == null || resourceId.isBlank()) {
            _uiState.value = _uiState.value.copy(deletionResult = DeletionResult.Error("User or Resource ID missing."))
            Log.e(TAG, "Cannot delete resource: User or Resource ID missing.")
            return
        }

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true) // Indicate loading for deletion
            try {
                Log.d(TAG, "Deleting resource ID: $resourceId, UserID: $currentUserId")
                val response = apiService.deleteResource(userId = currentUserId, resourceId = resourceId)
                if (response.isSuccessful) {
                    _uiState.value = _uiState.value.copy(isLoading = false, deletionResult = DeletionResult.Success)
                    Log.i(TAG, "Resource deleted successfully.")
                } else {
                    val errorMsg = "Error deleting resource: ${response.code()} - ${response.errorBody()?.string()}"
                    _uiState.value = _uiState.value.copy(isLoading = false, deletionResult = DeletionResult.Error(errorMsg))
                    Log.e(TAG, errorMsg)
                }
            } catch (e: Exception) {
                Log.e(TAG, "Exception deleting resource", e)
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    deletionResult = DeletionResult.Error("Failed to delete resource: ${e.localizedMessage}")
                )
            }
        }
    }

    fun clearDeletionResult() {
        _uiState.value = _uiState.value.copy(deletionResult = null)
    }
}