package ru.rikmasters.gilty

import org.koin.core.module.Module
import ru.rikmasters.gilty.core.module.FeatureDefinition
import ru.rikmasters.gilty.core.module.ModuleDefinition
import ru.rikmasters.gilty.core.navigation.DeepNavGraphBuilder
import ru.rikmasters.gilty.example.ExampleModule
import ru.rikmasters.gilty.login.Login

object AppModule : FeatureDefinition() {
    override fun Module.koin() {}
    override fun include(): Set<ModuleDefinition> =
        setOf(ExampleModule, Login)

    override fun DeepNavGraphBuilder.navigation() {}
}