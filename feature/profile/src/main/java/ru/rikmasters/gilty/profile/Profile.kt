package ru.rikmasters.gilty.profile

import org.koin.core.module.Module
import org.koin.core.module.dsl.scopedOf
import org.koin.core.module.dsl.singleOf
import ru.rikmasters.gilty.auth.manager.AuthManager
import ru.rikmasters.gilty.core.module.FeatureDefinition
import ru.rikmasters.gilty.core.navigation.DeepNavGraphBuilder
import ru.rikmasters.gilty.profile.presentation.ui.organizer.photo.AvatarScreen
import ru.rikmasters.gilty.profile.presentation.ui.user.HiddenPhotoScreen
import ru.rikmasters.gilty.profile.presentation.ui.user.UserProfileScreen
import ru.rikmasters.gilty.profile.presentation.ui.user.settings.SettingsScreen
import ru.rikmasters.gilty.profile.presentation.ui.user.settings.categories.CategoriesScreen
import ru.rikmasters.gilty.profile.viewmodel.SettingsViewModel

object Profile: FeatureDefinition() {
    
    override fun DeepNavGraphBuilder.navigation() {
        nested("profile", "main") {
            
            screen("main") { UserProfileScreen() }
            
            screen<SettingsViewModel>("settings") { vm, _ ->
                SettingsScreen(vm)
            }
            
            screen("avatar") { AvatarScreen() }
            
            screen("hidden") { HiddenPhotoScreen() }
            
            screen("categories") { CategoriesScreen() }
        }
    }
    
    override fun Module.koin() {
        
        singleOf(::AuthManager)
        
        scope<SettingsViewModel> {
            scopedOf(::SettingsViewModel)
        }
    }
}