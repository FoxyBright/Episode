package ru.rikmasters.gilty.addmeet.presentation.ui.detailed.bottom.duration

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import kotlinx.coroutines.launch
import org.koin.androidx.compose.get
import ru.rikmasters.gilty.addmeet.viewmodel.bottoms.DurationBsViewModel
import ru.rikmasters.gilty.core.app.AppStateModel

@Composable
fun DurationBs(vm: DurationBsViewModel) {

    val scope = rememberCoroutineScope()
    val asm = get<AppStateModel>()

    val duration by vm.duration.collectAsState()
    val online by vm.online.collectAsState()

    BackHandler {
        scope.launch {
            asm.bottomSheet.collapse()
        }
    }

    DurationBottomSheet(
        duration, Modifier, online,
        { scope.launch { vm.changeDuration(it) } },
    ) {
        scope.launch {
            vm.onSave()
            asm.bottomSheet.collapse()
        }
    }
}