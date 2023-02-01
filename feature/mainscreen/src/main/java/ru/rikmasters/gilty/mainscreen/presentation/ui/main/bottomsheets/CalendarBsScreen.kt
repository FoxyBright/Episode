package ru.rikmasters.gilty.mainscreen.presentation.ui.main.bottomsheets

import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import kotlinx.coroutines.launch
import org.koin.androidx.compose.get
import ru.rikmasters.gilty.core.app.AppStateModel
import ru.rikmasters.gilty.mainscreen.viewmodels.bottoms.CalendarBsViewModel

@Composable
fun CalendarBs(vm: CalendarBsViewModel) {
    
    val scope = rememberCoroutineScope()
    val days by vm.days.collectAsState()
    val asm = get<AppStateModel>()
    
    CalendarBsContent(
        CalendarBSState(days),
        Modifier, object: CalendarBSCallback {
            override fun onItemSelect(date: String) {
                scope.launch { vm.selectDay(date) }
            }
            
            override fun onSave() {
                scope.launch {
                    vm.onSave()
                    asm.bottomSheet.collapse()
                }
            }
            
            override fun onClear() {
                scope.launch { vm.onClear() }
            }
        }
    )
}