package ru.rikmasters.gilty.login

import androidx.navigation.NavType
import androidx.navigation.navArgument
import org.koin.core.component.inject
import org.koin.core.module.Module
import org.koin.core.module.dsl.scopedOf
import org.koin.core.module.dsl.singleOf
import ru.rikmasters.gilty.auth.Auth
import ru.rikmasters.gilty.auth.manager.AuthManager
import ru.rikmasters.gilty.auth.manager.RegistrationManager
import ru.rikmasters.gilty.core.app.EntrypointResolver
import ru.rikmasters.gilty.core.module.FeatureDefinition
import ru.rikmasters.gilty.core.navigation.DeepNavGraphBuilder
import ru.rikmasters.gilty.login.presentation.ui.categories.CategoriesScreen
import ru.rikmasters.gilty.login.presentation.ui.code.CodeScreen
import ru.rikmasters.gilty.login.presentation.ui.login.LoginScreen
import ru.rikmasters.gilty.login.presentation.ui.permissions.PermissionsScreen
import ru.rikmasters.gilty.login.presentation.ui.personal.PersonalScreen
import ru.rikmasters.gilty.login.presentation.ui.profile.HiddenPhotoScreen
import ru.rikmasters.gilty.login.presentation.ui.profile.ProfileScreen
import ru.rikmasters.gilty.login.presentation.ui.profile.ProfileSelectPhotoScreen
import ru.rikmasters.gilty.login.viewmodel.*
import ru.rikmasters.gilty.shared.country.CountryManager

object Login: FeatureDefinition() {
    
    private val authManager by inject<AuthManager>()
    private val regManager by inject<RegistrationManager>()
    
    private val authEntrypointResolver = EntrypointResolver {
        if(
            authManager.isAuthorized() &&
            regManager.isUserRegistered()
        ) "main/meetings" else "login"
    }
    
    override fun DeepNavGraphBuilder.navigation() {
        
        screen<LoginViewModel>("login") { vm, _ ->
            LoginScreen(vm)
        }
        
        nested("registration", "code") {
            
            screen<CodeViewModel>("code") { vm, _ ->
                CodeScreen(vm)
            }
            
            screen<ProfileViewModel>(
                "profile?photo={photo}&hp={hp}&jjj",
            ) { vm, it ->
                it.arguments?.getString("photo")?.let { avatar ->
                    it.arguments?.getString("hp")?.let { hiddenPhoto ->
                        ProfileScreen(vm, avatar, hiddenPhoto)
                    }
                }
            }
            
            screen<ProfileViewModel>( /* TODO Этот экран нужен для обрезания фотки под рамкку*/
                "resize?photo={photo}",
                listOf(navArgument("photo") {
                    type = NavType.StringType; defaultValue = ""
                })
            ) { vm, it ->
                it.arguments?.getString("photo")
                    ?.let { avatar -> ProfileScreen(vm, avatar) }
            }
            
            screen("hidden") { HiddenPhotoScreen() }
            
            screen(
                "gallery?multi={multi}",
                listOf(navArgument("multi") {
                    type = NavType.BoolType; defaultValue = false
                })
            ) {
                it.arguments?.getBoolean("multi")
                    ?.let { multi -> ProfileSelectPhotoScreen(multi) }
            }
            
            screen<PersonalViewModel>("personal") { vm, _ ->
                PersonalScreen(vm)
            }
            
            screen("categories") { CategoriesScreen() }
            
            screen<PermissionViewModel>("permissions") { vm, _ ->
                PermissionsScreen(vm)
            }
        }
    }
    
    override fun Module.koin() {
        this@koin.single { authEntrypointResolver }
        singleOf(::CountryManager)
        
        scope<CodeViewModel> {
            scopedOf(::CodeViewModel)
        }
        
        scope<ProfileViewModel> {
            scopedOf(::ProfileViewModel)
        }
        
        scope<PersonalViewModel> {
            scopedOf(::PersonalViewModel)
            scopedOf(::AgeBsViewModel)
        }
        
        scope<PermissionViewModel> {
            scopedOf(::PermissionViewModel)
        }
        
        scope<LoginViewModel> {
            scopedOf(::LoginViewModel)
            scopedOf(::CountryBsViewModel)
        }
    }
    
    override fun include() = setOf(Auth)
}