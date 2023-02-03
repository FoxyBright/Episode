package ru.rikmasters.gilty.addmeet.presentation.ui.detailed.bottom.time

import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import kotlinx.coroutines.launch
import org.koin.androidx.compose.get
import ru.rikmasters.gilty.addmeet.viewmodel.Online
import ru.rikmasters.gilty.addmeet.viewmodel.bottoms.TimeBsViewModel
import ru.rikmasters.gilty.core.app.AppStateModel
import ru.rikmasters.gilty.shared.common.extentions.LocalDateTime

@Composable
fun TimeBs(vm: TimeBsViewModel) {
    
    val asm = get<AppStateModel>()
    val scope = rememberCoroutineScope()
    
    val date by vm.date.collectAsState()
    val hour by vm.hour.collectAsState()
    val minute by vm.minute.collectAsState()
    
    fun isActive(): Boolean {
        return LocalDateTime.of(vm.normalizeDate(date, hour, minute)).isAfter(LocalDateTime.now())
    }
    
    DateTimeBS(
        DateTimeBSState(
            date, hour, minute, Online, isActive()
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