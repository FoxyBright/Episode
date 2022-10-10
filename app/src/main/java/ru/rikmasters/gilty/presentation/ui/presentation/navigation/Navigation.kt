package ru.rikmasters.gilty.presentation.ui.presentation.navigation

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import ru.rikmasters.gilty.presentation.model.meeting.CategoryModel
import ru.rikmasters.gilty.presentation.model.meeting.DemoCategoryModelList
import ru.rikmasters.gilty.presentation.ui.presentation.login.CodeContent
import ru.rikmasters.gilty.presentation.ui.presentation.login.LoginCallback
import ru.rikmasters.gilty.presentation.ui.presentation.login.LoginContent
import ru.rikmasters.gilty.presentation.ui.presentation.login.PermissionsContent
import ru.rikmasters.gilty.presentation.ui.presentation.login.PersonalInfoContent
import ru.rikmasters.gilty.presentation.ui.presentation.login.SelectCategories
import ru.rikmasters.gilty.presentation.ui.presentation.login.SelectCategoriesState
import ru.rikmasters.gilty.presentation.ui.presentation.profile.CreateProfile
import ru.rikmasters.gilty.presentation.ui.presentation.profile.CreateProfileCallback
import ru.rikmasters.gilty.presentation.ui.shared.CategoryItemCallback

@Composable
@ExperimentalMaterial3Api
fun Navigation() {
    val navController = rememberNavController()
    NavHost(navController, "select") {
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
            val selectCategories by remember { mutableStateOf(arrayListOf<CategoryModel>()) }
            SelectCategories(
                Modifier,
                SelectCategoriesState(DemoCategoryModelList, selectCategories),
                object : CategoryItemCallback {
                    override fun onBack() {
                        navController.navigate("personalInformation")
                    }

                    override fun onCategoryClick(category: CategoryModel) {
                        if (selectCategories.contains(category)) selectCategories.remove(category)
                        else selectCategories.add(category)
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

        composable("profile") {
            CreateProfile(Modifier, "", object : CreateProfileCallback {
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