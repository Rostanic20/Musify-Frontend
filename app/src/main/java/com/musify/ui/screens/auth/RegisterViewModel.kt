package com.musify.ui.screens.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.musify.domain.usecase.auth.RegisterUseCase
import com.musify.utils.TokenManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class RegisterState(
    val isLoading: Boolean = false,
    val isRegisterSuccessful: Boolean = false,
    val errorMessage: String? = null
)

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val registerUseCase: RegisterUseCase,
    private val tokenManager: TokenManager
) : ViewModel() {
    
    private val _state = MutableStateFlow(RegisterState())
    val state: StateFlow<RegisterState> = _state.asStateFlow()
    
    fun register(
        email: String,
        phoneNumber: String,
        username: String,
        displayName: String,
        password: String,
        confirmPassword: String,
        isArtist: Boolean = false,
        verificationType: String = "email"
    ) {
        viewModelScope.launch {
            _state.value = _state.value.copy(
                isLoading = true,
                errorMessage = null
            )

            registerUseCase(
                email = email,
                phoneNumber = phoneNumber,
                username = username,
                displayName = displayName,
                password = password,
                confirmPassword = confirmPassword,
                isArtist = isArtist,
                verificationType = verificationType
            ).fold(
                onSuccess = { authResult ->
                    android.util.Log.d("RegisterViewModel", "Registration successful, saving tokens")
                    // Save tokens
                    tokenManager.saveTokens(
                        authResult.accessToken,
                        authResult.refreshToken
                    )
                    
                    android.util.Log.d("RegisterViewModel", "Tokens saved, updating state")
                    _state.value = _state.value.copy(
                        isLoading = false,
                        isRegisterSuccessful = true
                    )
                    android.util.Log.d("RegisterViewModel", "State updated: isRegisterSuccessful = true")
                },
                onFailure = { exception ->
                    android.util.Log.e("RegisterViewModel", "Registration error: ${exception::class.simpleName} - ${exception.message}")
                    _state.value = _state.value.copy(
                        isLoading = false,
                        errorMessage = exception.message ?: "Registration failed"
                    )
                }
            )
        }
    }
}