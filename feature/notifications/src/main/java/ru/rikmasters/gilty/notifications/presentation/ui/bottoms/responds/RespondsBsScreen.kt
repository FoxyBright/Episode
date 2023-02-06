package ru.rikmasters.gilty.notifications.presentation.ui.bottoms.responds

import android.widget.Toast
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import kotlinx.coroutines.launch
import org.koin.androidx.compose.get
import ru.rikmasters.gilty.core.app.AppStateModel
import ru.rikmasters.gilty.notifications.viewmodel.bottoms.RespondsBsViewModel
import ru.rikmasters.gilty.shared.common.RespondCallback
import ru.rikmasters.gilty.shared.common.extentions.MeetSeparate
import ru.rikmasters.gilty.shared.model.notification.DemoReceivedRespondModelWithoutPhoto
import ru.rikmasters.gilty.shared.model.notification.DemoReceivedRespondsModel
import ru.rikmasters.gilty.shared.model.notification.RespondModel

@Composable
fun RespondsBs(vm: RespondsBsViewModel) {
    
    
    val scope = rememberCoroutineScope()
    val asm = get<AppStateModel>()
    val context = LocalContext.current
    
    val respondsList =
        remember {
            mutableStateListOf(
                DemoReceivedRespondModelWithoutPhoto,
                DemoReceivedRespondsModel
            )
        }
    
    val respondListStates =
        remember { mutableStateListOf<Boolean>() }
    repeat(respondsList.size) {
        if(it == 0) respondListStates.add(true) else
            respondListStates.add(false)
    }
    
    val notificationCallback = object: RespondCallback {
        override fun onCancelClick(respond: RespondModel) {
            respondsList.remove(respond)
            Toast.makeText(
                context, "Втреча отклонена",
                Toast.LENGTH_SHORT
            ).show()
            if(respondsList.size == 0)
                scope.launch { asm.bottomSheet.collapse() }
        }
        
        override fun onArrowClick(index: Int) {
            respondListStates[index] = !respondListStates[index]
        }
        
        override fun onAcceptClick(respond: RespondModel) {
            respondsList.remove(respond)
            Toast.makeText(
                context, "Втреча принята",
                Toast.LENGTH_SHORT
            ).show()
            if(respondsList.size == 0)
                scope.launch { asm.bottomSheet.collapse() }
        }
    }
    
    RespondsBsContent(
        NotificationRespondsState(
            MeetSeparate(respondsList),
            respondListStates
        ), Modifier, notificationCallback
    )
}