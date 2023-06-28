package ru.rikmasters.gilty.translation

import androidx.navigation.NavType
import androidx.navigation.navArgument
import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf
import ru.rikmasters.gilty.core.module.FeatureDefinition
import ru.rikmasters.gilty.core.navigation.DeepNavGraphBuilder
import ru.rikmasters.gilty.meetings.MeetingManager
import ru.rikmasters.gilty.translation.streamer.model.StreamerFacing
import ru.rikmasters.gilty.translation.streamer.ui.TranslationStreamerScreen
import ru.rikmasters.gilty.translation.streamer.viewmodel.TranslationStreamerViewModel
import ru.rikmasters.gilty.translation.viewer.viewmodel.TranslationViewerViewModel
import ru.rikmasters.gilty.translations.repository.TranslationRepository

object Translation : FeatureDefinition() {
    override fun DeepNavGraphBuilder.navigation() {
        nested("translations","streamer") {
            screen<TranslationStreamerViewModel>(
                route = "streamer?id={id},facing={facing}",
                arguments = listOf(
                    navArgument("id"){
                        type = NavType.StringType; defaultValue = ""
                    },
                    navArgument("facing") {
                        type = NavType.StringType; defaultValue = "front"
                    }
                )
            ) { vm, it ->
                it.arguments?.getString("id")?.let { id ->
                    val facingString = it.arguments?.getString("facing")
                    val facing = if (facingString == "back") StreamerFacing.BACK else StreamerFacing.FRONT
                    TranslationStreamerScreen(
                        vm = vm,
                        translationId = id,
                        selectedFacing = facing
                    )
                }
            }
        }
    }

    override fun Module.koin() {
        singleOf(::TranslationRepository)
        singleOf(::MeetingManager)
        singleOf(::TranslationStreamerViewModel)
        singleOf(::TranslationViewerViewModel)
    }
}