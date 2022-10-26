package ru.rikmasters.gilty.login

import org.koin.core.module.Module
import ru.rikmasters.gilty.core.app.EntrypointResolver
import ru.rikmasters.gilty.core.module.FeatureDefinition
import ru.rikmasters.gilty.core.navigation.DeepNavGraphBuilder
import ru.rikmasters.gilty.login.presentation.ui.categories.CategoriesScreen
import ru.rikmasters.gilty.login.presentation.ui.code.CodeScreen
import ru.rikmasters.gilty.login.presentation.ui.login.LoginScreen
import ru.rikmasters.gilty.login.presentation.ui.permissions.PermissionsScreen
import ru.rikmasters.gilty.login.presentation.ui.personal.PersonalScreen
import ru.rikmasters.gilty.login.presentation.ui.profile.ProfileScreen

object Login : FeatureDefinition() {
    override fun DeepNavGraphBuilder.navigation() {

        screen("authorization") { LoginScreen() }

        nested("registration", "code") {
            screen("code") { CodeScreen() }

            screen("profile") { ProfileScreen() }

            screen("personal") { PersonalScreen() }

            screen("categories") { CategoriesScreen() }

            screen("permissions") { PermissionsScreen() }
        }
    }

    override fun Module.koin() {
        single { EntrypointResolver { "authorization" } }
    }
}