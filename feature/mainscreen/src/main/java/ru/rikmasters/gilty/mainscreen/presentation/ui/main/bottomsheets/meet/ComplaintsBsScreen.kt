package ru.rikmasters.gilty.mainscreen.presentation.ui.main.bottomsheets.meet

import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.launch
import org.koin.androidx.compose.get
import ru.rikmasters.gilty.complaints.presentation.ui.ComplainsContent
import ru.rikmasters.gilty.core.app.AppStateModel
import ru.rikmasters.gilty.mainscreen.viewmodels.bottoms.MeetBsViewModel

@Composable
fun ComplaintsBs(
    vm: MeetBsViewModel,
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