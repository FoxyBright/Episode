package ru.rikmasters.gilty.auth.token

data class TokensRequest(
    
    val grantType: String,
    
    val clientId: String,
    
    val clientSecret: String,
    
    val refreshToken: String? = null,
    
    val token: String? = null,
    
    val phone: String? = null,
    
    val code: String? = null

)