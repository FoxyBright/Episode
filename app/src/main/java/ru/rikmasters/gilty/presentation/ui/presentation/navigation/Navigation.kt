package ru.rikmasters.gilty.presentation.ui.presentation.navigation

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import ru.rikmasters.gilty.presentation.model.meeting.DemoShortCategoryModelList
import ru.rikmasters.gilty.presentation.ui.presentation.login.CodeContent
import ru.rikmasters.gilty.presentation.ui.presentation.login.LoginCallback
import ru.rikmasters.gilty.presentation.ui.presentation.login.LoginContent
import ru.rikmasters.gilty.presentation.ui.presentation.login.PermissionsContent
import ru.rikmasters.gilty.presentation.ui.presentation.login.PersonalInfoContent
import ru.rikmasters.gilty.presentation.ui.presentation.login.SelectCategories
import ru.rikmasters.gilty.presentation.ui.presentation.login.SelectCategoriesCallback
import ru.rikmasters.gilty.presentation.ui.presentation.login.SelectCategoriesState
import ru.rikmasters.gilty.presentation.ui.presentation.profile.Profile
import ru.rikmasters.gilty.presentation.ui.presentation.profile.ProfileCallback
import ru.rikmasters.gilty.presentation.ui.presentation.profile.ProfileState

@Composable
@ExperimentalMaterial3Api
fun Navigation() {
    val navController = rememberNavController()
    NavHost(navController, "test") {
        composable("test") { }
        composable("personalInformation") {
            PersonalInfoContent(object : NavigationInterface {
                override fun onBack() {
                    navController.navigate("profile")
                }

                override fun onNext() {
                    navController.navigate("select")
                }
            })
        }

        composable("select") {
            SelectCategories(
                Modifier,
                SelectCategoriesState(DemoShortCategoryModelList, listOf()),
                object : SelectCategoriesCallback {
                    override fun onBack() {
                        navController.navigate("personalInformation")
                    }

                    override fun onNext() {
                        navController.navigate("permissions")
                    }
                })
        }

        composable("permissions") {
            PermissionsContent(Modifier, object : NavigationInterface {
                override fun onBack() {
                    navController.navigate("select")
                }
            })
        }

        composable("createProfile") {
            Profile(ProfileState(), Modifier, object : ProfileCallback {
                override fun onBack() {
                    navController.navigate("login")
                }

                override fun onNext() {
                    navController.navigate("personalInformation")
                }
            })
        }

        composable("code") {
            CodeContent(object : NavigationInterface {
                override fun onNext() {
                    navController.navigate("profile")
                }

                override fun onBack() {
                    navController.navigate("login")
                }
            })
        }

        composable("login") {
            LoginContent(
                Modifier,
                object : LoginCallback {
                    override fun onNext() {
                        navController.navigate("code")
                    }
                })
        }
    }
}