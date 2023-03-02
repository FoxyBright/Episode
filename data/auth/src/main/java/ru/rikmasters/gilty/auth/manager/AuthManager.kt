package ru.rikmasters.gilty.auth.manager

import io.ktor.client.plugins.auth.providers.BearerTokens
import ru.rikmasters.gilty.auth.login.SendCode
import ru.rikmasters.gilty.auth.saga.AuthSaga
import ru.rikmasters.gilty.auth.token.*
import ru.rikmasters.gilty.auth.token.PushType.FIREBASE
import ru.rikmasters.gilty.auth.token.TokenWebSource.GrantType.External
import ru.rikmasters.gilty.auth.token.TokenWebSource.GrantType.Otp
import ru.rikmasters.gilty.auth.token.TokenWebSource.GrantType.Refresh
import ru.rikmasters.gilty.core.data.repository.Repository
import ru.rikmasters.gilty.core.data.source.DbSource
import ru.rikmasters.gilty.core.data.source.deleteAll
import ru.rikmasters.gilty.core.data.source.find
import ru.rikmasters.gilty.core.util.random.randomAlphanumericString
import ru.rikmasters.gilty.core.viewmodel.Strategy.JOIN

class AuthManager(
    
    private val tokenWebSource: TokenWebSource,
    
    private val dbSource: DbSource,
    
    override val primarySource: TokenStore,
): Repository<TokenStore>(primarySource) {
    
    suspend fun isAuthorized(): Boolean {
        val token = primarySource.getTokensOrNull()
        logD("YOUR TOKENS: ----->>> $token")
        return token != null
    }
    
    suspend fun savePushToken(
        token: String,
        type: PushType = FIREBASE,
    ) {
        tokenWebSource.savePushToken(token, type)
    }
    
    @Suppress("unused")
    suspend fun deletePushToken(
        token: String,
        type: PushType = FIREBASE,
    ) = tokenWebSource.deletePushToken(token, type)
    
    private suspend fun login(tokens: Tokens) =
        primarySource.saveTokens(tokens)
    
    suspend fun logout() {
        tokenWebSource.logout()
        primarySource.deleteTokens()
        updateAuth { copy(externalToken = null) }
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
    
    suspend fun isExternalLinked(token: String) =
        tokens(
            grantType = External,
            token = token,
        )?.let { login(it); true } ?: false
    
    suspend fun onOtpAuthentication(code: String?) =
        tokens(
            grantType = Otp,
            phone = getAuth().phone,
            code = code
        )?.let { login(it); true } ?: false
    
    private suspend fun tokens(
        grantType: TokenWebSource.GrantType,
        refreshToken: String? = null,
        token: String? = null,
        phone: String? = null,
        code: String? = null,
    ) = kotlin.runCatching {
        tokenWebSource.getOauthTokens(
            grantType, refreshToken,
            token, phone, code
        )
    }.getOrNull()
    
    suspend fun linkExternal(token: String) {
        tokenWebSource.linkExternal(token)
    }
    
    suspend fun getTokens() =
        primarySource.getTokensOrNull()?.bearer()
            ?: throw IllegalStateException("Нет токенов")
    
    suspend fun refreshTokens() = single(JOIN) {
        val tokens = tokenWebSource.getOauthTokens(
            Refresh, getTokens().refreshToken
        ) ?: throw IllegalStateException("Сервер не отдал токены")
        
        primarySource.saveTokens(tokens)
        
        tokens.bearer()
    }
    
    private fun Tokens.bearer() = BearerTokens(
        accessToken, refreshToken
    )
}