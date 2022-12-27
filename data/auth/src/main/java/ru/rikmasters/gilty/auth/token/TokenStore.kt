package ru.rikmasters.gilty.auth.token

import io.ktor.client.call.body
import io.ktor.client.plugins.auth.providers.BearerTokens
import io.ktor.client.request.get
import io.ktor.client.request.setBody
import ru.rikmasters.gilty.core.data.repository.Repository
import ru.rikmasters.gilty.core.data.source.*
import ru.rikmasters.gilty.core.viewmodel.Strategy
import ru.rikmasters.gilty.data.ktor.KtorSource
import ru.rikmasters.gilty.data.ktor.TokenManager

class TokenStore(
    
    private val webSource: KtorSource,
    
    override val primarySource: DbSource
    
): Repository<DbSource>(primarySource), Source, TokenManager {
    
    companion object {
        const val CLIENT_ID = "97717bd0-9e33-49e8-aa6f-68c2b54a46be"
        const val CLIENT_SECRET = "nDS818gVflyLgLqFoq5CjE98q8GRYmomWD3umaTI"
    }
    
    suspend fun deleteTokens() = single {
        primarySource.deleteAll<Tokens>()
    }
    
    suspend fun saveTokens(tokens: Tokens) = single {
        deleteTokens()
        primarySource.save(tokens)
    }
    
    override suspend fun getTokens() =
        getTokensOrNull()
            ?: throw IllegalStateException("Нет токенов")
    
    suspend fun getTokensOrNull() =
        primarySource.find<Tokens>()?.bearer()
    
    override suspend fun refreshTokens(): BearerTokens = single(Strategy.JOIN) {
        val tokens = webSource.unauthorizedClient.get("/oauth/token") {
            val request = TokensRequest(
                TokensRequest.GrantType.REFRESH,
                CLIENT_ID, CLIENT_SECRET,
                refreshToken = getTokens().refreshToken
            )
            setBody(request)
        }.body<TokensResponse>().domain()
        
        saveTokens(tokens)
        
        tokens.bearer()
    }
    
    private fun Tokens.bearer() = BearerTokens(
        accessToken, refreshToken
    )
}