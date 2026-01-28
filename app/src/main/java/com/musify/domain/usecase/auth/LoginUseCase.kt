package com.musify.domain.usecase.auth

import com.musify.domain.entity.AuthResult
import com.musify.domain.exception.ValidationException
import com.musify.domain.repository.AuthRepository
import javax.inject.Inject

/**
 * Use case for user login
 * Encapsulates the business logic for authentication
 */
class LoginUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(
        usernameOrEmail: String,
        password: String,
        totpCode: String? = null
    ): Result<AuthResult> {
        // Validate input
        if (usernameOrEmail.isBlank()) {
            return Result.failure(ValidationException("Username cannot be empty"))
        }
        
        // No need to validate format - backend accepts both username and email
        // Username validation would be: alphanumeric, underscore, hyphen, 3-30 chars
        // But we'll let the backend handle validation
        
        if (password.isBlank()) {
            return Result.failure(ValidationException("Password cannot be empty"))
        }
        
        // Perform login
        return authRepository.login(usernameOrEmail, password, totpCode)
    }
}