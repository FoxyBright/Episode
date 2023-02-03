package ru.rikmasters.gilty.addmeet.presentation.ui.complete

import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import kotlinx.coroutines.launch
import org.koin.androidx.compose.get
import ru.rikmasters.gilty.addmeet.viewmodel.CompleteViewModel
import ru.rikmasters.gilty.addmeet.viewmodel.Online
import ru.rikmasters.gilty.core.navigation.NavState
import ru.rikmasters.gilty.shared.common.extentions.shareMeet

@Composable
fun CompleteScreen(vm: CompleteViewModel) {
    
    val scope = rememberCoroutineScope()
    val meet by vm.meet.collectAsState()
    val context = LocalContext.current
    val nav = get<NavState>()
    
    LaunchedEffect(Unit) {
        vm.setMeet()
        vm.addMeet()
    }
    
    meet?.let {
        CompleteContent(it, Online, Modifier,
            object: CompleteCallBack {
                override fun onShare() {
                    scope.launch { shareMeet(it.id, context) }
                }
                
                override fun onClose() {
                    nav.navigateAbsolute("main/meetings")
                }
            })
    }
}