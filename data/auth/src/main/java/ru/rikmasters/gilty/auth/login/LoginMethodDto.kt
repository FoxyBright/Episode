package ru.rikmasters.gilty.auth.login

data class LoginMethodDto(

    val type: Type,
    
    val url: String

) {
    enum class Type {
        APPLE, GOOGLE, VK
    }
    
    fun map() = when(type) {
        Type.APPLE -> Apple(url)
        Type.GOOGLE -> Google(url)
        Type.VK -> Vk(url)
    }
}