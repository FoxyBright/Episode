package ru.rikmasters.gilty.notification.model

internal data class PutRatingRequest(
    val userId: String,
    val emoji_type: String,
)