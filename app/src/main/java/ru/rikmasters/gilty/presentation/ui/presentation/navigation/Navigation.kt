package ru.rikmasters.gilty.presentation.ui.presentation.navigation

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import ru.rikmasters.gilty.presentation.model.meeting.DemoShortCategoryModelList
import ru.rikmasters.gilty.presentation.ui.presentation.login.CodeEnter
import ru.rikmasters.gilty.presentation.ui.presentation.login.CodeEnterCallback
import ru.rikmasters.gilty.presentation.ui.presentation.login.CodeEnterState
import ru.rikmasters.gilty.presentation.ui.presentation.login.LoginCallback
import ru.rikmasters.gilty.presentation.ui.presentation.login.LoginContent
import ru.rikmasters.gilty.presentation.ui.presentation.login.PermissionsContent
import ru.rikmasters.gilty.presentation.ui.presentation.login.PersonalInfoContent
import ru.rikmasters.gilty.presentation.ui.presentation.login.SelectCategories
import ru.rikmasters.gilty.presentation.ui.presentation.login.SelectCategoriesCallback
import ru.rikmasters.gilty.presentation.ui.presentation.login.SelectCategoriesState
import ru.rikmasters.gilty.presentation.ui.presentation.notification.NotificationsComposePreview
import ru.rikmasters.gilty.presentation.ui.presentation.profile.CreateProfile
import ru.rikmasters.gilty.presentation.ui.presentation.profile.ProfileCallback
import ru.rikmasters.gilty.presentation.ui.presentation.profile.ProfileState

@Composable
@ExperimentalMaterial3Api
fun Navigation() {
    val navController = rememberNavController()
    NavHost(navController, "test") {
        composable("test") { NotificationsComposePreview() }
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
            val lockState = remember { mutableStateOf(false) }
            val name = remember { mutableStateOf("") }
            val description = remember { mutableStateOf("") }
            val profileState = ProfileState(
                name = name.value,
                lockState = lockState.value,
                description = description.value,
                enabled = true
            )
            CreateProfile(profileState, Modifier, object : ProfileCallback {
                override fun onNameChange(text: String) {
                    name.value = text
                }

                override fun onLockClick(state: Boolean) {
                    lockState.value = state
                }

                override fun onDescriptionChange(text: String) {
                    description.value = text
                }

                override fun onBack() {
                    navController.navigate("login")
                }

                override fun onNext() {
                    navController.navigate("personalInformation")
                }
            })
        }

        composable("code") {
            var text by remember { mutableStateOf("") }
            val focuses = remember { Array(4) { FocusRequester() } }
            CodeEnter(CodeEnterState(text, focuses), Modifier, object : CodeEnterCallback {
                override fun onBack() {
                    navController.navigate("login")
                }

                override fun onCodeChange(index: Int, it: String) {
                    if (text.length <= focuses.size) {
                        if (it.length == focuses.size) {
                            text = it
                        } else if (it.length < 2) {
                            if (it == "") {
                                text = text.substring(0, text.lastIndex)
                                if (index - 1 >= 0) focuses[index - 1].requestFocus()
                            } else {
                                text += it
                                if (index + 1 < focuses.size) focuses[index + 1].requestFocus()
                            }
                        }
                    } else text = ""
                    if (text.length == focuses.size) navController.navigate("createProfile")
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