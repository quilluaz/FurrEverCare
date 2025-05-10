package com.jis_citu.furrevercare.model.resources

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.IgnoredOnParcel
import kotlinx.parcelize.Parcelize

@Parcelize
data class ResourceItem(
    // Ensure field names match what the backend Resource.java will be serialized to by Jackson/Firestore.
    // If backend uses "resourceID", use @SerializedName("resourceID") if your Kotlin field is different.
    // Assuming backend will send "resourceID" as the ID field from @DocumentId.
    @SerializedName("resourceID") // Match backend Resource.java field name
    val id: String = "", // Default for client-side creation before backend assigns one

    @SerializedName("userID") // Match backend Resource.java field name
    val userId: String = "", // Will be set by ViewModel

    val name: String = "",
    // For 'type', backend sends a String. We'll use ResourceType enum on client.
    // Gson needs a TypeAdapter to convert between String and ResourceType enum.
    // Or, receive as String and convert in ViewModel. Let's receive as String first.
    @SerializedName("type")
    val typeString: String = ResourceType.default().name, // Store as String, map to/from enum

    // Backend uses Map<String, String> for contactDetails.
    // Client can use ResourceContact and convert.
    @SerializedName("contactDetails")
    val contactDetailsMap: Map<String, String>? = null, // Receive as Map

    val notes: String? = null,
    val operatingHours: String? = null
) : Parcelable {
    // Transient property to get ResourceType enum easily
    @IgnoredOnParcel
    val resourceTypeEnum: ResourceType
        get() = try {
            ResourceType.valueOf(typeString.uppercase())
        } catch (e: IllegalArgumentException) {
            ResourceType.OTHER // Fallback
        }

    // Transient property to get ResourceContact object easily
    @IgnoredOnParcel
    val contact: ResourceContact
        get() = ResourceContact(contactDetailsMap)
}