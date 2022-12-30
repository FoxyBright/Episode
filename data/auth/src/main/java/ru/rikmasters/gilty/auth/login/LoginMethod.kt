package ru.rikmasters.gilty.auth.login

sealed interface LoginMethod {
    val url: String
}

data class Apple(
    override val url: String
): LoginMethod

data class Google(
    override val url: String
): LoginMethod

data class Vk(
    override val url: String
): LoginMethod