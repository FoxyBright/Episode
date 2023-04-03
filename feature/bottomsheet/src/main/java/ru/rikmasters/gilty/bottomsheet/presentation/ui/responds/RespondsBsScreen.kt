package ru.rikmasters.gilty.bottomsheet.presentation.ui.responds

import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import kotlinx.coroutines.launch
import ru.rikmasters.gilty.bottomsheet.presentation.ui.responds.RespondsBsType.FULL
import ru.rikmasters.gilty.bottomsheet.presentation.ui.responds.RespondsBsType.MEET
import ru.rikmasters.gilty.bottomsheet.presentation.ui.responds.RespondsBsType.SHORT
import ru.rikmasters.gilty.bottomsheet.viewmodel.RespondsBsViewModel
import ru.rikmasters.gilty.core.viewmodel.connector.Use
import ru.rikmasters.gilty.core.viewmodel.trait.LoadingTrait
import ru.rikmasters.gilty.shared.common.RespondsListCallback

@Composable
fun RespondsBs(
    full: Boolean,
    meetId: String? = null,
    vm: RespondsBsViewModel,
    nav: NavHostController,
) {
    
    val scope = rememberCoroutineScope()
    
    val respondsList by vm.responds.collectAsState()
    val groupsStates by vm.groupsStates.collectAsState()
    val tabs by vm.tabs.collectAsState()
    
    LaunchedEffect(Unit) { vm.getResponds(meetId) }
    
    Use<RespondsBsViewModel>(LoadingTrait) {
        RespondsList(
            type = meetId?.let { MEET }
                ?: if(full) FULL else SHORT,
            responds = respondsList,
            respondsStates = groupsStates,
            selectTab = tabs, Modifier,
            object: RespondsListCallback {
                
                override fun onRespondClick(authorId: String) {
                    nav.navigate("USER?user=$authorId&meet=$meetId")
                }
                
                override fun onImageClick(image: String) {
                }
                
                override fun onArrowClick(index: Int) {
                    scope.launch { vm.selectRespondsGroup(index) }
                }
                
                override fun onCancelClick(respondId: String) {
                    scope.launch {
                        if(tabs == 0)
                            vm.cancelRespond(respondId)
                        else
                            vm.deleteRespond(respondId)
                        vm.getResponds(meetId)
                    }
                }
                
                override fun onAcceptClick(respondId: String) {
                    scope.launch {
                        vm.acceptRespond(respondId)
                        vm.getResponds(meetId)
                    }
                }
                
                override fun onTabChange(tab: Int) {
                    scope.launch { vm.selectTab(tab) }
                }
                
                override fun onBack() {
                    nav.popBackStack()
                }
            }
        )
    }
}