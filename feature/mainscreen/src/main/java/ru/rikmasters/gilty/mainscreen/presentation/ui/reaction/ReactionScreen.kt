package ru.rikmasters.gilty.mainscreen.presentation.ui.reaction

import androidx.compose.runtime.Composable
import org.koin.androidx.compose.get
import ru.rikmasters.gilty.core.navigation.NavState

@Composable
fun ReactionScreen(avatar: String, nav: NavState = get()) {
    ReactionContent(avatar) { nav.navigateAbsolute("main/meetings") }
}