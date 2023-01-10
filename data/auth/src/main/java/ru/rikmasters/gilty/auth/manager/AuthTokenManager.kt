package ru.rikmasters.gilty.auth.manager

import io.ktor.client.plugins.auth.providers.BearerTokens
import ru.rikmasters.gilty.data.ktor.TokenManager

class AuthTokenManager(
    
    private val authManager: AuthManager
    
): TokenManager {
    
    override suspend fun getTokens(): BearerTokens =
        authManager.getTokens()
    
    override suspend fun refreshTokens(): BearerTokens =
        authManager.refreshTokens()
}