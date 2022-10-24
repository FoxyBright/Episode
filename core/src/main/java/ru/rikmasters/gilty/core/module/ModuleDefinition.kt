package ru.rikmasters.gilty.core.module

import org.koin.core.module.Module
import org.koin.dsl.module
import ru.rikmasters.gilty.core.log.Loggable

sealed class ModuleDefinition: Loggable {

    open val name: String by lazy { this::class.simpleName ?: toString() }
    final override fun logGroup() = name

    internal fun koin() = module { koin() }
    abstract fun Module.koin()
}