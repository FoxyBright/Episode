package ru.rikmasters.gilty.core.env

import android.content.Context
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.runBlocking
import org.koin.core.KoinApplication
import org.koin.core.component.KoinComponent
import org.koin.core.module.Module
import ru.rikmasters.gilty.core.log.Loggable
import ru.rikmasters.gilty.core.module.BusinessDefinition
import ru.rikmasters.gilty.core.module.FeatureDefinition
import ru.rikmasters.gilty.core.module.ModuleDefinition
import ru.rikmasters.gilty.core.navigation.DeepNavGraphBuilder

class Environment
internal constructor(
    context: Context,
    private val root: FeatureDefinition
): Loggable, KoinComponent {

    // Работа с модулями

    private val businessModules: MutableMap<String, BusinessDefinition> = hashMapOf()

    private fun checkModules() {
        val modules = mutableSetOf<ModuleDefinition>()
        root.forEach { module ->
            if(!modules.add(module))
                throw IllegalStateException("Модуль ${module.name} дублируется. Циклическая зависимость?")
            else
                logV("Обнаружен модуль ${module.name}")
            module.include().filterIsInstance<BusinessDefinition>().let { list ->
                if(list.isEmpty())
                    logW("В модуле ${module.name} не указаны зависимости от бизнес-модулей")
                list.forEach {
                    if(businessModules.put(it.name, it) == null)
                        logV("Обнаружен бизнес-модуль ${it.name}")
                }
            }
        }
    }

    init { checkModules() }

    fun buildNavigation(builder: DeepNavGraphBuilder) = root.buildNavigation(builder)

    // Koin

    private fun collectKoinModules() =
        (root + businessModules.values).map { it.koin() }

    internal fun onKoinStarted(koin: KoinApplication) {
        koin.modules(collectKoinModules())
        logV("Koin инициализирован")
    }

    fun loadModules(vararg modules: Module, reason: String? = null, allowOverride: Boolean = true) {
        getKoin().loadModules(modules.asList(), allowOverride)
        logV("Koin модули загружены: ${modules.size} (${reason ?: "неизвестные модули"})")
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