package ru.rikmasters.gilty.auth.token

import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import ru.rikmasters.gilty.data.ktor.KtorSource
import ru.rikmasters.gilty.shared.BuildConfig

class TokenWebSource: KtorSource() {
    
    enum class GrantType(val value: String) {
        Refresh("refresh_token"),
        Otp("otp"),
        External("external")
    }
    
    suspend fun getOauthTokens(
        grantType: GrantType,
        refreshToken: String? = null,
        token: String? = null,
        phone: String? = null,
        code: String? = null,
    ) = unauthorizedClient.post(
        "http://" + BuildConfig.HOST + "/oauth/token"
    ) {
        setBody(TokensRequest(
            grantType.value,
            BuildConfig.CLIENT_ID,
            BuildConfig.CLIENT_SECRET,
            refreshToken = refreshToken,
            token = token,
            phone = phone,
            code = code
        ))
    }.body<TokensResponse?>()?.domain()
    
}