package ru.rikmasters.gilty.login.presentation.ui.categories

import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import kotlinx.coroutines.launch
import org.koin.androidx.compose.get
import ru.rikmasters.gilty.auth.categories.Category
import ru.rikmasters.gilty.core.navigation.NavState
import ru.rikmasters.gilty.login.viewmodel.CategoryViewModel

@Composable
fun CategoriesScreen(vm: CategoryViewModel) {
    
    val nav = get<NavState>()
    val scope = rememberCoroutineScope()
    
    val selected by vm.selected.collectAsState()
    val categories by vm.categories.collectAsState()
    
    CategoriesContent(
        Modifier, CategoriesState(
            categories, selected
        ), object: CategoriesCallback {
            
            override fun onCategoryClick(category: Category) {
                scope.launch { vm.selectCategory(category) }
            }
            
            override fun onBack() {
                nav.navigationBack()
            }
            
            override fun onNext() {
                scope.launch {
                    vm.sendCategories()
                    nav.navigate("permissions")
                }
            }
        })
}