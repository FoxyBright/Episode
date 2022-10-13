package ru.rikmasters.gilty.example

import org.koin.core.module.Module
import org.koin.dsl.module
import ru.rikmasters.gilty.core.module.ModuleDefinition

object ExampleModule: ModuleDefinition() {

    override fun koin(): Module {
        logV("Koin function in ExampleModule")
        return module {

        }
    }

    override fun navigation() {
        logV("Navigation function in ExampleModule")
    }
}