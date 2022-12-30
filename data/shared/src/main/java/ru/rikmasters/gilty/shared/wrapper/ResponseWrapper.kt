package ru.rikmasters.gilty.shared.wrapper

import io.ktor.client.call.body
import io.ktor.client.statement.HttpResponse

data class ResponseWrapper<T: Any?>(
    val status: String,
    val data: T
) {
    val dataChecked: T get() {
        if(status != "success")
            throw NotSuccessException(status)
        return data
    }
}

suspend inline fun <reified T> HttpResponse.wrapped(): T
    where T: Any? = body<ResponseWrapper<T>>().dataChecked