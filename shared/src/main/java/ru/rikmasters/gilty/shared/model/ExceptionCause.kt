package ru.rikmasters.gilty.shared.model

// TODO: Число наследников будет увеличиваться
//  по мере необходимости выделения отдельной ошибки
/**
 * [ExceptionCause] Причина ошибки.
 * @param[serverMessage] - не null в случаях когда важно показать сообщение ошибки с бэка.
 * @param[defaultMessage] - дефолтное сообщение, null если ошибка не ожидаемая и показывать информацию пользователю не нужно.
 *
 * @property[RedirectResponse] - ошибка с кодом 300
 * @property[ClientRequest] - ошибка с кодом 400
 * @property[ServerResponse] - ошибка с кодом 500
 */
sealed class ExceptionCause(
    val defaultMessage: String? = null,
    val serverMessage: String? = null,
) {
    
    data class IO(
        val message: String,
    ): ExceptionCause(
        serverMessage = message
    )
    
    data class SocketTimeout(
        val message: String,
    ): ExceptionCause(
        serverMessage = message
    )
    
    data class UnknownHost(
        val message: String,
    ): ExceptionCause(
        serverMessage = message
    )
    
    data class RedirectResponse(
        val message: String,
    ): ExceptionCause(
        serverMessage = message
    )
    
    data class ClientRequest(
        val message: String,
    ): ExceptionCause(
        serverMessage = message
    )
    
    data class ServerResponse(
        val message: String,
    ): ExceptionCause(
        serverMessage = message
    )
    
    data class UnknownException(
        val message: String,
    ): ExceptionCause(
        serverMessage = message
    )
}