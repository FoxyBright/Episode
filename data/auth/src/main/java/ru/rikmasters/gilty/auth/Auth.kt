package ru.rikmasters.gilty.auth

import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import ru.rikmasters.gilty.auth.login.LoginRepository
import ru.rikmasters.gilty.auth.manager.AuthManager
import ru.rikmasters.gilty.auth.token.TokenStore
import ru.rikmasters.gilty.auth.token.Tokens
import ru.rikmasters.gilty.auth.token.TokensResponse
import ru.rikmasters.gilty.core.data.entity.builder.EntitiesBuilder
import ru.rikmasters.gilty.core.module.DataDefinition
import ru.rikmasters.gilty.data.ktor.TokenManager

object Auth: DataDefinition() {
    
    override fun EntitiesBuilder.entities() {
        entity(Tokens::class) {
            web(TokensResponse::class)
        }
    }
    
    override fun Module.koin() {
        singleOf(::TokenStore).bind<TokenManager>()
        singleOf(::AuthManager)
        singleOf(::LoginRepository)
    }
}