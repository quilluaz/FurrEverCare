package com.jis_citu.furrevercare.network

import com.jis_citu.furrevercare.model.EmergencyProfile
import com.jis_citu.furrevercare.model.MedicalRecord
import com.jis_citu.furrevercare.model.Pet
import com.jis_citu.furrevercare.model.ScheduledTask
import com.jis_citu.furrevercare.model.TreatmentPlan
import com.jis_citu.furrevercare.model.User
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.*

interface ApiService {

    // --- User Endpoints ---
    @GET("api/users/{userId}")
    suspend fun getUser(@Path("userId") userId: String): Response<User>

    @POST("api/users")
    suspend fun createUserProfile(@Body user: User): Response<String>

    @PUT("api/users/profile")
    suspend fun updateUserProfile(@Body user: User): Response<String>

    // --- Pet Endpoints ---
    @Multipart
    @POST("api/users/{userId}/pets")
    suspend fun addPet(
        @Path("userId") userId: String,
        @Part("pet") pet: RequestBody, // Pet data as JSON RequestBody
        @Part image: MultipartBody.Part?
    ): Response<String> // Backend returns message/ID

    @GET("api/users/{userId}/pets")
    suspend fun getUserPets(@Path("userId") userId: String): Response<List<Pet>>

    @GET("api/users/{userId}/pets/{petId}")
    suspend fun getPet(
        @Path("userId") userId: String,
        @Path("petId") petId: String
    ): Response<Pet>

    @Multipart
    @PUT("api/users/{userId}/pets/{petId}")
    suspend fun updatePet(
        @Path("userId") userId: String,
        @Path("petId") petId: String,
        @Part("pet") pet: RequestBody, // Pet data as JSON RequestBody
        @Part image: MultipartBody.Part?
    ): Response<String> // Backend returns message

    @DELETE("api/users/{userId}/pets/{petId}")
    suspend fun deletePet(
        @Path("userId") userId: String,
        @Path("petId") petId: String
    ): Response<String>

    // --- Emergency Profile Endpoints ---
    @POST("api/users/{userId}/pets/{petId}/emergencyProfile")
    suspend fun addEmergencyProfile(
        @Path("userId") userId: String, @Path("petId") petId: String,
        @Body profile: EmergencyProfile
    ): Response<String>

    @GET("api/users/{userId}/pets/{petId}/emergencyProfile")
    suspend fun getEmergencyProfile(
        @Path("userId") userId: String, @Path("petId") petId: String
    ): Response<EmergencyProfile> // Returns the single profile

    @PUT("api/users/{userId}/pets/{petId}/emergencyProfile")
    suspend fun updateEmergencyProfile(
        @Path("userId") userId: String, @Path("petId") petId: String,
        @Body profile: EmergencyProfile
    ): Response<String>

    @DELETE("api/users/{userId}/pets/{petId}/emergencyProfile")
    suspend fun deleteEmergencyProfile(
        @Path("userId") userId: String, @Path("petId") petId: String
    ): Response<String>

    // --- Scheduled Task Endpoints ---
    @POST("api/users/{userId}/pets/{petId}/scheduledTasks")
    suspend fun addScheduledTask(
        @Path("userId") userId: String, @Path("petId") petId: String,
        @Body task: ScheduledTask
    ): Response<String> // Returns taskID

    @GET("api/users/{userId}/pets/{petId}/scheduledTasks")
    suspend fun getScheduledTasks(
        @Path("userId") userId: String, @Path("petId") petId: String,
        @Query("date") date: String? = null, // Format "YYYY-MM-DD"
        @Query("startDate") startDate: String? = null, // Format "YYYY-MM-DDTHH:mm:ssZ" ISO 8601 UTC
        @Query("endDate") endDate: String? = null // Format "YYYY-MM-DDTHH:mm:ssZ" ISO 8601 UTC
    ): Response<List<ScheduledTask>>

    @GET("api/users/{userId}/pets/{petId}/scheduledTasks/upcoming")
    suspend fun getUpcomingScheduledTasks(
        @Path("userId") userId: String, @Path("petId") petId: String,
        @Query("limit") limit: Int? = 5
    ): Response<List<ScheduledTask>>

    @GET("api/users/{userId}/pets/{petId}/scheduledTasks/{taskId}")
    suspend fun getScheduledTaskById(
        @Path("userId") userId: String, @Path("petId") petId: String, @Path("taskId") taskId: String
    ): Response<ScheduledTask>

    @PUT("api/users/{userId}/pets/{petId}/scheduledTasks/{taskId}")
    suspend fun updateScheduledTask(
        @Path("userId") userId: String, @Path("petId") petId: String, @Path("taskId") taskId: String,
        @Body task: ScheduledTask
    ): Response<String>

    @PATCH("api/users/{userId}/pets/{petId}/scheduledTasks/{taskId}/status")
    suspend fun updateScheduledTaskStatus(
        @Path("userId") userId: String, @Path("petId") petId: String, @Path("taskId") taskId: String,
        @Query("status") status: String // Backend TaskStatus enum name (e.g., "COMPLETED")
    ): Response<String>

    @DELETE("api/users/{userId}/pets/{petId}/scheduledTasks/{taskId}")
    suspend fun deleteScheduledTask(
        @Path("userId") userId: String, @Path("petId") petId: String, @Path("taskId") taskId: String
    ): Response<String>

    // --- Treatment Plan Endpoints ---
    @POST("api/users/{userId}/pets/{petId}/treatmentPlans")
    suspend fun addTreatmentPlan(
        @Path("userId") userId: String, @Path("petId") petId: String,
        @Body plan: TreatmentPlan
    ): Response<String> // Returns planID

    @GET("api/users/{userId}/pets/{petId}/treatmentPlans")
    suspend fun getTreatmentPlans(
        @Path("userId") userId: String, @Path("petId") petId: String,
        @Query("status") status: String? = null // Backend PlanStatus enum name (e.g., "ACTIVE")
    ): Response<List<TreatmentPlan>>

    @GET("api/users/{userId}/pets/{petId}/treatmentPlans/{planId}")
    suspend fun getTreatmentPlanById(
        @Path("userId") userId: String, @Path("petId") petId: String, @Path("planId") planId: String
    ): Response<TreatmentPlan>

    @PUT("api/users/{userId}/pets/{petId}/treatmentPlans/{planId}")
    suspend fun updateTreatmentPlan(
        @Path("userId") userId: String, @Path("petId") petId: String, @Path("planId") planId: String,
        @Body plan: TreatmentPlan
    ): Response<String>

    @PATCH("api/users/{userId}/pets/{petId}/treatmentPlans/{planId}/progress")
    suspend fun updatePlanProgress(
        @Path("userId") userId: String, @Path("petId") petId: String, @Path("planId") planId: String,
        @Body progressPayload: Map<String, Int> // Send as {"progress": value}
    ): Response<String>

    @DELETE("api/users/{userId}/pets/{petId}/treatmentPlans/{planId}")
    suspend fun deleteTreatmentPlan(
        @Path("userId") userId: String, @Path("petId") petId: String, @Path("planId") planId: String
    ): Response<String>

    // --- Medical Record Endpoints --- (Based on MedicalRecordService)
    @POST("api/users/{userId}/pets/{petId}/medicalRecords")
    suspend fun addMedicalRecord(
        @Path("userId") userId: String, @Path("petId") petId: String,
        @Body record: MedicalRecord
    ): Response<String> // Returns recordID

    @GET("api/users/{userId}/pets/{petId}/medicalRecords")
    suspend fun getMedicalRecords(
        @Path("userId") userId: String, @Path("petId") petId: String
    ): Response<List<MedicalRecord>>

    @PUT("api/users/{userId}/pets/{petId}/medicalRecords/{recordId}")
    suspend fun updateMedicalRecord(
        @Path("userId") userId: String, @Path("petId") petId: String, @Path("recordId") recordId: String,
        @Body record: MedicalRecord
    ): Response<String>

    @DELETE("api/users/{userId}/pets/{petId}/medicalRecords/{recordId}")
    suspend fun deleteMedicalRecord(
        @Path("userId") userId: String,
        @Path("petId") petId: String,
        @Path("recordId") recordId: String
    ): Response<String>

}