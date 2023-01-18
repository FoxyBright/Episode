package ru.rikmasters.gilty.profile

import androidx.navigation.NavType
import androidx.navigation.navArgument
import org.koin.core.module.Module
import org.koin.core.module.dsl.scopedOf
import org.koin.core.module.dsl.singleOf
import ru.rikmasters.gilty.auth.Auth
import ru.rikmasters.gilty.auth.manager.AuthManager
import ru.rikmasters.gilty.core.module.FeatureDefinition
import ru.rikmasters.gilty.core.navigation.DeepNavGraphBuilder
import ru.rikmasters.gilty.profile.presentation.ui.organizer.photo.AvatarScreen
import ru.rikmasters.gilty.profile.presentation.ui.user.HiddenScreen
import ru.rikmasters.gilty.profile.presentation.ui.user.UserProfileScreen
import ru.rikmasters.gilty.profile.presentation.ui.user.settings.SettingsScreen
import ru.rikmasters.gilty.profile.presentation.ui.user.settings.categories.CategoriesScreen
import ru.rikmasters.gilty.profile.viewmodel.*

object Profile: FeatureDefinition() {
    
    override fun DeepNavGraphBuilder.navigation() {
        nested("profile", "main") {
            
            screen<UserProfileViewModel>("main") { vm, _ ->
                UserProfileScreen(vm)
            }
            
            screen<SettingsViewModel>("settings") { vm, _ ->
                SettingsScreen(vm)
            }
            
            screen<AvatarViewModel>(
                "avatar?image={image}",
                listOf(navArgument("image") {
                    type = NavType.StringType; defaultValue = ""
                })
            ) { vm, it ->
                it.arguments?.getString("image")?.let { image ->
                    AvatarScreen(vm, image)
                }
            }
            
            screen<HiddenViewModel>("hidden") { vm, _ ->
                HiddenScreen(vm)
            }
            
            screen("categories") { CategoriesScreen() }
        }
    }
    
    override fun Module.koin() {
        
        singleOf(::AuthManager)
        
        scope<HiddenViewModel> {
            scopedOf(::HiddenViewModel)
        }
        
        scope<AvatarViewModel> {
            scopedOf(::AvatarViewModel)
        }
        
        scope<SettingsViewModel> {
            scopedOf(::SettingsViewModel)
        }
        
        scope<UserProfileViewModel> {
            scopedOf(::UserProfileViewModel)
        }
    }
    
    override fun include() = setOf(Auth)
}