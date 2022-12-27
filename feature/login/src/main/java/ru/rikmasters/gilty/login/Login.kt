package ru.rikmasters.gilty.login

import androidx.navigation.NavType
import androidx.navigation.navArgument
import org.koin.core.module.Module
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
import ru.rikmasters.gilty.mainscreen.presentation.ui.main.screen.MainScreen

object Login: FeatureDefinition() {
    
    override fun DeepNavGraphBuilder.navigation() {
        
        //TODO Проверка на авторизованность пользователя
        val userLogged = false
        
        screen("authorization") {
            if(userLogged) MainScreen() else LoginScreen()
        }
        
        screen("login"){
            LoginScreen() //TODO для выхода на экран авторизации. Позже убрать
        }
        
        nested("registration", "code") {
            
            screen(
                "profile?photo={photo}&hp={hp}",
                listOf(navArgument("photo") {
                    type = NavType.StringType; defaultValue = ""
                }, navArgument("hp") {
                    type = NavType.StringType; defaultValue = ""
                })
            ) {
                it.arguments?.getString("photo")?.let { avatar ->
                    it.arguments?.getString("hp")?.let { hiddenPhoto ->
                        ProfileScreen(avatar, hiddenPhoto)
                    }
                }
            }
            
            screen( /* TODO Этот экран нужен для обрезания фотки под рамкку*/
                "resize?photo={photo}",
                listOf(navArgument("photo") {
                    type = NavType.StringType; defaultValue = ""
                })
            ) {
                it.arguments?.getString("photo")
                    ?.let { avatar -> ProfileScreen(avatar) }
            }
            
            screen(
                "gallery?multi={multi}",
                listOf(navArgument("multi") {
                    type = NavType.BoolType; defaultValue = false
                })
            ) {
                it.arguments?.getBoolean("multi")
                    ?.let { multi -> ProfileSelectPhotoScreen(multi) }
            }
            
            screen("code") { CodeScreen() }
            screen("hidden") { HiddenPhotoScreen() }
            screen("personal") { PersonalScreen() }
            screen("categories") { CategoriesScreen() }
            screen("permissions") { PermissionsScreen() }
        }
    }
    
    override fun Module.koin() {
        single { EntrypointResolver { "authorization" } }
    }
}