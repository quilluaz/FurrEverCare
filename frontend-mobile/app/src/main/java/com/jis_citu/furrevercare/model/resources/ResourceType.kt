package com.jis_citu.furrevercare.model.resources

enum class ResourceType(val displayName: String) {
    VET("Veterinarian"),
    GROOMER("Groomer"),
    PET_STORE("Pet Store"),
    SHELTER("Shelter/Rescue"),
    TRAINER("Pet Trainer"),
    PET_SITTER("Pet Sitter/Boarding"),
    EMERGENCY_HOSPITAL("Emergency Hospital"),
    OTHER("Other");

    companion object {
        fun fromDisplayName(displayName: String): ResourceType? =
            entries.find { it.displayName == displayName }

        fun default(): ResourceType = VET // Default or first item
    }
}