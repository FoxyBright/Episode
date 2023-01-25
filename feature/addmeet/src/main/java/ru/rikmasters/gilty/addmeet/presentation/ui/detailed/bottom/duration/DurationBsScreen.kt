package ru.rikmasters.gilty.addmeet.presentation.ui.detailed.bottom.duration

import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import kotlinx.coroutines.launch
import org.koin.androidx.compose.get
import ru.rikmasters.gilty.addmeet.viewmodel.Online
import ru.rikmasters.gilty.addmeet.viewmodel.bottoms.DurationBsViewModel
import ru.rikmasters.gilty.core.app.AppStateModel

@Composable
fun DurationBs(vm: DurationBsViewModel) {
    
    val asm = get<AppStateModel>()
    val scope = rememberCoroutineScope()
    
    val duration by vm.duration.collectAsState()
    
    DurationBottomSheet(
        duration, Modifier, Online,
        { scope.launch { vm.changeDuration(it) } },
    ) {
        scope.launch {
            vm.onSave()
            asm.bottomSheet.collapse()
        }
    }
}