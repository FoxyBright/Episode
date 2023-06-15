package ru.rikmasters.gilty.translation

import androidx.navigation.NavType
import androidx.navigation.navArgument
import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf
import ru.rikmasters.gilty.core.module.FeatureDefinition
import ru.rikmasters.gilty.core.navigation.DeepNavGraphBuilder
import ru.rikmasters.gilty.meetings.MeetingManager
import ru.rikmasters.gilty.translation.streamer.TranslationStreamerScreen
import ru.rikmasters.gilty.translation.streamer.viewmodel.TranslationViewModel
import ru.rikmasters.gilty.translation.viewer.viewmodel.TranslationViewerViewModel
import ru.rikmasters.gilty.translations.repository.TranslationRepository

object Translation : FeatureDefinition() {
    override fun DeepNavGraphBuilder.navigation() {
        nested("translations","streamer") {
            screen<TranslationViewModel>(
                route = "streamer?id={id}",
                arguments = listOf(
                    navArgument("id"){
                        type = NavType.StringType; defaultValue = ""
                    }
                )
            ) { vm, it ->
                it.arguments?.getString("id")?.let { id ->
                    TranslationStreamerScreen(
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
        singleOf(::TranslationViewModel)
        singleOf(::TranslationViewerViewModel)
    }
}