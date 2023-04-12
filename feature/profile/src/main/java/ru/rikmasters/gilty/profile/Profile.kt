package ru.rikmasters.gilty.profile

import androidx.navigation.NavType
import androidx.navigation.navArgument
import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf
import ru.rikmasters.gilty.auth.Auth
import ru.rikmasters.gilty.bottomsheet.viewmodel.ObserverBsViewModel
import ru.rikmasters.gilty.core.module.FeatureDefinition
import ru.rikmasters.gilty.core.navigation.DeepNavGraphBuilder
import ru.rikmasters.gilty.profile.presentation.ui.gallery.CropperScreen
import ru.rikmasters.gilty.profile.presentation.ui.gallery.GalleryScreen
import ru.rikmasters.gilty.profile.presentation.ui.respond.MeetRespondScreen
import ru.rikmasters.gilty.profile.presentation.ui.settings.SettingsScreen
import ru.rikmasters.gilty.profile.presentation.ui.settings.categories.CategoriesScreen
import ru.rikmasters.gilty.profile.presentation.ui.user.UserProfileScreen
import ru.rikmasters.gilty.profile.viewmodel.*
import ru.rikmasters.gilty.profile.viewmodel.bottoms.HiddenBsViewModel
import ru.rikmasters.gilty.profile.viewmodel.settings.SettingsViewModel
import ru.rikmasters.gilty.profile.viewmodel.settings.bottoms.*

object Profile: FeatureDefinition() {
    
    override fun DeepNavGraphBuilder.navigation() {
        
        nested("profile", "main") {
            
            screen<UserProfileViewModel>("main") { vm, _ ->
                UserProfileScreen(vm)
            }
            
            screen<SettingsViewModel>("settings") { vm, _ ->
                SettingsScreen(vm)
            }
            
            screen<GalleryViewModel>(
                "cropper?image={image}", listOf(navArgument("image") {
                    type = NavType.StringType; defaultValue = ""
                })
            ) { vm, stack ->
                stack.arguments?.getString("image")?.let {
                    CropperScreen(vm, it)
                }
            }
            
            screen<RespondsViewModel>(
                "reaction?meetId={meetId}",
                listOf(navArgument("meetId") {
                    type = NavType.StringType
                    defaultValue = ""
                })
            ) { vm, it ->
                it.arguments?.getString("meetId")?.let { meetId ->
                    MeetRespondScreen(vm, meetId)
                }
            }
            
            screen<GalleryViewModel>(
                "gallery?multi={multi}",
                listOf(navArgument("multi") {
                    type = NavType.BoolType; defaultValue = false
                })
            ) { vm, it ->
                it.arguments?.getBoolean("multi")
                    ?.let { multi -> GalleryScreen(vm, multi) }
            }
            
            screen<CategoryViewModel>("categories") { vm, _ ->
                CategoriesScreen(vm)
            }
        }
    }
    
    override fun Module.koin() {
        singleOf(::OrientationBsViewModel)
        singleOf(::UserProfileViewModel)
        singleOf(::ObserverBsViewModel)
        singleOf(::CategoryViewModel)
        singleOf(::SettingsViewModel)
        singleOf(::GenderBsViewModel)
        singleOf(::RespondsViewModel)
        singleOf(::HiddenBsViewModel)
        singleOf(::IconsBsViewModel)
        singleOf(::GalleryViewModel)
        singleOf(::AgeBsViewModel)
    }
    
    override fun include() = setOf(ProfileData, Auth)
}