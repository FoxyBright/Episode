package ru.rikmasters.gilty.auth.token

import ru.rikmasters.gilty.core.data.entity.interfaces.DomainEntity

data class Tokens(
    
    val accessToken: String,
    
    val refreshToken: String
    
): DomainEntity