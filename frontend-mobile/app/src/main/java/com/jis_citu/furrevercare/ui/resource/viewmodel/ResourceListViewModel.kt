package com.jis_citu.furrevercare.ui.resource.viewmodel // Or your preferred ViewModel package

import android.util.Log // Using standard Android Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jis_citu.furrevercare.data.AuthRepository // Using your AuthRepository
import com.jis_citu.furrevercare.network.ApiService // Using your ApiService
import com.jis_citu.furrevercare.model.resources.ResourceItem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ResourceListUiState(
    val resources: List<ResourceItem> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val currentUserId: String? = null // Good to have for context or refetching
)

@HiltViewModel
class ResourceListViewModel @Inject constructor(
    private val apiService: ApiService,
    private val authRepository: AuthRepository // Using your AuthRepository class
) : ViewModel() {

    private val _uiState = MutableStateFlow(ResourceListUiState())
    val uiState: StateFlow<ResourceListUiState> = _uiState.asStateFlow()

    companion object {
        private const val TAG = "ResourceListViewModel" // Tag for logging
    }

    init {
        val currentUserId = authRepository.getCurrentUserId()
        if (currentUserId != null) {
            _uiState.value = _uiState.value.copy(currentUserId = currentUserId)
            loadResources()
        } else {
            _uiState.value = ResourceListUiState(
                errorMessage = "User not logged in.",
                isLoading = false
            )
            Log.w(TAG, "User not logged in at init.")
        }
    }

    fun loadResources() {
        val userId = authRepository.getCurrentUserId() // Get userId again, in case it changed or for retry
        if (userId == null) {
            _uiState.value = _uiState.value.copy(
                isLoading = false,
                errorMessage = "User not authenticated. Please log in again."
            )
            Log.e(TAG, "User ID is null. Cannot load resources.")
            return
        }

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
            try {
                // The custom JWT is expected to be added by AuthInterceptor
                Log.d(TAG, "Fetching resources for user ID: $userId")
                val response = apiService.getAllResources(userId = userId) // Correct function name

                if (response.isSuccessful) {
                    val fetchedResources = response.body() ?: emptyList()
                    _uiState.value = _uiState.value.copy(
                        resources = fetchedResources,
                        isLoading = false
                    )
                    Log.i(TAG, "Successfully fetched ${fetchedResources.size} resources.")
                } else {
                    val errorBody = response.errorBody()?.string() ?: "Unknown error from server"
                    Log.e(TAG, "Error loading resources: ${response.code()} - $errorBody")
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = "Error ${response.code()}: $errorBody"
                    )
                }
            } catch (e: Exception) {
                Log.e(TAG, "Exception when loading resources", e)
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = "Failed to load resources: ${e.localizedMessage ?: "Unknown exception"}"
                )
            }
        }
    }

    fun clearErrorMessage() {
        _uiState.value = _uiState.value.copy(errorMessage = null)
    }
}