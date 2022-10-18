package ru.rikmasters.gilty.core.env

import android.content.Context
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.runBlocking
import org.koin.core.KoinApplication
import ru.rikmasters.gilty.core.log.Loggable
import ru.rikmasters.gilty.core.module.FeatureDefinition
import ru.rikmasters.gilty.core.module.ModuleDefinition
import java.lang.ClassCastException

class Environment
internal constructor(
    context: Context,
    root: FeatureDefinition
): Loggable {

    // Работа с модулями

    private val modules = mutableSetOf<ModuleDefinition>()

    private fun includeRecursive(node: ModuleDefinition) {
        if(!modules.add(node))
            throw IllegalStateException("Модуль ${node.name} дублируется. Цикличная зависимость?")
        else
            logV("Добавлен модуль ${node.name}")
        node.include().forEach(::includeRecursive)
    }

    init {
        includeRecursive(root)
    }

    // Инициализация Koin

    private fun collectKoinModules() =
        modules.map { it.koin() }

    internal fun onKoinStarted(koin: KoinApplication) {
        logV("Koin инициализирован")
        koin.modules(collectKoinModules())
    }

    // Корутины

    val scope = CoroutineScope(SupervisorJob())

    // Переменные среды

    private val variables = mutableMapOf<String, Any?>()
    private val subscribers = mutableMapOf<String, MutableList<(Any?) -> Unit>>()

    @Suppress("UNCHECKED_CAST")
    operator fun <T: Any> get(key: String): T? = variables[key] as T?

    operator fun set(key: String, value: Any?) = set(key, value, true)

    fun set(key: String, value: Any?, async: Boolean) = runBlocking {
        logV("Set $key $value")
        variables[key] = value
        subscribers[key]?.map { async {
            try {
                it.invoke(value)
            } catch(_: ClassCastException) {
                logW("Подписчик $it на переменную среды \"$key\" не может принять тип ${value!!::class.qualifiedName}")
            }
        } }?.let {
            if(!async) it.awaitAll()
        }
        logV("Set $key $value end")
    }

    @Suppress("UNCHECKED_CAST")
    fun <T: Any> subscribe(key: String, block: (T?) -> Unit) {
        val list = subscribers[key]
            ?: mutableListOf<(Any?) -> Unit>().also {
                subscribers[key] = it
            }
        list.add(block as ((Any?) -> Unit))
    }
}