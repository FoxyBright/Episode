package ru.rikmasters.gilty.translation

import androidx.navigation.NavType
import androidx.navigation.navArgument
import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf
import ru.rikmasters.gilty.core.module.FeatureDefinition
import ru.rikmasters.gilty.core.navigation.DeepNavGraphBuilder
import ru.rikmasters.gilty.meetings.MeetingManager
import ru.rikmasters.gilty.translation.viewer.presentation.ui.logic.TranslationViewerScreenLogic
import ru.rikmasters.gilty.translation.viewer.viewmodel.TranslationViewerViewModel
import ru.rikmasters.gilty.translations.repository.TranslationRepository

object TranslationViewer : FeatureDefinition() {
    override fun DeepNavGraphBuilder.navigation() {
        nested("translationviewer","viewer") {
            screen<TranslationViewerViewModel>(
                route = "viewer?id={id}",
                arguments = listOf(
                    navArgument("id"){
                        type = NavType.StringType; defaultValue = ""
                    }
                )
            ) { vm, it ->
                it.arguments?.getString("id")?.let { id ->
                    TranslationViewerScreenLogic(
                        vm = vm,
                        translationId = id
                    )
                }
            }
        }
    }

    override fun Module.koin() {
        singleOf(::TranslationRepository)
        singleOf(::MeetingManager)
        singleOf(::TranslationViewerViewModel)
    }
}