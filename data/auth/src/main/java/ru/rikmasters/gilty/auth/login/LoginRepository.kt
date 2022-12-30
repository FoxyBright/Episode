package ru.rikmasters.gilty.auth.login

import io.ktor.client.request.get
import ru.rikmasters.gilty.auth.token.TokenStore
import ru.rikmasters.gilty.core.data.repository.OfflineFirstRepository
import ru.rikmasters.gilty.core.data.source.DbSource
import ru.rikmasters.gilty.data.ktor.KtorSource
import ru.rikmasters.gilty.data.ktor.util.extension.query
import ru.rikmasters.gilty.shared.BuildConfig
import ru.rikmasters.gilty.shared.wrapper.wrapped

class LoginRepository(
    
    override val webSource: KtorSource,
    
    override val primarySource: DbSource
    
): OfflineFirstRepository<KtorSource, DbSource>(webSource, primarySource) {

    suspend fun getLoginMethods(state: String): Set<LoginMethod> =
        webSource.unauthorizedClient.get("/auth/externals") {
            url {
                query(
                    "client_id" to BuildConfig.CLIENT_ID,
                    "client_secret" to BuildConfig.CLIENT_SECRET,
                    "state" to state
                )
            }
        }.wrapped<List<LoginMethodDto>>()
            .map(LoginMethodDto::map)
            .toSet()
}