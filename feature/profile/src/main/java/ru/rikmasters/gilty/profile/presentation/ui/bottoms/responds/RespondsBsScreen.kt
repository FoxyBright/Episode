package ru.rikmasters.gilty.profile.presentation.ui.bottoms.responds

import android.widget.Toast
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import kotlinx.coroutines.launch
import ru.rikmasters.gilty.profile.viewmodel.bottoms.RespondsBsViewModel
import ru.rikmasters.gilty.shared.common.RespondCallback
import ru.rikmasters.gilty.shared.model.meeting.DemoMeetingModel
import ru.rikmasters.gilty.shared.model.meeting.MeetingModel
import ru.rikmasters.gilty.shared.model.notification.DemoReceivedRespondsModel
import ru.rikmasters.gilty.shared.model.notification.DemoSendRespondsModel
import ru.rikmasters.gilty.shared.model.notification.RespondModel
import ru.rikmasters.gilty.shared.model.profile.HiddenPhotoModel

@Composable
fun RespondsBs(vm: RespondsBsViewModel){
    
    val respondsSelectTab by vm.respondsSelectTab.collectAsState()
    val observeGroupStates by vm.observeGroupStates.collectAsState()
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    
    val respondsList =
        remember {
            mutableStateListOf(
                DemoReceivedRespondsModel,
                DemoReceivedRespondsModel,
                DemoSendRespondsModel,
                DemoSendRespondsModel,
                DemoSendRespondsModel
            )
        }
    
    val pairRespondsList =
        remember { mutableStateOf(Pair(DemoMeetingModel, respondsList)) }

    RespondsList(
        listOf(pairRespondsList.value),
        respondsSelectTab, observeGroupStates,
        Modifier, object: RespondCallback {
            override fun onCancelClick(respond: RespondModel) {
                respondsList.remove(respond)
                Toast.makeText(
                    context,
                    "Вы отказались от встречи",
                    Toast.LENGTH_SHORT
                ).show()
            }
            
            override fun onRespondClick(meet: MeetingModel) {
                Toast.makeText(
                    context,
                    "Вы нажали на встречу",
                    Toast.LENGTH_SHORT
                ).show()
            }
            
            override fun onAcceptClick(respond: RespondModel) {
                respondsList.remove(respond)
                Toast.makeText(
                    context,
                    "Вы приняли встречу",
                    Toast.LENGTH_SHORT
                ).show()
            }
            
            override fun onImageClick(image: HiddenPhotoModel) {
                Toast.makeText(
                    context,
                    "Фото смотреть нельзя",
                    Toast.LENGTH_SHORT
                ).show()
            }
            
            override fun onTabChange(tab: Int) {
                scope.launch { vm.changeObserveGroupStates(tab) }
            }
            
            override fun onArrowClick(index: Int) {
                scope.launch { vm.changeRespondsTab(index) }
            }
        })
}