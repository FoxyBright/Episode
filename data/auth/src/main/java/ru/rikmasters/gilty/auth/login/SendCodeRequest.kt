package ru.rikmasters.gilty.auth.login

data class SendCodeRequest(
    val phone: String,
    val clientId: String,
    val clientSecret: String
)