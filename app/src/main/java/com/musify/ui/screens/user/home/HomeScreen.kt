package com.musify.ui.screens.user.home

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.musify.R
import com.musify.ui.components.SongCard
import com.musify.ui.navigation.Routes

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navController: NavController,
    viewModel: HomeViewModel = hiltViewModel()
) {
    HomeContent(
        onNavigateToSong = { songId ->
            navController.navigate(Routes.songDetail(songId))
        },
        onNavigateToAlbum = { albumId ->
            navController.navigate(Routes.albumDetail(albumId))
        },
        onNavigateToArtist = { artistId ->
            navController.navigate(Routes.artistDetail(artistId))
        },
        onNavigateToPlaylist = { playlistId ->
            navController.navigate(Routes.playlistDetail(playlistId))
        },
        viewModel = viewModel
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeContent(
    onNavigateToSong: (Int) -> Unit,
    onNavigateToAlbum: (Int) -> Unit,
    onNavigateToArtist: (Int) -> Unit,
    onNavigateToPlaylist: (Int) -> Unit,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Welcome back!",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold
                    )
                },
                actions = {
                    IconButton(onClick = { /* TODO: Navigate to notifications */ }) {
                        Icon(
                            imageVector = Icons.Default.Notifications,
                            contentDescription = "Notifications"
                        )
                    }
                    IconButton(onClick = { /* TODO: Navigate to settings */ }) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = "Settings"
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        if (state.isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentPadding = PaddingValues(bottom = 16.dp)
            ) {
                // Welcome Section when no content
                if (state.recentlyPlayed.isEmpty() && 
                    state.recommendations.isEmpty() && 
                    state.popularAlbums.isEmpty()) {
                    item {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.primaryContainer
                            )
                        ) {
                            Column(
                                modifier = Modifier.padding(24.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Icon(
                                    imageVector = Icons.Default.LibraryMusic,
                                    contentDescription = null,
                                    modifier = Modifier.size(48.dp),
                                    tint = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                                Spacer(modifier = Modifier.height(16.dp))
                                Text(
                                    text = "Welcome to Musify!",
                                    style = MaterialTheme.typography.headlineMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "Your music library is empty. Start exploring!",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                                    textAlign = TextAlign.Center
                                )
                                Spacer(modifier = Modifier.height(24.dp))
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    Button(
                                        onClick = { /* TODO: Navigate to search */ },
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = MaterialTheme.colorScheme.primary
                                        )
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Search,
                                            contentDescription = null,
                                            modifier = Modifier.size(18.dp)
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text("Search Music")
                                    }
                                    OutlinedButton(
                                        onClick = { /* TODO: Navigate to upload */ }
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Upload,
                                            contentDescription = null,
                                            modifier = Modifier.size(18.dp)
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text("Upload")
                                    }
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(24.dp))
                    }
                }
                
                // Recently Played Section
                item {
                    HomeSection(
                        title = stringResource(R.string.recently_played),
                        content = {
                            if (state.recentlyPlayed.isEmpty()) {
                                EmptyContentCard(
                                    message = "Start listening to build your history"
                                )
                            } else {
                                LazyRow(
                                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                                    contentPadding = PaddingValues(horizontal = 16.dp)
                                ) {
                                    items(state.recentlyPlayed) { song ->
                                        SongCard(
                                            song = song,
                                            onClick = { onNavigateToSong(song.id) }
                                        )
                                    }
                                }
                            }
                        }
                    )
                }
                
                // Recommendations Section
                item {
                    HomeSection(
                        title = stringResource(R.string.recommended_for_you),
                        content = {
                            if (state.recommendations.isEmpty()) {
                                EmptyContentCard(
                                    message = "We'll recommend music as you listen"
                                )
                            } else {
                                LazyRow(
                                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                                    contentPadding = PaddingValues(horizontal = 16.dp)
                                ) {
                                    items(state.recommendations) { song ->
                                        SongCard(
                                            song = song,
                                            onClick = { onNavigateToSong(song.id) }
                                        )
                                    }
                                }
                            }
                        }
                    )
                }
                
                // Popular Albums Section
                item {
                    HomeSection(
                        title = stringResource(R.string.popular_albums),
                        content = {
                            LazyRow(
                                horizontalArrangement = Arrangement.spacedBy(16.dp),
                                contentPadding = PaddingValues(horizontal = 16.dp)
                            ) {
                                items(state.popularAlbums) { album ->
                                    // TODO: Create AlbumCard component
                                    Card(
                                        onClick = { onNavigateToAlbum(album.id) },
                                        modifier = Modifier.size(150.dp)
                                    ) {
                                        Box(
                                            modifier = Modifier.fillMaxSize(),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(album.title)
                                        }
                                    }
                                }
                            }
                        }
                    )
                }
                
                // New Releases Section
                item {
                    HomeSection(
                        title = stringResource(R.string.new_releases),
                        content = {
                            LazyRow(
                                horizontalArrangement = Arrangement.spacedBy(16.dp),
                                contentPadding = PaddingValues(horizontal = 16.dp)
                            ) {
                                items(state.newReleases) { album ->
                                    Card(
                                        onClick = { onNavigateToAlbum(album.id) },
                                        modifier = Modifier.size(150.dp)
                                    ) {
                                        Box(
                                            modifier = Modifier.fillMaxSize(),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(album.title)
                                        }
                                    }
                                }
                            }
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun HomeSection(
    title: String,
    content: @Composable () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
        )
        content()
        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
private fun EmptyContentCard(
    message: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .height(150.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = Icons.Default.MusicNote,
                    contentDescription = null,
                    modifier = Modifier.size(40.dp),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = message,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f),
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}