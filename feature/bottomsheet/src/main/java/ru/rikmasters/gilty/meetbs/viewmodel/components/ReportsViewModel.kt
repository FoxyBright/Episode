package ru.rikmasters.gilty.meetbs.viewmodel.components

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.koin.core.component.inject
import ru.rikmasters.gilty.core.viewmodel.ViewModel
import ru.rikmasters.gilty.data.reports.ReportsManager
import ru.rikmasters.gilty.meetbs.viewmodel.BsViewModel
import ru.rikmasters.gilty.shared.model.report.*
import ru.rikmasters.gilty.shared.model.report.Report.Companion.all
import ru.rikmasters.gilty.shared.model.report.ReportObjectType.PHOTO
import ru.rikmasters.gilty.shared.model.report.ReportSubtype.PHOTO_ANOTHER_USER
import ru.rikmasters.gilty.shared.model.report.ReportSubtype.PHOTO_FAMOUS

class ReportsViewModel(
    private val bsVm: BsViewModel = BsViewModel(),
): ViewModel() {
    
    private val reportsManager by inject<ReportsManager>()
    
    private val _reports = MutableStateFlow(emptyList<Report>())
    val reports = _reports.asStateFlow()
    
    suspend fun getReports(type: ReportObjectType) {
        _reports.emit(all(type))
    }
    
    private val _screen = MutableStateFlow<Report?>(null)
    val screen = _screen.asStateFlow()
    
    private val _description = MutableStateFlow("")
    val description = _description.asStateFlow()
    
    private val _selected = MutableStateFlow<ReportSubtype?>(null)
    val selected = _selected.asStateFlow()
    
    suspend fun selectReport(report: ReportSubtype?) {
        _selected.emit(report)
    }
    
    suspend fun changeDescription(text: String) {
        _description.emit(text)
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
            objectType = when(selected.value) {
                PHOTO_ANOTHER_USER, PHOTO_FAMOUS -> PHOTO
                else -> objectType
            }
        )
        reportsManager.sendReport(report)
    }
    
    suspend fun dismissAlertState(state: Boolean) {
        bsVm.dismissAlertState(state)
    }
}