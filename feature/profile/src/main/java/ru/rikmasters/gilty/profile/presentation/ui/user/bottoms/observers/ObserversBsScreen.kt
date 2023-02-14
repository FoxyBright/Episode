package ru.rikmasters.gilty.profile.presentation.ui.user.bottoms.observers

import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.androidx.compose.get
import ru.rikmasters.gilty.core.app.AppStateModel
import ru.rikmasters.gilty.core.app.ui.BottomSheetSwipeState.COLLAPSED
import ru.rikmasters.gilty.profile.viewmodel.bottoms.ObserverBsViewModel
import ru.rikmasters.gilty.profile.viewmodel.bottoms.ObserverBsViewModel.SubscribeType
import ru.rikmasters.gilty.shared.model.meeting.UserModel

@Composable
fun ObserversBs(
    vm: ObserverBsViewModel,
    username: String,
) {
    
    val scope = rememberCoroutineScope()
    val asm = get<AppStateModel>()
    
    val observersTab by vm.observersSelectTab.collectAsState()
    val observed by vm.observables.collectAsState()
    val observers by vm.observers.collectAsState()
    val unsubList by vm.unsubscribeMembers.collectAsState()
    
    LaunchedEffect(Unit) {
        vm.getObservables()
        vm.getObservers()
        asm.bottomSheet.current.collectLatest {
            if(it == COLLAPSED) vm.unsubscribeMembers()
        }
    }
    
    ObserversListContent(
        ObserversListState(
            username, observers,
            observed, unsubList, observersTab,
        ), Modifier.padding(top = 28.dp),
        object: ObserversListCallback {
            
            override fun onButtonClick(
                member: UserModel, type: SubscribeType,
            ) {
                scope.launch { vm.onSubScribe(member, type) }
            }
            
            override fun onClick(member: UserModel) {
            }
            
            override fun onTabChange(point: Int) {
                scope.launch { vm.changeObserversTab(point) }
            }
        }
    )
}