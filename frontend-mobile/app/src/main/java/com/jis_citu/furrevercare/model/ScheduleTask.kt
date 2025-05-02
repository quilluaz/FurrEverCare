package com.jis_citu.furrevercare.model

import com.google.firebase.Timestamp // Use Firebase Timestamp to match backend

data class ScheduledTask(
    val taskID: String? = null, // Firestore generates ID
    val petID: String? = null, // Set by service/client
    val userID: String? = null, // Set by service/client
    val taskType: TaskType? = null,
    val description: String? = null,
    val scheduledDateTime: Timestamp? = null, // Use Firebase Timestamp
    val status: TaskStatus? = null,
    val recurrenceRule: String? = null, // Keep as String for now
    val notes: String? = null,
    val completedAt: Timestamp? = null // Use Firebase Timestamp
) {
    // Mirroring backend enum cit.edu.furrevercare.entity.ScheduledTask.TaskType
    enum class TaskType {
        MEDICATION, FEEDING, WALK, VET_VISIT, GROOMING, APPOINTMENT, OTHER
    }

    // Mirroring backend enum cit.edu.furrevercare.entity.ScheduledTask.TaskStatus
    enum class TaskStatus {
        PENDING, COMPLETED, SKIPPED, OVERDUE // Match backend casing if needed
    }
}