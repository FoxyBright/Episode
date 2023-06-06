package ru.rikmasters.gilty.bottomsheet.presentation.ui.observers

import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.paging.compose.collectAsLazyPagingItems
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.androidx.compose.get
import ru.rikmasters.gilty.bottomsheet.viewmodel.ObserverBsViewModel
import ru.rikmasters.gilty.core.app.AppStateModel
import ru.rikmasters.gilty.core.app.ui.BottomSheetSwipeState.COLLAPSED
import ru.rikmasters.gilty.shared.model.image.EmojiModel.Companion.getEmoji
import ru.rikmasters.gilty.shared.model.meeting.UserModel

@Composable
fun ObserversBs(
    vm: ObserverBsViewModel,
    username: String,
    emoji: String,
    nav: NavHostController,
) {
    val scope = rememberCoroutineScope()
    val asm = get<AppStateModel>()
    
    val observed = vm.observables.collectAsLazyPagingItems()
    val observers = vm.observers.collectAsLazyPagingItems()
    val unsubList by vm.unsubscribeMembers.collectAsState()
    val searchObservers by vm.searchObservers.collectAsState()
    val searchObserved by vm.searchObserved.collectAsState()
    val observersTab by vm.observersSelectTab.collectAsState()
    val counters by vm.counters.collectAsState()
    
    LaunchedEffect(Unit) {
        vm.getCounters()
        asm.bottomSheet.current.collectLatest {
            if(it == COLLAPSED)
                vm.unsubscribeMembers()
        }
    }
    
    ObserversListContent(
        state = ObserversListState(
            user = username,
            emoji = getEmoji(emoji),
            observers = observers,
            observed = observed,
            unsubList = unsubList,
            selectTab = observersTab,
            searchObserved = searchObserved,
            searchObservers = searchObservers,
            counters = counters,
            scope = scope,
        ),
        modifier = Modifier.padding(top = 28.dp),
        callback = object: ObserversListCallback {
            override fun onSearchObservedTextChange(text: String) {
                scope.launch { vm.searchObservedChange(text) }
            }
            
            override fun onSearchObserversTextChange(text: String) {
                scope.launch { vm.searchObserversChange(text) }
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
