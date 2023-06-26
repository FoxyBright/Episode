package ru.rikmasters.gilty.profile

import androidx.navigation.NavType
import androidx.navigation.navArgument
import org.koin.core.module.Module
import org.koin.core.module.dsl.factoryOf
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
import ru.rikmasters.gilty.profile.viewmodel.settings.bottoms.AgeBsViewModel
import ru.rikmasters.gilty.profile.viewmodel.settings.bottoms.GenderBsViewModel
import ru.rikmasters.gilty.profile.viewmodel.settings.bottoms.IconsBsViewModel
import ru.rikmasters.gilty.profile.viewmodel.settings.bottoms.OrientationBsViewModel

object Profile : FeatureDefinition() {

    override fun DeepNavGraphBuilder.navigation() {

        nested("profile", "main") {

            screen<UserProfileViewModel>(
                route = "main",
            ) { vm, it ->
                val closePopUp = it.savedStateHandle.get<Boolean>("closePopUp") ?: false
                val update = it.savedStateHandle.get<Boolean>("update") ?: false
                UserProfileScreen(vm, update, closePopUp)
            }

            screen<AlbumDetailsViewModel>(
                route = "album?id={albumId}",
                arguments = listOf(
                    navArgument("albumId") {
                        type = NavType.IntType
                        defaultValue = 0
                    }
                )
            ) { vm, stack ->
                stack.arguments
                    ?.getInt("albumId")
                    ?.let { AlbumDetailsScreen(vm, it) }
            }

            screen<SettingsViewModel>("settings") { vm, _ ->
                SettingsScreen(vm)
            }

            screen<GalleryViewModel>(
                route = "cropper?image={image}",
                arguments = listOf(navArgument("image") {
                    type = NavType.StringType; defaultValue = ""
                })
            ) { vm, stack ->
                stack.arguments
                    ?.getString("image")
                    ?.let { CropperScreen(vm, it) }
            }

            screen<GalleryViewModel>(
                route = "gallery?multi={multi}&dest={dest}",
                arguments = listOf(
                    navArgument("multi") {
                        type = NavType.BoolType
                        defaultValue = false
                    },
                    navArgument("dest") {
                        type = NavType.StringType
                        defaultValue = ""
                    }
                )
            ) { vm, backStack ->
                val closePopUp = backStack.savedStateHandle.get<Boolean>("closePopUp") ?: false
                val arg = backStack.arguments
                arg?.getBoolean("multi")
                    ?.let { multi ->
                        arg.getString("dest")
                            ?.let { dest ->
                                GalleryScreen(vm, multi, dest, closePopUp)
                            }
                    }
            }

            screen<CategoryViewModel>("categories") { vm, _ ->
                CategoriesScreen(vm)
            }

            screen<HiddenViewModel>(
                route = "hidden?update={update}",
                arguments = listOf(
                    navArgument("update") {
                        type = NavType.BoolType
                        defaultValue = false
                    })
            ) { vm, it ->
                it.arguments
                    ?.getBoolean("update")
                    ?.let { update ->
                        HiddenBsScreen(vm, update)
                    }
            }
        }
    }

    override fun Module.koin() {
        singleOf(::OrientationBsViewModel)
        singleOf(::AlbumDetailsViewModel)
        singleOf(::UserProfileViewModel)
        singleOf(::ObserverBsViewModel)
        singleOf(::CategoryViewModel)
        singleOf(::SettingsViewModel)
        singleOf(::GenderBsViewModel)
        singleOf(::IconsBsViewModel)
        singleOf(::GalleryViewModel)
        factoryOf(::HiddenViewModel)
        singleOf(::AgeBsViewModel)
    }

    override fun include() = setOf(
        ProfileData, Auth
    )
}