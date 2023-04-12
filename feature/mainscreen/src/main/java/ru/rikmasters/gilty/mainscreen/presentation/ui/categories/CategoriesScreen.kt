package ru.rikmasters.gilty.mainscreen.presentation.ui.categories

import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import kotlinx.coroutines.launch
import ru.rikmasters.gilty.mainscreen.viewmodels.bottoms.FiltersBsViewModel
import ru.rikmasters.gilty.shared.model.meeting.CategoryModel

@Composable
fun CategoriesScreen(vm: FiltersBsViewModel) {
    
    val scope = rememberCoroutineScope()
    
    val selected by vm.selectedAdditionally.collectAsState()
    val categories by vm.allCategories.collectAsState()
    val states by vm.additionallyStates.collectAsState()
    
    LaunchedEffect(Unit) {
        vm.fullAdditionallySelect()
    }
    
    CategoryList(
        CategoryListState(
            categories, selected, states
        ), Modifier, object: CategoryListCallback {
            
            override fun onBack() {
                scope.launch { vm.navigate(0) }
            }
            
            override fun onCategoryClick(
                index: Int, category: CategoryModel,
            ) {
                scope.launch {
                    category.children?.let {
                        vm.changeAdditionallyStates(index)
                    } ?: run { vm.selectAdditionally(category) }
                }
            }
            
            override fun onSubClick(category: CategoryModel) {
                scope.launch { vm.selectAdditionally(category) }
            }
            
            override fun onComplete() {
                scope.launch {
                    vm.onAddComplete()
                    vm.navigate(0)
                }
            }
            
            override fun onClear() {
                scope.launch { vm.clearAdditionally() }
            }
        })
}