package ru.rikmasters.gilty.addmeet.presentation.ui.requirements.bottoms

import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import kotlinx.coroutines.launch
import org.koin.androidx.compose.get
import ru.rikmasters.gilty.addmeet.presentation.ui.requirements.AgeBottom
import ru.rikmasters.gilty.addmeet.viewmodel.Online
import ru.rikmasters.gilty.addmeet.viewmodel.bottoms.AgeBsViewModel
import ru.rikmasters.gilty.core.app.AppStateModel

@Composable
fun AgeBs(vm: AgeBsViewModel) {
    
    val asm = get<AppStateModel>()
    val scope = rememberCoroutineScope()
    
    val from by vm.from.collectAsState()
    val to by vm.to.collectAsState()
    
    AgeBottom(
        from, to, Modifier, Online,
        { scope.launch { vm.changeFrom(it) } },
        { scope.launch { vm.changeTo(it) } }
    ) {
        scope.launch {
            vm.onSave()
            asm.bottomSheet.collapse()
        }
    }
}