package ru.rikmasters.gilty.notifications.presentation.ui.bottoms.responds

import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import kotlinx.coroutines.launch
import org.koin.androidx.compose.get
import ru.rikmasters.gilty.core.app.AppStateModel
import ru.rikmasters.gilty.core.viewmodel.connector.Use
import ru.rikmasters.gilty.core.viewmodel.trait.LoadingTrait
import ru.rikmasters.gilty.notifications.viewmodel.bottoms.RespondsBsViewModel
import ru.rikmasters.gilty.shared.common.RespondsListCallback
import ru.rikmasters.gilty.shared.common.extentions.meetSeparate
import ru.rikmasters.gilty.shared.model.notification.ShortRespondModel

@Composable
fun RespondsBs(vm: RespondsBsViewModel) {
    
    val scope = rememberCoroutineScope()
    val asm = get<AppStateModel>()
    
    val respondsList by vm.responds.collectAsState()
    val groupsStates by vm.groupsStates.collectAsState()
    
    LaunchedEffect(Unit) { vm.getResponds() }
    
    Use<RespondsBsViewModel>(LoadingTrait) {
        RespondsBsContent(
            NotificationRespondsState(
                meetSeparate(respondsList), groupsStates
            ), Modifier, object: RespondsListCallback {
                
                override fun onCancelClick(respond: ShortRespondModel) {
                    scope.launch {
                        vm.cancelRespond(respond)
                        if(respondsList.isEmpty())
                            asm.bottomSheet.collapse()
                    }
                }
                
                override fun onAcceptClick(respond: ShortRespondModel) {
                    scope.launch {
                        vm.acceptRespond(respond)
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