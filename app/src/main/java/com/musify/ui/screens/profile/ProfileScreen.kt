package com.musify.ui.screens.profile

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.clickable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    navController: NavController,
    viewModel: ProfileViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Profile") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // User Info Card
            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Profile Picture Placeholder
                    Surface(
                        modifier = Modifier.size(80.dp),
                        shape = MaterialTheme.shapes.large,
                        color = MaterialTheme.colorScheme.primaryContainer
                    ) {
                        Box(
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = null,
                                modifier = Modifier.size(40.dp),
                                tint = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    // User Name and Email
                    Text(
                        text = state.user?.displayName ?: "Loading...",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )
                    
                    Text(
                        text = state.user?.email ?: "",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    
                    if (state.user?.isPremium == true) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Surface(
                            color = MaterialTheme.colorScheme.tertiary,
                            shape = MaterialTheme.shapes.small
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Star,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp),
                                    tint = MaterialTheme.colorScheme.onTertiary
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "Premium",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = MaterialTheme.colorScheme.onTertiary
                                )
                            }
                        }
                    }
                }
            }
            
            // Menu Items
            Card {
                Column {
                    ListItem(
                        headlineContent = { Text("Edit Profile") },
                        leadingContent = {
                            Icon(Icons.Default.Edit, contentDescription = null)
                        },
                        trailingContent = {
                            Icon(Icons.Default.ChevronRight, contentDescription = null)
                        },
                        modifier = Modifier.clickable { /* TODO */ }
                    )
                    
                    Divider()
                    
                    ListItem(
                        headlineContent = { Text("Settings") },
                        leadingContent = {
                            Icon(Icons.Default.Settings, contentDescription = null)
                        },
                        trailingContent = {
                            Icon(Icons.Default.ChevronRight, contentDescription = null)
                        },
                        modifier = Modifier.clickable { /* TODO */ }
                    )
                    
                    Divider()
                    
                    ListItem(
                        headlineContent = { Text("Privacy") },
                        leadingContent = {
                            Icon(Icons.Default.Lock, contentDescription = null)
                        },
                        trailingContent = {
                            Icon(Icons.Default.ChevronRight, contentDescription = null)
                        },
                        modifier = Modifier.clickable { /* TODO */ }
                    )
                }
            }
            
            // Account Actions
            Card {
                Column {
                    ListItem(
                        headlineContent = { Text("Upgrade to Premium") },
                        supportingContent = { Text("Get unlimited skips and downloads") },
                        leadingContent = {
                            Icon(
                                Icons.Default.WorkspacePremium, 
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary
                            )
                        },
                        modifier = Modifier.clickable { /* TODO */ }
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Logout Button
            Button(
                onClick = { 
                    viewModel.logout()
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.error
                )
            ) {
                Icon(
                    imageVector = Icons.Default.Logout,
                    contentDescription = null
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Logout")
            }
            
            // App Version
            Text(
                text = "Musify v1.0.0",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
        }
    }
    
    // Navigate to login when logout is successful
    LaunchedEffect(state.isLoggedOut) {
        if (state.isLoggedOut) {
            navController.navigate("login") {
                popUpTo(0) { inclusive = true }
            }
        }
    }
}