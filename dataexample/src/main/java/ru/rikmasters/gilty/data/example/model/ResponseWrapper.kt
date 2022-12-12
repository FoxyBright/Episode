package ru.rikmasters.gilty.data.example.model

data class ResponseWrapper<T>(
    val success: Boolean,
    val data: T?
)