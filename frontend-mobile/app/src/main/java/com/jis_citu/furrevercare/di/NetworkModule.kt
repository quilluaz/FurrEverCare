package com.jis_citu.furrevercare.di

import com.google.gson.Gson // <<< Import Gson
import com.google.gson.GsonBuilder // <<< Import GsonBuilder
import com.jis_citu.furrevercare.network.ApiService
//import com.jis_citu.furrevercare.network.AuthInterceptor // Keep if you added this
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    // Consider using BuildConfig for base URL
    private const val BASE_URL = "https://furrevercare-deploy-8.onrender.com/"

    @Provides
    @Singleton
    fun provideHttpLoggingInterceptor(): HttpLoggingInterceptor {
        return HttpLoggingInterceptor().apply {
            // Use Level.BODY for development, Level.NONE for release
            level = HttpLoggingInterceptor.Level.BODY
        }
    }

    // --- ADD THIS FUNCTION ---
    @Provides
    @Singleton
    fun provideGson(): Gson {
        // You can customize Gson here if needed (e.g., date formats)
        return GsonBuilder().create()
    }
    // --- END OF ADDED FUNCTION ---

    @Provides
    @Singleton
    fun provideOkHttpClient(
        loggingInterceptor: HttpLoggingInterceptor
        // Uncomment authInterceptor if you added it in the previous step
        // authInterceptor: AuthInterceptor
    ): OkHttpClient {
        return OkHttpClient.Builder()
            // Add interceptors if needed
            // .addInterceptor(authInterceptor) // Add Auth interceptor first
            .addInterceptor(loggingInterceptor)  // Add Logging interceptor last
            .build()
    }

    // --- UPDATE THIS FUNCTION ---
    @Provides
    @Singleton
    fun provideRetrofit(
        okHttpClient: OkHttpClient,
        gson: Gson // <<< Add Gson parameter
    ): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            // Use the injected Gson instance for the factory
            .addConverterFactory(GsonConverterFactory.create(gson)) // <<< Use provided Gson
            .build()
    }

    @Provides
    @Singleton
    fun provideApiService(retrofit: Retrofit): ApiService {
        return retrofit.create(ApiService::class.java)
    }
}