package com.jis_citu.furrevercare.model // Or your preferred model/dto package

import com.google.gson.annotations.SerializedName

data class Alert(
    @SerializedName("alertID") val alertID: String,
    @SerializedName("petID") val petID: String?, // Backend entity has this, can be nullable if user-level
    @SerializedName("userID") val userID: String,
    @SerializedName("message") val message: String,
    @SerializedName("alertType") val alertTypeString: String, // Received as String from backend enum
    @SerializedName("createdAt") val createdAtString: String, // EXPECTING ISO 8601 STRING
    @SerializedName("readStatus") val readStatus: Boolean,
    @SerializedName("severity") val severityString: String // Received as String from backend enum
) {
    // Client-side enums for convenience
    enum class AlertType {
        REMINDER, WARNING, INFO, UNKNOWN
    }

    enum class AlertSeverity {
        LOW, MEDIUM, HIGH, UNKNOWN
    }

    fun getAlertTypeEnum(): AlertType {
        return try { AlertType.valueOf(alertTypeString.uppercase()) } catch (e: Exception) { AlertType.UNKNOWN }
    }

    fun getAlertSeverityEnum(): AlertSeverity {
        return try { AlertSeverity.valueOf(severityString.uppercase()) } catch (e: Exception) { AlertSeverity.UNKNOWN }
    }
}