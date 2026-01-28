package com.musify.data.repository

import com.musify.data.api.MusifyApiService
import com.musify.data.models.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepository @Inject constructor(
    private val apiService: MusifyApiService
) {
    
    suspend fun login(request: LoginRequest): Result<AuthResponse> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.login(request)
                if (response.isSuccessful) {
                    response.body()?.let {
                        Result.success(it)
                    } ?: Result.failure(Exception("Empty response body"))
                } else {
                    // Try to parse error response
                    val errorBody = response.errorBody()?.string()
                    val errorMessage = try {
                        // Parse JSON error if available
                        com.google.gson.Gson().fromJson(errorBody, ErrorResponse::class.java).error
                    } catch (e: Exception) {
                        when (response.code()) {
                            401 -> "Invalid email or password"
                            403 -> "Account not verified"
                            429 -> "Too many login attempts. Please try again later."
                            else -> "Login failed: ${response.message()}"
                        }
                    }
                    Result.failure(Exception(errorMessage))
                }
            } catch (e: Exception) {
                Result.failure(Exception("Network error: ${e.message}"))
            }
        }
    }
    
    suspend fun register(request: RegisterRequest): Result<AuthResponse> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.register(request)
                if (response.isSuccessful) {
                    response.body()?.let {
                        Result.success(it)
                    } ?: Result.failure(Exception("Empty response body"))
                } else {
                    val errorBody = response.errorBody()?.string()
                    val errorMessage = try {
                        com.google.gson.Gson().fromJson(errorBody, ErrorResponse::class.java).error
                    } catch (e: Exception) {
                        when (response.code()) {
                            400 -> "Invalid registration data"
                            409 -> "Email or username already exists"
                            else -> "Registration failed: ${response.message()}"
                        }
                    }
                    Result.failure(Exception(errorMessage))
                }
            } catch (e: Exception) {
                Result.failure(Exception("Network error: ${e.message}"))
            }
        }
    }
    
    suspend fun refreshToken(refreshToken: String): Result<AuthResponse> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.refreshToken(RefreshTokenRequest(refreshToken))
                if (response.isSuccessful) {
                    response.body()?.let {
                        Result.success(it)
                    } ?: Result.failure(Exception("Empty response body"))
                } else {
                    Result.failure(Exception("Token refresh failed"))
                }
            } catch (e: Exception) {
                Result.failure(Exception("Network error: ${e.message}"))
            }
        }
    }
    
    suspend fun logout(): Result<Unit> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.logout()
                if (response.isSuccessful) {
                    Result.success(Unit)
                } else {
                    Result.failure(Exception("Logout failed"))
                }
            } catch (e: Exception) {
                // Logout locally even if network fails
                Result.success(Unit)
            }
        }
    }
    
    suspend fun verifyEmail(token: String): Result<MessageResponse> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.verifyEmail(token)
                if (response.isSuccessful) {
                    response.body()?.let {
                        Result.success(it)
                    } ?: Result.failure(Exception("Empty response body"))
                } else {
                    Result.failure(Exception("Email verification failed"))
                }
            } catch (e: Exception) {
                Result.failure(Exception("Network error: ${e.message}"))
            }
        }
    }
    
    suspend fun resendVerificationEmail(email: String): Result<Unit> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.resendVerification(mapOf("email" to email))
                if (response.isSuccessful) {
                    Result.success(Unit)
                } else {
                    val errorMessage = when (response.code()) {
                        429 -> "Too many requests. Please wait before trying again."
                        404 -> "Email not found"
                        else -> "Failed to resend verification email"
                    }
                    Result.failure(Exception(errorMessage))
                }
            } catch (e: Exception) {
                Result.failure(Exception("Network error: ${e.message}"))
            }
        }
    }
}