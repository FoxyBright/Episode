package ru.rikmasters.gilty.bottomsheet.presentation.ui.reports

import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import kotlinx.coroutines.launch
import org.koin.androidx.compose.get
import ru.rikmasters.gilty.bottomsheet.viewmodel.components.ReportsViewModel
import ru.rikmasters.gilty.core.app.AppStateModel
import ru.rikmasters.gilty.core.viewmodel.connector.Use
import ru.rikmasters.gilty.core.viewmodel.trait.LoadingTrait
import ru.rikmasters.gilty.shared.model.report.Report
import ru.rikmasters.gilty.shared.model.report.ReportObjectType
import ru.rikmasters.gilty.shared.model.report.ReportSubtype

@Composable
fun ReportsBs(
    vm: ReportsViewModel,
    type: String,
    id: String,
    nav: NavHostController,
) {
    
    val scope = rememberCoroutineScope()
    val asm = get<AppStateModel>()
    
    val selected by vm.selected.collectAsState()
    val description by vm.description.collectAsState()
    val reports by vm.reports.collectAsState()
    val screen by vm.screen.collectAsState()
    val alert by vm.alert.collectAsState()
    
    val enabled = selected != null || description.isNotBlank()
    val objectType = ReportObjectType.valueOf(type)
    
    LaunchedEffect(Unit) { vm.getReports(objectType) }
    
    Use<ReportsViewModel>(LoadingTrait) {
        ReportsBsContent(
            ReportsBsState(
                reports, screen, selected,
                description, objectType, enabled
            ), Modifier, object: ReportsBsCallback {
                
                override fun onSendReport() {
                    scope.launch {
                        vm.sendReport(id, objectType)
                        vm.alertDismiss(true)
                    }
                }
                
                override fun onDescriptionChange(text: String) {
                    scope.launch { vm.changeDescription(text) }
                }
                
                override fun onSelectReport(report: ReportSubtype?) {
                    scope.launch { vm.selectReport(report) }
                }
                
                override fun onClearDescription() {
                    scope.launch { vm.clearDescription() }
                }
                
                override fun onNavigate(screen: Report?) {
                    scope.launch { vm.navigate(screen) }
                }
                
                override fun onBack() {
                    nav.popBackStack()
                }
            }
        )
        ReportAlert(alert) {
            scope.launch {
                vm.alertDismiss(false)
                vm.navigate(null)
                asm.bottomSheet.collapse()
            }
        }
    }
}