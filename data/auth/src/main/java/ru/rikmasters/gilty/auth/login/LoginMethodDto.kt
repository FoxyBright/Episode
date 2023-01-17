package ru.rikmasters.gilty.auth.login

data class LoginMethodDto(
    
    val type: Type,
    
    val url: String

) {
    
    enum class Type {
        APPLE, GOOGLE, VK;
        
        fun getName(): String {
            val name = this.name
            val first = name.first().uppercase()
            return "$first${name.removePrefix(first).lowercase()}"
        }
    }
    
    fun map() = when(type) {
        Type.APPLE -> Apple(url, type.getName())
        Type.GOOGLE -> Google(url, type.getName())
        Type.VK -> Vk(url, type.name)
    }
}