package ru.rikmasters.gilty.mainscreen.presentation.ui.reaction

import androidx.compose.runtime.Composable
import org.koin.androidx.compose.get
import ru.rikmasters.gilty.core.navigation.NavState
import ru.rikmasters.gilty.shared.model.meeting.CategoryModel

@Composable
fun ReactionScreen(
    avatar: String,
    categoryModel: CategoryModel,
    nav: NavState = get()
) {
    ReactionContent(avatar, categoryModel)
    { nav.navigationBack() }
}