package ru.rikmasters.gilty.auth

import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import ru.rikmasters.gilty.auth.categories.CategoriesWebSource
import ru.rikmasters.gilty.auth.login.LoginRepository
import ru.rikmasters.gilty.auth.manager.*
import ru.rikmasters.gilty.auth.profile.ProfileWebSource
import ru.rikmasters.gilty.auth.saga.AuthSaga
import ru.rikmasters.gilty.auth.token.*
import ru.rikmasters.gilty.core.data.entity.builder.EntitiesBuilder
import ru.rikmasters.gilty.core.module.DataDefinition
import ru.rikmasters.gilty.data.ktor.TokenManager

object Auth: DataDefinition() {
    
    override fun EntitiesBuilder.entities() {
        entity<AuthSaga>()
        entity(Tokens::class) {
            web(TokensResponse::class)
        }
    }

    override fun Module.koin() {
        singleOf(::TokenWebSource)
        singleOf(::ProfileWebSource)
        singleOf(::CategoriesWebSource)
        singleOf(::TokenStore)
        singleOf(::AuthManager)
        singleOf(::RegistrationManager)
        singleOf(::ProfileManager)
        singleOf(::AuthTokenManager).bind<TokenManager>()
        singleOf(::LoginRepository)
    }
}