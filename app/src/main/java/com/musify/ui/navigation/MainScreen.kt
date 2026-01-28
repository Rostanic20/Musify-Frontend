package com.musify.ui.navigation

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.musify.R
import com.musify.domain.entity.User
import com.musify.ui.screens.artist.dashboard.ArtistDashboardScreen
import com.musify.ui.screens.artist.songs.ArtistSongsScreen
import com.musify.ui.screens.artist.upload.UploadSongScreen
import com.musify.ui.screens.user.browse.BrowseScreen
import com.musify.ui.screens.user.home.HomeViewModel
import com.musify.ui.screens.user.library.LibraryScreen
import com.musify.ui.screens.profile.ProfileScreen
import com.musify.ui.screens.user.search.SearchScreen

@Composable
fun MainScreen(
    navController: NavController,
    viewModel: MainViewModel = hiltViewModel()
) {
    android.util.Log.d("MainScreen", "MainScreen composable called")
    val state by viewModel.state.collectAsState()
    val user = state.user
    android.util.Log.d("MainScreen", "User state: $user")
    
    // Handle navigation to login if needed
    LaunchedEffect(state.shouldNavigateToLogin) {
        if (state.shouldNavigateToLogin) {
            navController.navigate(Routes.AUTH) {
                popUpTo(Routes.MAIN) { inclusive = true }
            }
        }
    }
    
    when {
        state.shouldNavigateToLogin -> {
            // Show nothing while navigating
        }
        user != null -> {
            if (user.isArtist) {
                ArtistMainScreen(navController, user)
            } else {
                UserMainScreen(navController, user)
            }
        }
        state.isLoading -> {
            // Loading state
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = androidx.compose.ui.Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }
        else -> {
            // Error state - no user and not loading
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = androidx.compose.ui.Alignment.Center
            ) {
                Column(horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally) {
                    Text("Failed to load user data")
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(onClick = { 
                        navController.navigate(Routes.AUTH) {
                            popUpTo(Routes.MAIN) { inclusive = true }
                        }
                    }) {
                        Text("Go to Login")
                    }
                }
            }
        }
    }
}

@Composable
private fun UserMainScreen(
    navController: NavController,
    user: User
) {
    val bottomNavController = rememberNavController()
    val navBackStackEntry by bottomNavController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination
    
    val bottomNavItems = listOf(
        BottomNavItem("home", stringResource(R.string.home), Icons.Default.Home),
        BottomNavItem("search", stringResource(R.string.search), Icons.Default.Search),
        BottomNavItem("library", stringResource(R.string.library), Icons.Default.LibraryMusic),
        BottomNavItem("profile", stringResource(R.string.profile), Icons.Default.Person)
    )
    
    Scaffold(
        bottomBar = {
            NavigationBar {
                bottomNavItems.forEach { item ->
                    NavigationBarItem(
                        icon = { Icon(item.icon, contentDescription = item.label) },
                        label = { Text(item.label) },
                        selected = currentDestination?.hierarchy?.any { it.route == item.route } == true,
                        onClick = {
                            bottomNavController.navigate(item.route) {
                                popUpTo(bottomNavController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    )
                }
            }
        }
    ) { paddingValues ->
        NavHost(
            navController = bottomNavController,
            startDestination = "home",
            modifier = Modifier.padding(paddingValues)
        ) {
            composable("home") {
                com.musify.ui.screens.user.home.HomeScreen(navController)
            }
            composable("search") { SearchScreen(navController) }
            composable("library") { LibraryScreen(navController) }
            composable("profile") { ProfileScreen(navController) }
        }
    }
}

@Composable
private fun ArtistMainScreen(
    navController: NavController,
    user: User
) {
    val bottomNavController = rememberNavController()
    val navBackStackEntry by bottomNavController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination
    
    val bottomNavItems = listOf(
        BottomNavItem("artist_dashboard", "Dashboard", Icons.Default.Dashboard),
        BottomNavItem("artist_songs", "My Songs", Icons.Default.MusicNote),
        BottomNavItem("artist_analytics", "Analytics", Icons.Default.Analytics),
        BottomNavItem("profile", stringResource(R.string.profile), Icons.Default.Person)
    )
    
    Scaffold(
        bottomBar = {
            NavigationBar {
                bottomNavItems.forEach { item ->
                    NavigationBarItem(
                        icon = { Icon(item.icon, contentDescription = item.label) },
                        label = { Text(item.label) },
                        selected = currentDestination?.hierarchy?.any { it.route == item.route } == true,
                        onClick = {
                            bottomNavController.navigate(item.route) {
                                popUpTo(bottomNavController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    )
                }
            }
        }
    ) { paddingValues ->
        NavHost(
            navController = bottomNavController,
            startDestination = "artist_dashboard",
            modifier = Modifier.padding(paddingValues)
        ) {
            composable("artist_dashboard") { 
                ArtistDashboardScreen(bottomNavController) 
            }
            composable("artist_songs") { 
                ArtistSongsScreen(bottomNavController)
            }
            composable("artist_analytics") { 
                // TODO: Analytics screen
                Box(modifier = Modifier.fillMaxSize()) {
                    Text("Analytics", modifier = Modifier.align(androidx.compose.ui.Alignment.Center))
                }
            }
            composable("artist_upload") { 
                UploadSongScreen(bottomNavController)
            }
            composable("profile") { ProfileScreen(navController) }
        }
    }
}

private data class BottomNavItem(
    val route: String,
    val label: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector
)