package ru.rikmasters.gilty.shared.models.socket

import com.fasterxml.jackson.annotation.JsonAlias

data class SocketResponse(
    
    val event: String = "",
    
    @JsonAlias("data")
    private val _data: Any? = null,
    
    val channel: String? = null,
) {
    
    val data get() = _data as String

    @Suppress("unused")
    val dataRaw get() = _data
}
