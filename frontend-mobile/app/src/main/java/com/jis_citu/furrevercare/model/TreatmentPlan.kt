package com.jis_citu.furrevercare.model

import java.time.Instant // Use Java 8+ Instant to match backend

// Mirroring backend cit.edu.furrevercare.entity.TreatmentPlan
data class TreatmentPlan(
    val planID: String? = null, // Firestore generates ID
    val petID: String? = null, // Set by service/client
    val userID: String? = null, // Set by service/client
    val name: String? = null,
    val description: String? = null,
    val startDate: Instant? = null, // Use java.time.Instant
    val endDate: Instant? = null, // Use java.time.Instant (optional)
    val goal: String? = null,
    val status: PlanStatus? = null, // Default to ACTIVE on backend if null
    val progressPercentage: Int? = null,
    val notes: String? = null
) {
    // Mirroring backend enum cit.edu.furrevercare.entity.TreatmentPlan.PlanStatus
    enum class PlanStatus {
        ACTIVE, COMPLETED, CANCELLED
    }
}