package ru.rikmasters.gilty.bottomsheet.viewmodel

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.koin.core.component.inject
import ru.rikmasters.gilty.core.viewmodel.ViewModel
import ru.rikmasters.gilty.data.reports.ReportsManager
import ru.rikmasters.gilty.shared.model.report.*
import ru.rikmasters.gilty.shared.model.report.Report.Companion.all

class ReportsBsViewModel: ViewModel() {
    
    private val reportsManager by inject<ReportsManager>()
    
    private val _reports = MutableStateFlow(emptyList<Report>())
    val reports = _reports.asStateFlow()
    
    private val _screen = MutableStateFlow<Report?>(null)
    val screen = _screen.asStateFlow()
    
    private val _alert = MutableStateFlow(false)
    val alert = _alert.asStateFlow()
    
    private val _description = MutableStateFlow("")
    val description = _description.asStateFlow()
    
    private val _selected = MutableStateFlow<ReportSubtype?>(null)
    val selected = _selected.asStateFlow()
    
    suspend fun selectReport(report: ReportSubtype?) {
        _selected.emit(report)
    }
    
    suspend fun alertDismiss(state: Boolean) {
        _alert.emit(state)
    }
    
    
    suspend fun changeDescription(text: String) {
        _description.emit(text)
    }
    
    suspend fun getReports(type: ReportObjectType) {
        _reports.emit(all(type))
    }
    
    suspend fun clearDescription() {
        changeDescription("")
    }
    
    suspend fun navigate(screen: Report?) {
        if(screen == null) {
            selectReport(null)
            clearDescription()
        }
        _screen.emit(screen)
    }
    
    suspend fun sendReport(
        objectId: String,
        objectType: ReportObjectType,
    ) = singleLoading {
        val report = ReportModel(
            type = screen.value,
            subtype = selected.value,
            description = description.value,
            objectId = objectId,
            objectType = objectType
        )
        reportsManager.sendReport(report)
    }
}