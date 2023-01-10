package ru.rikmasters.gilty.auth.login

data class SendCode(
    val code: String, // TODO
    val codeLength: Int,
    val codeTimeout: Int,
    val sessionTtl: Int,
)