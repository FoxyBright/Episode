package ru.rikmasters.gilty.shared.wrapper

import com.fasterxml.jackson.annotation.JsonAlias
import io.ktor.client.call.body
import io.ktor.client.network.sockets.SocketTimeoutException
import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.plugins.RedirectResponseException
import io.ktor.client.plugins.ResponseException
import io.ktor.client.plugins.ServerResponseException
import io.ktor.client.statement.HttpResponse
import io.ktor.utils.io.errors.IOException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
import ru.rikmasters.gilty.shared.model.DataStateTest
import ru.rikmasters.gilty.shared.model.ExceptionCause
import java.net.UnknownHostException

enum class Status {
    @JsonAlias("success")
    SUCCESS,

    @JsonAlias("error")
    @Suppress("unused")
    ERROR,
}

data class ResponseWrapper<T : Any?>(
    val status: Status,
    val data: T,
    val error: Error? = null,
    val paginator: Paginator? = null,
) {

    @Suppress("unused")
    class Paginator(
        val total: Int,
        val perPage: Int,
        val currentPage: Int,
        val list_page: Int,
        val limit: Int,
        val offset: Int,
    ) {

        constructor() : this(
            (0),
            (0),
            (0),
            (0),
            (0),
            (0),
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
            if (status != Status.SUCCESS) {
                throw NotSuccessException(status)
            }
            return data
        }
}

data class ErrorResponseWrapper(
    val status: Status,
    val error: Error,
) {

    data class Error(
        val code: String,
        val message: String,
        val exception: String,
        val file: String,
        val line: Int,
        val trace: Any?,
    )
}

suspend inline fun <reified T> HttpResponse.paginateWrapped(): Pair<T, ResponseWrapper.Paginator> where T : Any? {
    val response = body<ResponseWrapper<T>>()
    return Pair(response.data, response.paginator!!)
}

suspend inline fun HttpResponse.errorWrapped() =
    body<ErrorResponseWrapper>()

suspend inline fun <reified T> HttpResponse.wrapped(): T
    where T : Any? = body<ResponseWrapper<T>>().dataChecked

private suspend fun <T : Any> coroutinesState(
    block: suspend () -> T,
) = try {
    withContext(Dispatchers.IO) {
        DataStateTest.Success(
            data = block(),
        )
    }
} catch (e: IOException) {
    DataStateTest.Error(
        cause = ExceptionCause.IO,
    )
} catch (e: SocketTimeoutException) {
    DataStateTest.Error(
        cause = ExceptionCause.SocketTimeout,
    )
} catch (e: UnknownHostException) {
    DataStateTest.Error(
        cause = ExceptionCause.UnknownHost,
    )
} catch (e: ResponseException) {
    when (e) {
        is RedirectResponseException -> {
            DataStateTest.Error(
                cause = ExceptionCause.RedirectResponse,
            )
        }
        is ClientRequestException -> {
            DataStateTest.Error(
                cause = ExceptionCause.ClientRequest,
            )
        }
        is ServerResponseException -> {
            DataStateTest.Error(
                cause = ExceptionCause.ServerResponse,
            )
        }
        else -> {
            DataStateTest.Error(
                cause = ExceptionCause.UnknownException,
            )
        }
    }
} catch (e: Exception) {
    DataStateTest.Error(
        cause = ExceptionCause.UnknownException,
    )
}

private fun <T : Any> flowState(
    block: suspend () -> T,
) = flow<DataStateTest<T>> {
    emit(DataStateTest.Loading())
    val response = try {
        DataStateTest.Success(
            data = block(),
        )
    } catch (e: IOException) {
        DataStateTest.Error(
            cause = ExceptionCause.IO,
        )
    } catch (e: SocketTimeoutException) {
        DataStateTest.Error(
            cause = ExceptionCause.SocketTimeout,
        )
    } catch (e: UnknownHostException) {
        DataStateTest.Error(
            cause = ExceptionCause.UnknownHost,
        )
    } catch (e: ResponseException) {
        when (e) {
            is RedirectResponseException -> {
                DataStateTest.Error(
                    cause = ExceptionCause.RedirectResponse,
                )
            }
            is ClientRequestException -> {
                DataStateTest.Error(
                    cause = ExceptionCause.ClientRequest,
                )
            }
            is ServerResponseException -> {
                DataStateTest.Error(
                    cause = ExceptionCause.ServerResponse,
                )
            }
            else -> {
                DataStateTest.Error(
                    cause = ExceptionCause.UnknownException,
                )
            }
        }
    } catch (e: Exception) {
        DataStateTest.Error(
            cause = ExceptionCause.UnknownException,
        )
    }
    emit(response)
}.flowOn(Dispatchers.IO)
