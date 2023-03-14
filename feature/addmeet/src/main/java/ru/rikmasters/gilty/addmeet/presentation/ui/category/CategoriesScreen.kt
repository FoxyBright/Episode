package ru.rikmasters.gilty.addmeet.presentation.ui.category

import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import kotlinx.coroutines.launch
import org.koin.androidx.compose.get
import ru.rikmasters.gilty.addmeet.viewmodel.CategoryViewModel
import ru.rikmasters.gilty.core.navigation.NavState
import ru.rikmasters.gilty.shared.model.meeting.CategoryModel

@Composable
fun CategoriesScreen(vm: CategoryViewModel) {
    
    val scope = rememberCoroutineScope()
    val nav = get<NavState>()
    
    val categories by vm.categories.collectAsState()
    val alert by vm.alert.collectAsState()
    
    val selected = vm.addMeet
        .collectAsState().value?.category
    
    LaunchedEffect(Unit) { vm.getCategories() }
    
    CategoriesContent(
        Modifier, CategoriesState(
            categories, selected, alert
        ), object: CategoriesCallback {
            
            override fun onCategoryClick(category: CategoryModel) {
                scope.launch {
                    vm.selectCategory(category)
                    nav.navigate("conditions")
                }
            }
            
            override fun onCloseAlert(state: Boolean) {
                scope.launch { vm.alertDismiss(state) }
            }
            
            override fun onClose() {
                scope.launch {
                    vm.clearBase()
                    nav.navigateAbsolute("main/meetings")
                }
            }
        }
    )
}