package ru.rikmasters.gilty.auth.token

internal data class TokensRequest(
    val grantType: GrantType,
    val clientId: String,
    val clientSecret: String,
    
    val refreshToken: String? = null,
    
    val phone: String? = null,
    val code: String? = null,
) {
    enum class GrantType(value: String) {
        REFRESH("refresh_token"),
        OTP("otp")
    }
}