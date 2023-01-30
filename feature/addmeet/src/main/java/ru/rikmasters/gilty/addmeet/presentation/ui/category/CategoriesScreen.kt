package ru.rikmasters.gilty.addmeet.presentation.ui.category

import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import kotlinx.coroutines.launch
import org.koin.androidx.compose.get
import ru.rikmasters.gilty.addmeet.viewmodel.*
import ru.rikmasters.gilty.core.navigation.NavState
import ru.rikmasters.gilty.shared.model.meeting.CategoryModel
import ru.rikmasters.gilty.shared.model.meeting.RequirementModel

@Composable
fun CategoriesScreen(vm: CategoryViewModel) {
    
    val nav = get<NavState>()
    val scope = rememberCoroutineScope()
    
    val alert by vm.alert.collectAsState()
    
    val categories by vm.categories.collectAsState()
    val selected by vm.selected.collectAsState()
    
    // TODO - Не вызывается рекомпозиция блока с пузырями. Костыль для задержки
    var sleep by remember { mutableStateOf(false) }
    
    fun clearStates() {
        SelectCategory = null
        Online = false
        Condition = null
        Price = ""
        Hidden = false
        RestrictChat = false
        MeetingType = null
        Tags = emptyList()
        Description = ""
        HideAddress = false
        Address = ""
        Place = ""
        Date = ""
        Duration = ""
        AgeFrom = ""
        AgeTo = ""
        Gender = null
        Orientation = null
        MemberCount = ""
        Private = false
        WithoutRespond = false
        MemberLimited = false
        Requirements = arrayListOf(
            RequirementModel(
                gender = null,
                ageMin = 0,
                ageMax = 0,
                orientation = null
            )
        )
        RequirementsType = 0
    }
    
    LaunchedEffect(Unit) {
        vm.selectCategory(null)
        clearStates()
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
                    SelectCategory?.let {
                        nav.navigate("conditions")
                    }
                }
            }
        })
}