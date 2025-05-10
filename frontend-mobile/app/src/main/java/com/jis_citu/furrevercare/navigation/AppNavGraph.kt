package com.jis_citu.furrevercare.navigation

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.jis_citu.furrevercare.ui.auth.*
import com.jis_citu.furrevercare.ui.home.MainScreen
import com.jis_citu.furrevercare.ui.notifications.NotificationsScreen
import com.jis_citu.furrevercare.ui.onboarding.*
import com.jis_citu.furrevercare.ui.pet.*
import com.jis_citu.furrevercare.ui.profile.EditProfileScreen
import com.jis_citu.furrevercare.ui.settings.*
// import com.jis_citu.furrevercare.ui.task.AddEditScheduledTaskScreen // Keep if/when implemented
// import com.jis_citu.furrevercare.ui.plan.AddEditTreatmentPlanScreen // Keep if/when implemented
// import com.jis_citu.furrevercare.ui.record.AddEditMedicalRecordScreen // Keep if/when implemented

@Composable
fun AppNavGraph(
    navController: NavHostController = rememberNavController(),
    startDestination: String = Routes.ONBOARDING
) {
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        // Onboarding
        composable(Routes.ONBOARDING) { OnboardingScreen(navController) }

        // Auth
        composable(Routes.WELCOME_AUTH) { WelcomeAuthScreen(navController) }
        composable(Routes.LOGIN) { LoginScreen(navController) }
        composable(Routes.REGISTER) { RegisterScreen(navController) }
        composable(Routes.FORGOT_PASSWORD_EMAIL) { ForgotPasswordEmailScreen(navController) }
        composable(Routes.FORGOT_PASSWORD_VERIFICATION) { ForgotPasswordVerificationScreen(navController) }
        composable(Routes.FORGOT_PASSWORD_NEW_PASSWORD) { ForgotPasswordNewPasswordScreen(navController) }
        composable(Routes.VERIFICATION_SUCCESS) { VerificationSuccessScreen(navController) }

        // Main App
        composable(Routes.MAIN) { MainScreen(navController) }
        composable(Routes.NOTIFICATIONS) { NotificationsScreen(navController) }

        // Profile
        composable(Routes.EDIT_PROFILE) { EditProfileScreen(navController) }

        // Pet Management
        composable(
            route = "${Routes.PET_DETAILS}/{petId}",
            arguments = listOf(navArgument("petId") { type = NavType.StringType })
        ) {
            PetDetailsScreen(navController = navController)
        }
        composable(Routes.ADD_PET) {
            EditPetScreen(navController = navController)
        }
        composable(
            route = "${Routes.EDIT_PET}/{petId}",
            arguments = listOf(navArgument("petId") { type = NavType.StringType })
        ) {
            EditPetScreen(navController = navController)
        }
        composable(
            route = "${Routes.EMERGENCY_PROFILE}/{petId}",
            arguments = listOf(navArgument("petId") { type = NavType.StringType })
        ) {
            EmergencyProfileScreen(navController = navController)
        }

        // Settings
        composable(Routes.SETTINGS) { SettingsScreen(navController) }
        composable(Routes.FAQS) { FAQsScreen(navController) }
        composable(Routes.PRIVACY_POLICY) { PrivacyPolicyScreen(navController) }
        composable(Routes.DATA_USAGE) { DataUsageScreen(navController) }
        composable(Routes.CONTACT_US) { ContactUsScreen(navController) }

        // Placeholder for Medical Record List (Implement Screen Later)
        composable(
            route = "${Routes.MEDICAL_RECORD_LIST}/{petId}",
            arguments = listOf(navArgument("petId") { type = NavType.StringType })
        ) { backStackEntry ->
            val petId = backStackEntry.arguments?.getString("petId")
            requireNotNull(petId)
            Text("Placeholder: Medical Record List for Pet $petId")
        }

        // --- Pet Resource Directory ---
        composable(
            route = Routes.ADD_EDIT_RESOURCE + "?resourceId={resourceId}",
            arguments = listOf(
                navArgument("resourceId") {
                    type = NavType.StringType
                    nullable = true // null for "add", non-null for "edit"
                }
            )
        ) { backStackEntry ->
            val resourceId = backStackEntry.arguments?.getString("resourceId")
            Text("Placeholder: Add/Edit Resource Screen (ID: $resourceId)")
        }

        composable(
            route = "${Routes.RESOURCE_DETAILS}/{resourceId}",
            arguments = listOf(navArgument("resourceId") { type = NavType.StringType })
        ) { backStackEntry ->
            val resourceId = backStackEntry.arguments?.getString("resourceId")
            requireNotNull(resourceId) { "Resource ID cannot be null for details screen" }
            Text("Placeholder: Resource Details Screen for ID: $resourceId")
        }

    }
}