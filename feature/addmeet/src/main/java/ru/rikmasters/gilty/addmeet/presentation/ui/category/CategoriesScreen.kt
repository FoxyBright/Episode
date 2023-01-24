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
    
    val nav = get<NavState>()
    val scope = rememberCoroutineScope()
    
    val alert by vm.alert.collectAsState()
    
    val categories by vm.categories.collectAsState()
    val selected by vm.selected.collectAsState()
    
    //TODO - Не вызывается рекомпозиция блока с пузырями. Костыль с задержкой
    var sleep by remember { mutableStateOf(false) }
    
    LaunchedEffect(Unit) {
        vm.getCategories()
        sleep = true
    }
    
    if(sleep) CategoriesContent(
        Modifier, CategoriesState(
            categories, selected, alert
        ), object: CategoriesCallback {
            
            override fun onClose() {
                nav.navigateAbsolute("main/meetings")
            }
            
            override fun onCloseAlert(state: Boolean) {
                scope.launch { vm.alertDismiss(state) }
            }
            
            override fun onCategoryClick(category: CategoryModel) {
                scope.launch {
                    vm.selectCategory(category)
                    nav.navigate("conditions")
                }
            }
        })
}