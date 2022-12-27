package ru.rikmasters.gilty.data.ktor

import com.fasterxml.jackson.databind.PropertyNamingStrategies
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
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.serialization.jackson.jackson
import ru.rikmasters.gilty.core.data.source.WebSource
import java.io.IOException

open class KtorSource: WebSource() {
    
    private val baseClient by lazy { HttpClient(OkHttp) {
        install(Logging) {
            level = LogLevel.BODY
            logger = LogAdapter
        }
        install(HttpRequestRetry) {
            exponentialDelay()
            maxRetries = 5
            retryOnExceptionIf { _, throwable -> throwable is IOException }
        }
        install(ContentNegotiation) {
            jackson {
                propertyNamingStrategy = PropertyNamingStrategies.SnakeCaseStrategy()
            }
        }
        defaultRequest {
            contentType(ContentType.Application.Json)
            host = env[ENV_HOST] ?: ""
        }
        install(UserAgent) {
            agent = env[ENV_USER_AGENT] ?: ""
        }
    } }
    
    val unauthorizedClient by lazy { baseClient.config {
    
    } }

    val client by lazy { baseClient.config {
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
    } }
    
    private val tokenManager
        get() = getKoin()
            .getOrNull<TokenManager>()
            ?: throw IllegalStateException("Не предоставлен TokenManager")
}