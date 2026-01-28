package com.musify.utils

import android.content.Context
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TokenManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val masterKey = MasterKey.Builder(context)
        .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
        .build()
    
    private val sharedPreferences = EncryptedSharedPreferences.create(
        context,
        PREFS_NAME,
        masterKey,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )
    
    fun saveTokens(accessToken: String, refreshToken: String?) {
        android.util.Log.d("TokenManager", "Saving tokens - Access token length: ${accessToken.length}")
        with(sharedPreferences.edit()) {
            putString(KEY_ACCESS_TOKEN, accessToken)
            refreshToken?.let { putString(KEY_REFRESH_TOKEN, it) }
            putLong(KEY_TOKEN_TIMESTAMP, System.currentTimeMillis())
            apply()
        }
        android.util.Log.d("TokenManager", "Tokens saved successfully")
    }
    
    fun getAccessToken(): String? {
        val token = sharedPreferences.getString(KEY_ACCESS_TOKEN, null)
        android.util.Log.d("TokenManager", "Retrieved access token: ${token?.take(20)}...")
        return token
    }
    
    fun getRefreshToken(): String? {
        return sharedPreferences.getString(KEY_REFRESH_TOKEN, null)
    }
    
    fun clearTokens() {
        with(sharedPreferences.edit()) {
            remove(KEY_ACCESS_TOKEN)
            remove(KEY_REFRESH_TOKEN)
            remove(KEY_TOKEN_TIMESTAMP)
            apply()
        }
    }
    
    fun isLoggedIn(): Boolean {
        return getAccessToken() != null
    }
    
    fun getTokenTimestamp(): Long {
        return sharedPreferences.getLong(KEY_TOKEN_TIMESTAMP, 0)
    }
    
    companion object {
        private const val PREFS_NAME = "musify_auth_prefs"
        private const val KEY_ACCESS_TOKEN = "access_token"
        private const val KEY_REFRESH_TOKEN = "refresh_token"
        private const val KEY_TOKEN_TIMESTAMP = "token_timestamp"
    }
}