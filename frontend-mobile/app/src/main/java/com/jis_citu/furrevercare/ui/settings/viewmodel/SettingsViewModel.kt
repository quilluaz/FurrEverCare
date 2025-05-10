// In: com/jis_citu/furrevercare/ui/settings/viewmodel/SettingsViewModel.kt
package com.jis_citu.furrevercare.ui.settings.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth // For logout
import com.jis_citu.furrevercare.data.PreferenceManager
import com.jis_citu.furrevercare.navigation.Routes // For navigation event after logout
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject


sealed class SettingsNavigationEvent {
    data class Navigate(val route: String, val popUpToRoute: String? = null, val inclusive: Boolean = false, val launchSingleTop: Boolean = false) : SettingsNavigationEvent()
}

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val preferenceManager: PreferenceManager,
    private val firebaseAuth: FirebaseAuth // Inject FirebaseAuth if you want VM to handle signout
) : ViewModel() {

    val isDarkTheme: StateFlow<Boolean> = preferenceManager.darkThemeFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    fun setDarkTheme(isDark: Boolean) {
        viewModelScope.launch {
            preferenceManager.setDarkTheme(isDark)
        }
    }

    val unitPreference: StateFlow<String> = preferenceManager.unitPreferenceFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "Metric")

    fun setUnitPreference(unit: String) {
        viewModelScope.launch {
            preferenceManager.setUnitPreference(unit)
        }
    }

    fun onPrivacyPolicyClicked() {
        viewModelScope.launch {
            _navigationEvent.emit(SettingsNavigationEvent.Navigate(Routes.PRIVACY_POLICY))
        }
    }

    fun onDataUsageClicked() {
        viewModelScope.launch {
            _navigationEvent.emit(SettingsNavigationEvent.Navigate(Routes.DATA_USAGE))
        }
    }

    private val _navigationEvent = MutableSharedFlow<SettingsNavigationEvent>()
    val navigationEvent: SharedFlow<SettingsNavigationEvent> = _navigationEvent.asSharedFlow()

    fun logoutUser() {
        viewModelScope.launch {
            firebaseAuth.signOut() // Sign out from Firebase
            preferenceManager.clearAuthData() // Clear local tokens/user ID
            _navigationEvent.emit(
                SettingsNavigationEvent.Navigate(
                    route = Routes.WELCOME_AUTH, // Or your main auth/login entry route
                    popUpToRoute = Routes.MAIN, // Example: pop up to main graph route if settings is inside main
                    inclusive = true,
                    launchSingleTop = true
                )
            )
        }
    }
}