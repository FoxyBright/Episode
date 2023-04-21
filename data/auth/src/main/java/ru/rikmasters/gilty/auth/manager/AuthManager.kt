package ru.rikmasters.gilty.auth.manager

import io.ktor.client.plugins.auth.providers.BearerTokens
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.withContext
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
    
    suspend fun hasTokens() = withContext(IO) {
        val token = primarySource.getTokensOrNull()
        logD("YOUR TOKENS: ----->>> $token")
        token != null
    }
    
    suspend fun savePushToken(
        token: String,
        type: PushType = FIREBASE,
    ) = withContext(IO) {
        tokenWebSource.savePushToken(token, type)
        token
    }
    
    @Suppress("unused")
    suspend fun deletePushToken(
        token: String,
        type: PushType = FIREBASE,
    ) = withContext(IO) {
        tokenWebSource.deletePushToken(token, type)
    }
    
    private suspend fun login(
        tokens: Tokens,
    ) = withContext(IO) {
        primarySource.saveTokens(tokens)
    }
    
    suspend fun logout() {
        withContext(IO) {
            tokenWebSource.logout()
            primarySource.deleteTokens()
            updateAuth { copy(externalToken = null) }
        }
    }
    
    private suspend fun startAuth() = withContext(IO) {
        dbSource.deleteAll<AuthSaga>()
        AuthSaga(generateExternalState()).let {
            dbSource.save(it)
            it
        }
    }
    
    suspend fun getSendCode() = withContext(IO) {
        getAuth().sendCode
    }
    
    suspend fun getAuth() = withContext(IO) {
        dbSource.find() ?: startAuth()
    }
    
    suspend fun updateAuth(
        update: AuthSaga.() -> AuthSaga,
    ) {
        withContext(IO) {
            val auth = getAuth()
            dbSource.deleteAll<AuthSaga>()
            dbSource.save(auth.update())
        }
    }
    
    private fun generateExternalState() =
        randomAlphanumericString(32)
    
    suspend fun isExternalLinked(
        token: String,
    ) = withContext(IO) {
        tokens(
            grantType = External,
            token = token,
        )?.let { login(it); true } ?: false
    }
    
    suspend fun onOtpAuthentication(
        code: String?,
    ) = withContext(IO) {
        tokens(
            grantType = Otp,
            phone = getAuth().phone,
            code = code
        )?.let { login(it); true } ?: false
    }
    
    private suspend fun tokens(
        grantType: TokenWebSource.GrantType,
        refreshToken: String? = null,
        token: String? = null,
        phone: String? = null,
        code: String? = null,
    ) = withContext(IO) {
        kotlin.runCatching {
            tokenWebSource.getOauthTokens(
                grantType, refreshToken,
                token, phone, code
            )
        }.getOrNull()
    }
    
    suspend fun linkExternal(token: String) {
        withContext(IO) {
            tokenWebSource.linkExternal(token)
        }
    }
    
    suspend fun getTokens() = withContext(IO) {
        primarySource.getTokensOrNull()?.bearer()
            ?: throw IllegalStateException("Нет токенов")
    }
    
    suspend fun refreshTokens() = single(JOIN) {
        withContext(IO) {
            val tokens = tokenWebSource.getOauthTokens(
                Refresh, getTokens().refreshToken
            ) ?: throw IllegalStateException("Сервер не отдал токены")
            
            primarySource.saveTokens(tokens)
            
            tokens.bearer()
        }
    }
    
    private fun Tokens.bearer() = BearerTokens(
        accessToken, refreshToken
    )
}