package ru.rikmasters.gilty.shared.wrapper

import androidx.paging.PagingSource
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
    ERROR
}

data class ResponseWrapperTest<T: Any?>(
    val status: Status ? = null,
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

        constructor(): this(
            (0), (0), (0),
            (0), (0), (0)
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
        val total: Int,
        val perPage: Int,
        val currentPage: Int,
        val list_page: Int,
        val limit: Int,
        val offset: Int,
    ) {
        
        constructor(): this(
            (0), (0), (0),
            (0), (0), (0)
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

suspend inline fun <reified T> HttpResponse.paginateWrapped(
): Pair<T, ResponseWrapper.Paginator> where T: Any? {
    val response = body<ResponseWrapper<T>>()
    return Pair(response.data, response.paginator!!)
}

suspend inline fun HttpResponse.errorWrapped() =
    body<ErrorResponseWrapper>()

// По неизвестным причинам не приходит status
suspend inline fun <reified T> HttpResponse.wrappedTest(): T
        where T: Any? = body<ResponseWrapperTest<T>>().data

suspend inline fun <reified T> HttpResponse.wrapped(): T
        where T: Any? = body<ResponseWrapper<T>>().dataChecked


/**
 * [coroutinesState] превращает запрос в DataState (suspend)
 */
suspend fun <T : Any> coroutinesState(
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

/**
 * [flowState] превращает запрос в DataState (flow)
 */
fun <T : Any> flowState(
    block: suspend () -> T,
) = flow {
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

// TODO: Заменять Exception() на Exception(нужное в UI сообщение) в случае необходимости
/**
 * [paginateState] превращает запрос в LoadResult
 */
suspend fun <T : Any> paginateState(
    block: suspend () -> List<T>,
    loadSize: Int,
    page: Int,
) = try {
    withContext(Dispatchers.IO) {
        val result = block()
        val nextKey = if (result.size < loadSize) null else page + 1
        val prevKey = if (page == 1) null else page - 1
        PagingSource.LoadResult.Page(
            data = block(),
            prevKey = prevKey,
            nextKey = nextKey,
        )
    }
} catch (e: IOException) {
    PagingSource.LoadResult.Error(
        throwable = Exception(),
    )
} catch (e: SocketTimeoutException) {
    PagingSource.LoadResult.Error(
        throwable = Exception(),
    )
} catch (e: UnknownHostException) {
    PagingSource.LoadResult.Error(
        throwable = Exception(),
    )
} catch (e: ResponseException) {
    when (e) {
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
} catch (e: Exception) {
    PagingSource.LoadResult.Error(
        throwable = Exception(),
    )
}