package ru.rikmasters.gilty.bottomsheet.presentation.ui.observers

import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.androidx.compose.get
import ru.rikmasters.gilty.bottomsheet.viewmodel.ObserverBsViewModel
import ru.rikmasters.gilty.core.app.AppStateModel
import ru.rikmasters.gilty.core.app.ui.BottomSheetSwipeState.COLLAPSED
import ru.rikmasters.gilty.shared.model.meeting.UserModel

@Composable
fun ObserversBs(
    vm: ObserverBsViewModel,
    username: String,
    nav: NavHostController,
) {
    
    val scope = rememberCoroutineScope()
    val asm = get<AppStateModel>()
    
    val unsubList by vm.unsubscribeMembers.collectAsState()
    val observersTab by vm.observersSelectTab.collectAsState()
    val observed by vm.observables.collectAsState()
    val observers by vm.observers.collectAsState()
    val search by vm.search.collectAsState()
    
    LaunchedEffect(Unit) {
        vm.getObservables()
        vm.getObservers()
        asm.bottomSheet.current.collectLatest {
            if(it == COLLAPSED) vm.unsubscribeMembers()
        }
    }
    
    ObserversListContent(
        ObserversListState(
            username, observers, observed,
            unsubList, observersTab, search
        ), Modifier.padding(top = 28.dp),
        object: ObserversListCallback {
            override fun onSearchTextChange(text: String) {
                scope.launch { vm.searchChange(text) }
            }
            
            override fun onButtonClick(
                member: UserModel, type: SubscribeType,
            ) {
                scope.launch { vm.onSubScribe(member, type) }
            }
            
            override fun onClick(member: UserModel) {
                nav.navigate("USER?user=${member.id}")
            }
            
            override fun onTabChange(point: Int) {
                scope.launch { vm.changeObserversTab(point) }
            }
        }
    )
}