package ru.rikmasters.gilty.auth

import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import ru.rikmasters.gilty.auth.login.LoginRepository
import ru.rikmasters.gilty.auth.manager.AuthManager
import ru.rikmasters.gilty.auth.manager.AuthTokenManager
import ru.rikmasters.gilty.auth.manager.RegistrationManager
import ru.rikmasters.gilty.auth.saga.AuthSaga
import ru.rikmasters.gilty.auth.token.*
import ru.rikmasters.gilty.core.data.entity.builder.EntitiesBuilder
import ru.rikmasters.gilty.core.module.DataDefinition
import ru.rikmasters.gilty.data.ktor.TokenManager
import ru.rikmasters.gilty.profile.ProfileManager
import ru.rikmasters.gilty.profile.ProfileWebSource
import ru.rikmasters.gilty.shared.models.Profile

object Auth: DataDefinition() {
    
    override fun EntitiesBuilder.entities() {
        entity<AuthSaga>()
        entity<Profile>()
        entity(Tokens::class) {
            web(TokensResponse::class)
        }
    }

    override fun Module.koin() {
        singleOf(::AuthTokenManager).bind<TokenManager>()
        singleOf(::RegistrationManager)
        singleOf(::ProfileWebSource)
        singleOf(::LoginRepository)
        singleOf(::ProfileManager)
        singleOf(::TokenWebSource)
        singleOf(::AuthManager)
        singleOf(::TokenStore)
    }
}