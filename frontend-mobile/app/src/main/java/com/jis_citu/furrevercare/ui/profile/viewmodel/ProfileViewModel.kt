package com.jis_citu.furrevercare.ui.profile.viewmodel

// import android.graphics.Bitmap // Not needed if user image is disabled
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.jis_citu.furrevercare.data.AuthRepository
import com.jis_citu.furrevercare.data.PreferenceManager
import com.jis_citu.furrevercare.model.User
import com.jis_citu.furrevercare.navigation.Routes
import com.jis_citu.furrevercare.network.ApiService
// import com.jis_citu.furrevercare.utils.decodeBase64 // Not needed if user image is disabled
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ProfileUiState(
    val user: User? = null,
    // val userImage: Bitmap? = null, // Temporarily disable user image
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val isUserLoggedIn: Boolean = true
)

sealed class ProfileNavigationEvent {
    data class Navigate(
        val route: String,
        val popUpToRoute: String? = null,
        val inclusive: Boolean = false,
        val launchSingleTop: Boolean = false
    ) : ProfileNavigationEvent()
}

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val apiService: ApiService,
    private val authRepository: AuthRepository,
    private val firebaseAuth: FirebaseAuth,
    private val preferenceManager: PreferenceManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    private val _navigationEvent = MutableSharedFlow<ProfileNavigationEvent>()
    val navigationEvent: SharedFlow<ProfileNavigationEvent> = _navigationEvent.asSharedFlow()

    private var currentUserId: String? = null

    init {
        loadUserProfile()
    }

    fun loadUserProfile() {
        currentUserId = authRepository.getCurrentUserId()
        if (currentUserId == null) {
            _uiState.update {
                it.copy(
                    isLoading = false,
                    errorMessage = "User not logged in.",
                    isUserLoggedIn = false,
                    user = null
                    // userImage = null // Clear image state
                )
            }
            Log.e("ProfileVM", "User ID is null. Cannot load profile.")
            return
        }

        _uiState.update { it.copy(isLoading = true, errorMessage = null) }
        viewModelScope.launch {
            try {
                Log.d("ProfileVM", "Fetching profile for user ID: $currentUserId")
                val response = apiService.getUser(currentUserId!!)
                if (response.isSuccessful && response.body() != null) {
                    val fetchedUser = response.body()!!
                    // val decodedBitmap = fetchedUser.profileImageBase64?.let { decodeBase64(it) } // Disable image decoding
                    _uiState.update {
                        it.copy(
                            user = fetchedUser,
                            // userImage = decodedBitmap, // Disable image
                            isLoading = false,
                            isUserLoggedIn = true
                        )
                    }
                    Log.d("ProfileVM", "User profile loaded: ${fetchedUser.name}")
                } else {
                    Log.e("ProfileVM", "Error fetching user profile: ${response.code()} - ${response.message()}")
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = "Failed to load profile (Code: ${response.code()})."
                        )
                    }
                }
            } catch (e: Exception) {
                Log.e("ProfileVM", "Exception fetching user profile", e)
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = "Network error: ${e.message}"
                    )
                }
            }
        }
    }

    fun logoutUser() {
        viewModelScope.launch {
            try {
                firebaseAuth.signOut()
                preferenceManager.clearAuthData()
                Log.i("ProfileVM", "User logged out successfully.")
                _navigationEvent.emit(
                    ProfileNavigationEvent.Navigate(
                        route = Routes.WELCOME_AUTH,
                        popUpToRoute = Routes.MAIN,
                        inclusive = true,
                        launchSingleTop = true
                    )
                )
                _uiState.update { ProfileUiState(isUserLoggedIn = false, user = null /*, userImage = null */) } // Reset state
            } catch (e: Exception) {
                Log.e("ProfileVM", "Error during logout", e)
                _uiState.update { it.copy(errorMessage = "Logout failed: ${e.message}") }
            }
        }
    }
}