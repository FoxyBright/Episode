package ru.rikmasters.gilty.core.module

import ru.rikmasters.gilty.core.log.log
import ru.rikmasters.gilty.core.navigation.DeepNavGraphBuilder

abstract class FeatureDefinition: ModuleDefinition(), Iterable<FeatureDefinition> {

    final override fun iterator(): Iterator<FeatureDefinition> = ModuleIterator(this)

    open fun include(): Set<ModuleDefinition> = emptySet()
    fun includeFeatures() = include().filterIsInstance<FeatureDefinition>()

    internal fun buildNavigation(builder: DeepNavGraphBuilder) {
        builder.navigation()
        includeFeatures().forEach {
            it.buildNavigation(builder)
        }
    }
    abstract fun DeepNavGraphBuilder.navigation()
}