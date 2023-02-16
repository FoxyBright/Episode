package ru.rikmasters.gilty.chats

import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf
import ru.rikmasters.gilty.core.data.entity.builder.EntitiesBuilder
import ru.rikmasters.gilty.core.module.DataDefinition

object ChatData: DataDefinition() {
    
    override fun EntitiesBuilder.entities() {
    }
    
    override fun Module.koin() {
        singleOf(::ChatWebSource)
        singleOf(::ChatManager)
    }
}