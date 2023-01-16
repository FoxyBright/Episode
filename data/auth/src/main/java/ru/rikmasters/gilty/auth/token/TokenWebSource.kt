package ru.rikmasters.gilty.auth.token

import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import ru.rikmasters.gilty.data.ktor.KtorSource
import ru.rikmasters.gilty.data.ktor.util.extension.query
import ru.rikmasters.gilty.shared.BuildConfig
import ru.rikmasters.gilty.shared.BuildConfig.HOST

class TokenWebSource: KtorSource() {
    
    enum class GrantType(val value: String) {
        Refresh("refresh_token"),
        Otp("otp"),
        External("external")
    }
    
    suspend fun linkExternal(
        token: String
    ) = client.post(
        "http://$HOST/oauth/token"
    ) { url { query("token" to token) } }
    
    suspend fun getOauthTokens(
        grantType: GrantType,
        refreshToken: String? = null,
        token: String? = null,
        phone: String? = null,
        code: String? = null,
    ) = unauthorizedClient.post(
        "http://$HOST/oauth/token"
    ) {
        setBody(
            TokensRequest(
                grantType.value,
                BuildConfig.CLIENT_ID,
                BuildConfig.CLIENT_SECRET,
                refreshToken,
                token,
                phone,
                code
            )
        )
    }.body<TokensResponse?>()?.domain()
}