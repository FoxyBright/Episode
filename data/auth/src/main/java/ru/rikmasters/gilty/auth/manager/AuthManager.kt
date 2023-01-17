package ru.rikmasters.gilty.auth.manager

import io.ktor.client.plugins.auth.providers.BearerTokens
import ru.rikmasters.gilty.auth.login.SendCode
import ru.rikmasters.gilty.auth.saga.AuthSaga
import ru.rikmasters.gilty.auth.token.TokenStore
import ru.rikmasters.gilty.auth.token.TokenWebSource
import ru.rikmasters.gilty.auth.token.Tokens
import ru.rikmasters.gilty.core.data.repository.Repository
import ru.rikmasters.gilty.core.data.source.DbSource
import ru.rikmasters.gilty.core.data.source.deleteAll
import ru.rikmasters.gilty.core.data.source.find
import ru.rikmasters.gilty.core.util.random.randomAlphanumericString
import ru.rikmasters.gilty.core.viewmodel.Strategy

class AuthManager(
    
    private val tokenWebSource: TokenWebSource,
    
    private val dbSource: DbSource,
    
    override val primarySource: TokenStore

): Repository<TokenStore>(primarySource) {
    
    suspend fun isAuthorized() =
        primarySource.getTokensOrNull() != null
    
    private suspend fun login(tokens: Tokens) {
        primarySource.saveTokens(tokens)
    }
    
    suspend fun logout() {
        tokenWebSource.logout()
        primarySource.deleteTokens()
    }
    
    private suspend fun startAuth(): AuthSaga {
        dbSource.deleteAll<AuthSaga>()
        AuthSaga(generateExternalState()).let {
            dbSource.save(it)
            return it
        }
    }
    
    suspend fun getSendCode(): SendCode? =
        getAuth().sendCode
    
    suspend fun getAuth(): AuthSaga =
        dbSource.find() ?: startAuth()
    
    suspend fun updateAuth(update: AuthSaga.() -> AuthSaga) {
        val auth = getAuth()
        dbSource.deleteAll<AuthSaga>()
        return dbSource.save(auth.update())
    }
    
    private fun generateExternalState(): String =
        randomAlphanumericString(32)
    
    suspend fun isExternalLinked(token: String): Boolean {
        val tokens = kotlin.runCatching {
            tokenWebSource.getOauthTokens(
                TokenWebSource.GrantType.External,
                token = token,
            )
        }.getOrNull()
        
        tokens?.let { login(it) }
        
        return tokens != null
    }
    
    suspend fun onOtpAuthentication(code: String?): Boolean {
        val tokens = kotlin.runCatching {
            tokenWebSource.getOauthTokens(
                TokenWebSource.GrantType.Otp,
                phone = getAuth().phone,
                code = code
            )
        }.getOrNull()
        
        tokens?.let { login(it) }
        
        return tokens != null
    }
    
    suspend fun linkExternal(token: String) {
        tokenWebSource.linkExternal(token)
    }
    
    suspend fun getTokens() =
        primarySource.getTokensOrNull()?.bearer()
            ?: throw IllegalStateException("Нет токенов")
    
    suspend fun refreshTokens(): BearerTokens = single(Strategy.JOIN) {
        val tokens = tokenWebSource.getOauthTokens(
            TokenWebSource.GrantType.Refresh,
            refreshToken = getTokens().refreshToken,
            
            ) ?: throw IllegalStateException("Сервер не отдал токены")
        
        primarySource.saveTokens(tokens)
        
        tokens.bearer()
    }
    
    private fun Tokens.bearer() = BearerTokens(
        accessToken, refreshToken
    )
}