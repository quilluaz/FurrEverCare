package com.jis_citu.furrevercare.model

// Mirroring backend cit.edu.furrevercare.entity.MedicalRecord
data class MedicalRecord(
    val recordID: String? = null, // Firestore generates ID
    val clinicName: String? = null,
    val medications: List<String>? = null,
    val type: String? = null, // E.g., "Checkup", "Vaccination", "Surgery"
    val vetName: String? = null
    // Consider adding a date field if needed (backend entity doesn't have one)
    // val date: Timestamp? = null
)