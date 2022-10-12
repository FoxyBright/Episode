package ru.rikmasters.core.module

import org.koin.core.module.Module
import org.koin.dsl.module

abstract class ModuleDefinition {

    init {
        val module = module { koinModule() }

    }

    abstract fun Module.koinModule()

    abstract fun navigation()
}
