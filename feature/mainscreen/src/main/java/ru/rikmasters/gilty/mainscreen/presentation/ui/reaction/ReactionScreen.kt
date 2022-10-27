package ru.rikmasters.gilty.mainscreen.presentation.ui.reaction

import androidx.compose.runtime.Composable
import org.koin.androidx.compose.get
import ru.rikmasters.gilty.core.navigation.NavState
import ru.rikmasters.gilty.shared.model.profile.DemoAvatarModel
import ru.rikmasters.gilty.shared.theme.base.GiltyTheme

@Composable
fun ReactionScreen(avatar: String, nav: NavState = get()) {
    ReactionContent(avatar) { nav.navigateAbsolute("main/meetings") }
}