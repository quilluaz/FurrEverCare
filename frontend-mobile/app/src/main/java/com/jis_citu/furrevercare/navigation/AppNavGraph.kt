package com.jis_citu.furrevercare.navigation

// --- Verify All Imports ---
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument

// Import screen composables (adjust paths as needed)
import com.jis_citu.furrevercare.ui.auth.*
import com.jis_citu.furrevercare.ui.home.MainScreen
import com.jis_citu.furrevercare.ui.notifications.NotificationsScreen
import com.jis_citu.furrevercare.ui.onboarding.*
import com.jis_citu.furrevercare.ui.pet.*
import com.jis_citu.furrevercare.ui.profile.EditProfileScreen
// import com.jis_citu.furrevercare.ui.profile.ProfileScreen // Often inside MainScreen
// --- Comment out imports for screens not yet implemented ---
// import com.jis_citu.furrevercare.ui.resource.AddEditResourceScreen
// import com.jis_citu.furrevercare.ui.resource.ResourceDetailsScreen
// import com.jis_citu.furrevercare.ui.resource.ResourceListScreen
import com.jis_citu.furrevercare.ui.settings.*
// --- Verify these imports and the existence/composability of the target functions ---
import com.jis_citu.furrevercare.ui.task.AddEditScheduledTaskScreen
//import com.jis_citu.furrevercare.ui.plan.AddEditTreatmentPlanScreen
//import com.jis_citu.furrevercare.ui.record.AddEditMedicalRecordScreen
// --- End Verification ---


@Composable
fun AppNavGraph(
    navController: NavHostController = rememberNavController(),
    // Assuming Routes object is defined elsewhere and imported correctly
    startDestination: String = Routes.SPLASH
) {
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        // Onboarding Flow
        composable(Routes.SPLASH) { SplashScreen(navController) }
        composable(Routes.ONBOARDING) { OnboardingScreen(navController) }

        // Auth Flow
        composable(Routes.WELCOME_AUTH) { WelcomeAuthScreen(navController) }
        composable(Routes.LOGIN) { LoginScreen(navController) }
        composable(Routes.REGISTER) { RegisterScreen(navController) }
        composable(Routes.FORGOT_PASSWORD_EMAIL) { ForgotPasswordEmailScreen(navController) }
        composable(Routes.FORGOT_PASSWORD_VERIFICATION) { ForgotPasswordVerificationScreen(navController) }
        composable(Routes.FORGOT_PASSWORD_NEW_PASSWORD) { ForgotPasswordNewPasswordScreen(navController) }
        composable(Routes.VERIFICATION_SUCCESS) { VerificationSuccessScreen(navController) }

        // Main Application Flow
        composable(Routes.MAIN) { MainScreen(navController) } // Contains Bottom Nav

        // Destinations reachable from MainScreen or other screens
        composable(Routes.NOTIFICATIONS) { NotificationsScreen(navController) }

        // --- Comment out Resource Screen routes as they are not implemented yet ---
        /*
        composable(Routes.RESOURCE_LIST) {
            // *** CHECK ResourceListScreen definition WHEN IMPLEMENTED ***
            // ResourceListScreen(navController = navController) // Example call
             Text("Placeholder: Resource List Screen") // Placeholder
        }
        */

        composable(Routes.EDIT_PROFILE) { EditProfileScreen(navController) }

        composable(
            route = "${Routes.PET_DETAILS}/{petId}",
            arguments = listOf(navArgument("petId") { type = NavType.StringType })
        ) { // ViewModel gets petId via SavedStateHandle
            PetDetailsScreen(navController = navController) // Assumes PetDetailsScreen needs NavController
        }

        composable(Routes.ADD_PET) {
            EditPetScreen(navController = navController)
        }

        composable(
            route = "${Routes.EDIT_PET}/{petId}",
            arguments = listOf(navArgument("petId") { type = NavType.StringType })
        ) { // ViewModel gets petId via SavedStateHandle
            EditPetScreen(navController = navController)
        }

        composable(
            route = "${Routes.EMERGENCY_PROFILE}/{petId}",
            arguments = listOf(navArgument("petId") { type = NavType.StringType })
        ) { // ViewModel gets petId via SavedStateHandle
            EmergencyProfileScreen(navController = navController)
        }

        /*
        // --- Comment out Resource Screen routes as they are not implemented yet ---
        composable(
            route = "${Routes.RESOURCE_DETAILS}/{resourceId}",
            arguments = listOf(navArgument("resourceId") { type = NavType.StringType })
        ) { backStackEntry ->
            val resourceId = backStackEntry.arguments?.getString("resourceId")
            requireNotNull(resourceId) { "Resource ID is required." }
            // *** CHECK ResourceDetailsScreen definition WHEN IMPLEMENTED ***
            // ResourceDetailsScreen(navController = navController, resourceId = resourceId) // Example call
            Text("Placeholder: Resource Details $resourceId") // Placeholder
        }

        composable(
            route = Routes.ADD_EDIT_RESOURCE + "?resourceId={resourceId}",
            arguments = listOf(navArgument("resourceId") { type = NavType.StringType; nullable = true })
        ) { backStackEntry ->
            val resourceId = backStackEntry.arguments?.getString("resourceId") // Can be null for 'add'
            // *** CHECK AddEditResourceScreen definition WHEN IMPLEMENTED ***
            // AddEditResourceScreen(navController = navController, resourceId = resourceId) // Example call
            Text("Placeholder: Add/Edit Resource ${resourceId ?: "(New)"}") // Placeholder
        }
        */

        // Settings Module
        composable(Routes.SETTINGS) { SettingsScreen(navController) }
        composable(Routes.FAQS) { FAQsScreen(navController) }
        composable(Routes.PRIVACY_POLICY) { PrivacyPolicyScreen(navController) }
        composable(Routes.DATA_USAGE) { DataUsageScreen(navController) }
        composable(Routes.CONTACT_US) { ContactUsScreen(navController) }

        // --- Add/Edit Task, Plan, Record ---
        // *** Verify these screens exist and check their parameters ***

//        composable(
//            route = Routes.ADD_EDIT_SCHEDULED_TASK + "/{petId}?taskId={taskId}",
//            arguments = listOf(
//                navArgument("petId") { type = NavType.StringType },
//                navArgument("taskId") { type = NavType.StringType; nullable = true }
//            )
//        ) { backStackEntry ->
//            val petId = backStackEntry.arguments?.getString("petId")
//            requireNotNull(petId) { "Pet ID is required for scheduled tasks." }
//            val taskId = backStackEntry.arguments?.getString("taskId")
//            // *** Ensure AddEditScheduledTaskScreen composable exists and is imported ***
//            AddEditScheduledTaskScreen(navController = navController, petId = petId, taskId = taskId)
//            // Text("Placeholder: Add/Edit Task Screen for pet $petId, task $taskId") // Fallback placeholder
//        }
//
//        composable(
//            route = Routes.ADD_EDIT_TREATMENT_PLAN + "/{petId}?planId={planId}",
//            arguments = listOf(
//                navArgument("petId") { type = NavType.StringType },
//                navArgument("planId") { type = NavType.StringType; nullable = true }
//            )
//        ) { backStackEntry ->
//            val petId = backStackEntry.arguments?.getString("petId")
//            requireNotNull(petId) { "Pet ID is required for treatment plans." }
//            val planId = backStackEntry.arguments?.getString("planId")
//            // *** Ensure AddEditTreatmentPlanScreen composable exists and is imported ***
//            AddEditTreatmentPlanScreen(navController = navController, petId = petId, planId = planId)
//            // Text("Placeholder: Add/Edit Plan Screen for pet $petId, plan $planId") // Fallback placeholder
//        }

//        composable(
//            route = Routes.ADD_EDIT_MEDICAL_RECORD + "/{petId}?recordId={recordId}",
//            arguments = listOf(
//                navArgument("petId") { type = NavType.StringType },
//                navArgument("recordId") { type = NavType.StringType; nullable = true }
//            )
//        ) { backStackEntry ->
//            val petId = backStackEntry.arguments?.getString("petId")
//            requireNotNull(petId) { "Pet ID is required for medical records." }
//            val recordId = backStackEntry.arguments?.getString("recordId")
//            // *** Ensure AddEditMedicalRecordScreen composable exists and is imported ***
//            AddEditMedicalRecordScreen(navController = navController, petId = petId, recordId = recordId)
//            // Text("Placeholder: Add/Edit Record Screen for pet $petId, record $recordId") // Fallback placeholder
//        }

        // Routes for Lists (Placeholders - Implement if needed)
        composable(
            route = "${Routes.MEDICAL_RECORD_LIST}/{petId}",
            arguments = listOf(navArgument("petId") { type = NavType.StringType })
        ) { backStackEntry ->
            val petId = backStackEntry.arguments?.getString("petId")
            requireNotNull(petId)
            // TODO: Implement MedicalRecordListScreen if navigating directly to it
            Text("Placeholder: Medical Record List for Pet $petId")
        }
        // Add similar placeholders/implementations for Schedule List and Treatment Plan List if needed
    }
}

// Ensure the Routes object is defined in a separate Routes.kt file and imported correctly.