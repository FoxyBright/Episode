package ru.rikmasters.gilty.meetbs.presentation.ui.reports

import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.navigation.NavHostController
import kotlinx.coroutines.launch
import org.koin.androidx.compose.get
import ru.rikmasters.gilty.complaints.presentation.ui.ComplainsContent
import ru.rikmasters.gilty.complaints.presentation.ui.ReportsType
import ru.rikmasters.gilty.core.app.AppStateModel
import ru.rikmasters.gilty.meetbs.viewmodel.components.ReportsViewModel

@Composable
fun ReportsBs(
    vm: ReportsViewModel,
    type: String,
    id: String,
    nav: NavHostController
) {
    
    val reportType = ReportsType.valueOf(type)
    
    val scope = rememberCoroutineScope()
    val asm = get<AppStateModel>()
    
    ComplainsContent(id) {
        scope.launch {
            asm.bottomSheet.collapse()
            vm.dismissAlertState(true)
        }
    }
}