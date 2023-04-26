package ru.rikmasters.gilty.mainscreen.presentation.ui.filter

import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import kotlinx.coroutines.launch
import org.koin.androidx.compose.get
import ru.rikmasters.gilty.core.app.AppStateModel
import ru.rikmasters.gilty.mainscreen.presentation.ui.categories.CategoriesScreen
import ru.rikmasters.gilty.mainscreen.viewmodels.bottoms.FiltersBsViewModel
import ru.rikmasters.gilty.shared.model.meeting.CategoryModel
import ru.rikmasters.gilty.shared.model.meeting.TagModel

@Composable
fun FiltersBs(
    vm: FiltersBsViewModel,
    alpha: Float,
    isCollapsed: Boolean,
) {
    
    val scope = rememberCoroutineScope()
    val asm = get<AppStateModel>()
    
    val selectedCategories by vm.selectedCategories.collectAsState()
    val interest by vm.mainVm.userCategories.collectAsState()
    val selectedCondition by vm.selectedCondition.collectAsState()
    val categoriesStates by vm.categoriesStates.collectAsState()
    val categories by vm.categories.collectAsState()
    val distanceState by vm.distanceState.collectAsState()
    val meetTypes by vm.meetTypes.collectAsState()
    val today by vm.mainVm.today.collectAsState()
    val isOnline by vm.isOnline.collectAsState()
    val tags by vm.tags.collectAsState()
    val distance by vm.distance.collectAsState()
    val results by vm.results.collectAsState()
    val city by vm.city.collectAsState()
    val screen by vm.screen.collectAsState()
    
    LaunchedEffect(isCollapsed) {
        if(isCollapsed) vm.navigate(0)
    }
    
    val hasFilters = vm
        .hasFilters
        .collectAsState()
        .value
        .isNotNullOrEmpty()
            || (city != null
            || selectedCategories.isNotEmpty()
            || tags.isNotEmpty()
            || distance != 15
            || meetTypes.isNotEmpty()
            || isOnline
            || selectedCondition.isNotEmpty())
    
    val topRow = vm.removeChildren(interest)
        .let { favour ->
            favour + selectedCategories
                .map { vm.getParentCategory(it) }
                .filter { !favour.contains(it) }
        }.distinct()
    
    when(screen) {
        1 -> CategoriesScreen(vm, alpha)
        2 -> TagSearchScreen(vm, alpha)
        3 -> CitiesScreen(vm, alpha)
        else -> MeetingFilterBottom(
            Modifier, results, FilterListState(
                today, distanceState, distance,
                isOnline, meetTypes, selectedCondition, tags,
                topRow, categories, selectedCategories,
                categoriesStates, city, hasFilters, alpha
            ), object: MeetingFilterBottomCallback {
                
                override fun onCategoryClick(
                    index: Int, category: CategoryModel,
                ) {
                    scope.launch {
                        category.children?.let {
                            vm.changeCategoryState(index)
                        } ?: run { vm.selectCategory(category) }
                    }
                }
                
                override fun onSubClick(parent: CategoryModel) {
                    scope.launch { vm.selectCategory(parent) }
                }
                
                override fun onAllCategoryClick() {
                    scope.launch { vm.navigate(1) }
                }
                
                override fun onFilterClick() {
                    scope.launch { vm.navigate(2) }
                }
                
                override fun onCityClick() {
                    scope.launch { vm.navigate(3) }
                }
                
                override fun onDeleteTag(tag: TagModel) {
                    scope.launch { vm.deleteTag(tag) }
                }
                
                override fun onClear() {
                    scope.launch { vm.clearFilters() }
                }
                
                override fun onDistanceClick() {
                    scope.launch { vm.changeDistanceState() }
                }
                
                override fun onDistanceValueChange(it: Int) {
                    scope.launch { vm.changeDistance(it) }
                }
                
                override fun onOnlyOnlineClick() {
                    scope.launch { vm.changeOnline() }
                }
                
                override fun onMeetingTypeSelect(index: Int) {
                    scope.launch { vm.selectMeetType(index) }
                }
                
                override fun onConditionSelect(index: Int) {
                    scope.launch { vm.selectCondition(index) }
                }
                
                override fun onFilter() {
                    scope.launch {
                        vm.onSave()
                        asm.bottomSheet.collapse()
                    }
                }
            }
        )
    }
}