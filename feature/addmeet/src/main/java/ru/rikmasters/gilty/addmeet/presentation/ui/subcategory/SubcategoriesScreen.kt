package ru.rikmasters.gilty.addmeet.presentation.ui.subcategory

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import kotlinx.coroutines.launch
import org.koin.androidx.compose.get
import ru.rikmasters.gilty.addmeet.viewmodel.SubcategoryViewModel
import ru.rikmasters.gilty.core.navigation.NavState
import ru.rikmasters.gilty.shared.model.meeting.CategoryModel

@Composable
fun SubcategoriesScreen(vm: SubcategoryViewModel) {

    val scope = rememberCoroutineScope()
    val nav = get<NavState>()

    val subcategories by vm.subcategories.collectAsState()
    val selected by vm.selectedCategory.collectAsState()
    val online by vm.online.collectAsState()
    val alert by vm.alert.collectAsState()

    BackHandler(true) {
        scope.launch { vm.unselectSubcategory() }
        nav.navigationBack()
    }

    SubcategoriesContent(
        Modifier, SubcategoriesState(
            subcategories, selected, online, alert
        ), object : SubcategoriesCallback {

            override fun onCategoryClick(category: CategoryModel) {
                scope.launch {
                    vm.selectCategory(category)
                    nav.navigate("conditions")
                }
            }

            override fun onCloseAlert(state: Boolean) {
                scope.launch { vm.alertDismiss(state) }
            }

            override fun onBack() {
                scope.launch { vm.unselectSubcategory() }
                nav.navigationBack()
            }

            override fun onClose() {
                scope.launch {
                    vm.clearAddMeet()
                    nav.clearStackNavigation("main/meetings")
                }
            }
        }
    )
}