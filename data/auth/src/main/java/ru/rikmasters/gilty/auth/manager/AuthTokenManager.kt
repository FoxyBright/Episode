package ru.rikmasters.gilty.auth.manager

import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.withContext
import ru.rikmasters.gilty.data.ktor.TokenManager

class AuthTokenManager(
    private val authManager: AuthManager,
): TokenManager {
    
    override suspend fun getTokens() = withContext(IO) {
        authManager.getTokens()
    }
    
    override suspend fun refreshTokens() = withContext(IO) {
        authManager.refreshTokens()
    }
}