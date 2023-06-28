package ru.rikmasters.gilty.login.presentation.ui.categories

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.google.accompanist.insets.statusBarsPadding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.androidx.compose.get
import ru.rikmasters.gilty.core.navigation.NavState
import ru.rikmasters.gilty.login.viewmodel.CategoryViewModel
import ru.rikmasters.gilty.shared.model.meeting.CategoryModel

@Composable
fun CategoriesScreen(vm: CategoryViewModel) {
    
    val scope = rememberCoroutineScope()
    val nav = get<NavState>()
    
    val categories by vm.categories.collectAsState()
    val selected by vm.selected.collectAsState()
    val phase by vm.phase.collectAsState()

    // TODO - Не вызывается рекомпозиция блока с пузырями. Костыль для задержки
    var sleep by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        // empties data
        vm.emptyPhase()
        vm.emptyCategories()
        vm.emptySelected()
        // fetches data
        vm.getCategories()
        vm.getInterest()
        sleep = true
    }

    LaunchedEffect(key1 = categories, block = {
        sleep = false
        delay(50L)
        sleep = true
    })

    LaunchedEffect(key1 = phase, block = {
        if (phase == 1) {
            sleep = false
            delay(50L)
            sleep = true
        }
    })

    BackHandler {
        if(phase == 0){
            nav.navigationBack()
        }else {
            scope.launch {
                vm.emptyPhase()
                vm.emptyCategories()

                vm.getCategories()
                sleep = true
            }
        }
    }

    CategoriesContent(
        modifier = Modifier.systemBarsPadding(),
        state = CategoriesState(
            categoryList = categories,
            selectCategories = selected,
            hasParentCategory = if (phase == 1) false else selected.firstOrNull { it.children?.isNotEmpty() == true } != null,
            sleep = sleep,
            ), object: CategoriesCallback {
            
            override fun onCategoryClick(category: CategoryModel) {
                scope.launch { vm.selectCategory(category) }
            }
            
            override fun onBack() {
                if (phase == 0) nav.navigationBack()
                else {
                    scope.launch {
                        vm.emptyPhase()
                        vm.emptyCategories()

                        vm.getCategories()
                        sleep = true
                    }
                }
            }

            override fun onNext() {
                scope.launch {
                    vm.setUserInterest(onSuccess = {
                        scope.launch(Dispatchers.Main) {
                            nav.navigate("permissions")
                        }
                    })
                }
            }
        }
    )
}