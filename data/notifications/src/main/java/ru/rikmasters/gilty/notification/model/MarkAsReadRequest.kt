package ru.rikmasters.gilty.notification.model

internal data class MarkAsReadRequest(
    val notificationIds: List<String>,
    val readAll: Boolean,
)