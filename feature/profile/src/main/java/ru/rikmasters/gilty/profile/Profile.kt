package ru.rikmasters.gilty.profile

import androidx.navigation.NavType
import androidx.navigation.navArgument
import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf
import ru.rikmasters.gilty.auth.Auth
import ru.rikmasters.gilty.bottomsheet.viewmodel.ObserverBsViewModel
import ru.rikmasters.gilty.core.module.FeatureDefinition
import ru.rikmasters.gilty.core.navigation.DeepNavGraphBuilder
import ru.rikmasters.gilty.profile.presentation.ui.album_details.AlbumDetailsScreen
import ru.rikmasters.gilty.profile.presentation.ui.gallery.CropperScreen
import ru.rikmasters.gilty.profile.presentation.ui.gallery.GalleryScreen
import ru.rikmasters.gilty.profile.presentation.ui.gallery.hidden.HiddenBsScreen
import ru.rikmasters.gilty.profile.presentation.ui.settings.SettingsScreen
import ru.rikmasters.gilty.profile.presentation.ui.settings.categories.CategoriesScreen
import ru.rikmasters.gilty.profile.presentation.ui.user.UserProfileScreen
import ru.rikmasters.gilty.profile.viewmodel.AlbumDetailsViewModel
import ru.rikmasters.gilty.profile.viewmodel.CategoryViewModel
import ru.rikmasters.gilty.profile.viewmodel.GalleryViewModel
import ru.rikmasters.gilty.profile.viewmodel.UserProfileViewModel
import ru.rikmasters.gilty.profile.viewmodel.bottoms.HiddenViewModel
import ru.rikmasters.gilty.profile.viewmodel.settings.SettingsViewModel
import ru.rikmasters.gilty.profile.viewmodel.settings.bottoms.*

object Profile: FeatureDefinition() {
    
    override fun DeepNavGraphBuilder.navigation() {
        
        nested("profile", "main") {
            
            screen<UserProfileViewModel>("main") { vm, _ ->
                UserProfileScreen(vm)
            }

            screen<AlbumDetailsViewModel>("album?id={albumId}", listOf(navArgument("albumId"){
                type = NavType.IntType; defaultValue = 0
            })){ vm, stack->
                stack.arguments?.getInt("albumId")?.let {
                    AlbumDetailsScreen(vm, it)
                }
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

            screen<HiddenViewModel>("hidden"){ vm, _->
                HiddenBsScreen(vm = vm)
            }
        }
    }
    
    override fun Module.koin() {
        singleOf(::OrientationBsViewModel)
        singleOf(::UserProfileViewModel)
        singleOf(::AlbumDetailsViewModel)
        singleOf(::ObserverBsViewModel)
        singleOf(::CategoryViewModel)
        singleOf(::SettingsViewModel)
        singleOf(::GenderBsViewModel)
        singleOf(::HiddenViewModel)
        singleOf(::IconsBsViewModel)
        singleOf(::GalleryViewModel)
        singleOf(::AgeBsViewModel)
    }
    
    override fun include() = setOf(ProfileData, Auth)
}