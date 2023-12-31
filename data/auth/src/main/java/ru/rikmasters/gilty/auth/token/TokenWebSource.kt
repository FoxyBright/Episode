package ru.rikmasters.gilty.auth.token

import io.ktor.client.call.body
import io.ktor.client.request.setBody
import ru.rikmasters.gilty.data.ktor.KtorSource
import ru.rikmasters.gilty.data.ktor.util.extension.query
import ru.rikmasters.gilty.data.shared.BuildConfig.*
import ru.rikmasters.gilty.shared.wrapper.coroutinesState

class TokenWebSource: KtorSource() {
    
    enum class GrantType(val value: String) {
        Refresh("refresh_token"),
        Otp("otp"),
        External("external")
    }
    
    suspend fun savePushToken(token: String, type: PushType) =
        post("http://$HOST$PREFIX_URL/utils/device") {
            setBody(SavePushTokenRequest(("ANDROID"), type.name, token))
        }
    
    suspend fun deletePushToken(token: String, type: PushType) =
        delete("http://$HOST$PREFIX_URL/utils/device") {
            url { query("device_id" to token, "type" to type.name) }
        }
    
    suspend fun linkExternal(token: String) =
        tryPost("http://$HOST$PREFIX_URL/auth/externals/link") {
            url { query("token" to token) }
        }.let { coroutinesState({ it }) {} }
    
    suspend fun getOauthTokens(
        grantType: GrantType,
        refreshToken: String? = null,
        token: String? = null,
        phone: String? = null,
        code: String? = null,
    ) = unauthorizedPost(
        "http://$HOST/oauth/token"
    ) {
        setBody(
            TokensRequest(
                grantType.value, CLIENT_ID,
                CLIENT_SECRET, refreshToken,
                token, phone, code
            )
        )
    }.let {
        coroutinesState({it}){
            it.body<TokensResponse>().domain()
        }
    }
}