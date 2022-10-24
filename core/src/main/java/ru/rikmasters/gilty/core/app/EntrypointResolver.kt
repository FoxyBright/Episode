package ru.rikmasters.gilty.core.app

/**
 *  Интерфейс для определения точки входа навигации в приложение. Должен быть один на приложение
 */
fun interface EntrypointResolver {
    suspend fun resolve(): String
}