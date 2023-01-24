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
import ru.rikmasters.gilty.profile.presentation.ui.photo.AvatarScreen
import ru.rikmasters.gilty.profile.presentation.ui.user.UserProfileScreen
import ru.rikmasters.gilty.profile.presentation.ui.photo.gallerey.ProfileSelectPhotoScreen
import ru.rikmasters.gilty.profile.presentation.ui.settings.SettingsScreen
import ru.rikmasters.gilty.profile.presentation.ui.settings.categories.CategoriesScreen
import ru.rikmasters.gilty.profile.viewmodel.*
import ru.rikmasters.gilty.profile.viewmodel.bottoms.*

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
                "avatar?image={image}&hash={hash}",
                listOf(navArgument("image") {
                    type = NavType.StringType; defaultValue = ""
                }, navArgument("hash") {
                    type = NavType.StringType; defaultValue = ""
                })
            ) { vm, it ->
                it.arguments?.getString("image")?.let { image ->
                    it.arguments?.getString("hash")?.let { hash ->
                        AvatarScreen(vm, "$image&hash=$hash")
                    }
                }
            }
            
            screen<GalleryViewModel>(
                "gallery?multi={multi}",
                listOf(navArgument("multi") {
                    type = NavType.BoolType; defaultValue = false
                })
            ) { vm, it ->
                it.arguments?.getBoolean("multi")
                    ?.let { multi -> ProfileSelectPhotoScreen(vm, multi) }
            }
            
            screen("categories") { CategoriesScreen() }
        }
    }
    
    override fun Module.koin() {
        
        singleOf(::AuthManager)
        
        scope<AvatarViewModel> {
            scopedOf(::AvatarViewModel)
        }
        
        scope<GalleryViewModel> {
            scopedOf(::GalleryViewModel)
        }
        
        scope<SettingsViewModel> {
            scopedOf(::SettingsViewModel)
        }
        
        scope<UserProfileViewModel> {
            scopedOf(::UserProfileViewModel)
            scopedOf(::HiddenBsViewModel)
            scopedOf(::ObserverBsViewModel)
            scopedOf(::RespondsBsViewModel)
            scopedOf(::MeetingBsViewModel)
            scopedOf(::ParticipantsBsViewModel)
            scopedOf(::OrganizerBsViewModel)
        }
    }
    
    override fun include() = setOf(Auth)
}