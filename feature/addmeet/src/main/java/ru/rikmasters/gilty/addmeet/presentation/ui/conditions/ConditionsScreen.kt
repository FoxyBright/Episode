package ru.rikmasters.gilty.addmeet.presentation.ui.conditions

import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import kotlinx.coroutines.launch
import org.koin.androidx.compose.get
import ru.rikmasters.gilty.addmeet.viewmodel.ConditionViewModel
import ru.rikmasters.gilty.core.navigation.NavState

@Composable
fun ConditionsScreen(vm: ConditionViewModel) {
    
    val nav = get<NavState>()
    val scope = rememberCoroutineScope()
    
    val price by vm.price.collectAsState()
    val alert by vm.alert.collectAsState()
    val hidden by vm.hidden.collectAsState()
    val online by vm.online.collectAsState()
    val meetType by vm.meetType.collectAsState()
    val condition by vm.condition.collectAsState()
    val restrictChat by vm.restrictChat.collectAsState()
    
    val isActive = condition.isNotEmpty()
            && meetType.isNotEmpty()
            && if(condition.contains(3)) {
        price.isNotBlank()
        price.length <= 7
    } else true
    
    ConditionContent(ConditionState(
        online, hidden, meetType, condition,
        price, alert, restrictChat, isActive
    ), Modifier, object: ConditionsCallback {
        override fun onOnlineClick() {
            scope.launch { vm.changeOnline() }
        }
        
        override fun onRestrictClick() {
            scope.launch { vm.changeRestrictChat() }
        }
        
        override fun onCloseAlert(state: Boolean) {
            scope.launch { vm.alertDismiss(state) }
        }
        
        override fun onPriceChange(price: String) {
            scope.launch { vm.changePrice(price) }
        }
        
        override fun onClose() {
            nav.navigateAbsolute("main/meetings")
        }
        
        override fun onClear() {
            scope.launch { vm.clearPrice() }
        }
        
        override fun onHiddenClick() {
            scope.launch { vm.changeHidden() }
        }
        
        override fun onBack() {
            nav.navigationBack()
        }
        
        override fun onNext() {
            nav.navigate("detailed")
        }
        
        override fun onConditionSelect(condition: Int) {
            scope.launch { vm.changeCondition(condition) }
        }
        
        override fun onMeetingTypeSelect(type: Int) {
            scope.launch { vm.changeMeetType(type) }
        }
    })
}