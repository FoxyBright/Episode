package ru.rikmasters.gilty.mainscreen.presentation.ui.reaction

import androidx.compose.runtime.Composable
import org.koin.androidx.compose.get
import ru.rikmasters.gilty.core.navigation.NavState
import ru.rikmasters.gilty.shared.model.enumeration.CategoriesType

@Composable
fun ReactionScreen(
    avatar: String,
    categoriesType: CategoriesType,
    nav: NavState = get()
) {
    ReactionContent(avatar, categoriesType)
    { nav.navigationBack() }
}