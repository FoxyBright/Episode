package ru.rikmasters.gilty.shared.wrapper

import com.fasterxml.jackson.annotation.JsonAlias
import io.ktor.client.call.body
import io.ktor.client.statement.HttpResponse

data class ResponseWrapper<T: Any?>(
    val status: Status,
    val data: T,
    val error: Error?
) {
    enum class Status {
        @JsonAlias("success")
        SUCCESS,
        @JsonAlias("error")
        ERROR
    }
    
    data class Error(
        val code: String,
        val message: String
    )
    
    val dataChecked: T get() {
        if(status != Status.SUCCESS)
            throw NotSuccessException(status, error)
        return data
    }
}

suspend inline fun <reified T> HttpResponse.wrapped(): T
    where T: Any? = body<ResponseWrapper<T>>().dataChecked