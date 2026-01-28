package com.musify.domain.usecase.auth

import com.musify.domain.repository.AuthRepository
import javax.inject.Inject

/**
 * Use case for logging out the user
 */
class LogoutUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(): Result<Unit> {
        return authRepository.logout()
    }
}