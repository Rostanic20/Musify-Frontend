package com.musify.data.repository

import com.musify.data.api.MusifyApiService
import com.musify.data.mapper.UserMapper.toDomainModel
import com.musify.data.models.*
import com.musify.domain.entity.AuthResult
import com.musify.domain.entity.User
import com.musify.domain.exception.*
import com.musify.domain.repository.AuthRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implementation of AuthRepository that handles data layer operations
 */
@Singleton
class AuthRepositoryImpl @Inject constructor(
    private val apiService: MusifyApiService
) : AuthRepository {
    
    override suspend fun login(
        email: String, 
        password: String, 
        totpCode: String?
    ): Result<AuthResult> {
        return withContext(Dispatchers.IO) {
            try {
                android.util.Log.d("AuthRepository", "Attempting login with: $email")
                val response = apiService.login(
                    LoginRequest(
                        username = email, // This should be username, not email!
                        password = password,
                        totpCode = totpCode
                    )
                )
                
                android.util.Log.d("AuthRepository", "Login response code: ${response.code()}")
                if (response.isSuccessful) {
                    response.body()?.let {
                        android.util.Log.d("AuthRepository", "Login successful for user: ${it.user.username}")
                        Result.success(it.toDomainModel())
                    } ?: Result.failure(ServerException("Empty response body"))
                } else {
                    val errorBody = response.errorBody()?.string()
                    android.util.Log.e("AuthRepository", "Login failed - Code: ${response.code()}, Body: $errorBody")
                    Result.failure(handleErrorResponse(response.code(), errorBody))
                }
            } catch (e: Exception) {
                Result.failure(handleNetworkException(e))
            }
        }
    }
    
    override suspend fun register(
        email: String,
        phoneNumber: String,
        username: String,
        displayName: String,
        password: String,
        isArtist: Boolean,
        verificationType: String
    ): Result<AuthResult> {
        return withContext(Dispatchers.IO) {
            try {
                val request = RegisterRequest(
                    email = if (verificationType == "email") email else "",
                    phoneNumber = if (verificationType == "sms") phoneNumber else "",
                    username = username,
                    displayName = displayName,
                    password = password,
                    isArtist = isArtist,
                    verificationType = verificationType
                )
                android.util.Log.d("AuthRepository", "Registering user: $username, verificationType: $verificationType, isArtist: $isArtist")

                val response = apiService.register(request)

                if (response.isSuccessful) {
                    response.body()?.let {
                        Result.success(it.toDomainModel())
                    } ?: Result.failure(ServerException("Empty response body"))
                } else {
                    val errorBody = response.errorBody()?.string()
                    android.util.Log.e("AuthRepository", "Registration failed - Code: ${response.code()}, Body: $errorBody")
                    Result.failure(handleErrorResponse(response.code(), errorBody))
                }
            } catch (e: Exception) {
                Result.failure(handleNetworkException(e))
            }
        }
    }
    
    override suspend fun refreshToken(refreshToken: String): Result<AuthResult> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.refreshToken(RefreshTokenRequest(refreshToken))
                
                if (response.isSuccessful) {
                    response.body()?.let {
                        Result.success(it.toDomainModel())
                    } ?: Result.failure(ServerException("Empty response body"))
                } else {
                    Result.failure(SessionExpiredException())
                }
            } catch (e: Exception) {
                Result.failure(handleNetworkException(e))
            }
        }
    }
    
    override suspend fun logout(): Result<Unit> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.logout()
                if (response.isSuccessful) {
                    Result.success(Unit)
                } else {
                    // Even if logout fails on server, consider it successful locally
                    Result.success(Unit)
                }
            } catch (e: Exception) {
                // Logout locally even if network fails
                Result.success(Unit)
            }
        }
    }
    
    override suspend fun getCurrentUser(): Result<User?> {
        return withContext(Dispatchers.IO) {
            try {
                android.util.Log.d("AuthRepository", "Getting current user...")
                val response = apiService.getCurrentUser()
                android.util.Log.d("AuthRepository", "Get current user response code: ${response.code()}")
                android.util.Log.d("AuthRepository", "Get current user response headers: ${response.headers()}")
                
                if (response.isSuccessful) {
                    val userResponse = response.body()
                    android.util.Log.d("AuthRepository", "Raw user response: $userResponse")
                    val user = userResponse?.toDomainModel()
                    android.util.Log.d("AuthRepository", "Current user: ${user?.username}, isArtist: ${user?.isArtist}")
                    Result.success(user)
                } else {
                    val errorBody = response.errorBody()?.string()
                    android.util.Log.e("AuthRepository", "Get current user failed - Code: ${response.code()}, Body: $errorBody")
                    when (response.code()) {
                        401 -> Result.failure(UnauthorizedException())
                        else -> Result.failure(ServerException("Server error occurred"))
                    }
                }
            } catch (e: Exception) {
                android.util.Log.e("AuthRepository", "Get current user exception: ${e.message}", e)
                Result.failure(handleNetworkException(e))
            }
        }
    }
    
    override suspend fun verifyEmail(token: String): Result<Unit> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.verifyEmail(token)
                if (response.isSuccessful) {
                    Result.success(Unit)
                } else {
                    Result.failure(ServerException("Email verification failed"))
                }
            } catch (e: Exception) {
                Result.failure(handleNetworkException(e))
            }
        }
    }
    
    override suspend fun resendVerificationEmail(email: String): Result<Unit> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.resendVerification(mapOf("email" to email))
                if (response.isSuccessful) {
                    Result.success(Unit)
                } else {
                    Result.failure(ServerException("Failed to resend verification email"))
                }
            } catch (e: Exception) {
                Result.failure(handleNetworkException(e))
            }
        }
    }

    override suspend fun verifySMS(code: String, phoneNumber: String): Result<Unit> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.verifySMS(mapOf("code" to code, "phoneNumber" to phoneNumber))
                if (response.isSuccessful) {
                    Result.success(Unit)
                } else {
                    Result.failure(ServerException("SMS verification failed"))
                }
            } catch (e: Exception) {
                Result.failure(handleNetworkException(e))
            }
        }
    }

    override suspend fun resendVerificationSMS(phoneNumber: String): Result<Unit> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.resendVerificationSMS(mapOf("phoneNumber" to phoneNumber))
                if (response.isSuccessful) {
                    Result.success(Unit)
                } else {
                    Result.failure(ServerException("Failed to resend verification SMS"))
                }
            } catch (e: Exception) {
                Result.failure(handleNetworkException(e))
            }
        }
    }
    
    override suspend fun requestPasswordReset(email: String): Result<Unit> {
        // TODO: Implement when API endpoint is available
        return Result.failure(NotImplementedError("Password reset not implemented"))
    }
    
    override suspend fun resetPassword(token: String, newPassword: String): Result<Unit> {
        // TODO: Implement when API endpoint is available
        return Result.failure(NotImplementedError("Password reset not implemented"))
    }
    
    override suspend fun enable2FA(password: String): Result<Pair<String, String>> {
        // TODO: Implement when API endpoint is available
        return Result.failure(NotImplementedError("2FA not implemented"))
    }
    
    override suspend fun disable2FA(totpCode: String): Result<Unit> {
        // TODO: Implement when API endpoint is available
        return Result.failure(NotImplementedError("2FA not implemented"))
    }
    
    override suspend fun verify2FA(totpCode: String): Result<Unit> {
        // TODO: Implement when API endpoint is available
        return Result.failure(NotImplementedError("2FA not implemented"))
    }
    
    private fun handleErrorResponse(code: Int, errorBody: String?): DomainException {
        val errorMessage = try {
            com.google.gson.Gson().fromJson(errorBody, ErrorResponse::class.java).error
        } catch (e: Exception) {
            null
        }
        
        return when (code) {
            400 -> ValidationException(errorMessage ?: "Invalid request")
            401 -> InvalidCredentialsException()
            403 -> {
                // For development, log the error but don't assume it's email verification
                android.util.Log.w("AuthRepository", "403 Forbidden: $errorMessage")
                ValidationException(errorMessage ?: "Access forbidden")
            }
            409 -> when {
                errorMessage?.contains("email", ignoreCase = true) == true -> EmailAlreadyExistsException()
                errorMessage?.contains("username", ignoreCase = true) == true -> UsernameAlreadyExistsException()
                else -> ValidationException(errorMessage ?: "Conflict")
            }
            429 -> ValidationException("Too many attempts. Please try again later.")
            else -> ServerException(errorMessage ?: "Request failed")
        }
    }
    
    private fun handleNetworkException(e: Exception): DomainException {
        return when (e) {
            is java.net.UnknownHostException -> NetworkException("No internet connection")
            is java.net.SocketTimeoutException -> TimeoutException()
            else -> NetworkException(e.message ?: "Network error")
        }
    }
}