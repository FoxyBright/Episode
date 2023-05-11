package ru.rikmasters.gilty

import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf
import ru.rikmasters.gilty.core.module.FeatureDefinition
import ru.rikmasters.gilty.core.navigation.DeepNavGraphBuilder
import ru.rikmasters.gilty.translations.repository.TranslationRepository
import ru.rikmasters.gilty.viewmodel.TranslationViewModel

object Translation : FeatureDefinition() {
    override fun DeepNavGraphBuilder.navigation() {
        TODO("Not yet implemented")
    }

    override fun Module.koin() {
        singleOf(::TranslationRepository)
        singleOf(::TranslationViewModel)
    }
}