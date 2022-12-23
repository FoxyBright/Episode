package ru.rikmasters.gilty.example

import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf
import ru.rikmasters.gilty.core.module.FeatureDefinition
import ru.rikmasters.gilty.core.module.ModuleDefinition
import ru.rikmasters.gilty.core.navigation.DeepNavGraphBuilder
import ru.rikmasters.gilty.data.example.DataExampleModule

object ExampleModule : FeatureDefinition() {

    override fun DeepNavGraphBuilder.navigation() {
        screen<ExampleViewModel>("myentrypoint") { vm, _ ->
            ExampleContent(vm)
        }
    }

    override fun Module.koin() {
        singleOf(::ExampleViewModel)
    }
    
    override fun include(): Set<ModuleDefinition> =
        setOf(DataExampleModule)
}