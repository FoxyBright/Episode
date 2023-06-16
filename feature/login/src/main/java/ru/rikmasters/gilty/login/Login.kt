package ru.rikmasters.gilty.login

import androidx.navigation.NavType
import androidx.navigation.navArgument
import org.koin.core.module.Module
import org.koin.core.module.dsl.scopedOf
import org.koin.core.module.dsl.singleOf
import ru.rikmasters.gilty.auth.Auth
import ru.rikmasters.gilty.core.app.EntrypointResolver
import ru.rikmasters.gilty.core.module.FeatureDefinition
import ru.rikmasters.gilty.core.navigation.DeepNavGraphBuilder
import ru.rikmasters.gilty.login.presentation.ui.SplashScreen
import ru.rikmasters.gilty.login.presentation.ui.categories.CategoriesScreen
import ru.rikmasters.gilty.login.presentation.ui.code.CodeScreen
import ru.rikmasters.gilty.login.presentation.ui.gallery.CropperScreen
import ru.rikmasters.gilty.login.presentation.ui.gallery.GalleryScreen
import ru.rikmasters.gilty.login.presentation.ui.hidden.HiddenScreen
import ru.rikmasters.gilty.login.presentation.ui.login.LoginScreen
import ru.rikmasters.gilty.login.presentation.ui.permissions.PermissionsScreen
import ru.rikmasters.gilty.login.presentation.ui.personal.PersonalScreen
import ru.rikmasters.gilty.login.presentation.ui.profile.ProfileScreen
import ru.rikmasters.gilty.login.viewmodel.*
import ru.rikmasters.gilty.login.viewmodel.bottoms.AgeBsViewModel
import ru.rikmasters.gilty.login.viewmodel.bottoms.CountryBsViewModel
import ru.rikmasters.gilty.meetings.MeetingsData
import ru.rikmasters.gilty.shared.country.CountryManager

object Login: FeatureDefinition() {
    
    private val authEntrypointResolver = EntrypointResolver { "splash" }
    
    override fun DeepNavGraphBuilder.navigation() {
        
        screen<SplashViewModel>("splash") { vm, _ ->
            SplashScreen(vm)
        }
        
        screen<LoginViewModel>("login") { vm, _ ->
            LoginScreen(vm)
        }
        
        nested("registration", "code") {
            
            screen<CodeViewModel>("code") { vm, _ ->
                CodeScreen(vm)
            }
            
            screen<ProfileViewModel>("profile") { vm, _ ->
                ProfileScreen(vm)
            }
            
            screen<HiddenViewModel>("hidden") { vm, _ ->
                HiddenScreen(vm)
            }
            
            screen<GalleryViewModel>(
                route = "gallery?multi={multi}",
                arguments = listOf(
                    navArgument("multi") {
                        type = NavType.BoolType
                        defaultValue = false
                    }
                )
            ) { vm, stack ->
                stack.arguments?.getBoolean("multi")?.let {
                    GalleryScreen(vm, it)
                }
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
            
            screen<PersonalViewModel>("personal") { vm, _ ->
                PersonalScreen(vm)
            }
            
            screen<CategoryViewModel>("categories") { vm, _ ->
                CategoriesScreen(vm)
            }
            
            screen<PermissionViewModel>("permissions") { vm, _ ->
                PermissionsScreen(vm)
            }
        }
    }
    
    override fun Module.koin() {
        this@koin.single { authEntrypointResolver }
        singleOf(::GalleryViewModel)
        singleOf(::SplashViewModel)
        singleOf(::CountryManager)
        
        scope<LoginViewModel> {
            scopedOf(::LoginViewModel)
            scopedOf(::CountryBsViewModel)
        }
        
        scope<CategoryViewModel> {
            scopedOf(::CategoryViewModel)
        }
        
        scope<HiddenViewModel> {
            scopedOf(::HiddenViewModel)
        }
        
        scope<CodeViewModel> {
            scopedOf(::CodeViewModel)
        }
        
        scope<ProfileViewModel> {
            singleOf(::ProfileViewModel)
        }
        
        scope<PersonalViewModel> {
            scopedOf(::PersonalViewModel)
            scopedOf(::AgeBsViewModel)
        }
        
        scope<PermissionViewModel> {
            scopedOf(::PermissionViewModel)
        }
    }
    
    override fun include() = setOf(Auth, MeetingsData)
}