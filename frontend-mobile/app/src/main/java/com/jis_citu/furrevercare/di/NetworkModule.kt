package com.jis_citu.furrevercare.di

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.jis_citu.furrevercare.network.ApiService
import com.jis_citu.furrevercare.network.AuthInterceptor // Ensure this is your AuthInterceptor
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit // <<< Import TimeUnit for timeouts
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    // Your backend base URL
    private const val BASE_URL = "https://furrevercare-deploy-13.onrender.com/"

    @Provides
    @Singleton
    fun provideHttpLoggingInterceptor(): HttpLoggingInterceptor {
        return HttpLoggingInterceptor().apply {
            // Use Level.BODY for development to see request/response details
            // Consider Level.HEADERS or Level.NONE for release builds
            level = HttpLoggingInterceptor.Level.BODY
        }
    }

    @Provides
    @Singleton
    fun provideGson(): Gson {
        // You can customize Gson here if needed (e.g., date formats, type adapters)
        return GsonBuilder().create()
    }

    // AuthInterceptor has an @Inject constructor and is @Singleton.
    // Hilt will provide it automatically if its dependencies (like TokenManager) are met.
    // No explicit @Provides function for AuthInterceptor is needed here.

    @Provides
    @Singleton
    fun provideOkHttpClient(
        loggingInterceptor: HttpLoggingInterceptor,
        authInterceptor: AuthInterceptor // Hilt will inject this
    ): OkHttpClient {
        return OkHttpClient.Builder()
            // Set custom timeouts
            .connectTimeout(30, TimeUnit.SECONDS) // Timeout for establishing a connection
            .readTimeout(30, TimeUnit.SECONDS)    // Timeout for reading data from the server
            .writeTimeout(30, TimeUnit.SECONDS)   // Timeout for writing data to the server
            .addInterceptor(authInterceptor)      // Add your custom AuthInterceptor first
            .addInterceptor(loggingInterceptor)   // Add logging interceptor last to see final request
            .build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(
        okHttpClient: OkHttpClient, // Use the OkHttpClient configured above
        gson: Gson                 // Use the Gson instance provided above
    ): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient) // Attach the custom OkHttpClient
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
    }

    @Provides
    @Singleton
    fun provideApiService(retrofit: Retrofit): ApiService {
        return retrofit.create(ApiService::class.java)
    }
}