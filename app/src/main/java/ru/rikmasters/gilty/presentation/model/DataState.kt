@file:Suppress("unused")

package ru.rikmasters.gilty.presentation.model

sealed class DataState<out T: Any> {
    
    data class Success<out T: Any>(
        val data: T,
    ): DataState<T>()
    
    data class Loading<out T: Any>(
        val type: Type = Type.MEDIUM,
    ): DataState<T>() {
        
        enum class Type {
            SHORT, MEDIUM, LONG
        }
    }
    
    data class Error<out T: Any>(
        val exception: Exception = Exception("Unknown"),
        val isExpected: Boolean = exception.message == "Unknown",
    ): DataState<T>()
    
    
    val isSuccess: Boolean
        get() = this is Success
    
    val isError: Boolean
        get() = this is Error
    
    val isLoading: Boolean
        get() = this is Loading
    
    inline fun <E: Any?> on(
        success: (T) -> E,
        loading: (Loading.Type) -> E,
        error: (Exception) -> E,
    ): E {
        return when(this) {
            is Success -> success(data)
            is Loading -> loading(type)
            is Error -> if(isExpected)
                error(exception)
            else throw exception
        }
    }
    
    fun <E: Any?> on(
        success: E,
        loading: E,
        error: E,
    ): E = on({ success }, { loading }, { error })
    
    inline fun <E: Any?> onSuccess(
        block: (T) -> E,
    ): E? = if(this is Success) block(data) else null
    
    inline fun <E: Any?> onLoading(
        block: (Loading.Type) -> E,
    ): E? = if(this is Loading) block(type) else null
    
    inline fun <E: Any?> onError(
        block: (Exception) -> E,
    ): E? = if(this is Error)
        if(isExpected)
            block(exception)
        else throw exception
    else null
}