package ru.rikmasters.gilty.auth.token

data class SavePushTokenRequest(
    val platform: String,
    val type: String,
    val deviceId: String,
)