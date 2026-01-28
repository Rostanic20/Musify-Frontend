package com.musify.data.repository

import com.musify.data.api.MusifyApiService
import com.musify.data.mapper.SongMapper.toDomainModel
import com.musify.domain.entity.Album
import com.musify.domain.entity.Song
import com.musify.domain.exception.NetworkException
import com.musify.domain.exception.ServerException
import com.musify.domain.exception.SongNotFoundException
import com.musify.domain.repository.MusicRepository
import com.musify.domain.repository.SkipResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implementation of MusicRepository
 */
@Singleton
class MusicRepositoryImpl @Inject constructor(
    private val apiService: MusifyApiService
) : MusicRepository {
    
    override suspend fun getSongs(limit: Int, offset: Int): Result<List<Song>> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.getSongs(limit = limit, offset = offset)
                if (response.isSuccessful) {
                    val songs = response.body()?.map { it.toDomainModel() } ?: emptyList()
                    Result.success(songs)
                } else {
                    Result.failure(ServerException("Failed to fetch songs"))
                }
            } catch (e: Exception) {
                Result.failure(NetworkException(e.message ?: "Network error"))
            }
        }
    }
    
    override suspend fun getSongById(songId: Int): Result<Song?> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.getSongDetails(songId)
                if (response.isSuccessful) {
                    Result.success(response.body()?.toDomainModel())
                } else if (response.code() == 404) {
                    Result.failure(SongNotFoundException())
                } else {
                    Result.failure(ServerException("Failed to fetch song details"))
                }
            } catch (e: Exception) {
                Result.failure(NetworkException(e.message ?: "Network error"))
            }
        }
    }
    
    override suspend fun searchSongs(query: String, limit: Int): Result<List<Song>> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.search(
                    mapOf(
                        "query" to query,
                        "type" to "songs",
                        "limit" to limit
                    )
                )
                if (response.isSuccessful) {
                    // Parse search results - this would need proper implementation
                    // based on actual API response structure
                    Result.success(emptyList())
                } else {
                    Result.failure(ServerException("Search failed"))
                }
            } catch (e: Exception) {
                Result.failure(NetworkException(e.message ?: "Network error"))
            }
        }
    }
    
    override suspend fun getRecentlyPlayed(limit: Int): Result<List<Song>> {
        return withContext(Dispatchers.IO) {
            try {
                // For now, using regular songs endpoint as placeholder
                // Should use a specific endpoint for recently played
                val response = apiService.getSongs(limit = limit)
                if (response.isSuccessful) {
                    val songs = response.body()?.map { it.toDomainModel() } ?: emptyList()
                    Result.success(songs)
                } else {
                    Result.failure(ServerException("Failed to fetch recently played"))
                }
            } catch (e: Exception) {
                Result.failure(NetworkException(e.message ?: "Network error"))
            }
        }
    }
    
    override suspend fun getRecommendations(limit: Int): Result<List<Song>> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.getRecommendations(limit = limit)
                if (response.isSuccessful) {
                    val songs = response.body()?.map { it.toDomainModel() } ?: emptyList()
                    Result.success(songs)
                } else {
                    Result.failure(ServerException("Failed to fetch recommendations"))
                }
            } catch (e: Exception) {
                Result.failure(NetworkException(e.message ?: "Network error"))
            }
        }
    }
    
    override suspend fun getPopularAlbums(limit: Int): Result<List<Album>> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.getAlbums(sort = "popular", limit = limit)
                if (response.isSuccessful) {
                    val albums = response.body()?.map { it.toDomainModel() } ?: emptyList()
                    Result.success(albums)
                } else {
                    Result.failure(ServerException("Failed to fetch popular albums"))
                }
            } catch (e: Exception) {
                Result.failure(NetworkException(e.message ?: "Network error"))
            }
        }
    }
    
    override suspend fun getNewReleases(limit: Int): Result<List<Album>> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.getAlbums(sort = "newest", limit = limit)
                if (response.isSuccessful) {
                    val albums = response.body()?.map { it.toDomainModel() } ?: emptyList()
                    Result.success(albums)
                } else {
                    Result.failure(ServerException("Failed to fetch new releases"))
                }
            } catch (e: Exception) {
                Result.failure(NetworkException(e.message ?: "Network error"))
            }
        }
    }
    
    override suspend fun getAlbumById(albumId: Int): Result<Album?> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.getAlbumDetails(albumId)
                if (response.isSuccessful) {
                    Result.success(response.body()?.album?.toDomainModel())
                } else {
                    Result.failure(ServerException("Failed to fetch album"))
                }
            } catch (e: Exception) {
                Result.failure(NetworkException(e.message ?: "Network error"))
            }
        }
    }
    
    override suspend fun getAlbumSongs(albumId: Int): Result<List<Song>> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.getAlbumDetails(albumId)
                if (response.isSuccessful) {
                    val songs = response.body()?.songs?.map { it.toDomainModel() } ?: emptyList()
                    Result.success(songs)
                } else {
                    Result.failure(ServerException("Failed to fetch album songs"))
                }
            } catch (e: Exception) {
                Result.failure(NetworkException(e.message ?: "Network error"))
            }
        }
    }
    
    override suspend fun toggleFavorite(songId: Int): Result<Boolean> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.toggleFavorite(songId)
                if (response.isSuccessful) {
                    val isFavorite = response.body()?.get("isFavorite") ?: false
                    Result.success(isFavorite)
                } else {
                    Result.failure(ServerException("Failed to toggle favorite"))
                }
            } catch (e: Exception) {
                Result.failure(NetworkException(e.message ?: "Network error"))
            }
        }
    }
    
    override suspend fun getFavoriteSongs(): Flow<List<Song>> {
        return flow {
            try {
                // This would need proper implementation with a dedicated endpoint
                emit(emptyList())
            } catch (e: Exception) {
                emit(emptyList())
            }
        }
    }
    
    override suspend fun getStreamUrl(songId: Int, quality: String?): Result<String> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.getStreamUrl(songId, quality)
                if (response.isSuccessful) {
                    val url = response.body()?.get("url")
                    if (url != null) {
                        Result.success(url)
                    } else {
                        Result.failure(ServerException("No stream URL returned"))
                    }
                } else {
                    Result.failure(ServerException("Failed to get stream URL"))
                }
            } catch (e: Exception) {
                Result.failure(NetworkException(e.message ?: "Network error"))
            }
        }
    }
    
    override suspend fun recordPlay(songId: Int, playedDuration: Int): Result<Unit> {
        return withContext(Dispatchers.IO) {
            try {
                // This would need proper implementation
                Result.success(Unit)
            } catch (e: Exception) {
                Result.failure(NetworkException(e.message ?: "Network error"))
            }
        }
    }
    
    override suspend fun recordSkip(songId: Int, playedDuration: Int): Result<SkipResult> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.skipSong(
                    mapOf(
                        "songId" to songId,
                        "playedDuration" to playedDuration
                    )
                )
                if (response.isSuccessful) {
                    val body = response.body()
                    Result.success(
                        SkipResult(
                            allowed = body?.get("allowed") as? Boolean ?: true,
                            skipsRemaining = (body?.get("skipsRemaining") as? Double)?.toInt(),
                            message = body?.get("message") as? String
                        )
                    )
                } else {
                    Result.failure(ServerException("Skip not allowed"))
                }
            } catch (e: Exception) {
                Result.failure(NetworkException(e.message ?: "Network error"))
            }
        }
    }
    
    override suspend fun uploadSong(audioFile: File, artistId: Int, genre: String?): Result<Song> {
        return withContext(Dispatchers.IO) {
            try {
                val requestBody = audioFile.asRequestBody("audio/mpeg".toMediaType())
                val multipartBody = MultipartBody.Part.createFormData(
                    "file",
                    audioFile.name,
                    requestBody
                )
                
                val response = apiService.uploadSong(multipartBody, artistId, genre)
                if (response.isSuccessful) {
                    val uploadResponse = response.body()
                    if (uploadResponse != null) {
                        // Get the newly created song
                        val songResponse = apiService.getSongDetails(uploadResponse.songId)
                        if (songResponse.isSuccessful) {
                            Result.success(songResponse.body()!!.toDomainModel())
                        } else {
                            Result.failure(ServerException("Failed to fetch uploaded song details"))
                        }
                    } else {
                        Result.failure(ServerException("Upload failed: no response"))
                    }
                } else {
                    Result.failure(ServerException("Upload failed: ${response.code()}"))
                }
            } catch (e: Exception) {
                Result.failure(NetworkException(e.message ?: "Upload error"))
            }
        }
    }
    
    override suspend fun uploadCoverArt(imageFile: File, songId: Int): Result<String> {
        return withContext(Dispatchers.IO) {
            try {
                val requestBody = imageFile.asRequestBody("image/*".toMediaType())
                val multipartBody = MultipartBody.Part.createFormData(
                    "file",
                    imageFile.name,
                    requestBody
                )
                
                val response = apiService.uploadSongCover(songId, multipartBody)
                if (response.isSuccessful) {
                    val coverUrl = response.body()?.get("url")
                    if (coverUrl != null) {
                        Result.success(coverUrl)
                    } else {
                        Result.failure(ServerException("No cover URL returned"))
                    }
                } else {
                    Result.failure(ServerException("Cover upload failed: ${response.code()}"))
                }
            } catch (e: Exception) {
                Result.failure(NetworkException(e.message ?: "Cover upload error"))
            }
        }
    }
    
    override suspend fun getArtistSongs(artistId: Int): Result<List<Song>> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.getArtistSongs(artistId)
                if (response.isSuccessful) {
                    val songs = response.body()?.map { it.toDomainModel() } ?: emptyList()
                    Result.success(songs)
                } else {
                    Result.failure(ServerException("Failed to fetch artist songs"))
                }
            } catch (e: Exception) {
                Result.failure(NetworkException(e.message ?: "Network error"))
            }
        }
    }
}