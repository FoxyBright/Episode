package ru.rikmasters.gilty.login.presentation.ui.categories

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import org.koin.androidx.compose.get
import ru.rikmasters.gilty.core.navigation.NavState
import ru.rikmasters.gilty.shared.model.meeting.DemoShortCategoryModelList

@Composable
fun CategoriesScreen(nav: NavState = get()) {
    CategoriesContent(
        Modifier,
        CategoriesState(DemoShortCategoryModelList, listOf()),
        object : CategoriesCallback {
            override fun onBack() {
                nav.navigate("personal")
            }

            override fun onNext() {
                nav.navigate("permissions")
            }
        })
}