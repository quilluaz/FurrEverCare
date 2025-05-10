package com.jis_citu.furrevercare.network

import com.jis_citu.furrevercare.data.TokenManager // Inject the interface
import kotlinx.coroutines.runBlocking // For synchronously getting token in interceptor
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthInterceptor @Inject constructor(
    private val tokenManager: TokenManager
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val token = tokenManager.getToken()
        val originalRequest = chain.request()

        if (originalRequest.url.encodedPath.startsWith("/api/auth/") || token == null) {
            return chain.proceed(originalRequest)
        }

        val newRequest = originalRequest.newBuilder()
            .header("Authorization", "Bearer $token")
            .build()
        return chain.proceed(newRequest)
    }
}