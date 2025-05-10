package com.jis_citu.furrevercare.model

data class Pet(
    // Match backend fields
    val petID: String = "", // Backend assigns ID, provide default for initial object creation
    val ownerID: String? = null, // Backend sets this, can be nullable initially
    val name: String = "",
    val species: String = "",
    val breed: String = "",
    val age: Int = 0,
    val gender: String = "",
    val weight: Double = 0.0,
    val allergies: List<String>? = null, // Match backend List<String>
    val imageBase64: String? = null, // Match backend Base64 String

)