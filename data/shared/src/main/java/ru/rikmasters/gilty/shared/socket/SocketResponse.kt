package ru.rikmasters.gilty.shared.socket

import com.fasterxml.jackson.annotation.JsonAlias

data class SocketResponse(
    
    val event: String,
    
    @JsonAlias("data")
    private val _data: Any?,
    
    val channel: String?,
) {
    
    val data get() = _data as String
}
