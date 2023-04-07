package ru.rikmasters.gilty.addmeet.presentation.ui.complete

import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import kotlinx.coroutines.launch
import org.koin.androidx.compose.get
import ru.rikmasters.gilty.addmeet.viewmodel.CompleteViewModel
import ru.rikmasters.gilty.core.navigation.NavState
import ru.rikmasters.gilty.shared.common.extentions.shareMeet

@Composable
fun CompleteScreen(vm: CompleteViewModel) {
    
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val nav = get<NavState>()
    
    val addMeet by vm.addMeet.collectAsState(null)
    val meet by vm.meet.collectAsState()
    val online by vm.online.collectAsState()
    
    LaunchedEffect(addMeet) { addMeet?.let { vm.addMeet(it) } }
    
    meet?.let {
        CompleteContent(it, online, Modifier,
            object: CompleteCallBack {
                override fun onShare() {
                    scope.launch { shareMeet(it.id, context) }
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
}