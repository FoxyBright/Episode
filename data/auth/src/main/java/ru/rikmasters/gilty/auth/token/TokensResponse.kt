package ru.rikmasters.gilty.auth.token

import ru.rikmasters.gilty.auth.token.Tokens
import ru.rikmasters.gilty.core.data.entity.interfaces.WebEntity

internal data class TokensResponse(

    val tokenType: String,
    
    val expiresIn: Int,
    
    val accessToken: String,
    
    val refreshToken: String

): WebEntity<Tokens> {
    
    override fun domain() = Tokens(
        accessToken, refreshToken
    )
}