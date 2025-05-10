// In network/dto/AuthDtos.kt
package com.jis_citu.furrevercare.network.dto

import com.google.gson.annotations.SerializedName
import com.jis_citu.furrevercare.model.User // <<< YOUR ACTUAL USER MODEL

/**
 * Request body for sending the Firebase ID Token to your backend.
 */
data class FirebaseIdTokenRequest(
    @SerializedName("idToken") // This key must match what your backend AuthController expects
    val idToken: String
)

/**
 * Response body received from your backend after successful token exchange.
 * This structure is based on your backend's `GoogleAuthResponse` class,
 * which includes a token and your full User object.
 */
data class BackendAuthResponse(
    @SerializedName("token") // The custom JWT from your backend
    val token: String,

    @SerializedName("user") // The User object, matching your User.kt and backend entity
    val user: User
)

/**
 * Alternative Response if your backend returns a simpler structure
 * like `AuthResponse(String userId, String token)`.
 * Only use this if your token exchange endpoint returns this simpler form.
 */
// data class SimpleBackendAuthResponse(
//    @SerializedName("token")
//    val token: String,
//    @SerializedName("userId") // Assuming backend key is "userId" for the user's ID
//    val userId: String
// )