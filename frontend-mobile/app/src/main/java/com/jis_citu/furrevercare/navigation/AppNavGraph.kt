package com.jis_citu.furrevercare.navigation

import androidx.compose.animation.*
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.jis_citu.furrevercare.ui.auth.*
import com.jis_citu.furrevercare.ui.chat.*
import com.jis_citu.furrevercare.ui.article.ArticleScreen
import com.jis_citu.furrevercare.ui.article.SearchScreen
import com.jis_citu.furrevercare.ui.home.*
import com.jis_citu.furrevercare.ui.notifications.NotificationsScreen
import com.jis_citu.furrevercare.ui.onboarding.*
import com.jis_citu.furrevercare.ui.pet.*
import com.jis_citu.furrevercare.ui.profile.ProfileScreen
import com.jis_citu.furrevercare.ui.settings.*

@Composable
fun AppNavGraph(
    navController: NavHostController = rememberNavController(),
    startDestination: String = Routes.SPLASH
) {
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        // Onboarding
        composable(Routes.SPLASH) {
            SplashScreen(navController = navController)
        }
        composable(Routes.ONBOARDING) {
            OnboardingScreen(navController = navController)
        }

        // Auth
        composable(Routes.WELCOME_AUTH) {
            WelcomeAuthScreen(navController = navController)
        }
        composable(Routes.LOGIN) {
            LoginScreen(navController = navController)
        }
        composable(Routes.REGISTER) {
            RegisterScreen(navController = navController)
        }
        composable(Routes.FORGOT_PASSWORD_EMAIL) {
            ForgotPasswordEmailScreen(navController = navController)
        }
        composable(Routes.FORGOT_PASSWORD_VERIFICATION) {
            ForgotPasswordVerificationScreen(navController = navController)
        }
        composable(Routes.FORGOT_PASSWORD_NEW_PASSWORD) {
            ForgotPasswordNewPasswordScreen(navController = navController)
        }
        composable(Routes.VERIFICATION_SUCCESS) {
            VerificationSuccessScreen(navController = navController)
        }

        // Main
        composable(Routes.MAIN) {
            MainScreen(navController = navController)
        }

        // Home
        composable(Routes.HOME) {
            HomeScreen(navController = navController)
        }

        // Notifications
        composable(Routes.NOTIFICATIONS) {
            NotificationsScreen(navController = navController)
        }

        // Chat
        composable(
            route = "${Routes.CHAT_BOT}?session={session}",
            arguments = listOf(
                navArgument("session") {
                    type = NavType.StringType
                    defaultValue = "default_session"
                    nullable = true
                }
            ),
            enterTransition = { slideInHorizontally(initialOffsetX = { it }) },
            exitTransition = { slideOutHorizontally(targetOffsetX = { -it }) },
            popEnterTransition = { slideInHorizontally(initialOffsetX = { -it }) },
            popExitTransition = { slideOutHorizontally(targetOffsetX = { it }) }
        ) {
            val sessionId = it.arguments?.getString("session") ?: "default_session"
            ChatBotScreen(navController = navController, sessionId = sessionId)
        }
        composable(Routes.CHAT_LIST) {
            ChatListScreen(navController = navController)
        }

        // Articles
        composable(Routes.ARTICLE_SEARCH) {
            SearchScreen(navController = navController)
        }
        composable(
            route = "${Routes.ARTICLE_DETAIL}/{articleId}",
            arguments = listOf(navArgument("articleId") { type = NavType.StringType })
        ) {
            ArticleScreen(
                navController = navController,
                articleId = it.arguments?.getString("articleId") ?: ""
            )
        }

        // Profile
        composable(Routes.PROFILE) {
            ProfileScreen(navController = navController)
        }

        // Settings
        composable(Routes.SETTINGS) {
            SettingsScreen(navController = navController)
        }
        composable(Routes.FAQS) {
            FAQsScreen(navController = navController)
        }
        composable(Routes.PRIVACY_POLICY) {
            PrivacyPolicyScreen(navController = navController)
        }
        composable(Routes.DATA_USAGE) {
            DataUsageScreen(navController = navController)
        }
        composable(Routes.CONTACT_US) {
            ContactUsScreen(navController = navController)
        }

        // Pet
        composable(Routes.PET_LIST) {
            PetListScreen(navController = navController)
        }
        composable(
            route = "${Routes.PET_DETAILS}/{petId}",
            arguments = listOf(navArgument("petId") { type = NavType.StringType })
        ) {
            PetDetailsScreen(
                navController = navController,
                petId = it.arguments?.getString("petId") ?: ""
            )
        }
        composable(Routes.ADD_PET) {
            AddPetScreen(navController = navController)
        }
    }
}
