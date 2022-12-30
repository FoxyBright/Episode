package ru.rikmasters.gilty.auth.manager

import ru.rikmasters.gilty.auth.token.TokenStore
import ru.rikmasters.gilty.core.data.repository.Repository

class AuthManager(
    
    override val primarySource: TokenStore
    
): Repository<TokenStore>(primarySource) {

    suspend fun isAuthorized() =
        primarySource.getTokensOrNull() != null
}