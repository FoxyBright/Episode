package ru.rikmasters.gilty.addmeet.presentation.ui.detailed.bottom.time

import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import kotlinx.coroutines.launch
import org.koin.androidx.compose.get
import ru.rikmasters.gilty.addmeet.viewmodel.bottoms.TimeBsViewModel
import ru.rikmasters.gilty.core.app.AppStateModel
import ru.rikmasters.gilty.shared.common.extentions.LocalDateTime

@Composable
fun TimeBs(vm: TimeBsViewModel) {
    
    val scope = rememberCoroutineScope()
    val asm = get<AppStateModel>()
    
    val online by vm.online.collectAsState()
    val minute by vm.minute.collectAsState()
    val hour by vm.hour.collectAsState()
    val date by vm.date.collectAsState()
    
    val isActive = LocalDateTime.of(
        vm.normalizeDate(date, hour, minute)
    ).isAfter(LocalDateTime.now())
    
    DateTimeBS(
        DateTimeBSState(
            date, hour, minute, online, isActive
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