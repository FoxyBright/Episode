package ru.rikmasters.gilty.addmeet.presentation.ui.conditions

import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import kotlinx.coroutines.launch
import org.koin.androidx.compose.get
import ru.rikmasters.gilty.addmeet.viewmodel.ConditionViewModel
import ru.rikmasters.gilty.core.navigation.NavState

@Composable
fun ConditionsScreen(vm: ConditionViewModel) {
    
    val scope = rememberCoroutineScope()
    val nav = get<NavState>()
    
    val addMeet by vm.addMeet.collectAsState(null)
    val alert by vm.alert.collectAsState()
    
    val condition = addMeet?.condition?.let {
        listOf(it.ordinal)
    } ?: emptyList()
    
    val meetType = addMeet?.type?.let {
        listOf(it.ordinal)
    } ?: emptyList()
    
    val forbidden = addMeet?.chatForbidden ?: false
    val hidden = addMeet?.photoAccess ?: false
    val online = addMeet?.isOnline ?: false
    
    val price by vm.price.collectAsState()
    
    val isActive = condition.isNotEmpty()
            && meetType.isNotEmpty()
            && if(condition.contains(3)) {
        price.length > 1 && price.toInt() <= 1_000_000
    } else true
    
    ConditionContent(
        ConditionState(
            online, hidden, meetType, condition,
            forbidden, price, alert, isActive
        ), Modifier, object: ConditionsCallback {
            
            override fun onForbiddenClick() {
                scope.launch { vm.changeForbiddenChat(!forbidden) }
            }
            
            override fun onConditionSelect(condition: Int) {
                scope.launch { vm.changeCondition(condition) }
            }
            
            override fun onOnlineClick() {
                scope.launch { vm.changeOnline(!online) }
            }
            
            override fun onClose() {
                scope.launch {
                    vm.clearBase()
                    nav.navigateAbsolute("main/meetings")
                }
            }
            
            override fun onMeetingTypeSelect(type: Int) {
                scope.launch { vm.changeMeetType(type) }
            }
            
            override fun onCloseAlert(state: Boolean) {
                scope.launch { vm.alertDismiss(state) }
            }
            
            override fun onPriceChange(price: String) {
                scope.launch { vm.changePrice(price) }
            }
            
            override fun onHiddenClick() {
                scope.launch { vm.changeHidden(!hidden) }
            }
            
            override fun onClear() {
                scope.launch { vm.clearPrice() }
            }
            
            override fun onNext() {
                nav.navigate("detailed")
            }
            
            override fun onBack() {
                nav.navigationBack()
            }
        }
    )
}