package ru.rikmasters.gilty.core.env

import kotlinx.coroutines.*
import org.koin.core.KoinApplication
import org.koin.core.component.KoinComponent
import org.koin.core.module.Module
import ru.rikmasters.gilty.core.data.entity.EntitySpecs
import ru.rikmasters.gilty.core.data.entity.interfaces.DomainEntity
import ru.rikmasters.gilty.core.data.entity.interfaces.Entity
import ru.rikmasters.gilty.core.data.entity.interfaces.EntityVariant
import ru.rikmasters.gilty.core.log.Loggable
import ru.rikmasters.gilty.core.module.*
import ru.rikmasters.gilty.core.navigation.DeepNavGraphBuilder
import java.util.Collections
import kotlin.reflect.KClass

class Environment
internal constructor(
    
    private val root: FeatureDefinition,
): Loggable, KoinComponent {
    
    // Работа с модулями
    
    private val businessModules: MutableMap<String, BusinessDefinition> = hashMapOf()
    
    private fun checkAndLoadModules() {
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
    
    // Entity
    
    private val entities = HashMap<KClass<out Entity>, EntitySpecs<*>>()
    
    val specs: MutableCollection<EntitySpecs<*>> =
        Collections.unmodifiableCollection(entities.values)
    
    private fun loadEntities() {
        val list = businessModules.values
            .filterIsInstance<DataDefinition>()
            .distinct()
            .flatMap { it.entitiesBuilder() }
            .map(this::handleEntitySpecs)
        val specs = list.size
        val classes = list.sum()
        logV("Загружен список сущностей (спецификаций: $specs, классов: $classes)")
    }
    
    private fun handleEntitySpecs(specs: EntitySpecs<*>): Int =
        specs.classes.map {
            if(entities.containsKey(it))
                throw IllegalStateException("В списке сущностей повторно упоминается класс ${it.simpleName}")
            entities[it] = specs
        }.size
    
    @Suppress("unused")
    inline fun <reified T: DomainEntity> getEntitySpecsOf(): EntitySpecs<T> {
        return getEntitySpecs(T::class)
    }
    
    @Suppress("UNCHECKED_CAST", "unused")
    @JvmName("getEntitySpecsByVariant")
    fun <T: DomainEntity, V> getEntitySpecs(clazz: KClass<V>): EntitySpecs<T> where V: EntityVariant<T> {
        return entities[clazz] as EntitySpecs<T>
    }
    
    @Suppress("UNCHECKED_CAST")
    @JvmName("getEntitySpecsByDomain")
    fun <T: DomainEntity> getEntitySpecs(clazz: KClass<T>): EntitySpecs<T> {
        return entities[clazz] as EntitySpecs<T>
    }
    
    private val EntitySpecs<*>.classes: List<KClass<out Entity>>
        get() = listOfNotNull(domainClass, dbClass, webClass)
    
    // Корутины
    
    val scope = CoroutineScope(SupervisorJob())
    
    // Переменные среды
    
    private val variables = mutableMapOf<String, Any?>()
    private val subscribers = mutableMapOf<String, MutableList<(Any?) -> Unit>>()
    
    @Suppress("UNCHECKED_CAST")
    operator fun <T: Any> get(key: String): T? = variables[key] as T?
    
    operator fun set(key: String, value: Any?) = set(key, value, true)
    
    fun set(key: String, value: Any?, async: Boolean) = runBlocking {
        logV("Установлена переменная среды $key=$value")
        variables[key] = value
        subscribers[key]?.map {
            async {
                try {
                    it.invoke(value)
                } catch(_: ClassCastException) {
                    logW("Подписчик $it на переменную среды \"$key\" не может принять тип ${value!!::class.qualifiedName}")
                }
            }
        }?.let {
            if(!async) it.awaitAll()
        }
    }
    
    @Suppress("UNCHECKED_CAST", "unused")
    fun <T: Any> subscribe(key: String, block: (T?) -> Unit) {
        val list = subscribers[key]
            ?: mutableListOf<(Any?) -> Unit>().also {
                subscribers[key] = it
            }
        list.add(block as ((Any?) -> Unit))
    }
    
    init {
        checkAndLoadModules()
        loadEntities()
    }
}