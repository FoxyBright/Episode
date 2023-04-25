package ru.rikmasters.gilty.auth.login

import io.ktor.client.request.post
import io.ktor.client.request.setBody
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.withContext
import ru.rikmasters.gilty.core.data.repository.OfflineFirstRepository
import ru.rikmasters.gilty.core.data.source.DbSource
import ru.rikmasters.gilty.data.ktor.KtorSource
import ru.rikmasters.gilty.data.ktor.util.extension.query
import ru.rikmasters.gilty.data.shared.BuildConfig.CLIENT_ID
import ru.rikmasters.gilty.data.shared.BuildConfig.CLIENT_SECRET
import ru.rikmasters.gilty.shared.wrapper.errorWrapped
import ru.rikmasters.gilty.shared.wrapper.wrapped

class LoginRepository(
    override val webSource: KtorSource,
    override val primarySource: DbSource,
): OfflineFirstRepository<KtorSource, DbSource>(
    webSource, primarySource
) {
    
    suspend fun getLoginMethods(state: String): Set<LoginMethod> =
        webSource.unauthorizedGet("/auth/externals") {
            url {
                query(
                    "client_id" to CLIENT_ID,
                    "client_secret" to CLIENT_SECRET,
                    "state" to state
                )
            }
        }?.wrapped<List<LoginMethodDto>>()
            ?.map(LoginMethodDto::map)
            ?.toSet()
            ?: emptySet()
    
    suspend fun sendCode(phone: String) = withContext(IO) {
        webSource.unauthorizedClient
            .post("auth/sendCode") {
                setBody(
                    SendCodeRequest(
                        phone, CLIENT_ID,
                        CLIENT_SECRET
                    )
                )
            }.let {
                if(it.status.value == 200)
                    it.wrapped<SendCode>() to null
                else null to it.errorWrapped().error.message
            }
    }
}