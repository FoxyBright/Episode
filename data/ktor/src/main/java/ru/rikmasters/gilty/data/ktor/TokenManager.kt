package ru.rikmasters.gilty.data.ktor

import io.ktor.client.HttpClient
import io.ktor.client.plugins.auth.providers.BearerTokens

interface TokenManager {
    
    suspend fun getTokens(): BearerTokens
    
    suspend fun refreshTokens(unauthorizedClient: HttpClient): BearerTokens
}