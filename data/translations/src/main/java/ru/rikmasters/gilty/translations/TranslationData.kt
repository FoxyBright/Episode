package ru.rikmasters.gilty.translations

import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf
import ru.rikmasters.gilty.core.data.entity.builder.EntitiesBuilder
import ru.rikmasters.gilty.core.module.DataDefinition
import ru.rikmasters.gilty.translations.datasource.remote.TranslationWebSource
import ru.rikmasters.gilty.translations.repository.TranslationRepository

object TranslationData : DataDefinition() {
    override fun EntitiesBuilder.entities() {
    }

    override fun Module.koin() {
        singleOf(::TranslationWebSource)
        singleOf(::TranslationRepository)
    }
}
