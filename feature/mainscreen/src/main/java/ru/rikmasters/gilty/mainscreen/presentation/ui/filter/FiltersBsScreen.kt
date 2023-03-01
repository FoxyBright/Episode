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
fun FiltersBs(vm: FiltersBsViewModel) {
    
    val scope = rememberCoroutineScope()
    val asm = get<AppStateModel>()
    
    val screen by vm.screen.collectAsState()
    val today by vm.mainVm.today.collectAsState()
    val country by vm.country.collectAsState()
    val city by vm.city.collectAsState()
    
    val interest by vm.myInterest.collectAsState()
    val categories by vm.categories.collectAsState()
    val categoriesStates by vm.categoriesStates.collectAsState()
    val selectedCategories by vm.selectedCategories.collectAsState()
    val topRow = (interest + vm.removeChildren(selectedCategories)
        .filter { !interest.contains(it) })
    
    val tags by vm.tags.collectAsState()
    val distanceState by vm.distanceState.collectAsState()
    val distance by vm.distance.collectAsState()
    val online by vm.online.collectAsState()
    val meetTypes by vm.meetTypes.collectAsState()
    val selectedCondition by vm.selectedCondition.collectAsState()
    val results by vm.results.collectAsState()
    
    LaunchedEffect(Unit) {
        vm.getAllCategories()
        vm.getUserCategories()
        vm.findMeets()
    }
    
    when(screen) {
        1 -> CategoriesScreen(vm)
        2 -> TagSearchScreen(vm)
        //        3 -> страна
        //        4 -> город
        else -> MeetingFilterBottom(
            Modifier, results, FilterListState(
                today, distanceState, distance,
                online, meetTypes, selectedCondition, tags,
                topRow, categories, selectedCategories,
                categoriesStates, country, city
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
                
                override fun onCountryClick() {
                    scope.launch {
                        vm.changeCountry("Россия")
                        vm.navigate(3)
                    }
                }
                
                override fun onCityClick() {
                    scope.launch {
                        vm.changeCity("Москва")
                        vm.navigate(4)
                    }
                }
                
                override fun onBack() {}
                
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
                
                override fun onNext() {
                    scope.launch {
                        vm.onSave()
                        asm.bottomSheet.collapse()
                    }
                }
            })
    }
}