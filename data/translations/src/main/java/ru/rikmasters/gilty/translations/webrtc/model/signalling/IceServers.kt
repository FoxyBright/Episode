package ru.rikmasters.gilty.translations.webrtc.model.signalling

import ru.rikmasters.gilty.translations.webrtc.utils.asListOfType

data class IceServers(
    val user: String,
    val credential: String,
    val urls: List<String>
)

fun Map<String, *>.mapToIceServers(): IceServers {
    val user = this["username"] as? String ?: ""
    val credential = this["credential"] as? String ?: ""
    val urls = (this["urls"] as? List<*>)?.asListOfType<String>() ?: emptyList()
    return IceServers(
        user = user,
        credential = credential,
        urls = urls
    )
}