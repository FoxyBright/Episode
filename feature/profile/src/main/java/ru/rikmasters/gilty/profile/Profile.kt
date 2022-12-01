package ru.rikmasters.gilty.profile

import org.koin.core.module.Module
import ru.rikmasters.gilty.core.module.FeatureDefinition
import ru.rikmasters.gilty.core.navigation.DeepNavGraphBuilder
import ru.rikmasters.gilty.profile.presentation.ui.mymeetings.MyMeetingScreen
import ru.rikmasters.gilty.profile.presentation.ui.user.UserProfileScreen
import ru.rikmasters.gilty.profile.presentation.ui.user.settings.SettingsScreen
import ru.rikmasters.gilty.profile.presentation.ui.user.settings.categories.CategoriesScreen

object Profile : FeatureDefinition() {
    override fun DeepNavGraphBuilder.navigation() {
        nested("profile", "main") {
            screen("main") { UserProfileScreen() }
            screen("meeting") { MyMeetingScreen() }
            screen("settings") { SettingsScreen() }
            screen("categories") { CategoriesScreen() }
        }
    }

    override fun Module.koin() {}
}