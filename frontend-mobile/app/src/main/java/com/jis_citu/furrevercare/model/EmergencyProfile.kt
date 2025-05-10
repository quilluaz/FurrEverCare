package com.jis_citu.furrevercare.model

data class EmergencyProfile(
    val bloodType: String? = null,
    val chronicConditions: List<String>? = null,
    val emergencyContact: String? = null,
    val specialInstructions: String? = null
)