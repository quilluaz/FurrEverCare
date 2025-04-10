package com.jis_citu.furrevercare.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.jis_citu.furrevercare.ui.auth.LoginScreen
import com.jis_citu.furrevercare.ui.auth.RegisterScreen
import com.jis_citu.furrevercare.ui.auth.VerificationSuccessScreen
import com.jis_citu.furrevercare.ui.auth.WelcomeAuthScreen
import com.jis_citu.furrevercare.ui.chat.ChatBotScreen
import com.jis_citu.furrevercare.ui.chat.ChatDetailScreen
import com.jis_citu.furrevercare.ui.chat.ChatMessagesScreen
import com.jis_citu.furrevercare.ui.forum.ForumCreatePostScreen
import com.jis_citu.furrevercare.ui.forum.ForumListScreen
import com.jis_citu.furrevercare.ui.forum.ForumPostDetailScreen
import com.jis_citu.furrevercare.ui.home.HomeScreen
import com.jis_citu.furrevercare.ui.home.MainScreen
import com.jis_citu.furrevercare.ui.onboarding.OnboardingScreen
import com.jis_citu.furrevercare.ui.onboarding.SplashScreen
import com.jis_citu.furrevercare.ui.profile.ProfileScreen

@Composable
fun AppNavGraph(
    navController: NavHostController = rememberNavController(),
    startDestination: String = Routes.SPLASH
) {
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        // Onboarding flow
        composable(Routes.SPLASH) {
            SplashScreen(navController = navController)
        }
        composable(Routes.ONBOARDING) {
            OnboardingScreen(navController = navController)
        }

        // Auth flow
        composable(Routes.WELCOME_AUTH) {
            WelcomeAuthScreen(navController = navController)
        }
        composable(Routes.LOGIN) {
            LoginScreen(navController = navController)
        }
        composable(Routes.REGISTER) {
            RegisterScreen(navController = navController)
        }
        composable(Routes.VERIFICATION_SUCCESS) {
            VerificationSuccessScreen(navController = navController)
        }

        // Main flow with bottom navigation
        composable(Routes.MAIN) {
            MainScreen(navController = navController)
        }

        // Home
        composable(Routes.HOME) {
            HomeScreen(navController = navController)
        }

        // Chat
        composable(Routes.CHAT_BOT) {
            ChatBotScreen(navController = navController)
        }
        composable(Routes.CHAT_MESSAGES) {
            ChatMessagesScreen(navController = navController)
        }
        composable(
            route = "${Routes.CHAT_DETAIL}/{chatId}",
            arguments = listOf(navArgument("chatId") { type = NavType.StringType })
        ) {
            ChatDetailScreen(
                navController = navController,
                chatId = it.arguments?.getString("chatId") ?: ""
            )
        }

        // Forum
        composable(Routes.FORUM_LIST) {
            ForumListScreen(navController = navController)
        }
        composable(
            route = "${Routes.FORUM_POST_DETAIL}/{postId}",
            arguments = listOf(navArgument("postId") { type = NavType.StringType })
        ) {
            ForumPostDetailScreen(
                navController = navController,
                postId = it.arguments?.getString("postId") ?: ""
            )
        }
        composable(Routes.FORUM_CREATE_POST) {
            ForumCreatePostScreen(navController = navController)
        }

        // Profile
        composable(Routes.PROFILE) {
            ProfileScreen(navController = navController)
        }
    }
}
