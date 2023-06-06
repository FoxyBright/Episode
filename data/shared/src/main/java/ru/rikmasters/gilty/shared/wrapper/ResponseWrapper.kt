package ru.rikmasters.gilty.shared.wrapper

import androidx.paging.PagingSource
import com.fasterxml.jackson.annotation.JsonAlias
import io.ktor.client.call.body
import io.ktor.client.network.sockets.SocketTimeoutException
import io.ktor.client.plugins.*
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
    ERROR
}

data class ResponseWrapperTest<T: Any?>(
    val status: Status? = null,
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
}

data class ResponseWrapper<T: Any?>(
    val status: Status,
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

// По неизвестным причинам не приходит status
@Suppress("unused")
suspend inline fun <reified T> HttpResponse.wrappedTest(): T
        where T: Any? = body<ResponseWrapperTest<T>>().data

suspend inline fun <reified T> HttpResponse.wrapped(): T
        where T: Any? = body<ResponseWrapper<T>>().dataChecked

private fun String?.errorController(): String {
    return this ?: "Неизвестная ошибка"
}

/**
 * [coroutinesState] превращает запрос в DataState (suspend)
 */
suspend fun <T: Any> coroutinesState(
    request: () -> HttpResponse,
    expectCode: Int = 200,
    response: suspend () -> T,
) = try {
    request().let {
        if(it.status.value == expectCode)
            withContext(Dispatchers.IO) {
                DataStateTest.Success(
                    data = response(),
                )
            }
        else DataStateTest.Error(
            cause = ExceptionCause.UnknownException(
                message = it.errorWrapped().error
                    .message.errorController()
            ),
        )
    }
} catch(e: IOException) {
    DataStateTest.Error(
        cause = ExceptionCause.IO(
            message = e.message.errorController()
        ),
    )
} catch(e: SocketTimeoutException) {
    DataStateTest.Error(
        cause = ExceptionCause.SocketTimeout(
            message = e.message.errorController()
        ),
    )
} catch(e: UnknownHostException) {
    DataStateTest.Error(
        cause = ExceptionCause.UnknownHost(
            message = e.message.errorController()
        ),
    )
} catch(e: ResponseException) {
    when(e) {
        is RedirectResponseException -> {
            DataStateTest.Error(
                cause = ExceptionCause.RedirectResponse(
                    message = e.message.errorController()
                ),
            )
        }
        is ClientRequestException -> {
            DataStateTest.Error(
                cause = ExceptionCause.ClientRequest(
                    message = e.message.errorController()
                ),
            )
        }
        is ServerResponseException -> {
            DataStateTest.Error(
                cause = ExceptionCause.ServerResponse(
                    message = e.message.errorController()
                ),
            )
        }
        else -> {
            DataStateTest.Error(
                cause = ExceptionCause.UnknownException(
                    message = e.message.errorController()
                ),
            )
        }
    }
} catch(e: Exception) {
    DataStateTest.Error(
        cause = ExceptionCause.UnknownException(
            message = e.message.errorController()
        ),
    )
}

/**
 * [flowState] превращает запрос в DataState (flow)
 */
@Suppress("unused")
fun <T: Any> flowState(
    block: suspend () -> T,
) = flow {
    emit(DataStateTest.Loading())
    val response = try {
        DataStateTest.Success(
            data = block(),
        )
    } catch(e: IOException) {
        DataStateTest.Error(
            cause = ExceptionCause.IO(
                message = e.message.errorController()
            ),
        )
    } catch(e: SocketTimeoutException) {
        DataStateTest.Error(
            cause = ExceptionCause.SocketTimeout(
                message = e.message.errorController()
            ),
        )
    } catch(e: UnknownHostException) {
        DataStateTest.Error(
            cause = ExceptionCause.UnknownHost(
                message = e.message.errorController()
            ),
        )
    } catch(e: ResponseException) {
        when(e) {
            is RedirectResponseException -> {
                DataStateTest.Error(
                    cause = ExceptionCause.RedirectResponse(
                        message = e.message.errorController()
                    ),
                )
            }
            is ClientRequestException -> {
                DataStateTest.Error(
                    cause = ExceptionCause.ClientRequest(
                        message = e.message.errorController()
                    ),
                )
            }
            is ServerResponseException -> {
                DataStateTest.Error(
                    cause = ExceptionCause.ServerResponse(
                        message = e.message.errorController()
                    ),
                )
            }
            else -> {
                DataStateTest.Error(
                    cause = ExceptionCause.UnknownException(
                        message = e.message.errorController()
                    ),
                )
            }
        }
    } catch(e: Exception) {
        DataStateTest.Error(
            cause = ExceptionCause.UnknownException(
                message = e.message.errorController()
            ),
        )
    }
    emit(response)
}.flowOn(Dispatchers.IO)

// TODO: Заменять Exception()
//  на Exception(нужное в UI сообщение)
//  в случае необходимости
/**
 * [paginateState] превращает запрос в LoadResult
 */
@Suppress("unused")
suspend fun <T: Any> paginateState(
    block: suspend () -> List<T>,
    loadSize: Int,
    page: Int,
) = try {
    withContext(Dispatchers.IO) {
        val result = block()
        val nextKey = if(result.size < loadSize) null else page + 1
        val prevKey = if(page == 1) null else page - 1
        PagingSource.LoadResult.Page(
            data = block(),
            prevKey = prevKey,
            nextKey = nextKey,
        )
    }
} catch(e: IOException) {
    PagingSource.LoadResult.Error(
        throwable = Exception(),
    )
} catch(e: SocketTimeoutException) {
    PagingSource.LoadResult.Error(
        throwable = Exception(),
    )
} catch(e: UnknownHostException) {
    PagingSource.LoadResult.Error(
        throwable = Exception(),
    )
} catch(e: ResponseException) {
    when(e) {
        is RedirectResponseException -> {
            PagingSource.LoadResult.Error(
                throwable = Exception(),
            )
        }
        is ClientRequestException -> {
            PagingSource.LoadResult.Error(
                throwable = Exception(),
            )
        }
        is ServerResponseException -> {
            PagingSource.LoadResult.Error(
                throwable = Exception(),
            )
        }
        else -> {
            PagingSource.LoadResult.Error(
                throwable = Exception(),
            )
        }
    }
} catch(e: Exception) {
    PagingSource.LoadResult.Error(
        throwable = Exception(),
    )
}