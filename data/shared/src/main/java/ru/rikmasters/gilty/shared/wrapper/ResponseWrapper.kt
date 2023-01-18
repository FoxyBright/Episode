package ru.rikmasters.gilty.shared.wrapper

import com.fasterxml.jackson.annotation.JsonAlias
import io.ktor.client.call.body
import io.ktor.client.statement.HttpResponse

enum class Status {
    @JsonAlias("success")
    SUCCESS,
    
    @JsonAlias("error")
    ERROR
}

data class ResponseWrapper<T: Any?>(
    val status: Status,
    val data: T,
    val error: Error? = null
) {
    
    data class Error(
        val code: String,
        val message: String,
        val exception: String,
        val file: String,
        val line: Int,
        val trace: Any?
    )
    
    val dataChecked: T
        get() {
            if(status != Status.SUCCESS)
                throw NotSuccessException(status)
            return data
        }
}

data class ErrorResponseWrapper(
    val status: Status,
    val error: Error
) {
    data class Error(
        val code: String,
        val message: String,
        val exception: String,
        val file: String,
        val line: Int,
        val trace: Any?
    )
}

suspend inline fun HttpResponse.errorWrapped() =
    body<ErrorResponseWrapper>()

suspend inline fun <reified T> HttpResponse.wrapped(): T
        where T: Any? = body<ResponseWrapper<T>>().dataChecked