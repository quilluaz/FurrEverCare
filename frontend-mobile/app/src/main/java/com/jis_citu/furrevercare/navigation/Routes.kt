package com.jis_citu.furrevercare.navigation

object Routes {
    // Onboarding
    const val SPLASH = "splash"
    const val ONBOARDING = "onboarding"

    // Auth
    const val WELCOME_AUTH = "welcome_auth"
    const val LOGIN = "login"
    const val REGISTER = "register"
    const val FORGOT_PASSWORD_EMAIL = "forgot_password_email"
    const val FORGOT_PASSWORD_VERIFICATION = "forgot_password_verification"
    const val FORGOT_PASSWORD_NEW_PASSWORD = "forgot_password_new_password"
    const val VERIFICATION_SUCCESS = "verification_success"

    // Main App Structure
    const val MAIN = "main" // Host screen with bottom navigation

    // Core Features (Mobile - Accessible from Main/BottomNav or elsewhere)
    const val HOME = "home"
    const val NOTIFICATIONS = "notifications"
    const val PROFILE = "profile"
    const val PET_LIST = "pet_list"
    const val RESOURCE_LIST = "resource_list" // For Pet Resource Directory feature

    // Detail/Action Screens
    const val PET_DETAILS = "pet_details" // Route base: "pet_details/{petId}"
    const val ADD_PET = "add_pet" // Use combined AddEdit screen?
    const val EDIT_PET = "edit_pet" // Route base: "edit_pet/{petId}", use combined AddEdit screen?
    const val EDIT_PROFILE = "edit_profile"
    const val EMERGENCY_PROFILE = "emergency_profile" // Route base: "emergency_profile/{petId}"
    const val ADD_EDIT_RESOURCE = "add_edit_resource" // Route base: "add_edit_resource?resourceId={resourceId}"
    const val RESOURCE_DETAILS = "resource_details" // Route base: "resource_details/{resourceId}"

    // Tracking Feature Screens (Placeholders)
    const val ADD_EDIT_SCHEDULED_TASK = "add_edit_scheduled_task" // Route base: "add_edit_scheduled_task?petId={petId}&taskId={taskId}"
    const val ADD_EDIT_TREATMENT_PLAN = "add_edit_treatment_plan" // Route base: "add_edit_treatment_plan?petId={petId}&planId={planId}"
    const val ADD_EDIT_MEDICAL_RECORD = "add_edit_medical_record" // Route base: "add_edit_medical_record?petId={petId}&recordId={recordId}"
    const val SCHEDULE_LIST = "schedule_list" // Route base: "schedule_list/{petId}"
    const val TREATMENT_PLAN_LIST = "treatment_plan_list" // Route base: "treatment_plan_list/{petId}"
    const val MEDICAL_RECORD_LIST = "medical_record_list" // Route base: "medical_record_list/{petId}"


    // Settings Module
    const val SETTINGS = "settings"
    const val FAQS = "faqs"
    const val PRIVACY_POLICY = "privacy_policy"
    const val DATA_USAGE = "data_usage"
    const val CONTACT_US = "contact_us"

}