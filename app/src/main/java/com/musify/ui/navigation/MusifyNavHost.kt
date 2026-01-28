package com.musify.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.musify.ui.screens.auth.LoginScreen
import com.musify.ui.screens.auth.RegisterScreen
import com.musify.ui.screens.auth.VerificationScreen
import com.musify.ui.navigation.MainScreen
import com.musify.utils.TokenManager
import kotlinx.coroutines.delay
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.navigation.NavType
import androidx.navigation.navArgument

@Composable
fun MusifyNavHost(
    navController: NavHostController,
    tokenManager: TokenManager = hiltViewModel<SplashViewModel>().tokenManager
) {
    NavHost(
        navController = navController,
        startDestination = Routes.SPLASH
    ) {
        // Splash Screen
        composable(Routes.SPLASH) {
            SplashScreen(
                onNavigateToLogin = {
                    navController.navigate(Routes.AUTH) {
                        popUpTo(Routes.SPLASH) { inclusive = true }
                    }
                },
                onNavigateToHome = {
                    navController.navigate(Routes.MAIN) {
                        popUpTo(Routes.SPLASH) { inclusive = true }
                    }
                }
            )
        }
        
        // Auth Flow
        navigation(
            startDestination = Routes.LOGIN,
            route = Routes.AUTH
        ) {
            composable(Routes.LOGIN) {
                LoginScreen(
                    onNavigateToRegister = {
                        navController.navigate(Routes.REGISTER)
                    },
                    onNavigateToHome = {
                        navController.navigate(Routes.MAIN) {
                            popUpTo(Routes.AUTH) { inclusive = true }
                        }
                    }
                )
            }
            
            composable(Routes.REGISTER) {
                RegisterScreen(
                    onNavigateBack = {
                        navController.popBackStack()
                    },
                    onNavigateToEmailVerification = { email ->
                        navController.navigate(Routes.emailVerification(email))
                    }
                )
            }
            
            composable(
                route = Routes.EMAIL_VERIFICATION,
                arguments = listOf(
                    navArgument("email") { type = NavType.StringType }
                )
            ) { backStackEntry ->
                val verificationData = backStackEntry.arguments?.getString("email") ?: "email:"
                VerificationScreen(
                    verificationData = verificationData,
                    onNavigateToLogin = {
                        navController.navigate(Routes.LOGIN) {
                            popUpTo(Routes.EMAIL_VERIFICATION) { inclusive = true }
                        }
                    },
                    onVerificationSuccess = {
                        navController.navigate(Routes.LOGIN) {
                            popUpTo(Routes.EMAIL_VERIFICATION) { inclusive = true }
                        }
                    }
                )
            }
        }
        
        // Main App Flow
        navigation(
            startDestination = Routes.HOME,
            route = Routes.MAIN
        ) {
            composable(Routes.HOME) {
                MainScreen(navController = navController)
            }
            
            // TODO: Add more screens here
            // composable(Routes.BROWSE) { BrowseScreen() }
            // composable(Routes.SEARCH) { SearchScreen() }
            // composable(Routes.LIBRARY) { LibraryScreen() }
            // composable(Routes.PROFILE) { ProfileScreen() }
        }
    }
}

// Temporary Splash Screen
@Composable
private fun SplashScreen(
    onNavigateToLogin: () -> Unit,
    onNavigateToHome: () -> Unit,
    viewModel: SplashViewModel = hiltViewModel()
) {
    LaunchedEffect(Unit) {
        delay(1500) // Show splash for 1.5 seconds
        
        if (viewModel.isLoggedIn()) {
            onNavigateToHome()
        } else {
            onNavigateToLogin()
        }
    }
    
    // You can add a splash screen UI here
    androidx.compose.foundation.layout.Box(
        modifier = androidx.compose.ui.Modifier.fillMaxSize(),
        contentAlignment = androidx.compose.ui.Alignment.Center
    ) {
        androidx.compose.material3.Text(
            text = "Musify",
            style = androidx.compose.material3.MaterialTheme.typography.displayLarge,
            color = androidx.compose.material3.MaterialTheme.colorScheme.primary
        )
    }
}