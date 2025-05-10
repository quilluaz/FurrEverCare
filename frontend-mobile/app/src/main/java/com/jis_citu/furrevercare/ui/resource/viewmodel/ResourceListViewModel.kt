package com.jis_citu.furrevercare.ui.resource.viewmodel // Or your preferred ViewModel package

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jis_citu.furrevercare.data.api.ApiService
import com.jis_citu.furrevercare.model.resources.ResourceItem
import com.jis_citu.furrevercare.data.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import timber.log.Timber
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
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ResourceListUiState())
    val uiState: StateFlow<ResourceListUiState> = _uiState.asStateFlow()

    init {
        loadResources()
    }

    fun loadResources() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
            try {
                val user = authRepository.getCurrentUser().firstOrNull()
                val token = authRepository.getUserToken().firstOrNull()

                if (user != null && token != null) {
                    _uiState.value = _uiState.value.copy(currentUserId = user.uid)
                    // Assuming your ApiService is set up to automatically include the JWT
                    val fetchedResources = apiService.getUserResources(userId = user.uid)
                    _uiState.value = _uiState.value.copy(
                        resources = fetchedResources,
                        isLoading = false
                    )
                } else {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = "User not authenticated. Please log in again."
                    )
                    Timber.e("User not authenticated or token missing.")
                }
            } catch (e: Exception) {
                Timber.e(e, "Error loading resources")
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = "Failed to load resources: ${e.localizedMessage ?: "Unknown error"}"
                )
            }
        }
    }

    // Optional: A function to clear error messages if you want to dismiss them from UI
    fun clearErrorMessage() {
        _uiState.value = _uiState.value.copy(errorMessage = null)
    }
}