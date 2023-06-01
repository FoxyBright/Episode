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
import java.util.Locale.getDefault
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
                headers {
                    append(
                        name = "Accept-Language",
                        value = getDefault().language
                    )
                }
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
        baseClient.config {
            expectSuccess = false
        }
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
    ) = unauthorizedClient.get(url, block)
    
    suspend fun unauthorizedPost(
        url: String,
        block: HttpRequestBuilder.() -> Unit = {},
    ) = unauthorizedClient.post(url, block)
    
    fun closeClient() {
        unauthorizedClient.close()
        client.close()
    }
    
    private fun getClientWithTokens() =
        baseClient.config {
            install(Auth) {
                bearer {
                    loadTokens { tokenManager.getTokens() }
                    refreshTokens { tokenManager.refreshTokens() }
                }
            }
        }
    
    private val tokenManager
        get() = getKoin().getOrNull<TokenManager>()
            ?: throw IllegalStateException("Не предоставлен TokenManager")
    
    private val unExpectClient =
        client.config { expectSuccess = false }
    
    suspend fun delete(
        url: String,
        block: HttpRequestBuilder.() -> Unit = {},
    ) = updateClientToken().let {
        client.delete(url, block)
    }
    
    suspend fun post(
        url: String,
        block: HttpRequestBuilder.() -> Unit = {},
    ) = updateClientToken().let {
        client.post(url, block)
    }
    
    suspend fun tryGet(
        url: String,
        block: HttpRequestBuilder.() -> Unit = {},
    ) = updateClientToken().let {
        unExpectClient.get(url, block)
    }
    
    suspend fun tryPost(
        url: String,
        block: HttpRequestBuilder.() -> Unit = {},
    ) = updateClientToken().let {
        unExpectClient.post(url, block)
    }
    
    suspend fun tryPostFormData(
        url: String,
        formData: List<PartData>,
        block: HttpRequestBuilder.() -> Unit = {},
    ) = updateClientToken().let {
        unExpectClient.submitFormWithBinaryData(
            url, formData, block
        )
    }
    
    suspend fun tryPatch(
        url: String,
        block: HttpRequestBuilder.() -> Unit = {},
    ) = updateClientToken().let {
        unExpectClient.patch(url, block)
    }
    
    suspend fun tryDelete(
        url: String,
        block: HttpRequestBuilder.() -> Unit = {},
    ) = updateClientToken().let {
        unExpectClient.delete(url, block)
    }
    
    suspend fun tryPut(
        url: String,
        block: HttpRequestBuilder.() -> Unit = {},
    ) = updateClientToken().let {
        unExpectClient.put(url, block)
    }
}