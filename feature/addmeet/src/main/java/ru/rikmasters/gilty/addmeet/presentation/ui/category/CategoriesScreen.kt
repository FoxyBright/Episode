package ru.rikmasters.gilty.addmeet.presentation.ui.category

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.google.accompanist.insets.statusBarsPadding
import kotlinx.coroutines.launch
import org.koin.androidx.compose.get
import ru.rikmasters.gilty.addmeet.viewmodel.CategoryViewModel
import ru.rikmasters.gilty.core.app.AppStateModel
import ru.rikmasters.gilty.core.navigation.NavState
import ru.rikmasters.gilty.shared.model.meeting.CategoryModel

@Composable
fun CategoriesScreen(vm: CategoryViewModel) {
    
    val scope = rememberCoroutineScope()
    val asm = get<AppStateModel>()
    val nav = get<NavState>()
    
    val categories by vm.categories.collectAsState()
    val selected by vm.selectedCategory.collectAsState()
    val online by vm.online.collectAsState()
    val alert by vm.alert.collectAsState()
    
    val back = colorScheme.background
    LaunchedEffect(Unit) {
        asm.systemUi.setNavigationBarColor(back)
        vm.init()
        vm.getCategories()
    }

    BackHandler(!alert) {
        scope.launch { vm.alertDismiss(true) }
    }

    CategoriesContent(
        Modifier.systemBarsPadding(),
        CategoriesState(
            categories, selected, online, alert
        ), object: CategoriesCallback {
            
            override fun onCategoryClick(category: CategoryModel) {
                scope.launch {
                    vm.selectCategory(category)
                    if(category.children.isNullOrEmpty()){
                        nav.navigate("conditions")
                    }else {
                        nav.navigate("subcategory")
                    }

                }
            }
            
            override fun onCloseAlert(state: Boolean) {
                scope.launch { vm.alertDismiss(state) }
            }

            override fun onClose() {
                scope.launch {
                    vm.clearAddMeet()
                    nav.clearStackNavigation("main/meetings")
                }
            }
        }
    )
}