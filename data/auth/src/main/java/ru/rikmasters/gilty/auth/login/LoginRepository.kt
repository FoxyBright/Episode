package ru.rikmasters.gilty.auth.login

import io.ktor.client.request.setBody
import ru.rikmasters.gilty.core.data.repository.OfflineFirstRepository
import ru.rikmasters.gilty.core.data.source.DbSource
import ru.rikmasters.gilty.data.ktor.KtorSource
import ru.rikmasters.gilty.data.ktor.util.extension.query
import ru.rikmasters.gilty.shared.BuildConfig
import ru.rikmasters.gilty.shared.wrapper.errorWrapped
import ru.rikmasters.gilty.shared.wrapper.wrapped

class LoginRepository(
    
    override val webSource: KtorSource,
    
    override val primarySource: DbSource,
    
    ): OfflineFirstRepository<KtorSource, DbSource>(webSource, primarySource) {
    
    suspend fun getLoginMethods(state: String): Set<LoginMethod> =
        webSource.unauthorizedGet("/auth/externals") {
            url {
                query(
                    "client_id" to BuildConfig.CLIENT_ID,
                    "client_secret" to BuildConfig.CLIENT_SECRET,
                    "state" to state
                )
            }
        }?.wrapped<List<LoginMethodDto>>()
            ?.map(LoginMethodDto::map)
            ?.toSet()
            ?: emptySet()
    
    suspend fun sendCode(phone: String): Pair<SendCode?, String?> {
        val response = webSource
            .unauthorizedPost("auth/sendCode") {
                setBody(
                    SendCodeRequest(
                        phone,
                        BuildConfig.CLIENT_ID,
                        BuildConfig.CLIENT_SECRET
                    )
                )
            }
        
        var message: String? = null
        var sendCode: SendCode? = null
        
        try {
            sendCode = response?.wrapped<SendCode>()
        } catch(_: Exception) {
            message = response?.errorWrapped()?.error?.message
                ?: "Плохое соединение с интернетом"
        }
        
        return Pair(sendCode, message)
    }
}