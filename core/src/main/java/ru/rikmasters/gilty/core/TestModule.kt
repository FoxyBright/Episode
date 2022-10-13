package ru.rikmasters.gilty.core

import org.koin.core.module.Module
import org.koin.dsl.module
import ru.rikmasters.gilty.core.module.ModuleDefinition

object TestModule: ModuleDefinition() {

    override fun koin(): Module = module {

    }

    override fun navigation() {

    }
}