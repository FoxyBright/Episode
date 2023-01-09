package ru.rikmasters.gilty.profile

import org.koin.core.module.Module
import ru.rikmasters.gilty.core.module.FeatureDefinition
import ru.rikmasters.gilty.core.navigation.DeepNavGraphBuilder
import ru.rikmasters.gilty.profile.presentation.ui.organizer.photo.AvatarScreen
import ru.rikmasters.gilty.profile.presentation.ui.user.HiddenPhotoScreen
import ru.rikmasters.gilty.profile.presentation.ui.user.UserProfileScreen
import ru.rikmasters.gilty.profile.presentation.ui.user.settings.SettingsScreen
import ru.rikmasters.gilty.profile.presentation.ui.user.settings.categories.CategoriesScreen

object Profile : FeatureDefinition() {
    override fun DeepNavGraphBuilder.navigation() {
        nested("profile", "main") {
            screen("main") { UserProfileScreen() }
            screen("settings") { SettingsScreen() }
            screen("avatar") { AvatarScreen() }
            screen("hidden") { HiddenPhotoScreen() }
            screen("categories") { CategoriesScreen() }
        }
    }

    override fun Module.koin() {}
}