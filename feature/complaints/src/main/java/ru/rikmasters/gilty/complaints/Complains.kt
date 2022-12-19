package ru.rikmasters.gilty.complaints

import org.koin.core.module.Module
import ru.rikmasters.gilty.complaints.presentation.ui.ComplainsContent
import ru.rikmasters.gilty.core.module.FeatureDefinition
import ru.rikmasters.gilty.core.navigation.DeepNavGraphBuilder
import ru.rikmasters.gilty.shared.model.meeting.DemoFullMeetingModel

object Complains : FeatureDefinition() {

    override fun DeepNavGraphBuilder.navigation() {
        screen("complains") { ComplainsContent(DemoFullMeetingModel){} }
    }

    override fun Module.koin() {}
}