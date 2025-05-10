package com.jis_citu.furrevercare.utils // <<< Adjust package name if needed

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import android.util.Log

fun decodeBase64(base64String: String): Bitmap? {
    return try {
        // Ensure input is not empty or just padding before attempting decode
        if (base64String.isBlank() || base64String.length < 4) return null // Basic check

        // Remove potential data URI prefix if present (e.g., "data:image/png;base64,")
        val pureBase64 = if (base64String.contains(",")) {
            base64String.substringAfter(",")
        } else {
            base64String
        }

        // Decode the Base64 string
        val imageBytes = Base64.decode(pureBase64, Base64.DEFAULT)

        // Decode byte array to Bitmap
        BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)

    } catch (e: IllegalArgumentException) {
        Log.e("DecodeBase64", "Failed to decode Base64 string (Invalid Format)", e)
        null
    } catch (e: Exception) {
        Log.e("DecodeBase64", "Unexpected error decoding Base64 string", e)
        null
    }
}
