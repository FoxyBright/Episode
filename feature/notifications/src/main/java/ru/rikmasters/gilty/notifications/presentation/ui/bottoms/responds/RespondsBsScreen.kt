package ru.rikmasters.gilty.notifications.presentation.ui.bottoms.responds

import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.androidx.compose.get
import ru.rikmasters.gilty.core.app.AppStateModel
import ru.rikmasters.gilty.core.app.ui.BottomSheetSwipeState.COLLAPSED
import ru.rikmasters.gilty.core.viewmodel.connector.Use
import ru.rikmasters.gilty.core.viewmodel.trait.LoadingTrait
import ru.rikmasters.gilty.notifications.viewmodel.bottoms.RespondsBsViewModel
import ru.rikmasters.gilty.shared.common.RespondsListCallback

@Composable
fun RespondsBs(vm: RespondsBsViewModel) {
    
    val scope = rememberCoroutineScope()
    val asm = get<AppStateModel>()
    
    val respondsList by vm.responds.collectAsState()
    val groupsStates by vm.groupsStates.collectAsState()
    
    LaunchedEffect(Unit) {
        vm.getResponds()
        asm.bottomSheet.current.collectLatest {
            if(it == COLLAPSED)
                vm.notificationVM.getLastResponse()
        }
    }
    
    Use<RespondsBsViewModel>(LoadingTrait) {
        RespondsBsContent(
            NotificationRespondsState(
                respondsList, groupsStates
            ), Modifier, object: RespondsListCallback {
                
                override fun onImageClick(authorId: String) {
                
                }
                
                override fun onCancelClick(respondId: String) {
                    scope.launch {
                        vm.cancelRespond(respondId)
                        if(respondsList.isEmpty())
                            asm.bottomSheet.collapse()
                    }
                }
                
                override fun onAcceptClick(respondId: String) {
                    scope.launch {
                        vm.acceptRespond(respondId)
                        if(respondsList.isEmpty())
                            asm.bottomSheet.collapse()
                    }
                }
                
                override fun onArrowClick(index: Int) {
                    scope.launch { vm.selectRespondsGroup(index) }
                }
            }
        )
    }
}