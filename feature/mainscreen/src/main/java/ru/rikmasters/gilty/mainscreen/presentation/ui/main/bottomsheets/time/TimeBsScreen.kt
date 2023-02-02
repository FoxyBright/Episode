package ru.rikmasters.gilty.mainscreen.presentation.ui.main.bottomsheets.time

import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import kotlinx.coroutines.launch
import org.koin.androidx.compose.get
import ru.rikmasters.gilty.core.app.AppStateModel
import ru.rikmasters.gilty.mainscreen.viewmodels.bottoms.TimeBsViewModel

@Composable
fun TimeBs(vm: TimeBsViewModel) {
    
    val scope = rememberCoroutineScope()
    val asm = get<AppStateModel>()
    
    val hours by vm.hours.collectAsState()
    val minutes by vm.minutes.collectAsState()
    val time by vm.time.collectAsState()
    
    TimeBsContent(
        TimeBsState(
            minutes, hours, time
        ), Modifier, object: TimeBSCallback {
            override fun onSave() {
                scope.launch {
                    vm.onSave()
                    asm.bottomSheet.collapse()
                }
            }
            
            override fun onHourChange(hour: String) {
                scope.launch { vm.hoursChange(hour) }
            }
            
            override fun onMinuteChange(minute: String) {
                scope.launch { vm.minutesChange(minute) }
            }
            
            override fun onClear() {
                scope.launch { vm.onClear() }
            }
        }
    )
}