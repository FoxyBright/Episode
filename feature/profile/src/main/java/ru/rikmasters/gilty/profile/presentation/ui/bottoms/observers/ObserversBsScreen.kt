package ru.rikmasters.gilty.profile.presentation.ui.bottoms.observers

import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import ru.rikmasters.gilty.profile.viewmodel.ObserverBsViewModel

@Composable
fun ObserversBs(
    vm: ObserverBsViewModel,
    username: String
) {
    
    val scope = rememberCoroutineScope()
    
    val observersTab by vm.observersSelectTab.collectAsState()
    
    val observed by vm.observables.collectAsState()
    val observers by vm.observers.collectAsState()
    
    LaunchedEffect(Unit) {
        vm.getObservables()
        vm.getObservers()
    }
    
    ObserversListContent(
        ObserversListState(
            username, observers,
            observed, observersTab
        ), Modifier.padding(top = 28.dp),
        object: ObserversListCallback {
            override fun onDelete() {
                super.onDelete()
            }
            
            override fun onClick() {
                super.onClick()
            }
            
            override fun onTabChange(point: Int) {
                scope.launch { vm.changeObserversTab(point) }
            }
        }
    )
}