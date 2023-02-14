package ru.rikmasters.gilty.data.ktor

import com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL
import com.fasterxml.jackson.databind.DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES
import com.fasterxml.jackson.databind.PropertyNamingStrategies.SnakeCaseStrategy
import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.HttpRequestRetry
import io.ktor.client.plugins.UserAgent
import io.ktor.client.plugins.auth.Auth
import io.ktor.client.plugins.auth.providers.bearer
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.plugins.websocket.WebSockets
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.serialization.jackson.jackson
import okhttp3.OkHttpClient
import ru.rikmasters.gilty.core.data.source.WebSource
import java.io.IOException
import java.util.concurrent.TimeUnit.MILLISECONDS
import java.util.concurrent.TimeUnit.MINUTES

open class KtorSource: WebSource() {
    
    private val webSocketPingInterval = 20_000L
    
    private val baseClient by lazy {
        HttpClient(OkHttp) {
            engine { config { writeTimeout(5, MINUTES) } }
            install(Logging) {
                level = LogLevel.BODY
                logger = LogAdapter
            }
            install(HttpRequestRetry) {
                exponentialDelay()
                maxRetries = 3
                retryOnExceptionIf { _, throwable -> throwable is IOException }
            }
            install(ContentNegotiation) {
                jackson {
                    configure(FAIL_ON_UNKNOWN_PROPERTIES, false)
                    propertyNamingStrategy = SnakeCaseStrategy()
                    setSerializationInclusion(NON_NULL)
                }
            }
            defaultRequest {
                contentType(ContentType.Application.Json)
                host = env[ENV_BASE_URL] ?: ""
            }
            install(UserAgent) {
                agent = env[ENV_USER_AGENT] ?: ""
            }
            install(WebSockets) {
                pingInterval = webSocketPingInterval
            }
            engine {
                preconfigured = OkHttpClient.Builder()
                    .pingInterval(
                        webSocketPingInterval,
                        MILLISECONDS
                    ).build()
            }
        }
    }
    
    val unauthorizedClient by lazy {
        baseClient.config {}
    }
    
    var client = getClientWithTokens()
    
    fun updateClientToken() {
        client = getClientWithTokens()
    }
    
    private fun getClientWithTokens(): HttpClient {
        return baseClient.config {
            install(Auth) {
                bearer {
                    loadTokens {
                        tokenManager.getTokens()
                    }
                    refreshTokens {
                        tokenManager.refreshTokens()
                    }
                }
            }
        }
    }
    
    private val tokenManager
        get() = getKoin().getOrNull<TokenManager>()
            ?: throw IllegalStateException("Не предоставлен TokenManager")
}