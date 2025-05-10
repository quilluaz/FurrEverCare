package com.jis_citu.furrevercare.model.resources

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ResourceContact(
    val phoneNumber: String? = null,
    val emailAddress: String? = null,
    val website: String? = null,
    val address: String? = null,
    val latitude: Double? = null,
    val longitude: Double? = null
) : Parcelable {
    // Secondary constructor for easy creation from map for client-side if needed,
    // though Retrofit/Gson will handle it from backend's Map<String, String>
    constructor(contactDetailsMap: Map<String, String>?) : this(
        phoneNumber = contactDetailsMap?.get("phone"),
        emailAddress = contactDetailsMap?.get("email"),
        website = contactDetailsMap?.get("website"),
        address = contactDetailsMap?.get("address"),
        latitude = contactDetailsMap?.get("latitude")?.toDoubleOrNull(),
        longitude = contactDetailsMap?.get("longitude")?.toDoubleOrNull()
    )

    // Function to convert to Map<String, String> for sending to backend
    fun toMap(): Map<String, String> {
        val map = mutableMapOf<String, String>()
        phoneNumber?.let { map["phone"] = it }
        emailAddress?.let { map["email"] = it }
        website?.let { map["website"] = it }
        address?.let { map["address"] = it }
        latitude?.let { map["latitude"] = it.toString() }
        longitude?.let { map["longitude"] = it.toString() }
        return map
    }
}