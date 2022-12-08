package ru.rikmasters.gilty.data.ktor

import org.koin.core.module.Module
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.singleOf
import ru.rikmasters.gilty.core.data.source.WebSource
import ru.rikmasters.gilty.core.module.DomainDefinition

object Ktor: DomainDefinition() {
    
    override fun Module.koin() {
        singleOf(::KtorSource) {
            bind<WebSource>()
        }
    }
}