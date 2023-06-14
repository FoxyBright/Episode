package ru.rikmasters.gilty.profile.presentation.ui.settings.categories

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
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
        modifier = Modifier,
        state = CategoriesState(
            categoryList = categories,
            selectCategories = selected,
            alert = alert,
            sleep = sleep,
            hasParentCategory = if (phase == 1) false else selected.firstOrNull { it.children?.isNotEmpty() == true } != null,
        ), callback = object : CategoriesCallback {

            override fun onClose() {
                nav.navigationBack()
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