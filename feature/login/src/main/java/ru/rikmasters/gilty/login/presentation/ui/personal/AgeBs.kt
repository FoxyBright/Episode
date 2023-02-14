package ru.rikmasters.gilty.login.presentation.ui.personal

import androidx.compose.runtime.*
import kotlinx.coroutines.launch
import org.koin.androidx.compose.get
import ru.rikmasters.gilty.core.app.AppStateModel
import ru.rikmasters.gilty.login.viewmodel.AgeBsViewModel

@Composable
fun AgeBs(vm: AgeBsViewModel) {
    
    val asm = get<AppStateModel>()
    val scope = rememberCoroutineScope()
    
    val age by vm.age.collectAsState()
    val range = 17..99
    
    AgeBottomSheetContent(age, range, {
        scope.launch { vm.changeAge(it) }
    }) {
        scope.launch {
            vm.save()
            asm.bottomSheet.collapse()
        }
    }
}