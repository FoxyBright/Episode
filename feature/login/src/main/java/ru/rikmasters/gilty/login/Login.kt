package ru.rikmasters.gilty.login

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import org.koin.androidx.compose.get
import org.koin.core.module.Module
import ru.rikmasters.gilty.core.app.EntrypointResolver
import ru.rikmasters.gilty.core.module.FeatureDefinition
import ru.rikmasters.gilty.core.navigation.DeepNavGraphBuilder
import ru.rikmasters.gilty.core.navigation.NavState
import ru.rikmasters.gilty.login.presentation.ui.CodeEnter
import ru.rikmasters.gilty.login.presentation.ui.CodeEnterCallback
import ru.rikmasters.gilty.login.presentation.ui.CodeEnterState
import ru.rikmasters.gilty.login.presentation.ui.CreateProfile
import ru.rikmasters.gilty.login.presentation.ui.LoginCallback
import ru.rikmasters.gilty.login.presentation.ui.LoginContent
import ru.rikmasters.gilty.login.presentation.ui.PermissionsContent
import ru.rikmasters.gilty.login.presentation.ui.PersonalInfoContent
import ru.rikmasters.gilty.login.presentation.ui.SelectCategories
import ru.rikmasters.gilty.login.presentation.ui.SelectCategoriesCallback
import ru.rikmasters.gilty.login.presentation.ui.SelectCategoriesState
import ru.rikmasters.gilty.shared.NavigationInterface
import ru.rikmasters.gilty.shared.model.meeting.DemoShortCategoryModelList
import ru.rikmasters.gilty.shared.shared.ProfileCallback
import ru.rikmasters.gilty.shared.shared.ProfileState

object Login : FeatureDefinition() {
    override fun DeepNavGraphBuilder.navigation() {
        screen("authorization") {
            val nav = get<NavState>()
            LoginContent(Modifier, object : LoginCallback {
                override fun onNext() {
                    nav.navigate("registration/code")
                }
            })
        }

        nested("registration", "code") {
            screen("code") {
                val nav = get<NavState>()
                var text by remember { mutableStateOf("") }
                val focuses = remember { Array(4) { FocusRequester() } }
                CodeEnter(CodeEnterState(text, focuses), Modifier, object : CodeEnterCallback {
                    override fun onBack() {
                        nav.navigateAbsolute("authorization")
                    }

                    override fun onCodeChange(index: Int, it: String) {
                        if (text.length <= focuses.size) {
                            if (it.length == focuses.size) {
                                text = it
                            } else if (it.length < 2) {
                                if (it == "") {
                                    text = text.substring(0, text.lastIndex)
                                    if (index - 1 >= 0)
                                        focuses[index - 1].requestFocus()
                                } else {
                                    text += it
                                    if (index + 1 < focuses.size)
                                        focuses[index + 1].requestFocus()
                                }
                            }
                        } else text = ""
                        if (text.length == focuses.size)
                            nav.navigate("createProfile")
                    }
                })
            }

            screen("createProfile") {
                val nav = get<NavState>()
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
                        nav.navigateAbsolute("authorization")
                    }

                    override fun onNext() {
                        nav.navigate("personalInformation")
                    }
                })
            }

            screen("personalInformation") {
                val nav = get<NavState>()
                PersonalInfoContent(object : NavigationInterface {
                    override fun onBack() {
                        nav.navigate("createProfile")
                    }

                    override fun onNext() {
                        nav.navigate("selectCategories")
                    }
                })
            }

            screen("selectCategories") {
                val nav = get<NavState>()
                SelectCategories(
                    Modifier,
                    SelectCategoriesState(DemoShortCategoryModelList, listOf()),
                    object : SelectCategoriesCallback {
                        override fun onBack() {
                            nav.navigate("personalInformation")
                        }

                        override fun onNext() {
                            nav.navigate("permissions")
                        }
                    })
            }

            screen("permissions") {
                val nav = get<NavState>()
                PermissionsContent(Modifier, object : NavigationInterface {
                    override fun onBack() {
                        nav.navigate("selectCategories")
                    }
                })
            }

        }
    }

    override fun Module.koin() {
        single { EntrypointResolver { "authorization" } }
    }
}