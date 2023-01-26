package ru.rikmasters.gilty.addmeet.presentation.ui.complete

import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import kotlinx.coroutines.launch
import org.koin.androidx.compose.get
import ru.rikmasters.gilty.addmeet.viewmodel.CompleteViewModel
import ru.rikmasters.gilty.addmeet.viewmodel.Online
import ru.rikmasters.gilty.core.navigation.NavState

@Composable
fun CompleteScreen(vm: CompleteViewModel) {
    
    val nav = get<NavState>()
    val scope = rememberCoroutineScope()
    
    val meet by vm.meet.collectAsState()
    
    LaunchedEffect(Unit) {
        vm.setMeet()
        vm.addMeet()
    }
    meet?.let {
        CompleteContent(it, Online, Modifier,
            object: CompleteCallBack {
                override fun onShare() {
                    scope.launch { vm.onShared() }
                }
                
                override fun onClose() {
                    nav.navigateAbsolute("main/meetings")
                }
            })
    }
}