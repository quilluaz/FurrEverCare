package com.jis_citu.furrevercare.ui.home.viewmodel

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jis_citu.furrevercare.data.AuthRepository
import com.jis_citu.furrevercare.data.TokenManager
import com.jis_citu.furrevercare.model.Pet
import com.jis_citu.furrevercare.model.ScheduledTask
// import com.jis_citu.furrevercare.model.User // User model is used by apiService.getUser
import com.jis_citu.furrevercare.network.ApiService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.OffsetDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException
import java.util.Calendar // For time-based greeting
import java.util.Locale
import javax.inject.Inject

// Add a new field for the dynamic greeting
data class HomeUiState(
    val greeting: String = "Welcome", // For "Good Morning", "Good Afternoon", etc.
    val userName: String = "", // Will be just the first name
    val pets: List<Pet> = emptyList(),
    val reminders: List<DisplayReminder> = emptyList(),
    val isLoadingPets: Boolean = false,
    val isLoadingReminders: Boolean = false,
    val errorMessage: String? = null
)

data class DisplayReminder(
    val id: String,
    val petName: String?,
    val title: String,
    val time: String, // Formatted time string
    val originalDateTime: LocalDateTime, // For sorting
    val taskType: ScheduledTask.TaskType
)

@RequiresApi(Build.VERSION_CODES.O)
@HiltViewModel
class HomeViewModel @Inject constructor(
    private val apiService: ApiService,
    private val tokenManager: TokenManager,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    private val displayDateTimeFormatter = DateTimeFormatter.ofPattern("MMM dd, hh:mm a", Locale.getDefault())
    private val isoOffsetDateTimeFormatter = DateTimeFormatter.ISO_OFFSET_DATE_TIME

    init {
        refreshData()
    }

    private fun getDynamicGreeting(): String {
        val calendar = Calendar.getInstance()
        return when (calendar.get(Calendar.HOUR_OF_DAY)) {
            in 0..11 -> "Good Morning,"
            in 12..17 -> "Good Afternoon,"
            else -> "Good Evening,"
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun refreshData() {
        viewModelScope.launch {
            // Update greeting when refreshing data
            _uiState.update {
                it.copy(
                    isLoadingPets = true,
                    isLoadingReminders = true,
                    errorMessage = null,
                    greeting = getDynamicGreeting() // Set dynamic greeting
                )
            }
            val currentUserId = tokenManager.getUserId() ?: authRepository.getCurrentUserId()

            if (currentUserId == null) {
                _uiState.update { it.copy(errorMessage = "User not logged in. Please re-login.", isLoadingPets = false, isLoadingReminders = false) }
                Log.e("HomeViewModel", "User ID is null. Cannot load data.")
                return@launch
            }

            launch { fetchUserName(currentUserId) } // Fetch user name in parallel
            fetchUserPetsAndTheirReminders(currentUserId)
        }
    }

    private suspend fun fetchUserName(userId: String) {
        try {
            val userResponse = apiService.getUser(userId)
            if (userResponse.isSuccessful && userResponse.body() != null) {
                val fullName = userResponse.body()!!.name
                val firstName = fullName.split(" ").firstOrNull()?.takeIf { it.isNotBlank() } ?: "User"
                // Ensure "Google" is not used if it's a placeholder from Firebase/backend
                val finalUserName = if (firstName.equals("Google", ignoreCase = true) && fullName.split(" ").size == 1) "User" else firstName
                _uiState.update { it.copy(userName = finalUserName) }
            } else {
                Log.w("HomeViewModel", "Failed to fetch user name: ${userResponse.code()}, falling back to 'User'")
                _uiState.update { it.copy(userName = "User") } // Fallback
            }
        } catch (e: Exception) {
            Log.w("HomeViewModel", "Error fetching user name", e)
            _uiState.update { it.copy(userName = "User") } // Fallback
        }
    }


    @RequiresApi(Build.VERSION_CODES.O)
    private suspend fun fetchUserPetsAndTheirReminders(userId: String) {
        try {
            val petsResponse = apiService.getUserPets(userId)
            if (petsResponse.isSuccessful && petsResponse.body() != null) {
                val fetchedPets = petsResponse.body()!!
                _uiState.update { it.copy(pets = fetchedPets, isLoadingPets = false) }
                fetchUpcomingRemindersForAllPets(userId, fetchedPets)
            } else {
                val errorMsg = "Failed to load pets (${petsResponse.code()})"
                Log.e("HomeViewModel", "$errorMsg - ${petsResponse.message()} ")
                _uiState.update { it.copy(errorMessage = combineErrorMessages(it.errorMessage, errorMsg), isLoadingPets = false, isLoadingReminders = false) }
            }
        } catch (e: Exception) {
            Log.e("HomeViewModel", "Error fetching pets", e)
            _uiState.update { it.copy(errorMessage = combineErrorMessages(it.errorMessage, "Error loading pets."), isLoadingPets = false, isLoadingReminders = false) }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private suspend fun fetchUpcomingRemindersForAllPets(userId: String, pets: List<Pet>) {
        if (pets.isEmpty()) {
            _uiState.update { it.copy(isLoadingReminders = false, reminders = emptyList()) }
            return
        }

        val reminderDeferreds: List<Deferred<List<DisplayReminder>>> = pets.map { pet ->
            viewModelScope.async {
                try {
                    val response = apiService.getUpcomingScheduledTasks(userId, pet.petID, limit = 3)
                    if (response.isSuccessful && response.body() != null) {
                        response.body()!!.mapNotNull { task ->
                            try {
                                val offsetDateTime = OffsetDateTime.parse(task.scheduledDateTimeString, isoOffsetDateTimeFormatter)
                                val localDateTime = offsetDateTime.atZoneSameInstant(ZoneId.systemDefault()).toLocalDateTime()
                                val taskTypeEnum = task.getTaskTypeEnum() // Get the enum

                                DisplayReminder(
                                    id = task.taskID,
                                    petName = pet.name,
                                    // Use description, or fallback to formatted task type name
                                    title = task.description?.takeIf { it.isNotBlank() }
                                        ?: taskTypeEnum.name.replace("_", " ")
                                            .lowercase(Locale.getDefault())
                                            .replaceFirstChar {
                                                if (it.isLowerCase()) it.titlecase(
                                                    Locale.getDefault()
                                                ) else it.toString()
                                            },
                                    time = localDateTime.format(displayDateTimeFormatter),
                                    originalDateTime = localDateTime,
                                    taskType = taskTypeEnum // Store the enum
                                )
                            } catch (e: DateTimeParseException) {
                                Log.e("HomeViewModel", "Error parsing date for task ${task.taskID}: '${task.scheduledDateTimeString}'", e)
                                null
                            } catch (e: Exception) {
                                Log.e("HomeViewModel", "Unexpected error processing task ${task.taskID}", e)
                                null
                            }
                        }
                    } else {
                        Log.e("HomeViewModel", "Failed to fetch reminders for pet ${pet.petID}: ${response.code()} - ${response.message()}")
                        emptyList()
                    }
                } catch (e: Exception) {
                    Log.e("HomeViewModel", "Exception fetching reminders for pet ${pet.petID}", e)
                    emptyList<DisplayReminder>()
                }
            }
        }

        val allRemindersNested: List<List<DisplayReminder>> = reminderDeferreds.awaitAll()
        val flattenedSortedReminders = allRemindersNested.flatten()
            .sortedBy { it.originalDateTime }
            .take(5)

        _uiState.update { it.copy(reminders = flattenedSortedReminders, isLoadingReminders = false) }
    }

    private fun combineErrorMessages(existing: String?, new: String): String {
        return if (existing.isNullOrBlank()) new else "$existing\n$new"
    }
}