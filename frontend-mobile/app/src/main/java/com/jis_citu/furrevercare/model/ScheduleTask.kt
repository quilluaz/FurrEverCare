package com.jis_citu.furrevercare.model

import com.google.gson.annotations.SerializedName

data class ScheduledTask(
    @SerializedName("taskID") val taskID: String,
    @SerializedName("petID") val petID: String,
    @SerializedName("userID") val userID: String,
    @SerializedName("taskType") val taskTypeString: String, // From backend TaskType enum
    @SerializedName("description") val description: String?,
    @SerializedName("scheduledDateTime") val scheduledDateTimeString: String, // EXPECTING ISO 8601 STRING
    @SerializedName("status") val statusString: String,       // From backend TaskStatus enum
    @SerializedName("recurrenceRule") val recurrenceRule: String?,
    @SerializedName("notes") val notes: String?,
    @SerializedName("completedAt") val completedAtString: String?   // EXPECTING ISO 8601 STRING, nullable
) {
    enum class TaskType {
        MEDICATION, FEEDING, WALK, VET_VISIT, GROOMING, APPOINTMENT, OTHER, UNKNOWN
    }

    enum class TaskStatus {
        PENDING, COMPLETED, SKIPPED, OVERDUE, UNKNOWN
    }

    fun getTaskTypeEnum(): TaskType {
        return try { TaskType.valueOf(taskTypeString.uppercase()) }
        catch (e: IllegalArgumentException) { TaskType.UNKNOWN }
    }

    fun getTaskStatusEnum(): TaskStatus {
        return try { TaskStatus.valueOf(statusString.uppercase()) }
        catch (e: IllegalArgumentException) { TaskStatus.UNKNOWN }
    }
}