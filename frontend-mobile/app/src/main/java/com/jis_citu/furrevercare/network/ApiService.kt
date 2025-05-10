package com.jis_citu.furrevercare.network

import com.jis_citu.furrevercare.model.Alert
import com.jis_citu.furrevercare.model.EmergencyProfile
import com.jis_citu.furrevercare.model.MedicalRecord
import com.jis_citu.furrevercare.model.Pet
import com.jis_citu.furrevercare.model.resources.ResourceItem
import com.jis_citu.furrevercare.model.ScheduledTask
import com.jis_citu.furrevercare.model.TreatmentPlan
import com.jis_citu.furrevercare.model.User
import com.jis_citu.furrevercare.network.dto.BackendAuthResponse
import com.jis_citu.furrevercare.network.dto.FirebaseIdTokenRequest
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.*

interface ApiService {

    //region Authentication Endpoints
    @POST("api/auth/google-auth")
    suspend fun exchangeFirebaseToken(
        @Body request: FirebaseIdTokenRequest
    ): Response<BackendAuthResponse>
    //endregion

    // --- User Endpoints ---
    // ... (existing user endpoints: getUser, createUserProfile, updateUserProfile) ...
    @GET("api/users/{userId}")
    suspend fun getUser(@Path("userId") userId: String): Response<User>

    @POST("api/users")
    suspend fun createUserProfile(@Body user: User): Response<ResponseBody> // Or Response<User> if backend returns it

    @PUT("api/users/profile")
    suspend fun updateUserProfile(@Body user: User): Response<ResponseBody> // Or Response<User>

    // --- Pet Endpoints ---
    // ... (existing pet endpoints: addPet, getUserPets, getPetById, updatePet, deletePet) ...
    @Multipart
    @POST("api/users/{userId}/pets")
    suspend fun addPet(
        @Path("userId") userId: String,
        @Part("pet") pet: RequestBody,
        @Part image: MultipartBody.Part?
    ): Response<ResponseBody>

    @GET("api/users/{userId}/pets")
    suspend fun getUserPets(@Path("userId") userId: String): Response<List<Pet>>

    @GET("api/users/{userId}/pets/{petId}")
    suspend fun getPetById(
        @Path("userId") userId: String,
        @Path("petId") petId: String
    ): Response<Pet>

    @Multipart
    @PUT("api/users/{userId}/pets/{petId}")
    suspend fun updatePet(
        @Path("userId") userId: String,
        @Path("petId") petId: String,
        @Part("pet") pet: RequestBody,
        @Part image: MultipartBody.Part?
    ): Response<ResponseBody>

    @DELETE("api/users/{userId}/pets/{petId}")
    suspend fun deletePet(
        @Path("userId") userId: String,
        @Path("petId") petId: String
    ): Response<ResponseBody>


    // --- Emergency Profile Endpoints ---
    // ... (existing emergency profile endpoints) ...
    @POST("api/users/{userId}/pets/{petId}/emergencyProfile")
    suspend fun addEmergencyProfile(
        @Path("userId") userId: String, @Path("petId") petId: String,
        @Body profile: EmergencyProfile
    ): Response<ResponseBody>

    @GET("api/users/{userId}/pets/{petId}/emergencyProfile")
    suspend fun getEmergencyProfile(
        @Path("userId") userId: String, @Path("petId") petId: String
    ): Response<EmergencyProfile>

    @PUT("api/users/{userId}/pets/{petId}/emergencyProfile")
    suspend fun updateEmergencyProfile(
        @Path("userId") userId: String, @Path("petId") petId: String,
        @Body profile: EmergencyProfile
    ): Response<ResponseBody>

    @DELETE("api/users/{userId}/pets/{petId}/emergencyProfile")
    suspend fun deleteEmergencyProfile(
        @Path("userId") userId: String, @Path("petId") petId: String
    ): Response<ResponseBody>


    // --- Scheduled Task Endpoints ---
    // ... (existing scheduled task endpoints) ...
    @POST("api/users/{userId}/pets/{petId}/scheduledTasks")
    suspend fun addScheduledTask(
        @Path("userId") userId: String, @Path("petId") petId: String,
        @Body task: ScheduledTask
    ): Response<ResponseBody>

    @GET("api/users/{userId}/pets/{petId}/scheduledTasks/{taskId}")
    suspend fun getScheduledTaskById(
        @Path("userId") userId: String, @Path("petId") petId: String, @Path("taskId") taskId: String
    ): Response<ScheduledTask>

    @GET("api/users/{userId}/pets/{petId}/scheduledTasks")
    suspend fun getScheduledTasks(
        @Path("userId") userId: String, @Path("petId") petId: String,
        @Query("date") date: String? = null,
        @Query("startDate") startDate: String? = null,
        @Query("endDate") endDate: String? = null
    ): Response<List<ScheduledTask>>

    @GET("api/users/{userId}/pets/{petId}/scheduledTasks/upcoming")
    suspend fun getUpcomingScheduledTasks(
        @Path("userId") userId: String, @Path("petId") petId: String,
        @Query("limit") limit: Int? = 5
    ): Response<List<ScheduledTask>>

    @PUT("api/users/{userId}/pets/{petId}/scheduledTasks/{taskId}")
    suspend fun updateScheduledTask(
        @Path("userId") userId: String, @Path("petId") petId: String, @Path("taskId") taskId: String,
        @Body task: ScheduledTask
    ): Response<ResponseBody>

    @PATCH("api/users/{userId}/pets/{petId}/scheduledTasks/{taskId}/status")
    suspend fun updateScheduledTaskStatus(
        @Path("userId") userId: String, @Path("petId") petId: String, @Path("taskId") taskId: String,
        @Query("status") status: String
    ): Response<ResponseBody>

    @DELETE("api/users/{userId}/pets/{petId}/scheduledTasks/{taskId}")
    suspend fun deleteScheduledTask(
        @Path("userId") userId: String, @Path("petId") petId: String, @Path("taskId") taskId: String
    ): Response<ResponseBody>


    // --- Treatment Plan Endpoints ---
    // ... (existing treatment plan endpoints) ...
    @POST("api/users/{userId}/pets/{petId}/treatmentPlans")
    suspend fun addTreatmentPlan(
        @Path("userId") userId: String, @Path("petId") petId: String,
        @Body plan: TreatmentPlan
    ): Response<ResponseBody>

    @GET("api/users/{userId}/pets/{petId}/treatmentPlans/{planId}")
    suspend fun getTreatmentPlanById(
        @Path("userId") userId: String, @Path("petId") petId: String, @Path("planId") planId: String
    ): Response<TreatmentPlan>

    @GET("api/users/{userId}/pets/{petId}/treatmentPlans")
    suspend fun getTreatmentPlans(
        @Path("userId") userId: String, @Path("petId") petId: String,
        @Query("status") status: String? = null
    ): Response<List<TreatmentPlan>>

    @PUT("api/users/{userId}/pets/{petId}/treatmentPlans/{planId}")
    suspend fun updateTreatmentPlan(
        @Path("userId") userId: String, @Path("petId") petId: String, @Path("planId") planId: String,
        @Body plan: TreatmentPlan
    ): Response<ResponseBody>

    @PATCH("api/users/{userId}/pets/{petId}/treatmentPlans/{planId}/progress")
    suspend fun updatePlanProgress(
        @Path("userId") userId: String, @Path("petId") petId: String, @Path("planId") planId: String,
        @Body progressPayload: Map<String, Int>
    ): Response<ResponseBody>

    @DELETE("api/users/{userId}/pets/{petId}/treatmentPlans/{planId}")
    suspend fun deleteTreatmentPlan(
        @Path("userId") userId: String, @Path("petId") petId: String, @Path("planId") planId: String
    ): Response<ResponseBody>


    // --- Medical Record Endpoints ---
    // ... (existing medical record endpoints) ...
    @POST("api/users/{userId}/pets/{petId}/medicalRecords")
    suspend fun addMedicalRecord(
        @Path("userId") userId: String, @Path("petId") petId: String,
        @Body record: MedicalRecord
    ): Response<ResponseBody>

    @GET("api/users/{userId}/pets/{petId}/medicalRecords")
    suspend fun getMedicalRecords(
        @Path("userId") userId: String, @Path("petId") petId: String
    ): Response<List<MedicalRecord>>

    @PUT("api/users/{userId}/pets/{petId}/medicalRecords/{recordId}")
    suspend fun updateMedicalRecord(
        @Path("userId") userId: String, @Path("petId") petId: String, @Path("recordId") recordId: String,
        @Body record: MedicalRecord
    ): Response<ResponseBody>

    @DELETE("api/users/{userId}/pets/{petId}/medicalRecords/{recordId}")
    suspend fun deleteMedicalRecord(
        @Path("userId") userId: String,
        @Path("petId") petId: String,
        @Path("recordId") recordId: String
    ): Response<ResponseBody>


    // --- Alert Endpoints ---
    // ... (existing alert endpoints) ...
    @POST("api/users/{userId}/pets/{petId}/alerts")
    suspend fun addAlert(
        @Path("userId") userId: String, @Path("petId") petId: String,
        @Body alert: Alert
    ): Response<ResponseBody>

    @GET("api/users/{userId}/pets/{petId}/alerts")
    suspend fun getAlerts(
        @Path("userId") userId: String, @Path("petId") petId: String,
        @Query("unread") unread: Boolean? = null
    ): Response<List<Alert>>

    @GET("api/users/{userId}/pets/{petId}/alerts/{alertId}")
    suspend fun getAlertById(
        @Path("userId") userId: String, @Path("petId") petId: String, @Path("alertId") alertId: String
    ): Response<Alert>

    @PATCH("api/users/{userId}/pets/{petId}/alerts/{alertId}/read")
    suspend fun markAlertAsRead(
        @Path("userId") userId: String, @Path("petId") petId: String, @Path("alertId") alertId: String
    ): Response<ResponseBody>

    @DELETE("api/users/{userId}/pets/{petId}/alerts/{alertId}")
    suspend fun deleteAlert(
        @Path("userId") userId: String, @Path("petId") petId: String, @Path("alertId") alertId: String
    ): Response<ResponseBody>


    // --- Resource Directory Endpoints ---
    @POST("api/users/{userId}/resources")
    suspend fun addResource(
        @Path("userId") userId: String,
        @Body resource: ResourceItem // Your Android ResourceItem model
    ): Response<ResourceItem> // Backend returns the created ResourceItem with its ID

    @GET("api/users/{userId}/resources")
    suspend fun getAllResources(@Path("userId") userId: String): Response<List<ResourceItem>>

    @GET("api/users/{userId}/resources/{resourceId}")
    suspend fun getResourceById(
        @Path("userId") userId: String,
        @Path("resourceId") resourceId: String
    ): Response<ResourceItem>

    @PUT("api/users/{userId}/resources/{resourceId}")
    suspend fun updateResource(
        @Path("userId") userId: String,
        @Path("resourceId") resourceId: String,
        @Body resource: ResourceItem // Your Android ResourceItem model
    ): Response<ResourceItem> // Backend returns the updated ResourceItem

    @DELETE("api/users/{userId}/resources/{resourceId}")
    suspend fun deleteResource(
        @Path("userId") userId: String,
        @Path("resourceId") resourceId: String
    ): Response<ResponseBody> // Backend returns a success message string
}