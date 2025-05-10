package com.jis_citu.furrevercare.model

data class User(
    val userID: String = "",
    val name: String = "",
    val email: String = "",
    val phone: String? = null
)

// UserProfileUpdateRequest can be used later if you decide to send only specific fields for update
// data class UserProfileUpdateRequest(
//     val name: String,
//     val phone: String?
// )