package ru.rikmasters.gilty.profile.presentation.ui.user.bottoms.meeting

import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.launch
import org.koin.androidx.compose.get
import ru.rikmasters.gilty.complaints.presentation.ui.ComplainsContent
import ru.rikmasters.gilty.core.app.AppStateModel
import ru.rikmasters.gilty.profile.viewmodel.bottoms.MeetingBsViewModel

@Composable
fun ComplaintsBs(
    vm: MeetingBsViewModel,
    meetId: String,
) {
    
    val scope = rememberCoroutineScope()
    val asm = get<AppStateModel>()
    
    ComplainsContent(meetId) {
        scope.launch {
            asm.bottomSheet.collapse()
            vm.clearStack()
            vm.alertDismiss(true)
        }
    }
}