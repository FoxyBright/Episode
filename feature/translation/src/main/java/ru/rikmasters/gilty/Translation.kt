package ru.rikmasters.gilty

import androidx.navigation.NavType
import androidx.navigation.navArgument
import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf
import ru.rikmasters.gilty.core.module.FeatureDefinition
import ru.rikmasters.gilty.core.navigation.DeepNavGraphBuilder
import ru.rikmasters.gilty.presentation.ui.TestTranslationScreen
import ru.rikmasters.gilty.translations.repository.TranslationRepository
import ru.rikmasters.gilty.viewmodel.TranslationViewModel

object Translation : FeatureDefinition() {
    override fun DeepNavGraphBuilder.navigation() {
        nested("translations","main") {
            screen<TranslationViewModel>(
                route = "main",
                arguments = listOf(
                    navArgument("id"){
                        type = NavType.StringType; defaultValue = ""
                    }
                )
            ) { vm, it ->
                it.arguments?.getString("id")?.let { id ->
                    TestTranslationScreen(
                        vm = vm,
                        translationId = id
                    )
                }
            }
        }
    }

    override fun Module.koin() {
        singleOf(::TranslationRepository)
        singleOf(::TranslationViewModel)
    }
}