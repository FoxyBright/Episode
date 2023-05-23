package ru.rikmasters.gilty.shared.wrapper

import com.fasterxml.jackson.annotation.JsonAlias
import io.ktor.client.call.body
import io.ktor.client.statement.HttpResponse

enum class Status {
    @JsonAlias("success")
    SUCCESS,
    
    @JsonAlias("error")
    @Suppress("unused")
    ERROR
}

data class ResponseWrapper<T: Any?>(
    val status: Status = Status.SUCCESS,
    val data: T,
    val error: Error? = null,
    val paginator: Paginator? = null,
) {
    
    @Suppress("unused")
    class Paginator(
        val type: String,
        val total: Int,
        val perPage: Int,
        val currentPage: Int,
        val last_page: Int,
    ) {
        
        constructor(): this(
            (""), (0), (0),
            (0), (0)
        )
    }
    
    data class Error(
        val code: String? = null,
        val message: String? = null,
        val exception: String? = null,
        val file: String? = null,
        val line: Int? = null,
        val trace: Any? = null,
    )
    
    val dataChecked: T
        get() {
            if(status != Status.SUCCESS)
                throw NotSuccessException(status)
            return data
        }
}

data class ErrorResponseWrapper(
    val status: String = "",
    val error: Error = Error(),
) {
    
    data class Error(
        val code: String = "",
        val message: String = "",
        val exception: String = "",
        val file: String = "",
        val line: Int = 0,
        val trace: Any? = null,
    )
}

suspend inline fun <reified T> HttpResponse.paginateWrapped(
): Pair<T, ResponseWrapper.Paginator> where T: Any? {
    val response = body<ResponseWrapper<T>>()
    return Pair(response.data, response.paginator!!)
}

suspend inline fun HttpResponse.errorWrapped() =
    body<ErrorResponseWrapper>()

suspend inline fun <reified T> HttpResponse.wrapped(): T
        where T: Any? = body<ResponseWrapper<T>>().dataChecked