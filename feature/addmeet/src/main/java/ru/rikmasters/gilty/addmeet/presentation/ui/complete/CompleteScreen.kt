package ru.rikmasters.gilty.addmeet.presentation.ui.complete

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import kotlinx.coroutines.launch
import org.koin.androidx.compose.get
import ru.rikmasters.gilty.addmeet.viewmodel.CompleteViewModel
import ru.rikmasters.gilty.core.navigation.NavState
import ru.rikmasters.gilty.core.viewmodel.connector.Use
import ru.rikmasters.gilty.core.viewmodel.trait.LoadingTrait
import ru.rikmasters.gilty.shared.common.extentions.shareMeet

@Composable
fun CompleteScreen(vm: CompleteViewModel) {

    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val nav = get<NavState>()

    val addMeet by vm.addMeet.collectAsState(null)
    val meet by vm.meet.collectAsState()
    val online by vm.online.collectAsState()

    BackHandler {
        scope.launch {
            vm.clearAddMeet()
            nav.clearStackNavigation("main/meetings")
        }
    }

    meet?.let {
        Use<CompleteViewModel>(LoadingTrait) {
            CompleteContent(it, online, Modifier,
                object : CompleteCallBack {
                    override fun onShare() {
                        scope.launch { shareMeet(it.id, context) }
                    }

                    override fun onImageRefresh() {
                        scope.launch { vm.updateUserData() }
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
}