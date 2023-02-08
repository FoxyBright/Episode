package ru.rikmasters.gilty.profile.presentation.ui.settings.bottoms.age

import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import kotlinx.coroutines.launch
import org.koin.androidx.compose.get
import ru.rikmasters.gilty.core.app.AppStateModel
import ru.rikmasters.gilty.profile.viewmodel.settings.bottoms.AgeBsViewModel

@Composable
fun AgeBs(vm: AgeBsViewModel) {
    
    val scope = rememberCoroutineScope()
    val asm = get<AppStateModel>()
    val age by vm.age.collectAsState()
    
    AgeBsContent(
        AgeBsState(vm.ageRange, age),
        Modifier, object: AgeBsCallback {
            
            override fun onSave() {
                scope.launch {
                    asm.bottomSheet.collapse()
                    vm.onSave()
                }
            }
        
            override fun onAgeChange(age: Int) {
                scope.launch { vm.changeAge(age) }
            }
        }
    )
}