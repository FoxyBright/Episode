package ru.rikmasters.gilty.auth.login

sealed interface LoginMethod {
    
    val url: String
    val name: String
}

data class Google(
    override val url: String,
    override val name: String
): LoginMethod

data class Apple(
    override val url: String,
    override val name: String
): LoginMethod

data class Vk(
    override val url: String,
    override val name: String
): LoginMethod