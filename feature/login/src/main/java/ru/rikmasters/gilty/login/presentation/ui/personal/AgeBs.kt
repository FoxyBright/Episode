package ru.rikmasters.gilty.login.presentation.ui.personal

import androidx.compose.runtime.*
import kotlinx.coroutines.launch
import org.koin.androidx.compose.get
import ru.rikmasters.gilty.core.app.AppStateModel
import ru.rikmasters.gilty.login.viewmodel.bottoms.AgeBsViewModel

@Composable
fun AgeBs(vm: AgeBsViewModel) {
    
    val scope = rememberCoroutineScope()
    val asm = get<AppStateModel>()
    val age by vm.age.collectAsState()
    
    AgeBottomSheetContent(
        value = age,
        range = 17..99,
        onValueChange = {
            scope.launch { vm.changeAge(it) }
        }
    ) {
        scope.launch {
            vm.save()
            asm.bottomSheet.collapse()
        }
    }
}