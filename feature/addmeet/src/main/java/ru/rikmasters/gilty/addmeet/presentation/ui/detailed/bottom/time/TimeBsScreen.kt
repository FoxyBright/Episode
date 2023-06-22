package ru.rikmasters.gilty.addmeet.presentation.ui.detailed.bottom.time

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import kotlinx.coroutines.launch
import org.koin.androidx.compose.get
import ru.rikmasters.gilty.addmeet.viewmodel.bottoms.TimeBsViewModel
import ru.rikmasters.gilty.core.app.AppStateModel

@Composable
fun TimeBs(vm: TimeBsViewModel) {
    
    val scope = rememberCoroutineScope()
    val asm = get<AppStateModel>()
    
    val online by vm.online.collectAsState()
    val minute by vm.minute.collectAsState()
    val hour by vm.hour.collectAsState()
    val date by vm.date.collectAsState()

    BackHandler {
        scope.launch {
            asm.bottomSheet.collapse()
        }
    }

    DateTimeBS(
        DateTimeBSState(
            date, hour, minute, online, vm.isActive()
        ), Modifier, object: DateTimeBSCallback {
            
            override fun dateChange(it: String) {
                scope.launch { vm.changeDate(it) }
            }
            
            override fun hourChange(it: String) {
                scope.launch { vm.changeHour(it) }
            }
            
            override fun minuteChange(it: String) {
                scope.launch { vm.changeMinute(it) }
            }
            
            override fun onSave() {
                scope.launch {
                    vm.onSave()
                    asm.bottomSheet.collapse()
                }
            }
        }
    )
}