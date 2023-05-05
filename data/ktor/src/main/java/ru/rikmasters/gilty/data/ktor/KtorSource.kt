package ru.rikmasters.gilty.data.ktor

import com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL
import com.fasterxml.jackson.databind.DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES
import com.fasterxml.jackson.databind.PropertyNamingStrategies.SnakeCaseStrategy
import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.*
import io.ktor.client.plugins.auth.Auth
import io.ktor.client.plugins.auth.providers.bearer
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.plugins.websocket.WebSockets
import io.ktor.client.plugins.websocket.webSocketSession
import io.ktor.client.request.*
import io.ktor.client.request.forms.submitFormWithBinaryData
import io.ktor.http.ContentType.Application.Json
import io.ktor.http.content.PartData
import io.ktor.http.contentType
import io.ktor.serialization.jackson.JacksonWebsocketContentConverter
import io.ktor.serialization.jackson.jackson
import okhttp3.OkHttpClient
import ru.rikmasters.gilty.core.data.source.WebSource
import java.util.concurrent.TimeUnit.MILLISECONDS
import java.util.concurrent.TimeUnit.MINUTES

open class KtorSource: WebSource() {
    
    private val webSocketPingInterval = 20_000L
    
    private val baseClient by lazy {
        HttpClient(OkHttp) {
            
            expectSuccess = true
            
            engine {
                config { writeTimeout(5, MINUTES) }
                preconfigured = OkHttpClient.Builder()
                    .pingInterval(
                        webSocketPingInterval,
                        MILLISECONDS
                    )
                    .retryOnConnectionFailure(true)
                    .build()
            }
            install(Logging) {
                level = LogLevel.BODY
                logger = LogAdapter
            }
            install(HttpRequestRetry) {
                maxRetries = 3
                exponentialDelay()
            }
            install(ContentNegotiation) {
                jackson {
                    configure(FAIL_ON_UNKNOWN_PROPERTIES, false)
                    propertyNamingStrategy = SnakeCaseStrategy()
                    setSerializationInclusion(NON_NULL)
                }
            }
            defaultRequest {
                contentType(Json)
                headers { append("Accept-Language", "ru") }
                host = env[ENV_BASE_URL] ?: ""
            }
            install(HttpTimeout) { socketTimeoutMillis = 15000 }
            install(UserAgent) { agent = env[ENV_USER_AGENT] ?: "" }
            install(WebSockets) {
                contentConverter = JacksonWebsocketContentConverter()
                pingInterval = webSocketPingInterval
            }
        }
    }
    
    val unauthorizedClient by lazy {
        baseClient.config {}
    }
    
    private var client = getClientWithTokens()
    
    private fun updateClientToken() {
        client = getClientWithTokens()
    }
    
    suspend fun wsSession(
        host: String, port: Int, path: String,
    ) = unauthorizedClient.webSocketSession(
        host = host, port = port, path = path
    )
    
    suspend fun unauthorizedGet(
        url: String,
        block: HttpRequestBuilder.() -> Unit = {},
    ) = handler { unauthorizedClient.get(url, block) }
    
    suspend fun unauthorizedPost(
        url: String,
        block: HttpRequestBuilder.() -> Unit = {},
    ) = handler { unauthorizedClient.post(url, block) }
    
    private suspend fun <T> handler(
        block: suspend () -> T,
    ) = try {
        updateClientToken(); block()
    } catch(e: Exception) {
        logE("KTOR RESULT EXCEPTION: $e")
        null
    }
    
    suspend fun get(
        url: String,
        block: HttpRequestBuilder.() -> Unit = {},
    ) = handler { client.get(url, block) }
    
    suspend fun post(
        url: String,
        block: HttpRequestBuilder.() -> Unit = {},
    ) = handler { client.post(url, block) }
    
    suspend fun postFormData(
        url: String,
        formData: List<PartData>,
        block: HttpRequestBuilder.() -> Unit = {},
    ) = handler {
        client.submitFormWithBinaryData(
            url, formData, block
        )
    }
    
    suspend fun patch(
        url: String,
        block: HttpRequestBuilder.() -> Unit = {},
    ) = handler { client.patch(url, block) }
    
    suspend fun delete(
        url: String,
        block: HttpRequestBuilder.() -> Unit = {},
    ) = handler { client.delete(url, block) }
    
    suspend fun put(
        url: String,
        block: HttpRequestBuilder.() -> Unit = {},
    ) = handler { client.put(url, block) }
    
    fun closeClient() {
        unauthorizedClient.close()
        client.close()
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