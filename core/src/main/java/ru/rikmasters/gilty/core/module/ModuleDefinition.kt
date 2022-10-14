package ru.rikmasters.gilty.core.module

import org.koin.core.module.Module
import ru.rikmasters.gilty.core.log.Loggable

abstract class ModuleDefinition: Loggable {

    abstract fun koin(): Module

    abstract fun navigation()
}