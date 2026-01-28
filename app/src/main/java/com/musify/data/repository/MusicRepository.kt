package com.musify.data.repository

import com.musify.data.api.MusifyApiService
import com.musify.data.models.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MusicRepository @Inject constructor(
    private val apiService: MusifyApiService
) {
    
    suspend fun getRecentlyPlayed(limit: Int = 10): List<Song> {
        return withContext(Dispatchers.IO) {
            try {
                // For now, just get regular songs as a placeholder
                // In a real app, this would call a specific endpoint for recently played
                val response = apiService.getSongs(limit = limit)
                if (response.isSuccessful) {
                    response.body() ?: emptyList()
                } else {
                    emptyList()
                }
            } catch (e: Exception) {
                emptyList()
            }
        }
    }
    
    suspend fun getRecommendations(limit: Int = 20): List<Song> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.getRecommendations(limit = limit)
                if (response.isSuccessful) {
                    response.body() ?: emptyList()
                } else {
                    emptyList()
                }
            } catch (e: Exception) {
                emptyList()
            }
        }
    }
    
    suspend fun getPopularAlbums(limit: Int = 10): List<Album> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.getAlbums(sort = "popular", limit = limit)
                if (response.isSuccessful) {
                    response.body() ?: emptyList()
                } else {
                    emptyList()
                }
            } catch (e: Exception) {
                emptyList()
            }
        }
    }
    
    suspend fun getNewReleases(limit: Int = 10): List<Album> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.getAlbums(sort = "newest", limit = limit)
                if (response.isSuccessful) {
                    response.body() ?: emptyList()
                } else {
                    emptyList()
                }
            } catch (e: Exception) {
                emptyList()
            }
        }
    }
    
    suspend fun getSongs(limit: Int = 50, offset: Int = 0): List<Song> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.getSongs(limit = limit, offset = offset)
                if (response.isSuccessful) {
                    response.body() ?: emptyList()
                } else {
                    emptyList()
                }
            } catch (e: Exception) {
                emptyList()
            }
        }
    }
    
    suspend fun getSongDetails(songId: Int): SongDetails? {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.getSongDetails(songId)
                if (response.isSuccessful) {
                    response.body()
                } else {
                    null
                }
            } catch (e: Exception) {
                null
            }
        }
    }
    
    suspend fun toggleFavorite(songId: Int): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.toggleFavorite(songId)
                response.isSuccessful
            } catch (e: Exception) {
                false
            }
        }
    }
    
    suspend fun getStreamUrl(songId: Int, quality: String? = null): String? {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.getStreamUrl(songId, quality)
                if (response.isSuccessful) {
                    response.body()?.get("url")
                } else {
                    null
                }
            } catch (e: Exception) {
                null
            }
        }
    }
}