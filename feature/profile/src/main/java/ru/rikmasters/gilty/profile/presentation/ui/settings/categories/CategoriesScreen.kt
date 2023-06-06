package ru.rikmasters.gilty.profile.presentation.ui.settings.categories

import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.androidx.compose.get
import ru.rikmasters.gilty.core.navigation.NavState
import ru.rikmasters.gilty.profile.viewmodel.CategoryViewModel
import ru.rikmasters.gilty.shared.model.meeting.CategoryModel

@Composable
fun CategoriesScreen(vm: CategoryViewModel) {
    
    val scope = rememberCoroutineScope()
    val nav = get<NavState>()
    
    val categories by vm.categories.collectAsState()
    val selected by vm.selected.collectAsState()
    val alert by vm.alert.collectAsState()
    
    // TODO - Не вызывается рекомпозиция блока с пузырями. Костыль для задержки
    var sleep by remember { mutableStateOf(false) }
    
    LaunchedEffect(Unit) {
        vm.getCategories()
        vm.getInterest()
        sleep = true
    }
    
    if(sleep) CategoriesContent(
        Modifier, CategoriesState(
            categories, selected, alert
        ), object: CategoriesCallback {
            
            override fun onClose() {
                nav.navigationBack()
            }
            
            override fun onCloseAlert(it: Boolean) {
                scope.launch { vm.alertDismiss(it) }
            }
            
            override fun onCategoryClick(category: CategoryModel) {
                scope.launch { vm.selectCategory(category) }
            }
            
            override fun onNext() {
                scope.launch {
                    vm.setUserInterest(onSuccess = {
                        scope.launch(Dispatchers.Main) {
                            nav.navigationBack()
                        }
                    })
                }
            }
        })
}