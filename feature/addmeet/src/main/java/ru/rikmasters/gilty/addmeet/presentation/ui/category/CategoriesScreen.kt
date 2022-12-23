package ru.rikmasters.gilty.addmeet.presentation.ui.category

import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import org.koin.androidx.compose.get
import ru.rikmasters.gilty.core.navigation.NavState
import ru.rikmasters.gilty.shared.model.enumeration.CategoriesType

@Composable
fun CategoriesScreen(nav: NavState = get()) {
    var alert by remember { mutableStateOf(false) }
    CategoriesContent(
        Modifier,
        CategoriesState(
            CategoriesType.values().toList(),
            listOf(), alert
        ), object: CategoriesCallback {
            
            override fun onClose() {
                nav.navigateAbsolute("main/meetings")
            }
            
            override fun onCloseAlert(it: Boolean) {
                alert = it
            }
            
            override fun onCategoryClick(category: CategoriesType) {
                nav.navigateAbsolute("addmeet/conditions")
            }
        })
}