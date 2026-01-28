package com.musify.data.api

import com.musify.utils.TokenManager
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject

class AuthInterceptor @Inject constructor(
    private val tokenManager: TokenManager
) : Interceptor {
    
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        
        // Skip auth for login/register endpoints
        val path = originalRequest.url.encodedPath
        if (path.contains("/auth/login") || 
            path.contains("/auth/register") || 
            path.contains("/auth/refresh")) {
            return chain.proceed(originalRequest)
        }
        
        val token = tokenManager.getAccessToken()
        android.util.Log.d("AuthInterceptor", "Path: $path, Token exists: ${token != null}")
        
        return if (token != null) {
            android.util.Log.d("AuthInterceptor", "Adding Authorization header to request")
            val authenticatedRequest = originalRequest.newBuilder()
                .header("Authorization", "Bearer $token")
                .build()
            chain.proceed(authenticatedRequest)
        } else {
            android.util.Log.w("AuthInterceptor", "No token available for authenticated request")
            chain.proceed(originalRequest)
        }
    }
}