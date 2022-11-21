package ru.rikmasters.gilty.core.module

import ru.rikmasters.gilty.core.data.builder.EntitiesBuilder

abstract class DataDefinition: BusinessDefinition() {
    
    
    internal fun entitiesBuilder() = EntitiesBuilder().apply { entities() }.build()
    abstract fun EntitiesBuilder.entities()
}