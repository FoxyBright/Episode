package ru.rikmasters.gilty.auth.token

import ru.rikmasters.gilty.core.data.source.*

class TokenStore(
    
    private val primarySource: DbSource
    
): Source {
    
    suspend fun deleteTokens() {
        primarySource.deleteAll<Tokens>()
    }
    
    suspend fun saveTokens(tokens: Tokens) {
        deleteTokens()
        primarySource.save(tokens)
    }
    
    suspend fun getTokensOrNull() =
        primarySource.find<Tokens>()
}