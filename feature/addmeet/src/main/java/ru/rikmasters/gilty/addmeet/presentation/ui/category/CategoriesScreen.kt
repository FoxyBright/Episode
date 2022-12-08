package ru.rikmasters.gilty.addmeet.presentation.ui.category

import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import org.koin.androidx.compose.get
import ru.rikmasters.gilty.core.navigation.NavState
import ru.rikmasters.gilty.shared.model.meeting.DemoShortCategoryModelList
import ru.rikmasters.gilty.shared.model.meeting.ShortCategoryModel

@Composable
fun CategoriesScreen(nav: NavState = get()) {
    var alert by remember { mutableStateOf(false) }
    CategoriesContent(
        Modifier,
        CategoriesState(DemoShortCategoryModelList, listOf(), alert),
        object: CategoriesCallback {
            
            override fun onClose() {
                nav.navigateAbsolute("main/meetings")
            }
            
            override fun onCloseAlert(it: Boolean) {
                alert = it
            }
            
            override fun onCategoryClick(category: ShortCategoryModel) {
                nav.navigateAbsolute("addmeet/conditions")
            }
        })
}